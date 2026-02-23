# Config Schema Map — CustomEnchantment

## Primary Configs

| YAML File | Java Class | Key Paths | Module |
|-----------|------------|-----------|--------|
| config.yml | MainConfig | event.move-period, event.sneak-period, combat.time, combat.staff-min-required-attack-strength-scale, combat.require-weapon, unbreakable-armor.enable, outfit.extra-slot, max-extra-slot-use-count, extra-slot-settings.* | ConfigModule |
| messages.yml | CustomEnchantmentMessage | Message templates (via AdvancedFileConfiguration) | ConfigModule |
| book-craft.yml | BookCraftConfig | money-require.* (Map\<String, Double\>) | ConfigModule |
| book-upgrade.yml | BookUpgradeConfig | Book upgrade recipes | ConfigModule |
| tinkerer.yml | TinkererConfig | Tinkerer menu config | ConfigModule |
| artifact-upgrade.yml | ArtifactUpgradeConfig | Artifact upgrade recipes | ConfigModule |
| groups.yml | CEEnchantGroupConfig | Enchantment group definitions | ConfigModule |
| artifact-groups.yml | CEArtifactGroupConfig | Artifact group definitions | ConfigModule |
| sigil-groups.yml | CESigilGroupConfig | Sigil group definitions | ConfigModule |
| outfit-groups.yml | CEOutfitGroupConfig | Outfit group definitions | ConfigModule |
| items.yml | CEItemConfig | Custom item definitions | ConfigModule |

## Bulk-Loaded Config Folders

| Folder | Java Class | Purpose | File Pattern |
|--------|------------|---------|-------------|
| enchantment/ | CEEnchantConfig | Enchantment definitions | all.yml, armor.yml, sword.yml, bow.yml, axe.yml, pickaxe.yml, hoe.yml, trident.yml, boots.yml, helmet.yml, chestplate.yml, leggings.yml, mask.yml, artifact*.yml |
| artifact/ | CEItemConfig (artifact loader) | Artifact items by rarity | _1common.yml, _2rare.yml, _3epic.yml, _4legendary.yml, _5supreme.yml, _6ultimate.yml, _7event.yml, _8limit.yml |
| book-upgrade/ | BookUpgradeConfig | Individual upgrade configs | Per-upgrade files |
| weapon/ | CEWeaponConfig | Weapon definitions | Per-weapon files |
| outfit/ | CEOutfitConfig | Outfit definitions | Per-outfit files |
| skin/ | CESkinConfig | Skin/appearance configs | Per-skin files |
| menu/ | CustomMenu integration | Menu UI definitions | ce-menu.yml, ce-shop.yml, ce-trade-*.yml, book-craft.yml, etc. |

## Data Files

| File/Folder | Purpose | Loaded By |
|-------------|---------|-----------|
| storage/save-items.yml | Persistent item storage | CEItemStorageMap |
| data/player/\<name\>.yml | Per-player data | PlayerModule |

## Config Loading Order (ConfigModule.onReload)

1. Create all folders (enchantment, menu, data, etc.)
2. Create all config files
3. Load messages (CustomEnchantmentMessage)
4. Load MainConfig
5. Load BookCraftConfig
6. Load group configs (enchant, artifact, sigil, outfit)
7. Load enchant configs (CEEnchantConfig from enchantment/ folder)
8. Load item configs (CEItemConfig + bulk loaders)
9. Load VanillaItemConfig
10. Load BookUpgradeConfig
11. Load TinkererConfig
12. Load ArtifactUpgradeConfig

## Key @Configuration Classes

| Class | Annotation | Notable Fields |
|-------|-----------|----------------|
| MainConfig | @Configuration | @Path fields for all main settings |
| BookCraftConfig | @Configuration | @Path("money-require") Map\<String, Double\> |
| ExtraSlotSettingsData | @Configuration | @Path maxCount, list, slots |
| CEEnchantConfig | @Configuration | Enchantment definitions per file |
| CEEnchantGroupConfig | @Configuration | Group→enchantment mappings |
