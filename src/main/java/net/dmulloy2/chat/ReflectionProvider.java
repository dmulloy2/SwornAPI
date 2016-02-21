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

import net.dmulloy2.exception.ReflectionException;
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.types.ChatPosition;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class ReflectionProvider implements ChatProvider
{
	protected ReflectionProvider() { }

	@Override
	public void sendMessage(Player player, ChatPosition position, BaseComponent... message) throws ReflectionException
	{
		try
		{
			Class<?> serializerClass = getMinecraftClass("ChatSerializer", "IChatBaseComponent$ChatSerializer");
			Method serialize = serializerClass.getMethod("a", String.class);
			Object component = serialize.invoke(null, ComponentSerializer.toString(message));

			Class<?> chatPacketClass = getMinecraftClass("PacketPlayOutChat");
			Class<?> componentClass = getMinecraftClass("IChatBaseComponent");
			Constructor<?> constructor = chatPacketClass.getConstructor(componentClass);
			Object packet = constructor.newInstance(component);

			Method getHandle = player.getClass().getMethod("getHandle");
			Object entityPlayer = getHandle.invoke(player);

			Field playerConnectionField = entityPlayer.getClass().getField("playerConnection");
			Object playerConnection = playerConnectionField.get(entityPlayer);
			Class<?> packetClass = getMinecraftClass("Packet");
			Method sendPacket = playerConnection.getClass().getMethod("sendPacket", packetClass);
			sendPacket.invoke(playerConnection, packet);
		}
		catch (Throwable ex)
		{
			throw new ReflectionException("Sending chat packet to " + player.getName(), ex);
		}
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
}