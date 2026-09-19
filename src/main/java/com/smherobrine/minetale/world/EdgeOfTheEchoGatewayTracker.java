package com.smherobrine.minetale.world;

import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class EdgeOfTheEchoGatewayTracker {
	private static final Map<UUID, EdgeOfTheEchoGatewayState> RETURN_GATEWAYS = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> TELEPORT_COOLDOWNS = new ConcurrentHashMap<>();
	private static final int TELEPORT_COOLDOWN_TICKS = 40;

	private EdgeOfTheEchoGatewayTracker() {
	}

	public static EdgeOfTheEchoGatewayState getReturnGateway(ServerPlayer player) {
		return RETURN_GATEWAYS.get(player.getUUID());
	}

	public static void setReturnGateway(ServerPlayer player, EdgeOfTheEchoGatewayState state) {
		RETURN_GATEWAYS.put(player.getUUID(), state);
	}

	public static boolean isOnCooldown(ServerPlayer player) {
		Integer expiresAt = TELEPORT_COOLDOWNS.get(player.getUUID());
		return expiresAt != null && player.tickCount < expiresAt;
	}

	public static void setCooldown(ServerPlayer player) {
		TELEPORT_COOLDOWNS.put(player.getUUID(), player.tickCount + TELEPORT_COOLDOWN_TICKS);
	}
}
