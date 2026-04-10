package com.smherobrine.minetale.mixin;

import com.mojang.serialization.Codec;
import com.smherobrine.minetale.orbis.OrbisMemoryHelper;
import com.smherobrine.minetale.orbis.PlayerMemoryAccess;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements PlayerMemoryAccess {
	@Unique
	private static final String MINETALE_MEMORIES_TAG = "MinetaleMemories";
	@Unique
	private static final String UNLOCKED_TAG = "Unlocked";
	@Unique
	private static final String CLAIMED_TAG = "Claimed";
	@Unique
	private final Set<String> minetale$unlockedMemories = new LinkedHashSet<>();
	@Unique
	private final Set<String> minetale$claimedMemories = new LinkedHashSet<>();

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void minetale$saveMemoryData(ValueOutput output, CallbackInfo ci) {
		ValueOutput memoryOutput = output.child(MINETALE_MEMORIES_TAG);
		writeStringSet(memoryOutput, UNLOCKED_TAG, this.minetale$unlockedMemories);
		writeStringSet(memoryOutput, CLAIMED_TAG, this.minetale$claimedMemories);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void minetale$readMemoryData(ValueInput input, CallbackInfo ci) {
		this.minetale$unlockedMemories.clear();
		this.minetale$claimedMemories.clear();

		input.child(MINETALE_MEMORIES_TAG).ifPresent(memoryInput -> {
			readStringSet(memoryInput, UNLOCKED_TAG, this.minetale$unlockedMemories);
			readStringSet(memoryInput, CLAIMED_TAG, this.minetale$claimedMemories);
		});
	}

	@Override
	public boolean minetale$unlockMemory(EntityType<?> type) {
		return OrbisMemoryHelper.isTrackable(type) && this.minetale$unlockedMemories.add(OrbisMemoryHelper.getMemoryKey(type));
	}

	@Override
	public boolean minetale$claimMemory(EntityType<?> type) {
		String key = OrbisMemoryHelper.getMemoryKey(type);
		return this.minetale$unlockedMemories.contains(key) && this.minetale$claimedMemories.add(key);
	}

	@Override
	public boolean minetale$hasUnlockedMemory(EntityType<?> type) {
		return this.minetale$unlockedMemories.contains(OrbisMemoryHelper.getMemoryKey(type));
	}

	@Override
	public boolean minetale$hasClaimedMemory(EntityType<?> type) {
		return this.minetale$claimedMemories.contains(OrbisMemoryHelper.getMemoryKey(type));
	}

	@Override
	public Set<String> minetale$getUnlockedMemories() {
		return Set.copyOf(this.minetale$unlockedMemories);
	}

	@Override
	public Set<String> minetale$getClaimedMemories() {
		return Set.copyOf(this.minetale$claimedMemories);
	}

	@Override
	public void minetale$copyMemoryDataFrom(PlayerMemoryAccess other) {
		this.minetale$unlockedMemories.clear();
		this.minetale$unlockedMemories.addAll(other.minetale$getUnlockedMemories());
		this.minetale$claimedMemories.clear();
		this.minetale$claimedMemories.addAll(other.minetale$getClaimedMemories());
	}

	@Unique
	private static void writeStringSet(ValueOutput output, String key, Set<String> values) {
		ValueOutput.TypedOutputList<String> list = output.list(key, Codec.STRING);
		for (String value : values) {
			list.add(value);
		}
	}

	@Unique
	private static void readStringSet(ValueInput input, String key, Set<String> target) {
		input.list(key, Codec.STRING).ifPresent(list -> list.forEach(target::add));
	}
}
