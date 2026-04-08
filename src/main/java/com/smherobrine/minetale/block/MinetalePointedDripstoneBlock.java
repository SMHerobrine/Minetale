package com.smherobrine.minetale.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.material.Fluids;

final class MinetalePointedDripstoneBlock extends PointedDripstoneBlock {
	MinetalePointedDripstoneBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction tipDirection = state.getValue(TIP_DIRECTION);
		return isValidPointedPlacement(level, pos, tipDirection);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		LevelReader level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Direction preferredDirection = context.getNearestLookingVerticalDirection().getOpposite();
		Direction tipDirection = calculateTipDirection(level, pos, preferredDirection);
		if (tipDirection == null) {
			return null;
		}

		boolean tryMerge = !context.isSecondaryUseActive();
		DripstoneThickness thickness = calculateThickness(level, pos, tipDirection, tryMerge);
		return defaultBlockState()
			.setValue(TIP_DIRECTION, tipDirection)
			.setValue(THICKNESS, thickness)
			.setValue(WATERLOGGED, context.getLevel().getFluidState(pos).is(Fluids.WATER));
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos,
		Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		if (state.getValue(WATERLOGGED)) {
			scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		if (direction != Direction.UP && direction != Direction.DOWN) {
			return state;
		}

		Direction tipDirection = state.getValue(TIP_DIRECTION);
		if (tipDirection == Direction.DOWN && scheduledTickAccess.getBlockTicks().hasScheduledTick(pos, this)) {
			return state;
		}

		if (direction == tipDirection.getOpposite() && !canSurvive(state, level, pos)) {
			scheduledTickAccess.scheduleTick(pos, this, tipDirection == Direction.DOWN ? 2 : 1);
			return state;
		}

		boolean merged = state.getValue(THICKNESS) == DripstoneThickness.TIP_MERGE;
		return state.setValue(THICKNESS, calculateThickness(level, pos, tipDirection, merged));
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (isStalagmite(state)) {
			if (!canSurvive(state, level, pos)) {
				level.destroyBlock(pos, true);
			}
			return;
		}

		spawnFallingStalactite(state, level, pos);
	}

	private static Direction calculateTipDirection(LevelReader level, BlockPos pos, Direction preferredDirection) {
		if (isValidPointedPlacement(level, pos, preferredDirection)) {
			return preferredDirection;
		}

		Direction opposite = preferredDirection.getOpposite();
		return isValidPointedPlacement(level, pos, opposite) ? opposite : null;
	}

	private static DripstoneThickness calculateThickness(LevelReader level, BlockPos pos, Direction tipDirection, boolean tryMerge) {
		Direction opposite = tipDirection.getOpposite();
		BlockState oppositeState = level.getBlockState(pos.relative(tipDirection));
		if (isPointedFamilyWithDirection(oppositeState, opposite)) {
			if (tryMerge || oppositeState.getValue(THICKNESS) == DripstoneThickness.TIP_MERGE) {
				return DripstoneThickness.TIP_MERGE;
			}

			return DripstoneThickness.TIP;
		}

		if (!isPointedFamilyWithDirection(oppositeState, tipDirection)) {
			return DripstoneThickness.TIP;
		}

		DripstoneThickness previousThickness = oppositeState.getValue(THICKNESS);
		if (previousThickness == DripstoneThickness.TIP || previousThickness == DripstoneThickness.TIP_MERGE) {
			return DripstoneThickness.FRUSTUM;
		}

		BlockState forwardState = level.getBlockState(pos.relative(opposite));
		return isPointedFamilyWithDirection(forwardState, tipDirection) ? DripstoneThickness.MIDDLE : DripstoneThickness.BASE;
	}

	private static boolean isValidPointedPlacement(LevelReader level, BlockPos pos, Direction tipDirection) {
		BlockPos supportPos = pos.relative(tipDirection.getOpposite());
		BlockState supportState = level.getBlockState(supportPos);
		return supportState.isFaceSturdy(level, supportPos, tipDirection)
			|| isPointedFamilyWithDirection(supportState, tipDirection);
	}

	private static boolean isPointedFamilyWithDirection(BlockState state, Direction direction) {
		return isPointedFamily(state) && state.getValue(TIP_DIRECTION) == direction;
	}

	private static boolean isPointedFamily(BlockState state) {
		Block block = state.getBlock();
		return block == Blocks.POINTED_DRIPSTONE || block instanceof MinetalePointedDripstoneBlock;
	}

	private static boolean isTip(BlockState state, boolean allowMergedTip) {
		if (!isPointedFamily(state)) {
			return false;
		}

		DripstoneThickness thickness = state.getValue(THICKNESS);
		return thickness == DripstoneThickness.TIP
			|| allowMergedTip && thickness == DripstoneThickness.TIP_MERGE;
	}

	private static boolean isStalactite(BlockState state) {
		return isPointedFamilyWithDirection(state, Direction.DOWN);
	}

	private static boolean isStalagmite(BlockState state) {
		return isPointedFamilyWithDirection(state, Direction.UP);
	}

	private static void spawnFallingStalactite(BlockState state, ServerLevel level, BlockPos pos) {
		BlockPos.MutableBlockPos cursor = pos.mutable();
		BlockState current = state;

		while (isStalactite(current)) {
			FallingBlockEntity falling = FallingBlockEntity.fall(level, cursor, current);
			if (isTip(current, true)) {
				int height = Math.max(1 + pos.getY() - cursor.getY(), 6);
				falling.setHurtsEntities(height, 40);
				return;
			}

			cursor.move(Direction.DOWN);
			current = level.getBlockState(cursor);
		}
	}
}
