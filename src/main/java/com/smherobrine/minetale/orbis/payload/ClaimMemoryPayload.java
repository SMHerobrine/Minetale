package com.smherobrine.minetale.orbis.payload;

import com.smherobrine.minetale.Minetale;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClaimMemoryPayload(String entityId) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ClaimMemoryPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "claim_memory"));
	public static final StreamCodec<FriendlyByteBuf, ClaimMemoryPayload> CODEC = CustomPacketPayload.codec(ClaimMemoryPayload::write, ClaimMemoryPayload::new);

	private ClaimMemoryPayload(FriendlyByteBuf buf) {
		this(buf.readUtf());
	}

	private void write(FriendlyByteBuf buf) {
		buf.writeUtf(this.entityId);
	}

	@Override
	public Type<ClaimMemoryPayload> type() {
		return TYPE;
	}
}
