package com.smherobrine.minetale.mixin;

import com.mojang.datafixers.util.Pair;
import com.smherobrine.minetale.worldgen.MinetaleBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Mixin(targets = "net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList$Preset")
abstract class MultiNoiseBiomeSourceParameterListPresetMixin {
	@Inject(method = "generateOverworldBiomes", at = @At("RETURN"), cancellable = true)
	private static <T> void minetale$addLavaCaveBiome(Function<ResourceKey<Biome>, T> function,
		CallbackInfoReturnable<Climate.ParameterList<T>> cir) {
		List<Pair<Climate.ParameterPoint, T>> values = new ArrayList<>(cir.getReturnValue().values());
		values.add(Pair.of(
			Climate.parameters(
				Climate.Parameter.span(-1.0F, 1.0F),
				Climate.Parameter.span(-1.0F, 1.0F),
				Climate.Parameter.span(-1.0F, 1.0F),
				Climate.Parameter.span(-0.35F, 1.0F),
				Climate.Parameter.span(0.97F, 1.04F),
				Climate.Parameter.span(-1.0F, 1.0F),
				0.0F
			),
			function.apply(MinetaleBiomes.LAVA_CAVE)
		));
		cir.setReturnValue(new Climate.ParameterList<>(values));
	}
}
