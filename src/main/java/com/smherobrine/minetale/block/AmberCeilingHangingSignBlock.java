package com.smherobrine.minetale.block;

import com.smherobrine.minetale.block.entity.AmberHangingSignBlockEntity;
import com.smherobrine.minetale.block.entity.MinetaleBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jspecify.annotations.Nullable;

public class AmberCeilingHangingSignBlock extends CeilingHangingSignBlock {
	public AmberCeilingHangingSignBlock(WoodType type, BlockBehaviour.Properties properties) {
		super(type, properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AmberHangingSignBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, MinetaleBlockEntityTypes.AMBER_HANGING_SIGN, SignBlockEntity::tick);
	}
}
