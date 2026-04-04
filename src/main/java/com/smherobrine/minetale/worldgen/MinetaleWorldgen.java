package com.smherobrine.minetale.worldgen;

import com.smherobrine.minetale.Minetale;
import com.smherobrine.minetale.worldgen.feature.LavaCaveTransformFeature;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class MinetaleWorldgen {
	public static final Feature<NoneFeatureConfiguration> LAVA_CAVE_TRANSFORM = Registry.register(
		BuiltInRegistries.FEATURE,
		Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "lava_cave_transform"),
		new LavaCaveTransformFeature(NoneFeatureConfiguration.CODEC)
	);

	private MinetaleWorldgen() {
	}

	public static void initialize() {
	}
}
