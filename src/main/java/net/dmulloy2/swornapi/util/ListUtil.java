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
package net.dmulloy2.swornapi.util;

import java.util.*;

import net.dmulloy2.swornapi.util.Validate;

/**
 * Util dealing with Lists.
 *
 * @author dmulloy2
 */

public class ListUtil
{
	private ListUtil() { }

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

		List<T> list = new ArrayList<>(elements.length);
		list.addAll(Arrays.asList(elements));

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

		for (String element : list)
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

		list.removeIf(string::equalsIgnoreCase);
	}
}
