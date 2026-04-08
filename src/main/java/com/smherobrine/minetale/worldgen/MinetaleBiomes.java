package com.smherobrine.minetale.worldgen;

import com.smherobrine.minetale.Minetale;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public final class MinetaleBiomes {
	public static final ResourceKey<Biome> VOLCANIC_CAVE = ResourceKey.create(Registries.BIOME,
		Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "volcanic_cave"));

	private MinetaleBiomes() {
	}
}
