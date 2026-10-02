package com.smherobrine.minetale.world.biome;

import com.smherobrine.minetale.Minetale;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.CavePlacements;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;

public final class MinetaleBiomes {
	private static final Identifier VOLCANIC_CAVE_POST_PROCESSOR_ID = id("volcanic_cave_post_processor");
	private static final TagKey<Biome> VANILLA_CAVE_BIOMES = TagKey.create(Registries.BIOME, id("vanilla_cave_biomes"));

	public static final ResourceKey<Biome> VOLCANIC_CAVES = ResourceKey.create(Registries.BIOME, id("volcanic_caves"));
	public static final ResourceKey<Feature> VOLCANIC_CAVE_POST_PROCESSOR = ResourceKey.create(
		Registries.FEATURE,
		VOLCANIC_CAVE_POST_PROCESSOR_ID
	);
	public static final ResourceKey<PlacedFeature> VOLCANIC_CAVE_POST_PROCESSOR_PLACED = ResourceKey.create(
		Registries.PLACED_FEATURE,
		VOLCANIC_CAVE_POST_PROCESSOR_ID
	);

	private MinetaleBiomes() {
	}

	public static void initialize() {
		Registry.register(
			BuiltInRegistries.FEATURE_TYPE,
			VOLCANIC_CAVE_POST_PROCESSOR_ID,
			VolcanicCavePostProcessorFeature.CODEC
		);
		BiomeModifications.addFeature(
			BiomeSelectors.foundInOverworld(),
			GenerationStep.Decoration.TOP_LAYER_MODIFICATION,
			VOLCANIC_CAVE_POST_PROCESSOR_PLACED
		);
		// Ordinary underground caves retain their surface biome, so their decorations must
		// be added to those biomes explicitly. Dedicated cave biomes keep their own palette.
		for (String material : java.util.List.of("stone", "granite", "diorite", "andesite", "deepslate", "tuff", "calcite")) {
			BiomeModifications.addFeature(
				BiomeSelectors.foundInOverworld()
					.and(BiomeSelectors.tag(VANILLA_CAVE_BIOMES).negate())
					.and(BiomeSelectors.includeByKey(VOLCANIC_CAVES).negate()),
				GenerationStep.Decoration.UNDERGROUND_DECORATION,
				ResourceKey.create(Registries.PLACED_FEATURE, id("pointed_" + material + "_speleothems"))
			);
		}
	}

	public static void bootstrapFeatures(BootstrapContext<Feature> context) {
		context.register(VOLCANIC_CAVE_POST_PROCESSOR, new VolcanicCavePostProcessorFeature());
	}

	public static void bootstrapPlacedFeatures(BootstrapContext<PlacedFeature> context) {
		Holder<Feature> feature = context.lookup(Registries.FEATURE).getOrThrow(VOLCANIC_CAVE_POST_PROCESSOR);
		context.register(VOLCANIC_CAVE_POST_PROCESSOR_PLACED, new PlacedFeature(feature, java.util.List.of(BiomeFilter.biome())));
	}

	public static void bootstrapBiomes(BootstrapContext<Biome> context) {
		HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
		HolderGetter<WorldCarver> carvers = context.lookup(Registries.CARVER);
		BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder(placedFeatures, carvers);
		MobSpawnSettings.Builder mobs = new MobSpawnSettings.Builder();
		BiomeDefaultFeatures.commonSpawns(mobs);

		// Vanilla cave carving and placement keys preserve the normal shapes, heights, and rarity.
		generation.addCarver(Carvers.CAVE);
		generation.addCarver(Carvers.CAVE_EXTRA_UNDERGROUND);
		generation.addCarver(Carvers.CANYON);
		generation.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND);
		addVanillaOrePlacements(generation);
		generation.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, CavePlacements.LARGE_DRIPSTONE);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, CavePlacements.DRIPSTONE_CLUSTER);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, CavePlacements.POINTED_DRIPSTONE);

		Biome biome = new Biome.BiomeBuilder()
			.hasPrecipitation(false)
			.temperature(2.0F)
			.downfall(0.0F)
			.setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(0x4A1710))
			.setAttribute(EnvironmentAttributes.FOG_START_DISTANCE, 2.0F)
			.setAttribute(EnvironmentAttributes.FOG_END_DISTANCE, 40.0F)
			.setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(0x2B0D0A))
			.setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.vector3fFromRGB24(0x681B08))
			.setAttribute(EnvironmentAttributes.AMBIENT_LIGHT_COLOR, ARGB.vector3fFromRGB24(0x261008))
			.setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(ParticleTypes.ASH, 0.0125F))
			.setAttribute(EnvironmentAttributes.DEFAULT_DRIPSTONE_PARTICLE, ParticleTypes.DRIPPING_LAVA)
			.setAttribute(EnvironmentAttributes.WATER_EVAPORATES, true)
			.setAttribute(EnvironmentAttributes.SNOW_GOLEM_MELTS, true)
			.specialEffects(
				new BiomeSpecialEffects.Builder()
					.waterColor(0xE35B1C)
					.grassColorOverride(0x3A241C)
					.foliageColorOverride(0x43251A)
					.dryFoliageColorOverride(0x2B1712)
					.build()
			)
			.mobSpawnSettings(mobs.build())
			.generationSettings(generation.build())
			.build();

		context.register(VOLCANIC_CAVES, biome);
	}

	private static void addVanillaOrePlacements(BiomeGenerationSettings.Builder generation) {
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_COAL_UPPER);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_COAL_LOWER);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_IRON_UPPER);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_IRON_MIDDLE);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_IRON_SMALL);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GOLD);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_GOLD_LOWER);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_REDSTONE);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_REDSTONE_LOWER);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIAMOND);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIAMOND_MEDIUM);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIAMOND_LARGE);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_DIAMOND_BURIED);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_LAPIS);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_LAPIS_BURIED);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_COPPER);
		generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OrePlacements.ORE_EMERALD);
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(Minetale.MOD_ID, path);
	}
}
