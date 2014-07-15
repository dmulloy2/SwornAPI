/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.types.EnchantmentType;
import net.dmulloy2.types.StringJoiner;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

/**
 * Util that deals with Items.
 *
 * @author dmulloy2
 */

public class ItemUtil
{
	private ItemUtil() { }

	/**
	 * Reads an ItemStack from configuration.
	 * <p>
	 * The basic format is "[Type/ID]:[Data], [Amount], [Enchantment:Level...], [Meta]"
	 *
	 * @param string String to read
	 * @return ItemStack from given string, or null if parsing fails
	 */
	public static ItemStack readItem(String string)
	{
		try
		{
			Material mat = null;

			int amt = 0;
			short dat = 0;

			Map<Enchantment, Integer> enchantments = new HashMap<>();

			// Calculate lore first
			String name = "";
			List<String> lore = null;

			try
			{
				// Name
				String nameKey = "name:";
				if (string.contains(nameKey))
				{
					name = string.substring(string.indexOf(nameKey) + nameKey.length());
					int commaIndex = name.indexOf(",");
					if (commaIndex != -1)
						name = name.substring(0, commaIndex);
					name = name.replaceAll("_", " ");
					name = FormatUtil.format(name);
				}

				// Lore
				String loreKey = "lore:";
				if (string.contains(loreKey))
				{
					String str = string.substring(string.indexOf(loreKey) + loreKey.length());
					str = str.replaceAll("_", " ");

					lore = new ArrayList<>();
					for (String split : str.split("\\|"))
						lore.add(FormatUtil.format(split));
				}
			} catch (Throwable ex) { }

			// Remove any spaces
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
									int level = NumberUtil.toInt(ench.substring(ench.indexOf(":") + 1));

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
								int level = NumberUtil.toInt(s.substring(s.indexOf(":") + 1));

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

			if (mat == null || amt <= 0)
				return null;

			ItemStack ret = new ItemStack(mat, amt, dat);
			ret.addUnsafeEnchantments(enchantments);

			// ItemMeta
			ItemMeta meta = ret.getItemMeta();
			if (! name.isEmpty())
				meta.setDisplayName(name);
			if (lore != null)
				meta.setLore(lore);
			ret.setItemMeta(meta);

			return ret;
		} catch (Throwable ex) { }
		return null;
	}

	/**
	 * Reads a potion from configuration
	 *
	 * @param string - String to read
	 * @return ItemStack from string (will be a potion)
	 */
	public static ItemStack readPotion(String string)
	{
		try
		{
			string = string.replaceAll(" ", "");
			string = string.substring(string.indexOf(":") + 1);

			String[] split = string.split(",");
			if (split.length == 3)
			{
				// Get the type
				PotionType type = net.dmulloy2.types.PotionType.toType(split[0]);
				if (type != null)
				{
					// Get the amount
					int amount = NumberUtil.toInt(split[1]);
					if (amount != -1)
					{
						// Get the level
						int level = NumberUtil.toInt(split[2]);
						if (level != -1)
						{
							// Build potion / stack
							Potion potion = new Potion(1);
							potion.setType(type);
							potion.setLevel(level);
							potion.setSplash(false);
							ItemStack ret = potion.toItemStack(amount);
							return ret;
						}
					}
				}
			}
			else if (split.length == 4)
			{
				// Get the type
				PotionType type = net.dmulloy2.types.PotionType.toType(split[0]);
				if (type != null)
				{
					// Get the amount
					int amount = NumberUtil.toInt(split[1]);
					if (amount != -1)
					{
						// Get the level
						int level = NumberUtil.toInt(split[2]);
						if (level != -1)
						{
							// Is splash
							boolean splash = Util.toBoolean(split[3]);

							// Build potion / stack
							Potion potion = new Potion(1);
							potion.setType(type);
							potion.setLevel(level);
							potion.setSplash(splash);
							ItemStack ret = potion.toItemStack(amount);
							return ret;
						}
					}
				}
			}
		} catch (Throwable ex) { }
		return null;
	}

	/**
	 * Serializes a given ItemStack in the same format as
	 * {@link ItemUtil#readItem(String)}.
	 *
	 * @param stack Stack to serialize
	 * @return Serialized string
	 */
	public static String serialize(ItemStack stack)
	{
		StringBuilder ret = new StringBuilder();
		ret.append(stack.getType());
		if (stack.getDurability() > 0)
			ret.append(":" + stack.getDurability());
		ret.append(", " + stack.getAmount());

		if (! stack.getEnchantments().isEmpty())
		{
			StringJoiner joiner = new StringJoiner(", ");
			for (Entry<Enchantment, Integer> ench : stack.getEnchantments().entrySet())
				joiner.append(EnchantmentType.toName(ench.getKey()) + ":" + ench.getValue());
			ret.append(", " + joiner.toString());
		}

		ItemMeta meta = stack.getItemMeta();
		if (meta.hasDisplayName())
		{
			String name = meta.getDisplayName();
			name = name.replaceAll(ChatColor.COLOR_CHAR + "", "&");
			name = name.replaceAll(" ", "_");
			ret.append(", name:" + name);
		}

		if (meta.hasLore())
		{
			StringJoiner lore = new StringJoiner("|");
			for (String line : meta.getLore())
			{
				line = line.replaceAll(ChatColor.COLOR_CHAR + "", "&");
				line = line.replaceAll(" ", "_");
				lore.append(line);
			}

			ret.append(", lore:" + lore.toString());
		}

		return ret.toString();
	}

	/**
	 * Returns the basic data of an ItemStack in string form
	 *
	 * @param stack ItemStack to "convert" to a string
	 * @return ItemStack's data in string form
	 * @deprecated ItemStack defines a pretty useful toString() method
	 */
	@Deprecated
	public static String itemToString(ItemStack stack)
	{
		StringBuilder ret = new StringBuilder();
		ret.append("Type: " + FormatUtil.getFriendlyName(stack.getType()));
		ret.append(" Data: " + stack.getDurability());
		ret.append(" Amount: " + stack.getAmount());
		ret.append(" Enchants:");
		for (Entry<Enchantment, Integer> enchantment : stack.getEnchantments().entrySet())
			ret.append(" " + EnchantmentType.toName(enchantment.getKey()) + ": " + enchantment.getValue());
		ret.append(" ItemMeta: " + stack.getItemMeta());

		return ret.toString();
	}

	/**
	 * Returns an ItemStack's enchantments in string form
	 *
	 * @param stack ItemStack to get enchantments
	 * @return ItemStack's enchantments in string form
	 */
	public static String getEnchantments(ItemStack stack)
	{
		StringBuilder ret = new StringBuilder();
		if (! stack.getEnchantments().isEmpty())
		{
			ret.append("(");
			for (Entry<Enchantment, Integer> enchantment : stack.getEnchantments().entrySet())
				ret.append(EnchantmentType.toName(enchantment.getKey()) + ": " + enchantment.getValue() + ", ");
			ret.delete(ret.lastIndexOf(","), ret.lastIndexOf(" "));
			ret.append(")");
		}

		return ret.toString();
	}
}