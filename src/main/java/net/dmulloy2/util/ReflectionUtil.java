/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import net.dmulloy2.exception.ReflectionException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Util for dealing with Java Reflection.
 *
 * @author dmulloy2
 */

public class ReflectionUtil
{
	private ReflectionUtil() { }

	/**
	 * Attempts to get the NMS (net.minecraft.server) class with this name.
	 * While this does cross versions, it's important to note that this class
	 * may have been changed or removed.
	 *
	 * @param name Class Name
	 * @return NMS class, or null if none exists
	 */
	public static final Class<?> getNMSClass(String name)
	{
		String serverPackage = Bukkit.getServer().getClass().getPackage().getName();
		String version = serverPackage.substring(serverPackage.lastIndexOf('.') + 1);
		name = "net.minecraft.server." + version + "." + name;

		try
		{
			return Class.forName(name);
		} catch (Throwable ex) { }
		return null;
	}

	/**
	 * Attempts to get the OBC (org.bukkit.craftbukkit) class with this name.
	 * While this does cross versions, it's important to note that this class
	 * may have been changed or removed.
	 *
	 * @param name Class Name
	 * @return OBC class, or null if none exists
	 */
	public static final Class<?> getOBCClass(String name)
	{
		String serverPackage = Bukkit.getServer().getClass().getPackage().getName();
		String version = serverPackage.substring(serverPackage.lastIndexOf('.') + 1);
		name = "org.bukkit.craftbukkit." + version + "." + name;

		try
		{
			return Class.forName(name);
		} catch (Throwable ex) { }
		return null;
	}

	/**
	 * Gets a {@link Field} in a given {@link Class} object.
	 *
	 * @param clazz Class object
	 * @param name Field nameame
	 * @return The field, or null if none exists.
	 */
	public static final Field getField(Class<?> clazz, String name)
	{
		try
		{
			return clazz.getDeclaredField(name);
		} catch (Throwable ex) { }
		return null;
	}

	/**
	 * Checks if a field is declared in a given {@link Class}
	 *
	 * @param clazz Class object
	 * @param name Name of variable
	 * @return Whether or not the field is declared
	 */
	public static final boolean isDeclaredField(Class<?> clazz, String name)
	{
		return getField(clazz, name) != null;
	}

	/**
	 * Gets a {@link Method} in a given {@link Class} object with the specified
	 * args.
	 *
	 * @param clazz Class object
	 * @param name Method name
	 * @param args Arguments
	 * @return The method, or null if none exists.
	 */
	public static final Method getMethod(Class<?> clazz, String name, Class<?>... args)
	{
		for (Method method : clazz.getMethods())
		{
			if (method.getName().equals(name) && Arrays.equals(args, method.getParameterTypes()))
				return method;
		}

		return null;
	}

	/**
	 * Gets a {@link Method} in a given {@link Class} object.
	 *
	 * @param clazz Class object
	 * @param name Method name
	 * @return The method, or null if none exists.
	 */
	public static final Method getMethod(Class<?> clazz, String name)
	{
		for (Method method : clazz.getMethods())
		{
			if (method.getName().equals(name))
				return method;
		}

		return null;
	}

	/**
	 * Gets the handle of a given object. This only works for classes that
	 * declare the getHandle() method, like CraftPlayer.
	 *
	 * @param object Object to get the handle for
	 * @return The handle, or null if none exists
	 */
	public static final Object getHandle(Object object)
	{
		Method getHandle = getMethod(object.getClass(), "getHandle");

		try
		{
			return getHandle.invoke(object, new Object[0]);
		} catch (Throwable ex) { }
		return null;
	}

	/**
	 * Sends a packet to a {@link Player}
	 *
	 * @param player Player to send the packet to
	 * @param packet Packet to send
	 * @throws ReflectionException If something goes wrong
	 */
	// TODO: Keep up to date with MC versions. 1.7.10
	public static final void sendPacket(Player player, Object packet) throws ReflectionException
	{
		try
		{
			Object nmsPlayer = getHandle(player);
			Field playerConnectionField = getField(nmsPlayer.getClass(), "playerConnection");
			Object playerConnection = playerConnectionField.get(nmsPlayer);
			Method sendPacket = getMethod(playerConnection.getClass(), "sendPacket");
			sendPacket.invoke(playerConnection, packet);
		}
		catch (Throwable ex)
		{
			throw new ReflectionException("Sending packet to " + player.getName(), ex);
		}
	}
}