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

import org.apache.commons.lang.Validate;

/**
 * StringJoiner is used to construct a sequence of characters separated by a
 * delimiter.
 *
 * @author dmulloy2
 */

public class StringJoiner
{
	private static final String DEFAULT_DELIMITER = " ";
	public static final StringJoiner SPACE = new StringJoiner();

	private String delimiter;
	private StringBuilder builder;

	/**
	 * Constructs a new StringJoiner with the default delimiter.
	 */
	public StringJoiner()
	{
		this.delimiter = DEFAULT_DELIMITER;
		this.builder = new StringBuilder();
	}

	/**
	 * Constructs a new StringJoiner with a given delimiter.
	 *
	 * @param delimiter Delimiter
	 */
	public StringJoiner(String delimiter)
	{
		Validate.notNull(delimiter, "delimiter cannot be null!");

		this.delimiter = delimiter;
		this.builder = new StringBuilder();
	}

	/**
	 * Appends a given {@link String} to this StringJoiner.
	 *
	 * @param string String to append
	 * @return This, for chaining
	 */
	public final StringJoiner append(final String string)
	{
		Validate.notNull(string, "string cannot be null!");

		if (! string.isEmpty())
			builder.append(string).append(delimiter);
		return this;
	}

	/**
	 * Appends an {@link Iterable} set of {@link String}s.
	 *
	 * @param strings Strings to append
	 * @return This, for chaining
	 */
	public final StringJoiner appendAll(final Iterable<String> strings)
	{
		Validate.notNull(strings, "strings cannot be null!");

		for (String string : strings)
		{
			append(string);
		}

		return this;
	}

	/**
	 * Appends a given array of {@link String}s to this StringJoiner.
	 *
	 * @param strings Strings to append
	 * @return This, for chaining
	 */
	public final StringJoiner appendAll(final String... strings)
	{
		Validate.noNullElements(strings, "strings cannot have null elements!");

		for (String string : strings)
		{
			append(string);
		}

		return this;
	}

	/**
	 * Resets this {@link StringJoiner}'s string value.
	 *
	 * @return This, for chaining
	 */
	public final StringJoiner newString()
	{
		this.builder = new StringBuilder();
		return this;
	}

	/**
	 * Sets a new delimiter for this {@link StringJoiner} while keeping the
	 * string value.
	 *
	 * @param delimiter New delimiter
	 * @return This, for chaining
	 */
	public final StringJoiner setDelimiter(final String delimiter)
	{
		Validate.notNull(delimiter, "delimiter cannot be null!");

		this.delimiter = delimiter;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString()
	{
		if (builder.lastIndexOf(delimiter) >= 0)
			builder.delete(builder.lastIndexOf(delimiter), builder.length());

		return builder.toString();
	}
}
