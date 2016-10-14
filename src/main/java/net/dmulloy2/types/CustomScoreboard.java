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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.dmulloy2.util.FormatUtil;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import lombok.Data;

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

	private static class Entry
	{
		private String key;
		private String value;

		private String line;

		private Entry(String key, String value)
		{
			this.key = key;
			this.value = value;
		}

		private Entry(String line)
		{
			this.line = line;
		}

		public int getSize(EntryFormat format)
		{
			return line == null && format == EntryFormat.NEW_LINE ? 2 : 1;
		}

		@Override
		public int hashCode()
		{
			return line == null ? key.hashCode() : line.hashCode();
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof Entry)
			{
				Entry that = (Entry) obj;
				return line == null ? this.key.equals(that.key) : this.line.equals(that.line);
			}

			return false;
		}
	}

	private final Scoreboard board;
	private final String objectiveName;
	private final List<Entry> entries;

	private String keyPrefix;
	private String valuePrefix;

	private String display;
	private DisplaySlot slot;
	private EntryFormat format = EntryFormat.ON_LINE;
	private int minLength = -1;

	private CustomScoreboard(Scoreboard board, String objective)
	{
		this.board = board;
		this.objectiveName = objective;
		this.entries = new ArrayList<>();
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
		Validate.notNull(key, "key cannot be null!");
		Validate.notNull(value, "value cannot be null!");

		addOrReplace(new Entry(key, String.valueOf(value)));
	}

	/**
	 * Adds an entry to the scoreboard. {@link #update()} should be called after
	 * all entries are added
	 * 
	 * @param line Line
	 */
	public void addEntry(String line)
	{
		Validate.notNull(line, "line cannot be null!");

		addOrReplace(new Entry(line));
	}

	private void addOrReplace(Entry entry)
	{
		if (entries.contains(entry))
			entries.remove(entry);
		entries.add(entry);
	}

	/**
	 * Adds an entry to the scoreboard. {@link #update()} or should be called
	 * after all entries are added.
	 * 
	 * @param entries Entries to add
	 */
	public void addEntries(Map<String, Object> entries)
	{
		Validate.notNull(entries, "entries cannot be null!");

		for (Map.Entry<String, Object> entry : entries.entrySet())
		{
			addEntry(entry.getKey(), entry.getValue());
		}
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
		objective.setDisplaySlot(slot);

		int score = 0;
		for (Entry entry : entries)
			score += entry.getSize(format);

		for (Entry entry : entries)
		{
			if (entry.line != null)
			{
				String string = FormatUtil.format(entry.line);
				if (minLength > 0)
					string = fill(string, minLength);

				if (isScoreSet(objective.getScore(string)))
					string += nextNull();

				objective.getScore(string).setScore(score--);
				continue;
			}
			
			String key = entry.key;
			if (keyPrefix != null)
				key = keyPrefix + key;
			key = FormatUtil.format(key);

			String value = entry.value;
			if (valuePrefix != null)
				value = valuePrefix + value;
			value = FormatUtil.format(value);

			if (format == EntryFormat.NEW_LINE)
			{
				if (minLength > 0)
				{
					key = fill(key, minLength);
					value = fill(value, minLength);
				}

				if (isScoreSet(objective.getScore(key)))
					key += nextNull();
				if (isScoreSet(objective.getScore(value)))
					value += nextNull();

				objective.getScore(key).setScore(score--);
				objective.getScore(value).setScore(score--);
			}
			else
			{
				String string = key + value;
				if (minLength > 0)
					string = fill(string, minLength);
				
				if (isScoreSet(objective.getScore(string)))
					string += nextNull();

				objective.getScore(string).setScore(score--);
			}
		}
	}

	/**
	 * Check if the score's been set. If we can't determine it, just append the
	 * next null anyways.
	 */
	private boolean isScoreSet(Score score)
	{
		try
		{
			return score.isScoreSet();
		} catch (Throwable ex) { }
		return true;
	}

	/**
	 * Disposes of this scoreboard.
	 */
	public void dispose()
	{
		Objective objective = board.getObjective(objectiveName);
		if (objective != null)
			objective.unregister();
	}

	/**
	 * Applies this scoreboard to a given player, replacing any previous
	 * scoreboard.
	 * 
	 * @param player Player to apply to
	 */
	public void applyTo(Player player)
	{
		player.setScoreboard(board);
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
		 * Sets the minimum length for entries.
		 * @param minLength Minimum length
		 * @return This, for chaining
		 */
		public Builder minLength(int minLength)
		{
			Validate.isTrue(minLength > 0, "minLength must be > 0");
			board.minLength = minLength;
			return this;
		}

		/**
		 * Adds an entry to this scoreboard.
		 * @param key Key
		 * @param value Value
		 * @return This, for chaining
		 * @see CustomScoreboard#addEntry(String, Object)
		 */
		public Builder addEntry(String key, Object value)
		{
			board.addEntry(key, value);
			return this;
		}

		/**
		 * Adds an entry to this scoreboard.
		 * @param line Line
		 * @return This, for chaining
		 * @see CustomScoreboard#addEntry(String)
		 */
		public Builder addEntry(String line)
		{
			board.addEntry(line);
			return this;
		}

		/**
		 * Adds a map of entries to this scoreboard.
		 * @param entries Entries to add
		 * @return This, for chaining
		 * @see CustomScoreboard#addEntry(String, Object)
		 */
		public Builder addEntries(Map<String, Object> entries)
		{
			board.addEntries(entries);
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

	private static String fill(String str, int length)
	{
		if (str.length() >= length)
			return str;

		StringBuilder ret = new StringBuilder(str);
		while (ret.length() < length)
			ret.append(" ");

		return ret.toString();
	}

	public static boolean isDefault(Scoreboard board)
	{
		return Bukkit.getScoreboardManager().getMainScoreboard().equals(board);
	}
}
