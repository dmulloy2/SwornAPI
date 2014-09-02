/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.util;

import net.dmulloy2.chat.BaseComponent;
import net.dmulloy2.chat.ComponentSerializer;
import net.dmulloy2.exception.ReflectionException;
import net.dmulloy2.reflection.WrappedChatPacket;
import net.dmulloy2.reflection.WrappedChatSerializer;

import org.apache.commons.lang.Validate;
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

	/**
	 * Sends a message to a {@link CommandSender}. This method attempts to send
	 * a JSON chat message if the sender is a player. If message sending fails,
	 * a legacy message will be sent.
	 *
	 * @param sender CommandSender to send the message to
	 * @param message Message to send
	 */
	public static final void sendMessage(CommandSender sender, BaseComponent... message)
	{
		Validate.notNull(sender, "sender cannot be null!");

		if (sender instanceof Player && ReflectionUtil.isReflectionSupported())
		{
			try
			{
				sendChatPacket((Player) sender, ComponentSerializer.toString(message));
				return;
			} catch (Throwable ex) { }
		}

		sender.sendMessage(BaseComponent.toLegacyText(message));
	}

	private static final void sendChatPacket(Player player, String json) throws ReflectionException
	{
		try
		{
			Validate.notNull(player, "player cannot be null!");
			Validate.notEmpty(json, "json cannot be null or empty!");

			WrappedChatSerializer serializer = new WrappedChatSerializer();
			Object chatComponent = serializer.serialize(json);

			WrappedChatPacket packet = new WrappedChatPacket(chatComponent);
			packet.send(player);
		}
		catch (Throwable ex)
		{
			throw ReflectionException.fromThrowable("Sending chat packet to " + player.getName(), ex);
		}
	}
}