package me.texward.customenchantment.menu;

import me.texward.customenchantment.CustomEnchantmentLog;
import me.texward.customenchantment.CustomEnchantmentMessage;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.item.ApplyReason;
import me.texward.customenchantment.item.CEItem;
import me.texward.customenchantment.menu.anvil.AnvilSlot1View;
import me.texward.customenchantment.menu.anvil.AnvilSlot2View;
import me.texward.customenchantment.menu.anvil.Slot2CEDefaultView;
import me.texward.custommenu.menu.CMenuView;
import me.texward.texwardlib.util.InventoryUtils;
import me.texward.texwardlib.util.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CEAnvilMenu extends MenuAbstract {
	public static final String MENU_NAME = "ce-anvil";
	private static HashMap<String, CEAnvilMenu> map = new HashMap<String, CEAnvilMenu>();
	private static HashMap<String, AnvilSlot1View> slot1ViewMap = new HashMap<String, AnvilSlot1View>();
	private static HashMap<String, AnvilSlot2View> slot2ViewMap = new HashMap<String, AnvilSlot2View>();

	public static void registerView1(String type, Class<? extends AnvilSlot1View> clazz) {
		try {
			Constructor<?> constructor = clazz.getConstructor(CEAnvilMenu.class);
			CEAnvilMenu menu = null;
			AnvilSlot1View view = (AnvilSlot1View) constructor.newInstance(menu);
			slot1ViewMap.put(type, view);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void registerView2(String type, Class<? extends AnvilSlot2View> clazz) {
		try {
			Constructor<?> constructor = clazz.getConstructor(CEAnvilMenu.class);
			CEAnvilMenu menu = null;
			AnvilSlot2View view = (AnvilSlot2View) constructor.newInstance(menu);
			slot2ViewMap.put(type, view);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static CEAnvilMenu putCEAnvilMenu(Player player, CMenuView cMenuView) {
		CEAnvilMenu menu = map.get(player.getName());

		if (menu == null) {
			menu = new CEAnvilMenu(cMenuView, player);
			map.put(player.getName(), menu);
		}

		return menu;
	}

	public static CEAnvilMenu getCEAnvilMenu(Player player) {
		return map.get(player.getName());
	}

	public static CEAnvilMenu removeCEAnvilMenu(Player player) {
		return map.remove(player.getName());
	}

	public enum CEAnvilAddReason {
		SUCCESS, ALREADY_HAS_SLOT1, ALREADY_HAS_SLOT2, NOT_SUITABLE;
	}

	public enum CEAnvilConfirmReason {
		SUCCESS, NOT_ENOUGH_MONEY, NOT_ENOUGH_BOOK;
	}

	public class ItemData {
		private ItemStack itemStack;
		private CEItem ceItem;

		public ItemData(ItemStack itemStack, CEItem ceItem) {
			this.itemStack = itemStack;
			this.ceItem = ceItem;
		}

		public ItemStack getItemStack() {
			return itemStack;
		}

		public void updateItemStack(ItemStack itemStack) {
			this.itemStack = itemStack;
			this.ceItem = CEAPI.getCEItem(itemStack);
		}

		public CEItem getCEItem() {
			return ceItem;
		}
	}

	private AnvilSlot1View view1;
	private AnvilSlot2View view2;
	private ItemData itemData1;
	private ItemData itemData2;

	public CEAnvilMenu(CMenuView menuView, Player player) {
		super(menuView, player);
	}

	public CEAnvilAddReason addItem(ItemStack itemStack, CEItem ceItem) {
		AnvilSlot1View findView1 = null;
		for (AnvilSlot1View view : slot1ViewMap.values()) {
			if (view.isSuitable(ceItem)) {
				findView1 = view.instance(this);
				break;
			}
		}

        AnvilSlot2View findView2 = new Slot2CEDefaultView(this);

		if (findView1 != null) {
			if (this.view1 == null) {
				this.view1 = findView1;
				this.itemData1 = new ItemData(itemStack, ceItem);

                if (this.view2 == null) {
                    this.view2 = findView2;
                }

                updateMenu();
				return CEAnvilAddReason.SUCCESS;
			} else {
				return CEAnvilAddReason.ALREADY_HAS_SLOT1;
			}
		}

        for (AnvilSlot2View view : slot2ViewMap.values()) {
            if (view.isSuitable(ceItem)) {
                findView2 = view.instance(this);
                break;
            }
        }

		if (findView2 != null) {
			if (this.view2 == null || this.itemData2 == null) {
                if (this.view2 != null) {
                    view2.updateAllPreviewNull();
                }

				this.view2 = findView2;
				this.itemData2 = new ItemData(itemStack, ceItem);
				updateMenu();
				return CEAnvilAddReason.SUCCESS;
			} else {
				return CEAnvilAddReason.ALREADY_HAS_SLOT2;
			}
		}

		return CEAnvilAddReason.NOT_SUITABLE;
	}

	public void updateMenu() {
		updateItemSlot1();
		updateItemSlot2();
		updatePreview();
	}

	public void updateItemSlot1() {
		ItemStack itemStack = null;
		if (itemData1 == null) {
			updateSlots("slot1", null);
			return;
		}
		
		itemStack = itemData1.getItemStack();
		if (itemStack == null || itemStack.getAmount() == 0 || itemStack.getType() == Material.AIR) {
			itemData1 = null;
			view1 = null;
		}
		
		updateSlots("slot1", itemData1 != null ? itemData1.getItemStack() : null);
	}

	public void updateItemSlot2() {
		ItemStack itemStack = null;
		if (itemData2 == null) {
			updateSlots("slot2", null);
			return;
		}
		
		itemStack = itemData2.getItemStack();
		if (itemStack == null || itemStack.getAmount() == 0 || itemStack.getType() == Material.AIR) {
			itemData2 = null;
			view2 = new Slot2CEDefaultView(this);
		}
		
		updateSlots("slot2", itemData2 != null ? itemData2.getItemStack() : null);
	}

	public void updatePreview() {
		if (itemData1 != null) {
            if (view2 != null) {
                view2.updateView();
            }
		} else {
			updateAllPreviewNull();
		}
		updateConfirm();
	}

	public void updateAllPreviewNull() {
		for (int i = 0; i < 5; i++) {
			updateSlots("preview" + (i + 1), null);
		}

		updateSlots("confirm", null);
	}

	public void updateConfirm() {
		if (itemData1 == null || itemData2 == null) {
			updateSlots("confirm", null);
			return;
		}

		view2.updateConfirm();
	}

	public void confirm() {
		if (itemData1 == null || itemData2 == null) {
			return;
		}

		CEItem ceItem1 = itemData1.getCEItem();
		CEItem ceItem2 = itemData2.getCEItem();
		
		ItemStack itemStack1 = itemData1.getItemStack();
		ItemStack itemStack2 = itemData2.getItemStack();
		
		int amount1 = itemData1.getItemStack().getAmount();
		int amount2 = itemData2.getItemStack().getAmount();

		ApplyReason reason = null;

		reason = view2.apply(ceItem1, ceItem2);

		boolean useItem2 = false;
		
		switch (reason.getResult()) {
		case FAIL:
			useItem2 = true;
			break;
		case FAIL_AND_UPDATE:
			if (amount1 > 1) {
				InventoryUtils.addItem(player, ItemStackUtils.getItemStack(ceItem1.exportTo(), 1));
				this.itemData1.updateItemStack(ItemStackUtils.getItemStack(itemStack1, amount1 - 1));
			}else {
				this.itemData1.updateItemStack(ceItem1.exportTo());
			}
			useItem2 = true;
			break;
		case DESTROY:
			if (amount1 > 1) {
				this.itemData1.updateItemStack(ItemStackUtils.getItemStack(itemStack1, amount1 - 1));
			}else {
				this.itemData1 = null;
			}
			useItem2 = true;
			break;
		case NOTHING:
			break;
		case CANCEL:
			break;
		case SUCCESS:
			if (amount1 > 1) {
				InventoryUtils.addItem(player, ItemStackUtils.getItemStack(ceItem1.exportTo(), 1));
				this.itemData1.updateItemStack(ItemStackUtils.getItemStack(itemStack1, amount1 - 1));
			}else {
				this.itemData1.updateItemStack(ceItem1.exportTo());
			}
			useItem2 = true;
			break;
		default:
			break;
		}
		
		if (useItem2) {
			this.itemData2.updateItemStack(ItemStackUtils.getItemStack(itemStack2, amount2 - 1));
		}

		if (reason.isWriteLogs()) {
			reason.setPlayer(player);
			reason.setCEItem1(ceItem2);
			reason.setCEItem2(ceItem1);
			CustomEnchantmentLog.writeItemActionLogs(reason);
		}

		if (reason.getRewards() != null) {
			InventoryUtils.addItem(player, reason.getRewards());
		}

		CustomEnchantmentMessage.send(player, "ce-item." + ceItem2.getType() + "." + reason.getReason().toLowerCase(),
				reason.getPlaceholder());

		updateMenu();
	}

	public void clickProcess(String name) {
		if (name.equals("slot1") || name.equals("slot2")) {
			returnItem(name);
		}

		if (name.equals("confirm")) {
			confirm();
		}

		if (view1 != null) {
			view1.clickProcess(name);
		}
		
		if (view2 != null) {
			view2.clickProcess(name);
		}
	}

	public void returnItem(String name) {
		ItemStack itemStack = null;
		if (name.equals("slot1") && itemData1 != null) {
			itemStack = itemData1.getItemStack();
            if (view2 != null) {
                view2.updateAllPreviewNull();
            }

			itemData1 = null;
			view1 = null;
		} else if (name.equals("slot2") && itemData2 != null) {
			itemStack = itemData2.getItemStack();
            if (view2 != null) {
                view2.updateAllPreviewNull();

                view2 = new Slot2CEDefaultView(this);

                updateMenu();
            }

            if (view1 == null) {
                view2 = null;
            }

			itemData2 = null;
		}
		if (itemStack == null) {
			return;
		}

		InventoryUtils.addItem(player, itemStack);
		updateMenu();
	}

	public void returnItems() {
		List<ItemStack> itemStacks = new ArrayList<ItemStack>();
		if (itemData1 != null) {
			itemStacks.add(itemData1.getItemStack());
		}
		if (itemData2 != null) {
			itemStacks.add(itemData2.getItemStack());
		}
		InventoryUtils.addItem(player, itemStacks);
	}

	public ItemData getItemData1() {
		return itemData1;
	}

	public void setItemData1(ItemData itemData1) {
		this.itemData1 = itemData1;
	}

	public ItemData getItemData2() {
		return itemData2;
	}

	public void setItemData2(ItemData itemData2) {
		this.itemData2 = itemData2;
	}

    public CMenuView getMenuView() {
        return menuView;
    }
}
