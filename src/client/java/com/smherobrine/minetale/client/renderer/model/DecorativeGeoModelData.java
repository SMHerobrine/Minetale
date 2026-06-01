package com.smherobrine.minetale.client.renderer.model;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.smherobrine.minetale.Minetale;
import com.smherobrine.minetale.block.GeoDecorativeBlock;
import net.minecraft.resources.Identifier;

public final class DecorativeGeoModelData {
	static final DataTicket<Identifier> MODEL_ID = DataTicket.create("minetale_decorative_geo_model", Identifier.class);
	static final DataTicket<Identifier> TEXTURE_ID = DataTicket.create("minetale_decorative_geo_texture", Identifier.class);
	private static final DataTicket<Float> RENDER_WIDTH_SCALE = DataTicket.create("minetale_decorative_geo_render_width_scale", Float.class);
	private static final DataTicket<Float> RENDER_HEIGHT_SCALE = DataTicket.create("minetale_decorative_geo_render_height_scale", Float.class);
	private static final Identifier FALLBACK_MODEL_ID = Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "block/gaia_statue");
	private static final Identifier FALLBACK_TEXTURE_ID = Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "textures/block/gaia_statue_marble.png");

	private DecorativeGeoModelData() {
	}

	static void add(GeoDecorativeBlock block, GeoRenderState renderState) {
		renderState.addGeckolibData(MODEL_ID, block.getGeoModelId());
		renderState.addGeckolibData(TEXTURE_ID, block.getGeoTextureId());
		renderState.addGeckolibData(RENDER_WIDTH_SCALE, block.getGeoRenderWidthScale());
		renderState.addGeckolibData(RENDER_HEIGHT_SCALE, block.getGeoRenderHeightScale());
	}

	static Identifier getModel(GeoRenderState renderState) {
		return renderState.getOrDefaultGeckolibData(MODEL_ID, FALLBACK_MODEL_ID);
	}

	static Identifier getTexture(GeoRenderState renderState) {
		return renderState.getOrDefaultGeckolibData(TEXTURE_ID, FALLBACK_TEXTURE_ID);
	}

	public static float getWidthScale(RenderPassInfo<?> renderPassInfo) {
		return renderPassInfo.getOrDefaultGeckolibData(RENDER_WIDTH_SCALE, 1.0F);
	}

	public static float getHeightScale(RenderPassInfo<?> renderPassInfo) {
		return renderPassInfo.getOrDefaultGeckolibData(RENDER_HEIGHT_SCALE, 1.0F);
	}
}
