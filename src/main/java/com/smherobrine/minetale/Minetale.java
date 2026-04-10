package com.smherobrine.minetale;

import com.smherobrine.minetale.block.MinetaleBlocks;
import com.smherobrine.minetale.menu.MinetaleMenuTypes;
import com.smherobrine.minetale.orbis.MemoryUnlockTracker;
import com.smherobrine.minetale.orbis.MinetaleSoundEvents;
import com.smherobrine.minetale.orbis.OrbisMemoryRewards;
import com.smherobrine.minetale.orbis.OrbisNetworking;
import com.geckolib.GeckoLibConstants;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Minetale implements ModInitializer {
	public static final String MOD_ID = "minetale";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		GeckoLibConstants.init();
		MinetaleSoundEvents.initialize();
		OrbisMemoryRewards.initialize();
		OrbisNetworking.initialize();
		MemoryUnlockTracker.initialize();
		MinetaleMenuTypes.initialize();
		MinetaleBlocks.initialize();
	}
}
