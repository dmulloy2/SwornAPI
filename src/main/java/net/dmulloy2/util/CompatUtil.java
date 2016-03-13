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

import net.dmulloy2.types.PotionType;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;

/**
 * Utility for dealing with backwards compatibility
 * @author dmulloy2
 */

@SuppressWarnings("deprecation")
public class CompatUtil
{
	/**
	 * Creates a potion with the given attributes. Potions were changed in 1.9, necessitating this method.
	 * @param type Potion type
	 * @param amount ItemStack amount
	 * @param level Potion level
	 * @param splash Whether or not it's a splash potion
	 * @return The potion item
	 */
	public static ItemStack createPotion(PotionType type, int amount, int level, boolean splash)
	{
		try
		{
			Material material = splash ? Material.SPLASH_POTION : Material.POTION;
			PotionData data = new PotionData(type.getBukkit(), level == 2, false);
			ItemStack potion = new ItemStack(material, amount);
			PotionMeta meta = (PotionMeta) potion.getItemMeta();
			meta.setBasePotionData(data);
			potion.setItemMeta(meta);
			return potion;
		}
		catch (LinkageError e)
		{
			Potion potion = new Potion(1);
			potion.setType(type.getBukkit());
			potion.setLevel(level);
			potion.setSplash(splash);
			return potion.toItemStack(amount);
		}
	}

	/**
	 * Gets the item in a player's main hand. A second hand was added in 1.9, necessitating this method.
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
}