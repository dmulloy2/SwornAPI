package net.dmulloy2;

import net.dmulloy2.chat.BaseComponent;
import net.dmulloy2.chat.ComponentSerializer;
import net.dmulloy2.types.ChatPosition;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Provides better performance than reflection
 * @author dmulloy2
 */
public class Volatile
{
	public static String getName(ItemStack stack)
	{
		return org.bukkit.craftbukkit.v1_13_R1.util.CraftMagicNumbers.getItem(stack.getType()).getName();
	}

	public static boolean sendMessage(Player player, ChatPosition position, BaseComponent... message)
	{
		net.minecraft.server.v1_13_R1.IChatBaseComponent component =
				net.minecraft.server.v1_13_R1.IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(message));
		net.minecraft.server.v1_13_R1.ChatMessageType type = net.minecraft.server.v1_13_R1.ChatMessageType.a(position.getValue());
		((org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer) player).getHandle().playerConnection
				.sendPacket(new net.minecraft.server.v1_13_R1.PacketPlayOutChat(component, type));
		return true;
	}
}
