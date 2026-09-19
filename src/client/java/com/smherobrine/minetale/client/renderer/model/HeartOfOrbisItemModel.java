package com.smherobrine.minetale.client.renderer.model;

import com.smherobrine.minetale.Minetale;
import com.smherobrine.minetale.item.HeartOfOrbisItem;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;

public class HeartOfOrbisItemModel extends GeoModel<HeartOfOrbisItem> {
	private static final Identifier MODEL_ID = Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "block/heart_of_orbis");
	private static final Identifier TEXTURE_ID = Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "textures/block/heart_of_orbis.png");

	@Override
	public Identifier getModelResource(GeoRenderState renderState) {
		return MODEL_ID;
	}

	@Override
	public Identifier getTextureResource(GeoRenderState renderState) {
		return TEXTURE_ID;
	}

	@Override
	public Identifier getAnimationResource(HeartOfOrbisItem animatable) {
		return null;
	}
}
