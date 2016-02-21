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

import net.dmulloy2.exception.ReflectionException;
import net.dmulloy2.types.ChatPosition;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

/**
 * @author dmulloy2
 */

public class ProtocolLibProvider implements ChatProvider
{
	private final ProtocolManager manager;

	protected ProtocolLibProvider()
	{
		this.manager = ProtocolLibrary.getProtocolManager();
	}

	@Override
	public void sendMessage(Player player, ChatPosition position, BaseComponent... message) throws ReflectionException
	{
		try
		{
			PacketContainer packet = manager.createPacket(PacketType.Play.Server.CHAT);

			// Write our message to the packet
			WrappedChatComponent component = WrappedChatComponent.fromJson(ComponentSerializer.toString(message));
			packet.getChatComponents().write(0, component);

			// Nullify Spigot's components
			packet.getModifier().write(1, null);

			// Write the position
			packet.getBytes().write(0, position != null ? position.getValue() : 1);

			// Send the packet
			manager.sendServerPacket(player, packet);
		}
		catch (Throwable ex)
		{
			throw new ReflectionException("Sending chat packet to " + player.getName(), ex);
		}
	}
}