package com.smherobrine.minetale.block;

import com.smherobrine.minetale.Minetale;
import com.smherobrine.minetale.block.entity.MinetaleBlockEntityTypes;
import com.smherobrine.minetale.item.MinetaleItemGroups;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public final class MinetaleBlocks {
	public static final BlockSetType AMBER_BLOCK_SET_TYPE = BlockSetTypeBuilder.copyOf(BlockSetType.OAK)
		.register(id("amber"));
	public static final WoodType AMBER_WOOD_TYPE = WoodTypeBuilder.copyOf(WoodType.OAK)
		.register(id("amber"), AMBER_BLOCK_SET_TYPE);

	public static final Block AMBER_LOG = registerBlock("amber_log",
		new RotatedPillarBlock(copyProperties("amber_log", Blocks.OAK_LOG)));
	public static final Block AMBER_WOOD = registerBlock("amber_wood",
		new RotatedPillarBlock(copyProperties("amber_wood", Blocks.OAK_WOOD)));
	public static final Block STRIPPED_AMBER_LOG = registerBlock("stripped_amber_log",
		new RotatedPillarBlock(copyProperties("stripped_amber_log", Blocks.STRIPPED_OAK_LOG)));
	public static final Block STRIPPED_AMBER_WOOD = registerBlock("stripped_amber_wood",
		new RotatedPillarBlock(copyProperties("stripped_amber_wood", Blocks.STRIPPED_OAK_WOOD)));
	public static final Block AMBER_PLANKS = registerBlock("amber_planks",
		new Block(copyProperties("amber_planks", Blocks.OAK_PLANKS)));
	public static final Block AMBER_STAIRS = registerBlock("amber_stairs",
		new StairBlock(AMBER_PLANKS.defaultBlockState(), copyProperties("amber_stairs", Blocks.OAK_STAIRS)));
	public static final Block AMBER_SLAB = registerBlock("amber_slab",
		new SlabBlock(copyProperties("amber_slab", Blocks.OAK_SLAB)));
	public static final Block AMBER_FENCE = registerBlock("amber_fence",
		new FenceBlock(copyProperties("amber_fence", Blocks.OAK_FENCE)));
	public static final Block AMBER_FENCE_GATE = registerBlock("amber_fence_gate",
		new FenceGateBlock(AMBER_WOOD_TYPE, copyProperties("amber_fence_gate", Blocks.OAK_FENCE_GATE)));
	public static final Block AMBER_DOOR = registerBlock("amber_door",
		new DoorBlock(AMBER_BLOCK_SET_TYPE, copyProperties("amber_door", Blocks.OAK_DOOR)));
	public static final Block AMBER_TRAPDOOR = registerBlock("amber_trapdoor",
		new TrapDoorBlock(AMBER_BLOCK_SET_TYPE, copyProperties("amber_trapdoor", Blocks.OAK_TRAPDOOR)));
	public static final Block AMBER_BUTTON = registerBlock("amber_button",
		new ButtonBlock(AMBER_BLOCK_SET_TYPE, 30, copyProperties("amber_button", Blocks.OAK_BUTTON)));
	public static final Block AMBER_PRESSURE_PLATE = registerBlock("amber_pressure_plate",
		new PressurePlateBlock(AMBER_BLOCK_SET_TYPE, copyProperties("amber_pressure_plate", Blocks.OAK_PRESSURE_PLATE)));
	public static final Block AMBER_SIGN = registerBlockWithoutItem("amber_sign",
		new AmberStandingSignBlock(AMBER_WOOD_TYPE, copyProperties("amber_sign", Blocks.OAK_SIGN).noCollision().strength(1.0F)));
	public static final Block AMBER_WALL_SIGN = registerBlockWithoutItem("amber_wall_sign",
		new AmberWallSignBlock(AMBER_WOOD_TYPE, copyProperties("amber_wall_sign", Blocks.OAK_WALL_SIGN).noCollision().strength(1.0F)));
	public static final Block AMBER_HANGING_SIGN = registerBlockWithoutItem("amber_hanging_sign",
		new AmberCeilingHangingSignBlock(AMBER_WOOD_TYPE, copyProperties("amber_hanging_sign", Blocks.OAK_HANGING_SIGN).noCollision().strength(1.0F)));
	public static final Block AMBER_WALL_HANGING_SIGN = registerBlockWithoutItem("amber_wall_hanging_sign",
		new AmberWallHangingSignBlock(AMBER_WOOD_TYPE, copyProperties("amber_wall_hanging_sign", Blocks.OAK_WALL_HANGING_SIGN).noCollision().strength(1.0F)));

	public static final Item AMBER_SIGN_ITEM = registerItem("amber_sign",
		new SignItem(AMBER_SIGN, AMBER_WALL_SIGN, itemProperties("amber_sign").stacksTo(16)));
	public static final Item AMBER_HANGING_SIGN_ITEM = registerItem("amber_hanging_sign",
		new HangingSignItem(AMBER_HANGING_SIGN, AMBER_WALL_HANGING_SIGN, itemProperties("amber_hanging_sign").stacksTo(16)));

	private MinetaleBlocks() {
	}

	public static void initialize() {
		MinetaleBlockEntityTypes.initialize();
		MinetaleItemGroups.initialize();

		StrippableBlockRegistry.register(AMBER_LOG, STRIPPED_AMBER_LOG);
		StrippableBlockRegistry.register(AMBER_WOOD, STRIPPED_AMBER_WOOD);

		FlammableBlockRegistry flammables = FlammableBlockRegistry.getDefaultInstance();
		flammables.add(AMBER_LOG, 5, 5);
		flammables.add(AMBER_WOOD, 5, 5);
		flammables.add(STRIPPED_AMBER_LOG, 5, 5);
		flammables.add(STRIPPED_AMBER_WOOD, 5, 5);
		flammables.add(AMBER_PLANKS, 5, 20);
		flammables.add(AMBER_STAIRS, 5, 20);
		flammables.add(AMBER_SLAB, 5, 20);
		flammables.add(AMBER_FENCE, 5, 20);
		flammables.add(AMBER_FENCE_GATE, 5, 20);
		flammables.add(AMBER_DOOR, 5, 20);
		flammables.add(AMBER_TRAPDOOR, 5, 20);
		flammables.add(AMBER_BUTTON, 5, 20);
		flammables.add(AMBER_PRESSURE_PLATE, 5, 20);
		flammables.add(AMBER_SIGN, 5, 20);
		flammables.add(AMBER_WALL_SIGN, 5, 20);
		flammables.add(AMBER_HANGING_SIGN, 5, 20);
		flammables.add(AMBER_WALL_HANGING_SIGN, 5, 20);

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
			entries.accept(AMBER_LOG);
			entries.accept(AMBER_WOOD);
			entries.accept(STRIPPED_AMBER_LOG);
			entries.accept(STRIPPED_AMBER_WOOD);
			entries.accept(AMBER_PLANKS);
			entries.accept(AMBER_STAIRS);
			entries.accept(AMBER_SLAB);
			entries.accept(AMBER_FENCE);
			entries.accept(AMBER_FENCE_GATE);
			entries.accept(AMBER_DOOR);
			entries.accept(AMBER_TRAPDOOR);
		});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(entries -> {
			entries.accept(AMBER_BUTTON);
			entries.accept(AMBER_PRESSURE_PLATE);
		});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
			entries.accept(AMBER_SIGN_ITEM);
			entries.accept(AMBER_HANGING_SIGN_ITEM);
		});
	}

	private static Block registerBlock(String name, Block block) {
		Block registeredBlock = registerBlockWithoutItem(name, block);
		registerItem(name, new BlockItem(registeredBlock, itemProperties(name).useBlockDescriptionPrefix()));
		return registeredBlock;
	}

	private static Block registerBlockWithoutItem(String name, Block block) {
		return Registry.register(BuiltInRegistries.BLOCK, id(name), block);
	}

	private static Item registerItem(String name, Item item) {
		return Registry.register(BuiltInRegistries.ITEM, id(name), item);
	}

	private static BlockBehaviour.Properties copyProperties(String name, Block block) {
		return BlockBehaviour.Properties.ofLegacyCopy(block).setId(blockKey(name));
	}

	private static Item.Properties itemProperties(String name) {
		return new Item.Properties().setId(itemKey(name));
	}

	private static ResourceKey<Block> blockKey(String name) {
		return ResourceKey.create(Registries.BLOCK, id(name));
	}

	private static ResourceKey<Item> itemKey(String name) {
		return ResourceKey.create(Registries.ITEM, id(name));
	}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(Minetale.MOD_ID, path);
	}
}
