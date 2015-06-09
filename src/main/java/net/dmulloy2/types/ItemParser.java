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

import java.util.List;

import lombok.AllArgsConstructor;
import net.dmulloy2.SwornPlugin;
import net.dmulloy2.util.ItemUtil;

import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@Deprecated
@AllArgsConstructor
public class ItemParser
{
	private final SwornPlugin plugin;

	public final ItemStack parse(String string)
	{
		return parse(plugin, string);
	}

	public static final ItemStack parse(SwornPlugin plugin, String string)
	{
		return ItemUtil.readItem(string, plugin);
	}

	public final List<ItemStack> parse(List<String> strings)
	{
		return parse(plugin, strings);
	}

	public static final List<ItemStack> parse(final SwornPlugin plugin, List<String> strings)
	{
		return ItemUtil.readItems(strings, plugin);
	}
}