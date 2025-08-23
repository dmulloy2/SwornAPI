package net.dmulloy2.swornapi.util;

public final class Validate
{
	private Validate() { }

	public static void notNull(Object object, String message)
	{
		if (object == null)
		{
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEmpty(String string, String message)
	{
		if (string == null || string.isEmpty())
		{
			throw new IllegalArgumentException(message);
		}
	}

	public static void noNullElements(Object[] args, String error)
	{
		if (args == null)
		{
			return;
		}

		for (Object arg : args)
		{
			if (arg == null)
			{
				throw new IllegalArgumentException(error);
			}
		}
	}

	public static void isTrue(boolean bool, String error)
	{
		if (!bool)
		{
			throw new IllegalArgumentException(error);
		}
	}
}
