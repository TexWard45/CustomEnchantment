# CustomEnchantment NMS (Net.Minecraft.Server) Reference

This document provides comprehensive reference for the version-specific code (NMS) in the CustomEnchantment plugin.

## Table of Contents

- [Overview](#overview)
- [Version Compatibility](#version-compatibility)
- [NMS Classes](#nms-classes)
- [NBT Data System](#nbt-data-system)
- [Packet API](#packet-api)
- [Entity NMS](#entity-nms)
- [Best Practices](#best-practices)

---

## Overview

The plugin uses NMS (Net.Minecraft.Server) code for:

| Usage | Purpose |
|-------|---------|
| NBT Data | Custom item data storage |
| Packets | Client-side item display |
| Entity Access | Mob AI and targeting |
| Particles | Particle effects |

### Key Dependencies

```gradle
// Server API (Leaf fork)
compileOnly 'cn.dreeam.leaf:leaf-api:1.21.1-R0.1-SNAPSHOT'
compileOnly 'cn.dreeam.leaf:leaf:1.21.1-R0.1-SNAPSHOT'

// Particle effects
compileOnly 'com.github.fierioziy:ParticleNativeAPI:4.3.0'

// Item NBT
compileOnly 'de.tr7zw:item-nbt-api-plugin:2.13.1'
```

---

## Version Compatibility

### Current Target Version

- **Minecraft**: 1.21.1
- **Server**: Leaf API (Paper fork)
- **NMS Mappings**: Mojang

### NMS Package Structure

```
net.minecraft.core          - Core utilities (BlockPos)
net.minecraft.world.entity  - Entity classes
net.minecraft.network       - Network packets
net.minecraft.server        - Server classes
org.bukkit.craftbukkit      - CraftBukkit adapters
```

### Import Examples

```java
// NMS imports
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;

// CraftBukkit imports
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
```

---

## NMS Classes

### CECraftItemStackNMS

Wrapper for ItemStack with CE-specific NBT operations:

```java
import com.bafmc.customenchantment.nms.CECraftItemStackNMS;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;

public class CECraftItemStackNMS extends NMSItemStack {
    public CECraftItemStackNMS(ItemStack itemStack) {
        super(itemStack);
    }

    // Set CE NBT compound
    public void setCETag(NMSNBTTagCompound ceTag) {
        NMSNBTTagCompound tag = getCompound();
        if (ceTag != null) {
            tag.set(CENBT.CE, ceTag);
        } else {
            tag.remove(CENBT.CE);
        }
        setCompound(tag);
    }

    // Get CE NBT compound
    public NMSNBTTagCompound getCECompound() {
        NMSNBTTagCompound ceTag = getCompound().getCompound(CENBT.CE);
        return ceTag != null ? ceTag : new NMSNBTTagCompound();
    }

    // Check if CE compound exists
    public boolean isSetCECompound() {
        return getCompound().getCompound(CENBT.CE) != null;
    }
}
```

### EntityLivingNMS

Wrapper for accessing NMS LivingEntity:

```java
import com.bafmc.customenchantment.nms.EntityLivingNMS;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.entity.CraftEntity;

public class EntityLivingNMS {
    private Entity entity;
    private LivingEntity entityLiving;

    public EntityLivingNMS(Entity entity) {
        this.entity = entity;
        this.entityLiving = ((LivingEntity) ((CraftEntity) entity).getHandle());
    }

    public Entity getEntity() {
        return entity;
    }

    public LivingEntity getEntityLiving() {
        return entityLiving;
    }
}
```

### EntityInsentientNMS

Wrapper for mob AI and targeting:

```java
import com.bafmc.customenchantment.nms.EntityInsentientNMS;
import net.minecraft.world.entity.Mob;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

public class EntityInsentientNMS {
    private Entity entity;
    private Mob entityInsentient;
    private double speed;

    public EntityInsentientNMS(Entity entity, double speed) {
        this.entity = entity;
        this.entityInsentient = ((Mob) ((CraftEntity) entity).getHandle());
        this.speed = speed;
    }

    // Set attack target
    public void setGoalTarget(EntityInsentientNMS entity) {
        entityInsentient.setTarget(entity.getEntityInsentient(),
                                   TargetReason.CUSTOM, false);
    }

    public void setGoalTarget(EntityLivingNMS entity) {
        entityInsentient.setTarget(entity.getEntityLiving(),
                                   TargetReason.CUSTOM, false);
    }

    public void setGoalTarget(Entity entity) {
        entityInsentient.setTarget(
            ((LivingEntity) ((CraftEntity) entity).getHandle()));
    }
}
```

---

## NBT Data System

### CENBT Constants

Keys used for NBT data storage:

```java
public class CENBT {
    public static final String CE = "customenchantment";  // Root tag
    public static final String TYPE = "type";             // Item type
    public static final String CUSTOM_TYPE = "custom-type";
    public static final String PATTERN = "pattern";       // Item pattern
    public static final String NAME = "name";             // Item name
    public static final String LEVEL = "lvl";             // Level
    public static final String SUCCESS = "success";       // Success rate
    public static final String DESTROY = "destroy";       // Destroy rate
    public static final String ENCHANT = "enchant";       // Enchantment data
    public static final String DATA = "data";             // Generic data
    public static final String SETTINGS = "settings";     // Settings
    public static final String POINT = "point";           // Enchant points
    public static final String TIME = "time";             // Timestamp
    public static final String ID = "id";                 // Unique ID
    public static final String DEFAULT = "default";       // Default value
    public static final String UPGRADE = "upgrade";       // Upgrade data
}
```

### NBT Data Structure

```
ItemStack NBT
└── customenchantment (compound)
    ├── type: "WEAPON"
    ├── pattern: "excalibur"
    ├── enchant (compound)
    │   ├── lifesteal: 3
    │   ├── critical: 2
    │   └── ...
    ├── data (compound)
    │   ├── point: 5
    │   ├── gem-slot: 3
    │   └── ...
    └── settings (compound)
        ├── protect-dead: true
        ├── protect-destroy: true
        └── ...
```

### Reading NBT Data

```java
// Get CE NBT from ItemStack
CECraftItemStackNMS craftItem = new CECraftItemStackNMS(itemStack);

if (craftItem.isSetCECompound()) {
    NMSNBTTagCompound ceTag = craftItem.getCECompound();

    // Read values
    String type = ceTag.getString(CENBT.TYPE);
    String pattern = ceTag.getString(CENBT.PATTERN);

    // Read nested compound
    NMSNBTTagCompound enchantTag = ceTag.getCompound(CENBT.ENCHANT);
    int lifestealLevel = enchantTag.getInt("lifesteal");
}
```

### Writing NBT Data

```java
CECraftItemStackNMS craftItem = new CECraftItemStackNMS(itemStack);
NMSNBTTagCompound ceTag = new NMSNBTTagCompound();

// Set values
ceTag.setString(CENBT.TYPE, "WEAPON");
ceTag.setString(CENBT.PATTERN, "excalibur");

// Create nested compound
NMSNBTTagCompound enchantTag = new NMSNBTTagCompound();
enchantTag.setInt("lifesteal", 3);
ceTag.set(CENBT.ENCHANT, enchantTag);

// Apply to item
craftItem.setCETag(ceTag);

// Get updated ItemStack
ItemStack updatedItem = craftItem.getItemStack();
```

---

## Packet API

### ItemPacketAPI

Send item updates to client:

```java
import com.bafmc.customenchantment.api.ItemPacketAPI;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;

public class ItemPacketAPI {
    public static void sendItem(Player player, int windowId,
                                int vanillaSlot, ItemStack itemStack) {
        // Convert slot index for packet
        if (vanillaSlot >= 0 && vanillaSlot <= 8) {
            vanillaSlot += 36;
        } else if (vanillaSlot >= 36 && vanillaSlot <= 39) {
            vanillaSlot = 44 - vanillaSlot;
        } else if (vanillaSlot == 40) {
            vanillaSlot = 45;
        }

        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        // Create and send packet
        ClientboundContainerSetSlotPacket packet =
            new ClientboundContainerSetSlotPacket(
                windowId,
                0,  // stateId
                vanillaSlot,
                CraftItemStack.asNMSCopy(itemStack)
            );

        nmsPlayer.connection.send(packet);
    }
}
```

### Slot Index Mapping

| Bukkit Slot | Packet Slot | Description |
|-------------|-------------|-------------|
| 0-8 | 36-44 | Hotbar |
| 36-39 | 8-5 | Armor (reversed) |
| 40 | 45 | Off-hand |
| 9-35 | 9-35 | Main inventory |

---

## Entity NMS

### Accessing NMS Entities

```java
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;

// Get NMS entity from Bukkit entity
Entity bukkitEntity = ...;
net.minecraft.world.entity.Entity nmsEntity =
    ((CraftEntity) bukkitEntity).getHandle();

// Get NMS player
Player player = ...;
ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

// Get NMS living entity
LivingEntity bukkitLiving = ...;
net.minecraft.world.entity.LivingEntity nmsLiving =
    ((org.bukkit.craftbukkit.entity.CraftLivingEntity) bukkitLiving).getHandle();
```

### Mob AI Control

```java
EntityInsentientNMS mobNMS = new EntityInsentientNMS(mob, 1.0);

// Set attack target
mobNMS.setGoalTarget(targetPlayer);

// Set target to another mob
EntityInsentientNMS targetMob = new EntityInsentientNMS(target, 1.0);
mobNMS.setGoalTarget(targetMob);
```

---

## Best Practices

### Version Safety

1. **Wrap NMS access**: Use wrapper classes for all NMS operations
2. **Catch exceptions**: NMS can change between versions
3. **Fallback logic**: Provide non-NMS alternatives when possible

```java
try {
    // NMS operation
    performNMSAction();
} catch (Exception e) {
    // Fallback to Bukkit API
    performBukkitFallback();
}
```

### Null Safety

```java
// Always check for null before casting
Entity entity = event.getEntity();
if (entity instanceof LivingEntity) {
    CraftLivingEntity craftEntity = (CraftLivingEntity) entity;
    if (craftEntity.getHandle() != null) {
        // Safe to use
    }
}
```

### Thread Safety

```java
// NMS operations should run on main thread
if (Bukkit.isPrimaryThread()) {
    performNMSAction();
} else {
    Bukkit.getScheduler().runTask(plugin, this::performNMSAction);
}
```

### Performance

1. **Cache NMS references**: Don't repeatedly cast to NMS
2. **Batch packet operations**: Send multiple packets together
3. **Minimize reflection**: Use direct NMS access when possible

```java
// Cache the NMS entity
private net.minecraft.world.entity.Entity cachedNMSEntity;

public void init(Entity entity) {
    this.cachedNMSEntity = ((CraftEntity) entity).getHandle();
}

public void performOperation() {
    // Use cached reference
    cachedNMSEntity.doSomething();
}
```

---

## Framework Integration

### BafFramework NMS

The plugin uses BafFramework for NMS abstraction:

```java
import com.bafmc.bukkit.bafframework.nms.NMSItemStack;
import com.bafmc.bukkit.bafframework.nms.NMSNBTTagCompound;
import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;
```

### NMSItemStack Methods

```java
NMSItemStack nmsItem = new NMSItemStack(itemStack);

// Get/set NBT compound
NMSNBTTagCompound compound = nmsItem.getCompound();
nmsItem.setCompound(compound);

// Get updated ItemStack
ItemStack updated = nmsItem.getItemStack();
```

### NMSNBTTagCompound Methods

```java
NMSNBTTagCompound compound = new NMSNBTTagCompound();

// Primitives
compound.setString("key", "value");
compound.setInt("key", 42);
compound.setDouble("key", 3.14);
compound.setBoolean("key", true);

String str = compound.getString("key");
int num = compound.getInt("key");
double dbl = compound.getDouble("key");
boolean bool = compound.getBoolean("key");

// Nested compound
NMSNBTTagCompound nested = compound.getCompound("nested");
compound.set("nested", nestedCompound);

// Remove
compound.remove("key");

// Check existence
boolean has = compound.hasKey("key");
```

---

## Particle NMS

### ParticleNativeAPI Integration

```java
import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.plugin.ParticleNativePlugin;

public class ParticleAPI {
    public static void sendParticle(Location location, String particle) {
        ParticleNativeAPI api = ParticleNativePlugin.getAPI();

        ParticleList_1_13 particles = api.LIST_1_13;

        try {
            Field field = particles.getClass().getField(particle);
            ParticleType type = (ParticleType) field.get(particles);

            type.packet(true, location, 0, 0, 0, 0.1, 1)
                .sendInRadiusTo(Bukkit.getOnlinePlayers(), 32);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

---

## See Also

- [ARCHITECTURE.md](ARCHITECTURE.md) - System architecture
- [API.md](API.md) - Developer API reference
- [ITEM-SYSTEM.md](ITEM-SYSTEM.md) - Item system and NBT usage
