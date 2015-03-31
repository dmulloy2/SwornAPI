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
import java.util.List;

import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * ComponentBuilder simplifies creating basic messages by allowing the use of a
 * chainable builder.
 *
 * @author md_5
 */

@Getter
public class ComponentBuilder
{
	private final List<BaseComponent> parts;

	/**
	 * Creates a ComponentBuilder with the given text as the first part.
	 *
	 * @param text the first text element
	 */
	public ComponentBuilder(String text)
	{
		parts = new ArrayList<>();
		parts.add(new TextComponent(text));
	}

	/**
	 * Appends the text to the builder and makes it the current target for
	 * formatting. The text will have none of the formatting from the previous
	 * part.
	 *
	 * @param text the text to append
	 * @return this ComponentBuilder for chaining
	 */
	public ComponentBuilder append(String text)
	{
		parts.add(new TextComponent(text));
		return this;
	}

	/**
	 * Sets the color of the current part.
	 *
	 * @param color the new color
	 * @return this ComponentBuilder for chaining
	 */
	public ComponentBuilder color(ChatColor color)
	{
		getCurrent().setColor(color.name().toLowerCase());
		return this;
	}

	/**
	 * Sets the color of the current part.
	 *
	 * @param color the new color
	 * @return this ComponentBuilder for chaining
	 */
	public ComponentBuilder color(String color)
	{
		getCurrent().setColor(color.toLowerCase());
		return this;
	}

	/**
	 * Sets whether the current part is bold.
	 *
	 * @param bold whether this part is bold
	 * @return this ComponentBuilder for chaining
	 */
	public ComponentBuilder bold(boolean bold)
	{
		getCurrent().setBold(bold);
		return this;
	}

	/**
	 * Sets whether the current part is italic.
	 *
	 * @param italic whether this part is italic
	 * @return this ComponentBuilder for chaining
	 */
	public ComponentBuilder italic(boolean italic)
	{
		getCurrent().setItalic(italic);
		return this;
	}

	/**
	 * Sets whether the current part is underlined.
	 *
	 * @param underlined whether this part is underlined
	 * @return this ComponentBuilder for chaining
	 */
	public ComponentBuilder underlined(boolean underlined)
	{
		getCurrent().setUnderlined(underlined);
		return this;
	}

	/**
	 * Sets whether the current part is strikethrough.
	 *
	 * @param strikethrough whether this part is strikethrough
	 * @return this ComponentBuilder for chaining
	 */
	public ComponentBuilder strikethrough(boolean strikethrough)
	{
		getCurrent().setStrikethrough(strikethrough);
		return this;
	}

	/**
	 * Sets whether the current part is obfuscated.
	 *
	 * @param obfuscated whether this part is obfuscated
	 * @return this ComponentBuilder for chaining
	 */
	public ComponentBuilder obfuscated(boolean obfuscated)
	{
		getCurrent().setObfuscated(obfuscated);
		return this;
	}

	/**
	 * Sets the click event for the current part.
	 *
	 * @param clickEvent the click event
	 * @return this ComponentBuilder for chaining
	 */
	public ComponentBuilder event(ClickEvent clickEvent)
	{
		getCurrent().setClickEvent(clickEvent);
		return this;
	}

	/**
	 * Sets the hover event for the current part.
	 *
	 * @param hoverEvent the hover event
	 * @return this ComponentBuilder for chaining
	 */
	public ComponentBuilder event(HoverEvent hoverEvent)
	{
		getCurrent().setHoverEvent(hoverEvent);
		return this;
	}

	/**
	 * Adds existing components to this builder.
	 *
	 * @param components components to add
	 * @return this ComponentBuilder for chaining
	 */
	public ComponentBuilder addAll(BaseComponent... components)
	{
		for (BaseComponent component : components)
			parts.add(component);
		return this;
	}

	/**
	 * Returns the current BaseComponent
	 *
	 * @return the current BaseComponent
	 */
	public BaseComponent getCurrent()
	{
		return parts.get(parts.size() - 1);
	}

	/**
	 * Returns the components needed to display the message created by this
	 * builder.
	 *
	 * @return the created components
	 */
	public BaseComponent[] create()
	{
		return parts.toArray(new BaseComponent[parts.size()]);
	}

	/**
	 * Sends the resulting JSON-chat message to a given player.
	 *
	 * @param sender {@link CommandSender} to send the message to
	 * @return the created components
	 */
	public BaseComponent[] send(CommandSender sender)
	{
		BaseComponent[] components = create();
		ChatUtil.sendMessage(sender, components);
		return components;
	}
}
