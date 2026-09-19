package com.smherobrine.minetale.orbis;

import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

public final class OrbisMemoryHelper {
	private static final Set<EntityType<?>> EXCLUDED_TYPES = Set.of(
		EntityTypes.PLAYER,
		EntityTypes.GIANT,
		EntityTypes.ILLUSIONER
	);

	private OrbisMemoryHelper() {
	}

	public static boolean isTrackable(EntityType<?> type) {
		return !EXCLUDED_TYPES.contains(type);
	}

	public static String getMemoryKey(EntityType<?> type) {
		return BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
	}

	public static EntityType<?> getEntityType(String key) {
		Identifier id = Identifier.tryParse(key);
		return id == null ? null : BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
	}
}
