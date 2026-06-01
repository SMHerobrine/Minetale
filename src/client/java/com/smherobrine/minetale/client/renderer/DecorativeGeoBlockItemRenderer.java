package com.smherobrine.minetale.client.renderer;

import com.geckolib.renderer.GeoItemRenderer;
import com.smherobrine.minetale.client.renderer.model.DecorativeGeoBlockItemModel;
import com.smherobrine.minetale.item.DecorativeGeoBlockItem;

public class DecorativeGeoBlockItemRenderer extends GeoItemRenderer<DecorativeGeoBlockItem> {
	public DecorativeGeoBlockItemRenderer() {
		super(new DecorativeGeoBlockItemModel());
	}
}
