package com.bafmc.customenchantment.player;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handle attribute from player. Only handle attribute modifier which name
 * starts with specific prefix in variable PlayerAttribute.PREFIX.
 * 
 * @author nhata
 *
 */
public class PlayerVanillaAttribute extends CEPlayerExpansion {
	public static final List<Attribute> ATTRIBUTE_LIST = Arrays.asList(Attribute.GENERIC_ARMOR,
			Attribute.GENERIC_ARMOR_TOUGHNESS, Attribute.GENERIC_ATTACK_DAMAGE, Attribute.GENERIC_ATTACK_SPEED,
			Attribute.GENERIC_KNOCKBACK_RESISTANCE, Attribute.GENERIC_LUCK, Attribute.GENERIC_MAX_HEALTH,
			Attribute.GENERIC_MOVEMENT_SPEED);
	public static final String PREFIX = "ce-";
	public Multimap<Attribute, NMSAttribute> attributeMap = LinkedHashMultimap.create();

	public PlayerVanillaAttribute(CEPlayer cePlayer) {
		super(cePlayer);
	}

	public void onJoin() {
		clearAllAttribute();
	}

	public void onQuit() {
		clearAllAttribute();
	}

	/**
	 * Add attribute to player.
	 * 
	 * @param attribute type of attribute
	 * @param name      unique name of attribute (auto add PREFIX at head if not
	 *                  already available)
	 * @param amount    amount of attribute
	 * @param operation type of operation
	 * @return true if added, false if not
	 */
	public boolean addAttribute(Attribute attribute, String name, double amount, Operation operation) {
		name = getPrefix(name);
		name = name.toLowerCase(); // Must be lower case because of NameSpaceKey validation

		AttributeInstance aInstance = player.getAttribute(attribute);
		AttributeModifier aModifier = new AttributeModifier(NamespacedKey.fromString(name), amount, operation);

		if (isDuplicateAttributeModifier(attribute, aModifier)) {
			return false;
		}

		aInstance.addModifier(new AttributeModifier(NamespacedKey.fromString(name), amount, operation));
		return true;
	}

	/**
	 * Remove attribute from player.
	 * 
	 * @param attribute type of attribute
	 * @param name      unique name of attribute (auto add PREFIX at head if not
	 *                  already available)
	 * @return true if removed, false if not
	 */
	public boolean removeAttribute(Attribute attribute, String name) {
		name = getPrefix(name);
		name = name.toLowerCase();

		AttributeModifier aModifier = getAttributeModifier(attribute, name);

		if (aModifier == null) {
			return false;
		}

		player.getAttribute(attribute).removeModifier(aModifier);
		return true;
	}

	/**
	 * Clear specific attribute from player.
	 * 
	 * @param attribute type of attribute
	 */
	public void clearAttribute(Attribute attribute) {
		AttributeInstance aInstance = player.getAttribute(attribute);

		List<AttributeModifier> aModifiers = getAttributeModifiers(attribute);
		for (AttributeModifier aModifier : aModifiers) {
			aInstance.removeModifier(aModifier);
		}
	}

	/**
	 * Clear all attribute from player.
	 * 
	 */
	public void clearAllAttribute() {
		for (Attribute attribute : Attribute.values()) {
			clearAttribute(attribute);
		}
	}

	/**
	 * Clear attribute modifier by type of attribute and unique name.
	 * 
	 * @param attribute type of attribute
	 * @param name      unique name of attribute (auto add PREFIX at head if not
	 *                  already available)
	 * @return modifier of attribute by name
	 */
	public AttributeModifier getAttributeModifier(Attribute attribute, String name) {
		name = getPrefix(name);

		for (AttributeModifier aModifier : player.getAttribute(attribute).getModifiers()) {
			if (aModifier.getName().equals(name)) {
				return aModifier;
			}
		}

		return null;
	}

	/**
	 * Check if contains attribute modifier by type of attribute and unique name.
	 * 
	 * @param attribute type of attribute
	 * @param name      unique name of attribute (auto add PREFIX at head if not
	 *                  already available)
	 * @return true if contains, false if not
	 */
	public boolean hasAttributeModifier(Attribute attribute, String name) {
		return getAttributeModifier(attribute, name) != null;
	}

	/**
	 * Check if duplicate name of attribute modifier in type of attribute.
	 * 
	 * @param attribute type of attribute
	 * @param name      unique name of attribute (auto add PREFIX at head if not
	 *                  already available)
	 * @return true if duplicated, false if not
	 */
	public boolean isDuplicateAttributeModifier(Attribute attribute, AttributeModifier aModifier) {
		String name = aModifier.getName();
		return player.getAttribute(attribute).getModifiers().stream().anyMatch(a -> (a.getName().equals(name)));
	}

	/**
	 * Get list of attribute modifier by type of attribute.
	 * 
	 * @param attribute type of attribute
	 * @return list of attribute modifier
	 */
	public List<AttributeModifier> getAttributeModifiers(Attribute attribute) {
		AttributeInstance aInstance = player.getAttribute(attribute);

		if (aInstance == null) {
			return new ArrayList<>();
		}

		return new ArrayList<>(aInstance.getModifiers());
	}

	/**
	 * Get list of attribute modifier unique name by type of attribute.
	 * 
	 * @param attribute type of attribute
	 * @return list of attribute modifier unique name
	 */
	public List<String> getAttributeModifierNameList(Attribute attribute) {
		return getAttributeModifierNameList(attribute, PREFIX);
	}

	/**
	 * Get list of attribute modifier unique name by type of attribute and prefix.
	 *
	 * @param attribute type of attribute
	 * @param prefix    prefix of attribute modifier name
	 * @return list of attribute modifier unique name
	 */
	public List<String> getAttributeModifierNameList(Attribute attribute, String prefix) {
		List<String> list = new ArrayList<>();

		for (AttributeModifier aModifier : player.getAttribute(attribute).getModifiers()) {
			if (aModifier.getName().startsWith(prefix)) {
				list.add(aModifier.getName().substring(prefix.length()));
			}
		}

		return list;
	}

	/**
	 * Set prefix for name of attribute.
	 * 
	 * @param name name of attribute
	 * @return name with prefix at head
	 */
	public static String getPrefix(String name) {
		return name.contains(PREFIX) ? name : PREFIX + name;
	}
}