package com.bafmc.customenchantment;

import com.bafmc.bukkit.utils.ExpUtils;
import com.bafmc.bukkit.utils.SoundUtils;
import org.junit.jupiter.api.Test;

public class XpTest {
    @Test
    public void main() {
        for (int i = 1; i <= 100; i++) {
            int xp = ExpUtils.getExpToLevel(i * 20);

            System.out.println(xp);
        }
    }
}
