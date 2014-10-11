/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.util;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.types.Material;

import org.bukkit.Bukkit;

/**
 * Util dealing with the loss of item id's.
 *
 * @author dmulloy2
 */

public class MaterialUtil
{
	private MaterialUtil() { }

	/**
	 * Gets the {@link org.bukkit.Material} from a given string.
	 *
	 * @param string String to get the Material from
	 * @return The material, or null if not found
	 */
	public static final org.bukkit.Material getMaterial(String string)
	{
		if (NumberUtil.isInt(string))
			return getMaterial(NumberUtil.toInt(string));

		return matchMaterial(string);
	}

	@SuppressWarnings("deprecation") // Bukkit.getUnsafe()
	private static final org.bukkit.Material matchMaterial(String string)
	{
		org.bukkit.Material material = null;

		try
		{
			material = org.bukkit.Material.matchMaterial(string);
		} catch (Throwable ex) { }

		if (material == null)
		{
			try
			{
				// This method never returns null, but if a result is not found, it returns AIR
				org.bukkit.Material internal = Bukkit.getUnsafe().getMaterialFromInternalName(string);
				if (internal != org.bukkit.Material.AIR)
					material = internal;
			} catch (Throwable ex) { }
		}

		return material;
	}

	/**
	 * Returns the {@link org.bukkit.Material} from a given integer.
	 *
	 * @param id Integer to get the Material from
	 * @return Material, or null if not found
	 */
	public static final org.bukkit.Material getMaterial(int id)
	{
		Material mat = Material.getMaterial(id);
		if (mat != null)
			return mat.getBukkitMaterial();

		return null;
	}

	/**
	 * Gets the Item ID for a Bukkit Material.
	 *
	 * @param mat Bukkit material
	 * @return Item ID, or -1 if not found
	 */
	public static final int getItemId(org.bukkit.Material bukkitMaterial)
	{
		Material mat = Material.getByBukkitMaterial(bukkitMaterial);
		if (mat != null)
			return mat.getId();

		return - 1;
	}

	/**
	 * Gets the friendly name of a Material.
	 *
	 * @param mat Material
	 * @return Friendly name
	 */
	public static final String getMaterialName(org.bukkit.Material mat)
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
		org.bukkit.Material mat = getMaterial(name);
		if (mat == null)
			return "null";

		return getMaterialName(mat);
	}

	/**
	 * Gets the friendly name of a Material.
	 *
	 * @param id Item ID
	 * @return Friendly name, or "null" if not found
	 */
	public static final String getMaterialName(int id)
	{
		org.bukkit.Material mat = getMaterial(id);
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
	public static final List<org.bukkit.Material> fromStrings(List<String> strings)
	{
		List<org.bukkit.Material> ret = new ArrayList<>();

		for (String string : strings)
		{
			org.bukkit.Material material = getMaterial(string);
			if (material != null)
				ret.add(material);
		}

		return ret;
	}
}