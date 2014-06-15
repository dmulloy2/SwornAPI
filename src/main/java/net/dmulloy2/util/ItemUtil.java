/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.types.EnchantmentType;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Util that deals with Items
 * 
 * @author dmulloy2
 */

public class ItemUtil
{
	private ItemUtil() { }

	/**
	 * Reads an ItemStack from configuration
	 * <p>
	 * The basic format is "[Type/ID]:[Data], [Amount], [Enchantment:Level...]"
	 * 
	 * @param string
	 *        - String to read
	 * @return ItemStack from given string
	 */
	public static ItemStack readItem(String string)
	{
		try
		{
			Material mat = null;

			int amt = 0;
			short dat = 0;

			Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();

			string = string.replaceAll(" ", "");
			if (string.contains(","))
			{
				String s = string.substring(0, string.indexOf(","));
				if (s.contains(":"))
				{
					mat = MaterialUtil.getMaterial(s.substring(0, s.indexOf(":")));

					dat = Short.parseShort(s.substring(s.indexOf(":") + 1));
				}
				else
				{
					mat = MaterialUtil.getMaterial(s);
				}

				s = string.substring(string.indexOf(",") + 1);
				if (s.contains(","))
				{
					amt = Integer.parseInt(s.substring(0, s.indexOf(",")));

					s = s.substring(s.indexOf(",") + 1);

					if (! s.isEmpty())
					{
						if (s.contains(","))
						{
							String[] split = s.split(",");
							for (String ench : split)
							{
								if (ench.contains(":"))
								{
									Enchantment enchant = EnchantmentType.toEnchantment(ench.substring(0, ench.indexOf(":")));
									int level = Integer.parseInt(ench.substring(ench.indexOf(":") + 1));

									if (enchant != null && level > 0)
									{
										enchantments.put(enchant, level);
									}
								}
							}
						}
						else
						{
							if (s.contains(":"))
							{
								Enchantment enchant = EnchantmentType.toEnchantment(s.substring(0, s.indexOf(":")));
								int level = Integer.parseInt(s.substring(s.indexOf(":") + 1));

								if (enchant != null && level > 0)
								{
									enchantments.put(enchant, level);
								}
							}
						}
					}
				}
				else
				{
					amt = Integer.parseInt(s);
				}
			}

			ItemStack ret = null;
			if (mat != null && amt > 0)
			{
				ret = new ItemStack(mat, amt, dat);
			}

			if (ret != null && ! enchantments.isEmpty())
			{
				ret.addUnsafeEnchantments(enchantments);
			}

			return ret;
		} catch (Throwable ex) { }
		return null;
	}

	/**
	 * Returns the basic data of an ItemStack in string form
	 * 
	 * @param stack
	 *        - ItemStack to "convert" to a string
	 * @return ItemStack's data in string form
	 */
	public static String itemToString(ItemStack stack)
	{
		StringBuilder ret = new StringBuilder();
		ret.append("Type: " + FormatUtil.getFriendlyName(stack.getType()));
		ret.append(" Data: " + stack.getDurability());
		ret.append(" Amount: " + stack.getAmount());
		ret.append(" Enchants:");
		for (Entry<Enchantment, Integer> enchantment : stack.getEnchantments().entrySet())
		{
			ret.append(" " + EnchantmentType.toName(enchantment.getKey()) + ": " + enchantment.getValue());
		}

		return ret.toString();
	}

	/**
	 * Returns an ItemStack's enchantments in string form
	 * 
	 * @param stack
	 *        - ItemStack to get enchantments
	 * @return ItemStack's enchantments in string form
	 */
	public static String getEnchantments(ItemStack stack)
	{
		StringBuilder ret = new StringBuilder();
		if (! stack.getEnchantments().isEmpty())
		{
			ret.append("(");
			for (Entry<Enchantment, Integer> enchantment : stack.getEnchantments().entrySet())
			{
				ret.append(EnchantmentType.toName(enchantment.getKey()) + ": " + enchantment.getValue() + ", ");
			}
			ret.delete(ret.lastIndexOf(","), ret.lastIndexOf(" "));
			ret.append(")");
		}

		return ret.toString();
	}
}