package com.bafmc.customenchantment.listener;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.menu.MenuListenerAbstract;
import com.bafmc.custommenu.api.CustomMenuAPI;
import com.bafmc.custommenu.event.CustomMenuClickEvent;
import com.bafmc.custommenu.event.CustomMenuCloseEvent;
import com.bafmc.custommenu.event.CustomMenuOpenEvent;
import com.bafmc.custommenu.menu.CMenu;
import com.bafmc.custommenu.menu.CMenuView;
import com.bafmc.custommenu.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CMenuListener class.
 * Tests CustomMenu plugin integration for menu handling.
 */
@DisplayName("CMenuListener Tests")
class CMenuListenerTest {

    private CMenuListener cMenuListener;

    @Mock
    private CustomEnchantment mockPlugin;

    @Mock
    private Server mockServer;

    @Mock
    private PluginManager mockPluginManager;

    @Mock
    private Player mockPlayer;

    @Mock
    private CPlayer mockCPlayer;

    @Mock
    private CMenu mockCMenu;

    @Mock
    private CMenuView mockCMenuView;

    @Mock
    private MenuListenerAbstract mockMenuListener;

    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        playerUUID = UUID.randomUUID();

        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockPlayer.getName()).thenReturn("TestPlayer");

        when(mockCMenu.getName()).thenReturn("TestMenu");
        when(mockMenuListener.getMenuName()).thenReturn("TestMenu");
    }

    @AfterEach
    void tearDown() {
        // Clear static menu listeners list after each test
        clearMenuListeners();
    }

    private void clearMenuListeners() {
        try {
            Field field = CMenuListener.class.getDeclaredField("menuListeners");
            field.setAccessible(true);
            List<?> list = (List<?>) field.get(null);
            list.clear();
        } catch (Exception e) {
            // Ignore errors during cleanup
        }
    }

    private void setupCMenuListener() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);

            cMenuListener = new CMenuListener(mockPlugin);
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should initialize with plugin reference and register events")
        void constructor_initializesWithPluginAndRegistersEvents() {
            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
                mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);

                cMenuListener = new CMenuListener(mockPlugin);

                assertNotNull(cMenuListener);
                verify(mockPluginManager).registerEvents(cMenuListener, mockPlugin);
            }
        }
    }

    @Nested
    @DisplayName("registerMenuListener Tests")
    class RegisterMenuListenerTests {

        @Test
        @DisplayName("should add menu listener to static list")
        void registerMenuListener_addsListenerToList() {
            setupCMenuListener();

            CMenuListener.registerMenuListener(mockMenuListener);

            // Verify through onMenuOpen behavior
            CustomMenuOpenEvent event = mock(CustomMenuOpenEvent.class);
            when(event.getCMenu()).thenReturn(mockCMenu);

            cMenuListener.onMenuOpen(event);

            verify(mockMenuListener).onMenuOpen(event);
        }

        @Test
        @DisplayName("should allow multiple menu listeners")
        void registerMenuListener_allowsMultipleListeners() {
            setupCMenuListener();

            MenuListenerAbstract mockListener1 = mock(MenuListenerAbstract.class);
            MenuListenerAbstract mockListener2 = mock(MenuListenerAbstract.class);
            when(mockListener1.getMenuName()).thenReturn("Menu1");
            when(mockListener2.getMenuName()).thenReturn("Menu2");

            CMenuListener.registerMenuListener(mockListener1);
            CMenuListener.registerMenuListener(mockListener2);

            // Verify both were added
            CustomMenuOpenEvent event1 = mock(CustomMenuOpenEvent.class);
            CMenu mockCMenu1 = mock(CMenu.class);
            when(event1.getCMenu()).thenReturn(mockCMenu1);
            when(mockCMenu1.getName()).thenReturn("Menu1");

            cMenuListener.onMenuOpen(event1);

            verify(mockListener1).onMenuOpen(event1);
            verify(mockListener2, never()).onMenuOpen(any());
        }
    }

    @Nested
    @DisplayName("onMenuOpen Tests")
    class OnMenuOpenTests {

        @Test
        @DisplayName("should call matching menu listener on menu open")
        void onMenuOpen_callsMatchingMenuListener() {
            setupCMenuListener();
            CMenuListener.registerMenuListener(mockMenuListener);

            CustomMenuOpenEvent event = mock(CustomMenuOpenEvent.class);
            when(event.getCMenu()).thenReturn(mockCMenu);

            cMenuListener.onMenuOpen(event);

            verify(mockMenuListener).onMenuOpen(event);
        }

        @Test
        @DisplayName("should not call non-matching menu listener on menu open")
        void onMenuOpen_doesNotCallNonMatchingListener() {
            setupCMenuListener();

            MenuListenerAbstract otherListener = mock(MenuListenerAbstract.class);
            when(otherListener.getMenuName()).thenReturn("OtherMenu");
            CMenuListener.registerMenuListener(otherListener);

            CustomMenuOpenEvent event = mock(CustomMenuOpenEvent.class);
            when(event.getCMenu()).thenReturn(mockCMenu);

            cMenuListener.onMenuOpen(event);

            verify(otherListener, never()).onMenuOpen(any());
        }

        @Test
        @DisplayName("should break loop after finding matching listener")
        void onMenuOpen_breaksLoopAfterMatch() {
            setupCMenuListener();

            MenuListenerAbstract listener1 = mock(MenuListenerAbstract.class);
            MenuListenerAbstract listener2 = mock(MenuListenerAbstract.class);
            when(listener1.getMenuName()).thenReturn("TestMenu");
            when(listener2.getMenuName()).thenReturn("TestMenu");

            CMenuListener.registerMenuListener(listener1);
            CMenuListener.registerMenuListener(listener2);

            CustomMenuOpenEvent event = mock(CustomMenuOpenEvent.class);
            when(event.getCMenu()).thenReturn(mockCMenu);

            cMenuListener.onMenuOpen(event);

            // Only first matching listener should be called
            verify(listener1).onMenuOpen(event);
            verify(listener2, never()).onMenuOpen(any());
        }
    }

    @Nested
    @DisplayName("onMenuClose Tests")
    class OnMenuCloseTests {

        @Test
        @DisplayName("should call matching menu listener on menu close")
        void onMenuClose_callsMatchingMenuListener() {
            setupCMenuListener();
            CMenuListener.registerMenuListener(mockMenuListener);

            CustomMenuCloseEvent event = mock(CustomMenuCloseEvent.class);
            when(event.getCMenu()).thenReturn(mockCMenu);

            cMenuListener.onMenuClose(event);

            verify(mockMenuListener).onMenuClose(event);
        }

        @Test
        @DisplayName("should not call non-matching menu listener on menu close")
        void onMenuClose_doesNotCallNonMatchingListener() {
            setupCMenuListener();

            MenuListenerAbstract otherListener = mock(MenuListenerAbstract.class);
            when(otherListener.getMenuName()).thenReturn("OtherMenu");
            CMenuListener.registerMenuListener(otherListener);

            CustomMenuCloseEvent event = mock(CustomMenuCloseEvent.class);
            when(event.getCMenu()).thenReturn(mockCMenu);

            cMenuListener.onMenuClose(event);

            verify(otherListener, never()).onMenuClose(any());
        }
    }

    @Nested
    @DisplayName("onMenuClick Tests")
    class OnMenuClickTests {

        @Test
        @DisplayName("should call matching menu listener on menu click")
        void onMenuClick_callsMatchingMenuListener() {
            setupCMenuListener();
            CMenuListener.registerMenuListener(mockMenuListener);

            CustomMenuClickEvent event = mock(CustomMenuClickEvent.class);
            when(event.getCMenu()).thenReturn(mockCMenu);

            cMenuListener.onMenuClick(event);

            verify(mockMenuListener).onMenuClick(event);
        }

        @Test
        @DisplayName("should not call non-matching menu listener on menu click")
        void onMenuClick_doesNotCallNonMatchingListener() {
            setupCMenuListener();

            MenuListenerAbstract otherListener = mock(MenuListenerAbstract.class);
            when(otherListener.getMenuName()).thenReturn("OtherMenu");
            CMenuListener.registerMenuListener(otherListener);

            CustomMenuClickEvent event = mock(CustomMenuClickEvent.class);
            when(event.getCMenu()).thenReturn(mockCMenu);

            cMenuListener.onMenuClick(event);

            verify(otherListener, never()).onMenuClick(any());
        }
    }

    @Nested
    @DisplayName("onInventoryClick Tests")
    class OnInventoryClickTests {

        @Test
        @DisplayName("should return early when player has no custom menu open")
        void onInventoryClick_returnsEarlyWhenNoCustomMenuOpen() {
            setupCMenuListener();
            CMenuListener.registerMenuListener(mockMenuListener);

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            when(event.getWhoClicked()).thenReturn(mockPlayer);

            try (MockedStatic<CustomMenuAPI> mockedAPI = mockStatic(CustomMenuAPI.class)) {
                mockedAPI.when(() -> CustomMenuAPI.getCPlayer(mockPlayer)).thenReturn(mockCPlayer);
                when(mockCPlayer.isOpenCustomMenu()).thenReturn(false);

                cMenuListener.onMenuClick(event);

                verify(mockMenuListener, never()).onInventoryClick(any());
            }
        }

        @Test
        @DisplayName("should call matching menu listener on inventory click when custom menu open")
        void onInventoryClick_callsMatchingListenerWhenCustomMenuOpen() {
            setupCMenuListener();
            CMenuListener.registerMenuListener(mockMenuListener);

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            when(event.getWhoClicked()).thenReturn(mockPlayer);

            try (MockedStatic<CustomMenuAPI> mockedAPI = mockStatic(CustomMenuAPI.class)) {
                mockedAPI.when(() -> CustomMenuAPI.getCPlayer(mockPlayer)).thenReturn(mockCPlayer);
                when(mockCPlayer.isOpenCustomMenu()).thenReturn(true);
                when(mockCPlayer.getOpenCustomMenu()).thenReturn(mockCMenuView);
                when(mockCMenuView.getCMenu()).thenReturn(mockCMenu);

                cMenuListener.onMenuClick(event);

                verify(mockMenuListener).onInventoryClick(event);
            }
        }

        @Test
        @DisplayName("should not call non-matching menu listener on inventory click")
        void onInventoryClick_doesNotCallNonMatchingListener() {
            setupCMenuListener();

            MenuListenerAbstract otherListener = mock(MenuListenerAbstract.class);
            when(otherListener.getMenuName()).thenReturn("OtherMenu");
            CMenuListener.registerMenuListener(otherListener);

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            when(event.getWhoClicked()).thenReturn(mockPlayer);

            try (MockedStatic<CustomMenuAPI> mockedAPI = mockStatic(CustomMenuAPI.class)) {
                mockedAPI.when(() -> CustomMenuAPI.getCPlayer(mockPlayer)).thenReturn(mockCPlayer);
                when(mockCPlayer.isOpenCustomMenu()).thenReturn(true);
                when(mockCPlayer.getOpenCustomMenu()).thenReturn(mockCMenuView);
                when(mockCMenuView.getCMenu()).thenReturn(mockCMenu);

                cMenuListener.onMenuClick(event);

                verify(otherListener, never()).onInventoryClick(any());
            }
        }
    }
}
