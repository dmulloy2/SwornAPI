package net.dmulloy2;

import net.dmulloy2.chat.BaseComponent;
import net.dmulloy2.chat.ComponentSerializer;
import net.dmulloy2.types.ChatPosition;

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
		return org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers.getItem(stack.getType()).getName();
	}

	public static boolean sendMessage(Player player, ChatPosition position, BaseComponent... message)
	{
		net.minecraft.server.v1_16_R3.IChatBaseComponent component =
				net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(message));
		net.minecraft.server.v1_16_R3.ChatMessageType type = net.minecraft.server.v1_16_R3.ChatMessageType.a(position.getValue());
		((org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer) player).getHandle().playerConnection
				.sendPacket(new net.minecraft.server.v1_16_R3.PacketPlayOutChat(component, type, SERVER_UUID));
		return true;
	}
}
