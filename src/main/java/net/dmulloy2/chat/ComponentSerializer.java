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