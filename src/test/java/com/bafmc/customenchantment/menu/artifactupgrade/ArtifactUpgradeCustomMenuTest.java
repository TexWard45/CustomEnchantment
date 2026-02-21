package com.bafmc.customenchantment.menu.artifactupgrade;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.MenuData;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.artifact.CEArtifact;
import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeAddReason;
import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeConfirmReason;
import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ArtifactUpgradeCustomMenu
 *
 * Tests addItem flow, confirm logic, return operations,
 * chance calculations, and ExtraData array sync.
 */
@DisplayName("ArtifactUpgradeCustomMenu Tests")
class ArtifactUpgradeCustomMenuTest {

    private static ServerMock server;

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

    private record MenuTestContext(
            ArtifactUpgradeCustomMenu menu, Player player,
            ArtifactUpgradeExtraData extraData, ArtifactUpgradeSettings settings) {}

    private static MenuTestContext createMenuContext() {
        Player player = server.addPlayer();
        ArtifactUpgradeSettings settings = mock(ArtifactUpgradeSettings.class);
        when(settings.getMaxIngredientCount()).thenReturn(5);
        ArtifactUpgradeExtraData extraData = new ArtifactUpgradeExtraData(settings);
        ArtifactUpgradeCustomMenu menu = new ArtifactUpgradeCustomMenu();
        ArtifactUpgradeCustomMenu.setSettings(settings);

        MenuData menuData = new MenuData();
        menuData.setId("artifactupgrade-test");
        menuData.setType("artifact-upgrade");

        menu.setExtraData(extraData);
        menu.setOwner(player);
        menu.setMenuData(menuData);

        Inventory inventory = server.createInventory(player, 54, "Test Artifact Upgrade");
        menu.setInventory(inventory);

        return new MenuTestContext(menu, player, extraData, settings);
    }

    // ==================== Pure Unit Tests (no MockBukkit required) ====================

    @Nested
    @DisplayName("Constants Tests")
    class ConstantsTests {

        @Test
        @DisplayName("getType() returns 'artifact-upgrade'")
        void testGetType() {
            assertEquals("artifact-upgrade", new ArtifactUpgradeCustomMenu().getType());
        }

        @Test
        @DisplayName("MENU_NAME constant is 'artifact-upgrade'")
        void testMenuNameConstant() {
            assertEquals("artifact-upgrade", ArtifactUpgradeCustomMenu.MENU_NAME);
        }
    }

    @Nested
    @DisplayName("ExtraData Tests")
    class ExtraDataTests {

        private ArtifactUpgradeSettings settings;

        @BeforeEach
        void setUp() {
            settings = mock(ArtifactUpgradeSettings.class);
        }

        @Test
        @DisplayName("addIngredient keeps parallel arrays in sync")
        void addIngredientKeepsSync() {
            ArtifactUpgradeExtraData data = new ArtifactUpgradeExtraData(settings);
            CEItem item1 = mock(CEItem.class);
            CEItem item2 = mock(CEItem.class);
            data.addIngredient(item1, 10.0);
            data.addIngredient(item2, 20.0);

            assertEquals(2, data.getIngredientItems().size());
            assertEquals(2, data.getIngredientPoints().size());
            assertEquals(30.0, data.getTotalPoint(), 0.001);
            assertSame(item1, data.getIngredientItems().get(0));
            assertSame(item2, data.getIngredientItems().get(1));
            assertEquals(10.0, data.getIngredientPoints().get(0), 0.001);
            assertEquals(20.0, data.getIngredientPoints().get(1), 0.001);
        }

        @Test
        @DisplayName("removeIngredient keeps parallel arrays in sync")
        void removeIngredientKeepsSync() {
            ArtifactUpgradeExtraData data = new ArtifactUpgradeExtraData(settings);
            CEItem item1 = mock(CEItem.class);
            CEItem item2 = mock(CEItem.class);
            CEItem item3 = mock(CEItem.class);
            data.addIngredient(item1, 10.0);
            data.addIngredient(item2, 20.0);
            data.addIngredient(item3, 30.0);

            data.removeIngredient(1);

            assertEquals(2, data.getIngredientItems().size());
            assertEquals(2, data.getIngredientPoints().size());
            assertEquals(40.0, data.getTotalPoint(), 0.001);
            assertSame(item1, data.getIngredientItems().get(0));
            assertSame(item3, data.getIngredientItems().get(1));
        }

        @Test
        @DisplayName("clearIngredient resets all")
        void clearIngredientResetsAll() {
            ArtifactUpgradeExtraData data = new ArtifactUpgradeExtraData(settings);
            data.addIngredient(mock(CEItem.class), 10.0);
            data.addIngredient(mock(CEItem.class), 20.0);
            data.clearIngredient();
            assertTrue(data.getIngredientItems().isEmpty());
            assertTrue(data.getIngredientPoints().isEmpty());
            assertEquals(0.0, data.getTotalPoint(), 0.001);
        }

        @Test
        @DisplayName("clearSelectedArtifact nulls both artifact fields")
        void clearSelectedNullsBoth() {
            ArtifactUpgradeExtraData data = new ArtifactUpgradeExtraData(settings);
            data.setSelectedArtifact(mock(CEArtifact.class));
            data.setPreviewArtifact(mock(CEArtifact.class));
            data.clearSelectedArtifact();
            assertNull(data.getSelectedArtifact());
            assertNull(data.getPreviewArtifact());
        }

        @Test
        @DisplayName("canUpgrade returns false when no artifact")
        void canUpgradeFalseNoArtifact() {
            ArtifactUpgradeExtraData data = new ArtifactUpgradeExtraData(settings);
            data.addIngredient(mock(CEItem.class), 10.0);
            assertFalse(data.canUpgrade());
        }

        @Test
        @DisplayName("canUpgrade returns false when no points")
        void canUpgradeFalseNoPoints() {
            ArtifactUpgradeExtraData data = new ArtifactUpgradeExtraData(settings);
            data.setSelectedArtifact(mock(CEArtifact.class));
            assertFalse(data.canUpgrade());
        }

        @Test
        @DisplayName("canUpgrade returns true when artifact and points present")
        void canUpgradeTrueWhenBoth() {
            ArtifactUpgradeExtraData data = new ArtifactUpgradeExtraData(settings);
            data.setSelectedArtifact(mock(CEArtifact.class));
            data.addIngredient(mock(CEItem.class), 10.0);
            assertTrue(data.canUpgrade());
        }
    }

    @Nested
    @DisplayName("Chance Calculation Tests")
    class ChanceCalculationTests {

        private MenuTestContext ctx;

        @BeforeEach
        void setUp() {
            assumeTrue(server != null, "MockBukkit not available");
            ctx = createMenuContext();
        }

        @Test
        @DisplayName("getChance returns 0 when no preview artifact")
        void chanceZeroNoPreview() {
            when(ctx.settings.getArtifactUpgradeData(null)).thenReturn(null);
            assertEquals(0.0, ctx.menu.getChance(), 0.001);
        }

        @Test
        @DisplayName("getChance returns 0 when requiredPoint is 0")
        void chanceZeroWhenRequiredZero() {
            CEArtifact preview = mock(CEArtifact.class);
            ctx.extraData.setPreviewArtifact(preview);
            ArtifactUpgradeData upgradeData = mock(ArtifactUpgradeData.class);
            when(upgradeData.getRequiredPoint()).thenReturn(0.0);
            when(ctx.settings.getArtifactUpgradeData(preview)).thenReturn(upgradeData);
            assertEquals(0.0, ctx.menu.getChance(), 0.001);
        }

        @Test
        @DisplayName("getChance caps at 100")
        void chanceCapsAt100() {
            CEArtifact preview = mock(CEArtifact.class);
            ctx.extraData.setPreviewArtifact(preview);
            ArtifactUpgradeData upgradeData = mock(ArtifactUpgradeData.class);
            when(upgradeData.getRequiredPoint()).thenReturn(100.0);
            when(ctx.settings.getArtifactUpgradeData(preview)).thenReturn(upgradeData);
            ctx.extraData.addIngredient(mock(CEItem.class), 150.0);
            assertEquals(100.0, ctx.menu.getChance(), 0.001);
        }

        @Test
        @DisplayName("getChance calculates partial correctly")
        void chancePartial() {
            CEArtifact preview = mock(CEArtifact.class);
            ctx.extraData.setPreviewArtifact(preview);
            ArtifactUpgradeData upgradeData = mock(ArtifactUpgradeData.class);
            when(upgradeData.getRequiredPoint()).thenReturn(200.0);
            when(ctx.settings.getArtifactUpgradeData(preview)).thenReturn(upgradeData);
            ctx.extraData.addIngredient(mock(CEItem.class), 50.0);
            assertEquals(25.0, ctx.menu.getChance(), 0.001);
        }
    }

    // ==================== MockBukkit-dependent Tests ====================

    @Nested
    @DisplayName("Add Item Tests")
    class AddItemTests {

        private MenuTestContext ctx;

        @BeforeEach
        void setUp() {
            assumeTrue(server != null, "MockBukkit not available");
            ctx = createMenuContext();
        }

        @Test
        @DisplayName("addItem non-artifact (no selected) returns NOT_ARTIFACT")
        void addNonArtifact() {
            var result = ctx.menu.addItem(new ItemStack(Material.DIAMOND), mock(CEItem.class));
            assertEquals(ArtifactUpgradeAddReason.NOT_ARTIFACT, result);
            assertNull(ctx.extraData.getSelectedArtifact());
        }

        @Test
        @DisplayName("addItem ingredient with points > 0 returns ADD_INGREDIENT")
        void addIngredientWithPoints() {
            ctx.extraData.setSelectedArtifact(mock(CEArtifact.class));
            CEItem ingredient = mock(CEItem.class);
            when(ctx.settings.getRequiredPoint(ingredient)).thenReturn(10.0);
            var result = ctx.menu.addItem(new ItemStack(Material.EMERALD), ingredient);
            assertEquals(ArtifactUpgradeAddReason.ADD_INGREDIENT, result);
            assertEquals(1, ctx.extraData.getIngredientItems().size());
            assertEquals(10.0, ctx.extraData.getTotalPoint(), 0.001);
        }

        @Test
        @DisplayName("addItem ingredient exceeds max count returns MAX_INGREDIENT")
        void addIngredientExceedsMax() {
            ctx.extraData.setSelectedArtifact(mock(CEArtifact.class));
            when(ctx.settings.getMaxIngredientCount()).thenReturn(2);

            CEItem ing1 = mock(CEItem.class);
            CEItem ing2 = mock(CEItem.class);
            when(ctx.settings.getRequiredPoint(ing1)).thenReturn(5.0);
            when(ctx.settings.getRequiredPoint(ing2)).thenReturn(5.0);
            ctx.menu.addItem(new ItemStack(Material.EMERALD), ing1);
            ctx.menu.addItem(new ItemStack(Material.EMERALD), ing2);

            CEItem ing3 = mock(CEItem.class);
            when(ctx.settings.getRequiredPoint(ing3)).thenReturn(5.0);
            var result = ctx.menu.addItem(new ItemStack(Material.EMERALD), ing3);
            assertEquals(ArtifactUpgradeAddReason.MAX_INGREDIENT, result);
            assertEquals(2, ctx.extraData.getIngredientItems().size());
        }

        @Test
        @DisplayName("addItem ingredient with 0 points returns NOT_INGREDIENT")
        void addIngredientZeroPoints() {
            ctx.extraData.setSelectedArtifact(mock(CEArtifact.class));
            CEItem ceItem = mock(CEItem.class);
            when(ctx.settings.getRequiredPoint(ceItem)).thenReturn(0.0);
            var result = ctx.menu.addItem(new ItemStack(Material.STONE), ceItem);
            assertEquals(ArtifactUpgradeAddReason.NOT_INGREDIENT, result);
        }

        @Test
        @DisplayName("addItem ingredient with negative points returns NOT_INGREDIENT")
        void addIngredientNegativePoints() {
            ctx.extraData.setSelectedArtifact(mock(CEArtifact.class));
            CEItem ceItem = mock(CEItem.class);
            when(ctx.settings.getRequiredPoint(ceItem)).thenReturn(-5.0);
            var result = ctx.menu.addItem(new ItemStack(Material.STONE), ceItem);
            assertEquals(ArtifactUpgradeAddReason.NOT_INGREDIENT, result);
        }
    }

    @Nested
    @DisplayName("Confirm Upgrade Tests")
    class ConfirmUpgradeTests {

        private MenuTestContext ctx;

        @BeforeEach
        void setUp() {
            assumeTrue(server != null, "MockBukkit not available");
            ctx = createMenuContext();
        }

        @Test
        @DisplayName("confirmUpgrade with no artifact returns NO_SELECTED_ARTIFACT")
        void confirmNoArtifact() {
            assertEquals(ArtifactUpgradeConfirmReason.NO_SELECTED_ARTIFACT, ctx.menu.confirmUpgrade());
        }

        @Test
        @DisplayName("confirmUpgrade with no ingredients returns NO_INGREDIENT")
        void confirmNoIngredients() {
            ctx.extraData.setSelectedArtifact(mock(CEArtifact.class));
            assertEquals(ArtifactUpgradeConfirmReason.NO_INGREDIENT, ctx.menu.confirmUpgrade());
        }
    }

    @Nested
    @DisplayName("Return Tests")
    class ReturnTests {

        private MenuTestContext ctx;

        @BeforeEach
        void setUp() {
            assumeTrue(server != null, "MockBukkit not available");
            ctx = createMenuContext();
        }

        @Test
        @DisplayName("returnSelectedArtifact clears artifact and ingredients")
        void returnArtifactClearsAll() {
            CEArtifact artifact = mock(CEArtifact.class);
            when(artifact.getDefaultItemStack()).thenReturn(new ItemStack(Material.NETHER_STAR));
            ctx.extraData.setSelectedArtifact(artifact);
            CEItem ingredient = mock(CEItem.class);
            when(ingredient.getDefaultItemStack()).thenReturn(new ItemStack(Material.EMERALD));
            ctx.extraData.addIngredient(ingredient, 10.0);

            ctx.menu.returnSelectedArtifact();

            assertNull(ctx.extraData.getSelectedArtifact());
            assertTrue(ctx.extraData.getIngredientItems().isEmpty());
            assertEquals(0.0, ctx.extraData.getTotalPoint(), 0.001);
        }

        @Test
        @DisplayName("returnSelectedArtifact when null does not throw")
        void returnNullArtifactSafe() {
            assertDoesNotThrow(() -> ctx.menu.returnSelectedArtifact());
        }

        @Test
        @DisplayName("returnIngredientAt valid index removes and updates points")
        void returnIngredientAtValid() {
            ctx.extraData.setSelectedArtifact(mock(CEArtifact.class));
            CEItem ing1 = mock(CEItem.class);
            CEItem ing2 = mock(CEItem.class);
            when(ing1.getDefaultItemStack()).thenReturn(new ItemStack(Material.EMERALD));
            when(ing2.getDefaultItemStack()).thenReturn(new ItemStack(Material.DIAMOND));
            ctx.extraData.addIngredient(ing1, 10.0);
            ctx.extraData.addIngredient(ing2, 20.0);

            ctx.menu.returnIngredientAt(0);

            assertEquals(1, ctx.extraData.getIngredientItems().size());
            assertEquals(20.0, ctx.extraData.getTotalPoint(), 0.001);
            assertSame(ing2, ctx.extraData.getIngredientItems().get(0));
        }

        @Test
        @DisplayName("returnIngredientAt negative index is no-op")
        void returnIngredientNegative() {
            ctx.extraData.setSelectedArtifact(mock(CEArtifact.class));
            ctx.extraData.addIngredient(mock(CEItem.class), 10.0);
            ctx.menu.returnIngredientAt(-1);
            assertEquals(1, ctx.extraData.getIngredientItems().size());
        }

        @Test
        @DisplayName("returnIngredientAt too high index is no-op")
        void returnIngredientTooHigh() {
            ctx.extraData.setSelectedArtifact(mock(CEArtifact.class));
            ctx.extraData.addIngredient(mock(CEItem.class), 10.0);
            ctx.menu.returnIngredientAt(5);
            assertEquals(1, ctx.extraData.getIngredientItems().size());
        }
    }

    @Nested
    @DisplayName("Handle Close Tests")
    class HandleCloseTests {

        private MenuTestContext ctx;

        @BeforeEach
        void setUp() {
            assumeTrue(server != null, "MockBukkit not available");
            ctx = createMenuContext();
        }

        @Test
        @DisplayName("handleClose with no items does not throw")
        void handleCloseEmpty() {
            assertDoesNotThrow(() -> ctx.menu.handleClose());
        }

        @Test
        @DisplayName("handleClose with artifact and ingredients does not throw")
        void handleCloseWithItems() {
            CEArtifact artifact = mock(CEArtifact.class);
            when(artifact.getDefaultItemStack()).thenReturn(new ItemStack(Material.NETHER_STAR));
            ctx.extraData.setSelectedArtifact(artifact);
            CEItem ingredient = mock(CEItem.class);
            when(ingredient.getDefaultItemStack()).thenReturn(new ItemStack(Material.EMERALD));
            ctx.extraData.addIngredient(ingredient, 10.0);
            assertDoesNotThrow(() -> ctx.menu.handleClose());
        }
    }
}
