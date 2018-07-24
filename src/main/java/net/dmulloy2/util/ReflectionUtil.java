package net.dmulloy2.util;

import net.dmulloy2.handlers.LogHandler;

import org.bukkit.Bukkit;

public class ReflectionUtil
{
	private static String VERSION;
	private static String NMS;
	private static String OBC;

	private static boolean initialized;

	private static void initialize()
	{
		if (! initialized)
		{
			initialized = true;

			String serverPackage = Bukkit.getServer().getClass().getPackage().getName();
			VERSION = serverPackage.substring(serverPackage.lastIndexOf('.') + 1);
			NMS = "net.minecraft.server." + VERSION + ".";
			OBC = "org.bukkit.craftbukkit." + VERSION + ".";
		}
	}

	public static Class<?> getMinecraftClass(String name)
	{
		initialize();

		try
		{
			return Class.forName(NMS + name);
		}
		catch (Throwable ex)
		{
			LogHandler.globalDebug("Could not find Minecraft class {0}", NMS + name);
			return null;
		}
	}

	public static Class<?> getCraftClass(String name)
	{
		initialize();

		try
		{
			return Class.forName(OBC + name);
		}
		catch (Throwable ex)
		{
			LogHandler.globalDebug("Could not find CraftBukkit class {0}", NMS + name);
			return null;
		}
	}

	public static Class<?> getMinecraftClass(String name, String... aliases)
	{
		Class<?> clazz = getMinecraftClass(name);
		if (clazz == null)
		{
			for (String alias : aliases)
			{
				clazz = getMinecraftClass(alias);
				if (clazz != null)
					return clazz;
			}
		}

		return clazz;
	}
}
