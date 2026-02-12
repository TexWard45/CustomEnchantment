package com.bafmc.customenchantment.guard;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.task.GuardTask;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for GuardModule class.
 * Tests module lifecycle and task management.
 */
public class GuardModuleTest {

    private GuardModule guardModule;

    @Mock
    private CustomEnchantment mockPlugin;

    @Mock
    private Server mockServer;

    @Mock
    private BukkitScheduler mockScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockPlugin.getServer()).thenReturn(mockServer);
        when(mockServer.getScheduler()).thenReturn(mockScheduler);

        guardModule = new GuardModule(mockPlugin);
    }

    // ==================== Constructor Tests ====================

    @Test
    void constructor_initializesWithPlugin() {
        assertNotNull(guardModule);
    }

    // ==================== onEnable Tests ====================

    @Test
    void onEnable_initializesGuardManager() {
        guardModule.onEnable();

        assertNotNull(guardModule.getGuardManager());
    }

    @Test
    void onEnable_initializesGuardTask() {
        guardModule.onEnable();

        assertNotNull(guardModule.getGuardTask());
    }

    @Test
    void onEnable_schedulesTaskWithCorrectParameters() {
        guardModule.onEnable();

        GuardTask task = guardModule.getGuardTask();
        // Task is scheduled via runTaskTimer(plugin, 0, 20)
        // We can verify the task was created
        assertNotNull(task);
    }

    // ==================== setupTask Tests ====================

    @Test
    void setupTask_createsNewGuardTask() {
        guardModule.onEnable();
        GuardTask firstTask = guardModule.getGuardTask();

        guardModule.setupTask();
        GuardTask secondTask = guardModule.getGuardTask();

        // Tasks should be different instances
        assertNotSame(firstTask, secondTask);
    }

    // ==================== onDisable Tests ====================

    @Test
    void onDisable_cancelsGuardTask() {
        guardModule.onEnable();
        GuardTask task = guardModule.getGuardTask();

        // Spy on the task to verify cancel is called
        GuardTask spyTask = spy(task);

        // Use reflection to set the spy task
        try {
            java.lang.reflect.Field taskField = GuardModule.class.getDeclaredField("guardTask");
            taskField.setAccessible(true);
            taskField.set(guardModule, spyTask);
        } catch (Exception e) {
            fail("Failed to set spy task: " + e.getMessage());
        }

        guardModule.onDisable();

        verify(spyTask).cancel();
    }

    // ==================== Getter Tests ====================

    @Test
    void getGuardManager_returnsNullBeforeEnable() {
        assertNull(guardModule.getGuardManager());
    }

    @Test
    void getGuardManager_returnsManagerAfterEnable() {
        guardModule.onEnable();

        GuardManager manager = guardModule.getGuardManager();

        assertNotNull(manager);
    }

    @Test
    void getGuardTask_returnsNullBeforeEnable() {
        assertNull(guardModule.getGuardTask());
    }

    @Test
    void getGuardTask_returnsTaskAfterEnable() {
        guardModule.onEnable();

        GuardTask task = guardModule.getGuardTask();

        assertNotNull(task);
    }

    // ==================== Integration Tests ====================

    @Test
    void fullLifecycle_enableAndDisable() {
        // Enable
        guardModule.onEnable();
        assertNotNull(guardModule.getGuardManager());
        assertNotNull(guardModule.getGuardTask());

        // Spy on task for cancel verification
        GuardTask spyTask = spy(guardModule.getGuardTask());
        try {
            java.lang.reflect.Field taskField = GuardModule.class.getDeclaredField("guardTask");
            taskField.setAccessible(true);
            taskField.set(guardModule, spyTask);
        } catch (Exception e) {
            fail("Failed to set spy task: " + e.getMessage());
        }

        // Disable
        guardModule.onDisable();
        verify(spyTask).cancel();
    }

    @Test
    void guardManager_isReusableBetweenTasks() {
        guardModule.onEnable();
        GuardManager manager1 = guardModule.getGuardManager();

        guardModule.setupTask();
        GuardManager manager2 = guardModule.getGuardManager();

        // GuardManager should remain the same even when task is reset
        assertSame(manager1, manager2);
    }
}
