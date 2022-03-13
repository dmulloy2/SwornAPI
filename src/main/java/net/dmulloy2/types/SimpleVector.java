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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;
import net.dmulloy2.util.NumberUtil;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;

/**
 * Represents a serializable vector.
 *
 * @author dmulloy2
 */

@Getter
@SerializableAs("net.dmulloy2.SimpleVector")
public final class SimpleVector implements ConfigurationSerializable, Cloneable
{
	private int x, y, z;

	public SimpleVector()
	{
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public SimpleVector(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public SimpleVector(Vector vec)
	{
		this(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
	}

	public SimpleVector(Location loc)
	{
		this(loc.toVector());
	}

	public SimpleVector(String s)
	{
		Validate.notEmpty(s, "s cannot be null or empty!");

		String[] ss = s.split(",");
		this.x = NumberUtil.toInt(ss[0]);
		this.y = NumberUtil.toInt(ss[1]);
		this.z = NumberUtil.toInt(ss[2]);
	}

	public SimpleVector(Map<String, Object> args)
	{
		this((String) args.get("c"));
	}

	// ---- Conversion

	/**
	 * Converts this SimpleVector into a Bukkit {@link Vector}
	 */
	public Vector toVector()
	{
		return new Vector(x, y, z);
	}

	/**
	 * Converts this SimpleVector into a Bukkit {@link Location}
	 *
	 * @param world World
	 */
	public Location toLocation(World world)
	{
		return toVector().toLocation(world);
	}

	// ---- Serialization

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("c", toString());
		return result;
	}

	// ---- Generic Methods

	@Override
	public String toString()
	{
		return x + "," + y + "," + z;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof SimpleVector that)
		{
            return this.x == that.x && this.y == that.y && this.z == that.z;
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(x, y, z);
	}

	@Override
	public SimpleVector clone()
	{
		return new SimpleVector(x, y, z);
	}
}
