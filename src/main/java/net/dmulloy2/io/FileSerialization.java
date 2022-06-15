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
package net.dmulloy2.io;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.util.Util;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * Utility methods for serializing and deserializing Objects with YAML.
 * @author dmulloy2
 */

public class FileSerialization
{
	/**
	 * Loads a previously serialized object from a given file using YAML.
	 * 
	 * @param file File to load from
	 * @param clazz Class the object should be of
	 * @param exists Whether or not the file exists and the expensive
	 *        {@link File#exists()} operation can be skipped
	 * @return The deserialized object, or null if the file does not exist
	 * @throws IllegalArgumentException If the file or class is null
	 * @throws IOException If the file cannot be read
	 * @throws InvalidConfigurationException If the given file is not a valid
	 *         YAML configuration
	 * @see #save(ConfigurationSerializable, File)
	 */
	@SuppressWarnings("unchecked")
	public static <T extends ConfigurationSerializable> T load(File file, Class<T> clazz, boolean exists) throws IOException, InvalidConfigurationException
	{
		Validate.notNull(file, "file cannot be null!");
		Validate.notNull(clazz, "clazz cannot be null!");

		if (! exists && ! file.exists())
			return null;

		YamlConfiguration config = new YamlConfiguration();
		config.load(file);

		Map<String, Object> map = config.getValues(true);
		return (T) ConfigurationSerialization.deserializeObject(map, clazz);
	}

	/**
	 * Loads a previously serialized object from a given file using YAML. Alias
	 * for {@link #load(File, Class, boolean)}, exists defaults to false.
	 * 
	 * @see #save(ConfigurationSerializable, File)
	 */
	public static <T extends ConfigurationSerializable> T load(File file, Class<T> clazz) throws IOException, InvalidConfigurationException
	{
		return load(file, clazz, false);
	}

	/**
	 * Saves a serializable object to a given file.
	 * 
	 * @param instance Object to seriaize
	 * @param file File to save to
	 * @throws IllegalArgumentException If the instance or file is null
	 * @throws IOException If the file cannot be written to
	 * @see #load(File, Class, boolean)
	 */
	public static void save(ConfigurationSerializable instance, File file) throws IOException
	{
		Validate.notNull(instance, "instance cannot be null!");
		Validate.notNull(file, "file cannot be null!");

		file.delete();
		file.createNewFile();

		YamlConfiguration config = new YamlConfiguration();

		for (Entry<String, Object> entry : instance.serialize().entrySet())
		{
			config.set(entry.getKey(), entry.getValue());
		}

		config.save(file);
	}

	private static final ConcurrentMap<String, Optional<MethodHandle>> SERIALIZE_CACHE = new ConcurrentHashMap<>();

	/**
	 * Serializes all of an object's fields into a Map. This method ignores
	 * transient, null, zero, and empty fields.
	 * 
	 * @param object Object to serialize
	 * @return The map
	 */
	public static Map<String, Object> serialize(Object object)
	{
		Map<String, Object> data = new LinkedHashMap<>();

		for (Field field : object.getClass().getDeclaredFields())
		{
			try
			{
				if (Modifier.isTransient(field.getModifiers()))
					continue;

				field.setAccessible(true);

				if (field.getType().equals(Integer.TYPE))
				{
					if (field.getInt(object) != 0)
						data.put(field.getName(), field.getInt(object));
				}
				else if (field.getType().equals(Long.TYPE))
				{
					if (field.getLong(object) != 0)
						data.put(field.getName(), field.getLong(object));
				}
				else if (field.getType().equals(Boolean.TYPE))
				{
					if (field.getBoolean(object))
						data.put(field.getName(), field.getBoolean(object));
				}
				else if (field.getType().isAssignableFrom(Collection.class))
				{
					if (! ((Collection<?>) field.get(object)).isEmpty())
						data.put(field.getName(), field.get(object));
				}
				else if (field.getType().isAssignableFrom(String.class))
				{
					if (field.get(object) != null)
						data.put(field.getName(), field.get(object));
				}
				else if (field.getType().isAssignableFrom(Map.class))
				{
					if (! ((Map<?, ?>) field.get(object)).isEmpty())
						data.put(field.getName(), field.get(object));
				}
				else if (field.getType().isEnum())
				{
					data.put(field.getName(), ((Enum<?>) field.get(object)).name().toLowerCase());
				}
				else if (field.get(object) != null)
				{
					String className = field.getType().getName();
					if (!SERIALIZE_CACHE.containsKey(className))
					{
						try
						{
							MethodType methodType = MethodType.methodType(Object.class);
							MethodHandle serialHandle = MethodHandles.publicLookup().findVirtual(field.getType(), "serialize", methodType);
							SERIALIZE_CACHE.put(className, Optional.of(serialHandle));
						}
						catch (NoSuchMethodException ex)
						{
							SERIALIZE_CACHE.put(className, Optional.empty());
						}
					}

					Optional<MethodHandle> serialHandle = SERIALIZE_CACHE.get(className);
					if (serialHandle.isPresent())
					{
						data.put(field.getName(), serialHandle.get().invoke(field.get(object)));
					}
					else
					{
						data.put(field.getName(), field.get(object));
					}
				}
			}
			catch (Throwable ex)
			{
				if (LogHandler.isGlobalDebugEnabled())
					LogHandler.globalDebug(Util.getUsefulStack(ex, "serializing field {0} of {1}",
						field.getName(), object));
			}
		}

		return data;
	}

	private static final ConcurrentMap<String, Optional<MethodHandle>> DESERIALIZE_CACHE = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public static void deserialize(Object object, Map<String, Object> data)
	{
		for (Entry<String, Object> entry : data.entrySet())
		{
			String fieldName = entry.getKey();
			Object fieldData = entry.getValue();

			try
			{
				Field field = object.getClass().getDeclaredField(fieldName);
				if (Modifier.isTransient(field.getModifiers()))
					continue;

				field.setAccessible(true);

				Class<?> fieldType = field.getType();
				if (fieldType.isPrimitive())
				{
					field.set(object, fieldData);
				}
				else if (fieldType.isEnum())
				{
					Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) fieldType;
					String name = fieldData.toString();
					Object enumValue = Arrays.stream(enumType.getEnumConstants())
						.filter(e -> e.name().equalsIgnoreCase(name)).findFirst().orElseThrow();
					field.set(object, enumValue);
				}
				else
				{
					String className = fieldType.getName();
					if (!DESERIALIZE_CACHE.containsKey(className))
					{
						try
						{
							MethodType methodType = MethodType.methodType(fieldType, Object.class);
							MethodHandle serialHandle = MethodHandles.publicLookup().findStatic(field.getType(), "deserialize", methodType);
							DESERIALIZE_CACHE.put(className, Optional.of(serialHandle));
						}
						catch (NoSuchMethodException ex)
						{
							DESERIALIZE_CACHE.put(className, Optional.empty());
						}
					}

					Optional<MethodHandle> deserialize = DESERIALIZE_CACHE.get(className);
					if (deserialize.isPresent())
					{
						field.set(object, deserialize.get().invoke(fieldData));
					}
					else
					{
						field.set(object, fieldData);
					}
				}
			}
			catch (NoSuchFieldException ignored) { }
			catch (Throwable ex)
			{
				if (LogHandler.isGlobalDebugEnabled())
					LogHandler.globalDebug(Util.getUsefulStack(ex, "deserializing field {0} of {1}",
						fieldName, object));
			}
		}
	}
}