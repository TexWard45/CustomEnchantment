package me.texward.customenchantment.item;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ApplyReason {
	public static final ApplyReason NOTHING = new ApplyReason("nothing", ApplyResult.NOTHING);
	public static final ApplyReason CANCEL = new ApplyReason("cancel", ApplyResult.CANCEL);
	public static final ApplyReason SUCCESS = new ApplyReason("success", ApplyResult.SUCCESS);
	public static final ApplyReason FAIL = new ApplyReason("fail", ApplyResult.FAIL);
	public static final ApplyReason FAIL_AND_UPDATE = new ApplyReason("fail_and_update", ApplyResult.FAIL_AND_UPDATE);
	public static final ApplyReason DESTROY = new ApplyReason("destroy", ApplyResult.DESTROY);

	private String reason;
	private ApplyResult result;
	private Player player;
	private CEItem ceItem1;
	private CEItem ceItem2;
	private Map<String, String> placeholder;
	private Map<String, Object> data = new LinkedHashMap<String, Object>();
	private List<ItemStack> rewards;
	private CEItem source;
	private boolean writeLogs;

	public ApplyReason(String reason, ApplyResult result) {
		this.reason = reason;
		this.result = result;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public ApplyResult getResult() {
		return result;
	}

	public void setResult(ApplyResult result) {
		this.result = result;
	}

	public Map<String, String> getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(Map<String, String> placeholder) {
		this.placeholder = placeholder;
	}

	public Map<String, Object> getData() {
		return data;
	}
	
	public void putData(String key, Object value) {
		data.put(key, value);
	}

	public List<ItemStack> getRewards() {
		return rewards;
	}

	public void setRewards(List<ItemStack> itemStacks) {
		this.rewards = itemStacks;
	}

	public boolean isChangeSource() {
		return source != null;
	}

	public CEItem getSource() {
		return source;
	}

	public void setSource(CEItem source) {
		this.source = source;
	}

	public CEItem getCEItem1() {
		return ceItem1;
	}

	public void setCEItem1(CEItem ceItem1) {
		this.ceItem1 = ceItem1;
	}

	public CEItem getCEItem2() {
		return ceItem2;
	}

	public void setCEItem2(CEItem ceItem2) {
		this.ceItem2 = ceItem2;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean isWriteLogs() {
		return writeLogs;
	}

	public void setWriteLogs(boolean writeLogs) {
		this.writeLogs = writeLogs;
	}
}
