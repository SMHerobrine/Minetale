package com.smherobrine.minetale.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class HeartOfOrbisMenu extends AbstractContainerMenu {
	public HeartOfOrbisMenu(int containerId, Inventory playerInventory) {
		super(MinetaleMenuTypes.HEART_OF_ORBIS, containerId);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}
}
