/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.chat;

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

		if (sender instanceof Player)
		{
			try
			{
				Player player = (Player) sender;

				WrappedChatSerializer serializer = new WrappedChatSerializer();
				Object chatComponent = serializer.serialize(ComponentSerializer.toString(message));

				WrappedChatPacket packet = new WrappedChatPacket(chatComponent);
				packet.send(player);
				return;
			} catch (Throwable ex) { }
		}

		sender.sendMessage(BaseComponent.toLegacyText(message));
	}

	/**
	 * Sends a JSON chat message to a {@link CommandSender}. If message sending
	 * fails, a {@link ReflectionException} will be thrown.
	 *
	 * @param sender CommandSender to send the message to
	 * @param message Message to send
	 * @throws ReflectionException If sending fails
	 */
	public static final void sendMessageRaw(CommandSender sender, BaseComponent... message) throws ReflectionException
	{
		Validate.notNull(sender, "sender cannot be null!");

		if (sender instanceof Player)
		{
			Player player = (Player) sender;

			WrappedChatSerializer serializer = new WrappedChatSerializer();
			Object chatComponent = serializer.serialize(ComponentSerializer.toString(message));

			WrappedChatPacket packet = new WrappedChatPacket(chatComponent);
			packet.send(player);
		}
		else
		{
			throw new ReflectionException("JSON chat messages can only be sent to players.");
		}
	}
}