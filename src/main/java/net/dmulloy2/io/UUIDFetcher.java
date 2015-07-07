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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import net.dmulloy2.types.Versioning;
import net.dmulloy2.types.Versioning.Version;

import org.apache.commons.lang.Validate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Fetches UUIDs for a list of names
 *
 * @author evilmidget38
 * @version 3
 */

public class UUIDFetcher implements Callable<Map<String, UUID>>
{
	private static boolean cachingEnabled = Versioning.getVersion() == Version.MC_16;
	private static final Cache<String, UUID> cache = CacheBuilder.newBuilder().weakKeys().weakValues().build();

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

		Iterator<List<String>> iter = namesList.iterator();
		while (iter.hasNext())
		{
			List<String> names = iter.next();
			Iterator<String> iter1 = names.iterator();
			while (iter1.hasNext())
			{
				String name = iter1.next();
				UUID uniqueId = cache.getIfPresent(name);
				if (uniqueId != null)
				{
					iter1.remove();
					uuidMap.put(name, uniqueId);
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
		try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream()))
		{
			writer.write(body.getBytes());
		}
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
			UUID uniqueId = cache.getIfPresent(name);
			if (uniqueId != null)
				return uniqueId;
		}

		UUIDFetcher fetcher = new UUIDFetcher(Arrays.asList(name));
		return fetcher.call().get(name);
	}

	public static UUID fromCache(String name)
	{
		return cache.getIfPresent(name);
	}

	public static void setCachingEnabled(boolean enabled)
	{
		if (cachingEnabled != enabled)
		{
			if (! enabled)
				cache.invalidateAll();
			cachingEnabled = enabled;
		}
	}
}
