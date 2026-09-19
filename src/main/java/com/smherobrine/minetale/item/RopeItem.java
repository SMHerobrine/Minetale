package com.smherobrine.minetale.item;

import com.smherobrine.minetale.block.MinetaleBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RopeItem extends BlockItem {
	public RopeItem(Block block, Item.Properties properties) {
		super(block, properties);
	}

	protected boolean canPlaceAt(Level level, BlockPos pos) {
		BlockState above = level.getBlockState(pos.above());
		return above.is(MinetaleBlocks.ROPE)
			|| above.is(BlockTags.LEAVES)
			|| above.isFaceSturdy(level, pos, Direction.DOWN);
	}

	@Override
	public BlockPlaceContext updatePlacementContext(BlockPlaceContext context) {
		BlockPos blockPos = context.getClickedPos().relative(context.getClickedFace().getOpposite());
		Level level = context.getLevel();
		BlockState blockState = level.getBlockState(blockPos);

		if (!blockState.is(getBlock())) {
			return canPlaceAt(level, blockPos.relative(context.getClickedFace())) ? context : null;
		}

		Direction direction = Direction.DOWN;
		BlockPos.MutableBlockPos mutable = blockPos.mutable().move(direction);
		while (true) {
			if (!level.isClientSide() && !level.isInWorldBounds(mutable)) {
				Player player = context.getPlayer();
				if (player instanceof ServerPlayer serverPlayer && mutable.getY() > level.getMaxY()) {
					serverPlayer.sendSystemMessage(
						Component.translatable("argument.pos.outofbounds").withStyle(ChatFormatting.RED),
						true
					);
				}
				break;
			}

			blockState = level.getBlockState(mutable);
			if (!blockState.is(getBlock())) {
				if (blockState.canBeReplaced(context)) {
					return BlockPlaceContext.at(context, mutable, direction);
				}
				break;
			}

			mutable.move(direction);
		}

		return null;
	}

	@Override
	protected boolean mustSurvive() {
		return false;
	}
}
