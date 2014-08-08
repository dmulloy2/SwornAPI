package net.dmulloy2.io;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.apache.commons.lang.Validate;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.common.collect.ImmutableList;

/**
 * Fetches names for a list of UUIDs
 *
 * @author evilmidget38
 * @version 3
 */

public class NameFetcher implements Callable<Map<UUID, String>>
{
	private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
	private static final JSONParser jsonParser = new JSONParser();

	private final List<UUID> uuids;
	public NameFetcher(List<UUID> uuids)
	{
		Validate.notNull(uuids, "uuids cannot be null!");
		this.uuids = ImmutableList.copyOf(uuids);
	}

	@Override
	public Map<UUID, String> call() throws Exception
	{
		Map<UUID, String> uuidStringMap = new HashMap<>();
		for (UUID uuid : uuids)
		{
			HttpURLConnection connection = (HttpURLConnection) new URL(PROFILE_URL + uuid.toString().replace("-", "")).openConnection();
			JSONObject response = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
			String name = (String) response.get("name");
			if (name == null)
				continue;

			String cause = (String) response.get("cause");
			String errorMessage = (String) response.get("errorMessage");
			if (cause != null && cause.length() > 0)
				throw new Exception(cause + ": " + errorMessage);

			uuidStringMap.put(uuid, name);
		}

		return uuidStringMap;
	}
}