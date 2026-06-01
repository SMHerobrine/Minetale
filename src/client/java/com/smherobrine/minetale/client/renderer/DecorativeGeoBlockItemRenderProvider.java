package com.smherobrine.minetale.client.renderer;

import com.geckolib.animatable.client.GeoRenderProvider;

public final class DecorativeGeoBlockItemRenderProvider implements GeoRenderProvider {
	private DecorativeGeoBlockItemRenderer renderer;

	@Override
	public DecorativeGeoBlockItemRenderer getGeoItemRenderer() {
		if (this.renderer == null) {
			this.renderer = new DecorativeGeoBlockItemRenderer();
		}

		return this.renderer;
	}
}
