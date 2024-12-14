package com.bafmc.customenchantment;

public class MobSlayerCalculator {
    public static void main(String[] args) {
        long xpNeed = getGeneralXpToLevel(2000, 10, 10);

        System.out.println(getLevel(xpNeed, 100, 20));
    }

    public static long getGeneralXpToLevel(long level, long baseXp, long increment) {
        return (long) (baseXp * level + increment * Math.pow(level, 2));
    }

    public static long getLevel(long xp, long baseXp, long increment) {
        return (long) ((-baseXp + Math.sqrt(Math.pow(baseXp, 2) - 4 * increment * -xp)) / (2 * increment));
    }
}
