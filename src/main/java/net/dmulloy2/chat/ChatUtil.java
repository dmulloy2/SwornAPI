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

import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.types.ChatPosition;
import net.dmulloy2.util.Util;

/**
 * Utility class for sending JSON chat.
 *
 * @author dmulloy2
 */

public class ChatUtil
{
	private ChatUtil() { }

	private static ChatProvider provider;

	static
	{
		provider = findProvider();
		LogHandler.globalDebug("Using {0} provider for JSON chat", provider.getName());
	}

	private static ChatProvider findProvider()
	{
		// See if the user has a preference first
		String name = System.getProperty("swornapi.chatprovider");
		if (name != null)
		{
			Provider preferred = Provider.fromName(name);
			if (preferred != null)
			{
				try
				{
					return preferred.getClazz().newInstance();
				}
				catch (Throwable ex)
				{
					LogHandler.globalDebug(Util.getUsefulStack(ex, "using preferred provider {0}", name));
				}
			}
		}

		// Go through ours
		for (Provider available : Provider.values())
		{
			try
			{
				return available.getClazz().newInstance();
			} catch (Throwable ex) { }
		}

		// Fall back to plain text
		return new PlainTextProvider();
	}

	@Getter
	@AllArgsConstructor
	private static enum Provider
	{
		SPIGOT("Spigot", SpigotProvider.class),
		PROTOCOLLIB("ProtocolLib", ProtocolLibProvider.class),
		REFLECTION("Reflection", ReflectionProvider.class),
		PLAINTEXT("Plaintext", PlainTextProvider.class);

		private String name;
		private Class<? extends ChatProvider> clazz;

		private static Provider fromName(String name)
		{
			name = name.toLowerCase();
			for (Provider provider : values())
			{
				if (provider.name.toLowerCase().equals(name))
					return provider;
			}

			return null;
		}
	}

	private static class PlainTextProvider implements ChatProvider
	{
		@Override
		public boolean sendMessage(Player player, ChatPosition position, BaseComponent... message)
		{
			// Delegate to the last statement in sendMessage
			return false;
		}

		@Override
		public String getName()
		{
			return "Plain text";
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
		Validate.notNull(sender, "sender cannot be null!");
		Validate.notNull(position, "position cannot be null!");
		Validate.notNull(message, "message cannot be null!");

		if (sender instanceof Player)
		{
			// JSON messages can only be sent to players
			if (sendMessageRaw((Player) sender, position, message))
				return;
		}

		// Fall back to plain text
		sender.sendMessage(TextComponent.toLegacyText(message));
	}

	/**
	 * Alias for {@link #sendMessageRaw(Player, ChatPosition, BaseComponent...)}. Defaults to {@link ChatPosition#SYSTEM}
	 */
	public static boolean sendMessageRaw(Player player, BaseComponent... message)
	{
		return sendMessageRaw(player, ChatPosition.SYSTEM, message);
	}

	/**
	 * Sends a JSON chat message to a given Player
	 *
	 * @param player Player to send the message to
	 * @param position Message position
	 * @param message Message to send
	 * @return True if it was sent, false if not
	 */
	public static boolean sendMessageRaw(Player player, ChatPosition position, BaseComponent... message)
	{
		Validate.notNull(player, "player cannot be null!");
		Validate.notNull(position, "position cannot be null!");
		Validate.notNull(message, "message cannot be null!");

		return provider.sendMessage(player, position, message);
	}
}