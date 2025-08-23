package net.dmulloy2.swornapi.io;

import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

public interface IPlayerDataCache<T extends AbstractPlayerData>
{
	T getData(OfflinePlayer player);

	T loadData(OfflinePlayer player);

	T newData(OfflinePlayer player);

	void save();

	void purgeCache();

	Map<UUID, T> getAllData();

	Map<UUID, T> getAllLoadedData();
}
