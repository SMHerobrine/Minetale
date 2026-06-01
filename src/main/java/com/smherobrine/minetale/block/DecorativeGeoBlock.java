package com.smherobrine.minetale.block;

import com.mojang.serialization.MapCodec;
import com.smherobrine.minetale.Minetale;
import com.smherobrine.minetale.block.entity.DecorativeGeoBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DecorativeGeoBlock extends BaseEntityBlock implements GeoDecorativeBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	private static final MapCodec<DecorativeGeoBlock> CODEC = simpleCodec(DecorativeGeoBlock::new);
	private static final Identifier FALLBACK_MODEL_ID = Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "block/gaia_statue");
	private static final Identifier FALLBACK_TEXTURE_ID = Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "textures/block/gaia_statue_marble.png");

	private final Identifier modelId;
	private final Identifier textureId;
	private final float renderScale;
	private final VoxelShape northShape;
	private final VoxelShape eastShape;
	private final VoxelShape southShape;
	private final VoxelShape westShape;

	private DecorativeGeoBlock(BlockBehaviour.Properties properties) {
		this(properties, FALLBACK_MODEL_ID, FALLBACK_TEXTURE_ID, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D));
	}

	public DecorativeGeoBlock(BlockBehaviour.Properties properties, Identifier modelId, Identifier textureId, VoxelShape northShape) {
		this(properties, modelId, textureId, northShape, 1.0F);
	}

	public DecorativeGeoBlock(BlockBehaviour.Properties properties, Identifier modelId, Identifier textureId, VoxelShape northShape, float renderScale) {
		super(properties);
		this.modelId = modelId;
		this.textureId = textureId;
		this.renderScale = renderScale;
		this.northShape = northShape;
		this.eastShape = rotateShape(northShape, 1);
		this.southShape = rotateShape(northShape, 2);
		this.westShape = rotateShape(northShape, 3);
		registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	public Identifier getGeoModelId() {
		return this.modelId;
	}

	@Override
	public Identifier getGeoTextureId() {
		return this.textureId;
	}

	@Override
	public float getGeoRenderScale() {
		return this.renderScale;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DecorativeGeoBlockEntity(pos, state);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return getShapeForFacing(state);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return getShapeForFacing(state);
	}

	@Override
	protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return getShapeForFacing(state);
	}

	@Override
	protected VoxelShape getOcclusionShape(BlockState state) {
		return Shapes.empty();
	}

	private VoxelShape getShapeForFacing(BlockState state) {
		return switch (state.getValue(FACING)) {
			case EAST -> this.eastShape;
			case SOUTH -> this.southShape;
			case WEST -> this.westShape;
			default -> this.northShape;
		};
	}

	private static VoxelShape rotateShape(VoxelShape shape, int clockwiseTurns) {
		VoxelShape rotatedShape = shape;

		for (int turn = 0; turn < clockwiseTurns; turn++) {
			VoxelShape sourceShape = rotatedShape;
			VoxelShape[] buffer = new VoxelShape[] { Shapes.empty() };
			sourceShape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
				buffer[0] = Shapes.or(buffer[0], Shapes.box(1.0D - maxZ, minY, minX, 1.0D - minZ, maxY, maxX))
			);
			rotatedShape = buffer[0];
		}

		return rotatedShape;
	}
}
