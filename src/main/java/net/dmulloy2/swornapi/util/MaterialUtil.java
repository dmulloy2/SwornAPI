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
package net.dmulloy2.swornapi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

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

	private static Map<Integer, Material> LEGACY_BY_ID = null;

	/**
	 * @deprecated It's way past time to migrate from IDs
	 */
	@Deprecated
	private static Material materialFromId(int id)
	{
		if (LEGACY_BY_ID == null)
		{
			LEGACY_BY_ID = new HashMap<>();

			for (Material material : Material.values())
			{
				if (material.isLegacy())
				{
					LEGACY_BY_ID.put(material.getId(), material);
				}
			}
		}

		return LEGACY_BY_ID.get(id);
	}

	/**
	 * @deprecated Should only be called on the first run when migrating pre 1.13 data/configs
	 */
	@Deprecated
	public static Material convertFromLegacy(String legacyString)
	{
		String materialName = legacyString;
		byte magicData = 0;

		if (legacyString.contains(":"))
		{
			String[] split = legacyString.split(":");
			materialName = split[0];
			magicData = Byte.parseByte(split[1]);
		}

		Material material;

		try
		{
			int materialId = Integer.parseInt(materialName);
			material = materialFromId(materialId);
		}
		catch (NumberFormatException ex)
		{
			material = Material.getMaterial(materialName, true);
		}

		MaterialData data = new MaterialData(material, magicData);
		return Bukkit.getUnsafe().fromLegacy(data);
	}
}
