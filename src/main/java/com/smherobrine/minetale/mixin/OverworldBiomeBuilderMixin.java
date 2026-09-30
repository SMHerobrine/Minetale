package com.smherobrine.minetale.mixin;

import com.mojang.datafixers.util.Pair;
import com.smherobrine.minetale.world.biome.MinetaleBiomes;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OverworldBiomeBuilder.class)
abstract class OverworldBiomeBuilderMixin {
	@ModifyArg(
		method = "addUndergroundBiomes",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/biome/OverworldBiomeBuilder;addBottomBiome(Ljava/util/function/Consumer;Lnet/minecraft/world/level/biome/Climate$Parameter;Lnet/minecraft/world/level/biome/Climate$Parameter;Lnet/minecraft/world/level/biome/Climate$Parameter;Lnet/minecraft/world/level/biome/Climate$Parameter;Lnet/minecraft/world/level/biome/Climate$Parameter;FLnet/minecraft/resources/ResourceKey;)V"
		),
		index = 6
	)
	private float minetale$leaveRoomForVolcanicCaves(float originalOffset) {
		return 0.05F;
	}

	@Inject(method = "addBiomes", at = @At("TAIL"))
	private void minetale$addVolcanicCaves(
		Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> biomes,
		CallbackInfo callbackInfo
	) {
		Climate.Parameter fullRange = Climate.Parameter.span(-1.0F, 1.0F);
		biomes.accept(Pair.of(
			Climate.parameters(
				Climate.Parameter.span(0.15F, 1.0F),
				fullRange,
				fullRange,
				Climate.Parameter.span(-1.0F, -0.2F),
				Climate.Parameter.span(0.55F, 1.5F),
				fullRange,
				0.0F
			),
			MinetaleBiomes.VOLCANIC_CAVES
		));
	}
}
