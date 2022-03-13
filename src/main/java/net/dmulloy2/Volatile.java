package net.dmulloy2;

import net.dmulloy2.chat.BaseComponent;
import net.dmulloy2.chat.ComponentSerializer;
import net.dmulloy2.types.ChatPosition;

import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Provides better performance than reflection
 * @author dmulloy2
 */
public class Volatile
{
	private static final UUID SERVER_UUID = new UUID(0L, 0L);

	public static String getName(ItemStack stack)
	{
		return org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers.getItem(stack.getType()).a();
	}

	public static boolean sendMessage(Player player, ChatPosition position, BaseComponent... message)
	{
		IChatBaseComponent component = IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(message));
		ChatMessageType type = ChatMessageType.a(position.getValue());
		((org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer) player).getHandle().b.a(new PacketPlayOutChat(component, type, SERVER_UUID));
		return true;
	}
}
