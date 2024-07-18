package me.texward.customenchantment.item;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.Parameter;
import me.texward.texwardlib.util.GaussianRandomRangeInt;
import me.texward.texwardlib.util.ItemStackUtils;

public class CEIncreaseRateBookStorage extends CEItemStorage<CEIncreaseRateBook> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEIncreaseRateBook getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		int success = new GaussianRandomRangeInt(parameter.getString(1, "100"), 0.5).getValue();
		int destroy = new GaussianRandomRangeInt(parameter.getString(2, "0"), 0.5).getValue();

		CEIncreaseRateBook increaseRateBook = (CEIncreaseRateBook) CustomEnchantment.instance().getCEItemStorageMap()
				.get(CEItemType.INCREASE_RATE_BOOK).get(name);

		CEIncreaseRateBook increaseRateBookClone = new CEIncreaseRateBook(increaseRateBook.getDefaultItemStack());
		
		CEIncreaseRateBookData data = increaseRateBook.getData().clone();
		data.setSuccess(success);
		data.setDestroy(destroy);
		increaseRateBookClone.setData(data);
		
		return increaseRateBookClone;
	}
	
	public List<ItemStack> getItemStacksByParameter(Parameter parameter) {
		CEIncreaseRateBook ceIncreaseRateBook = getByParameter(parameter);

		if (ceIncreaseRateBook == null) {
			return null;
		}

		int amount = 1;
		// 2: name and amount
		// 4: name, success, destroy and amount
		if (parameter.size() == 2 || parameter.size() >= 4) {
			amount = parameter.getInteger(parameter.size() - 1, 1);
		}

		amount = Math.max(amount, 1);

		ItemStack itemStack = ceIncreaseRateBook.exportTo();
		return ItemStackUtils.getItemStacks(itemStack, amount);
	}
	
}
