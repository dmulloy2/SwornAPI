/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;

/**
 * Util dealing with Lists.
 *
 * @author dmulloy2
 */

public class ListUtil
{
	private static final Object EMPTY = new Object();

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
			map.put(element, EMPTY);

		return new ArrayList<>(map.keySet());
	}

	/**
	 * Constructs a new {@link List} paramaterized with <code>T</code>.
	 *
	 * @param elements Array of <code>T</code> to create the list with
	 * @return a new {@link List} from the given objects
	 */
	@SafeVarargs
	public static <T> List<T> toList(T... elements)
	{
		Validate.notNull(elements, "elements cannot be null!");

		List<T> list = new ArrayList<T>();
		for (T element : elements)
		{
			list.add(element);
		}

		return list;
	}

	/**
	 * Whether or not a list contains a String, ignoring case.
	 * @param list List to check
	 * @param string String to check for
	 * @return Whether or not the list contains the given String
	 */
	public static boolean containsIgnoreCase(List<String> list, String string)
	{
		Validate.notNull(list, "list cannot be null!");
		Validate.notNull(string, "string cannot be null!");

		for (String element : list.toArray(new String[0]))
		{
			if (string.equalsIgnoreCase(element))
				return true;
		}

		return false;
	}

	/**
	 * Removes a given String element from a list, ignoring case.
	 * @param list List to remove from
	 * @param string String to remove
	 */
	public static void removeIgnoreCase(List<String> list, String string)
	{
		Validate.notNull(list, "list cannot be null!");
		Validate.notNull(string, "string cannot be null!");

		for (String element : list.toArray(new String[0]))
		{
			if (string.equalsIgnoreCase(element))
				list.remove(element);
		}
	}
}