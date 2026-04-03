package com.smherobrine.minetale.client.mixin;

import com.smherobrine.minetale.block.MinetaleBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
	@Shadow
	@Final
	protected Minecraft minecraft;

	@Inject(method = "openTextEdit", at = @At("HEAD"), cancellable = true)
	private void openAmberHangingSignEditor(SignBlockEntity sign, boolean isFrontText, CallbackInfo ci) {
		BlockState state = sign.getBlockState();
		if (state.is(MinetaleBlocks.AMBER_HANGING_SIGN) || state.is(MinetaleBlocks.AMBER_WALL_HANGING_SIGN)) {
			this.minecraft.setScreen(new HangingSignEditScreen(sign, isFrontText, this.minecraft.isTextFilteringEnabled()));
			ci.cancel();
		}
	}
}
