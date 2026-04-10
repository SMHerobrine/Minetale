package com.smherobrine.minetale.menu;

import com.smherobrine.minetale.Minetale;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class MinetaleMenuTypes {
	public static final MenuType<HeartOfOrbisMenu> HEART_OF_ORBIS = Registry.register(
		BuiltInRegistries.MENU,
		Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "heart_of_orbis"),
		new MenuType<>(HeartOfOrbisMenu::new, FeatureFlags.VANILLA_SET)
	);

	private MinetaleMenuTypes() {
	}

	public static void initialize() {
	}
}
