package net.dmulloy2.swornapi.io;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import net.dmulloy2.swornapi.SwornPlugin;
import net.dmulloy2.swornapi.util.Util;

public abstract class FileDataCache<T extends AbstractPlayerData> extends AbstractDataCache<T>
{
	private String extension;

	private File folder;

	public FileDataCache(SwornPlugin plugin, String folderName, String extension)
	{
		super(plugin);
		this.extension = extension;
		this.folder = new File(plugin.getDataFolder(), folderName);
	}

	protected abstract T dataCtor();

	@Override
	public void saveData(UUID key, T value) throws IOException
	{
		if (!folder.exists())
			folder.mkdirs();

		File file = new File(folder, getFileName(key));
		FileSerialization.save(value, file);
	}

	private T loadData(File file)
	{
		try
		{
			YamlConfiguration config = new YamlConfiguration();
			config.load(file);
			Map<String, Object> values = config.getValues(true);

			T data = dataCtor();
			FileSerialization.deserialize(data, values);

			return data;
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading data from {0}", file.getName()));
			return null;
		}
	}

	@Override
	public T loadData(OfflinePlayer player)
	{
		File file = new File(folder, getFileName(player.getUniqueId()));
		if (!file.exists())
		{
			return null;
		}

		T data = loadData(file);
		if (data == null)
		{
			// Corrupt data :(
			if (! file.renameTo(new File(folder, file.getName() + "_bad")))
				file.delete();
		}

		return data;
	}

	@Override
	public Map<UUID, T> getAllData()
	{
		Map<UUID, T> data = new HashMap<>(cache);

		if (!folder.exists())
		{
			return data;
		}

		File[] files = folder.listFiles(file -> file.getName().endsWith(extension));
		if (files == null || files.length == 0)
		{
			return data;
		}

		for (File file : files)
		{
			String fileName = IOUtil.trimFileExtension(file, extension);
			UUID uuid = UUID.fromString(fileName);
			if (cache.containsKey(uuid))
			{
				continue;
			}

			T loaded = loadData(file);
			if (loaded != null)
			{
				data.put(uuid, loaded);
			}
		}

		return data;
	}

	private String getFileName(UUID key)
	{
		return key.toString() + extension;
	}
}
