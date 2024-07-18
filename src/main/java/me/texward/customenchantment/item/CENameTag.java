package me.texward.customenchantment.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.iridium.iridiumcolorapi.IridiumColorAPI;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.nms.CECraftItemStackNMS;
import me.texward.texwardlib.util.ColorUtils;
import me.texward.texwardlib.util.nms.NMSNBTTagCompound;
import net.md_5.bungee.api.ChatColor;

public class CENameTag extends CEItem<CENameTagData> {

	public CENameTag(ItemStack itemStack) {
		super(CEItemType.NAME_TAG, itemStack);
	}

	public void importFrom(ItemStack source) {
		CECraftItemStackNMS itemStackNMS = getCraftItemStack();
		NMSNBTTagCompound tag = itemStackNMS.getCECompound();

		String pattern = tag.getString(CENBT.PATTERN);

		CENameTag item = (CENameTag) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.NAME_TAG)
				.get(pattern);
		
		if (item != null) {
			setData(item.getData());
		}
	}
	
	public String getNewDisplay(String text) {
		List<Character> list = getData().getColorCharacterEnableList();
		
		text = IridiumColorAPI.process(text);
		text = ColorUtils.t(text);

		for (Character c : ChatColor.ALL_CODES.toCharArray()) {
			c = Character.toLowerCase(c);
			if (!list.contains(c)) {
				text = text.replace("§" + c, "#" + c);
			}
		}
		
		return text;
	}
	
	public ItemStack exportTo(CENameTagData data) {
		CECraftItemStackNMS itemStackNMS = new CECraftItemStackNMS(getDefaultItemStack());
		NMSNBTTagCompound tag = new NMSNBTTagCompound();

		tag.setString(CENBT.TYPE, getType());
		tag.setString(CENBT.PATTERN, data.getPattern());

		if (!tag.isEmpty()) {
			itemStackNMS.setCETag(tag);
		}
		
		return getItemStackWithPlaceholder(itemStackNMS.getNewItemStack(), data);
	}

	public Map<String, String> getPlaceholder(CENameTagData data) {
		Map<String, String> map = new HashMap<String, String>();
		return map;
	}

	public ApplyReason applyTo(CEItem ceItem) {
		return ApplyReason.NOTHING;
	}
}
