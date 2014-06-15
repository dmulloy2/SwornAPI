/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.types;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;

/**
 * @author dmulloy2
 */

@SerializableAs("net.dmulloy2.SimpleVector")
public final class SimpleVector implements ConfigurationSerializable
{
	private int x, y, z;

	public SimpleVector()
	{
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public SimpleVector(String s)
	{
		String[] ss = s.split(",");

		this.x = Integer.parseInt(ss[0]);
		this.y = Integer.parseInt(ss[1]);
		this.z = Integer.parseInt(ss[2]);
	}

	public SimpleVector(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public SimpleVector(Vector v)
	{
		this(v.getBlockX(), v.getBlockY(), v.getBlockZ());
	}

	public SimpleVector(Location l)
	{
		this(l.toVector());
	}

	@Override
	public String toString()
	{
		return x + "," + y + "," + z;
	}

	public Vector toVector()
	{
		return new Vector(x, y, z);
	}

	public Location toLocation(World w)
	{
		return toVector().toLocation(w);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null)
			return false;

		if (getClass() != o.getClass())
			return false;

		final SimpleVector that = (SimpleVector) o;

		if (this.x != that.x)
			return false;
		if (this.y != that.y)
			return false;
		if (this.z != that.z)
			return false;
		return true;
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> result = new LinkedHashMap<String, Object>();

		result.put("c", toString());

		return result;
	}

	public static SimpleVector deserialize(Map<String, Object> args)
	{
		return new SimpleVector((String) args.get("c"));
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getZ()
	{
		return z;
	}
}