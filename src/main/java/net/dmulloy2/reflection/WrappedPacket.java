/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.dmulloy2.exception.ReflectionException;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

// TODO: Keep up to date with MC versions. 1.8
public abstract class WrappedPacket extends AbstractWrapper
{
	private static final Field playerConnectionField;
	private static final Method sendPacket;

	static
	{
		Class<?> entityPlayer = ReflectionUtil.getNMSClass("EntityPlayer");
		playerConnectionField = ReflectionUtil.getField(entityPlayer, "playerConnection");

		Class<?> playerConnection = playerConnectionField.getDeclaringClass();
		sendPacket = ReflectionUtil.getMethod(playerConnection, "sendPacket");
	}
	
	public final void send(Player player) throws ReflectionException
	{
		try
		{
			Object nmsPlayer = ReflectionUtil.getHandle(player);
			Object playerConnection = playerConnectionField.get(nmsPlayer);
			sendPacket.invoke(playerConnection, nmsHandle);
		}
		catch (Throwable ex)
		{
			throw new ReflectionException(String.format("Sending packet to %s", player.getName()), ex);
		}
	}

	public final void sendToServer(Player player) throws ReflectionException
	{
		try
		{
			Object nmsPlayer = ReflectionUtil.getHandle(player);
			Object playerConnection = playerConnectionField.get(nmsPlayer);
			Method a = ReflectionUtil.getMethod(playerConnection.getClass(), "a", nmsClass);
			a.invoke(playerConnection, nmsHandle);
		}
		catch (Throwable ex)
		{
			throw new ReflectionException(String.format("Sending packet from %s to the server", player.getName()), ex);
		}
	}
}