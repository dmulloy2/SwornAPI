/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.io;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.util.Util;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * @author dmulloy2
 */

public class FileSerialization
{
	public static <T extends ConfigurationSerializable> void save(T instance, File file)
	{
		try
		{
			Validate.notNull(instance, "instance cannot be null!");
			Validate.notNull(file, "file cannot be null!");

			if (file.exists())
				file.delete();

			file.createNewFile();

			FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
			for (Entry<String, Object> entry : instance.serialize().entrySet())
			{
				fc.set(entry.getKey(), entry.getValue());
			}

			fc.save(file);
		}
		catch (Throwable ex)
		{
			Bukkit.getLogger().severe("[SwornAPI] " + Util.getUsefulStack(ex, "saving file " + file.getName()));
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends ConfigurationSerializable> T load(File file, Class<T> clazz)
	{
		try
		{
			Validate.notNull(file, "file cannot be null!");
			Validate.notNull(clazz, "clazz cannot be null!");

			if (! file.exists())
				return null;

			FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
			Map<String, Object> map = fc.getValues(true);

			return (T) ConfigurationSerialization.deserializeObject(map, clazz);
		}
		catch (Throwable ex)
		{
			Bukkit.getLogger().severe("[SwornAPI] " + Util.getUsefulStack(ex, "loading file " + file.getName()));
			return null;
		}
	}
}