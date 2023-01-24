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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dmulloy2.swornapi.exception.BadTimeException;

/**
 * Util for dealing with time.
 *
 * @author dmulloy2
 */

public class TimeUtil
{
	private TimeUtil() { }

	/**
	 * Returns the formatted time difference between two times.
	 *
	 * @param time1 First time in milliseconds
	 * @param time2 Second time in milliseconds
	 * @return Formatted time difference
	 */
	public static String formatTimeDifference(long time1, long time2)
	{
		return formatTime(getTimeDifference(time1, time2));
	}

	/**
	 * Returns the absolute difference between two times.
	 *
	 * @param time1 First time in milliseconds
	 * @param time2 Second time in milliseconds
	 * @return Absolute time difference
	 */
	public static long getTimeDifference(long time1, long time2)
	{
		return Math.abs(time2 - time1);
	}

	/**
	 * Formats a given time.
	 *
	 * @param time Time in milliseconds
	 * @return Formatted time
	 */
	public static String formatTime(long time)
	{
		StringBuilder ret = new StringBuilder();
		int days = (int) Math.floor(time / (1000 * 3600 * 24));
		int hours = (int) Math.floor((time % (1000 * 3600 * 24)) / (1000 * 3600));
		int minutes = (int) Math.floor((time % (1000 * 3600 * 24)) % (1000 * 3600) / (1000 * 60));
		int seconds = (int) Math.floor(time % (1000 * 3600 * 24) % (1000 * 3600) % (1000 * 60) / 1000);

		if (days != 0)
			ret.append(days).append("d");
		if (hours != 0 || days != 0)
			ret.append(hours).append("h");
		if (minutes != 0 || hours != 0 || days != 0)
			ret.append(minutes).append("m");
		ret.append(seconds).append("s");

		return ret.toString();
	}

	/**
	 * Gets the current long date in a given time zone.
	 *
	 * @param timeZone Time zone, defaults to GMT
	 * @return The current long date
	 */
	public static String getLongDateCurr(String timeZone)
	{
		if (timeZone == null || timeZone.isEmpty())
			timeZone = "GMT";

		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm");
		Date date = new Date();
		dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
		return dateFormat.format(date);
	}

	/**
	 * Gets the current long date in GMT.
	 *
	 * @see TimeUtil#getLongDateCurr(String)
	 */
	public static String getLongDateCurr()
	{
		return getLongDateCurr("GMT");
	}

	/**
	 * Gets the current short date in a given time zone.
	 *
	 * @param time Time in milliseconds
	 * @param timeZone Time zone, defaults to GMT
	 * @return The current short date
	 */
	public static String getSimpleDate(long time, String timeZone)
	{
		if (timeZone == null || timeZone.isEmpty())
			timeZone = "GMT";

		DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
		Date date = new Date(time);
		dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
		return dateFormat.format(date);
	}

	/**
	 * Gets the simple date of a given time in GMT.
	 *
	 * @param time Time in milliseconds
	 * @see TimeUtil#getSimpleDate(long, String)
	 */
	public static String getSimpleDate(long time)
	{
		return getSimpleDate(time, "GMT");
	}

	/**
	 * Parses a given time.
	 *
	 * @param time Time
	 * @return The parsed time
	 * @throws BadTimeException If parsing fails
	 */
	public static long parseTime(String time) throws BadTimeException
	{
		try
		{
			if (! time.matches("[0-9]+d[a-z]*"))
				return Math.round(Double.parseDouble(time) * 60) * 1000;
			else
			{
				Pattern dayPattern = Pattern.compile("([0-9]+)d[a-z]*", Pattern.CASE_INSENSITIVE);
				Matcher m = dayPattern.matcher(time);

				if (m.matches())
				{
					return Integer.parseInt(m.group(1)) * 24 * 60 * 60 * 1000;
				}
			}
		}
		catch (NumberFormatException ex)
		{
			Pattern hourPattern = Pattern.compile("([0-9]+)h[a-z]*", Pattern.CASE_INSENSITIVE);
			Pattern minPattern = Pattern.compile("([0-9]+)m[a-z]*", Pattern.CASE_INSENSITIVE);
			Pattern secPattern = Pattern.compile("([0-9]+)s[a-z]*", Pattern.CASE_INSENSITIVE);

			Matcher m = hourPattern.matcher(time);

			if (m.matches())
			{
				return Integer.parseInt(m.group(1)) * 60 * 60 * 1000;
			}

			m = minPattern.matcher(time);

			if (m.matches())
			{
				return Integer.parseInt(m.group(1)) * 60 * 1000;
			}

			m = secPattern.matcher(time);

			if (m.matches())
			{
				return Integer.parseInt(m.group(1)) * 1000;
			}
		}
		catch (Throwable ex)
		{
			throw new BadTimeException("Failed to parse time", ex);
		}

		// Message for backwards compatibility.
		throw new BadTimeException("badtime");
	}

	// ---- Conversion Methods

	public static long toTicks(Number seconds)
	{
		return seconds.intValue() * 20;
	}

	public static int toSeconds(Number ticks)
	{
		return ticks.intValue() / 20;
	}
}
