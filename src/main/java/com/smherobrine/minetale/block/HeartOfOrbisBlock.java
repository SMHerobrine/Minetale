package com.smherobrine.minetale.block;

import com.smherobrine.minetale.block.entity.HeartOfOrbisBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class HeartOfOrbisBlock extends BaseEntityBlock {
	public static final MapCodec<HeartOfOrbisBlock> CODEC = simpleCodec(HeartOfOrbisBlock::new);

	public HeartOfOrbisBlock(BlockBehaviour.Properties properties) {
		super(properties);
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
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new HeartOfOrbisBlockEntity(pos, state);
	}
}
