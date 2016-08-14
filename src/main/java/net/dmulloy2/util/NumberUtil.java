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

import java.text.DecimalFormat;

/**
 * Util for managing Numbers.
 *
 * @author dmulloy2
 */

public class NumberUtil
{
	private NumberUtil() { }

	/**
	 * Parses an {@code int} from an Object.
	 *
	 * @param object Object to parse
	 * @return Parsed integer, or -1 if parsing failed
	 */
	public static int toInt(Object object)
	{
		if (object instanceof Number)
		{
			return ((Number) object).intValue();
		}

		try
		{
			return Integer.valueOf(object.toString());
		} catch (Throwable ex) { }
		return -1;
	}

	/**
	 * Returns whether or not an Object can be parsed as an Integer.
	 *
	 * @param object Object to parse
	 * @return True if it can be parsed, false if not
	 */
	public static boolean isInt(Object object)
	{
		try
		{
			Integer.valueOf(object.toString());
			return true;
		}
		catch (NumberFormatException ex)
		{
			return false;
		}
	}

	/**
	 * Parses an {@code int} from an Object.
	 *
	 * @param object Object to parse
	 * @return Parsed float, or -1 if parsing failed
	 */
	public static float toFloat(Object object)
	{
		if (object instanceof Number)
		{
			return ((Number) object).floatValue();
		}

		try
		{
			return Float.valueOf(object.toString());
		} catch (Throwable ex) { }
		return -1;
	}

	/**
	 * Returns whether or not an Object can be parsed as a Float.
	 *
	 * @param object Object to parse
	 * @return True if it can be parsed, false if not
	 */
	public static boolean isFloat(Object object)
	{
		try
		{
			Float.valueOf(object.toString());
			return true;
		}
		catch (NumberFormatException ex)
		{
			return false;
		}
	}

	/**
	 * Parses a {@code double} from an Object.
	 *
	 * @param object Object to parse
	 * @return Parsed double, or -1 if parsing failed
	 */
	public static double toDouble(Object object)
	{
		if (object instanceof Number)
		{
			return ((Number) object).doubleValue();
		}

		try
		{
			return Double.valueOf(object.toString());
		} catch (Throwable ex) { }
		return -1;
	}

	/**
	 * Returns whether or not an Object can be parsed as a Double
	 *
	 * @param object Object to parse
	 * @return True if it can be parsed, false if not
	 */
	public static boolean isDouble(Object object)
	{
		try
		{
			Double.valueOf(object.toString());
			return true;
		}
		catch (NumberFormatException ex)
		{
			return false;
		}
	}

	/**
	 * Parses a {@code long} from an Object.
	 *
	 * @param object Object to parse
	 * @return Parsed long, or -1 if parsing failed
	 */
	public static long toLong(Object object)
	{
		if (object instanceof Number)
		{
			return ((Number) object).longValue();
		}

		try
		{
			return Long.valueOf(object.toString());
		} catch (Throwable ex) { }
		return -1;
	}

	/**
	 * Returns whether or not an Object can be parsed as a Long.
	 *
	 * @param object Object to parse
	 * @return True if it can be parsed, false if not
	 */
	public static boolean isLong(Object object)
	{
		try
		{
			Long.valueOf(object.toString());
			return true;
		}
		catch (NumberFormatException ex)
		{
			return false;
		}
	}

	/**
	 * Parses a {@code short} from an Object.
	 *
	 * @param object Object to parse
	 * @return Parsed short, or -1 if parsing failed
	 */
	public static short toShort(Object object)
	{
		if (object instanceof Number)
		{
			return ((Number) object).shortValue();
		}

		try
		{
			return Short.valueOf(object.toString());
		} catch (Throwable ex) { }
		return -1;
	}

	/**
	 * Returns whether or not an Object can be parsed as a Short.
	 *
	 * @param object Object to parse
	 * @return True if it can be parsed, false if not
	 */
	public static boolean isShort(Object object)
	{
		try
		{
			Short.valueOf(object.toString());
			return true;
		}
		catch (NumberFormatException ex)
		{
			return false;
		}
	}

	/**
	 * Parses an {@code int} from an Object.
	 *
	 * @param object Object to parse
	 * @return Parsed byte, or -1 if parsing failed
	 */
	public static byte toByte(Object object)
	{
		if (object instanceof Number)
		{
			return ((Number) object).byteValue();
		}

		try
		{
			return Byte.valueOf(object.toString());
		} catch (Throwable ex) { }
		return -1;
	}

	/**
	 * Returns whether or not an Object can be parsed as a Byte.
	 *
	 * @param object Object to parse
	 * @return True if it can be parsed, false if not
	 */
	public static boolean isByte(Object object)
	{
		try
		{
			Byte.valueOf(object.toString());
			return true;
		}
		catch (NumberFormatException ex)
		{
			return false;
		}
	}

	/**
	 * Returns the given double, rounded to a given number of places.
	 *
	 * @param d Double to round
	 * @param num Places to round to
	 * @return The given double, rounded to a given number of places
	 */
	public static double roundNumDecimals(double d, int num)
	{
		StringBuilder format = new StringBuilder("#.");
		for (int i = 0; i < num; i++)
			format.append("#");
		DecimalFormat f = new DecimalFormat(format.toString());
		return toDouble(f.format(d));
	}

	/**
	 * Rounds a given number up to the nearest multiple of another number.
	 * 
	 * @param number Number to round up
	 * @param multipleOf Rounds to a multiple of this number
	 * @return The nearest multiple
	 */
	public static int roundUp(double number, int multipleOf)
	{
		return (int) Math.ceil(number / multipleOf) * multipleOf;
	}
}
