/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.chat;

import java.util.Arrays;
import java.util.List;

import net.dmulloy2.reflection.ReflectionUtil;
import net.dmulloy2.reflection.WrappedChatPacket;
import net.dmulloy2.reflection.WrappedChatSerializer;
import net.dmulloy2.types.Versioning.Version;

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
				if (SUPPORTED_VERSIONS.contains(ReflectionUtil.getClientVersion(player)))
				{
					WrappedChatSerializer serializer = new WrappedChatSerializer();
					Object chatComponent = serializer.serialize(ComponentSerializer.toString(message));

					WrappedChatPacket packet = new WrappedChatPacket(chatComponent);
					packet.send(player);
					return;
				}
			} catch (Throwable ex) { }
		}

		sender.sendMessage(BaseComponent.toLegacyText(message));
	}

	private static final List<Version> SUPPORTED_VERSIONS = Arrays.asList(
			Version.MC_17, Version.MC_18
	);
}