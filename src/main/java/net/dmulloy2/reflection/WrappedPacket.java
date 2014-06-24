/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.reflection;

import net.dmulloy2.exception.ReflectionException;
import net.dmulloy2.util.ReflectionUtil;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public abstract class WrappedPacket extends AbstractWrapper
{
	public final void send(Player player) throws ReflectionException
	{
		ReflectionUtil.sendPacket(player, nmsHandle);
	}
}