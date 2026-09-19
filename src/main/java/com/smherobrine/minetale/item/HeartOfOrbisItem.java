package com.smherobrine.minetale.item;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class HeartOfOrbisItem extends BlockItem implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private final Object renderProvider = createRenderProvider();

	public HeartOfOrbisItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	public Object getRenderProvider() {
		return this.renderProvider;
	}

	private static Object createRenderProvider() {
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
			return null;
		}

		try {
			return Class.forName("com.smherobrine.minetale.client.renderer.HeartOfOrbisItemRenderProvider")
				.getDeclaredConstructor()
				.newInstance();
		}
		catch (ReflectiveOperationException exception) {
			throw new IllegalStateException("Failed to create Heart of Orbis item renderer", exception);
		}
	}
}
