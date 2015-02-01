/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.reflection;

import net.dmulloy2.exception.ReflectionException;

/**
 * @author dmulloy2
 */

// TODO: Keep up to date with MC versions. 1.8
public class WrappedChatPacket extends WrappedPacket
{
	private static final String NMS_CLASS_NAME = "PacketPlayOutChat";
	private static final Class<?> chatComponentClass = ReflectionUtil.getNMSClass("IChatBaseComponent");

	public WrappedChatPacket(Object chatComponent) throws ReflectionException
	{
		try
		{
			this.nmsClass = ReflectionUtil.getNMSClass(NMS_CLASS_NAME);
			this.constructor = nmsClass.getConstructor(chatComponentClass);
			this.nmsHandle = constructor.newInstance(chatComponent);
		}
		catch (Throwable ex)
		{
			throw new ReflectionException("Constructing wrapped chat packet", ex);
		}
	}
}