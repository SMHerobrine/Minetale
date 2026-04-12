package com.smherobrine.minetale.world;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record EdgeOfTheEchoGatewayState(ResourceKey<Level> dimension, BlockPos centerPos) {
}
