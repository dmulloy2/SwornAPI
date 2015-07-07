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
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import net.dmulloy2.types.StringJoiner;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

/**
 * General utility class.
 *
 * @author dmulloy2
 */

public class Util
{
	private Util() { }

	/**
	 * Gets the {@link Player} from a given name or {@link UUID}.
	 *
	 * @param identifier Player name or UUID
	 * @return Player from the given name or UUID, or null if none exists.
	 * @see {@link Bukkit#getPlayer(UUID)}
	 * @see {@link Bukkit#getPlayer(String)}
	 */
	public static Player matchPlayer(String identifier)
	{
		Validate.notNull(identifier, "identifier cannot be null!");

		// First, get by UUID
		if (identifier.length() == 36)
			return Bukkit.getPlayer(UUID.fromString(identifier));

		// Last, get by name
		return Bukkit.getPlayer(identifier);
	}

	/**
	 * Gets the {@link OfflinePlayer} from a given name or {@link UUID}.<br>
	 * This method is potentially blocking, especially when using names.
	 *
	 * @param identifier Player name or UUID
	 * @return OfflinePlayer from the given name or UUID, or null if none exists
	 * @see {@link #matchPlayer(String)}
	 * @see {@link Bukkit#getOfflinePlayer(UUID)}
	 * @see {@link Bukkit#getOfflinePlayer(String)}
	 */
	@SuppressWarnings("deprecation") // Bukkit#getOfflinePlayer(String)
	public static OfflinePlayer matchOfflinePlayer(String identifier)
	{
		Validate.notNull(identifier, "identifier cannot be null!");

		// First, check online players
		Player player = matchPlayer(identifier);
		if (player != null)
			return player;

		// Then, check UUID
		if (identifier.length() == 36)
			return Bukkit.getOfflinePlayer(UUID.fromString(identifier));

		// Last, get by name
		return Bukkit.getOfflinePlayer(identifier);
	}

	private static Method getOnlinePlayers;

	/**
	 * Gets a list of currently online Players. This also provides backwards
	 * compatibility as Bukkit changed <code>getOnlinePlayers</code>.
	 *
	 * @return A list of currently online Players
	 */
	@SuppressWarnings("unchecked")
	public static List<Player> getOnlinePlayers()
	{
		try
		{
			// Provide backwards compatibility
			if (getOnlinePlayers == null)
				getOnlinePlayers = Bukkit.class.getMethod("getOnlinePlayers");
			if (getOnlinePlayers.getReturnType() != Collection.class)
				return Arrays.asList((Player[]) getOnlinePlayers.invoke(null));
		} catch (Throwable ex) { }
		return (List<Player>) Bukkit.getOnlinePlayers();
	}

	/**
	 * Plays an effect to all online players.
	 *
	 * @param effect Effect type to play
	 * @param loc Location where the effect should be played
	 * @param data Effect data, can be null
	 * @see {@link Player#playEffect(Location, Effect, Object)}
	 */
	public static <T> void playEffect(Effect effect, Location loc, T data)
	{
		Validate.notNull(effect, "effect cannot be null!");
		Validate.notNull(loc, "loc cannot be null!");

		for (Player player : getOnlinePlayers())
		{
			if (player.getWorld().equals(loc.getWorld()))
				player.playEffect(loc, effect, data);
		}
	}

	/**
	 * @deprecated Replaced by {@link coordsEqual(Location, Location)}
	 */
	@Deprecated
	public static boolean checkLocation(Location loc1, Location loc2)
	{
		return coordsEqual(loc1, loc2);
	}

	/**
	 * Whether or not two locations share the same coordinates.<br>
	 * This method does not take pitch or yaw into account.
	 *
	 * @param first First location
	 * @param second Second location
	 * @return True if they share coordinates, false if not.
	 */
	public static boolean coordsEqual(Location first, Location second)
	{
		Validate.notNull(first, "first location cannot be null!");
		Validate.notNull(second, "second location cannot be null!");

		return first.equals(second) || first.getBlock().equals(second.getBlock());
	}

	/**
	 * Converts a {@link Location} to a String for debugging purposes.
	 * 
	 * @param loc Location to convert
	 * @return String for debugging purposes
	 */
	public static String locationToString(Location loc)
	{
		Validate.notNull(loc, "loc cannot be null!");

		return "Location[world=" + loc.getWorld().getName() +
				", x=" + loc.getBlockX() +
				", y=" + loc.getBlockY() +
				", z=" + loc.getBlockZ() + "]";
	}

	private static Random random;

	/**
	 * Returns a pseudorandom integer out of <code>x</code>.
	 *
	 * @param x Integer the random should be out of
	 * @return A random integer out of x.
	 * @throws IllegalArgumentException if <code>x</code> is less than 0.
	 */
	public static int random(int x)
	{
		Validate.isTrue(x > 0, "x cannot be negative!");

		if (random == null)
			random = new Random();

		return random.nextInt(x);
	}

	/**
	 * Returns a useful Stack Trace for debugging purposes.
	 *
	 * @param ex {@link Throwable} to get the stack trace for
	 * @param circumstance Circumstance in which the Throwable was thrown
	 * @param args Arguments to format into circumstance
	 */
	public static String getUsefulStack(Throwable ex, String circumstance, Object... args)
	{
		Validate.notNull(ex, "ex cannot be null!");

		StringJoiner joiner = new StringJoiner("\n");
		circumstance = circumstance != null ? FormatUtil.format(" while " + circumstance, args) : "";
		joiner.append("Encountered an exception" + circumstance + ": " + ex.toString());
		joiner.append("Affected classes:");

		for (StackTraceElement ste : ex.getStackTrace())
		{
			String className = ste.getClassName();
			if (! className.contains("net.minecraft"))
			{
				StringBuilder line = new StringBuilder();
				line.append("\t" + className + "." + ste.getMethodName());
				if (ste.getLineNumber() > 0)
					line.append("(Line " + ste.getLineNumber() + ")");
				else
					line.append("(Native Method)");

				String jar = getWorkingJar(className);
				if (jar != null)
					line.append(" [" + jar + "]");

				joiner.append(line.toString());
			}
		}

		while (ex.getCause() != null)
		{
			ex = ex.getCause();
			joiner.append("Caused by: " + ex.toString());
			joiner.append("Affected classes:");
			for (StackTraceElement ste : ex.getStackTrace())
			{
				String className = ste.getClassName();
				if (! className.contains("net.minecraft"))
				{
					StringBuilder line = new StringBuilder();
					line.append("\t" + className + "." + ste.getMethodName());
					if (ste.getLineNumber() > 0)
						line.append("(Line " + ste.getLineNumber() + ")");
					else
						line.append("(Native Method)");

					String jar = getWorkingJar(className);
					if (jar != null)
						line.append(" [" + jar + "]");

					joiner.append(line.toString());
				}
			}
		}

		return joiner.toString();
	}

	/**
	 * Gets the current thread's stack.
	 *
	 * @return The current thread's stack
	 */
	public static final String getThreadStack()
	{
		try
		{
			throw new Exception("Thread Stack");
		}
		catch (Exception ex)
		{
			return getUsefulStack(ex, null);
		}
	}

	/**
	 * Gets the working jar of a given Class. This is the same as
	 * {@link #getWorkingJar(Class)}, but the class name is passed through
	 * {@link Class#forName(String)} first.
	 *
	 * @param clazzName Class name
	 * @return The working jar, or null if not found
	 */
	public static final String getWorkingJar(String clazzName)
	{
		try
		{
			return getWorkingJar(Class.forName(clazzName));
		} catch (Throwable ex) { }
		return null;
	}

	/**
	 * Gets the working jar of a given {@link Class}.
	 *
	 * @param clazz Class to get the jar for
	 * @return The working jar, or null if not found
	 */
	public static final String getWorkingJar(Class<?> clazz)
	{
		try
		{
			String path = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
			path = URLDecoder.decode(path, "UTF-8");
			path = path.substring(path.lastIndexOf("/") + 1);
			return ! path.isEmpty() ? path : null;
		} catch (Throwable ex) { }
		return null;
	}

	/**
	 * Filters duplicate entries from a {@link Map} according to the original
	 * map.
	 *
	 * @param map {@link Map} to filter
	 * @param original Original map
	 * @return Filtered map
	 */
	public static <K, V> Map<K, V> filterDuplicateEntries(Map<K, V> map, Map<K, V> original)
	{
		Validate.notNull(map, "map cannot be null!");
		Validate.notNull(original, "original cannot be null!");

		Iterator<Entry<K, V>> iter = map.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<K, V> entry = iter.next();

			K key = entry.getKey();
			if (original.containsKey(key))
			{
				V val = entry.getValue();
				V def = original.get(key);
				if (val.equals(def))
					iter.remove();
			}
		}

		return map;
	}

	/**
	 * Parses a given {@link Object} (preferably a {@link String}) and returns a
	 * boolean value.
	 *
	 * @param object Object to parse
	 * @return Boolean value from the given object
	 */
	public static boolean toBoolean(Object object)
	{
		Validate.notNull(object, "object cannot be null!");

		if (object instanceof Boolean)
		{
			return ((Boolean) object).booleanValue();
		}

		String str = object.toString();
		return str.startsWith("y") || str.startsWith("t") || str.startsWith("on") || str.startsWith("+") || str.startsWith("1");
	}

	/**
	 * Sets a {@link Block}'s {@link MaterialData}. Exists because Bukkit's
	 * BlockState API is not easily cloneable.
	 *
	 * @param block Block to set data of
	 * @param data Data to set
	 */
	@SuppressWarnings("deprecation")
	public static void setData(Block block, MaterialData data)
	{
		Validate.notNull(block, "block cannot be null!");
		Validate.notNull(data, "data cannot be null!");

		block.setData(data.getData());
		block.getState().update(true);
	}
}