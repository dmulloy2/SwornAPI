package net.dmulloy2.swornapi.io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.FindOptions;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.dizitart.no2.repository.Cursor;
import org.dizitart.no2.repository.ObjectRepository;

import net.dmulloy2.swornapi.SwornPlugin;

public abstract class NitriteDataCache<T extends AbstractPlayerData> extends AbstractDataCache<T> implements AutoCloseable
{
	protected final ObjectRepository<T> repository;
	private final Nitrite db;

	public NitriteDataCache(SwornPlugin plugin, File dbFile, Class<T> dataClass)
	{
		super(plugin);

		MVStoreModule storeModule = MVStoreModule.withConfig()
			.filePath(dbFile.getAbsolutePath())
			.compress(true)
			.build();

		this.db = Nitrite.builder()
			.loadModule(storeModule)
			.openOrCreate();

		this.repository = db.getRepository(dataClass);
	}

	@Override
	public void close()
	{
		if (repository != null)
		{
			repository.close();
		}

		if (db != null)
		{
			db.close();
		}
	}

	@Override
	public void saveData(UUID key, T value) throws IOException
	{
		repository.update(value, true);
	}

	@Override
	public T loadData(OfflinePlayer player)
	{
		return repository.getById(player.getUniqueId());
	}

	@Override
	public Map<UUID, T> getAllData()
	{
		Map<UUID, T> data = new HashMap<>(cache);

		Cursor<T> cursor = repository.find();

		for (T document : cursor)
		{
			if (!data.containsKey(document.getId()))
			{
				data.put(document.getId(), document);
			}
		}

		return data;
	}

	public Iterable<T> query(FindOptions options)
	{
		return repository.find(options);
	}
}
