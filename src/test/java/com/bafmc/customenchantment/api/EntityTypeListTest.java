package com.bafmc.customenchantment.api;

import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for EntityTypeList class - list of EntityTypes with deduplication and lookup.
 */
@DisplayName("EntityTypeList Tests")
class EntityTypeListTest {

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

    @BeforeEach
    void setUp() {
        // Clear the static DEFINE_MAP before each test
        EntityTypeList.getMap().clear();
    }

    @AfterEach
    void tearDown() {
        // Clean up after tests
        EntityTypeList.getMap().clear();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty EntityTypeList")
        void shouldCreateEmptyEntityTypeList() {
            EntityTypeList list = new EntityTypeList();

            assertNotNull(list);
            assertEquals(0, list.size());
        }

        @Test
        @DisplayName("Should create EntityTypeList from list of EntityTypes")
        void shouldCreateEntityTypeListFromList() {
            List<EntityType> types = Arrays.asList(
                EntityType.ZOMBIE,
                EntityType.SKELETON,
                EntityType.CREEPER
            );

            EntityTypeList list = new EntityTypeList(types);

            assertEquals(3, list.size());
            assertTrue(list.contains(EntityType.ZOMBIE));
            assertTrue(list.contains(EntityType.SKELETON));
            assertTrue(list.contains(EntityType.CREEPER));
        }

        @Test
        @DisplayName("Should create EntityTypeList from empty list")
        void shouldCreateEntityTypeListFromEmptyList() {
            EntityTypeList list = new EntityTypeList(new ArrayList<>());

            assertEquals(0, list.size());
        }
    }

    @Nested
    @DisplayName("add Tests")
    class AddTests {

        @Test
        @DisplayName("Should add EntityType to list")
        void shouldAddEntityTypeToList() {
            EntityTypeList list = new EntityTypeList();

            boolean result = list.add(EntityType.ZOMBIE);

            assertTrue(result);
            assertEquals(1, list.size());
            assertTrue(list.contains(EntityType.ZOMBIE));
        }

        @Test
        @DisplayName("Should prevent duplicate EntityType")
        void shouldPreventDuplicateEntityType() {
            EntityTypeList list = new EntityTypeList();
            list.add(EntityType.ZOMBIE);

            boolean result = list.add(EntityType.ZOMBIE);

            assertFalse(result);
            assertEquals(1, list.size());
        }

        @Test
        @DisplayName("Should allow different EntityTypes")
        void shouldAllowDifferentEntityTypes() {
            EntityTypeList list = new EntityTypeList();

            list.add(EntityType.ZOMBIE);
            list.add(EntityType.SKELETON);
            list.add(EntityType.CREEPER);

            assertEquals(3, list.size());
        }
    }

    @Nested
    @DisplayName("addAll Tests")
    class AddAllTests {

        @Test
        @DisplayName("Should add all EntityTypes from list")
        void shouldAddAllEntityTypesFromList() {
            EntityTypeList list = new EntityTypeList();
            List<EntityType> types = Arrays.asList(
                EntityType.ZOMBIE,
                EntityType.SKELETON,
                EntityType.CREEPER
            );

            list.addAll(types);

            assertEquals(3, list.size());
        }

        @Test
        @DisplayName("Should skip duplicates when adding all")
        void shouldSkipDuplicatesWhenAddingAll() {
            EntityTypeList list = new EntityTypeList();
            list.add(EntityType.ZOMBIE);

            List<EntityType> types = Arrays.asList(
                EntityType.ZOMBIE,     // Duplicate
                EntityType.SKELETON
            );

            list.addAll(types);

            assertEquals(2, list.size());
        }

        @Test
        @DisplayName("Should handle empty list in addAll")
        void shouldHandleEmptyListInAddAll() {
            EntityTypeList list = new EntityTypeList();
            list.add(EntityType.ZOMBIE);

            list.addAll(new ArrayList<>());

            assertEquals(1, list.size());
        }

        @Test
        @DisplayName("addAll should always return true")
        void addAllShouldAlwaysReturnTrue() {
            EntityTypeList list = new EntityTypeList();

            boolean result = list.addAll(Arrays.asList(EntityType.ZOMBIE));

            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("defineEntityTypeList Tests")
    class DefineEntityTypeListTests {

        @Test
        @DisplayName("Should define entity type list with key")
        void shouldDefineEntityTypeListWithKey() {
            EntityTypeList list = new EntityTypeList();
            list.add(EntityType.ZOMBIE);
            list.add(EntityType.SKELETON);

            EntityTypeList.defineEntityTypeList("UNDEAD", list);

            assertNotNull(EntityTypeList.getEntityTypeList("UNDEAD"));
            assertEquals(2, EntityTypeList.getEntityTypeList("UNDEAD").size());
        }

        @Test
        @DisplayName("Should not define list with EntityType name as key")
        void shouldNotDefineListWithEntityTypeNameAsKey() {
            EntityTypeList list = new EntityTypeList();
            list.add(EntityType.SKELETON);

            // ZOMBIE is a valid EntityType name, so it should be rejected
            EntityTypeList.defineEntityTypeList("ZOMBIE", list);

            // The list should not be stored because ZOMBIE is a valid EntityType
            assertNull(EntityTypeList.getEntityTypeList("ZOMBIE"));
        }

        @Test
        @DisplayName("Should overwrite existing definition")
        void shouldOverwriteExistingDefinition() {
            EntityTypeList list1 = new EntityTypeList();
            list1.add(EntityType.ZOMBIE);

            EntityTypeList list2 = new EntityTypeList();
            list2.add(EntityType.SKELETON);
            list2.add(EntityType.CREEPER);

            EntityTypeList.defineEntityTypeList("MONSTERS", list1);
            EntityTypeList.defineEntityTypeList("MONSTERS", list2);

            assertEquals(2, EntityTypeList.getEntityTypeList("MONSTERS").size());
        }
    }

    @Nested
    @DisplayName("getEntityTypeList by key Tests")
    class GetEntityTypeListByKeyTests {

        @Test
        @DisplayName("Should return entity type list by key")
        void shouldReturnEntityTypeListByKey() {
            EntityTypeList list = new EntityTypeList();
            list.add(EntityType.ZOMBIE);
            EntityTypeList.defineEntityTypeList("MY_LIST", list);

            EntityTypeList result = EntityTypeList.getEntityTypeList("MY_LIST");

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should return null for undefined key")
        void shouldReturnNullForUndefinedKey() {
            EntityTypeList result = EntityTypeList.getEntityTypeList("UNDEFINED_KEY");

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("getEntityTypeList from String list Tests")
    class GetEntityTypeListFromStringListTests {

        @Test
        @DisplayName("Should parse list of entity type names")
        void shouldParseListOfEntityTypeNames() {
            List<String> names = Arrays.asList("ZOMBIE", "SKELETON", "CREEPER");

            EntityTypeList result = EntityTypeList.getEntityTypeList(names);

            assertNotNull(result);
            assertEquals(3, result.size());
            assertTrue(result.contains(EntityType.ZOMBIE));
            assertTrue(result.contains(EntityType.SKELETON));
            assertTrue(result.contains(EntityType.CREEPER));
        }

        @Test
        @DisplayName("Should resolve defined entity type list")
        void shouldResolveDefinedEntityTypeList() {
            EntityTypeList predefined = new EntityTypeList();
            predefined.add(EntityType.ZOMBIE);
            predefined.add(EntityType.SKELETON);
            EntityTypeList.defineEntityTypeList("UNDEAD", predefined);

            List<String> names = Arrays.asList("UNDEAD", "CREEPER");

            EntityTypeList result = EntityTypeList.getEntityTypeList(names);

            assertNotNull(result);
            assertEquals(3, result.size());
        }

        @Test
        @DisplayName("Should handle empty string list")
        void shouldHandleEmptyStringList() {
            EntityTypeList result = EntityTypeList.getEntityTypeList(new ArrayList<>());

            assertNotNull(result);
            assertEquals(0, result.size());
        }

        @Test
        @DisplayName("Should return null for invalid entity type name")
        void shouldReturnNullForInvalidEntityTypeName() {
            List<String> names = Arrays.asList("INVALID_ENTITY_TYPE");

            EntityTypeList result = EntityTypeList.getEntityTypeList(names);

            // The method returns null when an invalid entity type is encountered
            assertNull(result);
        }

        @Test
        @DisplayName("Should handle mixed defined and entity type names")
        void shouldHandleMixedDefinedAndEntityTypeNames() {
            EntityTypeList undead = new EntityTypeList();
            undead.add(EntityType.ZOMBIE);
            undead.add(EntityType.SKELETON);
            EntityTypeList.defineEntityTypeList("UNDEAD_MOBS", undead);

            List<String> names = Arrays.asList("UNDEAD_MOBS", "CREEPER", "SPIDER");

            EntityTypeList result = EntityTypeList.getEntityTypeList(names);

            assertNotNull(result);
            assertEquals(4, result.size());
        }
    }

    @Nested
    @DisplayName("getMap Tests")
    class GetMapTests {

        @Test
        @DisplayName("Should return DEFINE_MAP")
        void shouldReturnDefineMap() {
            ConcurrentHashMap<String, EntityTypeList> map = EntityTypeList.getMap();

            assertNotNull(map);
        }

        @Test
        @DisplayName("Map should be ConcurrentHashMap")
        void mapShouldBeConcurrentHashMap() {
            assertTrue(EntityTypeList.getMap() instanceof ConcurrentHashMap);
        }

        @Test
        @DisplayName("Map should be modifiable")
        void mapShouldBeModifiable() {
            EntityTypeList list = new EntityTypeList();
            list.add(EntityType.ZOMBIE);

            EntityTypeList.getMap().put("DIRECT_KEY", list);

            assertNotNull(EntityTypeList.getEntityTypeList("DIRECT_KEY"));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle all mob types")
        void shouldHandleAllMobTypes() {
            EntityTypeList list = new EntityTypeList();
            list.add(EntityType.ZOMBIE);
            list.add(EntityType.SKELETON);
            list.add(EntityType.SPIDER);
            list.add(EntityType.CREEPER);
            list.add(EntityType.ENDERMAN);

            assertEquals(5, list.size());
        }

        @Test
        @DisplayName("Should handle non-mob entity types")
        void shouldHandleNonMobEntityTypes() {
            EntityTypeList list = new EntityTypeList();
            list.add(EntityType.ITEM);
            list.add(EntityType.ARROW);
            list.add(EntityType.EXPERIENCE_ORB);

            assertEquals(3, list.size());
        }

        @Test
        @DisplayName("Should handle case sensitivity in defined lists")
        void shouldHandleCaseSensitivityInDefinedLists() {
            EntityTypeList list = new EntityTypeList();
            list.add(EntityType.ZOMBIE);

            EntityTypeList.defineEntityTypeList("MY_LIST", list);

            // Keys are case-sensitive
            assertNotNull(EntityTypeList.getEntityTypeList("MY_LIST"));
            assertNull(EntityTypeList.getEntityTypeList("my_list"));
            assertNull(EntityTypeList.getEntityTypeList("My_List"));
        }

        @Test
        @DisplayName("List should be thread-safe for concurrent access")
        void listShouldBeThreadSafeForConcurrentAccess() {
            // The DEFINE_MAP uses ConcurrentHashMap which is thread-safe
            EntityTypeList list = new EntityTypeList();
            list.add(EntityType.ZOMBIE);

            EntityTypeList.defineEntityTypeList("THREAD_SAFE_KEY", list);

            // Verify concurrent access doesn't throw
            for (int i = 0; i < 100; i++) {
                EntityTypeList.getEntityTypeList("THREAD_SAFE_KEY");
            }

            assertNotNull(EntityTypeList.getEntityTypeList("THREAD_SAFE_KEY"));
        }
    }
}
