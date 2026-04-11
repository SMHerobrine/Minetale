package com.smherobrine.minetale.client.renderer;

import com.smherobrine.minetale.client.renderer.model.HeartOfOrbisItemModel;
import com.smherobrine.minetale.item.HeartOfOrbisItem;
import com.geckolib.renderer.GeoItemRenderer;

public class HeartOfOrbisItemRenderer extends GeoItemRenderer<HeartOfOrbisItem> {
	public HeartOfOrbisItemRenderer() {
		super(new HeartOfOrbisItemModel());
	}
}
