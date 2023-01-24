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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

/**
 * This class provides a lazy-load Location, so that World doesn't need to be
 * initialized yet when an object of this class is created, only when the
 * {@link Location} is first accessed. This class is also serializable and
 * can easily be converted into a {@link Location} or {@link SimpleVector}
 *
 * @author dmulloy2
 */

@Getter @Setter
@SerializableAs("net.dmulloy2.LazyLocation")
public final class LazyLocation implements ConfigurationSerializable, Cloneable
{
	private final int x, y, z;
	private final String worldName;

	private transient World world;
	private transient Location location;
	private transient SimpleVector simpleVector;

	public LazyLocation(String worldName, int x, int y, int z)
	{
		Validate.notNull(worldName, "worldName cannot be null!");
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public LazyLocation(String worldName, int x, int z)
	{
		this(worldName, x, 0, z);
	}

	public LazyLocation(World world, int x, int y, int z)
	{
		this(world.getName(), x, y, z);
	}

	public LazyLocation(String worldName, SimpleVector vector)
	{
		this(worldName, vector.getX(), vector.getY(), vector.getZ());
		this.simpleVector = vector;
	}

	public LazyLocation(Location location)
	{
		this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
		this.location = location;
	}

	public LazyLocation(Player player)
	{
		this(player.getLocation());
	}

	public LazyLocation(Map<String, Object> args)
	{
		Validate.notNull(args, "args cannot be null!");
		this.worldName = (String) args.get("worldName");
		this.x = (int) args.get("x");
		this.y = (int) args.get("y");
		this.z = (int) args.get("z");
	}

	/**
	 * Returns the {@link World} associated with this
	 */
	public World getWorld()
	{
		if (world == null)
			world = Bukkit.getWorld(worldName);

		return world;
	}

	/**
	 * Initializes the Location
	 */
	private void initLocation()
	{
		// if location is already initialized, simply return
		if (location != null)
			return;

		// get World; hopefully it's initialized at this point
		World world = getWorld();
		if (world == null)
			return;

		// store the Location for future calls, and pass it on
		location = new Location(world, x, y, z);
	}

	/**
	 * Initializes the SimpleVector
	 */
	private void initSimpleVector()
	{
		if (simpleVector != null)
			return;

		Location location = getLocation();
		if (location == null)
			return;

		simpleVector = new SimpleVector(location);
	}

	/**
	 * Returns the actual {@link Location}
	 */
	public Location getLocation()
	{
		initLocation();
		return location;
	}

	/**
	 * Returns a new {@link SimpleVector} based around this
	 */
	public SimpleVector getSimpleVector()
	{
		initSimpleVector();
		return simpleVector;
	}

	public static LazyLocation getMaximum(LazyLocation l1, LazyLocation l2)
	{
		return new LazyLocation(l1.worldName, Math.max(l1.x, l2.x), Math.max(l1.y, l2.y), Math.max(l1.z, l2.z));
	}

	public static LazyLocation getMinimum(LazyLocation l1, LazyLocation l2)
	{
		return new LazyLocation(l1.worldName, Math.min(l1.x, l2.x), Math.min(l1.y, l2.y), Math.min(l1.z, l2.z));
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("worldName", worldName);
		result.put("x", x);
		result.put("y", y);
		result.put("z", z);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof LazyLocation that)
		{
			return x == that.x && y == that.y && z == that.z && worldName.equals(that.worldName);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(x, y, z, worldName);
	}

	@Override
	public String toString()
	{
		return "LastLocation[worldName=" + worldName + ", x=" + x + ", y=" + y + ", z=" + z + "]";
	}

	@Override
	public LazyLocation clone()
	{
		return new LazyLocation(worldName, x, y, z);
	}
}
