/**
 * (c) 2015 dmulloy2
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
	 * Returns whether or not an Object can be parsed into an {@code int}.
	 *
	 * @param object Object to parse
	 * @return True if it can be parsed, false if not
	 */
	public static boolean isInt(Object object)
	{
		return toInt(object) != -1;
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
	 * Returns whether or not an Object can be parsed into a {@code float}.
	 *
	 * @param object Object to parse
	 * @return True if it can be parsed, false if not
	 */
	public static boolean isFloat(Object object)
	{
		return toFloat(object) != -1;
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
	 * Returns whether or not an Object can be parsed into a {@code double}.
	 *
	 * @param object Object to parse
	 * @return True if it can be parsed, false if not
	 */
	public static boolean isDouble(Object object)
	{
		return toDouble(object) != -1;
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
	 * Returns whether or not an Object can be parsed into a {@code long}.
	 *
	 * @param object Object to parse
	 * @return True if it can be parsed, false if not
	 */
	public static boolean isLong(Object object)
	{
		return toLong(object) != -1;
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
	 * Returns whether or not an Object can be parsed into a {@code short}.
	 *
	 * @param object Object to parse
	 * @return True if it can be parsed, false if not
	 */
	public static boolean isShort(Object object)
	{
		return toShort(object) != -1;
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
	 * Returns whether or not an Object can be parsed into a {@code byte}.
	 *
	 * @param object Object to parse
	 * @return True if it can be parsed, false if not
	 */
	public static boolean isByte(Object object)
	{
		return toByte(object) != -1;
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
}