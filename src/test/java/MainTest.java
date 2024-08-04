import me.texward.texwardlib.util.GaussianChance;
import me.texward.texwardlib.util.StdRandom;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainTest {
    @Test
    public void onTest() {
        Map<Integer, Integer> levelCountMap = new LinkedHashMap<>();

        double sigma = 0.01;

        levelCountMap.put(1, 0);
        levelCountMap.put(2, 0);
        levelCountMap.put(3, 0);
        levelCountMap.put(4, 0);
        levelCountMap.put(5, 0);
        levelCountMap.put(6, 0);
        levelCountMap.put(7, 0);
        levelCountMap.put(8, 0);
        levelCountMap.put(9, 0);
        levelCountMap.put(10, 0);

        System.out.println("Open 100 Books");
        for (int i = 0; i < 100; i++) {
            double rate = StdRandom.gaussian(0.0, sigma);
            rate = Math.abs(rate);

            rate = Math.min(rate, 1.0);

            double level = Math.ceil(rate * 10);
            levelCountMap.put((int) level, levelCountMap.get((int) level) + 1);
        }
        System.out.println(levelCountMap);
        System.out.println("Open 1000 Books");
        levelCountMap.clear();
        levelCountMap.put(1, 0);
        levelCountMap.put(2, 0);
        levelCountMap.put(3, 0);
        levelCountMap.put(4, 0);
        levelCountMap.put(5, 0);
        levelCountMap.put(6, 0);
        levelCountMap.put(7, 0);
        levelCountMap.put(8, 0);
        levelCountMap.put(9, 0);
        levelCountMap.put(10, 0);
        for (int i = 0; i < 1000; i++) {
            double rate = StdRandom.gaussian(0.0, sigma);
            rate = Math.abs(rate);

            rate = Math.min(rate, 1.0);

            double level = Math.ceil(rate * 10);
            levelCountMap.put((int) level, levelCountMap.get((int) level) + 1);
        }
        System.out.println(levelCountMap);
        System.out.println("Open 10000 Books");
        levelCountMap.clear();
        levelCountMap.put(1, 0);
        levelCountMap.put(2, 0);
        levelCountMap.put(3, 0);
        levelCountMap.put(4, 0);
        levelCountMap.put(5, 0);
        levelCountMap.put(6, 0);
        levelCountMap.put(7, 0);
        levelCountMap.put(8, 0);
        levelCountMap.put(9, 0);
        levelCountMap.put(10, 0);
        for (int i = 0; i < 10000; i++) {
            double rate = StdRandom.gaussian(0.0, sigma);
            rate = Math.abs(rate);

            rate = Math.min(rate, 1.0);

            double level = Math.ceil(rate * 10);
            levelCountMap.put((int) level, levelCountMap.get((int) level) + 1);
        }


        System.out.println(levelCountMap);

        System.out.println(levelCountMap);
        System.out.println("Open 100000 Books");
        levelCountMap.clear();
        levelCountMap.put(1, 0);
        levelCountMap.put(2, 0);
        levelCountMap.put(3, 0);
        levelCountMap.put(4, 0);
        levelCountMap.put(5, 0);
        levelCountMap.put(6, 0);
        levelCountMap.put(7, 0);
        levelCountMap.put(8, 0);
        levelCountMap.put(9, 0);
        levelCountMap.put(10, 0);
        for (int i = 0; i < 100000; i++) {
            double rate = StdRandom.gaussian(0.0, sigma);
            rate = Math.abs(rate);

            rate = Math.min(rate, 1.0);

            double level = Math.ceil(rate * 10);
            levelCountMap.put((int) level, levelCountMap.get((int) level) + 1);
        }


        System.out.println(levelCountMap);
    }
}
