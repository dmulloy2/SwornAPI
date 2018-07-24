/**
 * SwornAPI - common API for MineSworn and Shadowvolt plugins
 * Copyright (C) 2016 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.types;

import java.util.Collection;

import net.dmulloy2.util.FormatUtil;

import org.apache.commons.lang.Validate;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lombok.Getter;

/**
 * Represents various potion types with friendlier configuration and display names.
 * @author dmulloy2
 */
@Getter
public enum PotionType
{
	AWKWARD("Awkward"),
	FIRE_RESISTANCE("Fire Resistance", "fireres"),
	INSTANT_DAMAGE("Harming", "damage"),
	INSTANT_HEAL("Healing", "heal"),
	INVISIBILITY("Invisibility", "invis"),
	JUMP("Leaping", "jump"),
	LUCK("Luck", "luck"),
	MUNDANE("Mundane"),
	NIGHT_VISION("Night Vision", "nvg"),
	POISON("Poison"),
	REGEN("Regeneration", "regen"),
	SLOWNESS("Slowness", "slow"),
	SPEED("Swiftness", "swift", "speed"),
	STRENGTH("Strength"),
	THICK("Thick"),
	WATER("Water"),
	WATER_BREATHING("Water Breathing", "waterbreath"),
	WEAKNESS("Weakness", "weak");

	private final String name;
	private final String[] aliases;

	PotionType(String name, String... aliases)
	{
		this.name = name;
		this.aliases = aliases;
	}

	/**
	 * Gets the Minecraft Potion display name of this type.
	 * @return The display name
	 */
	public String getPotionDisplay()
	{
		switch (this)
		{
			case AWKWARD:
			case MUNDANE:
				return name + " Potion";
			case WATER:
				return name;
			default:
				return "Potion of " + name;
		}
	}

	/**
	 * Gets the Minecraft Effect display name of this type.
	 * @return The display name
	 */
	public String getEffectDisplay()
	{
		return name;
	}

	/**
	 * Gets the Bukkit PotionType equivalent of this PotionType
	 * @return The Bukkit equivalent
	 */
	public org.bukkit.potion.PotionType getBukkit()
	{
		return org.bukkit.potion.PotionType.valueOf(name());
	}

	/**
	 * Gets the Bukkit PotionEffectType equivalent of this PotionType.
	 * An equivalent does not exist for every PotionType.
	 * @return The Bukkit equivalent, or null if it does not exist
	 */
	public PotionEffectType getEffectType()
	{
		return getBukkit().getEffectType();
	}

	// ---- Finders

	/**
	 * Finds the Bukkit PotionType from a given string, falling back to enum names
	 * if no PotionType exists.
	 * @param string String to parse
	 * @return The PotionType, or null if none could be found
	 */
	public static org.bukkit.potion.PotionType findPotion(String string)
	{
		PotionType type = find(string);
		if (type != null)
			return type.getBukkit();

		try
		{
			return org.bukkit.potion.PotionType.valueOf(
				string.toUpperCase().replace(" ", "_"));
		} catch (IllegalArgumentException ex) { }
		return null;
	}

	/**
	 * Finds the Bukkit PotionEffectType from a given string, falling back to
	 * {@link PotionEffectType#getByName(String)} if no PotionType exists.
	 * @param string String to parse
	 * @return The PotionEffectType, or null if none could be found
	 */
	public static PotionEffectType findEffect(String string)
	{
		PotionType type = find(string);
		if (type != null)
		{
			PotionEffectType effect = type.getEffectType();
			if (effect != null)
				return effect;
		}

		return PotionEffectType.getByName(string.replace(" ", "_"));
	}

	/**
	 * Finds a PotionType from a given string matcher
	 * @param matcher String matcher
	 * @return The PotionType, or null if none exists
	 */
	public static PotionType find(String matcher)
	{
		Validate.notNull(matcher, "matcher cannot be null!");
		matcher = matcher.toLowerCase();

		for (PotionType type : values())
		{
			if (type.getName().equals(matcher)
					|| type.name().toLowerCase().equals(matcher)
					|| containsIgnoreCase(matcher, type.getAliases()))
				return type;
		}

		return null;
	}

	private static boolean containsIgnoreCase(String lookup, String[] array)
	{
		for (String string : array)
		{
			if (string.equalsIgnoreCase(lookup))
				return true;
		}

		return false;
	}

	/**
	 * Returns a String representation of a {@link Collection} of
	 * PotionEffects
	 *
	 * @param effects Collection of potion effects.
	 */
	// TODO Figure out a good way to use our strings
	public static String toString(Collection<PotionEffect> effects)
	{
		Validate.notNull(effects, "effects cannot be null!");

		StringJoiner joiner = new StringJoiner(", ");
		for (PotionEffect effect : effects)
		{
			joiner.append(FormatUtil.getFriendlyName(effect.getType()));
		}

		return joiner.toString();
	}
}