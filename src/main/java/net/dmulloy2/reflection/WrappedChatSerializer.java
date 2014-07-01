/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.reflection;

import java.lang.reflect.Method;

import net.dmulloy2.exception.ReflectionException;
import net.dmulloy2.util.ReflectionUtil;

/**
 * @author dmulloy2
 */

public class WrappedChatSerializer extends AbstractWrapper
{
	private static final String NMS_CLASS_NAME = "ChatSerializer";
	
	private final Method a;
	public WrappedChatSerializer() throws ReflectionException
	{
		try
		{
			this.nmsClass = ReflectionUtil.getNMSClass(NMS_CLASS_NAME);
			this.constructor = null;
			this.nmsHandle = null;

			this.a = ReflectionUtil.getMethod(nmsClass, "a", String.class);
		}
		catch (Throwable ex)
		{
			throw new ReflectionException("Constructing chat serializer", ex);
		}
	}

	public Object a(String jsonString) throws ReflectionException
	{
		return invokeMethod(a, jsonString);
	}
}