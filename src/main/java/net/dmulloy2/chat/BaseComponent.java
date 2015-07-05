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
	 * Whether the component has any formatting or events applied to it
	 *
	 * @return True if it does, false if not
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
