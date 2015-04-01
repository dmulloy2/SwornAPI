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
import java.util.logging.Level;

import lombok.AllArgsConstructor;
import net.dmulloy2.SwornPlugin;
import net.dmulloy2.util.ItemUtil;
import net.dmulloy2.util.Util;

import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@AllArgsConstructor
public class ItemParser
{
	private final SwornPlugin plugin;

	public final ItemStack parse(String string)
	{
		try
		{
			return ItemUtil.readItem(string);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "parsing item \"" + string + "\""));
			return null;
		}
	}

	public static final ItemStack parse(SwornPlugin plugin, String string)
	{
		try
		{
			return ItemUtil.readItem(string);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "parsing item \"" + string + "\""));
			return null;
		}
	}

	public final List<ItemStack> parse(List<String> strings)
	{
		List<ItemStack> ret = new ArrayList<>();
		for (String string : strings)
		{
			ItemStack item = parse(string);
			if (item != null)
				ret.add(item);
		}

		return ret;
	}

	public static final List<ItemStack> parse(final SwornPlugin plugin, List<String> strings)
	{
		List<ItemStack> ret = new ArrayList<>();
		for (String string : strings)
		{
			ItemStack item = parse(plugin, string);
			if (item != null)
				ret.add(item);
		}

		return ret;
	}
}
