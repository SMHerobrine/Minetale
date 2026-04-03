package com.smherobrine.minetale.block;

import com.smherobrine.minetale.block.entity.AmberSignBlockEntity;
import com.smherobrine.minetale.block.entity.MinetaleBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jspecify.annotations.Nullable;

public class AmberStandingSignBlock extends StandingSignBlock {
	public AmberStandingSignBlock(WoodType type, BlockBehaviour.Properties properties) {
		super(type, properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AmberSignBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, MinetaleBlockEntityTypes.AMBER_SIGN, SignBlockEntity::tick);
	}
}
