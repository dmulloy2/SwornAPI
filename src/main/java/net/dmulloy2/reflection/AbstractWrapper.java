/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import lombok.Data;
import net.dmulloy2.exception.ReflectionException;

/**
 * @author dmulloy2
 */

@Data
public abstract class AbstractWrapper
{
	protected Object nmsHandle;
	protected Class<?> nmsClass;
	protected Constructor<?> constructor;

	protected AbstractWrapper() { }

	protected final Object invokeMethod(Method method, Object... args) throws ReflectionException
	{
		try
		{
			return method.invoke(nmsHandle, args);
		}
		catch (Throwable ex)
		{
			throw new ReflectionException("invokeMethod(" + method + ", " + args + ")", ex);
		}
	}

	protected final void setField(Field field, Object value) throws ReflectionException
	{
		try
		{
			field.setAccessible(true);
			field.set(nmsHandle, value);
		}
		catch (Throwable ex)
		{
			throw new ReflectionException("setField(" + field + ", " + value + ")", ex);
		}
	}
}