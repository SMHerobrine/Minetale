package com.smherobrine.minetale.orbis;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public final class MemoryUnlockTracker {
	private static final double LOOK_RANGE = 32.0D;
	private static final int CHECK_INTERVAL_TICKS = 10;

	private MemoryUnlockTracker() {
	}

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				if (player.tickCount % CHECK_INTERVAL_TICKS != 0) {
					continue;
				}

				checkPlayerSightline(player);
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> OrbisNetworking.sendMemorySync(handler.player));
		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			if (oldPlayer instanceof PlayerMemoryAccess oldAccess && newPlayer instanceof PlayerMemoryAccess newAccess) {
				newAccess.minetale$copyMemoryDataFrom(oldAccess);
			}
		});
	}

	private static void checkPlayerSightline(ServerPlayer player) {
		if (!(player instanceof PlayerMemoryAccess access)) {
			return;
		}

		Mob mob = getLookedAtMob(player);
		if (mob == null || access.minetale$hasUnlockedMemory(mob.getType())) {
			return;
		}

		if (access.minetale$unlockMemory(mob.getType())) {
			OrbisNetworking.sendMemorySync(player);
			OrbisNetworking.sendMemoryToast(player, mob.getType());
		}
	}

	private static Mob getLookedAtMob(ServerPlayer player) {
		Vec3 eyePosition = player.getEyePosition();
		Vec3 viewVector = player.getViewVector(1.0F);
		Vec3 reachEnd = eyePosition.add(viewVector.scale(LOOK_RANGE));
		AABB searchBox = player.getBoundingBox().expandTowards(viewVector.scale(LOOK_RANGE)).inflate(1.0D);
		EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
			player,
			eyePosition,
			reachEnd,
			searchBox,
			entity -> isValidTarget(player, entity),
			LOOK_RANGE * LOOK_RANGE
		);
		return hitResult != null && hitResult.getEntity() instanceof Mob mob ? mob : null;
	}

	private static boolean isValidTarget(ServerPlayer player, Entity entity) {
		return entity instanceof Mob mob
			&& entity.isPickable()
			&& !entity.isSpectator()
			&& OrbisMemoryHelper.isTrackable(mob.getType())
			&& player.hasLineOfSight(entity);
	}
}
