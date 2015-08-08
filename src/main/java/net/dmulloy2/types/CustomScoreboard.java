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
package net.dmulloy2.types;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Data;
import net.dmulloy2.util.FormatUtil;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * A scoreboard with custom keys and values.
 * @author dmulloy2
 */

@Data
public final class CustomScoreboard
{
	/**
	 * Represents the supported entry formats.
	 * @author dmulloy2
	 */
	public static enum EntryFormat
	{
		/**
		 * Keys and values will be on the same line.
		 */
		ON_LINE,

		/**
		 * Keys and values will be on different lines.
		 */
		NEW_LINE,
		;
	}

	private final Scoreboard board;
	private final String objectiveName;
	private final Map<String, String> entries;

	private String keyPrefix;
	private String valuePrefix;

	private String display;
	private DisplaySlot slot;
	private EntryFormat format = EntryFormat.ON_LINE;

	private CustomScoreboard(Scoreboard board, String objective)
	{
		this.board = board;
		this.objectiveName = objective;
		this.entries = new LinkedHashMap<>();
	}

	/**
	 * Adds an entry to the scoreboard. {@link #update()} should be called after
	 * all entries are added.
	 * 
	 * @param key Key
	 * @param value Value
	 */
	public void addEntry(String key, Object value)
	{
		entries.put(key, String.valueOf(value));
	}

	/**
	 * Updates this scoreboard.
	 */
	public void update()
	{
		board.clearSlot(slot);

		Objective objective = board.getObjective(objectiveName);
		if (objective != null)
			objective.unregister();

		objective = board.registerNewObjective(objectiveName, "dummy");
		objective.setDisplayName(display);

		int score = entries.size();
		if (format == EntryFormat.NEW_LINE)
			score *= 2;

		for (Entry<String, String> entry : entries.entrySet())
		{
			String key = FormatUtil.format(entry.getKey());
			if (keyPrefix != null)
				key = keyPrefix + key;

			String value = FormatUtil.format(entry.getValue());
			if (valuePrefix != null)
				value = valuePrefix + value;

			if (format == EntryFormat.NEW_LINE)
			{
				if (objective.getScore(key).isScoreSet())
					key += nextNull();
				if (objective.getScore(value).isScoreSet())
					value += nextNull();

				objective.getScore(key).setScore(score--);
				objective.getScore(value).setScore(score--);
			}
			else
			{
				String string = key + value;
				if (objective.getScore(string).isScoreSet())
					string += nextNull();

				objective.getScore(string).setScore(score--);
			}
		}
	}

	private void validate()
	{
		Validate.notNull(display, "display cannot be null!");
		Validate.notNull(slot, "slot cannot be null!");
		Validate.notNull(format, "format cannot be null!");
	}

	/**
	 * Assists in building custom scoreboards.
	 * @author dmulloy2
	 */
	public static class Builder
	{
		private final CustomScoreboard board;
		private Builder(Scoreboard board, String objective)
		{
			this.board = new CustomScoreboard(board, objective);
		}

		/**
		 * Sets this scoreboard's display name.
		 * @param display Display name
		 * @return This, for chaining
		 */
		public Builder displayName(String display)
		{
			Validate.notNull(display, "display cannot be null!");
			board.display = display;
			return this;
		}

		/**
		 * Sets this scoreboard's DisplaySlot.
		 * @param slot Display slot
		 * @return This, for chaining
		 */
		public Builder displaySlot(DisplaySlot slot)
		{
			Validate.notNull(slot, "slot cannot be null!");
			board.slot = slot;
			return this;
		}

		/**
		 * Sets this scoreboard's format
		 * @param format EntryFormat
		 * @return This, for chaining
		 */
		public Builder entryFormat(EntryFormat format)
		{
			Validate.notNull(format, "format cannot be null!");
			board.format = format;
			return this;
		}

		/**
		 * Sets the prefix for keys.
		 * @param prefix Prefix
		 * @return This, for chaining
		 */
		public Builder keyPrefix(String prefix)
		{
			Validate.notNull(prefix, "prefix cannot be null!");
			board.keyPrefix = prefix;
			return this;
		}

		/**
		 * Sets the prefix for values.
		 * @param prefix Prefix
		 * @return This, for chaining
		 */
		public Builder valuePrefix(String prefix)
		{
			Validate.notNull(prefix, "prefix cannot be null!");
			board.valuePrefix = prefix;
			return this;
		}

		/**
		 * Adds an entry to this scoreboard.
		 * @param key Key
		 * @param value Value
		 * @return This, for chaining
		 */
		public Builder addEntry(String key, Object value)
		{
			Validate.notNull(key, "key cannot be null!");
			Validate.notNull(value, "value cannot be null!");

			board.entries.put(key, String.valueOf(value));
			return this;
		}

		/**
		 * Builds this scoreboard.
		 * @return The scoreboard
		 */
		public CustomScoreboard build()
		{
			board.validate();
			board.update();
			return board;
		}
	}

	/**
	 * Creates a new scoreboard builder.
	 * @param board Scoreboard
	 * @param objective Objective name
	 * @return The builder
	 */
	public static Builder newBuilder(Scoreboard board, String objective)
	{
		Validate.notNull(board, "board cannot be null!");
		Validate.notNull(objective, "objective cannot be null!");

		return new Builder(board, objective);
	}

	private static int nextNull = 0;

	private static String nextNull()
	{
		ChatColor color = ChatColor.values()[nextNull++];
		if (nextNull >= ChatColor.values().length)
			nextNull = 0;

		return color.toString();
	}
}
