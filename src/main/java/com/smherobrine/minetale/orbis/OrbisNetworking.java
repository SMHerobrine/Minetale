package com.smherobrine.minetale.orbis;

import com.smherobrine.minetale.orbis.payload.ClaimMemoryPayload;
import com.smherobrine.minetale.orbis.payload.MemorySyncPayload;
import com.smherobrine.minetale.orbis.payload.MemoryToastPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;

public final class OrbisNetworking {
	private OrbisNetworking() {
	}

	public static void initialize() {
		PayloadTypeRegistry.clientboundPlay().register(MemoryToastPayload.TYPE, MemoryToastPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(MemorySyncPayload.TYPE, MemorySyncPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(ClaimMemoryPayload.TYPE, ClaimMemoryPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(ClaimMemoryPayload.TYPE, (payload, context) -> {
			EntityType<?> type = OrbisMemoryHelper.getEntityType(payload.entityId());
			if (type == null || !OrbisMemoryHelper.isTrackable(type)) {
				return;
			}

			if (!(context.player() instanceof PlayerMemoryAccess access)) {
				return;
			}

			if (!access.minetale$hasUnlockedMemory(type) || access.minetale$hasClaimedMemory(type)) {
				return;
			}

			if (access.minetale$claimMemory(type)) {
				context.player().giveExperiencePoints(OrbisMemoryRewards.getExperienceReward(type));
				sendMemorySync(context.player());
			}
		});
	}

	public static void sendMemorySync(ServerPlayer player) {
		if (!(player instanceof PlayerMemoryAccess access) || !ServerPlayNetworking.canSend(player, MemorySyncPayload.TYPE)) {
			return;
		}

		ServerPlayNetworking.send(player, new MemorySyncPayload(access.minetale$getUnlockedMemories(), access.minetale$getClaimedMemories()));
	}

	public static void sendMemoryToast(ServerPlayer player, EntityType<?> type) {
		if (!ServerPlayNetworking.canSend(player, MemoryToastPayload.TYPE)) {
			return;
		}

		ServerPlayNetworking.send(player, new MemoryToastPayload(OrbisMemoryHelper.getMemoryKey(type), type.getDescription().getString()));
	}
}
