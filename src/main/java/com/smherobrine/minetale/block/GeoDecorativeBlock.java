package com.smherobrine.minetale.block;

import net.minecraft.resources.Identifier;

public interface GeoDecorativeBlock {
	Identifier getGeoModelId();

	Identifier getGeoTextureId();

	float getGeoRenderScale();

	default float getGeoRenderWidthScale() {
		return getGeoRenderScale();
	}

	default float getGeoRenderHeightScale() {
		return getGeoRenderScale();
	}
}
