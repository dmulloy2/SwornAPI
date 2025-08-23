package net.dmulloy2.swornapi.io;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

import net.dmulloy2.swornapi.SwornPlugin;
import net.dmulloy2.swornapi.util.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public abstract class AbstractDataCache<T extends AbstractPlayerData> implements IPlayerDataCache<T>
{
	protected final ConcurrentMap<UUID, T> cache;

	protected final SwornPlugin plugin;

	public AbstractDataCache(SwornPlugin plugin)
	{
		this.cache = new ConcurrentHashMap<>(64, 0.75F, 64);
		this.plugin = plugin;
	}

	protected void onDataLoad(T data, OfflinePlayer player)
	{
		// no-op
	}

	@Override
	public T getData(OfflinePlayer player)
	{
		T data = cache.get(player.getUniqueId());
		if (data != null)
		{
			return data;
		}

		data = loadData(player);
		if (data == null && player.isOnline())
		{
			data = newData(player);
			data.setId(player.getUniqueId());
		}

		if (data != null)
		{
			onDataLoad(data, player);
			cache.put(player.getUniqueId(), data);
		}

		return data;
	}

	public abstract void saveData(UUID key, T value) throws IOException;

	@Override
	public void save()
	{
		long start = System.currentTimeMillis();
		plugin.getLogHandler().log("Saving player data...");

		for (Map.Entry<UUID, T> entry : cache.entrySet())
		{
			try
			{
				saveData(entry.getKey(), entry.getValue());
			}
			catch (Throwable ex)
			{
				plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "saving data for {0}", entry.getKey()));
			}
		}

		plugin.getLogHandler().log("Players saved. Took {0} ms.", System.currentTimeMillis() - start);
	}

	@Override
	public void purgeCache()
	{
		List<UUID> online = new ArrayList<>();
		for (Player player : Util.getOnlinePlayers())
			online.add(player.getUniqueId());

		cache.keySet().removeIf(key -> !online.contains(key));
		online.clear();
	}

	@Override
	public Map<UUID, T> getAllLoadedData()
	{
		return Collections.unmodifiableMap(cache);
	}
}
