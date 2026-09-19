package com.smherobrine.minetale.orbis.payload;

import com.smherobrine.minetale.Minetale;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record MemorySyncPayload(Set<String> unlocked, Set<String> claimed) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<MemorySyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "memory_sync"));
	private static final StreamCodec<FriendlyByteBuf, Set<String>> STRING_SET_CODEC = ByteBufCodecs.collection(
		LinkedHashSet::new, ByteBufCodecs.STRING_UTF8
	);
	public static final StreamCodec<FriendlyByteBuf, MemorySyncPayload> CODEC = StreamCodec.composite(
		STRING_SET_CODEC, MemorySyncPayload::unlocked,
		STRING_SET_CODEC, MemorySyncPayload::claimed,
		MemorySyncPayload::new
	);

	public MemorySyncPayload {
		unlocked = Set.copyOf(unlocked);
		claimed = Set.copyOf(claimed);
	}

	@Override
	public Type<MemorySyncPayload> type() {
		return TYPE;
	}
}
