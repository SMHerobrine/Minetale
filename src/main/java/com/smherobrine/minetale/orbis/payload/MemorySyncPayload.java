package com.smherobrine.minetale.orbis.payload;

import com.smherobrine.minetale.Minetale;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record MemorySyncPayload(Set<String> unlocked, Set<String> claimed) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<MemorySyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "memory_sync"));
	public static final StreamCodec<FriendlyByteBuf, MemorySyncPayload> CODEC = CustomPacketPayload.codec(MemorySyncPayload::write, MemorySyncPayload::new);

	public MemorySyncPayload {
		unlocked = Set.copyOf(unlocked);
		claimed = Set.copyOf(claimed);
	}

	private MemorySyncPayload(FriendlyByteBuf buf) {
		this(
			buf.readCollection(LinkedHashSet::new, FriendlyByteBuf::readUtf),
			buf.readCollection(LinkedHashSet::new, FriendlyByteBuf::readUtf)
		);
	}

	private void write(FriendlyByteBuf buf) {
		buf.writeCollection(this.unlocked, FriendlyByteBuf::writeUtf);
		buf.writeCollection(this.claimed, FriendlyByteBuf::writeUtf);
	}

	@Override
	public Type<MemorySyncPayload> type() {
		return TYPE;
	}
}
