package com.smherobrine.minetale.client.renderer;

import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.smherobrine.minetale.Minetale;
import com.smherobrine.minetale.block.GeoDecorativeBlock;
import com.smherobrine.minetale.block.entity.DecorativeGeoBlockEntity;
import com.smherobrine.minetale.client.renderer.model.DecorativeGeoBlockModel;
import com.smherobrine.minetale.client.renderer.model.DecorativeGeoModelData;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

public class DecorativeGeoBlockRenderer extends GeoBlockRenderer<DecorativeGeoBlockEntity, BlockEntityRenderState> {
	private static final Identifier GAIA_STATUE_MODEL_ID = Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "block/gaia_statue");

	public DecorativeGeoBlockRenderer(BlockEntityRendererProvider.Context context) {
		super(context, new DecorativeGeoBlockModel());
	}

	@Override
	protected Direction getBlockStateDirection(DecorativeGeoBlockEntity animatable) {
		Direction direction = super.getBlockStateDirection(animatable);

		if (animatable.getBlockState().getBlock() instanceof GeoDecorativeBlock block
				&& block.getGeoModelId().equals(GAIA_STATUE_MODEL_ID)) {
			return direction.getOpposite();
		}

		return direction;
	}

	@Override
	public void scaleModelForRender(RenderPassInfo<BlockEntityRenderState> renderPassInfo, float widthScale, float heightScale) {
		float renderWidthScale = DecorativeGeoModelData.getWidthScale(renderPassInfo);
		float renderHeightScale = DecorativeGeoModelData.getHeightScale(renderPassInfo);
		super.scaleModelForRender(renderPassInfo, widthScale * renderWidthScale, heightScale * renderHeightScale);
	}
}
