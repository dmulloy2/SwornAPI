/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.types;

import java.util.List;
import java.util.logging.Level;

import lombok.AllArgsConstructor;
import net.dmulloy2.SwornPlugin;
import net.dmulloy2.util.ItemUtil;
import net.dmulloy2.util.Util;

import org.bukkit.inventory.ItemStack;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

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
		return Lists.transform(strings, new Function<String, ItemStack>()
		{
			@Override
			public ItemStack apply(String string)
			{
				return parse(string);
			}
		});
	}

	public static final List<ItemStack> parse(final SwornPlugin plugin, List<String> strings)
	{
		return Lists.transform(strings, new Function<String, ItemStack>()
		{
			@Override
			public ItemStack apply(String string)
			{
				return parse(plugin, string);
			}
		});
	}
}