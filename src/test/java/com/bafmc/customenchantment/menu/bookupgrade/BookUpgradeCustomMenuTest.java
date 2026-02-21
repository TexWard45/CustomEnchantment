package com.bafmc.customenchantment.menu.bookupgrade;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.MenuData;
import com.bafmc.bukkit.utils.RandomRangeInt;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.menu.bookupgrade.data.BookUpgradeAddReason;
import com.bafmc.customenchantment.menu.bookupgrade.data.BookUpgradeConfirmReason;
import com.bafmc.customenchantment.menu.bookupgrade.data.BookUpgradeData;
import com.bafmc.customenchantment.menu.data.BookData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookUpgradeCustomMenu
 *
 * Tests addBook state machine, XP calculations, ingredient management,
 * return logic, and the reorderSlots utility.
 */
@DisplayName("BookUpgradeCustomMenu Tests")
class BookUpgradeCustomMenuTest {

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
            BookUpgradeCustomMenu menu, Player player,
            BookUpgradeExtraData extraData, BookUpgradeSettings settings) {}

    private static MenuTestContext createMenuContext() {
        Player player = server.addPlayer();
        BookUpgradeSettings settings = mock(BookUpgradeSettings.class);
        BookUpgradeExtraData extraData = new BookUpgradeExtraData(settings);
        BookUpgradeCustomMenu menu = new BookUpgradeCustomMenu();
        BookUpgradeCustomMenu.setSettings(settings);

        MenuData menuData = new MenuData();
        menuData.setId("bookupgrade-test");
        menuData.setType("book-upgrade");

        menu.setExtraData(extraData);
        menu.setOwner(player);
        menu.setMenuData(menuData);

        Inventory inventory = server.createInventory(player, 54, "Test Book Upgrade");
        menu.setInventory(inventory);

        return new MenuTestContext(menu, player, extraData, settings);
    }

    private static CEEnchantSimple createMockEnchant(String name, int level, int success, int destroy) {
        CEEnchantSimple enchant = mock(CEEnchantSimple.class);
        when(enchant.getName()).thenReturn(name);
        when(enchant.getLevel()).thenReturn(level);
        when(enchant.getSuccess()).thenReturn(new RandomRangeInt(success));
        when(enchant.getDestroy()).thenReturn(new RandomRangeInt(destroy));
        when(enchant.getXp()).thenReturn(0);
        return enchant;
    }

    // ==================== Pure Unit Tests (no MockBukkit required) ====================

    @Nested
    @DisplayName("Constants Tests")
    class ConstantsTests {

        @Test
        @DisplayName("getType() returns 'book-upgrade'")
        void testGetType() {
            assertEquals("book-upgrade", new BookUpgradeCustomMenu().getType());
        }

        @Test
        @DisplayName("MENU_NAME constant is 'book-upgrade'")
        void testMenuNameConstant() {
            assertEquals("book-upgrade", BookUpgradeCustomMenu.MENU_NAME);
        }
    }

    @Nested
    @DisplayName("Reorder Slots Tests")
    class ReorderSlotsTests {

        @Test
        @DisplayName("reorderSlots with odd number of slots centers from midpoint")
        void reorderOddSlots() {
            List<Integer> slots = Arrays.asList(10, 11, 12, 13, 14);
            List<Integer> result = BookUpgradeCustomMenu.reorderSlots(slots);
            assertEquals(12, result.get(0)); // center
            assertEquals(11, result.get(1)); // left 1
            assertEquals(13, result.get(2)); // right 1
            assertEquals(10, result.get(3)); // left 2
            assertEquals(14, result.get(4)); // right 2
        }

        @Test
        @DisplayName("reorderSlots with even number of slots")
        void reorderEvenSlots() {
            List<Integer> slots = Arrays.asList(10, 11, 12, 13);
            List<Integer> result = BookUpgradeCustomMenu.reorderSlots(slots);
            assertEquals(12, result.get(0)); // center
            assertEquals(11, result.get(1)); // left 1
            assertEquals(13, result.get(2)); // right 1
            assertEquals(10, result.get(3)); // left 2
        }

        @Test
        @DisplayName("reorderSlots with single slot returns same slot")
        void reorderSingleSlot() {
            List<Integer> result = BookUpgradeCustomMenu.reorderSlots(Arrays.asList(5));
            assertEquals(1, result.size());
            assertEquals(5, result.get(0));
        }

        @Test
        @DisplayName("reorderSlots with 3 slots")
        void reorderThreeSlots() {
            List<Integer> result = BookUpgradeCustomMenu.reorderSlots(Arrays.asList(0, 1, 2));
            assertEquals(1, result.get(0)); // center
            assertEquals(0, result.get(1)); // left
            assertEquals(2, result.get(2)); // right
        }
    }

    @Nested
    @DisplayName("ExtraData Tests")
    class ExtraDataTests {

        private BookUpgradeSettings settings;

        @BeforeEach
        void setUp() {
            settings = mock(BookUpgradeSettings.class);
        }

        @Test
        @DisplayName("initializes with no main book")
        void extraDataNoMainBook() {
            BookUpgradeExtraData data = new BookUpgradeExtraData(settings);
            assertFalse(data.hasMainBook());
            assertNull(data.getMainBook());
        }

        @Test
        @DisplayName("initializes with empty ingredients")
        void extraDataEmptyIngredients() {
            BookUpgradeExtraData data = new BookUpgradeExtraData(settings);
            assertFalse(data.hasBookIngredients());
            assertTrue(data.getBookIngredients().isEmpty());
        }

        @Test
        @DisplayName("clearMainBook nulls main book")
        void clearMainBookNulls() {
            BookUpgradeExtraData data = new BookUpgradeExtraData(settings);
            data.setMainBook(new BookData(mock(ItemStack.class), mock(CEEnchantSimple.class)));
            assertTrue(data.hasMainBook());
            data.clearMainBook();
            assertFalse(data.hasMainBook());
        }

        @Test
        @DisplayName("clearBookIngredients empties list")
        void clearBookIngredientsEmpties() {
            BookUpgradeExtraData data = new BookUpgradeExtraData(settings);
            data.getBookIngredients().add(new BookData(mock(ItemStack.class), mock(CEEnchantSimple.class)));
            assertTrue(data.hasBookIngredients());
            data.clearBookIngredients();
            assertFalse(data.hasBookIngredients());
        }

        @Test
        @DisplayName("resetXp resets to zero")
        void resetXpResetsToZero() {
            BookUpgradeExtraData data = new BookUpgradeExtraData(settings);
            data.setRandomXp(new RandomRangeInt(10, 20));
            data.resetXp();
            assertEquals(0, data.getRandomXp().getMin());
            assertEquals(0, data.getRandomXp().getMax());
        }

        @Test
        @DisplayName("readyToUpgrade defaults to false")
        void readyToUpgradeDefaultsFalse() {
            assertFalse(new BookUpgradeExtraData(settings).isReadyToUpgrade());
        }
    }

    // ==================== MockBukkit-dependent Tests ====================

    @Nested
    @DisplayName("Add Book Tests")
    class AddBookTests {

        private MenuTestContext ctx;

        @BeforeEach
        void setUp() {
            assumeTrue(server != null, "MockBukkit not available");
            ctx = createMenuContext();
        }

        @Test
        @DisplayName("addBook as main book returns SUCCESS and sets mainBook")
        void addMainBookReturnsSuccess() {
            CEEnchantSimple enchant = createMockEnchant("sharpness", 1, 100, 0);
            BookUpgradeData upgradeData = mock(BookUpgradeData.class);
            when(upgradeData.getRequiredXp()).thenReturn(100);
            when(ctx.settings.getBookUpgradeData("sharpness", 1)).thenReturn(upgradeData);

            var result = ctx.menu.addBook(new ItemStack(Material.ENCHANTED_BOOK), enchant);

            assertEquals(BookUpgradeAddReason.SUCCESS, result);
            assertTrue(ctx.extraData.hasMainBook());
            assertSame(enchant, ctx.extraData.getMainBook().getCESimple());
        }

        @Test
        @DisplayName("addBook when no BookUpgradeData returns NOT_UPGRADE_BOOK")
        void addBookNoUpgradeData() {
            CEEnchantSimple enchant = createMockEnchant("unknown", 1, 100, 0);
            when(ctx.settings.getBookUpgradeData("unknown", 1)).thenReturn(null);

            var result = ctx.menu.addBook(new ItemStack(Material.ENCHANTED_BOOK), enchant);
            assertEquals(BookUpgradeAddReason.NOT_UPGRADE_BOOK, result);
            assertFalse(ctx.extraData.hasMainBook());
        }

        @Test
        @DisplayName("addBook with imperfect success returns NOT_PERFECT_BOOK")
        void addBookImperfectSuccess() {
            CEEnchantSimple enchant = createMockEnchant("sharpness", 1, 90, 0);
            when(ctx.settings.getBookUpgradeData("sharpness", 1)).thenReturn(mock(BookUpgradeData.class));

            var result = ctx.menu.addBook(new ItemStack(Material.ENCHANTED_BOOK), enchant);
            assertEquals(BookUpgradeAddReason.NOT_PERFECT_BOOK, result);
        }

        @Test
        @DisplayName("addBook with non-zero destroy returns NOT_PERFECT_BOOK")
        void addBookNonZeroDestroy() {
            CEEnchantSimple enchant = createMockEnchant("sharpness", 1, 100, 10);
            when(ctx.settings.getBookUpgradeData("sharpness", 1)).thenReturn(mock(BookUpgradeData.class));

            var result = ctx.menu.addBook(new ItemStack(Material.ENCHANTED_BOOK), enchant);
            assertEquals(BookUpgradeAddReason.NOT_PERFECT_BOOK, result);
        }

        @Test
        @DisplayName("addBook as ingredient (same enchant) returns SUCCESS")
        void addIngredientSameEnchant() {
            CEEnchantSimple mainEnchant = createMockEnchant("sharpness", 1, 100, 0);
            BookUpgradeData upgradeData = mock(BookUpgradeData.class);
            when(upgradeData.getRequiredXp()).thenReturn(100);
            when(upgradeData.getXpEnchantWhitelist()).thenReturn(new ArrayList<>());
            when(ctx.settings.getBookUpgradeData("sharpness", 1)).thenReturn(upgradeData);
            ctx.menu.addBook(new ItemStack(Material.ENCHANTED_BOOK), mainEnchant);

            CEEnchantSimple ingredient = createMockEnchant("sharpness", 1, 100, 0);
            when(ctx.settings.getXp(ingredient)).thenReturn(new RandomRangeInt(5, 10));

            var result = ctx.menu.addBook(new ItemStack(Material.ENCHANTED_BOOK), ingredient);
            assertEquals(BookUpgradeAddReason.SUCCESS, result);
            assertEquals(1, ctx.extraData.getBookIngredients().size());
        }

        @Test
        @DisplayName("addBook as ingredient (different enchant, not whitelisted) returns DIFFERENT_ENCHANT")
        void addIngredientDifferentNotWhitelisted() {
            CEEnchantSimple mainEnchant = createMockEnchant("sharpness", 1, 100, 0);
            BookUpgradeData upgradeData = mock(BookUpgradeData.class);
            when(upgradeData.getRequiredXp()).thenReturn(100);
            when(upgradeData.getXpEnchantWhitelist()).thenReturn(new ArrayList<>());
            when(ctx.settings.getBookUpgradeData("sharpness", 1)).thenReturn(upgradeData);
            ctx.menu.addBook(new ItemStack(Material.ENCHANTED_BOOK), mainEnchant);

            CEEnchantSimple ingredient = createMockEnchant("protection", 1, 100, 0);
            when(ctx.settings.getXp(ingredient)).thenReturn(new RandomRangeInt(5, 10));

            var result = ctx.menu.addBook(new ItemStack(Material.ENCHANTED_BOOK), ingredient);
            assertEquals(BookUpgradeAddReason.DIFFERENT_ENCHANT, result);
        }

        @Test
        @DisplayName("addBook as ingredient (whitelisted different enchant) returns SUCCESS")
        void addIngredientWhitelisted() {
            CEEnchantSimple mainEnchant = createMockEnchant("sharpness", 1, 100, 0);
            BookUpgradeData upgradeData = mock(BookUpgradeData.class);
            when(upgradeData.getRequiredXp()).thenReturn(100);
            when(upgradeData.getXpEnchantWhitelist()).thenReturn(Arrays.asList("protection"));
            when(ctx.settings.getBookUpgradeData("sharpness", 1)).thenReturn(upgradeData);
            ctx.menu.addBook(new ItemStack(Material.ENCHANTED_BOOK), mainEnchant);

            CEEnchantSimple ingredient = createMockEnchant("protection", 1, 100, 0);
            when(ctx.settings.getXp(ingredient)).thenReturn(new RandomRangeInt(5, 10));

            var result = ctx.menu.addBook(new ItemStack(Material.ENCHANTED_BOOK), ingredient);
            assertEquals(BookUpgradeAddReason.SUCCESS, result);
        }

        @Test
        @DisplayName("addBook as ingredient with no XP returns NOT_XP_BOOK")
        void addIngredientNoXp() {
            CEEnchantSimple mainEnchant = createMockEnchant("sharpness", 1, 100, 0);
            BookUpgradeData upgradeData = mock(BookUpgradeData.class);
            when(upgradeData.getRequiredXp()).thenReturn(100);
            when(ctx.settings.getBookUpgradeData("sharpness", 1)).thenReturn(upgradeData);
            ctx.menu.addBook(new ItemStack(Material.ENCHANTED_BOOK), mainEnchant);

            CEEnchantSimple ingredient = createMockEnchant("sharpness", 1, 100, 0);
            when(ctx.settings.getXp(ingredient)).thenReturn(null);

            var result = ctx.menu.addBook(new ItemStack(Material.ENCHANTED_BOOK), ingredient);
            assertEquals(BookUpgradeAddReason.NOT_XP_BOOK, result);
        }

        @Test
        @DisplayName("addBook when readyToUpgrade returns NOTHING")
        void addBookWhenReady() {
            CEEnchantSimple mainEnchant = createMockEnchant("sharpness", 1, 100, 0);
            BookUpgradeData upgradeData = mock(BookUpgradeData.class);
            when(upgradeData.getRequiredXp()).thenReturn(100);
            when(ctx.settings.getBookUpgradeData("sharpness", 1)).thenReturn(upgradeData);
            ctx.menu.addBook(new ItemStack(Material.ENCHANTED_BOOK), mainEnchant);
            ctx.extraData.setReadyToUpgrade(true);

            var result = ctx.menu.addBook(new ItemStack(Material.ENCHANTED_BOOK), createMockEnchant("sharpness", 1, 100, 0));
            assertEquals(BookUpgradeAddReason.NOTHING, result);
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
        @DisplayName("confirmUpgrade with no main book returns NOTHING")
        void confirmNoMainBook() {
            assertEquals(BookUpgradeConfirmReason.NOTHING, ctx.menu.confirmUpgrade());
        }

        @Test
        @DisplayName("confirmUpgrade with main book but no ingredients returns NOTHING")
        void confirmNoIngredients() {
            CEEnchantSimple enchant = mock(CEEnchantSimple.class);
            when(enchant.getName()).thenReturn("sharpness");
            when(enchant.getLevel()).thenReturn(1);
            ctx.extraData.setMainBook(new BookData(new ItemStack(Material.ENCHANTED_BOOK), enchant));
            assertEquals(BookUpgradeConfirmReason.NOTHING, ctx.menu.confirmUpgrade());
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
        @DisplayName("returnIngredient at invalid index is no-op")
        void returnIngredientInvalidIndex() {
            assertDoesNotThrow(() -> ctx.menu.returnIngredient(-1));
            assertDoesNotThrow(() -> ctx.menu.returnIngredient(0));
            assertDoesNotThrow(() -> ctx.menu.returnIngredient(5));
        }

        @Test
        @DisplayName("returnMainBook clears main book and ingredients when not consolidated")
        void returnMainBookClearsWhenNotConsolidated() {
            CEEnchantSimple enchant = mock(CEEnchantSimple.class);
            when(enchant.getName()).thenReturn("sharpness");
            when(enchant.getLevel()).thenReturn(1);
            ctx.extraData.setMainBook(new BookData(new ItemStack(Material.ENCHANTED_BOOK), enchant));
            ctx.extraData.getBookIngredients().add(new BookData(new ItemStack(Material.ENCHANTED_BOOK), enchant));

            ctx.menu.returnMainBook();

            assertFalse(ctx.extraData.hasMainBook());
            assertFalse(ctx.extraData.hasBookIngredients());
        }

        @Test
        @DisplayName("returnMainBook does NOT return ingredients when readyToUpgrade")
        void returnMainBookKeepsIngredientsWhenReady() {
            CEEnchantSimple enchant = mock(CEEnchantSimple.class);
            when(enchant.getName()).thenReturn("sharpness");
            when(enchant.getLevel()).thenReturn(1);
            ctx.extraData.setMainBook(new BookData(new ItemStack(Material.ENCHANTED_BOOK), enchant));
            ctx.extraData.getBookIngredients().add(new BookData(new ItemStack(Material.ENCHANTED_BOOK), enchant));
            ctx.extraData.setReadyToUpgrade(true);

            ctx.menu.returnMainBook();

            assertFalse(ctx.extraData.hasMainBook());
            assertTrue(ctx.extraData.hasBookIngredients());
        }

        @Test
        @DisplayName("returnMainBook resets readyToUpgrade flag")
        void returnMainBookResetsFlag() {
            CEEnchantSimple enchant = mock(CEEnchantSimple.class);
            when(enchant.getName()).thenReturn("sharpness");
            when(enchant.getLevel()).thenReturn(1);
            ctx.extraData.setMainBook(new BookData(new ItemStack(Material.ENCHANTED_BOOK), enchant));
            ctx.extraData.setReadyToUpgrade(true);

            ctx.menu.returnMainBook();
            assertFalse(ctx.extraData.isReadyToUpgrade());
        }

        @Test
        @DisplayName("returnIngredient at valid index removes and recalculates XP")
        void returnIngredientRecalculates() {
            CEEnchantSimple mainEnchant = mock(CEEnchantSimple.class);
            when(mainEnchant.getName()).thenReturn("sharpness");
            when(mainEnchant.getLevel()).thenReturn(1);
            ctx.extraData.setMainBook(new BookData(new ItemStack(Material.ENCHANTED_BOOK), mainEnchant));

            CEEnchantSimple ingEnchant = mock(CEEnchantSimple.class);
            when(ingEnchant.getName()).thenReturn("sharpness");
            ctx.extraData.getBookIngredients().add(new BookData(new ItemStack(Material.ENCHANTED_BOOK), ingEnchant));
            ctx.extraData.setRandomXp(new RandomRangeInt(10, 20));
            when(ctx.settings.getXp(ingEnchant)).thenReturn(new RandomRangeInt(10, 20));

            ctx.menu.returnIngredient(0);

            assertFalse(ctx.extraData.hasBookIngredients());
            assertEquals(0, ctx.extraData.getRandomXp().getMin());
            assertEquals(0, ctx.extraData.getRandomXp().getMax());
        }
    }

    @Nested
    @DisplayName("XP Calculation Tests")
    class XpCalculationTests {

        private MenuTestContext ctx;

        @BeforeEach
        void setUp() {
            assumeTrue(server != null, "MockBukkit not available");
            ctx = createMenuContext();
        }

        @Test
        @DisplayName("getMinFutureXp caps at requiredXp")
        void minFutureXpCaps() {
            CEEnchantSimple enchant = mock(CEEnchantSimple.class);
            when(enchant.getName()).thenReturn("sharpness");
            when(enchant.getLevel()).thenReturn(1);
            when(enchant.getXp()).thenReturn(90);

            BookUpgradeData upgradeData = mock(BookUpgradeData.class);
            when(upgradeData.getRequiredXp()).thenReturn(100);
            when(ctx.settings.getBookUpgradeData("sharpness", 1)).thenReturn(upgradeData);

            ctx.extraData.setMainBook(new BookData(new ItemStack(Material.ENCHANTED_BOOK), enchant));
            ctx.extraData.setRandomXp(new RandomRangeInt(15, 30));

            assertEquals(100, ctx.menu.getMinFutureXp());
        }

        @Test
        @DisplayName("getMaxFutureXp caps at requiredXp")
        void maxFutureXpCaps() {
            CEEnchantSimple enchant = mock(CEEnchantSimple.class);
            when(enchant.getName()).thenReturn("sharpness");
            when(enchant.getLevel()).thenReturn(1);
            when(enchant.getXp()).thenReturn(90);

            BookUpgradeData upgradeData = mock(BookUpgradeData.class);
            when(upgradeData.getRequiredXp()).thenReturn(100);
            when(ctx.settings.getBookUpgradeData("sharpness", 1)).thenReturn(upgradeData);

            ctx.extraData.setMainBook(new BookData(new ItemStack(Material.ENCHANTED_BOOK), enchant));
            ctx.extraData.setRandomXp(new RandomRangeInt(5, 30));

            assertEquals(100, ctx.menu.getMaxFutureXp());
        }

        @Test
        @DisplayName("getMinFutureXp without cap returns xp + min")
        void minFutureXpNoCap() {
            CEEnchantSimple enchant = mock(CEEnchantSimple.class);
            when(enchant.getName()).thenReturn("sharpness");
            when(enchant.getLevel()).thenReturn(1);
            when(enchant.getXp()).thenReturn(10);

            BookUpgradeData upgradeData = mock(BookUpgradeData.class);
            when(upgradeData.getRequiredXp()).thenReturn(100);
            when(ctx.settings.getBookUpgradeData("sharpness", 1)).thenReturn(upgradeData);

            ctx.extraData.setMainBook(new BookData(new ItemStack(Material.ENCHANTED_BOOK), enchant));
            ctx.extraData.setRandomXp(new RandomRangeInt(5, 10));

            assertEquals(15, ctx.menu.getMinFutureXp());
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
        @DisplayName("handleClose with main book does not throw")
        void handleCloseWithMainBook() {
            CEEnchantSimple enchant = mock(CEEnchantSimple.class);
            when(enchant.getName()).thenReturn("sharpness");
            when(enchant.getLevel()).thenReturn(1);
            ctx.extraData.setMainBook(new BookData(new ItemStack(Material.ENCHANTED_BOOK), enchant));
            assertDoesNotThrow(() -> ctx.menu.handleClose());
        }
    }
}
