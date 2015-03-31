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

import java.util.Arrays;
import java.util.HashSet;

import com.google.common.base.Preconditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

/**
 * @author md_5
 */

public class BaseComponentSerializer
{
	protected void deserialize(JsonObject object, BaseComponent component, JsonDeserializationContext context)
	{
		if (object.has("color"))
		{
			component.setColor(object.get("color").getAsString().toLowerCase());
		}
		if (object.has("bold"))
		{
			component.setBold(object.get("bold").getAsBoolean());
		}
		if (object.has("italic"))
		{
			component.setItalic(object.get("italic").getAsBoolean());
		}
		if (object.has("underlined"))
		{
			component.setUnderlined(object.get("underlined").getAsBoolean());
		}
		if (object.has("strikethrough"))
		{
			component.setUnderlined(object.get("strikethrough").getAsBoolean());
		}
		if (object.has("obfuscated"))
		{
			component.setUnderlined(object.get("obfuscated").getAsBoolean());
		}
		if (object.has("extra"))
		{
			component
					.setExtra(Arrays.<BaseComponent> asList(context.<BaseComponent[]> deserialize(object.get("extra"), BaseComponent[].class)));
		}

		// Events
		if (object.has("clickEvent"))
		{
			JsonObject event = object.getAsJsonObject("clickEvent");
			component.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(event.get("action").getAsString().toUpperCase()), event.get(
					"value").getAsString()));
		}
		if (object.has("hoverEvent"))
		{
			JsonObject event = object.getAsJsonObject("hoverEvent");
			BaseComponent[] res;
			if (event.get("value").isJsonArray())
			{
				res = context.deserialize(event.get("value"), BaseComponent[].class);
			}
			else
			{
				res = new BaseComponent[]
				{
					context.<BaseComponent> deserialize(event.get("value"), BaseComponent.class)
				};
			}
			component.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(event.get("action").getAsString().toUpperCase()), res));
		}
	}

	protected void serialize(JsonObject object, BaseComponent component, JsonSerializationContext context)
	{
		boolean first = false;
		if (ComponentSerializer.serializedComponents.get() == null)
		{
			first = true;
			ComponentSerializer.serializedComponents.set(new HashSet<BaseComponent>());
		}
		try
		{
			Preconditions.checkArgument(! ComponentSerializer.serializedComponents.get().contains(component), "Component loop");
			ComponentSerializer.serializedComponents.get().add(component);
			if (component.getColorRaw() != null)
			{
				object.addProperty("color", component.getColorRaw().toLowerCase());
			}
			if (component.isBoldRaw() != null)
			{
				object.addProperty("bold", component.isBoldRaw());
			}
			if (component.isItalicRaw() != null)
			{
				object.addProperty("italic", component.isItalicRaw());
			}
			if (component.isUnderlinedRaw() != null)
			{
				object.addProperty("underlined", component.isUnderlinedRaw());
			}
			if (component.isStrikethroughRaw() != null)
			{
				object.addProperty("strikethrough", component.isStrikethroughRaw());
			}
			if (component.isObfuscatedRaw() != null)
			{
				object.addProperty("obfuscated", component.isObfuscatedRaw());
			}

			if (component.getExtra() != null)
			{
				object.add("extra", context.serialize(component.getExtra()));
			}

			// Events
			if (component.getClickEvent() != null)
			{
				JsonObject clickEvent = new JsonObject();
				clickEvent.addProperty("action", component.getClickEvent().getAction().toString().toLowerCase());
				clickEvent.addProperty("value", component.getClickEvent().getValue());
				object.add("clickEvent", clickEvent);
			}
			if (component.getHoverEvent() != null)
			{
				JsonObject hoverEvent = new JsonObject();
				hoverEvent.addProperty("action", component.getHoverEvent().getAction().toString().toLowerCase());
				hoverEvent.add("value", context.serialize(component.getHoverEvent().getValue()));
				object.add("hoverEvent", hoverEvent);
			}
		}
		finally
		{
			ComponentSerializer.serializedComponents.get().remove(component);
			if (first)
			{
				ComponentSerializer.serializedComponents.set(null);
			}
		}
	}
}
