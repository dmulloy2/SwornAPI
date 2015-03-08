/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.reflection;

import java.lang.reflect.Method;

import net.dmulloy2.exception.ReflectionException;

/**
 * @author dmulloy2
 */

// TODO: Keep up to date with MC versions. 1.8.3
public class WrappedChatSerializer extends AbstractWrapper
{
	private static final String CLASS_NAME = "ChatSerializer";
	private static final String[] ALIASES = { "IChatBaseComponent$ChatSerializer" };

	private final Method serialize;
	public WrappedChatSerializer() throws ReflectionException
	{
		try
		{
			this.nmsClass = ReflectionUtil.getMinecraftClass(CLASS_NAME, ALIASES);
			this.constructor = null;
			this.nmsHandle = null;

			this.serialize = ReflectionUtil.getMethod(nmsClass, "a", String.class);
		}
		catch (Throwable ex)
		{
			throw new ReflectionException("Constructing chat serializer", ex);
		}
	}

	public final Object serialize(String json) throws ReflectionException
	{
		return invokeMethod(serialize, json);
	}
}