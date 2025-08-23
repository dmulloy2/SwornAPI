/**
 * SwornAPI - common API for MineSworn and Shadowvolt plugins
 * Copyright (C) 2016 dmulloy2
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
package net.dmulloy2.swornapi.util;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.regex.Pattern;

import net.dmulloy2.swornapi.SwornPlugin;
import net.dmulloy2.swornapi.types.CustomSkullType;
import net.dmulloy2.swornapi.types.EnchantmentType;
import net.dmulloy2.swornapi.types.PotionType;
import net.dmulloy2.swornapi.types.StringJoiner;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Utility for dealing with items and potions
 * @author dmulloy2
 */

public class ItemUtil
{
	private ItemUtil() { }

	/**
	 * Parses an ItemStack from configuration. This provides limited meta
	 * support. This should be surrounded in a try-catch block to deal with
	 * unparsable items.
	 * <p>
	 * The basic format is "<code>[Type]:[Data], [Amount],
	 *  [Enchantment:Level...], [Meta]</code>"
	 *
	 * @param string String to parse
	 * @return ItemStack from given string, unless parsing fails
	 * @throws NullPointerException if the material is null
	 * @throws IllegalArgumentException if the amount is less than 1
	 * @throws IndexOutOfBoundsException if the string is in an improper format
	 */
	public static ItemStack readItem(String string)
	{
		if (string.startsWith("potion:"))
			return readPotion(string);

		String materialStr = string, amountStr = "";
		Map<Enchantment, Integer> enchants = new HashMap<>();

		string = string.replaceAll("\\s", "");
		if (string.contains(","))
		{
			String[] split = string.split(",");

			materialStr = split[0];
			amountStr = split[1];

			for (int i = 2; i < split.length; i++)
			{
				String[] enchSplit = split[i].split(":");

				Enchantment enchant = EnchantmentType.toEnchantment(enchSplit[0]);
				int level = NumberUtil.toInt(enchSplit[1]);

				if (enchant != null && level > 0)
				{
					enchants.put(enchant, level);
				}
			}
		}

		short data = 0;
		if (materialStr.contains(":"))
		{
			String[] split = materialStr.split(":");
			materialStr = split[0];

			data = NumberUtil.toShort(split[1]);
			if (data < 0)
			{
				throw new IllegalArgumentException("Invalid data: " + split[1]);
			}
		}

		Material material = Material.matchMaterial(materialStr);
		if (material == null)
		{
			throw new NullPointerException("Invalid material: " + materialStr);
		}

		int amount = 1;
		if (!amountStr.isEmpty())
		{
			amount = NumberUtil.toInt(amountStr);
			if (amount <= 0)
			{
				throw new IllegalArgumentException("Illegal amount: " + amountStr);
			}
		}

		ItemStack item = new ItemStack(material, amount, data);
		item.addUnsafeEnchantments(enchants);

		// Parse meta
		parseItemMeta(item, string);
		return item;
	}

	/**
	 * Safely reads an item, logging any exceptions.
	 * 
	 * @param string String to parse
	 * @param plugin Plugin instance
	 * @return ItemStack, or null if parsing failed
	 * @see #readItem(String)
	 */
	public static ItemStack readItem(String string, SwornPlugin plugin)
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

	/**
	 * Safely reads a list of items, logging any exceptions.
	 * 
	 * @param strings Strings to parse
	 * @param plugin Plugin instance
	 * @return List of ItemStacks
	 * @see #readItem(String)
	 */
	public static List<ItemStack> readItems(List<String> strings, SwornPlugin plugin)
	{
		List<ItemStack> ret = new ArrayList<>();
		for (String string : strings)
		{
			ItemStack item = readItem(string, plugin);
			if (item != null)
				ret.add(item);
		}

		return ret;
	}

	/**
	 * Parses a potion from configuration.
	 * <p>
	 * The basic format is <code>potion: &lt;type&gt;,&lt;amount&gt;,&ltlevel&gt;,[splash]</code>
	 *
	 * @param string String to read
	 * @return ItemStack from string, or null if parsing fails
	 */
	public static ItemStack readPotion(final String string)
	{
		// Normalize string
		String normalized = string.replaceAll("\\s", "");
		normalized = normalized.substring(string.indexOf(":") + 1);

		String[] split = normalized.split(",");

		PotionEffectType type = PotionType.findEffect(split[0]);
		if (type == null)
			throw new NullPointerException("Null potion type \"" + split[0] + "\"");

		int amount = NumberUtil.toInt(split[1]);
		if (amount < 0)
			throw new IllegalArgumentException("Invalid amount " + amount);

		int level = NumberUtil.toInt(split[2]);
		if (level < 0)
			throw new IllegalArgumentException("Invalid level " + level);

		boolean splash = split.length > 3 && Util.toBoolean(split[3]);
		boolean extended = split.length > 4 && Util.toBoolean(split[4]);

		Material material = splash ? Material.SPLASH_POTION : Material.POTION;

		ItemStack item = new ItemStack(material, amount);
		PotionMeta potionData = (PotionMeta) item.getItemMeta();
		PotionEffect effect = new PotionEffect(type, extended ? 9600 : 3600, level - 1);
		potionData.addCustomEffect(effect, true);
		item.setItemMeta(potionData);

		// Parse meta
		parseItemMeta(item, normalized);
		return item;
	}

	private static final Pattern LORE_MATCHER = Pattern.compile("name:|lore:|color:|owner:|type:");

	/**
	 * Parses ItemMeta from a given string, then applies it to a given item.
	 * @param item Item to apply meta to
	 * @param string String to parse meta from
	 */
	public static void parseItemMeta(ItemStack item, String string)
	{
		if (! LORE_MATCHER.matcher(string).find())
		{
			return;
		}

		ItemMeta meta = item.getItemMeta();
		if (meta == null)
		{
			return;
		}

		try
		{
			// Name
			String nameKey = "name:";
			if (string.contains(nameKey))
			{
				String name = string.substring(string.indexOf(nameKey) + nameKey.length());
				int commaIndex = name.indexOf(",");
				if (commaIndex != -1)
					name = name.substring(0, commaIndex);

				meta.setDisplayName(FormatUtil.format(name.replace('_', ' ')));
			}

			// Lore
			String loreKey = "lore:";
			if (string.contains(loreKey))
			{
				String str = string.substring(string.indexOf(loreKey) + loreKey.length());
				int commaIndex = str.indexOf(",");
				if (commaIndex != -1)
					str = str.substring(0, commaIndex);
				str = str.replace('_', ' ');

				List<String> lore = new ArrayList<>();
				for (String split : str.split("\\|"))
					lore.add(FormatUtil.format(split));
				meta.setLore(lore);
			}

			// Leather armor
			if (meta instanceof LeatherArmorMeta)
			{
				String colorKey = "color:";
				if (string.contains(colorKey))
				{
					String str = string.substring(string.indexOf(colorKey) + colorKey.length());
					int commaIndex = str.indexOf(",");
					if (commaIndex != -1)
						str = str.substring(0, commaIndex);

					DyeColor dyeColor = DyeColor.valueOf(str.toUpperCase());
					((LeatherArmorMeta) meta).setColor(dyeColor.getColor());
				}
			}

			// Skulls
			if (meta instanceof SkullMeta)
			{
				String ownerKey = "owner:";
				String typeKey = "type:";

				if (string.contains(ownerKey))
				{
					String owner = string.substring(string.indexOf(ownerKey) + ownerKey.length());

					// Attempt to use CustomSkullType
					CustomSkullType type = CustomSkullType.get(owner);
					if (type != null)
						((SkullMeta) meta).setOwner(type.getOwner());
					else
						((SkullMeta) meta).setOwner(owner);
				}
				else if (string.contains(typeKey))
				{
					String type = string.substring(string.indexOf(typeKey) + typeKey.length());

					// Attempt to use CustomSkullType
					CustomSkullType customType = CustomSkullType.get(type);
					((SkullMeta) meta).setOwner(customType.getOwner());
				}
			}

			// TODO: Firework and Book support
			item.setItemMeta(meta);
		} catch (Throwable ignored) { }
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
			ret.append(":").append(stack.getDurability());
		ret.append(", ").append(stack.getAmount());

		if (! stack.getEnchantments().isEmpty())
		{
			StringJoiner joiner = new StringJoiner(", ");
			for (Entry<Enchantment, Integer> ench : stack.getEnchantments().entrySet())
				joiner.append(EnchantmentType.toName(ench.getKey()) + ":" + ench.getValue());
			ret.append(", ").append(joiner);
		}

		ItemMeta meta = stack.getItemMeta();
		if (meta == null)
		{
			return ret.toString();
		}

		if (meta.hasDisplayName())
		{
			ret.append(", name:").append(meta.getDisplayName()
					.replace(ChatColor.COLOR_CHAR, '&')
					.replace(' ', '_'));
		}

		if (meta.hasLore())
		{
			StringJoiner lore = new StringJoiner("|");
			for (String line : meta.getLore())
			{
				lore.append(line
						.replace(ChatColor.COLOR_CHAR, '&')
						.replace(' ', '_'));
			}

			ret.append(", lore:").append(lore.toString());
		}

		// TODO: More meta support

		return ret.toString();
	}

	/**
	 * Returns an ItemStack's enchantments in string form.
	 *
	 * @param stack ItemStack to get enchantments
	 * @return The item's enchantments in string form
	 */
	public static String getEnchantments(ItemStack stack)
	{
		StringBuilder ret = new StringBuilder();
		if (! stack.getEnchantments().isEmpty())
		{
			ret.append("(");
			for (Entry<Enchantment, Integer> enchantment : stack.getEnchantments().entrySet())
				ret.append(EnchantmentType.toName(enchantment.getKey())).append(": ").append(enchantment.getValue())
				   .append(", ");
			ret.delete(ret.lastIndexOf(","), ret.lastIndexOf(" "));
			ret.append(")");
		}

		return ret.toString();
	}
}
