package com.smherobrine.minetale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class ForgottenTempleGatewayBlockEntity extends BlockEntity {
	public ForgottenTempleGatewayBlockEntity(BlockPos pos, BlockState blockState) {
		super(MinetaleBlockEntityTypes.FORGOTTEN_TEMPLE_GATEWAY, pos, blockState);
	}
}
