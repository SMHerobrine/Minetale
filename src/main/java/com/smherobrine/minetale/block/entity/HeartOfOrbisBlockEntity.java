package com.smherobrine.minetale.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;

public class HeartOfOrbisBlockEntity extends BlockEntity implements GeoBlockEntity {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public HeartOfOrbisBlockEntity(BlockPos pos, BlockState blockState) {
		super(MinetaleBlockEntityTypes.HEART_OF_ORBIS, pos, blockState);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
