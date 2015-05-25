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
import java.util.Map;
import java.util.Map.Entry;

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
	 * @return The deserialized object, or null if the file does not exist
	 * @throws IllegalArgumentException If the file or class is null
	 * @throws IOException If the file cannot be read
	 * @throws InvalidConfigurationException If the given file is not a valid configuration
	 * @see #save(ConfigurationSerializable, File)
	 */
	@SuppressWarnings("unchecked")
	public static <T extends ConfigurationSerializable> T load(File file, Class<T> clazz) throws IOException, InvalidConfigurationException
	{
		Validate.notNull(file, "file cannot be null!");
		Validate.notNull(clazz, "clazz cannot be null!");

		if (! file.exists())
			return null;

		YamlConfiguration config = new YamlConfiguration();
		config.load(file);

		Map<String, Object> map = config.getValues(true);
		return (T) ConfigurationSerialization.deserializeObject(map, clazz);
	}

	/**
	 * Saves a serializable object to a given file.
	 * 
	 * @param instance Object to seriaize
	 * @param file File to save to
	 * @throws IllegalArgumentException If the instance or file is null
	 * @throws IOException If the file cannot be written to
	 * @see #load(File, Class)
	 */
	public static void save(ConfigurationSerializable instance, File file) throws IOException
	{
		Validate.notNull(instance, "instance cannot be null!");
		Validate.notNull(file, "file cannot be null!");

		if (file.exists())
			file.delete();

		file.createNewFile();

		YamlConfiguration config = new YamlConfiguration();

		for (Entry<String, Object> entry : instance.serialize().entrySet())
		{
			config.set(entry.getKey(), entry.getValue());
		}

		config.save(file);
	}
}