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

/**
 * @author dmulloy2
 */

public interface ChatProvider
{
	/**
	 * Sends a JSON chat message to a given player
	 * 
	 * @param player Player to send the message to
	 * @param position Position to display the message
	 * @param message Message to send
	 * @return True if it was sent, false if not
	 */
	boolean sendMessage(Player player, ChatPosition position, BaseComponent... message);

	/**
	 * Gets the name of this provider
	 * @return The name
	 */
	String getName();
}