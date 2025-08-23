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
package net.dmulloy2.swornapi.types;

import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.swornapi.util.FormatUtil;
import net.kyori.adventure.key.Key;

import lombok.Getter;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

/**
 * Represents various enchantments with friendlier configuration and display names.
 * @author dmulloy2
 */
@Getter
public enum EnchantmentType
{
	ARROW_DAMAGE(Enchantment.POWER, "Power", "arrowdmg"),
	ARROW_FIRE(Enchantment.FIRE_ASPECT, "Flame", "fire"),
	ARROW_INFINITE(Enchantment.INFINITY, "Infinity", "inf"),
	ARROW_KNOCKBACK(Enchantment.PUNCH, "Punch", "arrowkb"),
	BINDING_CURSE(Enchantment.BINDING_CURSE, "Curse of Bind", "bind", "binding"),
	DAMAGE_ALL(Enchantment.SHARPNESS, "Sharpness", "sharp"),
	DAMAGE_ARTHROPODS(Enchantment.BANE_OF_ARTHROPODS, "Bane of Arthropods", "bane"),
	DAMAGE_UNDEAD(Enchantment.SMITE, "Smite"),
	DEPTH_STRIDER(Enchantment.DEPTH_STRIDER, "Depth Strider", "depthstrider", "depth"),
	DIG_SPEED(Enchantment.EFFICIENCY, "Efficiency", "eff"),
	DURABILITY(Enchantment.UNBREAKING, "Durability", "dura", "unbreaking"),
	FIRE_ASPECT(Enchantment.FIRE_ASPECT, "Fire Aspect", "fireaspect"),
	FROST_WALKER(Enchantment.FROST_WALKER, "Frost Walker", "frostwalker"),
	KNOCKBACK(Enchantment.KNOCKBACK, "Knockback"),
	LOOT_BONUS_BLOCKS(Enchantment.FORTUNE, "Fortune"),
	LOOT_BONUS_MOBS(Enchantment.LOOTING, "Looting"),
	LUCK(Enchantment.LUCK_OF_THE_SEA, "Luck", "luckofthesea"),
	LOOT(Enchantment.LOOTING, "Loot", "looting"),
	LURE(Enchantment.LURE, "Lure"),
	MENDING(Enchantment.MENDING, "Mending", "mend"),
	OXYGEN(Enchantment.RESPIRATION, "Respiration", "breathing"),
	PROTECTION_ENVIRONMENTAL(Enchantment.PROTECTION, "Protection", "prot"),
	PROTECTION_EXPLOSIONS(Enchantment.BLAST_PROTECTION, "Blast Protection", "blast"),
	PROTECTION_FALL(Enchantment.FEATHER_FALLING, "Feather Falling", "feather"),
	PROTECTION_FIRE(Enchantment.FIRE_PROTECTION, "Fire Protection", "fireprot"),
	PROTECTION_PROJECTILE(Enchantment.PROJECTILE_PROTECTION, "Projectile Protection", "proj"),
	SILK_TOUCH(Enchantment.SILK_TOUCH, "Silk Touch", "silk"),
	SWEEPING_EDGE(Enchantment.SWEEPING_EDGE, "Sweeping Edge", "sweep"),
	THORNS(Enchantment.THORNS, "Thorns"),
	VANISHING_CURSE(Enchantment.VANISHING_CURSE, "Vanishing Curse", "vanish"),
	WATER_WORKER(Enchantment.AQUA_AFFINITY, "Aqua Affinity", "aqua", "affinity");

	private final Enchantment bukkitEnchant;
	private final String name;
	private final String[] aliases;

	EnchantmentType(Enchantment bukkitEnchant, String name, String... aliases)
	{
		this.bukkitEnchant = bukkitEnchant;
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
		EnchantmentType type = fromBukkit(enchant);
		if (type != null)
			return type.getName();

		return FormatUtil.getFriendlyName(enchant);
	}

	/**
	 * Attempts to get an {@link Enchantment} from a given string.
	 *
	 * @param name Enchantment name
	 * @return The enchantment, or null if none exists.
	 */
	public static Enchantment toEnchantment(String name)
	{
		name = name.replaceAll(" ", "_");

		Enchantment enchant = RegistryAccess.registryAccess()
			.getRegistry(RegistryKey.ENCHANTMENT)
			.get(Key.key("minecraft", name.toLowerCase()));
		if (enchant != null)
			return enchant;

		EnchantmentType type = getByName(name.toUpperCase());
		if (type != null)
			return type.bukkitEnchant;

		return null;
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

	private static EnchantmentType fromBukkit(Enchantment bukkitEnchant)
	{
		for (EnchantmentType type : values())
		{
			if (type.getBukkitEnchant().equals(bukkitEnchant))
				return type;
		}

		return null;
	}

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
