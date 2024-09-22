package com.bafmc.customenchantment.player;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.bafmc.customenchantment.player.mining.AbstractSpecialMine;

public class PlayerSpecialMiningRegister {
	public static final List<Class<? extends AbstractSpecialMine>> list = new ArrayList<Class<? extends AbstractSpecialMine>>();

	public static void setup(PlayerSpecialMining playerSpecialMine) {
		List<AbstractSpecialMine> list = new ArrayList<AbstractSpecialMine>();

		for (Class<? extends AbstractSpecialMine> clazz : PlayerSpecialMiningRegister.list) {
			try {
				Constructor<?> constructor = clazz.getConstructor(PlayerSpecialMining.class);
				AbstractSpecialMine expansion = (AbstractSpecialMine) constructor.newInstance(playerSpecialMine);
				list.add(expansion);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Collections.sort(list, new Comparator<AbstractSpecialMine>() {
			public int compare(AbstractSpecialMine o1, AbstractSpecialMine o2) {
				int a = o1.getPriority();
				int b = o2.getPriority();
				return a < b ? -1 : a > b ? 1 : 0;
			}
		});

		for (AbstractSpecialMine expansion : list) {
			playerSpecialMine.addExpantion(expansion);
		}
	}

	public static void register(Class<? extends AbstractSpecialMine> clazz) {
		if (list.contains(clazz)) {
			return;
		}
		list.add(clazz);
	}

	public static void unregister(Class<? extends AbstractSpecialMine> clazz) {
		list.remove(clazz);
	}
}
