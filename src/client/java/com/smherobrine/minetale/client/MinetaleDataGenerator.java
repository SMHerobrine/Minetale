package com.smherobrine.minetale.client;

import com.smherobrine.minetale.world.biome.MinetaleBiomes;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class MinetaleDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(MinetaleWorldGenProvider::new);
	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder
			.add(Registries.FEATURE, MinetaleBiomes::bootstrapFeatures)
			.add(Registries.PLACED_FEATURE, MinetaleBiomes::bootstrapPlacedFeatures)
			.add(Registries.BIOME, MinetaleBiomes::bootstrapBiomes);
	}
}
