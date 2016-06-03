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

import org.bukkit.entity.Player;

import net.dmulloy2.types.ChatPosition;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 * @author dmulloy2
 */

public class SpigotProvider implements ChatProvider
{
	public SpigotProvider() throws NoSuchMethodException
	{
		// Make sure the method we're going to use actually exists
		Player.Spigot.class.getMethod("sendMessage", ChatMessageType.class, BaseComponent.class);
	}

	@Override
	public boolean sendMessage(Player player, ChatPosition position, net.dmulloy2.chat.BaseComponent... message)
	{
		player.spigot().sendMessage(toBungeeType(position), toBungeeComponents(message));
		return true;
	}

	// Should always be 1:1
	private ChatMessageType toBungeeType(ChatPosition position)
	{
		switch (position)
		{
			case ACTION_BAR:
				return ChatMessageType.ACTION_BAR;
			case CHAT:
				return ChatMessageType.CHAT;
			case SYSTEM:
				return ChatMessageType.SYSTEM;	
		}

		throw new IllegalArgumentException("Could not find bungee equivalent for " + position);
	}

	private BaseComponent[] toBungeeComponents(net.dmulloy2.chat.BaseComponent[] message)
	{
		return net.md_5.bungee.chat.ComponentSerializer.parse(ComponentSerializer.toString(message));
	}

	@Override
	public String getName()
	{
		return "Spigot";
	}
}