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
package net.dmulloy2.chat;

import java.lang.reflect.Type;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * @author md_5
 */

public class ComponentSerializer implements JsonDeserializer<BaseComponent>
{
	private final static Gson gson = new GsonBuilder()
		.registerTypeAdapter(BaseComponent.class, new ComponentSerializer())
		.registerTypeAdapter(TextComponent.class, new TextComponentSerializer()).create();

	public final static ThreadLocal<HashSet<BaseComponent>> serializedComponents = new ThreadLocal<>();

	public static BaseComponent[] parse(String json)
	{
		if (json.startsWith("[")) // Array
		{
			return gson.fromJson(json, BaseComponent[].class);
		}
		return new BaseComponent[]
		{
			gson.fromJson(json, BaseComponent.class)
		};
	}

	public static String toString(BaseComponent component)
	{
		return gson.toJson(component);
	}

	public static String toString(BaseComponent... components)
	{
		return gson.toJson(new TextComponent(components));
	}

	@Override
	public BaseComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		if (json.isJsonPrimitive())
		{
			return new TextComponent(json.getAsString());
		}

		return context.deserialize(json, TextComponent.class);
	}
}
