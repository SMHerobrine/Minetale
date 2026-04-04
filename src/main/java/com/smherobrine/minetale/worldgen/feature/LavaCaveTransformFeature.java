package com.smherobrine.minetale.worldgen.feature;

import com.mojang.serialization.Codec;
import com.smherobrine.minetale.block.MinetaleBlocks;
import com.smherobrine.minetale.worldgen.MinetaleBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class LavaCaveTransformFeature extends Feature<NoneFeatureConfiguration> {
	private static final int MAX_LAVA_POOL_Y = -10;
	private static final int POINTED_LAVA_RADIUS = 2;
	private static final int LAVA_BLOB_RADIUS = 3;

	public LavaCaveTransformFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		WorldGenLevel level = context.level();
		BlockPos origin = context.origin();
		int minX = origin.getX() >> 4 << 4;
		int minZ = origin.getZ() >> 4 << 4;
		int maxX = minX + 15;
		int maxZ = minZ + 15;
		int minY = level.getMinY();
		int maxY = level.getMaxY();
		boolean changed = false;
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		for (int y = minY; y < maxY; y++) {
			for (int z = minZ; z <= maxZ; z++) {
				for (int x = minX; x <= maxX; x++) {
					pos.set(x, y, z);
					if (!level.getBiome(pos).is(MinetaleBiomes.LAVA_CAVE)) {
						continue;
					}

					BlockState state = level.getBlockState(pos);
					if (!shouldProcessState(state)) {
						continue;
					}

					BlockState replacement = replacementState(level, pos, state);
					if (replacement != null && !replacement.equals(state)) {
						level.setBlock(pos, replacement, Block.UPDATE_CLIENTS);
						changed = true;
					}
				}
			}
		}

		return changed;
	}

	private static BlockState replacementState(WorldGenLevel level, BlockPos pos, BlockState state) {
		Block block = state.getBlock();

		if (state.getFluidState().is(FluidTags.WATER)) {
			return pos.getY() <= MAX_LAVA_POOL_Y ? Blocks.LAVA.defaultBlockState() : null;
		}

		if (block == Blocks.AIR) {
			return shouldExpandLavaPool(level, pos) ? Blocks.LAVA.defaultBlockState() : null;
		}

		if (block == Blocks.POINTED_DRIPSTONE) {
			if (isNearLava(level, pos, POINTED_LAVA_RADIUS)) {
				return Blocks.AIR.defaultBlockState();
			}

			return MinetaleBlocks.POINTED_VOLCANIC_ROCK.defaultBlockState()
				.setValue(PointedDripstoneBlock.TIP_DIRECTION, state.getValue(PointedDripstoneBlock.TIP_DIRECTION))
				.setValue(PointedDripstoneBlock.THICKNESS, state.getValue(PointedDripstoneBlock.THICKNESS))
				.setValue(PointedDripstoneBlock.WATERLOGGED, false);
		}

		if (block == Blocks.DRIPSTONE_BLOCK) {
			return shouldUseCrackedVolcanicRock(level, pos)
				? MinetaleBlocks.CRACKED_VOLCANIC_ROCK.defaultBlockState()
				: MinetaleBlocks.VOLCANIC_ROCK.defaultBlockState();
		}

		BlockState ore = volcanicOreState(block);
		if (ore != null) {
			return ore;
		}

		if (isReplaceableStone(block)) {
			if (shouldExpandLavaPool(level, pos)) {
				return Blocks.LAVA.defaultBlockState();
			}

			return shouldUseCrackedVolcanicRock(level, pos)
				? MinetaleBlocks.CRACKED_VOLCANIC_ROCK.defaultBlockState()
				: MinetaleBlocks.VOLCANIC_ROCK.defaultBlockState();
		}

		return null;
	}

	private static boolean shouldProcessState(BlockState state) {
		Block block = state.getBlock();
		return state.getFluidState().is(FluidTags.WATER)
			|| block == Blocks.AIR
			|| block == Blocks.POINTED_DRIPSTONE
			|| block == Blocks.DRIPSTONE_BLOCK
			|| volcanicOreState(block) != null
			|| isReplaceableStone(block);
	}

	private static BlockState volcanicOreState(Block block) {
		if (block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE) {
			return MinetaleBlocks.VOLCANIC_COAL_ORE.defaultBlockState();
		}
		if (block == Blocks.COPPER_ORE || block == Blocks.DEEPSLATE_COPPER_ORE) {
			return MinetaleBlocks.VOLCANIC_COPPER_ORE.defaultBlockState();
		}
		if (block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE) {
			return MinetaleBlocks.VOLCANIC_IRON_ORE.defaultBlockState();
		}
		if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE) {
			return MinetaleBlocks.VOLCANIC_GOLD_ORE.defaultBlockState();
		}
		if (block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE) {
			return MinetaleBlocks.VOLCANIC_REDSTONE_ORE.defaultBlockState();
		}
		if (block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE) {
			return MinetaleBlocks.VOLCANIC_EMERALD_ORE.defaultBlockState();
		}
		if (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE) {
			return MinetaleBlocks.VOLCANIC_LAPIS_ORE.defaultBlockState();
		}
		if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
			return MinetaleBlocks.VOLCANIC_DIAMOND_ORE.defaultBlockState();
		}

		return null;
	}

	private static boolean isReplaceableStone(Block block) {
		return block == Blocks.STONE
			|| block == Blocks.DEEPSLATE
			|| block == Blocks.COBBLED_DEEPSLATE
			|| block == Blocks.TUFF
			|| block == Blocks.GRANITE
			|| block == Blocks.DIORITE
			|| block == Blocks.ANDESITE
			|| block == Blocks.CALCITE
			|| block == Blocks.DRIPSTONE_BLOCK;
	}

	private static boolean shouldUseCrackedVolcanicRock(WorldGenLevel level, BlockPos pos) {
		float blobValue = coarseBlobNoise(pos, 4, 3, 4);
		if (blobValue > 0.74F) return true;
		if (blobValue <= 0.28F) return false;
		int lavaInfluence = lavaInfluence(level, pos);
		float threshold = lavaInfluence > 0 ? 0.52F - 0.08F * lavaInfluence : 0.74F;
		return blobValue > threshold;
	}

	private static boolean shouldExpandLavaPool(WorldGenLevel level, BlockPos pos) {
		if (pos.getY() > MAX_LAVA_POOL_Y) {
			return false;
		}

		if (isBiomeBoundary(level, pos)) {
			return false;
		}

		int adjacentLava = 0;
		int openNeighbors = 0;
		BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();
		for (Direction direction : Direction.values()) {
			neighborPos.setWithOffset(pos, direction);
			BlockState neighborState = level.getBlockState(neighborPos);
			if (neighborState.is(Blocks.LAVA)) {
				adjacentLava++;
				openNeighbors++;
			} else if (neighborState.isAir()) {
				openNeighbors++;
			}
		}

		return adjacentLava >= 2 && openNeighbors >= 3;
	}

	private static int lavaInfluence(WorldGenLevel level, BlockPos pos) {
		int influence = 0;
		BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();
		for (Direction direction : Direction.values()) {
			neighborPos.set(pos);
			for (int distance = 1; distance <= LAVA_BLOB_RADIUS; distance++) {
				neighborPos.move(direction);
				if (level.getBlockState(neighborPos).is(Blocks.LAVA)) {
					influence = Math.max(influence, LAVA_BLOB_RADIUS - distance + 1);
					break;
				}
			}
		}

		return influence;
	}

	private static boolean isNearLava(WorldGenLevel level, BlockPos pos, int radius) {
		BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();
		for (Direction direction : Direction.values()) {
			neighborPos.set(pos);
			for (int distance = 1; distance <= radius; distance++) {
				neighborPos.move(direction);
				if (level.getBlockState(neighborPos).is(Blocks.LAVA)) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean isBiomeBoundary(WorldGenLevel level, BlockPos pos) {
		BlockPos.MutableBlockPos neighbor = new BlockPos.MutableBlockPos();
		for (Direction direction : Direction.values()) {
			neighbor.setWithOffset(pos, direction);
			if (!level.getBiome(neighbor).is(MinetaleBiomes.LAVA_CAVE)) {
				return true;
			}
		}

		return false;
	}

	private static float coarseBlobNoise(BlockPos pos, int scaleX, int scaleY, int scaleZ) {
		int x = Math.floorDiv(pos.getX(), scaleX);
		int y = Math.floorDiv(pos.getY(), scaleY);
		int z = Math.floorDiv(pos.getZ(), scaleZ);
		long hash = 1469598103934665603L;
		hash ^= x;
		hash *= 1099511628211L;
		hash ^= y;
		hash *= 1099511628211L;
		hash ^= z;
		hash *= 1099511628211L;
		return ((hash >>> 40) & 1023L) / 1023.0F;
	}
}
