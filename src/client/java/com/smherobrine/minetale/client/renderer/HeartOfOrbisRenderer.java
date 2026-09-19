package com.smherobrine.minetale.client.renderer;

import com.smherobrine.minetale.block.entity.HeartOfOrbisBlockEntity;
import com.smherobrine.minetale.client.renderer.model.HeartOfOrbisModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import com.geckolib.renderer.GeoBlockRenderer;

public class HeartOfOrbisRenderer extends GeoBlockRenderer<HeartOfOrbisBlockEntity, BlockEntityRenderState> {
	public HeartOfOrbisRenderer(BlockEntityRendererProvider.Context context) {
		super(context, new HeartOfOrbisModel());
	}
}
