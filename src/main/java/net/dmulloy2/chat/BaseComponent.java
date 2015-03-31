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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.bukkit.ChatColor;

/**
 * Represents a base chat component.
 * 
 * @author md_5
 */

@Setter
@NoArgsConstructor
public abstract class BaseComponent
{
	@Setter(AccessLevel.NONE)
	BaseComponent parent;

	/**
	 * The color of this component and any child components (unless overridden)
	 */
	private String color;

	/**
	 * Whether this component and any child components (unless overridden) is
	 * bold
	 */
	private Boolean bold;

	/**
	 * Whether this component and any child components (unless overridden) is
	 * italic
	 */
	private Boolean italic;

	/**
	 * Whether this component and any child components (unless overridden) is
	 * underlined
	 */
	private Boolean underlined;

	/**
	 * Whether this component and any child components (unless overridden) is
	 * strikethrough
	 */
	private Boolean strikethrough;

	/**
	 * Whether this component and any child components (unless overridden) is
	 * obfuscated
	 */
	private Boolean obfuscated;

	/**
	 * Appended components that inherit this component's formatting and events
	 */
	@Getter
	private List<BaseComponent> extra;

	/**
	 * The action to preform when this component (and child components) are
	 * clicked
	 */
	@Getter
	private ClickEvent clickEvent;

	/**
	 * The action to preform when this component (and child components) are
	 * hovered over
	 */
	@Getter
	private HoverEvent hoverEvent;

	protected BaseComponent(BaseComponent old)
	{
		setColor(old.getColorRaw());
		setBold(old.isBoldRaw());
		setItalic(old.isItalicRaw());
		setUnderlined(old.isUnderlinedRaw());
		setStrikethrough(old.isStrikethroughRaw());
		setObfuscated(old.isObfuscatedRaw());
		setClickEvent(old.getClickEvent());
		setHoverEvent(old.getHoverEvent());
	}

	/**
	 * Converts the components to a string that uses the old formatting codes.
	 *
	 * @param components the components to convert
	 * @return the string in the old format
	 * @see ChatColor
	 */
	public static String toLegacyText(BaseComponent... components)
	{
		StringBuilder builder = new StringBuilder();
		for (BaseComponent msg : components)
		{
			builder.append(msg.toLegacyText());
		}

		return builder.toString();
	}

	/**
	 * Converts the components into a string without any formatting
	 *
	 * @param components the components to convert
	 * @return the string as plain text
	 */
	public static String toPlainText(BaseComponent... components)
	{
		StringBuilder builder = new StringBuilder();
		for (BaseComponent msg : components)
		{
			builder.append(msg.toPlainText());
		}

		return builder.toString();
	}

	/**
	 * Returns the color of this component. This uses the parent's color if this
	 * component doesn't have one. Null is returned if no color is found.
	 *
	 * @return the color of this component
	 */
	public String getColor()
	{
		if (color == null)
		{
			if (parent == null)
				return null;

			return parent.getColor();
		}

		return color;
	}

	/**
	 * Returns the color of this component without checking the parents color.
	 * May return null
	 *
	 * @return the color of this component
	 */
	public String getColorRaw()
	{
		return color;
	}

	/**
	 * Returns whether this component is bold. This uses the parent's setting if
	 * this component hasn't been set. false is returned if none of the parent
	 * chain has been set.
	 *
	 * @return whether the component is bold
	 */
	public boolean isBold()
	{
		if (bold == null)
			return parent != null && parent.isBold();

		return bold;
	}

	/**
	 * Returns whether this component is bold without checking the parents
	 * setting. May return null
	 *
	 * @return whether the component is bold
	 */
	public Boolean isBoldRaw()
	{
		return bold;
	}

	/**
	 * Returns whether this component is italic. This uses the parent's setting
	 * if this component hasn't been set. false is returned if none of the
	 * parent chain has been set.
	 *
	 * @return whether the component is italic
	 */
	public boolean isItalic()
	{
		if (italic == null)
			return parent != null && parent.isItalic();

		return italic;
	}

	/**
	 * Returns whether this component is italic without checking the parents
	 * setting. May return null
	 *
	 * @return whether the component is italic
	 */
	public Boolean isItalicRaw()
	{
		return italic;
	}

	/**
	 * Returns whether this component is underlined. This uses the parent's
	 * setting if this component hasn't been set. false is returned if none of
	 * the parent chain has been set.
	 *
	 * @return whether the component is underlined
	 */
	public boolean isUnderlined()
	{
		if (underlined == null)
			return parent != null && parent.isUnderlined();

		return underlined;
	}

	/**
	 * Returns whether this component is underlined without checking the parents
	 * setting. May return null
	 *
	 * @return whether the component is underlined
	 */
	public Boolean isUnderlinedRaw()
	{
		return underlined;
	}

	/**
	 * Returns whether this component is strikethrough. This uses the parent's
	 * setting if this component hasn't been set. false is returned if none of
	 * the parent chain has been set.
	 *
	 * @return whether the component is strikethrough
	 */
	public boolean isStrikethrough()
	{
		if (strikethrough == null)
			return parent != null && parent.isStrikethrough();

		return strikethrough;
	}

	/**
	 * Returns whether this component is strikethrough without checking the
	 * parents setting. May return null
	 *
	 * @return whether the component is strikethrough
	 */
	public Boolean isStrikethroughRaw()
	{
		return strikethrough;
	}

	/**
	 * Returns whether this component is obfuscated. This uses the parent's
	 * setting if this component hasn't been set. false is returned if none of
	 * the parent chain has been set.
	 *
	 * @return whether the component is obfuscated
	 */
	public boolean isObfuscated()
	{
		if (obfuscated == null)
			return parent != null && parent.isObfuscated();

		return obfuscated;
	}

	/**
	 * Returns whether this component is obfuscated without checking the parents
	 * setting. May return null
	 *
	 * @return whether the component is obfuscated
	 */
	public Boolean isObfuscatedRaw()
	{
		return obfuscated;
	}

	public void setExtra(List<BaseComponent> components)
	{
		for (BaseComponent component : components)
		{
			component.parent = this;
		}

		extra = components;
	}

	/**
	 * Appends a text element to the component. The text will inherit this
	 * component's formatting
	 *
	 * @param text the text to append
	 */
	public void addExtra(String text)
	{
		addExtra(new TextComponent(text));
	}

	/**
	 * Appends a component to the component. The text will inherit this
	 * component's formatting
	 *
	 * @param component the component to append
	 */
	public void addExtra(BaseComponent component)
	{
		if (extra == null)
			extra = new ArrayList<>();

		component.parent = this;
		extra.add(component);
	}

	/**
	 * Returns whether the component has any formatting or events applied to it
	 *
	 * @return
	 */
	public boolean hasFormatting()
	{
		return color != null || bold != null || italic != null || underlined != null || strikethrough != null || obfuscated != null
				|| hoverEvent != null || clickEvent != null;
	}

	/**
	 * Converts the component into a string without any formatting
	 *
	 * @return the string as plain text
	 */
	public String toPlainText()
	{
		StringBuilder builder = new StringBuilder();
		toPlainText(builder);
		return builder.toString();
	}

	protected void toPlainText(StringBuilder builder)
	{
		if (extra != null)
		{
			for (BaseComponent e : extra)
			{
				e.toPlainText(builder);
			}
		}
	}

	/**
	 * Converts the component to a string that uses the old formatting codes.
	 *
	 * @return the string in the old format
	 * @see ChatColor
	 */
	public String toLegacyText()
	{
		StringBuilder builder = new StringBuilder();
		toLegacyText(builder);
		return builder.toString();
	}

	protected void toLegacyText(StringBuilder builder)
	{
		if (extra != null)
		{
			for (BaseComponent e : extra)
			{
				e.toLegacyText(builder);
			}
		}
	}

	@Override
	public String toString()
	{
		return String.format("BaseComponent{color=%s, bold=%b, italic=%b, underlined=%b, strikethrough=%b, obfuscated=%b, clickEvent=%s, hoverEvent=%s, extra=%s}",
				getColor(), isBold(), isItalic(), isUnderlined(), isStrikethrough(), isObfuscated(), getClickEvent(),
				getHoverEvent(), getExtra());
	}
}
