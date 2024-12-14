package com.bafmc.customenchantment.player;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class CEPlayerExpansionRegister {
	public static final List<Class<? extends CEPlayerExpansion>> list = new ArrayList<Class<? extends CEPlayerExpansion>>();

	public static void setup(CEPlayer cePlayer) {
		for (Class<? extends CEPlayerExpansion> clazz : list) {
			try {
				Constructor<?> constructor = clazz.getConstructor(CEPlayer.class);
				CEPlayerExpansion expansion = (CEPlayerExpansion) constructor.newInstance(cePlayer);
				cePlayer.addExpansion(expansion);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void register(Class<? extends CEPlayerExpansion> clazz) {
		if (!list.contains(clazz)) {
			list.add(clazz);
		}
	}

	public static void unregister(Class<? extends CEPlayerExpansion> clazz) {
		list.remove(clazz);
	}
}
