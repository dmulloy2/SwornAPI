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
package net.dmulloy2.swornapi.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents transformations done to config values.
 * 
 * @author dmulloy2
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValueOptions
{
	/**
	 * An array of standard value options.
	 * 
	 * @return Standard value options
	 */
	ValueOption value();

	/**
	 * Whether or not to allow null values. Defaults to false.
	 * 
	 * @return True or false
	 */
	boolean allowNull() default false;

	/**
	 * An array of custom value options. Options provided here must have a
	 * static method, <code>public static Object convert(Object)</code>, but
	 * this is unenforcable due to how Java handles annotations.
	 * 
	 * @return Custom value options
	 */
	Class<?>[] custom() default {};

	/**
	 * Represents a standard value option.
	 * 
	 * @author dmulloy2
	 */
	enum ValueOption
	{
		FORMAT,
		LIST_LOWER_CASE,
		LIST_UPPER_CASE,
		LOWER_CASE,
		MINUTE_TO_MILLIS,
		MINUTE_TO_TICKS,
		PARSE_ENUM,
		PARSE_ITEM,
		PARSE_ITEMS,
		PARSE_MATERIAL,
		PARSE_MATERIALS,
		SECOND_TO_MILLIS,
		SECOND_TO_TICKS
	}
}
