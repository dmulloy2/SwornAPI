/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.util;

import net.dmulloy2.chat.BaseComponent;
import net.dmulloy2.chat.ComponentSerializer;
import net.dmulloy2.exception.ReflectionException;
import net.dmulloy2.reflection.WrappedChatPacket;
import net.dmulloy2.reflection.WrappedChatSerializer;

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

	public static final void sendMessage(CommandSender sender, BaseComponent... message)
	{
		if (sender instanceof Player)
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
			WrappedChatSerializer serializer = new WrappedChatSerializer();
			Object chatComponent = serializer.serialize(json);

			WrappedChatPacket packet = new WrappedChatPacket(chatComponent);
			packet.send(player);
		}
		catch (Throwable ex)
		{
			throw new ReflectionException("Sending chat packet to " + player.getName(), ex);
		}
	}
}