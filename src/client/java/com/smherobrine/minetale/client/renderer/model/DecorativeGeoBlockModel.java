package com.smherobrine.minetale.client.renderer.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.smherobrine.minetale.block.GeoDecorativeBlock;
import com.smherobrine.minetale.block.entity.DecorativeGeoBlockEntity;
import net.minecraft.resources.Identifier;

public class DecorativeGeoBlockModel extends GeoModel<DecorativeGeoBlockEntity> {
	@Override
	public void addAdditionalStateData(DecorativeGeoBlockEntity animatable, Object relatedObject, GeoRenderState renderState) {
		if (animatable.getBlockState().getBlock() instanceof GeoDecorativeBlock block) {
			DecorativeGeoModelData.add(block, renderState);
		}
	}

	@Override
	public Identifier getModelResource(GeoRenderState renderState) {
		return DecorativeGeoModelData.getModel(renderState);
	}

	@Override
	public Identifier getTextureResource(GeoRenderState renderState) {
		return DecorativeGeoModelData.getTexture(renderState);
	}

	@Override
	public Identifier getAnimationResource(DecorativeGeoBlockEntity animatable) {
		return null;
	}
}
