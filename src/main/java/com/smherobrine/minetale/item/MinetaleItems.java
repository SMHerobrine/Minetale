package com.smherobrine.minetale.item;

import com.smherobrine.minetale.Minetale;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public final class MinetaleItems {
	private static final float LEAF_DROP_CHANCE = 0.5F;
	private static final float PLANT_DROP_CHANCE = 1.0F;
	private static final TagKey<Block> PLANT_FIBER_PLANTS = TagKey.create(Registries.BLOCK, id("plant_fiber_plants"));

	public static final Item PLANT_FIBERS = register("plant_fibers");

	private MinetaleItems() {
	}

	public static void initialize() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
			.register(entries -> entries.accept(PLANT_FIBERS));

		LootTableEvents.MODIFY_DROPS.register((lootTable, context, drops) -> {
			BlockState state = context.getOptionalParameter(LootContextParams.BLOCK_STATE);
			if (state == null) {
				return;
			}

			float dropChance;
			if (state.is(BlockTags.LEAVES)) {
				dropChance = LEAF_DROP_CHANCE;
			} else if (state.is(PLANT_FIBER_PLANTS)) {
				dropChance = PLANT_DROP_CHANCE;
			} else {
				return;
			}

			if (context.getRandom().nextFloat() < dropChance) {
				drops.add(new ItemStack(PLANT_FIBERS));
			}
		});
	}

	private static Item register(String name) {
		return Registry.register(BuiltInRegistries.ITEM, id(name), new Item(new Item.Properties().setId(itemKey(name))));
	}

	private static ResourceKey<Item> itemKey(String name) {
		return ResourceKey.create(Registries.ITEM, id(name));
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(Minetale.MOD_ID, path);
	}
}
