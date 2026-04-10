package com.smherobrine.minetale.client.renderer.model;

import com.smherobrine.minetale.Minetale;
import com.smherobrine.minetale.block.entity.HeartOfOrbisBlockEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedBlockGeoModel;

public class HeartOfOrbisModel extends DefaultedBlockGeoModel<HeartOfOrbisBlockEntity> {
	public HeartOfOrbisModel() {
		super(Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "heart_of_orbis"));
	}
}
