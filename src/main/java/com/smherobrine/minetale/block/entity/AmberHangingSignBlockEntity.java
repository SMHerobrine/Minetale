package com.smherobrine.minetale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AmberHangingSignBlockEntity extends SignBlockEntity {
	public AmberHangingSignBlockEntity(BlockPos pos, BlockState state) {
		super(MinetaleBlockEntityTypes.AMBER_HANGING_SIGN, pos, state);
	}

	@Override
	public int getTextLineHeight() {
		return 9;
	}

	@Override
	public int getMaxTextLineWidth() {
		return 60;
	}

	@Override
	public SoundEvent getSignInteractionFailedSoundEvent() {
		return SoundEvents.WAXED_HANGING_SIGN_INTERACT_FAIL;
	}
}
