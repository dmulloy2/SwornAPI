/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.reflection;

import net.dmulloy2.exception.ReflectionException;

/**
 * @author dmulloy2
 */

// TODO: Keep up to date with MC versions. 1.8.3
public class WrappedChatPacket extends WrappedPacket
{
	private static final String CLASS_NAME = "PacketPlayOutChat";
	private static final Class<?> CHAT_COMPONENT = ReflectionUtil.getMinecraftClass("IChatBaseComponent");

	public WrappedChatPacket(Object chatComponent) throws ReflectionException
	{
		try
		{
			this.nmsClass = ReflectionUtil.getMinecraftClass(CLASS_NAME);
			this.constructor = nmsClass.getConstructor(CHAT_COMPONENT);
			this.nmsHandle = constructor.newInstance(chatComponent);
		}
		catch (Throwable ex)
		{
			throw new ReflectionException("Constructing wrapped chat packet", ex);
		}
	}
}