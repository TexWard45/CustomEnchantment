package com.bafmc.customenchantment.api;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import com.bafmc.bukkit.utils.EnumUtils;
import com.bafmc.bukkit.utils.StringUtils;

public class MaterialData {
	private Material material;
	private int data;

	public MaterialData(ItemStack itemStack) {
		this(itemStack.getType());
	}

	public MaterialData(Block block) {
		this(block.getType(), block.getBlockData());
	}

	public MaterialData(Material material) {
		this.material = material;
	}

	public MaterialData(Material material, BlockData blockData) {
		this.material = material;

		if (blockData instanceof Ageable) {
			this.data = ((Ageable) blockData).getAge();
		}
	}

	public MaterialData(Material material, int data) {
		this.material = material;
		this.data = data;
	}

	public Material getMaterial() {
		return material;
	}

	public int getData() {
		return data;
	}

	public boolean equals(Object obj) {
		if (obj instanceof MaterialData) {
			return material == ((MaterialData) obj).material && data == ((MaterialData) obj).data;
		}
		return super.equals(obj);
	}

	public static MaterialData getMaterialNMSByString(String line) {
		List<String> parameter = StringUtils.split(line, " ", 0);

		if (parameter.isEmpty()) {
			return null;
		}

		Material material = EnumUtils.valueOf(Material.class, parameter.get(0));

		if (material == null) {
			System.out.println("Cannot find Material." + parameter.get(0));
			return null;
		}

		if (parameter.size() == 1) {
			return new MaterialData(material);
		}

		try {
			return new MaterialData(material, Integer.valueOf(parameter.get(1)));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return "MaterialData [material=" + material + ", data=" + data + "]";
	}

}
