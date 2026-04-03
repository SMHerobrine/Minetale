package com.smherobrine.minetale.client;

import com.smherobrine.minetale.block.entity.MinetaleBlockEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class MinetaleClient implements ClientModInitializer {
	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void onInitializeClient() {
		BlockEntityRendererRegistry.register((BlockEntityType) MinetaleBlockEntityTypes.AMBER_SIGN, context -> new StandingSignRenderer(context));
		BlockEntityRendererRegistry.register((BlockEntityType) MinetaleBlockEntityTypes.AMBER_HANGING_SIGN, context -> new HangingSignRenderer(context));
	}
}
