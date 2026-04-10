package com.smherobrine.minetale.client.orbis;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientMemoryState {
	private static final Set<String> UNLOCKED = ConcurrentHashMap.newKeySet();
	private static final Set<String> CLAIMED = ConcurrentHashMap.newKeySet();
	private static volatile boolean hydrated;

	private ClientMemoryState() {
	}

	public static void replace(Set<String> unlocked, Set<String> claimed) {
		UNLOCKED.clear();
		UNLOCKED.addAll(unlocked);
		CLAIMED.clear();
		CLAIMED.addAll(claimed);
		hydrated = true;
	}

	public static boolean isHydrated() {
		return hydrated;
	}

	public static boolean isUnlocked(String key) {
		return UNLOCKED.contains(key);
	}

	public static boolean isClaimed(String key) {
		return CLAIMED.contains(key);
	}

	public static int getUnlockedCount() {
		return UNLOCKED.size();
	}

	public static int getClaimedCount() {
		return CLAIMED.size();
	}
}
