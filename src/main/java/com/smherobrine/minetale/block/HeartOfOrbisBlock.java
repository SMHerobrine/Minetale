package com.smherobrine.minetale.block;

import com.smherobrine.minetale.block.entity.HeartOfOrbisBlockEntity;
import com.smherobrine.minetale.menu.HeartOfOrbisMenu;
import com.smherobrine.minetale.orbis.OrbisNetworking;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HeartOfOrbisBlock extends BaseEntityBlock {
	public static final MapCodec<HeartOfOrbisBlock> CODEC = simpleCodec(HeartOfOrbisBlock::new);
	private static final int HEIGHT = 4;
	private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	private static final Component MENU_TITLE = Component.translatable("block.minetale.heart_of_orbis");
	private static final IntegerProperty PART = IntegerProperty.create("part", 0, HEIGHT - 1);

	public HeartOfOrbisBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(this.stateDefinition.any().setValue(PART, 0));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(PART);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		return new SimpleMenuProvider((id, inventory, player) -> new HeartOfOrbisMenu(id, inventory), MENU_TITLE);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		BlockPos basePos = getBasePos(pos, state);
		BlockState baseState = level.getBlockState(basePos);

		if (!level.isClientSide()) {
			player.openMenu(baseState.getMenuProvider(level, basePos));
			if (player instanceof ServerPlayer serverPlayer) {
				OrbisNetworking.sendMemorySync(serverPlayer);
			}
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos pos = context.getClickedPos();
		Level level = context.getLevel();

		for (int offset = 1; offset < HEIGHT; offset++) {
			if (!level.getBlockState(pos.above(offset)).canBeReplaced(context)) {
				return null;
			}
		}

		return defaultBlockState();
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		for (int offset = 1; offset < HEIGHT; offset++) {
			level.setBlock(pos.above(offset), state.setValue(PART, offset), Block.UPDATE_ALL);
		}
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		int part = state.getValue(PART);

		if (part == 0) {
			return true;
		}

		BlockState belowState = level.getBlockState(pos.below());

		return belowState.is(this) && belowState.getValue(PART) == part - 1;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(PART) == 0 ? new HeartOfOrbisBlockEntity(pos, state) : null;
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		BlockPos basePos = getBasePos(pos, state);

		for (int offset = 0; offset < HEIGHT; offset++) {
			BlockPos partPos = basePos.above(offset);
			if (partPos.equals(pos)) {
				continue;
			}

			BlockState partState = level.getBlockState(partPos);
			if (partState.is(this)) {
				level.destroyBlock(partPos, false, player);
			}
		}

		return super.playerWillDestroy(level, pos, state, player);
	}

	private static BlockPos getBasePos(BlockPos pos, BlockState state) {
		return pos.below(state.getValue(PART));
	}
}
