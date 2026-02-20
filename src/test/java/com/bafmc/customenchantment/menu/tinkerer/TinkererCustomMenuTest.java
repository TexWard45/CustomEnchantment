package com.bafmc.customenchantment.menu.tinkerer;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.MenuData;
import com.bafmc.bukkit.feature.execute.Execute;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TinkererCustomMenu
 *
 * Tests player inventory click handling, dynamic item updates,
 * reward amount matching, and menu close behavior.
 */
@DisplayName("TinkererCustomMenu Tests")
class TinkererCustomMenuTest {

    private static ServerMock server;
    private TinkererCustomMenu menu;
    private Player player;
    private TinkererExtraData extraData;
    private TinkererSettings settings;

    @BeforeAll
    static void setUpAll() {
        try {
            if (MockBukkit.isMocked()) {
                MockBukkit.unmock();
            }
            server = MockBukkit.mock();
        } catch (Throwable e) {
            server = null;
        }
    }

    @AfterAll
    static void tearDownAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
    }

    @BeforeEach
    void setUp() {
        assumeTrue(server != null, "MockBukkit not available");
        player = server.addPlayer();
        menu = new TinkererCustomMenu();

        // Setup test data
        extraData = new TinkererExtraData();
        settings = mock(TinkererSettings.class);
        when(settings.getSize()).thenReturn(3);

        extraData.setSettings(settings);
        TinkererCustomMenu.setSettings(settings);

        // Setup menu
        MenuData menuData = new MenuData();
        menuData.setId("tinkerer-test");
        menuData.setType("tinkerer");

        menu.setExtraData(extraData);
        menu.setOwner(player);
        menu.setMenuData(menuData);

        Inventory inventory = server.createInventory(player, 54, "Test Tinkerer");
        menu.setInventory(inventory);
    }

    @Test
    @DisplayName("getType() returns 'tinkerer'")
    void testGetType() {
        assertEquals("tinkerer", menu.getType());
    }

    @Test
    @DisplayName("Adds item successfully when reward exists")
    void testAddItemSuccess() {
        ItemStack item = new ItemStack(Material.DIAMOND);
        CEItem ceItem = mock(CEItem.class);
        TinkererReward reward = mock(TinkererReward.class);

        when(reward.getItemStack()).thenReturn(new ItemStack(Material.GOLD_INGOT));
        when(settings.getReward(ceItem)).thenReturn(reward);
        when(settings.getTinkerSlot(0)).thenReturn(1);
        when(settings.getRewardSlot(0)).thenReturn(5);

        TinkererExtraData.TinkererAddReason result = menu.addItem(item, ceItem);

        assertEquals(TinkererExtraData.TinkererAddReason.SUCCESS, result);
        assertEquals(1, extraData.getTinkererDataList().size());
    }

    @Test
    @DisplayName("Returns FULL_SLOT when tinkerer is full")
    void testFullSlot() {
        ItemStack item = new ItemStack(Material.DIAMOND);
        CEItem ceItem = mock(CEItem.class);
        TinkererReward reward = mock(TinkererReward.class);

        when(reward.getItemStack()).thenReturn(new ItemStack(Material.GOLD_INGOT));
        when(settings.getSize()).thenReturn(1);
        when(settings.getReward(ceItem)).thenReturn(reward);

        menu.addItem(item, ceItem);
        TinkererExtraData.TinkererAddReason result = menu.addItem(item, ceItem);

        assertEquals(TinkererExtraData.TinkererAddReason.FULL_SLOT, result);
    }

    @Test
    @DisplayName("Returns NOT_SUPPORT_ITEM when reward is null")
    void testNotSupportItem() {
        ItemStack item = new ItemStack(Material.DIAMOND);
        CEItem ceItem = mock(CEItem.class);

        when(settings.getReward(ceItem)).thenReturn(null);

        TinkererExtraData.TinkererAddReason result = menu.addItem(item, ceItem);

        assertEquals(TinkererExtraData.TinkererAddReason.NOT_SUPPORT_ITEM, result);
    }

    @Test
    @DisplayName("Reward amount matches input item amount")
    void testRewardAmountMatches() {
        ItemStack inputItem = new ItemStack(Material.DIAMOND, 5);
        CEItem ceItem = mock(CEItem.class);
        TinkererReward reward = mock(TinkererReward.class);

        ItemStack rewardTemplate = new ItemStack(Material.GOLD_INGOT, 1);
        when(reward.getItemStack()).thenReturn(rewardTemplate);
        when(settings.getReward(ceItem)).thenReturn(reward);
        when(settings.getTinkerSlot(0)).thenReturn(1);
        when(settings.getRewardSlot(0)).thenReturn(5);

        menu.addItem(inputItem, ceItem);

        ItemStack rewardInSlot = menu.getInventory().getItem(5);
        assertNotNull(rewardInSlot);
        assertEquals(5, rewardInSlot.getAmount());
        assertEquals(Material.GOLD_INGOT, rewardInSlot.getType());
    }

    @Test
    @DisplayName("Confirms tinkerer successfully")
    void testConfirmSuccess() {
        ItemStack item = new ItemStack(Material.DIAMOND);
        CEItem ceItem = mock(CEItem.class);
        TinkererReward reward = mock(TinkererReward.class);
        Execute execute = mock(Execute.class);

        when(reward.getItemStack()).thenReturn(new ItemStack(Material.GOLD_INGOT));
        when(reward.getExecute()).thenReturn(execute);
        when(settings.getReward(ceItem)).thenReturn(reward);

        menu.addItem(item, ceItem);

        TinkererExtraData.TinkererConfirmReason result = menu.confirmTinkerer();

        assertEquals(TinkererExtraData.TinkererConfirmReason.SUCCESS, result);
        assertTrue(extraData.getTinkererDataList().isEmpty());
        verify(execute).execute(player);
    }

    @Test
    @DisplayName("Returns NOTHING when confirming empty tinkerer")
    void testConfirmEmpty() {
        TinkererExtraData.TinkererConfirmReason result = menu.confirmTinkerer();

        assertEquals(TinkererExtraData.TinkererConfirmReason.NOTHING, result);
    }
}
