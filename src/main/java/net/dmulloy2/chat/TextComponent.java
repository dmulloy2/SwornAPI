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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.bukkit.ChatColor;

/**
 * Represents a text component.
 *
 * @author md_5
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TextComponent extends BaseComponent
{
	/**
	 * The text of the component that will be displayed to the client
	 */
	private String text;

	/**
	 * Creates a TextComponent with formatting and text from the passed
	 * component
	 *
	 * @param textComponent the component to copy from
	 */
	public TextComponent(TextComponent textComponent)
	{
		super(textComponent);
		setText(textComponent.getText());
	}

	/**
	 * Creates a TextComponent with blank text and the extras set to the passed
	 * array
	 *
	 * @param extras the extras to set
	 */
	public TextComponent(BaseComponent... extras)
	{
		setText("");
		setExtra(Arrays.asList(extras));
	}

	@Override
	protected void toPlainText(StringBuilder builder)
	{
		builder.append(text);
		super.toPlainText(builder);
	}

	@Override
	protected void toLegacyText(StringBuilder builder)
	{
		String color = getColor();
		if (color != null)
			builder.append(color);

		if (isBold())
			builder.append(ChatColor.BOLD);

		if (isItalic())
			builder.append(ChatColor.ITALIC);

		if (isUnderlined())
			builder.append(ChatColor.UNDERLINE);

		if (isStrikethrough())
			builder.append(ChatColor.STRIKETHROUGH);

		if (isObfuscated())
			builder.append(ChatColor.MAGIC);

		builder.append(text);
		super.toLegacyText(builder);
	}

	private static final Pattern url = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");

	/**
	 * Converts the old formatting system into the new json based system.
	 *
	 * @param message the text to convert
	 * @return the components needed to print the message to the client
	 */
	public static BaseComponent[] fromLegacyText(String message)
	{
		ArrayList<BaseComponent> components = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		TextComponent component = new TextComponent();
		Matcher matcher = url.matcher(message);

		for (int i = 0; i < message.length(); i++)
		{
			char c = message.charAt(i);
//			if (c == ChatColor.COLOR_CHAR)
//			{
//				i++;
//				c = message.charAt(i);
//				if (c >= 'A' && c <= 'Z')
//				{
//					c += 32;
//				}
//				ChatColor format = ChatColor.getByChar(c);
//				if (format == null)
//				{
//					continue;
//				}
//				if (builder.length() > 0)
//				{
//					TextComponent old = component;
//					component = new TextComponent(old);
//					old.setText(builder.toString());
//					builder = new StringBuilder();
//					components.add(old);
//				}
//				switch (format)
//				{
//					case BOLD:
//						component.setBold(true);
//						break;
//					case ITALIC:
//						component.setItalic(true);
//						break;
//					case UNDERLINE:
//						component.setUnderlined(true);
//						break;
//					case STRIKETHROUGH:
//						component.setStrikethrough(true);
//						break;
//					case MAGIC:
//						component.setObfuscated(true);
//						break;
//					case RESET:
//						format = ChatColor.WHITE;
//					default:
//						component = new TextComponent();
//						component.setColor(format);
//						break;
//				}
//				continue;
//			}
			int pos = message.indexOf(' ', i);
			if (pos == - 1)
			{
				pos = message.length();
			}
			if (matcher.region(i, pos).find()) // Web link handling
			{
				if (builder.length() > 0)
				{
					TextComponent old = component;
					component = new TextComponent(old);
					old.setText(builder.toString());
					builder = new StringBuilder();
					components.add(old);
				}

				TextComponent old = component;
				component = new TextComponent(old);
				String urlString = message.substring(i, pos);
				component.setText(urlString);
				component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, urlString.startsWith("http") ? urlString : "http://"
						+ urlString));
				components.add(component);
				i += pos - i - 1;
				component = old;
				continue;
			}

			builder.append(c);
		}

		if (builder.length() > 0)
		{
			component.setText(builder.toString());
			components.add(component);
		}

		// The client will crash if the array is empty
		if (components.size() == 0)
		{
			components.add(new TextComponent(""));
		}

		return components.toArray(new BaseComponent[components.size()]);
	}

	public static List<BaseComponent[]> fromLegacyList(List<String> list)
	{
		List<BaseComponent[]> ret = new ArrayList<>();

		for (String legacy : list)
			ret.add(fromLegacyText(legacy));

		return ret;
	}

	@Override
	public String toString()
	{
		return String.format("TextComponent{text=%s, %s}", text, super.toString());
	}
}
