/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.types;

import java.util.List;
import java.util.logging.Level;

import lombok.AllArgsConstructor;
import net.dmulloy2.SwornPlugin;
import net.dmulloy2.util.ItemUtil;
import net.dmulloy2.util.ListUtil;
import net.dmulloy2.util.Util;

import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@AllArgsConstructor
public class ItemParser
{
	private final SwornPlugin plugin;

	public ItemStack parse(String string)
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

	public List<ItemStack> parse(List<String> strings)
	{
		return ListUtil.transform(strings, new Transformation<String, ItemStack>()
		{
			@Override
			public ItemStack transform(String string)
			{
				return parse(string);
			}
		});
	}
}