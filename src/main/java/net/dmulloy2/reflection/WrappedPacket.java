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

// TODO: Keep up to date with MC versions. 1.8.3
public abstract class WrappedPacket extends AbstractWrapper
{
	public final void send(Player player) throws ReflectionException
	{
		try
		{
			Object nmsPlayer = ReflectionUtil.getHandle(player);
			Field playerConnectionField = ReflectionUtil.getField(nmsPlayer.getClass(), "playerConnection");
			Object playerConnection = playerConnectionField.get(nmsPlayer);
			Method sendPacket = ReflectionUtil.getMethod(playerConnection.getClass(), "sendPacket");
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
			Field playerConnectionField = ReflectionUtil.getField(nmsPlayer.getClass(), "playerConnection");
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