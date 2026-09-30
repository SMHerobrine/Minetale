package com.smherobrine.minetale.client;

import com.smherobrine.minetale.world.biome.MinetaleBiomes;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;

/** Client-only steam particles above exposed water in the volcanic caves biome. */
final class VolcanicWaterSteam {
	private static final RandomSource RANDOM = RandomSource.create();
	private static final int HORIZONTAL_RADIUS = 16;
	private static final int VERTICAL_RADIUS = 12;
	private static final int COLUMNS_PER_SCAN = 24;
	private static final int MAX_STEAM_COLUMNS = 3;
	private static int tickCounter;

	private VolcanicWaterSteam() {
	}

	static void initialize() {
		ClientTickEvents.END_CLIENT_TICK.register(VolcanicWaterSteam::spawnSteam);
	}

	private static void spawnSteam(Minecraft client) {
		if (client.level == null || client.player == null) return;
		if (++tickCounter % 2 != 0) return;

		BlockPos center = client.player.blockPosition();
		BlockPos.MutableBlockPos waterPos = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos abovePos = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos biomeProbe = new BlockPos.MutableBlockPos();
		int steamColumns = 0;
		for (int sample = 0; sample < COLUMNS_PER_SCAN && steamColumns < MAX_STEAM_COLUMNS; sample++) {
			int x = center.getX() + RANDOM.nextInt(HORIZONTAL_RADIUS * 2 + 1) - HORIZONTAL_RADIUS;
			int z = center.getZ() + RANDOM.nextInt(HORIZONTAL_RADIUS * 2 + 1) - HORIZONTAL_RADIUS;
			for (int y = center.getY() + VERTICAL_RADIUS; y >= center.getY() - VERTICAL_RADIUS; y--) {
				waterPos.set(x, y, z);
				abovePos.set(x, y + 1, z);
				if (!client.level.getFluidState(waterPos).is(FluidTags.WATER)
					|| !client.level.getBlockState(abovePos).isAir()
					|| !isNearVolcanicBiome(client.level, waterPos, biomeProbe)) {
					continue;
				}

				double particleX = x + RANDOM.nextDouble();
				double particleY = y + 1.05;
				double particleZ = z + RANDOM.nextDouble();
				double velocityX = (RANDOM.nextDouble() - 0.5) * 0.012;
				double velocityY = 0.035 + RANDOM.nextDouble() * 0.03;
				double velocityZ = (RANDOM.nextDouble() - 0.5) * 0.012;
				client.level.addParticle(
					ParticleTypes.CLOUD,
					particleX,
					particleY,
					particleZ,
					velocityX,
					velocityY,
					velocityZ
				);
				client.level.addParticle(
					ParticleTypes.WHITE_SMOKE,
					particleX,
					particleY + 0.05,
					particleZ,
					velocityX * 0.5,
					velocityY * 0.8,
					velocityZ * 0.5
				);
				if (RANDOM.nextInt(4) == 0) {
					client.level.addParticle(
						ParticleTypes.CAMPFIRE_COSY_SMOKE,
						particleX,
						particleY,
						particleZ,
						velocityX * 0.25,
						velocityY * 0.65,
						velocityZ * 0.25
					);
				}
				steamColumns++;
				break;
			}
		}
	}

	private static boolean isNearVolcanicBiome(
		ClientLevel level,
		BlockPos waterPos,
		BlockPos.MutableBlockPos probe
	) {
		for (int dx = -4; dx <= 4; dx += 4) {
			for (int dy = -4; dy <= 4; dy += 4) {
				for (int dz = -4; dz <= 4; dz += 4) {
					probe.set(waterPos.getX() + dx, waterPos.getY() + dy, waterPos.getZ() + dz);
					if (level.getBiome(probe).is(MinetaleBiomes.VOLCANIC_CAVES)) return true;
				}
			}
		}
		return false;
	}
}
