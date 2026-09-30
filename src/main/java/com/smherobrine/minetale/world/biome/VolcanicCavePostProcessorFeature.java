package com.smherobrine.minetale.world.biome;

import com.mojang.serialization.MapCodec;
import com.smherobrine.minetale.block.MinetaleBlocks;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Runs after vanilla cave decoration. It keeps vanilla placement algorithms intact, then converts
 * their output to the volcanic block palette and adds volcanic cave details.
 */
public record VolcanicCavePostProcessorFeature() implements Feature {
	public static final MapCodec<VolcanicCavePostProcessorFeature> CODEC = MapCodec.unit(VolcanicCavePostProcessorFeature::new);
	private static final Direction[] DIRECTIONS = Direction.values();

	@Override
	public MapCodec<VolcanicCavePostProcessorFeature> codec() {
		return CODEC;
	}

	@Override
	public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
		int minX = origin.getX();
		int minZ = origin.getZ();
		int minY = level.getMinY();
		// Bottom biomes cannot occur above this band; avoiding sky-height biome lookups keeps the
		// globally registered cleanup pass inexpensive in ordinary Overworld chunks.
		int maxY = Math.min(level.getMaxY(), 64);
		int quartLayers = (maxY - minY + 4) / 4;
		boolean[][][] volcanicQuarts = new boolean[4][quartLayers][4];
		boolean foundBiome = false;
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

		for (int qx = 0; qx < 4; qx++) {
			for (int qy = 0; qy < quartLayers; qy++) {
				for (int qz = 0; qz < 4; qz++) {
					cursor.set(minX + qx * 4, minY + qy * 4, minZ + qz * 4);
					boolean volcanic = isVolcanic(level, cursor);
					volcanicQuarts[qx][qy][qz] = volcanic;
					foundBiome |= volcanic;
				}
			}
		}

		if (!foundBiome) {
			return false;
		}

		boolean[][][] volcanicPalette = buildVolcanicPaletteMask(level, minX, minZ, minY, maxY, cursor);
		List<BlockPos> lava = new ArrayList<>();
		boolean changed = false;

		// Pass 1: Smooth volcanic block-palette replacement.
		for (int x = minX; x < minX + 16; x++) {
			for (int z = minZ; z < minZ + 16; z++) {
				for (int y = minY; y <= maxY; y++) {
					boolean exactVolcanic = volcanicQuarts[(x - minX) >> 2][(y - minY) >> 2][(z - minZ) >> 2];
					boolean paletteVolcanic = volcanicPalette[x - minX][y - minY][z - minZ];
					cursor.set(x, y, z);
					BlockState current = level.getBlockState(cursor);
					boolean dripstoneFormation = current.is(Blocks.POINTED_DRIPSTONE)
						|| current.is(MinetaleBlocks.POINTED_VOLCANIC_ROCK)
						|| current.is(Blocks.DRIPSTONE_BLOCK);
					if (!exactVolcanic && !paletteVolcanic && !dripstoneFormation) continue;

					BlockState replacement = paletteVolcanic || dripstoneFormation ? replacementFor(level, cursor, current) : null;
					if (replacement != null && replacement != current) {
						level.setBlock(cursor, replacement, Block.UPDATE_CLIENTS);
						current = replacement;
						changed = true;
					}

					if (exactVolcanic && current.getFluidState().is(FluidTags.LAVA)) {
						lava.add(cursor.immutable());
					}
				}
			}
		}

		// Convert complete dripstone formations in a volcanic chunk, including portions that
		// extend beyond the individual biome cells that caused them to generate.
		changed |= replaceDripstoneFormations(level, minX, minZ, maxY + 1, Math.min(level.getMaxY(), maxY + 64));

		List<PoolPlacement> placedPools = new ArrayList<>();
		int targetPools = 3 + random.nextInt(3);
		for (int attempt = 0; attempt < 16 && placedPools.size() < targetPools; attempt++) {
			changed |= placeCavePool(level, random, minX, minZ, minY, maxY, lava, placedPools);
		}
		changed |= cleanLavaSurroundings(level, lava);
		heatDamageLavaRims(level, random, lava);
		return changed;
	}

	private static final int CHUNK_QUARTS = 4;
	private static final int PALETTE_QUART_HALO = 2;
	private static final double PALETTE_THRESHOLD = 0.5;

	/**
	 * Converts the hard 4x4x4 biome cells into a continuous block-resolution palette mask.
	 * A one-quart box blur removes isolated holes and protrusions, then trilinear sampling
	 * produces sloped boundaries instead of exposed checkerboard faces.
	 */
	private static boolean[][][] buildVolcanicPaletteMask(
		WorldGenLevel level,
		int minX,
		int minZ,
		int minY,
		int maxY,
		BlockPos.MutableBlockPos cursor
	) {
		int height = maxY - minY + 1;
		int quartLayers = (height + 3) / 4;
		int rawHorizontalSize = CHUNK_QUARTS + PALETTE_QUART_HALO * 2;
		int rawVerticalSize = quartLayers + PALETTE_QUART_HALO * 2;
		boolean[][][] raw = new boolean[rawHorizontalSize][rawVerticalSize][rawHorizontalSize];

		for (int rawX = 0; rawX < rawHorizontalSize; rawX++) {
			int quartX = rawX - PALETTE_QUART_HALO;
			for (int rawY = 0; rawY < rawVerticalSize; rawY++) {
				int quartY = rawY - PALETTE_QUART_HALO;
				int sampleY = minY + quartY * 4 + 2;
				if (sampleY < minY || sampleY > maxY) continue;
				for (int rawZ = 0; rawZ < rawHorizontalSize; rawZ++) {
					int quartZ = rawZ - PALETTE_QUART_HALO;
					cursor.set(minX + quartX * 4 + 2, sampleY, minZ + quartZ * 4 + 2);
					raw[rawX][rawY][rawZ] = isVolcanic(level, cursor);
				}
			}
		}

		int densityHorizontalSize = CHUNK_QUARTS + 2;
		int densityVerticalSize = quartLayers + 2;
		double[][][] density = new double[densityHorizontalSize][densityVerticalSize][densityHorizontalSize];
		for (int gridX = 0; gridX < densityHorizontalSize; gridX++) {
			for (int gridY = 0; gridY < densityVerticalSize; gridY++) {
				for (int gridZ = 0; gridZ < densityHorizontalSize; gridZ++) {
					int volcanicSamples = 0;
					for (int dx = 0; dx < 3; dx++) {
						for (int dy = 0; dy < 3; dy++) {
							for (int dz = 0; dz < 3; dz++) {
								if (raw[gridX + dx][gridY + dy][gridZ + dz]) volcanicSamples++;
							}
						}
					}
					density[gridX][gridY][gridZ] = volcanicSamples / 27.0;
				}
			}
		}

		boolean[][][] mask = new boolean[16][height][16];
		for (int localX = 0; localX < 16; localX++) {
			for (int localY = 0; localY < height; localY++) {
				for (int localZ = 0; localZ < 16; localZ++) {
					double threshold = PALETTE_THRESHOLD
						+ (paletteNoise(minX + localX, minY + localY, minZ + localZ) - 0.5) * 0.24;
					mask[localX][localY][localZ] = samplePaletteDensity(density, localX, localY, localZ) >= threshold;
				}
			}
		}
		return mask;
	}

	private static double samplePaletteDensity(double[][][] density, int localX, int localY, int localZ) {
		double quartX = (localX - 2) / 4.0;
		double quartY = (localY - 2) / 4.0;
		double quartZ = (localZ - 2) / 4.0;
		int lowerX = (int)Math.floor(quartX);
		int lowerY = (int)Math.floor(quartY);
		int lowerZ = (int)Math.floor(quartZ);
		double blendX = smoothStep(quartX - lowerX);
		double blendY = smoothStep(quartY - lowerY);
		double blendZ = smoothStep(quartZ - lowerZ);
		int indexX = lowerX + 1;
		int indexY = lowerY + 1;
		int indexZ = lowerZ + 1;

		double x00 = lerp(density[indexX][indexY][indexZ], density[indexX + 1][indexY][indexZ], blendX);
		double x10 = lerp(density[indexX][indexY + 1][indexZ], density[indexX + 1][indexY + 1][indexZ], blendX);
		double x01 = lerp(density[indexX][indexY][indexZ + 1], density[indexX + 1][indexY][indexZ + 1], blendX);
		double x11 = lerp(density[indexX][indexY + 1][indexZ + 1], density[indexX + 1][indexY + 1][indexZ + 1], blendX);
		return lerp(lerp(x00, x10, blendY), lerp(x01, x11, blendY), blendZ);
	}

	private static double smoothStep(double value) {
		return value * value * (3.0 - 2.0 * value);
	}

	private static double lerp(double start, double end, double delta) {
		return start + (end - start) * delta;
	}

	private static final int PALETTE_NOISE_SCALE = 12;

	/** Low-frequency value noise keeps the blurred biome edge from forming long planar lines. */
	private static double paletteNoise(int x, int y, int z) {
		int cellX = Math.floorDiv(x, PALETTE_NOISE_SCALE);
		int cellY = Math.floorDiv(y, PALETTE_NOISE_SCALE);
		int cellZ = Math.floorDiv(z, PALETTE_NOISE_SCALE);
		double blendX = smoothStep(Math.floorMod(x, PALETTE_NOISE_SCALE) / (double)PALETTE_NOISE_SCALE);
		double blendY = smoothStep(Math.floorMod(y, PALETTE_NOISE_SCALE) / (double)PALETTE_NOISE_SCALE);
		double blendZ = smoothStep(Math.floorMod(z, PALETTE_NOISE_SCALE) / (double)PALETTE_NOISE_SCALE);

		double x00 = lerp(hashNoise(cellX, cellY, cellZ), hashNoise(cellX + 1, cellY, cellZ), blendX);
		double x10 = lerp(hashNoise(cellX, cellY + 1, cellZ), hashNoise(cellX + 1, cellY + 1, cellZ), blendX);
		double x01 = lerp(hashNoise(cellX, cellY, cellZ + 1), hashNoise(cellX + 1, cellY, cellZ + 1), blendX);
		double x11 = lerp(hashNoise(cellX, cellY + 1, cellZ + 1), hashNoise(cellX + 1, cellY + 1, cellZ + 1), blendX);
		return lerp(lerp(x00, x10, blendY), lerp(x01, x11, blendY), blendZ);
	}

	private static double hashNoise(int x, int y, int z) {
		long hash = x * 0x9E3779B97F4A7C15L;
		hash ^= y * 0xC2B2AE3D27D4EB4FL;
		hash ^= z * 0x165667B19E3779F9L;
		hash ^= hash >>> 30;
		hash *= 0xBF58476D1CE4E5B9L;
		hash ^= hash >>> 27;
		hash *= 0x94D049BB133111EBL;
		hash ^= hash >>> 31;
		return (hash >>> 11) * 0x1.0p-53;
	}

	private static boolean replaceDripstoneFormations(
		WorldGenLevel level,
		int minX,
		int minZ,
		int minY,
		int maxY
	) {
		boolean changed = false;
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		for (int x = minX; x < minX + 16; x++) {
			for (int z = minZ; z < minZ + 16; z++) {
				for (int y = minY; y <= maxY; y++) {
					cursor.set(x, y, z);
					BlockState current = level.getBlockState(cursor);
					BlockState replacement = null;
					if (current.is(Blocks.POINTED_DRIPSTONE) || current.is(MinetaleBlocks.POINTED_VOLCANIC_ROCK)) {
						replacement = replacementFor(level, cursor, current);
					} else if (current.is(Blocks.DRIPSTONE_BLOCK)) {
						replacement = MinetaleBlocks.VOLCANIC_ROCK.defaultBlockState();
					}
					if (replacement != null) {
						level.setBlock(cursor, replacement, Block.UPDATE_CLIENTS);
						changed = true;
					}
				}
			}
		}
		return changed;
	}

	private static boolean isInWater(WorldGenLevel level, BlockPos pos, BlockState state) {
		if (state.hasProperty(SpeleothemBlock.WATERLOGGED) && state.getValue(SpeleothemBlock.WATERLOGGED)) {
			return true;
		}
		if (level.getFluidState(pos).is(FluidTags.WATER)) {
			return true;
		}
		if (level.getBlockState(pos.above()).getFluidState().is(FluidTags.WATER)) {
			return true;
		}
		if (level.getBlockState(pos.below()).getFluidState().is(FluidTags.WATER)) {
			return true;
		}

		int horizontalWater = 0;
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			if (level.getBlockState(pos.relative(dir)).getFluidState().is(FluidTags.WATER)) {
				horizontalWater++;
			}
		}
		return horizontalWater >= 2;
	}

	private static boolean isSubmergedInLava(WorldGenLevel level, BlockPos pos) {
		if (level.getBlockState(pos.above()).getFluidState().is(FluidTags.LAVA)) {
			return true;
		}

		int horizontalLava = 0;
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			if (level.getBlockState(pos.relative(dir)).getFluidState().is(FluidTags.LAVA)) {
				horizontalLava++;
			}
		}

		if (horizontalLava >= 2) {
			return true;
		}

		return horizontalLava >= 1 && level.getBlockState(pos.below()).getFluidState().is(FluidTags.LAVA);
	}

	private static BlockState replacementFor(WorldGenLevel level, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		if (block == Blocks.POINTED_DRIPSTONE || block == MinetaleBlocks.POINTED_VOLCANIC_ROCK) {
			if (isSubmergedInLava(level, pos)) {
				return Blocks.LAVA.defaultBlockState();
			}

			boolean waterlogged = isInWater(level, pos, state);
			return MinetaleBlocks.POINTED_VOLCANIC_ROCK.defaultBlockState()
				.setValue(SpeleothemBlock.TIP_DIRECTION, state.getValue(SpeleothemBlock.TIP_DIRECTION))
				.setValue(SpeleothemBlock.THICKNESS, state.getValue(SpeleothemBlock.THICKNESS))
				.setValue(SpeleothemBlock.WATERLOGGED, waterlogged);
		}

		Block ore = volcanicOreFor(block);
		if (ore != null) {
			return ore.defaultBlockState();
		}

		if (state.is(BlockTags.BASE_STONE_OVERWORLD)
			|| state.is(BlockTags.STONE_ORE_REPLACEABLES)
			|| state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)
			|| block == Blocks.DRIPSTONE_BLOCK
			|| block == Blocks.INFESTED_STONE
			|| block == Blocks.INFESTED_DEEPSLATE) {
			return MinetaleBlocks.VOLCANIC_ROCK.defaultBlockState();
		}
		return null;
	}

	private static Block volcanicOreFor(Block block) {
		if (block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE) return MinetaleBlocks.VOLCANIC_COAL_ORE;
		if (block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE) return MinetaleBlocks.VOLCANIC_COPPER_ORE;
		if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) return MinetaleBlocks.VOLCANIC_IRON_ORE;
		if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) return MinetaleBlocks.VOLCANIC_GOLD_ORE;
		if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) return MinetaleBlocks.VOLCANIC_REDSTONE_ORE;
		if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) return MinetaleBlocks.VOLCANIC_EMERALD_ORE;
		if (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE) return MinetaleBlocks.VOLCANIC_LAPIS_ORE;
		if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) return MinetaleBlocks.VOLCANIC_DIAMOND_ORE;
		return null;
	}

	private static boolean isHorizontalBiomeBoundary(WorldGenLevel level, BlockPos pos) {
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos adjacent = pos.relative(direction);
			if (level.isInsideBuildHeight(adjacent) && !isVolcanic(level, adjacent)) {
				return true;
			}
		}
		return false;
	}

	private static final int[][] HORIZONTAL_SAMPLE_OFFSETS = {
		{3, 0}, {-3, 0}, {0, 3}, {0, -3},
		{5, 0}, {-5, 0}, {0, 5}, {0, -5},
		{4, 4}, {-4, 4}, {4, -4}, {-4, -4},
		{8, 0}, {-8, 0}, {0, 8}, {0, -8}
	};

	private enum CavernSize {
		SMALL,
		MEDIUM,
		LARGE,
		HUGE
	}

	private static CavernSize evaluateCavernSize(WorldGenLevel level, BlockPos floorPos) {
		int x = floorPos.getX();
		int floorY = floorPos.getY();
		int z = floorPos.getZ();
		BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();

		int ceilingHeight = 0;
		while (ceilingHeight < 24) {
			probe.set(x, floorY + 1 + ceilingHeight, z);
			if (!level.isInsideBuildHeight(probe) || level.getBlockState(probe).isSolid()) {
				break;
			}
			ceilingHeight++;
		}

		int openSamples = 0;
		for (int[] offset : HORIZONTAL_SAMPLE_OFFSETS) {
			probe.set(x + offset[0], floorY + 1, z + offset[1]);
			if (level.isInsideBuildHeight(probe) && !level.getBlockState(probe).isSolid()) {
				openSamples++;
			}
		}

		double openRatio = (double)openSamples / HORIZONTAL_SAMPLE_OFFSETS.length;

		if (ceilingHeight >= 10 && openRatio >= 0.70) {
			return CavernSize.HUGE;
		} else if (ceilingHeight >= 6 && openRatio >= 0.50) {
			return CavernSize.LARGE;
		} else if (ceilingHeight >= 3 && openRatio >= 0.30) {
			return CavernSize.MEDIUM;
		}
		return CavernSize.SMALL;
	}

	private record PoolDimensions(int radiusX, int radiusZ, int depth) {}
	private record PoolPlacement(int x, int y, int z, int radius) {}

	private static PoolDimensions choosePoolDimensions(CavernSize size, RandomSource random) {
		return switch (size) {
			case HUGE -> new PoolDimensions(
				5 + random.nextInt(3),
				5 + random.nextInt(3),
				2 + random.nextInt(2)
			);
			case LARGE -> new PoolDimensions(
				4 + random.nextInt(2),
				4 + random.nextInt(2),
				2
			);
			case MEDIUM -> new PoolDimensions(
				3 + random.nextInt(2),
				3 + random.nextInt(2),
				1 + random.nextInt(2)
			);
			case SMALL -> new PoolDimensions(
				2,
				2,
				1
			);
		};
	}

	private static boolean placeCavePool(
		WorldGenLevel level,
		RandomSource random,
		int minX,
		int minZ,
		int minY,
		int maxY,
		List<BlockPos> lava,
		List<PoolPlacement> placedPools
	) {
		int searchTop = maxY - 4;
		int searchBottom = minY + 6;
		if (searchTop <= searchBottom) {
			return false;
		}

		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos floorProbe = new BlockPos.MutableBlockPos();

		for (int attempt = 0; attempt < 24; attempt++) {
			int x = minX + 2 + random.nextInt(12);
			int z = minZ + 2 + random.nextInt(12);
			int startY = searchBottom + random.nextInt(searchTop - searchBottom);

			int floorY = Integer.MIN_VALUE;
			for (int y = startY; y >= searchBottom; y--) {
				cursor.set(x, y, z);
				if (level.getBlockState(cursor).isAir()) {
					floorProbe.set(x, y - 1, z);
					BlockState belowState = level.getBlockState(floorProbe);
					if (belowState.isSolid() && !belowState.getFluidState().is(FluidTags.LAVA) && isVolcanic(level, cursor)) {
						floorY = y - 1;
						break;
					}
				}
			}

			if (floorY == Integer.MIN_VALUE) {
				for (int y = searchTop; y > startY; y--) {
					cursor.set(x, y, z);
					if (level.getBlockState(cursor).isAir()) {
						floorProbe.set(x, y - 1, z);
						BlockState belowState = level.getBlockState(floorProbe);
						if (belowState.isSolid() && !belowState.getFluidState().is(FluidTags.LAVA) && isVolcanic(level, cursor)) {
							floorY = y - 1;
							break;
						}
					}
				}
			}

			if (floorY == Integer.MIN_VALUE) {
				continue;
			}

			floorProbe.set(x, floorY, z);
			CavernSize cavernSize = evaluateCavernSize(level, floorProbe);
			PoolDimensions dimensions = choosePoolDimensions(cavernSize, random);
			int radiusX = dimensions.radiusX();
			int radiusZ = dimensions.radiusZ();
			int depth = dimensions.depth();
			int surfaceY = floorY;
			int maxRadius = Math.max(radiusX, radiusZ);

			// Maintain ground pathways and prevent pools at the same level from merging into a single sea
			boolean tooClose = false;
			for (PoolPlacement prev : placedPools) {
				if (Math.abs(floorY - prev.y()) <= 4) {
					int pDx = x - prev.x();
					int pDz = z - prev.z();
					int minDist = prev.radius() + maxRadius + 4;
					if (pDx * pDx + pDz * pDz < minDist * minDist) {
						tooClose = true;
						break;
					}
				}
			}
			if (tooClose) {
				continue;
			}

			List<BlockPos> poolCandidates = new ArrayList<>();
			int totalColumns = 0;
			int supportedColumns = 0;

			for (int dx = -radiusX; dx <= radiusX; dx++) {
				for (int dz = -radiusZ; dz <= radiusZ; dz++) {
					double normDist = (double)(dx * dx) / (radiusX * radiusX) + (double)(dz * dz) / (radiusZ * radiusZ);
					double wobble = 0.88 + 0.24 * paletteNoise(x + dx, surfaceY, z + dz);
					if (normDist > wobble) {
						continue;
					}

					int colX = x + dx;
					int colZ = z + dz;
					cursor.set(colX, surfaceY, colZ);
					if (!level.isInsideBuildHeight(cursor) || !isVolcanic(level, cursor) || isHorizontalBiomeBoundary(level, cursor)) {
						continue;
					}

					// Only place pool columns where the cave is open above the lava surface
					cursor.set(colX, surfaceY + 1, colZ);
					if (level.getBlockState(cursor).isSolid()) {
						continue;
					}

					// Preserve natural stepping stones and rocky outcrops in larger pools for a 50/50 lava/ground balance
					if (maxRadius >= 5 && normDist < 0.38 && paletteNoise(colX * 2, surfaceY, colZ * 2) > 0.58) {
						continue;
					}

					totalColumns++;

					boolean hasGround = false;
					for (int checkY = surfaceY + 1; checkY >= surfaceY - 3; checkY--) {
						cursor.set(colX, checkY, colZ);
						if (level.getBlockState(cursor).isSolid()) {
							hasGround = true;
							break;
						}
					}
					if (hasGround) {
						supportedColumns++;
					}

					int localDepth = Math.max(1, (int)Math.ceil(depth * (1.0 - normDist * 0.45)));
					for (int dy = 1 - localDepth; dy <= 0; dy++) {
						poolCandidates.add(new BlockPos(colX, surfaceY + dy, colZ));
					}
				}
			}

			if (totalColumns < 4 || (double)supportedColumns / totalColumns < 0.55 || poolCandidates.isEmpty()) {
				continue;
			}

			Set<BlockPos> poolSet = new HashSet<>(poolCandidates);

			// Commit lava and clear pointed dripstone / water in columns directly above
			for (BlockPos poolPos : poolCandidates) {
				level.setBlock(poolPos, Blocks.LAVA.defaultBlockState(), Block.UPDATE_CLIENTS);
				lava.add(poolPos);

				if (poolPos.getY() == surfaceY) {
					for (int upY = surfaceY + 1; upY <= Math.min(level.getMaxY(), surfaceY + 24); upY++) {
						cursor.set(poolPos.getX(), upY, poolPos.getZ());
						BlockState upState = level.getBlockState(cursor);
						if (upState.isSolid()) {
							break;
						}
						if (upState.is(MinetaleBlocks.POINTED_VOLCANIC_ROCK)
							|| upState.is(Blocks.POINTED_DRIPSTONE)
							|| upState.getFluidState().is(FluidTags.WATER)
							|| upState.is(Blocks.WATER)) {
							level.setBlock(cursor, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
						}
					}
				}
			}

			// Solidify bed underneath the pool bottom
			for (BlockPos poolPos : poolCandidates) {
				BlockPos below = poolPos.below();
				if (!poolSet.contains(below)) {
					BlockState belowState = level.getBlockState(below);
					if (!belowState.isSolid() && !belowState.getFluidState().is(FluidTags.LAVA)) {
						level.setBlock(below, MinetaleBlocks.VOLCANIC_ROCK.defaultBlockState(), Block.UPDATE_CLIENTS);
					}
				}
			}

			// Solidify perimeter rim around the pool
			for (BlockPos poolPos : poolCandidates) {
				for (Direction dir : Direction.Plane.HORIZONTAL) {
					BlockPos neighbor = poolPos.relative(dir);
					if (!poolSet.contains(neighbor)) {
						BlockState neighborState = level.getBlockState(neighbor);
						if (!neighborState.isSolid() && !neighborState.getFluidState().is(FluidTags.LAVA)) {
							level.setBlock(neighbor, MinetaleBlocks.VOLCANIC_ROCK.defaultBlockState(), Block.UPDATE_CLIENTS);
						}
					}
				}
			}

			placedPools.add(new PoolPlacement(x, surfaceY, z, maxRadius));
			return true;
		}
		return false;
	}

	private static boolean cleanLavaSurroundings(WorldGenLevel level, List<BlockPos> lava) {
		boolean changed = false;
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		Set<BlockPos> lavaSet = new HashSet<>(lava);

		for (BlockPos lavaPos : lava) {
			int lx = lavaPos.getX();
			int ly = lavaPos.getY();
			int lz = lavaPos.getZ();

			// If this lava block is at the surface of a lava pool, clear upwards
			cursor.set(lx, ly + 1, lz);
			if (!lavaSet.contains(cursor)) {
				for (int y = ly + 1; y <= Math.min(level.getMaxY(), ly + 32); y++) {
					cursor.set(lx, y, lz);
					BlockState state = level.getBlockState(cursor);
					if (state.isSolid()) {
						break;
					}

					if (state.is(MinetaleBlocks.POINTED_VOLCANIC_ROCK) || state.is(Blocks.POINTED_DRIPSTONE)) {
						level.setBlock(cursor, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
						changed = true;
					} else if (state.getFluidState().is(FluidTags.WATER) || state.is(Blocks.WATER)) {
						level.setBlock(cursor, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
						changed = true;
					}
				}
			}

			// 2. Clear submerged pointed rock or block water horizontally adjacent to lava
			for (Direction dir : Direction.Plane.HORIZONTAL) {
				cursor.set(lx + dir.getStepX(), ly, lz + dir.getStepZ());
				if (!lavaSet.contains(cursor)) {
					BlockState neighborState = level.getBlockState(cursor);
					if (neighborState.is(MinetaleBlocks.POINTED_VOLCANIC_ROCK) || neighborState.is(Blocks.POINTED_DRIPSTONE)) {
						if (isSubmergedInLava(level, cursor)) {
							level.setBlock(cursor, Blocks.LAVA.defaultBlockState(), Block.UPDATE_CLIENTS);
							changed = true;
						}
					} else if (neighborState.getFluidState().is(FluidTags.WATER) || neighborState.is(Blocks.WATER)) {
						level.setBlock(cursor, MinetaleBlocks.VOLCANIC_ROCK.defaultBlockState(), Block.UPDATE_CLIENTS);
						changed = true;
					}
				}

				cursor.set(lx + dir.getStepX(), ly + 1, lz + dir.getStepZ());
				BlockState aboveNeighborState = level.getBlockState(cursor);
				if (aboveNeighborState.getFluidState().is(FluidTags.WATER) || aboveNeighborState.is(Blocks.WATER)) {
					level.setBlock(cursor, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
					changed = true;
				}
			}

			// 3. Clear submerged pointed rock directly below lava
			cursor.set(lx, ly - 1, lz);
			if (!lavaSet.contains(cursor)) {
				BlockState belowState = level.getBlockState(cursor);
				if (belowState.is(MinetaleBlocks.POINTED_VOLCANIC_ROCK) || belowState.is(Blocks.POINTED_DRIPSTONE)) {
					level.setBlock(cursor, Blocks.LAVA.defaultBlockState(), Block.UPDATE_CLIENTS);
					changed = true;
				}
			}
		}
		return changed;
	}

	private static void heatDamageLavaRims(WorldGenLevel level, RandomSource random, List<BlockPos> lava) {
		BlockPos.MutableBlockPos rim = new BlockPos.MutableBlockPos();
		Set<BlockPos> lavaSet = new HashSet<>(lava);
		for (BlockPos lavaPos : lava) {
			if (lavaSet.contains(lavaPos.above())
				&& lavaSet.contains(lavaPos.below())
				&& lavaSet.contains(lavaPos.north())
				&& lavaSet.contains(lavaPos.south())
				&& lavaSet.contains(lavaPos.east())
				&& lavaSet.contains(lavaPos.west())) {
				continue;
			}

			for (int dx = -2; dx <= 2; dx++) {
				for (int dy = -2; dy <= 1; dy++) {
					for (int dz = -2; dz <= 2; dz++) {
						if (dx * dx + dy * dy + dz * dz > 6) continue;
						rim.set(lavaPos.getX() + dx, lavaPos.getY() + dy, lavaPos.getZ() + dz);
						if (level.isInsideBuildHeight(rim)
							&& isVolcanic(level, rim)
							&& level.getBlockState(rim).is(MinetaleBlocks.VOLCANIC_ROCK)
							&& random.nextFloat() < 0.7F) {
							level.setBlock(rim, MinetaleBlocks.CRACKED_VOLCANIC_ROCK.defaultBlockState(), Block.UPDATE_CLIENTS);
						}
					}
				}
			}
		}
	}

	private static boolean isVolcanic(WorldGenLevel level, BlockPos pos) {
		return level.getBiome(pos).is(MinetaleBiomes.VOLCANIC_CAVES);
	}
}
