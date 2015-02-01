/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.types;

import java.util.LinkedHashMap;
import java.util.Map;

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("c", toString());
		return result;
	}

	// ---- Generic Methods

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return x + "," + y + "," + z;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof SimpleVector)
		{
			SimpleVector that = (SimpleVector) obj;
			return this.x == that.x && this.y == that.y && this.z == that.z;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		int hash = 40;
		hash *= 1 + x;
		hash *= 1 + y;
		hash *= 1 + z;
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SimpleVector clone()
	{
		return new SimpleVector(x, y, z);
	}
}