package net.dmulloy2.swornapi.util;

public final class Validate
{
	private Validate() { }

	public static <T> T notNull(T object, String message)
	{
		if (object == null)
		{
			throw new IllegalArgumentException(message);
		}

		return object;
	}

	public static String notEmpty(String string, String message)
	{
		if (string == null || string.isEmpty())
		{
			throw new IllegalArgumentException(message);
		}

		return string;
	}

	public static <T> T[] noNullElements(T[] args, String error)
	{
		if (args == null)
		{
			return args;
		}

		for (Object arg : args)
		{
			if (arg == null)
			{
				throw new IllegalArgumentException(error);
			}
		}

		return args;
	}

	public static boolean isTrue(boolean bool, String error)
	{
		if (!bool)
		{
			throw new IllegalArgumentException(error);
		}

		return bool;
	}
}
