package net.dmulloy2.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;

import net.dmulloy2.types.Versioning;
import net.dmulloy2.types.Versioning.Version;

import org.apache.commons.lang.Validate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Fetches UUIDs for a list of names
 *
 * @author evilmidget38
 * @version 3
 */

public class UUIDFetcher implements Callable<Map<String, UUID>>
{
	private static boolean cachingEnabled = Versioning.getVersion() == Version.MC_16;
	private static final Map<String, UUID> cache = new WeakHashMap<>();

	private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
	private static final JSONParser jsonParser = new JSONParser();

	private final List<List<String>> namesList;
	public UUIDFetcher(List<String> names)
	{
		Validate.notNull(names, "names cannot be null!");
		this.namesList = new ArrayList<List<String>>();

		int namesCopied = 0;
		while (namesCopied < names.size())
		{
			namesList.add(names.subList(namesCopied, Math.min(namesCopied + 100, names.size())));
			namesCopied += 100;
		}
	}

	@Override
	public Map<String, UUID> call() throws IOException, ParseException
	{
		Map<String, UUID> uuidMap = new HashMap<>();
		for (List<String> names : new ArrayList<>(namesList))
		{
			if (cachingEnabled)
			{
				for (String name : new ArrayList<>(names))
				{
					if (cache.containsKey(name))
					{
						names.remove(name);
						uuidMap.put(name, cache.get(name));
					}
				}
			}

			String body = buildBody(names);
			HttpURLConnection connection = createConnection();
			writeBody(connection, body);
			JSONArray jsonArr = (JSONArray) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
			for (Object profile : jsonArr)
			{
				JSONObject jsonProfile = (JSONObject) profile;
				String id = (String) jsonProfile.get("id");
				String name = (String) jsonProfile.get("name");
				UUID uuid = UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-"
						+ id.substring(16, 20) + "-" + id.substring(20, 32));
				uuidMap.put(name, uuid);
			}
		}

		if (cachingEnabled)
			cache.putAll(uuidMap);
		return uuidMap;
	}

	private static void writeBody(HttpURLConnection connection, String body) throws IOException
	{
		DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
		writer.write(body.getBytes());
		writer.flush();
		writer.close();
	}

	private static HttpURLConnection createConnection() throws IOException
	{
		URL url = new URL(PROFILE_URL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		return connection;
	}

	private static String buildBody(List<String> names)
	{
		return JSONValue.toJSONString(names);
	}

	public static UUID getUUID(String name) throws IOException, ParseException
	{
		Validate.notNull(name, "name cannot be null!");
		if (name.length() == 36)
			return UUID.fromString(name);

		if (cachingEnabled)
		{
			if (cache.containsKey(name))
				return cache.get(name);
		}

		UUIDFetcher fetcher = new UUIDFetcher(Arrays.asList(name));
		return fetcher.call().get(name);
	}

	public static UUID fromCache(String name)
	{
		return cache.get(name);
	}

	public static void setCachingEnabled(boolean enabled)
	{
		if (cachingEnabled != enabled)
		{
			if (! enabled)
				cache.clear();
			cachingEnabled = enabled;
		}
	}
}