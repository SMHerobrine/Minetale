package com.smherobrine.minetale.client.renderer.model;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.smherobrine.minetale.item.DecorativeGeoBlockItem;
import net.minecraft.resources.Identifier;

public class DecorativeGeoBlockItemModel extends GeoModel<DecorativeGeoBlockItem> {
	@Override
	public void addAdditionalStateData(DecorativeGeoBlockItem animatable, Object relatedObject, GeoRenderState renderState) {
		DecorativeGeoModelData.add(animatable.getGeoDecorativeBlock(), renderState);
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
	public Identifier getAnimationResource(DecorativeGeoBlockItem animatable) {
		return null;
	}
}
