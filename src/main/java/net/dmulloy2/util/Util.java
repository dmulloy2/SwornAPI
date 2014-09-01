/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.util;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import net.dmulloy2.io.UUIDFetcher;
import net.dmulloy2.types.StringJoiner;
import net.dmulloy2.types.Versioning;
import net.dmulloy2.types.Versioning.Version;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Furnace;
import org.bukkit.block.Jukebox;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
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
	 * Gets the Player from a given name or {@link UUID}.
	 *
	 * @param identifier Player name or UUID
	 * @return Player from the given name or UUID, null if none exists.
	 * @see {@link Bukkit#getPlayer(UUID)}
	 * @see {@link Bukkit#matchPlayer(String)}
	 */
	public static Player matchPlayer(String identifier)
	{
		Validate.notNull(identifier, "identifier cannot be null!");

		// Get by UUID first
		if (identifier.length() == 36)
			return Bukkit.getPlayer(UUID.fromString(identifier));

		// Then attempt to match
		List<Player> players = Bukkit.matchPlayer(identifier);
		if (! players.isEmpty())
			return players.get(0);

		return null;
	}

	/**
	 * Gets the OfflinePlayer from a given name or {@link UUID}.
	 * <p>
	 * Use of this method is discouraged as it is potentially blocking.
	 *
	 * @param identifier Player name or UUID
	 * @return OfflinePlayer from the given name or UUID, null if none exists
	 * @see {@link #matchPlayer(String)}
	 * @see {@link Bukkit#getOfflinePlayer(UUID)}
	 * @see {@link Bukkit#getOfflinePlayer(String)}
	 */
	@SuppressWarnings("deprecation") // Bukkit#getOfflinePlayer(String)
	public static OfflinePlayer matchOfflinePlayer(String identifier)
	{
		Validate.notNull(identifier, "identifier cannot be null!");

		// Check online players first
		Player player = matchPlayer(identifier);
		if (player != null)
			return player;

		// Then check UUID
		if (identifier.length() == 36)
			return Bukkit.getOfflinePlayer(UUID.fromString(identifier));

		OfflinePlayer op = Bukkit.getOfflinePlayer(identifier);
		if (op.hasPlayedBefore())
			return op;

		return null;
	}

	/**
	 * Gets a {@link Player}'s {@link UUID}. This can be potentially blocking if
	 * an outdated version is being used.
	 *
	 * @param player Player to get the unique id for
	 * @return The player's UUID or null if one cannot be found
	 */
	public static UUID getUniqueId(OfflinePlayer player)
	{
		try
		{
			// Lookup the UUID
			if (Versioning.getVersion() == Version.MC_16)
				return UUIDFetcher.getUUID(player.getName());
		} catch (Throwable ex) { }
		return player.getUniqueId();
	}

	/**
	 * Gets a list of online {@link Player}s. This also provides backwards
	 * compatibility as Bukkit changed <code>getOnlinePlayers</code>.
	 *
	 * @return A list of online Players
	 */
	public static List<Player> getOnlinePlayers()
	{
		try
		{
			// Provide backwards compatibility
			Method getOnlinePlayers = ReflectionUtil.getMethod(Bukkit.class, "getOnlinePlayers");
			if (getOnlinePlayers.getReturnType() != Collection.class)
				return toList((Player[]) getOnlinePlayers.invoke(null));
		} catch (Throwable ex) { }
		return newList(Bukkit.getOnlinePlayers());
	}

	/**
	 * Whether or not a player is banned.
	 *
	 * @param identifier Player name or UUID
	 * @return True if the player is banned, false if not
	 */
	public static boolean isBanned(String identifier)
	{
		Validate.notNull(identifier, "identifier cannot be null!");
		for (OfflinePlayer banned : Bukkit.getBannedPlayers())
		{
			if (identifier.equalsIgnoreCase(banned.getName()) || identifier.equals(getUniqueId(banned)))
				return true;
		}

		return false;
	}

	/**
	 * Returns a random integer out of <code>x</code>.
	 *
	 * @param x Integer the random should be out of
	 * @return A random integer out of x.
	 * @throws IllegalArgumentException if <code>x</code> is less than 0.
	 */
	public static int random(int x)
	{
		Validate.isTrue(x > 0, "x cannot be negative!");

		Random rand = new Random();
		return rand.nextInt(x);
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
			if (player.getWorld().getUID().equals(loc.getWorld().getUID()))
				player.playEffect(loc, effect, data);
		}
	}

	/**
	 * Whether or not two locations are similar. This does not take pitch or yaw
	 * into account.
	 *
	 * @param loc First location
	 * @param loc2 Second location
	 * @return True if the locations are similar, false if not
	 */
	public static boolean checkLocation(Location loc, Location loc2)
	{
		Validate.notNull(loc, "loc cannot be null!");
		Validate.notNull(loc2, "loc2 cannot be null!");

		if (loc.equals(loc2))
			return true;

		return loc.getBlockX() == loc2.getBlockX()
				&& loc.getBlockY() == loc2.getBlockY()
				&& loc.getBlockZ() == loc2.getBlockZ()
				&& loc.getWorld().getUID().equals(loc2.getWorld().getUID());
	}

	/**
	 * Turns a {@link Location} into a string for debug purpouses.
	 *
	 * @param loc {@link Location} to convert
	 * @return String for debug purpouses
	 */
	public static String locationToString(Location loc)
	{
		Validate.notNull(loc, "loc cannot be null!");

		StringBuilder ret = new StringBuilder();
		ret.append("World: " + loc.getWorld().getName());
		ret.append(" X: " + loc.getBlockX());
		ret.append(" Y: " + loc.getBlockY());
		ret.append(" Z: " + loc.getBlockZ());
		return ret.toString();
	}

	/**
	 * Returns a useful Stack Trace for debugging purpouses.
	 *
	 * @param ex Underlying {@link Throwable}
	 * @param circumstance Circumstance in which the Exception occured
	 */
	public static String getUsefulStack(Throwable ex, String circumstance)
	{
		Validate.notNull(ex, "ex cannot be null!");

		StringJoiner joiner = new StringJoiner("\n");
		joiner.append("Encountered an exception" + (circumstance != null ? " while " + circumstance : "") + ": " + ex.toString());
		joiner.append("Affected classes:");

		for (StackTraceElement ste : ex.getStackTrace())
		{
			String className = ste.getClassName();
			if (! className.contains("net.minecraft"))
			{
				StringBuilder line = new StringBuilder();
				line.append("  " + className + "." + ste.getMethodName());
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
					line.append("  " + className + "." + ste.getMethodName());
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
	 * @see {@link Util#getWorkingJar(Class)
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
	 * Gets the working jar of a given class.
	 *
	 * @return The name, null if not found
	 */
	public static final String getWorkingJar(Class<?> clazz)
	{
		try
		{
			String path = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
			path = URLDecoder.decode(path, "UTF-8");
			return path.substring(path.lastIndexOf("/") + 1);
		} catch (Throwable ex) { }
		return null;
	}

	/**
	 * Constructs a new {@link List} from an existing {@link Collection}. This
	 * helps with concurrency problems. Changes to the returned list will not be
	 * reflected in the original collection. The use of this method is generally
	 * not performance-effective.
	 * <p>
	 * TODO: Find a better solution.
	 *
	 * @param coll Base Collection
	 * @return The List
	 */
	public static <T> List<T> newList(Collection<? extends T> coll)
	{
		Validate.notNull(coll, "coll cannot be null!");
		return new ArrayList<>(coll);
	}

	/**
	 * Constructs a new {@link List} paramaterized with <code>T</code>.
	 *
	 * @param objects Array of <code>T</code> to create the list with
	 * @return a new {@link List} from the given objects
	 */
	@SafeVarargs
	public static <T> List<T> toList(T... objects)
	{
		Validate.notNull(objects, "objects cannot be null!");

		List<T> ret = new ArrayList<>();

		for (T t : objects)
			ret.add(t);

		return ret;
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

		for (Entry<K, V> entry : new LinkedHashMap<>(map).entrySet())
		{
			K key = entry.getKey();
			if (original.containsKey(key))
			{
				V val = entry.getValue();
				V def = original.get(key);
				if (val.equals(def))
				{
					map.remove(key);
				}
			}
		}

		return map;
	}

	/**
	 * Gets the key mapped to a given value in a given map.
	 *
	 * @param map Map containing the value
	 * @param value Value to get the key for
	 * @return The key, or null if not found
	 */
	public static <K, V> K getKey(Map<K, V> map, V value)
	{
		Validate.notNull(map, "map cannot be null!");
		Validate.notNull(value, "value cannot be null!");

		for (Entry<K, V> entry : new HashMap<>(map).entrySet())
		{
			if (entry.getValue().equals(value))
				return entry.getKey();
		}

		return null;
	}

	private static final Object NULL = new Object();

	/**
	 * Removes duplicate entries from a {@link List}. Retains order.
	 *
	 * @param list List to remove duplicate entries from
	 * @return The list, without duplicate entries
	 */
	public static <T> List<T> removeDuplicates(List<T> list)
	{
		Validate.notNull(list, "list cannot be null!");

		Map<T, Object> map = new LinkedHashMap<>();

		for (T element : list)
			map.put(element, NULL);

		return new ArrayList<>(map.keySet());
	}

	/**
	 * Parses a given {@link Object} (preferably a {@link String}) and returns a
	 * boolean value.
	 *
	 * @param object Object to parse
	 * @return Boolean value from the given object. Defaults to
	 *         <code>false</code>
	 */
	public static boolean toBoolean(Object object)
	{
		Validate.notNull(object, "object cannot be null!");

		if (object instanceof Boolean)
		{
			return ((Boolean) object).booleanValue();
		}

		if (object instanceof String)
		{
			String str = (String) object;
			return str.startsWith("y") || str.startsWith("t") || str.startsWith("on") || str.startsWith("+") || str.startsWith("1");
		}

		return Boolean.parseBoolean(object.toString());
	}

	/**
	 * Sets a {@link Block}'s {@link MaterialData}. Exists because Bukkit's
	 * BlockState API sucks.
	 * <p>
	 * This method is deprecated and is not guaranteed to work.
	 *
	 * @param block Block to set data of
	 * @param data Data to set
	 * @deprecated {@link Block#setData(byte)} is deprecated
	 */
	@Deprecated
	public static void setData(Block block, MaterialData data)
	{
		Validate.notNull(block, "block cannot be null!");
		Validate.notNull(data, "data cannot be null!");

		block.setData(data.getData());
		block.getState().update(true);
	}

	/**
	 * Returns a <code>String</code> representation of a {@link BlockState},
	 * since BlockStates do not define a <code>toString()</code> method.
	 *
	 * @param state BlockState to represent
	 * @return The string representation
	 */
	public static String blockStateToString(BlockState state)
	{
		Validate.notNull(state, "state cannot be null!");

		if (state instanceof Sign)
		{
			Sign sign = (Sign) state;
			return "Sign { lines = " + Arrays.toString(sign.getLines()) + " }";
		}
		else if (state instanceof CommandBlock)
		{
			CommandBlock cmd = (CommandBlock) state;
			return "CommandBlock { command = " + cmd.getCommand() + ", name = " + cmd.getName() + " }";
		}
		else if (state instanceof Jukebox)
		{
			Jukebox jukebox = (Jukebox) state;
			return "Jukebox { playing = " + FormatUtil.getFriendlyName(jukebox.getPlaying()) + " }";
		}
		else if (state instanceof NoteBlock)
		{
			NoteBlock note = (NoteBlock) state;
			return "NoteBlock { note = " + FormatUtil.getFriendlyName(note.getNote().getTone()) + " }";
		}
		else if (state instanceof Skull)
		{
			Skull skull = (Skull) state;
			return "Skull { type = " + FormatUtil.getFriendlyName(skull.getSkullType()) + ", owner = " + skull.getOwner() + " }";
		}
		else if (state instanceof Furnace)
		{
			Furnace furnace = (Furnace) state;
			return "Furnace { burnTime = " + furnace.getBurnTime() + ", cookTime = " + furnace.getCookTime() + " }";
		}
		else
		{
			return "BlockState { type = " + FormatUtil.getFriendlyName(state.getType()) + " }";
		}
	}

	/**
	 * Concatenates two arrays of the same type.
	 *
	 * @param first First array
	 * @param second Second array
	 * @return Concatenated array
	 */
	public static <T> T[] concat(T[] first, T[] second)
	{
		Validate.notNull(first, "first cannot be null!");
		Validate.notNull(second, "second cannot be null!");

		T[] ret = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, ret, first.length, second.length);
        return ret;
	}

	/**
	 * Whether or not an Enum type exists with a given name in a given class.
	 *
	 * @param clazz Enum class
	 * @param name Type name
	 * @return True if the type exists, false if not
	 */
	public static <T extends Enum<T>> boolean isEnumType(Class<T> clazz, String name)
	{
        try
        {
            Enum.valueOf(clazz, name.toUpperCase());
            return true;
        } catch (Throwable ex) { }
        return false;
    }
}