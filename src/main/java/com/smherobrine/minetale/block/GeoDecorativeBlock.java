package com.smherobrine.minetale.block;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

public interface GeoDecorativeBlock {
	Identifier getGeoModelId();

	default Identifier getGeoModelId(BlockState state) {
		return getGeoModelId();
	}

	Identifier getGeoTextureId();

	default Identifier getGeoTextureId(BlockState state) {
		return getGeoTextureId();
	}

	float getGeoRenderScale();

	default float getGeoRenderWidthScale() {
		return getGeoRenderScale();
	}

	default float getGeoRenderHeightScale() {
		return getGeoRenderScale();
	}
}
