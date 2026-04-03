package com.smherobrine.minetale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AmberSignBlockEntity extends SignBlockEntity {
	public AmberSignBlockEntity(BlockPos pos, BlockState state) {
		super(MinetaleBlockEntityTypes.AMBER_SIGN, pos, state);
	}
}
