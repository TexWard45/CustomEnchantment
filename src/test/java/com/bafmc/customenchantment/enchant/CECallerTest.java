package com.bafmc.customenchantment.enchant;

import com.bafmc.bukkit.utils.EquipSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CECaller - the main enchant execution caller with fluent API.
 * Heavy methods like call/callCE/callCEFunction depend on full plugin runtime,
 * so we test the fluent setters, getters, and utility methods.
 */
@DisplayName("CECaller Tests")
class CECallerTest {

    private CECaller caller;

    @BeforeEach
    void setUp() {
        caller = CECaller.instance();
    }

    @Nested
    @DisplayName("instance Tests")
    class InstanceTests {

        @Test
        @DisplayName("should create new instance each call")
        void shouldCreateNewInstanceEachCall() {
            CECaller caller1 = CECaller.instance();
            CECaller caller2 = CECaller.instance();

            assertNotSame(caller1, caller2);
        }
    }

    @Nested
    @DisplayName("Fluent Setter Tests")
    class FluentSetterTests {

        @Test
        @DisplayName("should set and get CEType")
        void shouldSetCEType() {
            CECaller result = caller.setCEType(CEType.ATTACK);

            assertSame(caller, result); // fluent
            assertSame(CEType.ATTACK, caller.getCEType());
        }

        @Test
        @DisplayName("should set and get equipSlot")
        void shouldSetEquipSlot() {
            CECaller result = caller.setEquipSlot(EquipSlot.MAINHAND);

            assertSame(caller, result);
            assertEquals(EquipSlot.MAINHAND, caller.getEquipSlot());
        }

        @Test
        @DisplayName("should set and get activeEquipSlot")
        void shouldSetActiveEquipSlot() {
            CECaller result = caller.setActiveEquipSlot(EquipSlot.HELMET);

            assertSame(caller, result);
            assertEquals(EquipSlot.HELMET, caller.getActiveEquipSlot());
        }

        @Test
        @DisplayName("should set and get byPassCooldown")
        void shouldSetByPassCooldown() {
            CECaller result = caller.setByPassCooldown(true);

            assertSame(caller, result);
            assertTrue(caller.isByPassCooldown());
        }

        @Test
        @DisplayName("should set and get primary")
        void shouldSetPrimary() {
            CECaller result = caller.setPrimary(true);

            assertSame(caller, result);
            assertTrue(caller.isPrimary());
        }

        @Test
        @DisplayName("should set and get executerLater")
        void shouldSetExecuterLater() {
            caller.setExecuterLater(false);

            assertFalse(caller.isExecuterLater());
        }

        @Test
        @DisplayName("should default executerLater to true")
        void shouldDefaultExecuterLaterTrue() {
            assertTrue(caller.isExecuterLater());
        }
    }

    @Nested
    @DisplayName("CESimple and CEEnchant Tests")
    class CESimpleTests {

        @Test
        @DisplayName("should set and get CESimple")
        void shouldSetCESimple() {
            CEEnchantSimple simple = new CEEnchantSimple("Test", 1);
            caller.setCESimple(simple);

            assertSame(simple, caller.getCESimple());
        }

        @Test
        @DisplayName("should set and get CESimpleList")
        void shouldSetCESimpleList() {
            List<CEEnchantSimple> list = Arrays.asList(
                    new CEEnchantSimple("A", 1),
                    new CEEnchantSimple("B", 2)
            );
            caller.setCESimpleList(list);

            assertEquals(2, caller.getCESimpleList().size());
        }

        @Test
        @DisplayName("should set empty list when null weapon")
        void shouldSetEmptyListWhenNullWeapon() {
            caller.setCESimpleList((com.bafmc.customenchantment.item.CEWeaponAbstract) null);

            assertNotNull(caller.getCESimpleList());
            assertTrue(caller.getCESimpleList().isEmpty());
        }
    }

    @Nested
    @DisplayName("EquipSlotList Tests")
    class EquipSlotListTests {

        @Test
        @DisplayName("should set equip slot list from array")
        void shouldSetFromArray() {
            EquipSlot[] arr = {EquipSlot.MAINHAND, EquipSlot.OFFHAND};
            caller.setEquipSlotList(arr);

            assertEquals(2, caller.getEquipSlotList().size());
        }

        @Test
        @DisplayName("should set equip slot list from list")
        void shouldSetFromList() {
            List<EquipSlot> list = Arrays.asList(EquipSlot.HELMET, EquipSlot.BOOTS);
            caller.setEquipSlotList(list);

            assertEquals(2, caller.getEquipSlotList().size());
        }

        @Test
        @DisplayName("should be null initially")
        void shouldBeNullInitially() {
            assertNull(caller.getEquipSlotList());
        }
    }

    @Nested
    @DisplayName("Caller Tests")
    class CallerTests {

        @Test
        @DisplayName("should return false for hasCaller when not set")
        void shouldReturnFalseWhenNotSet() {
            assertFalse(caller.hasCaller());
        }

        @Test
        @DisplayName("should return null for getCaller when not set")
        void shouldReturnNullWhenNotSet() {
            assertNull(caller.getCaller());
        }
    }

    @Nested
    @DisplayName("resetResult Tests")
    class ResetResultTests {

        @Test
        @DisplayName("should create fresh result")
        void shouldCreateFreshResult() {
            caller.getResult().put("old_key", "old_value");
            caller.resetResult();

            assertFalse(caller.getResult().isSet("old_key"));
        }

        @Test
        @DisplayName("should return this for chaining")
        void shouldReturnThisForChaining() {
            assertSame(caller, caller.resetResult());
        }
    }

    @Nested
    @DisplayName("getResult Tests")
    class GetResultTests {

        @Test
        @DisplayName("should create result lazily if null")
        void shouldCreateResultLazily() {
            CECallerResult result = caller.getResult();

            assertNotNull(result);
        }

        @Test
        @DisplayName("should return same result on subsequent calls")
        void shouldReturnSameResult() {
            CECallerResult result1 = caller.getResult();
            CECallerResult result2 = caller.getResult();

            assertSame(result1, result2);
        }
    }

    @Nested
    @DisplayName("SlotEnchantsChanceMap Tests")
    class SlotEnchantsChanceMapTests {

        @Test
        @DisplayName("should return empty map initially")
        void shouldReturnEmptyMapInitially() {
            assertNotNull(caller.getSlotEnchantsChanceMap());
            assertTrue(caller.getSlotEnchantsChanceMap().isEmpty());
        }

        @Test
        @DisplayName("should set and get slot enchants chance map")
        void shouldSetAndGetMap() {
            HashMap<EquipSlot, List<String>> map = new HashMap<>();
            map.put(EquipSlot.MAINHAND, Arrays.asList("enchant1"));

            caller.setSlotEnchantsChanceMap(map);

            assertEquals(1, caller.getSlotEnchantsChanceMap().size());
        }
    }

    @Nested
    @DisplayName("putEnchantChance Tests")
    class PutEnchantChanceTests {

        @Test
        @DisplayName("should add enchant to slot chance list")
        void shouldAddEnchantToSlotChanceList() {
            caller.putEnchantChance(EquipSlot.MAINHAND, "Sharpness");

            List<String> list = caller.getEnchantChanceList(EquipSlot.MAINHAND);
            assertEquals(1, list.size());
            assertTrue(list.contains("Sharpness"));
        }

        @Test
        @DisplayName("should not add duplicate enchant name")
        void shouldNotAddDuplicate() {
            caller.putEnchantChance(EquipSlot.MAINHAND, "Sharpness");
            caller.putEnchantChance(EquipSlot.MAINHAND, "Sharpness");

            assertEquals(1, caller.getEnchantChanceList(EquipSlot.MAINHAND).size());
        }

        @Test
        @DisplayName("should add to multiple slots")
        void shouldAddToMultipleSlots() {
            List<EquipSlot> slots = Arrays.asList(EquipSlot.MAINHAND, EquipSlot.OFFHAND);
            caller.putEnchantChance(slots, "Fire");

            assertTrue(caller.getEnchantChanceList(EquipSlot.MAINHAND).contains("Fire"));
            assertTrue(caller.getEnchantChanceList(EquipSlot.OFFHAND).contains("Fire"));
        }
    }

    @Nested
    @DisplayName("getEnchantChanceList Tests")
    class GetEnchantChanceListTests {

        @Test
        @DisplayName("should return empty list for new slot")
        void shouldReturnEmptyForNewSlot() {
            List<String> list = caller.getEnchantChanceList(EquipSlot.HELMET);

            assertNotNull(list);
            assertTrue(list.isEmpty());
        }

        @Test
        @DisplayName("should return same list reference for same slot")
        void shouldReturnSameListReference() {
            List<String> list1 = caller.getEnchantChanceList(EquipSlot.MAINHAND);
            List<String> list2 = caller.getEnchantChanceList(EquipSlot.MAINHAND);

            assertSame(list1, list2);
        }
    }

    @Nested
    @DisplayName("WeaponMap Tests")
    class WeaponMapTests {

        @Test
        @DisplayName("should set and get weapon map")
        void shouldSetAndGetWeaponMap() {
            Map<EquipSlot, com.bafmc.customenchantment.item.CEWeaponAbstract> map = new HashMap<>();
            caller.setWeaponMap(map);

            assertSame(map, caller.getWeaponMap());
        }
    }
}
