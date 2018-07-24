/**
 * SwornAPI - common API for MineSworn and Shadowvolt plugins
 * Copyright (C) 2015 dmulloy2
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

import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.util.FormatUtil;

import lombok.Getter;

import org.bukkit.enchantments.Enchantment;

/**
 * Represents various enchantments with friendlier configuration and display names.
 * @author dmulloy2
 */
@Getter
public enum EnchantmentType
{
	ARROW_DAMAGE("Power", "arrowdmg"),
	ARROW_FIRE("Flame", "fire"),
	ARROW_INFINITE("Infinity", "inf"),
	ARROW_KNOCKBACK("Punch", "arrowkb"),
	BINDING_CURSE("Curse of Bind", "bind", "binding"),
	DAMAGE_ALL("Sharpness", "sharp"),
	DAMAGE_ARTHROPODS("Bane of Arthropods", "bane"),
	DAMAGE_UNDEAD("Smite"),
	DEPTH_STRIDER("Depth Strider", "depthstrider", "depth"),
	DIG_SPEED("Efficiency", "eff"),
	DURABILITY("Durability", "dura"),
	FIRE_ASPECT("Fire Aspect", "fireaspect"),
	FROST_WALKER("Frost Walker", "frostwalker"),
	KNOCKBACK("Knockback"),
	LOOT_BONUS_BLOCKS("Fortune"),
	LOOT_BONUS_MOBS("Looting"),
	LUCK("Luck"),
	LOOT("Loot"),
	LURE("Lure"),
	MENDING("Mending", "mend"),
	OXYGEN("Respiration", "breathing"),
	PROTECTION_ENVIRONMENTAL("Protection", "prot"),
	PROTECTION_EXPLOSIONS("Blast Protection", "blast"),
	PROTECTION_FALL("Feather Falling", "feather"),
	PROTECTION_FIRE("Fire Protection", "fireprot"),
	PROTECTION_PROJECTILE("Projectile Protection", "proj"),
	SILK_TOUCH("Silk Touch", "silk"),
	SWEEPING_EDGE("Sweeping Edge", "sweep"),
	THORNS("Thorns"),
	VANISHING_CURSE("Vanishing Curse", "vanish"),
	WATER_WORKER("Aqua Affinity", "aqua", "affinity");

	private String name;
	private String[] aliases;

	EnchantmentType(String name, String... aliases)
	{
		this.name = name;
		this.aliases = aliases;
	}

	/**
	 * Returns a friendlier name of a given {@link Enchantment}.
	 *
	 * @param enchant {@link Enchantment}
	 * @return Friendlier name.
	 */
	public static String toName(Enchantment enchant)
	{
		EnchantmentType type = getByName(enchant.getName());
		if (type != null)
			return type.getName();

		return FormatUtil.getFriendlyName(enchant);
	}

	/**
	 * Attempts to get an {@link Enchantment} from a given string.
	 *
	 * @param enchant Enchantment name
	 * @return The enchantment, or null if none exists.
	 */
	public static Enchantment toEnchantment(String enchant)
	{
		enchant = enchant.replaceAll(" ", "_");
		enchant = enchant.toUpperCase();

		EnchantmentType type = getByName(enchant);
		if (type != null)
			return Enchantment.getByName(type.name());

		return Enchantment.getByName(enchant);
	}

	/**
	 * Returns a <code>String</code> representation of a {@link Map} of
	 * enchantments.
	 *
	 * @param enchantments Enchantment map
	 */
	public static String toString(Map<Enchantment, Integer> enchantments)
	{
		StringJoiner joiner = new StringJoiner(", ");
		for (Entry<Enchantment, Integer> entry : enchantments.entrySet())
		{
			joiner.append(EnchantmentType.toName(entry.getKey()) + ":" + entry.getValue());
		}

		return joiner.toString();
	}

	// ---- Utility Methods

	private static EnchantmentType getByName(String name)
	{
		for (EnchantmentType type : values())
		{
			if (type.getName().equalsIgnoreCase(name)
					|| type.name().equalsIgnoreCase(name)
					|| containsIgnoreCase(name, type.getAliases()))
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
}
