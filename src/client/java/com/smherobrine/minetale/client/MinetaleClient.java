package com.smherobrine.minetale.client;

import com.smherobrine.minetale.block.entity.MinetaleBlockEntityTypes;
import com.smherobrine.minetale.client.orbis.OrbisClientNetworking;
import com.smherobrine.minetale.client.renderer.HeartOfOrbisRenderer;
import com.smherobrine.minetale.client.screen.HeartOfOrbisScreen;
import com.smherobrine.minetale.menu.MinetaleMenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class MinetaleClient implements ClientModInitializer {
	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void onInitializeClient() {
		OrbisClientNetworking.initialize();
		BlockEntityRendererRegistry.register((BlockEntityType) MinetaleBlockEntityTypes.AMBER_SIGN, context -> new StandingSignRenderer(context));
		BlockEntityRendererRegistry.register((BlockEntityType) MinetaleBlockEntityTypes.AMBER_HANGING_SIGN, context -> new HangingSignRenderer(context));
		BlockEntityRendererRegistry.register((BlockEntityType) MinetaleBlockEntityTypes.HEART_OF_ORBIS, HeartOfOrbisRenderer::new);
		MenuScreens.register(MinetaleMenuTypes.HEART_OF_ORBIS, HeartOfOrbisScreen::new);
	}
}
