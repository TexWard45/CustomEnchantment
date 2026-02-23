# Command Module Summary

**Package:** `com.bafmc.customenchantment.command` | **Classes:** 31 | **Init Phase:** 3
**Purpose:** 9 root commands, 20+ subcommands

## Key Classes

### CommandOpen (309 lines)
**Type:** class
**Purpose:** Subcommand: open menu
**Fields:** `AdvancedCommandExecutor storageExecutor`, `AdvancedCommandExecutor headExecutor`, `AdvancedCommandExecutor bookExecutor`, `AdvancedTabCompleter storageTab`, `AdvancedTabCompleter headTab`, `AdvancedTabCompleter groupTab`
**Key Methods:**
- `onCommand(CommandSender sender, Argument arg)` -> `boolean`: 
- `onCommand(CommandSender sender, Argument arg)` -> `boolean`: 
- `onCommand(CommandSender sender, Argument arg)` -> `boolean`: 
- `onCommand(CommandSender sender, Argument arg)` -> `boolean`: 
- `onTabComplete(CommandSender arg0, Argument arg1)` -> `List<String>`: 
- `onTabComplete(CommandSender arg0, Argument arg1)` -> `List<String>`: 
- `onTabComplete(CommandSender arg0, Argument arg1)` -> `List<String>`: 
- `onTabComplete(CommandSender arg0, Argument arg1)` -> `List<String>`: 

### CommandGiveItem (275 lines)
**Type:** class
**Purpose:** Subcommand: give item
**Fields:** `AdvancedCommandExecutor giveExecutor`, `AdvancedTabCompleter nameTab`, `AdvancedTabCompleter bookLevelTab`, `AdvancedTabCompleter levelTab`, `AdvancedTabCompleter successTab`, `AdvancedTabCompleter destroyTab` (+2 more)
**Key Methods:**
- `onCommand(CommandSender sender, Argument arg)` -> `boolean`: 
- `onTabComplete(CommandSender sender, Argument arg)` -> `List<String>`: 
- `onTabComplete(CommandSender sender, Argument arg)` -> `List<String>`: 
- `onTabComplete(CommandSender sender, Argument arg)` -> `List<String>`: 
- `onTabComplete(CommandSender sender, Argument arg)` -> `List<String>`: 
- `onTabComplete(CommandSender sender, Argument arg)` -> `List<String>`: 
- `onTabComplete(CommandSender sender, Argument arg)` -> `List<String>`: 
- `onTabComplete(CommandSender sender, Argument arg)` -> `List<String>`: 

### CommandNameTag (172 lines)
**Type:** class
**Purpose:** /nametag handler
**Fields:** `AdvancedTabCompleter typeTab`, `AdvancedCommandExecutor help1Command`, `AdvancedCommandExecutor help2Command`, `AdvancedCommandExecutor help3Command`, `AdvancedCommandExecutor showCommand`, `AdvancedCommandExecutor setCommand` (+1 more)
**Key Methods:**
- `onTabComplete(CommandSender arg0, Argument arg1)` -> `List<String>`: 
- `onCommand(CommandSender sender, Argument arg)` -> `boolean`: 
- `onCommand(CommandSender sender, Argument arg)` -> `boolean`: 
- `onCommand(CommandSender sender, Argument arg)` -> `boolean`: 
- `onCommand(CommandSender sender, Argument arg)` -> `boolean`: 
- `onCommand(CommandSender sender, Argument arg)` -> `boolean`: 
- `onCommand(CommandSender sender, Argument arg)` -> `boolean`: 
- `onRegister(AdvancedCommandBuilder builder)`: 

### CommandModule (63 lines)
**Type:** class
**Purpose:** PluginModule — command registration
**Key Methods:**
- `onEnable()`: 

### ArtifactUpgradeCommand (54 lines)
**Type:** class
**Purpose:** /artifactupgrade handler
**Fields:** `CustomEnchantment plugin`
**Key Methods:**
- `onCommand(CommandSender sender, Argument arg)` -> `boolean`: 

### BookCraftCommand (51 lines)
**Type:** class
**Purpose:** /bookcraft handler
**Fields:** `CustomEnchantment plugin`
**Key Methods:**
- `onCommand(CommandSender sender, Argument arg)` -> `boolean`: 

### BookUpgradeCommand (54 lines)
**Type:** class
**Purpose:** /bookupgrade handler
**Fields:** `CustomEnchantment plugin`
**Key Methods:**
- `onCommand(CommandSender sender, Argument arg)` -> `boolean`: 

### TinkererCommand (69 lines)
**Type:** class
**Purpose:** /tinkerer handler
**Fields:** `CustomEnchantment plugin`
**Key Methods:**
- `onCommand(CommandSender sender, Argument arg)` -> `boolean`: 

## Other Classes (23)

- **CommandAddEnchant** (107L): Subcommand: add enchant
- **CommandAddGem** (77L): Subcommand: add gem
- **CommandAdmin** (51L): Subcommand: admin ops
- **CommandClearTime** (55L): Subcommand: clear cooldowns
- **CommandDebug** (51L): Subcommand: debug feature
- **CommandDisableHelmet** (51L): Subcommand: disable helmet
- **CommandFilterEnchant** (120L): /cefilter handler
- **CommandInfo** (53L): Subcommand: show info
- **CommandRemoveGem** (83L): Subcommand: remove gem
- **CommandTest** (51L): Subcommand: test features
- **CommandUseItem** (58L): Subcommand: use item
- **CEAnvilCommand** (47L): /ceanvil handler
- **EquipmentCommand** (42L): /equipment handler
- **CustomEnchantmentCommand** (36L): /customenchantment main handler
- **CommandAddItem** (40L): Subcommand: add CE item
- **CommandDebugAll** (31L): Subcommand: debug all
- **CommandDebugCE** (50L): Subcommand: debug CE enchants
- **CommandEnableHelmet** (46L): Subcommand: enable helmet
- **CommandFullChance** (45L): Subcommand: test chances
- **CommandReload** (33L): Subcommand: reload config
- **CommandRemoveEnchant** (49L): Subcommand: remove enchant
- **CommandRemoveItem** (38L): Subcommand: remove item
- **CommandUpdateItem** (45L): Subcommand: update item
