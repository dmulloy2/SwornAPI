package net.dmulloy2.io;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import com.google.common.collect.ImmutableList;

/**
 * Fetches UUIDs for a list of names
 *
 * @author evilmidget38
 * @version 3
 */

public class UUIDFetcher implements Callable<Map<String, UUID>>
{
	private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
	private final JSONParser jsonParser = new JSONParser();
	private final List<List<String>> namesList;

	public UUIDFetcher(List<String> names)
	{
		ImmutableList.Builder<List<String>> builder = ImmutableList.builder();
		int namesCopied = 0;
		while (namesCopied < names.size())
		{
			builder.add(ImmutableList.copyOf(names.subList(namesCopied, Math.min(namesCopied + 100, names.size()))));
			namesCopied += 100;
		}

		this.namesList = builder.build();
	}

	@Override
	public Map<String, UUID> call() throws Exception
	{
		Map<String, UUID> uuidMap = new HashMap<String, UUID>();
		for (List<String> names : namesList)
		{
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

		return uuidMap;
	}

	private static void writeBody(HttpURLConnection connection, String body) throws Exception
	{
		DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
		writer.write(body.getBytes());
		writer.flush();
		writer.close();
	}

	private static HttpURLConnection createConnection() throws Exception
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
}