package com.smherobrine.minetale.orbis.payload;

import com.smherobrine.minetale.Minetale;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record MemoryToastPayload(String entityId, String mobName) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<MemoryToastPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "memory_toast"));
	public static final StreamCodec<FriendlyByteBuf, MemoryToastPayload> CODEC = CustomPacketPayload.codec(MemoryToastPayload::write, MemoryToastPayload::new);

	private MemoryToastPayload(FriendlyByteBuf buf) {
		this(buf.readUtf(), buf.readUtf());
	}

	private void write(FriendlyByteBuf buf) {
		buf.writeUtf(this.entityId);
		buf.writeUtf(this.mobName);
	}

	@Override
	public Type<MemoryToastPayload> type() {
		return TYPE;
	}
}
