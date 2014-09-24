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
			throw ReflectionException.fromThrowable("invokeMethod(" + method + ", " + args + ")", ex);
		}
	}

	protected final Object getField(String name) throws ReflectionException
	{
		try
		{
			Field field = ReflectionUtil.getField(nmsClass, name);
			field.setAccessible(true);
			return field.get(nmsHandle);
		}
		catch (Throwable ex)
		{
			throw ReflectionException.fromThrowable("getField(" + name + ")", ex);
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
			throw ReflectionException.fromThrowable("setField(" + field + ", " + value + ")", ex);
		}
	}
}