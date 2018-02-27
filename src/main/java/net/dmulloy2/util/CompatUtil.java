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
package net.dmulloy2.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

/**
 * Utility for dealing with backwards compatibility
 * @author dmulloy2
 */

@SuppressWarnings("deprecation")
public class CompatUtil
{
	/**
	 * Creates a potion with the given attributes. Potions were changed in 1.9,
	 * necessitating this method.
	 * 
	 * @param type Potion type
	 * @param amount ItemStack amount
	 * @param level Potion level
	 * @param splash Whether or not it's a splash potion
	 * @param extended Extended potion
	 * @return The potion item
	 */
	public static ItemStack createPotion(PotionType type, int amount, int level, boolean splash, boolean extended)
	{
		try
		{
			Material material = splash ? Material.SPLASH_POTION : Material.POTION;
			PotionData data = new PotionData(type, level == 2, extended);
			ItemStack potion = new ItemStack(material, amount);
			PotionMeta meta = (PotionMeta) potion.getItemMeta();
			meta.setBasePotionData(data);
			potion.setItemMeta(meta);
			return potion;
		}
		catch (LinkageError e)
		{
			Potion potion = new Potion(1);
			potion.setType(type);
			potion.setLevel(level);
			potion.setSplash(splash);
			potion.setHasExtendedDuration(extended);
			return potion.toItemStack(amount);
		}
	}

	/**
	 * Gets the item in a player's main hand. The ability to use the other hand
	 * was added in 1.9, necessitating this method.
	 * 
	 * @param player Player to get item from
	 * @return The item
	 */
	public static ItemStack getItemInMainHand(Player player)
	{
		try
		{
			return player.getInventory().getItemInMainHand();
		}
		catch (LinkageError e)
		{
			return player.getItemInHand();
		}
	}

	/**
	 * Sets the item in a player's main hand to a given item stack. The ability
	 * to use the other hand was added in 1.9, necessitating this method.
	 * 
	 * @param player Player to set item in hand
	 * @param item Item to set
	 */
	public static void setItemInMainHand(Player player, ItemStack item)
	{
		try
		{
			player.getInventory().setItemInMainHand(item);
		}
		catch (LinkageError e)
		{
			player.setItemInHand(item);
		}
	}

	/**
	 * Gets a player's max health. Implementations targeting newer versions of
	 * Minecraft should use Attributes.
	 * 
	 * @param player Player to get max health of
	 * @return The max health
	 */
	public static double getMaxHealth(Player player)
	{
		try
		{
			return player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue();
		}
		catch (LinkageError e)
		{
			return player.getMaxHealth();
		}
	}

	/**
	 * Sets a player's max health. Implementations targeting newer versions of
	 * Minecraft should use Attributes.
	 * 
	 * @param player Player to set max health of
	 * @param value New max health
	 */
	public static void setMaxHealth(Player player, double value)
	{
		try
		{
			player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(value);
		}
		catch (LinkageError e)
		{
			player.setMaxHealth(value);
		}
	}
}