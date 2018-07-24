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

import net.dmulloy2.Volatile;
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.types.ChatPosition;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

import static net.dmulloy2.util.ReflectionUtil.getMinecraftClass;

/**
 * @author dmulloy2
 */

public class NMSProvider implements ChatProvider
{
	private Method serialize;
	private Constructor<?> packetConstructor;
	private Field connectionField;
	private Method getMessageType;
	private Method sendPacket;

	private boolean reflected;

	private void setupReflection() throws ReflectiveOperationException
	{
		if (reflected) return;
		reflected = true;

		Class<?> serializerClass = getMinecraftClass("ChatSerializer", "IChatBaseComponent$ChatSerializer");
		serialize = serializerClass.getMethod("a", String.class);

		Class<?> chatPacketClass = getMinecraftClass("PacketPlayOutChat");
		Class<?> componentClass = getMinecraftClass("IChatBaseComponent");
		Class<?> messageTypeClass = getMinecraftClass("ChatMessageType");
		getMessageType = messageTypeClass.getMethod("a", byte.class);
		packetConstructor = chatPacketClass.getConstructor(componentClass, messageTypeClass);

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
			Volatile.sendMessage(player, position, message);
			return true;
		} catch (Exception ex)
		{
			try
			{
				setupReflection();

				Object component = serialize.invoke(null, ComponentSerializer.toString(message));
				Object messageType = getMessageType.invoke(null, position.getValue());
				Object packet = packetConstructor.newInstance(component, messageType);

				Method getHandle = player.getClass().getMethod("getHandle");
				Object entityPlayer = getHandle.invoke(player);

				Object playerConnection = connectionField.get(entityPlayer);
				sendPacket.invoke(playerConnection, packet);
				return true;
			} catch (Exception ex2)
			{
				LogHandler.globalDebug(Util.getUsefulStack(ex2, "sending chat packet to {0}", player.getName()));
				return false;
			}
		}
	}

	@Override
	public String getName()
	{
		return "NMS";
	}
}