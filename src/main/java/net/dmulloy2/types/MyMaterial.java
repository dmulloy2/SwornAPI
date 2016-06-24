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

import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import lombok.Data;
import net.dmulloy2.util.MaterialUtil;
import net.dmulloy2.util.NumberUtil;

/**
 * A serializable Material and data combination.
 *
 * @author dmulloy2
 */

@Data
public class MyMaterial
{
	private final boolean ignoreData;
	private final Material material;
	private final short data;

	public MyMaterial(Material material, short data, boolean ignoreData)
	{
		this.ignoreData = ignoreData;
		this.material = material;
		this.data = data;
	}

	public MyMaterial(Material material, short data)
	{
		this(material, data, false);
	}

	@SuppressWarnings("deprecation") // MaterialData#getData()
	public MyMaterial(Material material, MaterialData data)
	{
		this(material, data.getData(), false);
	}

	public MyMaterial(Material material)
	{
		this(material, (short) 0, true);
	}

	// --- ItemStacks

	/**
	 * Whether or not a given {@link ItemStack} matches this MyMaterial.
	 *
	 * @param item ItemStack to check
	 * @return True if they match, false if not
	 */
	public final boolean matches(ItemStack item)
	{
		return item.getType() == material && (ignoreData ? true : item.getDurability() == data);
	}

	/**
	 * Whether or not this MyMaterial matches the given Material and data.
	 * 
	 * @param material Material to check
	 * @param data Data to check
	 * @return True if they matche, false if not
	 */
	public boolean matches(Material material, short data)
	{
		return this.material == material && (ignoreData || this.data == data);
	}

	/**
	 * Creates a new {@link ItemStack} from this MyMaterial.
	 *
	 * @param amount Amount, defaults to 1
	 * @return The new {@link ItemStack}
	 */
	public final ItemStack newItemStack(int amount)
	{
		if (amount <= 0)
			amount = 1;

		return new ItemStack(material, amount, ignoreData ? 0 : data);
	}

	// ---- Getters

	/**
	 * Gets the friendly name of the underlying item.
	 *
	 * @return The name
	 */
	public final String getName()
	{
		return MaterialUtil.getName(newItemStack(1));
	}

	/**
	 * Serializes this MyMaterial. This is essentially the inverse of
	 * {@link #fromString(String)}
	 * <p>
	 * Format: {@code <Material>[:Data]}
	 *
	 * @return This MyMaterial, serialized
	 */
	public final String serialize()
	{
		return material.name() + (! ignoreData ? ":" + data : "");
	}

	// ---- Generic Methods

	@Override
	public String toString()
	{
		if (ignoreData)
			return material.toString();

		return "MyMaterial[material=" + material + ", data=" + data + "]";
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof MyMaterial)
		{
			MyMaterial that = (MyMaterial) obj;
			return this.material == that.material && (ignoreData || this.data == that.data);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(material, (ignoreData ? 0 : data));
	}

	/**
	 * Retrieves the equivalent MyMaterial from a given ItemStack using its type
	 * and durability.
	 * 
	 * @param item ItemStack
	 * @return The equivalent MyMaterial
	 */
	public static MyMaterial fromItem(ItemStack item)
	{
		if (item.getDurability() == 0)
			return new MyMaterial(item.getType(), item.getDurability());
		else
			return new MyMaterial(item.getType());
	}

	/**
	 * Attempts to parse a MyMaterial from a given string.
	 * <p>
	 * Format: <code>Material:Data</code>
	 *
	 * @param string String to get the MyMaterial from
	 * @return Resulting MyMaterial, or null if parsing failed
	 */
	public static MyMaterial fromString(String string)
	{
		string = string.replaceAll(" ", "");

		try
		{
			if (string.contains(":"))
			{
				String[] split = string.split(":");
				Material material = MaterialUtil.getMaterial(split[0]);
				if (material != null)
				{
					short data = NumberUtil.toShort(split[1]);
					boolean ignoreData = data == -1;
					if (data <= 0)
						data = 0;

					return new MyMaterial(material, data, ignoreData);
				}
			}

			Material material = MaterialUtil.getMaterial(string);
			if (material != null)
				return new MyMaterial(material);
		} catch (Throwable ex) { }
		return null;
	}
}
