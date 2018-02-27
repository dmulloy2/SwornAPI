/**
 * SwornAPI - common API for MineSworn and Shadowvolt plugins
 * Copyright (C) 2016 dmulloy2
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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Utility class for sorting lists and arrays based on given criteria.
 * @author dmulloy2
 */
@Getter @Setter
public class Sorter<K, V extends Comparable<V>>
{
	/**
	 * Obtains criteria used for sorting
	 * @author dmulloy2
	 */
	@FunctionalInterface
	public interface SortCriteria<K, V>
	{
		/**
		 * Obtains the criteria used for sorting from the given object
		 * @param key Object in the list
		 * @return Criteria for sorting
		 */
		V getValue(K key);
	}

	/**
	 * Sorting mode, either ascending or descending
	 * @author dmulloy2
	 */
	@Getter
	@AllArgsConstructor
	public enum SortMode
	{
		/**
		 * Sorts values in ascending order, with the lowest values first
		 */
		ASCENDING(1),
		/**
		 * Sorts values in descending order, with the highest values first
		 */
		DESCENDING(-1);

		private final int sign;
	}

	/**
	 * Filters out values from sorting
	 * @author dmulloy2
	 */
	@FunctionalInterface
	public interface Filter<V>
	{
		/**
		 * Whether or not to include this value in the sorting process
		 * @param value Value to check
		 * @return True if acceptable, false if not
		 */
		boolean accept(V value);
	}

	private SortCriteria<K, V> criteria;
	private SortMode mode;

	/**
	 * Creates a new sorter with no filtering.
	 * @param criteria Criteria to sort by
	 * @param mode Sorting mode
	 */
	public Sorter(SortCriteria<K, V> criteria, SortMode mode)
	{
		this.criteria = criteria;
		this.mode = mode;
	}

	/**
	 * Creates a new sorter, defaulting to descending order.
	 * @param criteria Criteria to sort by
	 */
	public Sorter(SortCriteria<K, V> criteria)
	{
		this(criteria, SortMode.DESCENDING);
	}

	/**
	 * Sorts the given array based on this Sorter's criteria and mode.
	 * @param array Array to sort
	 * @return The sorted array as a list
	 */
	public List<K> sort(K[] array)
	{
		return sort(Arrays.stream(array)
		                  .collect(Collectors.toMap(k -> k, criteria::getValue)));
	}

	/**
	 * Sorts the given collection based on this Sorter's criteria and mode.
	 * @param collection Collection to sort
	 * @return The sorted collection as a list
	 */
	public List<K> sort(Collection<K> collection) {
		return sort(collection.stream()
		                      .collect(Collectors.toMap(k -> k, criteria::getValue)));
	}

	// Where the actual sorting takes place
	private List<K> sort(Map<K, V> map) {
		return map.entrySet()
		          .stream()
		          .sorted((e1, e2) -> mode.getSign() * e1.getValue().compareTo(e2.getValue()))
		          .map(Entry::getKey)
		          .collect(Collectors.toList());
	}
}
