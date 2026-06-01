package com.smherobrine.minetale.client.renderer;

import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.smherobrine.minetale.block.entity.DecorativeGeoBlockEntity;
import com.smherobrine.minetale.client.renderer.model.DecorativeGeoBlockModel;
import com.smherobrine.minetale.client.renderer.model.DecorativeGeoModelData;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class DecorativeGeoBlockRenderer extends GeoBlockRenderer<DecorativeGeoBlockEntity, BlockEntityRenderState> {
	public DecorativeGeoBlockRenderer(BlockEntityRendererProvider.Context context) {
		super(context, new DecorativeGeoBlockModel());
	}

	@Override
	public void scaleModelForRender(RenderPassInfo<BlockEntityRenderState> renderPassInfo, float widthScale, float heightScale) {
		float renderWidthScale = DecorativeGeoModelData.getWidthScale(renderPassInfo);
		float renderHeightScale = DecorativeGeoModelData.getHeightScale(renderPassInfo);
		super.scaleModelForRender(renderPassInfo, widthScale * renderWidthScale, heightScale * renderHeightScale);
	}
}
