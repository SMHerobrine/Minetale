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

	private MinetaleBlockEntityTypes() {
	}

	public static void initialize() {
	}
}
