/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dmulloy2
 */

public final class TimeUtil
{
	private TimeUtil() { }

	public static String formatTimeDifference(long time1, long time2)
	{
		return formatTime(getTimeDifference(time1, time2));
	}

	public static long getTimeDifference(long time1, long time2)
	{
		return (time2 - time1);
	}

	public static String formatTime(long time)
	{
		StringBuilder ret = new StringBuilder();
		int days = (int) Math.floor(time / (1000 * 3600 * 24));
		int hours = (int) Math.floor((time % (1000 * 3600 * 24)) / (1000 * 3600));
		int minutes = (int) Math.floor((time % (1000 * 3600 * 24)) % (1000 * 3600) / (1000 * 60));
		int seconds = (int) Math.floor(time % (1000 * 3600 * 24) % (1000 * 3600) % (1000 * 60) / 1000);

		if (days != 0)
			ret.append(days + "d");
		if (hours != 0 || days != 0)
			ret.append(hours + "h");
		if (minutes != 0 || hours != 0 || days != 0)
			ret.append(minutes + "m");
		ret.append(seconds + "s");

		return ret.toString();
	}

	public static String getLongDateCurr()
	{
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm");
		Date date = new Date();
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(date);
	}

	public static String getSimpleDate(long time)
	{
		DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
		Date date = new Date(time);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(date);
	}

	public static long parseTime(String t) throws Exception
	{
		try
		{
			if (!t.matches("[0-9]+d[a-z]*"))
				return Math.round(Double.parseDouble(t) * 60) * 1000;
			else
			{
				Pattern dayPattern = Pattern.compile("([0-9]+)d[a-z]*", Pattern.CASE_INSENSITIVE);
				Matcher m = dayPattern.matcher(t);

				if (m.matches())
				{
					return Integer.parseInt(m.group(1)) * 24 * 60 * 60 * 1000;
				}
			}
		}
		catch (NumberFormatException e)
		{
			Pattern hourPattern = Pattern.compile("([0-9]+)h[a-z]*", Pattern.CASE_INSENSITIVE);
			Pattern minPattern = Pattern.compile("([0-9]+)m[a-z]*", Pattern.CASE_INSENSITIVE);
			Pattern secPattern = Pattern.compile("([0-9]+)s[a-z]*", Pattern.CASE_INSENSITIVE);

			Matcher m = hourPattern.matcher(t);

			if (m.matches())
			{
				return Integer.parseInt(m.group(1)) * 60 * 60 * 1000;
			}

			m = minPattern.matcher(t);

			if (m.matches())
			{
				return Integer.parseInt(m.group(1)) * 60 * 1000;
			}

			m = secPattern.matcher(t);

			if (m.matches())
			{
				return Integer.parseInt(m.group(1)) * 1000;
			}
		}

		throw new Exception("badtime");
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