package com.smherobrine.minetale.orbis;

import com.smherobrine.minetale.Minetale;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

public final class MinetaleSoundEvents {
	public static final Identifier MEMORIES_UNLOCK_ID = Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "memories_unlock");
	public static final SoundEvent MEMORIES_UNLOCK = SoundEvent.createVariableRangeEvent(MEMORIES_UNLOCK_ID);
	public static final SoundEvent ROPE_BREAK = createSoundEvent("block.rope.break");
	public static final SoundEvent ROPE_STEP = createSoundEvent("block.rope.step");
	public static final SoundEvent ROPE_PLACE = createSoundEvent("block.rope.place");
	public static final SoundType ROPE = new SoundType(1.0F, 1.0F,
		ROPE_BREAK, ROPE_STEP, ROPE_PLACE, ROPE_STEP, ROPE_STEP);

	private MinetaleSoundEvents() {
	}

	public static void initialize() {
		Registry.register(BuiltInRegistries.SOUND_EVENT, MEMORIES_UNLOCK_ID, MEMORIES_UNLOCK);
		Registry.register(BuiltInRegistries.SOUND_EVENT, ROPE_BREAK.location(), ROPE_BREAK);
		Registry.register(BuiltInRegistries.SOUND_EVENT, ROPE_STEP.location(), ROPE_STEP);
		Registry.register(BuiltInRegistries.SOUND_EVENT, ROPE_PLACE.location(), ROPE_PLACE);
	}

	private static SoundEvent createSoundEvent(String name) {
		return SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(Minetale.MOD_ID, name));
	}
}
