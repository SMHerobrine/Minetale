package com.smherobrine.minetale.client.renderer;

import com.geckolib.animatable.client.GeoRenderProvider;

public final class HeartOfOrbisItemRenderProvider implements GeoRenderProvider {
	private HeartOfOrbisItemRenderer renderer;

	@Override
	public HeartOfOrbisItemRenderer getGeoItemRenderer() {
		if (this.renderer == null) {
			this.renderer = new HeartOfOrbisItemRenderer();
		}

		return this.renderer;
	}
}
