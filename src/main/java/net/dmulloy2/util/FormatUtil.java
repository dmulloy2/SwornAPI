/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.util;

import java.io.File;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import net.dmulloy2.types.RainbowColors;
import net.dmulloy2.types.StringJoiner;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

/**
 * Util used for general formatting.
 *
 * @author dmulloy2
 */

public class FormatUtil
{
	private FormatUtil() { }

	/**
	 * Formats a given string with its objects.
	 *
	 * @param format Base string
	 * @param objects Objects to format in
	 * @return Formatted string
	 * @see {@link MessageFormat#format(String, Object...)}
	 */
	public static String format(String format, Object... objects)
	{
		try
		{
			format = MessageFormat.format(format, objects);
		} catch (Throwable ex) { }

		return replaceColors(format);
	}

	/**
	 * Replaces color codes in a given string. Includes rainbow.
	 *
	 * @param message Message to replace color codes in
	 * @return Formatted chat message
	 */
	public static String replaceColors(String message)
	{
		// Rainbow
		message = message.replaceAll("(&([zZ]))", "&z");
		if (message.contains("&z"))
		{
			StringBuilder ret = new StringBuilder();
			String[] ss = message.split("&z");
			ret.append(ss[0]);
			ss[0] = null;

			for (String s : ss)
			{
				if (s != null)
				{
					int index = 0;
					while (index < s.length() && s.charAt(index) != '&')
					{
						ret.append("&" + RainbowColors.getColor(index % RainbowColors.values().length));
						ret.append(s.charAt(index));
						index++;
					}

					if (index < s.length())
					{
						ret.append(s.substring(index));
					}
				}
			}

			message = ret.toString();
		}

		// Format the colors
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	/**
	 * Returns a user-friendly representation of a given Object. This is mostly
	 * used for {@link Enum} constants.
	 * <p>
	 * If the object or any of its superclasses (minus Object) do not implement
	 * a toString() method, the object's simple name will be returned.
	 *
	 * @param obj Object to get the user-friendly representation of
	 * @return A user-friendly representation of the given Object.
	 */
	public static String getFriendlyName(Object obj)
	{
		try
		{
			// Clever little method to check if the method isn't declared by a
			// class other than Object.
			Method method = ReflectionUtil.getMethod(obj.getClass(), "toString");
			if (method.getDeclaringClass().getSuperclass() == null)
			{
				return obj.getClass().getSimpleName();
			}
		} catch (Throwable ex) { }

		return getFriendlyName(obj.toString());
	}

	/**
	 * Returns a user-friendly version of a given String.
	 *
	 * @param string String to get the user-friendly version of
	 * @return A user-friendly version of the given String.
	 */
	public static String getFriendlyName(String string)
	{
		String ret = string.toLowerCase();
		ret = ret.replaceAll("_", " ");
		return WordUtils.capitalize(ret);
	}

	/**
	 * Returns the proper article of a given string
	 *
	 * @param string String to get the article for
	 * @return The proper article of a given string
	 */
	public static String getArticle(String string)
	{
		string = string.toLowerCase();
		if (string.startsWith("a") || string.startsWith("e") || string.startsWith("i") || string.startsWith("o") || string.startsWith("u"))
			return "an";

		return "a";
	}

	/**
	 * Returns the proper plural of a given string
	 *
	 * @param string String to get the plural for
	 * @return The proper plural of a given string
	 */
	public static String getPlural(String string, int amount)
	{
		amount = Math.abs(amount);
		if (amount == 0 || amount > 1)
		{
			if (! string.toLowerCase().endsWith("s"))
				return string + "s";
		}

		return string;
	}

	/**
	 * Joins together multiple given strings with the given glue using the
	 * {@link StringJoiner} class.
	 *
	 * @param glue String to join the args together with
	 * @param args Strings to join together
	 * @return Multiple strings joined together with the given glue.
	 * @see {@link StringJoiner}
	 */
	public static String join(String glue, String... args)
	{
		return new StringJoiner(glue).appendAll(args).toString();
	}

	/**
	 * Returns the given {@link File}'s name with the extension omitted.
	 *
	 * @param file {@link File}
	 * @param extension File extension
	 * @return The file's name with the extension omitted
	 */
	public static String trimFileExtension(File file, String extension)
	{
		int index = file.getName().lastIndexOf(extension);
		return index > 0 ? file.getName().substring(0, index) : file.getName();
	}
}