package com.smherobrine.minetale.orbis;

import com.smherobrine.minetale.Minetale;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public final class MinetaleSoundEvents {
	public static final Identifier MEMORIES_UNLOCK_ID = Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "memories_unlock");
	public static final SoundEvent MEMORIES_UNLOCK = SoundEvent.createVariableRangeEvent(MEMORIES_UNLOCK_ID);

	private MinetaleSoundEvents() {
	}

	public static void initialize() {
		Registry.register(BuiltInRegistries.SOUND_EVENT, MEMORIES_UNLOCK_ID, MEMORIES_UNLOCK);
	}
}
