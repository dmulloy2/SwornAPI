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
package net.dmulloy2.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.dmulloy2.Volatile;
import net.dmulloy2.integration.VaultHandler;

/**
 * Utility class for dealing with Materials.
 *
 * @author dmulloy2
 */

public class MaterialUtil
{
	private MaterialUtil() { }

	/**
	 * Gets the {@link Material} from a given string using Bukkit, Vault, or
	 * internal Minecraft.
	 *
	 * @param string String to get the Material from
	 * @return The material, or null if not found
	 * @see Material#matchMaterial(String)
	 */
	public static Material getMaterial(String string)
	{
		Material material = Material.matchMaterial(string);
		if (material != null)
			return material;

		// Resolve using Vault, if applicable
		if (Bukkit.getPluginManager().isPluginEnabled("Vault"))
		{
			try
			{
				material = VaultHandler.resolve(string);
				if (material != null)
					return material;
			} catch (Throwable ex) { }
		}

		try
		{
			// Attempt to grab it unsafely. The call will never return null,
			// but if nothing is found, it will return air.

			@SuppressWarnings("deprecation")
			Material internal = Bukkit.getUnsafe().getMaterialFromInternalName(string);
			if (internal != Material.AIR)
				return internal;
		} catch (Throwable ex) { }

		return null;
	}

	/**
	 * Gets the friendly name of a Material.
	 *
	 * @param material Material to get the name of
	 * @return The name
	 */
	public static String getName(Material material)
	{
		if (material == null)
			return "null";

		return getName(new ItemStack(material));
	}

	/**
	 * Gets the friendly name of an ItemStack.
	 *
	 * @param stack Stack to get the name of
	 * @return The name
	 */
	public static String getName(ItemStack stack)
	{
		if (stack == null)
			return "null";
		
		try
		{
			return Volatile.getName(stack);
		} catch (Exception ex)
		{
			try
			{
				Class<?> craftItemStack = ReflectionUtil.getCraftClass("inventory.CraftItemStack");
				Method asNMSCopy = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
				Object nmsItem = asNMSCopy.invoke(null, stack);
				Method getItem = nmsItem.getClass().getMethod("getItem");
				Object item = getItem.invoke(nmsItem);
				Method getName = item.getClass().getMethod("a", nmsItem.getClass());
				return (String) getName.invoke(item, nmsItem);
			} catch (Exception ex1)
			{
				return FormatUtil.getFriendlyName(stack.getType().name());
			}
		}
	}

	/**
	 * Gets the friendly name of an Item or Material.
	 * @param string String to parse
	 * @return The name, or {@code -string} if parsing fails
	 */
	public static String getName(String string)
	{
		try
		{
			ItemStack stack = ItemUtil.readItem(string);
			if (stack != null)
				return getName(stack);
		} catch (Throwable ex) { }

		return "-" + string;
	}

	/**
	 * @deprecated Renamed to {@link #getName(String)
	 */
	@Deprecated
	public static String getMaterialName(String name)
	{
		return getName(name);
	}

	/**
	 * Converts a list of strings into a list of Materials.
	 *
	 * @param strings List to convert
	 * @return Converted list
	 */
	public static List<Material> fromStrings(List<String> strings)
	{
		List<Material> ret = new ArrayList<>();

		for (String string : strings)
		{
			Material material = getMaterial(string);
			if (material != null)
				ret.add(material);
		}

		return ret;
	}
}
