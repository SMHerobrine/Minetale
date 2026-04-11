package com.smherobrine.minetale.block;

import com.smherobrine.minetale.Minetale;
import com.smherobrine.minetale.block.entity.MinetaleBlockEntityTypes;
import com.smherobrine.minetale.item.HeartOfOrbisItem;
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
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
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

	public static final Block MARBLE = registerBlock("marble",
		new Block(copyProperties("marble", Blocks.STONE)));
	public static final Block MARBLE_STAIRS = registerBlock("marble_stairs",
		new StairBlock(MARBLE.defaultBlockState(), copyProperties("marble_stairs", Blocks.POLISHED_ANDESITE_STAIRS)));
	public static final Block MARBLE_SLAB = registerBlock("marble_slab",
		new SlabBlock(copyProperties("marble_slab", Blocks.STONE_SLAB)));
	public static final Block MARBLE_WALL = registerBlock("marble_wall",
		new WallBlock(copyProperties("marble_wall", Blocks.COBBLESTONE_WALL)));
	public static final Block COBBLED_MARBLE = registerBlock("cobbled_marble",
		new Block(copyProperties("cobbled_marble", Blocks.COBBLESTONE)));
	public static final Block COBBLED_MARBLE_STAIRS = registerBlock("cobbled_marble_stairs",
		new StairBlock(COBBLED_MARBLE.defaultBlockState(), copyProperties("cobbled_marble_stairs", Blocks.COBBLESTONE_STAIRS)));
	public static final Block COBBLED_MARBLE_SLAB = registerBlock("cobbled_marble_slab",
		new SlabBlock(copyProperties("cobbled_marble_slab", Blocks.COBBLESTONE_SLAB)));
	public static final Block COBBLED_MARBLE_WALL = registerBlock("cobbled_marble_wall",
		new WallBlock(copyProperties("cobbled_marble_wall", Blocks.COBBLESTONE_WALL)));
	public static final Block SMOOTH_MARBLE = registerBlock("smooth_marble",
		new Block(copyProperties("smooth_marble", Blocks.SMOOTH_STONE)));
	public static final Block SMOOTH_MARBLE_STAIRS = registerBlock("smooth_marble_stairs",
		new StairBlock(SMOOTH_MARBLE.defaultBlockState(), copyProperties("smooth_marble_stairs", Blocks.STONE_STAIRS)));
	public static final Block SMOOTH_MARBLE_SLAB = registerBlock("smooth_marble_slab",
		new SlabBlock(copyProperties("smooth_marble_slab", Blocks.SMOOTH_STONE_SLAB)));
	public static final Block SMOOTH_MARBLE_WALL = registerBlock("smooth_marble_wall",
		new WallBlock(copyProperties("smooth_marble_wall", Blocks.COBBLESTONE_WALL)));
	public static final Block CRACKED_SMOOTH_MARBLE = registerBlock("cracked_smooth_marble",
		new Block(copyProperties("cracked_smooth_marble", Blocks.SMOOTH_STONE)));
	public static final Block CRACKED_SMOOTH_MARBLE_STAIRS = registerBlock("cracked_smooth_marble_stairs",
		new StairBlock(CRACKED_SMOOTH_MARBLE.defaultBlockState(), copyProperties("cracked_smooth_marble_stairs", Blocks.STONE_STAIRS)));
	public static final Block CRACKED_SMOOTH_MARBLE_SLAB = registerBlock("cracked_smooth_marble_slab",
		new SlabBlock(copyProperties("cracked_smooth_marble_slab", Blocks.STONE_SLAB)));
	public static final Block CRACKED_SMOOTH_MARBLE_WALL = registerBlock("cracked_smooth_marble_wall",
		new WallBlock(copyProperties("cracked_smooth_marble_wall", Blocks.COBBLESTONE_WALL)));
	public static final Block MARBLE_BRICKS = registerBlock("marble_bricks",
		new Block(copyProperties("marble_bricks", Blocks.STONE_BRICKS)));
	public static final Block MARBLE_BRICKS_STAIRS = registerBlock("marble_bricks_stairs",
		new StairBlock(MARBLE_BRICKS.defaultBlockState(), copyProperties("marble_bricks_stairs", Blocks.STONE_BRICK_STAIRS)));
	public static final Block MARBLE_BRICKS_SLAB = registerBlock("marble_bricks_slab",
		new SlabBlock(copyProperties("marble_bricks_slab", Blocks.STONE_BRICK_SLAB)));
	public static final Block MARBLE_BRICKS_WALL = registerBlock("marble_bricks_wall",
		new WallBlock(copyProperties("marble_bricks_wall", Blocks.STONE_BRICK_WALL)));
	public static final Block CRACKED_MARBLE_BRICKS = registerBlock("cracked_marble_bricks",
		new Block(copyProperties("cracked_marble_bricks", Blocks.CRACKED_STONE_BRICKS)));
	public static final Block CHISELED_MARBLE = registerBlock("chiseled_marble",
		new Block(copyProperties("chiseled_marble", Blocks.CHISELED_QUARTZ_BLOCK)));
	public static final Block CHISELED_MARBLE_BRICKS = registerBlock("chiseled_marble_bricks",
		new Block(copyProperties("chiseled_marble_bricks", Blocks.CHISELED_STONE_BRICKS)));
	public static final Block CHALK = registerBlock("chalk",
		new Block(copyProperties("chalk", Blocks.CALCITE)));
	public static final Block HEART_OF_ORBIS = registerBlockWithoutItem("heart_of_orbis",
		new HeartOfOrbisBlock(copyProperties("heart_of_orbis", Blocks.ENCHANTING_TABLE).noOcclusion().dynamicShape()));
	public static final Item HEART_OF_ORBIS_ITEM = registerItem("heart_of_orbis",
		new HeartOfOrbisItem(HEART_OF_ORBIS, itemProperties("heart_of_orbis").useBlockDescriptionPrefix()));
	public static final Block STONE_WALL = registerBlock("stone_wall",
		new WallBlock(copyProperties("stone_wall", Blocks.COBBLESTONE_WALL)));
	public static final Block MOSSY_STONE = registerBlock("mossy_stone",
		new Block(copyProperties("mossy_stone", Blocks.MOSSY_COBBLESTONE)));
	public static final Block MOSSY_STONE_STAIRS = registerBlock("mossy_stone_stairs",
		new StairBlock(MOSSY_STONE.defaultBlockState(), copyProperties("mossy_stone_stairs", Blocks.MOSSY_COBBLESTONE_STAIRS)));
	public static final Block MOSSY_STONE_SLAB = registerBlock("mossy_stone_slab",
		new SlabBlock(copyProperties("mossy_stone_slab", Blocks.MOSSY_COBBLESTONE_SLAB)));
	public static final Block MOSSY_STONE_WALL = registerBlock("mossy_stone_wall",
		new WallBlock(copyProperties("mossy_stone_wall", Blocks.MOSSY_COBBLESTONE_WALL)));
	public static final Block VOLCANIC_ROCK = registerBlock("volcanic_rock",
		new Block(copyProperties("volcanic_rock", Blocks.DEEPSLATE)));
	public static final Block CRACKED_VOLCANIC_ROCK = registerBlock("cracked_volcanic_rock",
		new Block(copyProperties("cracked_volcanic_rock", Blocks.DEEPSLATE)));
	public static final Block VOLCANIC_COAL_ORE = registerBlock("volcanic_coal_ore",
		new DropExperienceBlock(UniformInt.of(0, 2), copyProperties("volcanic_coal_ore", Blocks.DEEPSLATE_COAL_ORE)));
	public static final Block VOLCANIC_COPPER_ORE = registerBlock("volcanic_copper_ore",
		new Block(copyProperties("volcanic_copper_ore", Blocks.DEEPSLATE_COPPER_ORE)));
	public static final Block VOLCANIC_IRON_ORE = registerBlock("volcanic_iron_ore",
		new Block(copyProperties("volcanic_iron_ore", Blocks.DEEPSLATE_IRON_ORE)));
	public static final Block VOLCANIC_GOLD_ORE = registerBlock("volcanic_gold_ore",
		new Block(copyProperties("volcanic_gold_ore", Blocks.DEEPSLATE_GOLD_ORE)));
	public static final Block VOLCANIC_REDSTONE_ORE = registerBlock("volcanic_redstone_ore",
		new RedStoneOreBlock(copyProperties("volcanic_redstone_ore", Blocks.DEEPSLATE_REDSTONE_ORE)));
	public static final Block VOLCANIC_EMERALD_ORE = registerBlock("volcanic_emerald_ore",
		new DropExperienceBlock(UniformInt.of(3, 7), copyProperties("volcanic_emerald_ore", Blocks.DEEPSLATE_EMERALD_ORE)));
	public static final Block VOLCANIC_LAPIS_ORE = registerBlock("volcanic_lapis_ore",
		new DropExperienceBlock(UniformInt.of(2, 5), copyProperties("volcanic_lapis_ore", Blocks.DEEPSLATE_LAPIS_ORE)));
	public static final Block VOLCANIC_DIAMOND_ORE = registerBlock("volcanic_diamond_ore",
		new DropExperienceBlock(UniformInt.of(3, 7), copyProperties("volcanic_diamond_ore", Blocks.DEEPSLATE_DIAMOND_ORE)));
	public static final Block POINTED_STONE = registerPointedStone("pointed_stone");
	public static final Block POINTED_GRANITE = registerPointedStone("pointed_granite");
	public static final Block POINTED_DIORITE = registerPointedStone("pointed_diorite");
	public static final Block POINTED_ANDESITE = registerPointedStone("pointed_andesite");
	public static final Block POINTED_DEEPSLATE = registerPointedStone("pointed_deepslate");
	public static final Block POINTED_TUFF = registerPointedStone("pointed_tuff");
	public static final Block POINTED_CALCITE = registerPointedStone("pointed_calcite");
	public static final Block POINTED_VOLCANIC_ROCK = registerPointedStone("pointed_volcanic_rock");

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
			entries.accept(MARBLE);
			entries.accept(MARBLE_STAIRS);
			entries.accept(MARBLE_SLAB);
			entries.accept(MARBLE_WALL);
			entries.accept(COBBLED_MARBLE);
			entries.accept(COBBLED_MARBLE_STAIRS);
			entries.accept(COBBLED_MARBLE_SLAB);
			entries.accept(COBBLED_MARBLE_WALL);
			entries.accept(SMOOTH_MARBLE);
			entries.accept(SMOOTH_MARBLE_STAIRS);
			entries.accept(SMOOTH_MARBLE_SLAB);
			entries.accept(SMOOTH_MARBLE_WALL);
			entries.accept(CRACKED_SMOOTH_MARBLE);
			entries.accept(CRACKED_SMOOTH_MARBLE_STAIRS);
			entries.accept(CRACKED_SMOOTH_MARBLE_SLAB);
			entries.accept(CRACKED_SMOOTH_MARBLE_WALL);
			entries.accept(MARBLE_BRICKS);
			entries.accept(MARBLE_BRICKS_STAIRS);
			entries.accept(MARBLE_BRICKS_SLAB);
			entries.accept(MARBLE_BRICKS_WALL);
			entries.accept(CRACKED_MARBLE_BRICKS);
			entries.accept(CHISELED_MARBLE);
			entries.accept(CHISELED_MARBLE_BRICKS);
			entries.accept(CHALK);
			entries.accept(STONE_WALL);
			entries.accept(MOSSY_STONE);
			entries.accept(MOSSY_STONE_STAIRS);
			entries.accept(MOSSY_STONE_SLAB);
			entries.accept(MOSSY_STONE_WALL);
			entries.accept(VOLCANIC_ROCK);
			entries.accept(CRACKED_VOLCANIC_ROCK);
		});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> {
			entries.accept(CHALK);
			entries.accept(MOSSY_STONE);
			entries.accept(MOSSY_STONE_STAIRS);
			entries.accept(MOSSY_STONE_SLAB);
			entries.accept(MOSSY_STONE_WALL);
			entries.accept(VOLCANIC_ROCK);
			entries.accept(CRACKED_VOLCANIC_ROCK);
			entries.accept(VOLCANIC_COAL_ORE);
			entries.accept(VOLCANIC_COPPER_ORE);
			entries.accept(VOLCANIC_IRON_ORE);
			entries.accept(VOLCANIC_GOLD_ORE);
			entries.accept(VOLCANIC_REDSTONE_ORE);
			entries.accept(VOLCANIC_EMERALD_ORE);
			entries.accept(VOLCANIC_LAPIS_ORE);
			entries.accept(VOLCANIC_DIAMOND_ORE);
			entries.accept(POINTED_STONE);
			entries.accept(POINTED_GRANITE);
			entries.accept(POINTED_DIORITE);
			entries.accept(POINTED_ANDESITE);
			entries.accept(POINTED_DEEPSLATE);
			entries.accept(POINTED_TUFF);
			entries.accept(POINTED_CALCITE);
			entries.accept(POINTED_VOLCANIC_ROCK);
		});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(entries -> {
			entries.accept(AMBER_BUTTON);
			entries.accept(AMBER_PRESSURE_PLATE);
			entries.accept(VOLCANIC_REDSTONE_ORE);
		});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
			entries.accept(AMBER_SIGN_ITEM);
			entries.accept(AMBER_HANGING_SIGN_ITEM);
			entries.accept(HEART_OF_ORBIS);
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

	private static Block registerPointedStone(String name) {
		return registerBlock(name, new MinetalePointedDripstoneBlock(copyProperties(name, Blocks.POINTED_DRIPSTONE)));
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
