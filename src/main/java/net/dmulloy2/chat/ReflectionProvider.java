/**
 * SwornAPI - common API for MineSworn and Shadowvolt plugins
 * Copyright (C) 2016 dmulloy2
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
package net.dmulloy2.chat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.types.ChatPosition;
import net.dmulloy2.util.Util;

/**
 * @author dmulloy2
 */

public class ReflectionProvider implements ChatProvider
{
	private Method serialize;
	private Constructor<?> packetConstructor;
	private Field connectionField;
	private Method sendPacket;

	protected ReflectionProvider() throws ReflectiveOperationException
	{
		Class<?> serializerClass = getMinecraftClass("ChatSerializer", "IChatBaseComponent$ChatSerializer");
		serialize = serializerClass.getMethod("a", String.class);

		Class<?> chatPacketClass = getMinecraftClass("PacketPlayOutChat");
		Class<?> componentClass = getMinecraftClass("IChatBaseComponent");
		packetConstructor = chatPacketClass.getConstructor(componentClass, byte.class);

		Class<?> entityPlayer = getMinecraftClass("EntityPlayer");
		connectionField = entityPlayer.getField("playerConnection");
		Class<?> playerConnection = connectionField.getType();
		Class<?> packetClass = getMinecraftClass("Packet");
		sendPacket = playerConnection.getMethod("sendPacket", packetClass);
	}

	@Override
	public boolean sendMessage(Player player, ChatPosition position, BaseComponent... message)
	{
		try
		{
			Object component = serialize.invoke(null, ComponentSerializer.toString(message));
			Object packet = packetConstructor.newInstance(component, position.getValue());

			Method getHandle = player.getClass().getMethod("getHandle");
			Object entityPlayer = getHandle.invoke(player);

			Object playerConnection = connectionField.get(entityPlayer);
			sendPacket.invoke(playerConnection, packet);
			return true;
		}
		catch (Throwable ex)
		{
			LogHandler.globalDebug(Util.getUsefulStack(ex, "sending chat packet to {0}", player.getName()));
			return false;
		}
	}

	@Override
	public String getName()
	{
		return "Reflection";
	}

	private static String VERSION;
	private static String NMS;

	private static boolean initialized;

	private static void initialize()
	{
		if (! initialized)
		{
			initialized = true;

			String serverPackage = Bukkit.getServer().getClass().getPackage().getName();
			VERSION = serverPackage.substring(serverPackage.lastIndexOf('.') + 1);
			NMS = "net.minecraft.server." + VERSION + ".";
		}
	}

	private static Class<?> getMinecraftClass(String name)
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

	private static Class<?> getMinecraftClass(String name, String... aliases)
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