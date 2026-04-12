package com.smherobrine.minetale.world;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;

import java.lang.reflect.Method;

public final class EdgeOfTheEchoProtection {
	private static final long FIXED_DAYTIME = 6000L;

	private EdgeOfTheEchoProtection() {
	}

	public static void initialize() {
		PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> !isRestricted(player, level));

		AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			return isRestricted(player, level) ? InteractionResult.FAIL : InteractionResult.PASS;
		});

		UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
			if (!isRestricted(player, level)) {
				return InteractionResult.PASS;
			}

			return mayPlaceFromHand(player) ? InteractionResult.FAIL : InteractionResult.PASS;
		});

		UseItemCallback.EVENT.register((player, level, hand) -> {
			if (!isRestricted(player, level)) {
				return InteractionResult.PASS;
			}

			return mayPlaceFromHand(player) ? InteractionResult.FAIL : InteractionResult.PASS;
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			Level level = server.getLevel(EdgeOfTheEchoDimension.LEVEL_KEY);
			if (level != null) {
				lockDaytime(level);
			}
		});
	}

	private static boolean mayPlaceFromHand(Player player) {
		return player.getMainHandItem().getItem() instanceof BlockItem
			|| player.getMainHandItem().getItem() instanceof BucketItem
			|| player.getOffhandItem().getItem() instanceof BlockItem
			|| player.getOffhandItem().getItem() instanceof BucketItem;
	}

	private static boolean isRestricted(Player player, Level level) {
		return !player.isCreative()
			&& !player.isSpectator()
			&& level.dimension() == EdgeOfTheEchoDimension.LEVEL_KEY;
	}

	private static void lockDaytime(Level level) {
		if (invokeTimeSetter(level, "setDayTime")) {
			return;
		}

		Object levelData = level.getLevelData();
		if (invokeTimeSetter(levelData, "setDayTime")) {
			return;
		}

		if (invokeTimeSetter(levelData, "setTimeOfDay")) {
			return;
		}

		invokeTimeSetter(levelData, "setTime");
	}

	private static boolean invokeTimeSetter(Object target, String methodName) {
		try {
			Method method = target.getClass().getMethod(methodName, long.class);
			method.invoke(target, FIXED_DAYTIME);
			return true;
		} catch (ReflectiveOperationException ignored) {
			return false;
		}
	}
}
