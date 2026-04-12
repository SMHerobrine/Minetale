package com.smherobrine.minetale.block;

import com.smherobrine.minetale.block.entity.ForgottenTempleGatewayBlockEntity;
import com.smherobrine.minetale.world.EdgeOfTheEchoDimension;
import com.smherobrine.minetale.world.EdgeOfTheEchoGatewayTracker;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ForgottenTempleGatewayBlock extends BaseEntityBlock {
	public static final MapCodec<ForgottenTempleGatewayBlock> CODEC = simpleCodec(ForgottenTempleGatewayBlock::new);
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
	private static final IntegerProperty PART_X = IntegerProperty.create("part_x", 0, 2);
	private static final IntegerProperty PART_Z = IntegerProperty.create("part_z", 0, 2);
	private static final VoxelShape PART_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
	public static final java.util.function.Supplier<IntegerProperty> PART_X_ACCESSOR = () -> PART_X;
	public static final java.util.function.Supplier<IntegerProperty> PART_Z_ACCESSOR = () -> PART_Z;

	public ForgottenTempleGatewayBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false).setValue(PART_X, 1).setValue(PART_Z, 1));
	}

	@Override
	public MapCodec<ForgottenTempleGatewayBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ACTIVE, PART_X, PART_Z);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return isCenter(state) ? RenderShape.MODEL : RenderShape.INVISIBLE;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ForgottenTempleGatewayBlockEntity(pos, state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos centerPos = context.getClickedPos();
		Level level = context.getLevel();

		for (int partX = 0; partX < 3; partX++) {
			for (int partZ = 0; partZ < 3; partZ++) {
				BlockPos partPos = centerPos.offset(partX - 1, 0, partZ - 1);
				if (!level.getBlockState(partPos).canBeReplaced(context)) {
					return null;
				}
			}
		}

		return defaultBlockState();
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		for (int partX = 0; partX < 3; partX++) {
			for (int partZ = 0; partZ < 3; partZ++) {
				BlockPos partPos = pos.offset(partX - 1, 0, partZ - 1);
				BlockState partState = state.setValue(PART_X, partX).setValue(PART_Z, partZ);
				if (!partPos.equals(pos)) {
					level.setBlock(partPos, partState, Block.UPDATE_ALL);
				}
			}
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide()) {
			setActive(level, getCenterPos(pos, state), !state.getValue(ACTIVE));
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		super.stepOn(level, pos, state, entity);
		if (level.isClientSide() || !state.getValue(ACTIVE) || !isCenter(state) || !(entity instanceof net.minecraft.server.level.ServerPlayer serverPlayer)) {
			return;
		}

		if (EdgeOfTheEchoGatewayTracker.isOnCooldown(serverPlayer)) {
			return;
		}

		boolean teleported = level.dimension() == EdgeOfTheEchoDimension.LEVEL_KEY
			? EdgeOfTheEchoDimension.returnFromGateway(serverPlayer)
			: EdgeOfTheEchoDimension.teleportFromGateway(serverPlayer, level.dimension(), pos);

		if (!teleported) {
			serverPlayer.sendSystemMessage(Component.literal("The Edge of the Echo could not be reached."));
		}
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		removeAllParts(level, pos, state, player);
		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return PART_SHAPE;
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return PART_SHAPE;
	}

	@Override
	protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
		return PART_SHAPE;
	}

	@Override
	protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return PART_SHAPE;
	}

	@Override
	protected VoxelShape getOcclusionShape(BlockState state) {
		return Shapes.empty();
	}

	private static void removeAllParts(Level level, BlockPos pos, BlockState state, Player player) {
		BlockPos centerPos = getCenterPos(pos, state);
		for (int partX = 0; partX < 3; partX++) {
			for (int partZ = 0; partZ < 3; partZ++) {
				BlockPos partPos = centerPos.offset(partX - 1, 0, partZ - 1);
				if (partPos.equals(pos)) {
					continue;
				}

				BlockState partState = level.getBlockState(partPos);
				if (partState.is(state.getBlock())) {
					level.destroyBlock(partPos, false, player);
				}
			}
		}
	}

	private static void setActive(Level level, BlockPos centerPos, boolean active) {
		for (int partX = 0; partX < 3; partX++) {
			for (int partZ = 0; partZ < 3; partZ++) {
				BlockPos partPos = centerPos.offset(partX - 1, 0, partZ - 1);
				BlockState partState = level.getBlockState(partPos);
				if (partState.is(MinetaleBlocks.FORGOTTEN_TEMPLE_GATEWAY)) {
					level.setBlock(partPos, partState.setValue(ACTIVE, active), Block.UPDATE_ALL);
				}
			}
		}
	}

	public static BlockPos getCenterPos(BlockPos pos, BlockState state) {
		return pos.offset(1 - state.getValue(PART_X), 0, 1 - state.getValue(PART_Z));
	}

	public static boolean isCenter(BlockState state) {
		return state.getValue(PART_X) == 1 && state.getValue(PART_Z) == 1;
	}
}
