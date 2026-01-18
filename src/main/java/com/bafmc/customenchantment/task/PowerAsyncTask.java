package com.bafmc.customenchantment.task;

import com.bafmc.bukkit.bafframework.nms.NMSAttributeOperation;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.attribute.RangeAttribute;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerCustomAttribute;
import com.bafmc.customenchantment.player.PlayerVanillaAttribute;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class PowerAsyncTask extends BukkitRunnable {
    private CustomEnchantment plugin;
    private Workbook workbook;
    private FormulaEvaluator evaluator;
    private Sheet sheet;
    @Setter
    private boolean reloading = false;

    // Attribute mapping: row index (D3 = 2, D4 = 3, ..., D24 = 23)
    private static final LinkedHashMap<Integer, AttributeSetter> ATTRIBUTE_SETTERS = new LinkedHashMap<>();
    static {
        ATTRIBUTE_SETTERS.put(2, (player, vanilla, custom) -> player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()); // Max Health
        ATTRIBUTE_SETTERS.put(3, (player, vanilla, custom) -> player.getAbsorptionAmount()); // Max Absorption
        ATTRIBUTE_SETTERS.put(4, (player, vanilla, custom) -> custom.getValue(CustomAttributeType.HEALTH_REGENERATION)); // Health Regeneration
        ATTRIBUTE_SETTERS.put(5, (player, vanilla, custom) -> player.getAttribute(Attribute.GENERIC_ARMOR).getValue()); // Armor
        ATTRIBUTE_SETTERS.put(6, (player, vanilla, custom) -> custom.getValue(CustomAttributeType.DAMAGE_REDUCTION)); // Damage Reduction
        ATTRIBUTE_SETTERS.put(7, (player, vanilla, custom) -> player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue()); // Movement Speed
        ATTRIBUTE_SETTERS.put(8, (player, vanilla, custom) -> custom.getValue(CustomAttributeType.DODGE_CHANCE)); // Dodge Chance
        ATTRIBUTE_SETTERS.put(9, (player, vanilla, custom) -> custom.getValue(CustomAttributeType.SLOW_RESISTANCE)); // Slow Resistance
        ATTRIBUTE_SETTERS.put(10, (player, vanilla, custom) -> custom.getValue(CustomAttributeType.MAGIC_RESISTANCE)); // Magic Resistance
        ATTRIBUTE_SETTERS.put(11, (player, vanilla, custom) -> player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue()); // Attack Damage
        ATTRIBUTE_SETTERS.put(12, (player, vanilla, custom) -> player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue()); // Attack Speed
        ATTRIBUTE_SETTERS.put(13, (player, vanilla, custom) -> custom.getValue(CustomAttributeType.CRITICAL_DAMAGE)); // Critical Damage (Multiply)
        ATTRIBUTE_SETTERS.put(14, (player, vanilla, custom) -> custom.getValue(CustomAttributeType.CRITICAL_CHANCE)); // Critical Chance
        ATTRIBUTE_SETTERS.put(15, (player, vanilla, custom) -> custom.getValue(CustomAttributeType.ARMOR_PENETRATION)); // Armor Penetration
        ATTRIBUTE_SETTERS.put(16, (player, vanilla, custom) -> custom.getValue(CustomAttributeType.LIFE_STEAL)); // Life Steal
        ATTRIBUTE_SETTERS.put(17, (player, vanilla, custom) -> player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE).getValue()); // Attack Range
        ATTRIBUTE_SETTERS.put(18, (player, vanilla, custom) -> custom.getValue(CustomAttributeType.AOE_RANGE)); // Aoe Range
        ATTRIBUTE_SETTERS.put(19, (player, vanilla, custom) -> custom.getValue(CustomAttributeType.AOE_DAMAGE_RATIO)); // Aoe Damage Ratio
        ATTRIBUTE_SETTERS.put(20, (player, vanilla, custom) -> custom.getValue(CustomAttributeType.ACCURACY_CHANCE)); // Accuracy Chance
        ATTRIBUTE_SETTERS.put(21, (player, vanilla, custom) -> custom.getValue(CustomAttributeType.GRIEVOUS_WOUNDS)); // Grievous Wounds (custom, adjust if needed)
        ATTRIBUTE_SETTERS.put(22, (player, vanilla, custom) -> custom.getValue(CustomAttributeType.VULNERABILITY)); // Vulnerability
        ATTRIBUTE_SETTERS.put(23, (player, vanilla, custom) -> custom.getValue(CustomAttributeType.MINING_POWER)); // Mining Power
    }

    @FunctionalInterface
    private interface AttributeSetter {
        double get(Player player, PlayerVanillaAttribute vanilla, PlayerCustomAttribute custom);
    }

    public PowerAsyncTask(CustomEnchantment plugin) {
        this.plugin = plugin;
        this.load();
    }

    public void load() {
        try {
            if (workbook != null) {
                workbook.close();
            }
            File file = getPowerCalculatorFile();
            FileInputStream fis = new FileInputStream(file);
            this.workbook = new XSSFWorkbook(fis);
            this.evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            this.sheet = workbook.getSheetAt(0);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        if (reloading) {
            load();

            reloading = false;
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            run(player);
        }
    }

    // Set of row indices (D3=2, D4=3, ...) that are percentage-based attributes
    private static final Set<Integer> PERCENTAGE_ROWS = new HashSet<>();
    static {
        // Fill with row indices of percentage attributes
        PERCENTAGE_ROWS.add(6);  // Damage Reduction
        PERCENTAGE_ROWS.add(8);  // Dodge Chance
        PERCENTAGE_ROWS.add(9);  // Slow Resistance
        PERCENTAGE_ROWS.add(10); // Magic Resistance
        PERCENTAGE_ROWS.add(14); // Critical Chance
        PERCENTAGE_ROWS.add(15); // Armor Penetration
        PERCENTAGE_ROWS.add(16); // Life Steal
        PERCENTAGE_ROWS.add(19); // Aoe Damage Ratio
        PERCENTAGE_ROWS.add(20); // Accuracy Chance
        PERCENTAGE_ROWS.add(21); // Grievous Wounds
        // Add or remove as needed based on your attribute definitions
    }

    public void run(Player player) {
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);
        PlayerVanillaAttribute vanillaAttribute = cePlayer.getVanillaAttribute();
        PlayerCustomAttribute customAttribute = cePlayer.getCustomAttribute();

        if (workbook == null || evaluator == null || sheet == null) return;

        synchronized (this) {
            // Set all attributes from D3 to D24
            for (Map.Entry<Integer, AttributeSetter> entry : ATTRIBUTE_SETTERS.entrySet()) {
                double value = entry.getValue().get(player, vanillaAttribute, customAttribute);
                // If this row is a percentage, convert 0-100 to 0-1
                if (PERCENTAGE_ROWS.contains(entry.getKey())) {
                    value = value / 100.0;
                }
                setCellValue(entry.getKey(), 3, value);
            }

            evaluator.clearAllCachedResultValues();
            evaluator.evaluateAll();

            double defensePower = Math.round(readEvaluatedCell(sheet, evaluator, 24, 11)); // L25
            double attackPower = Math.round(readEvaluatedCell(sheet, evaluator, 25, 11));  // L26

            customAttribute.addCustomAttribute("TOTAL_POWER",
                    new RangeAttribute(CustomAttributeType.TOTAL_POWER, attackPower + defensePower, NMSAttributeOperation.ADD_NUMBER));
            customAttribute.addCustomAttribute("ATK_POWER",
                    new RangeAttribute(CustomAttributeType.ATK_POWER, attackPower, NMSAttributeOperation.ADD_NUMBER));
            customAttribute.addCustomAttribute("DEF_POWER",
                    new RangeAttribute(CustomAttributeType.DEF_POWER, defensePower, NMSAttributeOperation.ADD_NUMBER));
        }
    }

    private void setCellValue(int rowIdx, int colIdx, double value) {
        Row row = sheet.getRow(rowIdx);
        if (row == null) row = sheet.createRow(rowIdx);
        Cell cell = row.getCell(colIdx);
        if (cell == null) cell = row.createCell(colIdx);
        cell.setCellValue(value);
    }

    static double readEvaluatedCell(Sheet sheet, FormulaEvaluator evaluator, int row, int col) {
        Row r = sheet.getRow(row);
        if (r == null) return 0;
        Cell c = r.getCell(col);
        if (c == null) return 0;
        CellValue cv = evaluator.evaluate(c);
        return cv != null ? cv.getNumberValue() : 0;
    }

    public File getPowerCalculatorFile() {
        return new File(plugin.getDataFolder(), "PowerCalculator.xlsx");
    }

    public void close() {
        try {
            if (workbook != null) {
                workbook.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        close();
        super.cancel();
    }
}
