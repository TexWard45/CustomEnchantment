import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.Test;

public class MainTest {
//    @Test
//    public void onTest() {
//        Map<Integer, Integer> levelCountMap = new LinkedHashMap<>();
//
//        double sigma = 0.3;
//
//        levelCountMap.put(1, 0);
//        levelCountMap.put(2, 0);
//        levelCountMap.put(3, 0);
//        levelCountMap.put(4, 0);
//        levelCountMap.put(5, 0);
//        levelCountMap.put(6, 0);
//        levelCountMap.put(7, 0);
//        levelCountMap.put(8, 0);
//        levelCountMap.put(9, 0);
//        levelCountMap.put(10, 0);
//
//        System.out.println("Open 100 Books");
//        for (int i = 0; i < 100; i++) {
//            double rate = StdRandom.gaussian(0.0, sigma);
//            rate = Math.abs(rate);
//
//            rate = Math.min(rate, 1.0);
//
//            double level = Math.ceil(rate * 10);
//            levelCountMap.put((int) level, levelCountMap.get((int) level) + 1);
//        }
//        System.out.println(levelCountMap);
//        System.out.println("Open 1000 Books");
//        levelCountMap.clear();
//        levelCountMap.put(1, 0);
//        levelCountMap.put(2, 0);
//        levelCountMap.put(3, 0);
//        levelCountMap.put(4, 0);
//        levelCountMap.put(5, 0);
//        levelCountMap.put(6, 0);
//        levelCountMap.put(7, 0);
//        levelCountMap.put(8, 0);
//        levelCountMap.put(9, 0);
//        levelCountMap.put(10, 0);
//        for (int i = 0; i < 1000; i++) {
//            double rate = StdRandom.gaussian(0.0, sigma);
//            rate = Math.abs(rate);
//
//            rate = Math.min(rate, 1.0);
//
//            double level = Math.ceil(rate * 10);
//            levelCountMap.put((int) level, levelCountMap.get((int) level) + 1);
//        }
//        System.out.println(levelCountMap);
//        System.out.println("Open 10000 Books");
//        levelCountMap.clear();
//        levelCountMap.put(1, 0);
//        levelCountMap.put(2, 0);
//        levelCountMap.put(3, 0);
//        levelCountMap.put(4, 0);
//        levelCountMap.put(5, 0);
//        levelCountMap.put(6, 0);
//        levelCountMap.put(7, 0);
//        levelCountMap.put(8, 0);
//        levelCountMap.put(9, 0);
//        levelCountMap.put(10, 0);
//        for (int i = 0; i < 10000; i++) {
//            double rate = StdRandom.gaussian(0.0, sigma);
//            rate = Math.abs(rate);
//
//            rate = Math.min(rate, 1.0);
//
//            double level = Math.ceil(rate * 10);
//            levelCountMap.put((int) level, levelCountMap.get((int) level) + 1);
//        }
//
//
//        System.out.println(levelCountMap);
//
//        System.out.println(levelCountMap);
//        System.out.println("Open 100000 Books");
//        levelCountMap.clear();
//        levelCountMap.put(1, 0);
//        levelCountMap.put(2, 0);
//        levelCountMap.put(3, 0);
//        levelCountMap.put(4, 0);
//        levelCountMap.put(5, 0);
//        levelCountMap.put(6, 0);
//        levelCountMap.put(7, 0);
//        levelCountMap.put(8, 0);
//        levelCountMap.put(9, 0);
//        levelCountMap.put(10, 0);
//        for (int i = 0; i < 100000; i++) {
//            double rate = StdRandom.gaussian(0.0, sigma);
//            rate = Math.abs(rate);
//
//            rate = Math.min(rate, 1.0);
//
//            double level = Math.ceil(rate * 10);
//            levelCountMap.put((int) level, levelCountMap.get((int) level) + 1);
//        }
//
//
//        System.out.println(levelCountMap);
//    }
//
//    @Test
//    public void onLevel() {
//        for (int i = 1; i < 20; i++) {
//            System.out.println(i * 40 + " " + ExpUtils.getExpToLevel(i * 40));
//        }
//    }

    @Test
    public void onMask() {
        for (EntityType entityType : EntityType.values()) {
            System.out.println("  - '" + entityType.name() + "'");
        }
//        File file = new File("src/test/resources/mask.yml");
//        AdvancedFileConfiguration config = new AdvancedFileConfiguration(file);
//
//        for (String key : config.getKeys(false)) {
//            String path = key + ".levels";
//
//            try {
//                for (String level : config.getConfigurationSection(path).getKeys(false)) {
//                    String levelPath = path + "." + level;
//
//                    for (String functionName : config.getConfigurationSection(levelPath).getKeys(false)) {
//                        String functionPath = levelPath + "." + functionName;
//
//                        String typePath = functionPath + ".type";
//                        String type = config.getString(typePath);
//
//                        if (!type.equals("ARMOR_EQUIP") && !type.equals("ARMOR_UNDRESS") && !type.equals("HOLD") && !type.equals("CHANGE_HAND")) {
//                            List<String> conditions = config.getStringList(functionPath + ".conditions");
//                            if (conditions.isEmpty()) {
//                                conditions.add("EQUIP_SLOT:MAINHAND");
//                            } else if (!conditions.contains("EQUIP_SLOT:MAINHAND")) {
//                                conditions.add(0, "EQUIP_SLOT:MAINHAND");
//                            }
//
//                            // remove empty
//                            conditions.removeIf(String::isEmpty);
//
//                            config.set(functionPath + ".conditions", conditions);
//                        }
//                    }
//                }
//            } catch (NullPointerException e) {
//                System.out.println("Failed to load " + path);
//            }
//        }
//
//        config.save();
    }
}
