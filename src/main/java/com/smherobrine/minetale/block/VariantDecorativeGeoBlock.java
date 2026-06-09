package com.smherobrine.minetale.block;

import com.mojang.serialization.MapCodec;
import com.smherobrine.minetale.Minetale;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class VariantDecorativeGeoBlock extends DecorativeGeoBlock {
	public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 2);
	private static final Identifier FALLBACK_TEXTURE_ID = Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "textures/block/gaia_statue_marble.png");
	private static final Identifier[] FALLBACK_MODEL_IDS = new Identifier[] {
		Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "block/gaia_statue"),
		Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "block/gaia_statue"),
		Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "block/gaia_statue")
	};
	private static final MapCodec<VariantDecorativeGeoBlock> CODEC = simpleCodec(VariantDecorativeGeoBlock::new);

	private final Identifier[] modelIds;

	private VariantDecorativeGeoBlock(BlockBehaviour.Properties properties) {
		this(properties, FALLBACK_MODEL_IDS, FALLBACK_TEXTURE_ID, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D));
	}

	public VariantDecorativeGeoBlock(BlockBehaviour.Properties properties, Identifier[] modelIds, Identifier textureId, VoxelShape northShape) {
		this(properties, modelIds, textureId, northShape, 1.0F);
	}

	public VariantDecorativeGeoBlock(BlockBehaviour.Properties properties, Identifier[] modelIds, Identifier textureId, VoxelShape northShape,
			float renderScale) {
		this(properties, modelIds, textureId, northShape, renderScale, renderScale);
	}

	public VariantDecorativeGeoBlock(BlockBehaviour.Properties properties, Identifier[] modelIds, Identifier textureId, VoxelShape northShape,
			float renderWidthScale, float renderHeightScale) {
		super(properties, firstModelId(modelIds), textureId, northShape, renderWidthScale, renderHeightScale);
		this.modelIds = modelIds.clone();
		registerDefaultState(defaultBlockState().setValue(VARIANT, 0));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(VARIANT);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context)
			.setValue(VARIANT, context.getLevel().getRandom().nextInt(this.modelIds.length));
	}

	@Override
	public Identifier getGeoModelId(BlockState state) {
		return modelIdForVariant(state.getValue(VARIANT));
	}

	private Identifier modelIdForVariant(int variant) {
		if (variant < 0 || variant >= this.modelIds.length) {
			return this.modelIds[0];
		}

		return this.modelIds[variant];
	}

	private static Identifier firstModelId(Identifier[] modelIds) {
		if (modelIds.length == 0) {
			throw new IllegalArgumentException("Variant decorative geo blocks require at least one model");
		}

		return modelIds[0];
	}
}
