package com.bafmc.customenchantment.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.enchant.CESimple;
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;

public class CEIncreaseRateBook extends CEItem<CEIncreaseRateBookData> {

	public CEIncreaseRateBook(ItemStack itemStack) {
		super(CEItemType.INCREASE_RATE_BOOK, itemStack);
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);

		CEIncreaseRateBook item = (CEIncreaseRateBook) CustomEnchantment.instance().getCEItemStorageMap()
				.get(CEItemType.INCREASE_RATE_BOOK).get(pattern);

		CEIncreaseRateBookData data = new CEIncreaseRateBookData();
		if (item != null) {
			data = item.getData().clone();
		}

		data.setPattern(pattern);
		if (tag.hasKey(CENBT.SUCCESS)) {
			data.setSuccess((int) tag.getDouble(CENBT.SUCCESS));
		}
		if (tag.hasKey(CENBT.DESTROY)) {
			data.setDestroy((int) tag.getDouble(CENBT.DESTROY));
		}

		setData(data);
	}

	public ItemStack exportTo(CEIncreaseRateBookData data) {
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(getDefaultItemStack());
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.PATTERN, data.getPattern());
		tag.setDouble(CENBT.SUCCESS, (int) data.getSuccess());
		tag.setDouble(CENBT.DESTROY, (int) data.getDestroy());

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}

		return getItemStackWithPlaceholder(itemStackNMS.getNewItemStack(), data);
	}

	public Map<String, String> getPlaceholder(CEIncreaseRateBookData data) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("%success%", String.valueOf((int) data.getSuccess()));
		map.put("%destroy%", String.valueOf((int) data.getDestroy()));
		return map;
	}

	public ApplyReason applyTo(CEItem ceItem) {
		if (!(ceItem instanceof CEBook)) {
			return ApplyReason.NOTHING;
		}

		CEBook book = (CEBook) ceItem;
		CEBookData data = book.getData();
		CESimple ceSimple = data.getCESimple();
		int oldSuccess = ceSimple.getSuccess().getValue();
		int oldDestroy = ceSimple.getDestroy().getValue();

		if (!getData().getGroups().contains(ceSimple.getCEEnchant().getGroupName())) {
			return new ApplyReason("not-same-group", ApplyResult.CANCEL);
		}

		int success = getData().getSuccess();
		int destroy = getData().getDestroy();

		if (oldSuccess == 100 && oldDestroy == 0) {
			return new ApplyReason("perfect", ApplyResult.CANCEL);
		}
		
		if (oldSuccess == 100 && success > 0 && destroy == 0) {
			return new ApplyReason("enough-success", ApplyResult.CANCEL);
		}
		
		if (oldDestroy == 0 && destroy > 0 && success == 0) {
			return new ApplyReason("enough-destroy", ApplyResult.CANCEL);
		}
		
		int newSuccess = Math.min(oldSuccess + success, 100);
		int newDestroy = Math.max(oldDestroy - destroy, 0);

		data.setCESimple(new CESimple(ceSimple.getName(), ceSimple.getLevel(), newSuccess, newDestroy));
		
		ApplyReason reason = new ApplyReason("success", ApplyResult.SUCCESS);
		reason.setWriteLogs(true);
		reason.putData("pattern", this.data.getPattern());
		reason.putData("enchant", ceSimple.getName());
		reason.putData("level", ceSimple.getLevel());
		reason.putData("old-success", ceSimple.getSuccess().getValue());
		reason.putData("old-destroy", ceSimple.getDestroy().getValue());
		reason.putData("new-success", newSuccess);
		reason.putData("new-destroy", newDestroy);
		return reason;
	}

}
