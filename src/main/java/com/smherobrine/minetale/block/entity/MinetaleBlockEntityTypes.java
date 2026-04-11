package com.smherobrine.minetale.block.entity;

import com.smherobrine.minetale.Minetale;
import com.smherobrine.minetale.block.MinetaleBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class MinetaleBlockEntityTypes {
	public static final BlockEntityType<AmberSignBlockEntity> AMBER_SIGN = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "amber_sign"),
		FabricBlockEntityTypeBuilder.create(AmberSignBlockEntity::new, MinetaleBlocks.AMBER_SIGN, MinetaleBlocks.AMBER_WALL_SIGN).build()
	);

	public static final BlockEntityType<AmberHangingSignBlockEntity> AMBER_HANGING_SIGN = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "amber_hanging_sign"),
		FabricBlockEntityTypeBuilder.create(AmberHangingSignBlockEntity::new, MinetaleBlocks.AMBER_HANGING_SIGN, MinetaleBlocks.AMBER_WALL_HANGING_SIGN).build()
	);

	public static final BlockEntityType<HeartOfOrbisBlockEntity> HEART_OF_ORBIS = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "heart_of_orbis"),
		FabricBlockEntityTypeBuilder.create(HeartOfOrbisBlockEntity::new, MinetaleBlocks.HEART_OF_ORBIS).build()
	);

	public static final BlockEntityType<ForgottenTempleGatewayBlockEntity> FORGOTTEN_TEMPLE_GATEWAY = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "forgotten_temple_gateway"),
		FabricBlockEntityTypeBuilder.create(ForgottenTempleGatewayBlockEntity::new, MinetaleBlocks.FORGOTTEN_TEMPLE_GATEWAY).build()
	);

	private MinetaleBlockEntityTypes() {
	}

	public static void initialize() {
	}
}
