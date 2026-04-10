package com.smherobrine.minetale.orbis;

import java.util.Set;
import net.minecraft.world.entity.EntityType;

public interface PlayerMemoryAccess {
	boolean minetale$unlockMemory(EntityType<?> type);

	boolean minetale$claimMemory(EntityType<?> type);

	boolean minetale$hasUnlockedMemory(EntityType<?> type);

	boolean minetale$hasClaimedMemory(EntityType<?> type);

	Set<String> minetale$getUnlockedMemories();

	Set<String> minetale$getClaimedMemories();

	void minetale$copyMemoryDataFrom(PlayerMemoryAccess other);
}
