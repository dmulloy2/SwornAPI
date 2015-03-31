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
 * @author dmulloy2
 */

public class FileSerialization
{
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

	public static <T extends ConfigurationSerializable> void save(T instance, File file) throws IOException
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
