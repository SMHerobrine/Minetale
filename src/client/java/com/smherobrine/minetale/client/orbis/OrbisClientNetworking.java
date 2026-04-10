package com.smherobrine.minetale.client.orbis;

import com.smherobrine.minetale.orbis.MinetaleSoundEvents;
import com.smherobrine.minetale.orbis.payload.ClaimMemoryPayload;
import com.smherobrine.minetale.orbis.payload.MemorySyncPayload;
import com.smherobrine.minetale.orbis.payload.MemoryToastPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public final class OrbisClientNetworking {
	private static final SystemToast.SystemToastId MEMORY_UNLOCK_TOAST = new SystemToast.SystemToastId();

	private OrbisClientNetworking() {
	}

	public static void initialize() {
		ClientPlayNetworking.registerGlobalReceiver(MemorySyncPayload.TYPE, (payload, context) -> {
			boolean hadBaseline = ClientMemoryState.isHydrated();
			int previousClaimedCount = ClientMemoryState.getClaimedCount();
			ClientMemoryState.replace(payload.unlocked(), payload.claimed());
			if (hadBaseline && payload.claimed().size() > previousClaimedCount && context.player() != null) {
				context.player().level().playLocalSound(
					context.player().getX(),
					context.player().getY(),
					context.player().getZ(),
					MinetaleSoundEvents.MEMORIES_UNLOCK,
					SoundSource.PLAYERS,
					0.95F,
					1.08F,
					false
				);
				context.player().level().playLocalSound(
					context.player().getX(),
					context.player().getY(),
					context.player().getZ(),
					SoundEvents.PLAYER_LEVELUP,
					SoundSource.PLAYERS,
					0.35F,
					1.45F,
					false
				);
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(MemoryToastPayload.TYPE, (payload, context) -> {
			Minecraft client = context.client();
			SystemToast.add(
				client.getToastManager(),
				MEMORY_UNLOCK_TOAST,
				Component.translatable("toast.minetale.memory_unlocked.title"),
				Component.translatable("toast.minetale.memory_unlocked.description", payload.mobName())
			);

			if (context.player() != null) {
				context.player().level().playLocalSound(
					context.player().getX(),
					context.player().getY(),
					context.player().getZ(),
					MinetaleSoundEvents.MEMORIES_UNLOCK,
					SoundSource.PLAYERS,
					0.9F,
					1.0F,
					false
				);
			}
		});
	}

	public static void claimMemory(String entityId) {
		ClientPlayNetworking.send(new ClaimMemoryPayload(entityId));
	}
}
