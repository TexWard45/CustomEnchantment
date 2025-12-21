package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.customenchantment.api.ITrade;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class CEItem<T extends CEItemData> implements ITrade<ItemStack> {
	protected String type;
	protected CECraftItemStackNMS craftItemStack;
	protected T data;

	public CEItem(String type, ItemStack itemStack) {
		this.type = type;
		this.craftItemStack = new CECraftItemStackNMS(itemStack);
		this.importFrom(itemStack);
	}

	public ItemStack exportTo() {
		return exportTo(data);
	}

	public ItemStack exportTo(T data) {
		return null;
	}

	public String getType() {
		return type;
	}

	public void setCraftItemStack(CECraftItemStackNMS craftItemStack) {
		this.craftItemStack = craftItemStack;
	}

	public void updateDefaultItemStack(ItemStack itemStack) {
		setCraftItemStack(new CECraftItemStackNMS(itemStack));
	}

	public CECraftItemStackNMS getCraftItemStack() {
		return craftItemStack;
	}

	public ItemStack getCurrentItemStack() {
		return craftItemStack.getNewItemStack();
	}

	public ItemStack getCurrentItemStackWithPlaceholder() {
		return getItemStackWithPlaceholder(getCurrentItemStack(), getData());
	}

	public ItemStack getItemStackWithPlaceholder(ItemStack itemStack, T data) {
		return ItemStackUtils.setItemStack(itemStack, getPlaceholder(data));
	}

	public Map<String, String> getPlaceholder(T data) {
		return new HashMap<String, String>();
	}

	public ItemStack getDefaultItemStack() {
		return craftItemStack.getDefaultItemStack();
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public ApplyReason applyTo(CEItem<T> ceItem) {
		return ApplyReason.NOTHING;
	}
}
