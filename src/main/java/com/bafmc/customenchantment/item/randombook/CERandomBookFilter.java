package com.bafmc.customenchantment.item.randombook;

import com.bafmc.bukkit.utils.GaussianChance;
import com.bafmc.bukkit.utils.StdRandom;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEEnchant;
import com.bafmc.customenchantment.enchant.CEGroup;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class CERandomBookFilter {
	public class LevelList extends ArrayList<Integer> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void addLevel(int from, int to) {
			for (int i = from; i <= to; i++) {
				if (!contains(i)) {
					add(i);
				}
			}
		}

		public void removeLevel(int from, int to) {
			for (int i = from; i <= to; i++) {
				remove(Integer.valueOf(i));
			}
		}
	}

	private Random random = new Random();
	private LinkedHashMap<String, LevelList> map = new LinkedHashMap<String, LevelList>();
	private int totalChance;
	private int success = -1;
	private int destroy = -1;
    private double sigma = 0;
    private GaussianChance gaussianChance;

	public CEEnchantSimple getRandomEnchant() {
		int index = random.nextInt(totalChance);

		String[] keys = map.keySet().toArray(new String[map.size()]);

		int minIndex = 0;
		int maxIndex = -1;
		for (int i = 0; i < keys.length; i++) {
			minIndex = maxIndex + 1;
			maxIndex = minIndex - 1 + map.get(keys[i]).size();

			if (index >= minIndex && index <= maxIndex) {
				String name = keys[i];
				int level = 0;

                if (sigma > 0) {
                    double rate = StdRandom.gaussian(0.0, sigma);
                    rate = Math.abs(rate);
                    rate = Math.min(rate, 1.0);

                    level = map.get(keys[i]).get((int) ((Math.ceil(rate * 10) / 10) * (maxIndex - minIndex)));
                }else {
                    level = map.get(keys[i]).get(random.nextInt(maxIndex - minIndex + 1));
                }

				CEEnchant enchant = CEAPI.getCEEnchant(name);
				CEGroup group = enchant.getCEGroup();
				int success = this.success < 0 ? group.getSuccess().getValue() : this.success;
				int destroy = this.destroy < 0 ? group.getDestroy().getValue() : this.destroy;
				
				return new CEEnchantSimple(name, level, success, destroy);
			}
		}
		return null;
	}

	private int minLevel = 1;
	private int maxLevel = Integer.MAX_VALUE;

	private void addEnchant(CEEnchant enchant) {
		LevelList list = map.get(enchant.getName());

		if (list == null) {
			list = new LevelList();
			map.put(enchant.getName(), list);
		}

		if (enchant.getMaxLevel() < minLevel) {
			return;
		}

		int maxLevel = Math.min(enchant.getMaxLevel(), this.maxLevel);

		list.addLevel(minLevel, maxLevel);
	}

	private void removeEnchant(CEEnchant enchant) {
		if (!map.containsKey(enchant.getName())) {
			return;
		}

		LevelList list = map.get(enchant.getName());

		if (list == null) {
			list = new LevelList();
			map.put(enchant.getName(), list);
		}

		if (enchant.getMaxLevel() < minLevel) {
			return;
		}

		int maxLevel = Math.min(enchant.getMaxLevel(), this.maxLevel);

		list.removeLevel(minLevel, maxLevel);
	}

	private void updateTotalChance() {
		totalChance = 0;
		for (LevelList level : map.values()) {
			totalChance += level.size();
		}
	}

	public void parse(List<String> list) {
		for (String line : list) {
			try {
				parse(line);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void parse(String line) {
		String key = line.substring(0, line.indexOf("="));
		String value = line.substring(line.indexOf("=") + 1, line.length());

		if (key.equals("PUT_GROUP")) {
			CEGroup group = CEAPI.getCEGroup(value);
			if (group == null) {
				return;
			}
			for (CEEnchant enchant : group.getEnchantList()) {
				addEnchant(enchant);
			}
		} else if (key.equals("REMOVE_GROUP")) {
			CEGroup group = CEAPI.getCEGroup(value);
			if (group == null) {
				return;
			}
			for (CEEnchant enchant : group.getEnchantList()) {
				removeEnchant(enchant);
			}
		} else if (key.equals("ADD_ENCHANT")) {
			CEEnchant enchant = CEAPI.getCEEnchant(value);
			addEnchant(enchant);
		} else if (key.equals("REMOVE_ENCHANT")) {
			CEEnchant enchant = CEAPI.getCEEnchant(value);
			removeEnchant(enchant);
		} else if (key.equals("MIN_LEVEL")) {
			this.minLevel = Integer.valueOf(value);
		} else if (key.equals("MAX_LEVEL")) {
			this.maxLevel = Integer.valueOf(value);
		} else if (key.equals("SUCCESS")) {
			this.success = Integer.valueOf(value);
		} else if (key.equals("DESTROY")) {
			this.destroy = Integer.valueOf(value);
		} else if (key.equals("LEVEL_SIGMA")) {
            this.sigma = Double.valueOf(value);
        }

		updateTotalChance();
	}
}
