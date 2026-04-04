package com.smherobrine.minetale.mixin;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.smherobrine.minetale.worldgen.MinetaleBiomes;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiNoiseBiomeSource.class)
abstract class MultiNoiseBiomeSourceMixin {
	private static final int MAX_LAVA_CAVE_QUART_Y = 0;

	@Shadow
	@Final
	private Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters;

	@Inject(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;", at = @At("RETURN"), cancellable = true)
	private void minetale$limitLavaCaveHeight(int quartX, int quartY, int quartZ, Climate.Sampler sampler,
		CallbackInfoReturnable<Holder<Biome>> cir) {
		Holder<Biome> biome = cir.getReturnValue();
		if (quartY <= MAX_LAVA_CAVE_QUART_Y || !biome.is(MinetaleBiomes.LAVA_CAVE)) {
			return;
		}

		Climate.TargetPoint target = sampler.sample(quartX, quartY, quartZ);
		Holder<Biome> fallback = nearestNonLavaCaveBiome(this.minetale$parameters(), target);
		if (fallback != null) {
			cir.setReturnValue(fallback);
		}
	}

	private Climate.ParameterList<Holder<Biome>> minetale$parameters() {
		return this.parameters.map(direct -> direct, preset -> preset.value().parameters());
	}

	private static Holder<Biome> nearestNonLavaCaveBiome(Climate.ParameterList<Holder<Biome>> parameters, Climate.TargetPoint target) {
		Holder<Biome> bestBiome = null;
		long bestFitness = Long.MAX_VALUE;

		for (Pair<Climate.ParameterPoint, Holder<Biome>> entry : parameters.values()) {
			Holder<Biome> biome = entry.getSecond();
			if (biome.is(MinetaleBiomes.LAVA_CAVE)) {
				continue;
			}

			long fitness = fitness(entry.getFirst(), target);
			if (fitness < bestFitness) {
				bestFitness = fitness;
				bestBiome = biome;
			}
		}

		return bestBiome;
	}

	private static long fitness(Climate.ParameterPoint point, Climate.TargetPoint target) {
		return square(point.temperature().distance(target.temperature()))
			+ square(point.humidity().distance(target.humidity()))
			+ square(point.continentalness().distance(target.continentalness()))
			+ square(point.erosion().distance(target.erosion()))
			+ square(point.depth().distance(target.depth()))
			+ square(point.weirdness().distance(target.weirdness()))
			+ square(point.offset());
	}

	private static long square(long value) {
		return Mth.square(value);
	}
}
