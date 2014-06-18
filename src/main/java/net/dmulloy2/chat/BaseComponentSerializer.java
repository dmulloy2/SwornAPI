/**
 * Copyright (c) 2012, md_5. All rights reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.dmulloy2.chat;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;

import com.google.common.base.Preconditions;

/**
 * @author md_5
 */

public class BaseComponentSerializer
{
	protected void deserialize(JsonObject object, BaseComponent component, JsonDeserializationContext context)
	{
		if (object.has("color"))
		{
			component.setColor(ChatColor.valueOf(object.get("color").getAsString().toUpperCase()));
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
				object.addProperty("color", component.getColorRaw().name());
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