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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Utility class for dealing with Materials.
 *
 * @author dmulloy2
 */

public class MaterialUtil
{
	private MaterialUtil() { }

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

		return FormatUtil.getFriendlyName(material);
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

		return getName(stack.getType());
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
			return getName(stack);
		} catch (Throwable ignored) { }

		return "-" + string;
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
		List<String> invalid = new ArrayList<>();

		for (String string : strings)
		{
			Material material = Material.matchMaterial(string);
			if (material == null)
				invalid.add(string);
			else
				ret.add(material);
		}

		if (!invalid.isEmpty())
			throw new IllegalArgumentException("Invalid materials: " + invalid);

		return ret;
	}
}
