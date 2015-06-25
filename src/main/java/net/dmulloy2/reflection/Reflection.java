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
package net.dmulloy2.reflection;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import net.dmulloy2.chat.BaseComponent;
import net.dmulloy2.chat.ComponentSerializer;
import net.dmulloy2.exception.ReflectionException;
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.types.Versioning;
import net.dmulloy2.types.Versioning.Version;
import net.dmulloy2.util.Util;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

/**
 * @author dmulloy2
 */

public final class Reflection
{
	private Reflection() { }

	// ---- General Reflection
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

	/**
	 * Attempts to get a Minecraft (net.minecraft.server) class with a given
	 * name. While in theory this allows the crossing of versions, it is
	 * important to note that internal classes are frequently removed and/or
	 * reofbuscated.
	 *
	 * @param name Class Name
	 * @return Minecraft class, or null if none exists
	 */
	public static Class<?> getMinecraftClass(String name)
	{
		Validate.notNull(name, "name cannot be null!");

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

	/**
	 * Attempts to get a Minecraft (net.minecraft.server) class with a given
	 * name or alias. While in theory this allows the crossing of versions,
	 * it is important to note that internal classes are frequently moved
	 * and/or reofbsucated.
	 *
	 * @param name Class name
	 * @param aliases Aliases
	 * @return Minecraft class, or null if none exists
	 */
	public static Class<?> getMinecraftClass(String name, String... aliases)
	{
		Validate.notNull(name, "name cannot be null!");
		Validate.noNullElements(aliases, "aliases cannot contain null elements!");

		Class<?> clazz = getMinecraftClass(name);
		if (clazz != null)
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

	/**
	 * Attempts to get the CraftBukkit (org.bukkit.craftbukkit) class with a
	 * given name.
	 *
	 * @param name Class Name
	 * @return CraftBukkit class, or null if none exists
	 */
	public static Class<?> getCraftBukkitClass(String name)
	{
		Validate.notNull(name, "name cannot be null!");

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

	/**
	 * Retrieve the current package version.
	 * @return The current package version.
	 */
	public static String getVersion()
	{
		initialize();
		return VERSION;
	}

	/**
	 * Retrieve the current NMS (net.minecraft.server) package including the version.
	 * @return The current NMS package.
	 */
	public static String getMinecraftPackage()
	{
		initialize();
		return NMS;
	}

	/**
	 * Retrieve the current OBC (org.bukkit.craftbukkit) package including the version.
	 * @return The current OBC package.
	 */
	public static String getCraftBukkitPackage()
	{
		initialize();
		return OBC;
	}

	@SafeVarargs
	private static <T> Set<T> setUnion(T[]... array)
	{
		Set<T> result = new LinkedHashSet<T>();

		for (T[] elements : array)
		{
			for (T element : elements)
			{
				result.add(element);
			}
		}

		return result;
	}

	/**
	 * Retrieves all fields in declared order.
	 * 
	 * @param clazz Class to get the fields for
	 * @return Every field
	 */
	public static Set<Field> getFields(Class<?> clazz)
	{
		Validate.notNull(clazz, "clazz cannot be null!");
		return setUnion(clazz.getDeclaredFields(), clazz.getFields());
	}

	/**
	 * Retrieves all methods in declared order.
	 * 
	 * @param clazz Class to get the methods for
	 * @return Every method
	 */
	public static Set<Method> getMethods(Class<?> clazz)
	{
		Validate.notNull(clazz, "clazz cannot be null!");
		return setUnion(clazz.getDeclaredMethods(), clazz.getMethods());
	}

	/**
	 * Gets a {@link Field} in a given {@link Class} object.
	 *
	 * @param clazz Class object
	 * @param name Field name
	 * @return The field, or null if none exists.
	 */
	public static Field getField(Class<?> clazz, String name)
	{
		Validate.notNull(clazz, "clazz cannot be null!");
		Validate.notNull(name, "name cannot be null!");

		try
		{
			for (Field field : getFields(clazz))
			{
				if (field.getName().equals(name))
					return field;
			}
		} catch (Throwable ex) { }
		return null;
	}

	/**
	 * Gets a {@link Method} in a given {@link Class} object with the specified
	 * arguments.
	 *
	 * @param clazz Class object
	 * @param name Method name
	 * @param params Parameters
	 * @return The method, or null if none exists
	 */
	public static Method getMethod(Class<?> clazz, String name, Class<?>... params)
	{
		Validate.notNull(clazz, "clazz cannot be null!");
		Validate.notNull(name, "name cannot be null!");
		if (params == null)
			params = new Class<?>[0];

		for (Method method : getMethods(clazz))
		{
			if (method.getName().equals(name) && Arrays.equals(params, method.getParameterTypes()))
				return method;
		}

		return null;
	}

	/**
	 * Gets a {@link Method} in a given {@link Class} object.
	 *
	 * @param clazz Class object
	 * @param name Method name
	 * @return The method, or null if none exists
	 */
	public static Method getMethod(Class<?> clazz, String name)
	{
		Validate.notNull(clazz, "clazz cannot be null!");
		Validate.notNull(name, "name cannot be null!");

		for (Method method : getMethods(clazz))
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
	public static Object getHandle(Object object)
	{
		Validate.notNull(object, "object cannot be null!");

		Method getHandle = getMethod(object.getClass(), "getHandle");
		Validate.notNull(getHandle, object.getClass() + " does not declare a getHandle() method!");

		try
		{
			return getHandle.invoke(object, new Object[0]);
		}
		catch (Throwable ex)
		{
			LogHandler.globalDebug(Util.getUsefulStack(ex, "obtaining handle for {0}", object));
			return null;
		}
	}

	/**
	 * Prints the contents of a given Object to a given PrintStream.
	 * @param out PrintStream to print contents to
	 * @param obj Object to print contents of
	 */
	public static void printObject(PrintStream out, Object obj)
	{
		Class<?> clazz = obj.getClass();
		out.println(clazz.getSimpleName() + "[");

		for (Field field : getFields(clazz))
		{
			try
			{
				out.println("  " + field.getName() + " = " + field.get(obj));
			} catch (Throwable ex) { }
		}

		for (Method method : getMethods(clazz))
		{
			if (method.getParameterTypes().length == 0 && method.getReturnType() != Void.TYPE)
			{
				try
				{
					out.println("  " + method.getName() + " = " + method.invoke(obj));
				} catch (Throwable ex) { }
			}
		}

		out.println("]");
	}

	// ---- Versioning

	/**
	 * Whether or not a {@link Player} can reliably be sent packets. This works
	 * by checking the player's client {@link Version} against the supported
	 * version.
	 *
	 * @param player Player to check
	 * @return True
	 */
	public static boolean isReflectionSupported(Player player)
	{
		return Versioning.getVersion() == getClientVersion(player);
	}

	/**
	 * Returns the client {@link Version} a given {@link Player} is using.
	 *
	 * @param player Player to get version for
	 * @return Their client version
	 */
	public static final Version getClientVersion(Player player)
	{
		Validate.notNull(player, "player cannot be null!");

		// If the server is truly running 1.8, return it
		if (Versioning.getVersion() == Version.MC_18)
			return Version.MC_18;

		try
		{
			// If not, try to determine it
			Object handle = getHandle(player);
			Field playerConnectionField = getField(handle.getClass(), "playerConnection");
			Object playerConnection = playerConnectionField.get(handle);

			Field networkManagerField = getField(playerConnection.getClass(), "networkManager");
			Object networkManager = networkManagerField.get(playerConnection);

			Method getVersion = getMethod(networkManager.getClass(), "getVersion", new Class<?>[0]);
			int version = (int) getVersion.invoke(networkManager);
			switch (version)
			{
				case 4:
				case 5:
					return Version.MC_17;
				case 47:
					return Version.MC_18;
				default:
					return Version.UNKNOWN;
			}
		}
		catch (Throwable ex)
		{
			LogHandler.globalDebug(Util.getUsefulStack(ex, "obtaining version for {0}", player.getName()));
			return Version.UNKNOWN;
		}
	}

	// ---- ProtocolLib
	private static boolean triedProtocolLib, protocolLibEnabled;

	private static boolean isProtocolLibEnabled()
	{
		if (! triedProtocolLib)
		{
			triedProtocolLib = true;

			try
			{
				ProtocolLibrary.getProtocolManager();
				return protocolLibEnabled = true;
			}
			catch (Throwable ex)
			{
				LogHandler.globalDebug(Util.getUsefulStack(ex, "checking for ProtocolLib"));
			}
		}

		return protocolLibEnabled;
	}

	// ---- Packets

	/**
	 * Gets a chat packet with a given message using ProtocolLib or our
	 * wrappers.
	 * 
	 * @param components Chat message
	 * @return The chat packet
	 * @throws ReflectionException If reflection fails
	 */
	public static Packet getChatPacket(final BaseComponent... components) throws ReflectionException
	{
		// Try to use ProtocolLib
		if (isProtocolLibEnabled())
		{
			return new Packet()
			{
				@Override
				public void send(Player player) throws ReflectionException
				{
					try
					{
						ProtocolManager manager = ProtocolLibrary.getProtocolManager();
						PacketConstructor constructor = manager.createPacketConstructor(PacketType.Play.Server.CHAT,
								MinecraftReflection.getIChatBaseComponentClass());
						WrappedChatComponent component = WrappedChatComponent.fromJson(ComponentSerializer.toString(components));
						PacketContainer packet = constructor.createPacket(component.getHandle());
						manager.sendServerPacket(player, packet);
					}
					catch (Throwable ex)
					{
						throw new ReflectionException("Failed to send chat packet to " + player.getName(), ex);
					}
				}
			};
		}

		// Fall back to our less reliable wrappers
		LogHandler.globalDebug("Falling back to SwornAPI wrappers");
		WrappedChatSerializer serializer = new WrappedChatSerializer();
		Object chatComponent = serializer.serialize(ComponentSerializer.toString(components));
		return new WrappedChatPacket(chatComponent);
	}
}