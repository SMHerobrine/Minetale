package com.smherobrine.minetale.world;

import com.smherobrine.minetale.Minetale;
import com.smherobrine.minetale.block.ForgottenTempleGatewayBlock;
import com.smherobrine.minetale.block.MinetaleBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public final class EdgeOfTheEchoDimension {
	public static final ResourceKey<Level> LEVEL_KEY = ResourceKey.create(
		Registries.DIMENSION,
		Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "edge_of_the_echo")
	);
	private static final BlockPos ISLAND_CENTER = new BlockPos(0, 64, 0);
	private static final BlockPos GATEWAY_CENTER = ISLAND_CENTER.above();

	private EdgeOfTheEchoDimension() {
	}

	public static boolean teleportFromGateway(ServerPlayer player, ResourceKey<Level> sourceDimension, BlockPos sourceGatewayCenter) {
		ServerLevel destination = (ServerLevel) player.level().getServer().getLevel(LEVEL_KEY);
		if (destination == null) {
			return false;
		}

		EdgeOfTheEchoGatewayTracker.setReturnGateway(player, new EdgeOfTheEchoGatewayState(sourceDimension, sourceGatewayCenter.immutable()));
		EdgeOfTheEchoGatewayTracker.setCooldown(player);
		ensureSpawnIsland(destination);
		ensureGateway(destination);
		Vec3 spawnPos = new Vec3(GATEWAY_CENTER.getX() + 0.5D, GATEWAY_CENTER.getY() + 0.5D, GATEWAY_CENTER.getZ() + 0.5D);
		player.teleportTo(destination, spawnPos.x(), spawnPos.y(), spawnPos.z(), Set.of(), player.getYRot(), player.getXRot(), false);
		EdgeOfTheEchoGatewayTracker.setCooldown(player);
		return true;
	}

	public static boolean returnFromGateway(ServerPlayer player) {
		EdgeOfTheEchoGatewayState returnGateway = EdgeOfTheEchoGatewayTracker.getReturnGateway(player);
		if (returnGateway == null) {
			return false;
		}

		ServerLevel destination = (ServerLevel) player.level().getServer().getLevel(returnGateway.dimension());
		if (destination == null) {
			return false;
		}

		Vec3 spawnPos = new Vec3(returnGateway.centerPos().getX() + 0.5D, returnGateway.centerPos().getY() + 0.5D, returnGateway.centerPos().getZ() + 0.5D);
		player.teleportTo(destination, spawnPos.x(), spawnPos.y(), spawnPos.z(), Set.of(), player.getYRot(), player.getXRot(), false);
		EdgeOfTheEchoGatewayTracker.setCooldown(player);
		return true;
	}

	private static void ensureSpawnIsland(ServerLevel level) {
		if (!level.getBlockState(ISLAND_CENTER).isAir()) {
			return;
		}

		BlockState topBlock = Blocks.GRASS_BLOCK.defaultBlockState();
		BlockState fillerBlock = Blocks.DIRT.defaultBlockState();
		BlockState coreBlock = Blocks.STONE.defaultBlockState();
		BlockState rimBlock = Blocks.MOSS_BLOCK.defaultBlockState();

		for (int x = -12; x <= 12; x++) {
			for (int z = -12; z <= 12; z++) {
				double distance = Math.sqrt(x * x + z * z);
				if (distance > 12.4D) {
					continue;
				}

				double ridgeNoise = Math.sin(x * 0.45D) * 0.65D + Math.cos(z * 0.4D) * 0.65D;
				double edgeDrop = Math.max(0.0D, distance - 7.5D);
				int height = Math.max(2, 8 - (int) Math.floor(edgeDrop * 0.95D + Math.max(0.0D, distance - 3.0D) * 0.25D - ridgeNoise));

				for (int y = 0; y < height; y++) {
					BlockPos placePos = ISLAND_CENTER.offset(x, -y, z);
					BlockState state;
					if (y == 0) {
						state = distance > 10.0D ? rimBlock : topBlock;
					} else if (y <= 3) {
						state = fillerBlock;
					} else {
						state = coreBlock;
					}
					level.setBlock(placePos, state, 3);
				}
			}
		}
	}

	private static void ensureGateway(ServerLevel level) {
		BlockState centerState = level.getBlockState(GATEWAY_CENTER);
		if (centerState.is(MinetaleBlocks.FORGOTTEN_TEMPLE_GATEWAY) && centerState.getValue(ForgottenTempleGatewayBlock.ACTIVE)) {
			return;
		}

		BlockState baseState = MinetaleBlocks.FORGOTTEN_TEMPLE_GATEWAY.defaultBlockState().setValue(ForgottenTempleGatewayBlock.ACTIVE, true);
		for (int partX = 0; partX < 3; partX++) {
			for (int partZ = 0; partZ < 3; partZ++) {
				BlockPos partPos = GATEWAY_CENTER.offset(partX - 1, 0, partZ - 1);
				BlockState partState = baseState.setValue(ForgottenTempleGatewayBlock.PART_X_ACCESSOR.get(), partX)
					.setValue(ForgottenTempleGatewayBlock.PART_Z_ACCESSOR.get(), partZ);
				level.setBlock(partPos, partState, 3);
			}
		}
	}
}
