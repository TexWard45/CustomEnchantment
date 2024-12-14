package com.bafmc.customenchantment.utils;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeOperation;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeSlot;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;

public class AttributeUtils {
    public static NMSAttribute createAttribute(String format) {
        return createAttribute(format, "");
    }

    public static NMSAttribute createAttribute(String format, String prefix) {
        String[] split = format.split(",");

        NMSAttribute.Builder builder = NMSAttribute.newBuilder();
        for (String string : split) {
            try {
                String[] data = string.split("=");

                String key = data[0];
                String value = data[1];

                switch (key) {
                    case "type":
                        NMSAttributeType type = NMSAttributeType.valueOf(value);
                        builder.type(type);
                        builder.name(String.valueOf(System.nanoTime()));
                        break;
                    case "name":
                        builder.name(prefix + value);
                        break;
                    case "operation":
                        builder.operation(NMSAttributeOperation.fromId(Integer.valueOf(value)));
                        break;
                    case "amount":
                        builder.amount(Double.valueOf(value));
                        break;
                    case "slot":
                        builder.slot(NMSAttributeSlot.valueOf(value.toUpperCase()));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return builder.build();
    }
}
