package com.smherobrine.minetale.client;

import com.smherobrine.minetale.world.biome.MinetaleBiomes;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;

final class MinetaleWorldGenProvider extends FabricDynamicRegistryProvider {
	MinetaleWorldGenProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void configure(HolderLookup.Provider registries, Entries entries) {
		entries.add(registries.lookupOrThrow(Registries.FEATURE), MinetaleBiomes.VOLCANIC_CAVE_POST_PROCESSOR);
		entries.add(registries.lookupOrThrow(Registries.PLACED_FEATURE), MinetaleBiomes.VOLCANIC_CAVE_POST_PROCESSOR_PLACED);
		entries.add(registries.lookupOrThrow(Registries.BIOME), MinetaleBiomes.VOLCANIC_CAVES);
	}

	@Override
	public String getName() {
		return "Minetale world generation";
	}
}
