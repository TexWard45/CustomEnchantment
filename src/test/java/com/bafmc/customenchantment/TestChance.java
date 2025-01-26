package com.bafmc.customenchantment;

import org.junit.jupiter.api.Test;

public class TestChance {
    @Test
    public void test() {
        // Test random 1 roll and random 3 roll with same chance
        //Total Probability (%)	Number of Roll(s)	Roll Win In A Rows Chance
        //75	3	=pow(A2/100,1/B2)*100

        int roll = 3;
        double chance = 0.1;

        double winTotal = 0;
        int winCount = 0;

        double win3Total = 0;
        int win3Count = 0;

        for (int i = 0; i < 100; i++) {
            // 1 roll with 10000 times
            int win = 0;
            for (int j = 0; j < 10000; j++) {
                if (Math.random() < chance) {
                    win++;
                }
            }

            // 3 roll with 10000 times
            chance = Math.pow(chance, 1.0 / roll);
            int win3 = 0;
            for (int j = 0; j < 10000; j++) {
                if (Math.random() < chance) {
                    if (Math.random() < chance) {
                        if (Math.random() < chance) {
                            win3++;
                        }
                    }
                }
            }

            winTotal += win;
            win3Total += win3;
            winCount++;
            win3Count++;
        }

        System.out.println("1 roll win: " + winTotal / winCount);
        System.out.println("3 roll win: " + win3Total / win3Count);
    }
}
