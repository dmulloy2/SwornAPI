/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.dmulloy2.chat.BaseComponent;
import net.dmulloy2.chat.ComponentSerializer;
import net.dmulloy2.exception.ReflectionException;

import org.bukkit.entity.Player;

/**
 * Util for dealing with JSON-based chat.
 * 
 * @author dmulloy2
 */

public class ChatUtil
{
	private ChatUtil() { }

	public static void sendMessage(Player player, BaseComponent... message) throws ReflectionException
	{
		sendChatPacket(player, ComponentSerializer.toString(message));
	}

	public static void sendMessage(Player player, BaseComponent message) throws ReflectionException
	{
		sendChatPacket(player, ComponentSerializer.toString(message));
	}

	private static void sendChatPacket(Player player, String jsonString) throws ReflectionException
	{
		try
		{
			Class<?> iChatBaseComponentClass = ReflectionUtil.getNMSClass("IChatBaseComponent");
			Class<?> packetPlayOutChatClass = ReflectionUtil.getNMSClass("PacketPlayOutChat");
			Class<?> chatSerializer = ReflectionUtil.getNMSClass("ChatSerializer");
			Method a = ReflectionUtil.getMethod(chatSerializer, "a");
			Object iChatBaseComponent = a.invoke(null, jsonString);
			Constructor<?> constructor = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass);
			Object packetPlayOutChat = constructor.newInstance(iChatBaseComponent);

			ReflectionUtil.sendPacket(player, packetPlayOutChat);
		}
		catch (Throwable ex)
		{
			throw new ReflectionException("Sending chat packet to " + player.getName());
		}
	}
}