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

import org.bukkit.Bukkit;
import org.bukkit.Material;

/**
 * Util dealing with the loss of item id's.
 *
 * @author dmulloy2
 */

public class MaterialUtil
{
	private MaterialUtil() { }

	/**
	 * Gets the {Material} from a given string. This is essentially a wrapper
	 * for {@link Material#matchMaterial(String)}
	 *
	 * @param string String to get the Material from
	 * @return The material, or null if not found
	 * @see Material#matchMaterial(String)
	 */
	public static final Material getMaterial(String string)
	{
		Material material = null;

		try
		{
			material = Material.matchMaterial(string);
		} catch (Throwable ex) { }

		if (material == null)
		{
			try
			{
				// Attempt to grab it unsafely. The call will never return null,
				// but if nothing is found, it will return air.

				@SuppressWarnings("deprecation")
				Material internal = Bukkit.getUnsafe().getMaterialFromInternalName(string);
				if (internal != Material.AIR)
					material = internal;
			} catch (Throwable ex) { }
		}

		return material;
	}

	/**
	 * Gets the friendly name of a Material.
	 *
	 * @param mat Material
	 * @return Friendly name
	 */
	public static final String getMaterialName(Material mat)
	{
		return FormatUtil.getFriendlyName(mat);
	}

	/**
	 * Gets the friendly name of a Material.
	 *
	 * @param name Material name
	 * @return Friendly name, or "null" if not found
	 */
	public static final String getMaterialName(String name)
	{
		Material mat = getMaterial(name);
		if (mat == null)
			return "null";

		return getMaterialName(mat);
	}

	/**
	 * Converts a list of strings into a list of Materials.
	 *
	 * @param strings List to convert
	 * @return Converted list
	 */
	public static final List<Material> fromStrings(List<String> strings)
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
