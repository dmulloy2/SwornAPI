/**
 * SwornAPI - common API for MineSworn and Shadowvolt plugins
 * Copyright (C) 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

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
			method.setAccessible(true);
			return method.invoke(nmsHandle, args);
		}
		catch (Throwable ex)
		{
			throw new ReflectionException(String.format("invokeMethod(%s, %s)", method, Arrays.toString(args)), ex);
		}
	}

	protected final Object getField(String name) throws ReflectionException
	{
		try
		{
			Field field = Reflection.getField(nmsClass, name);
			field.setAccessible(true);
			return field.get(nmsHandle);
		}
		catch (Throwable ex)
		{
			throw new ReflectionException(String.format("getField(%s)", name), ex);
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
			throw new ReflectionException(String.format("setField(%s, %s)", field, value));
		}
	}
}
