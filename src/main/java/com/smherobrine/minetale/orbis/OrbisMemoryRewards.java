package com.smherobrine.minetale.orbis;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.smherobrine.minetale.Minetale;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;

public final class OrbisMemoryRewards {
	private static final Gson GSON = new Gson();
	private static final Identifier RELOAD_ID = Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "orbis_memory_rewards");
	private static final String REWARD_PATH = "orbis_memory_rewards";
	private static final int DEFAULT_EXPERIENCE = 3;
	private static Map<String, Integer> experienceRewards = Map.of();

	private OrbisMemoryRewards() {
	}

	public static void initialize() {
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return RELOAD_ID;
			}

			@Override
			public void onResourceManagerReload(ResourceManager manager) {
				experienceRewards = loadRewards(manager);
			}
		});
	}

	public static int getExperienceReward(EntityType<?> type) {
		return experienceRewards.getOrDefault(OrbisMemoryHelper.getMemoryKey(type), DEFAULT_EXPERIENCE);
	}

	private static Map<String, Integer> loadRewards(ResourceManager manager) {
		Map<String, Integer> loadedRewards = new HashMap<>();

		for (Map.Entry<Identifier, Resource> entry : manager.listResources(REWARD_PATH, path -> path.getPath().endsWith(".json")).entrySet()) {
			try (BufferedReader reader = entry.getValue().openAsReader()) {
				JsonElement json = GSON.fromJson(reader, JsonElement.class);
				if (!(json instanceof JsonObject object)) {
					throw new JsonParseException("Reward file must contain a JSON object");
				}

				String entityTypeId = object.get("entity_type").getAsString();
				Identifier entityId = Identifier.tryParse(entityTypeId);
				if (entityId == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(entityId)) {
					throw new JsonParseException("Unknown entity type: " + entityTypeId);
				}

				int experience = object.has("experience") ? object.get("experience").getAsInt() : DEFAULT_EXPERIENCE;
				loadedRewards.put(entityId.toString(), Math.max(0, experience));
			} catch (IOException | RuntimeException exception) {
				Minetale.LOGGER.error("Failed to load Orbis memory reward from {}", entry.getKey(), exception);
			}
		}

		return Map.copyOf(loadedRewards);
	}
}
