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
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.types.ChatPosition;
import net.dmulloy2.util.Util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Util for dealing with JSON-based chat.
 *
 * @author dmulloy2
 */

public class ChatUtil
{
	private ChatUtil() { }

	private static ChatProvider provider;

	static
	{
		try
		{
			provider = new ProtocolLibProvider();
		}
		catch (Throwable ex)
		{
			provider = new ReflectionProvider();
		}
	}

	/**
	 * Alias for {@link #sendMessage(CommandSender, ChatPosition, BaseComponent...)}. Defaults to {@link ChatPosition#SYSTEM}.
	 */
	public static void sendMessage(CommandSender sender, BaseComponent... message)
	{
		sendMessage(sender, ChatPosition.SYSTEM, message);
	}

	/**
	 * Sends a message to a {@link CommandSender}. This method attempts to send
	 * a JSON chat message if the sender is a player. If message sending fails,
	 * a legacy message will be sent.
	 *
	 * @param sender CommandSender to send the message to
	 * @param position Message position
	 * @param message Message to send
	 */
	public static void sendMessage(CommandSender sender, ChatPosition position, BaseComponent... message)
	{
		if (sender instanceof Player)
		{
			try
			{
				sendMessageRaw(sender, position, message);
				return;
			}
			catch (Throwable ex)
			{
				LogHandler.globalDebug(Util.getUsefulStack(ex, "sending message {0} to {1}", ComponentSerializer.toString(message), sender.getName()));
			}
		}

		sender.sendMessage(TextComponent.toLegacyText(message));
	}

	/**
	 * Alias for {@link #sendMessageRaw(CommandSender, ChatPosition, BaseComponent...)}. Defaults to {@link ChatPosition#SYSTEM}
	 */
	public static void sendMessageRaw(CommandSender sender, BaseComponent... message) throws ReflectionException
	{
		sendMessageRaw(sender, ChatPosition.SYSTEM, message);
	}

	/**
	 * Sends a JSON chat message to a {@link CommandSender}. If message sending
	 * fails, a {@link ReflectionException} will be thrown.
	 *
	 * @param sender CommandSender to send the message to
	 * @param position Message position
	 * @param message Message to send
	 * @throws ReflectionException If sending fails
	 */
	public static void sendMessageRaw(CommandSender sender, ChatPosition position, BaseComponent... message) throws ReflectionException
	{
		if (sender instanceof Player)
		{
			provider.sendMessage((Player) sender, position, message);
		}
		else
		{
			throw new ReflectionException("JSON chat messages can only be sent to players.");
		}
	}
}