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
package net.dmulloy2.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Util dealing with Inventories.
 *
 * @author dmulloy2
 */

public class InventoryUtil
{
	private InventoryUtil() { }

	/**
	 * Whether or not a given {@link Inventory} is empty.
	 *
	 * @param inventory Inventory to check
	 * @return True if the inventory is empty, false if not
	 */
	public static boolean isEmpty(Inventory inventory)
	{
		Validate.notNull(inventory, "inventory cannot be null!");

		for (ItemStack stack : inventory.getContents())
		{
			if (stack != null && stack.getType() != Material.AIR)
				return false;
		}

		if (inventory instanceof PlayerInventory)
		{
			for (ItemStack armor : ((PlayerInventory) inventory).getArmorContents())
			{
				if (armor != null && armor.getType() != Material.AIR)
					return false;
			}
		}

		return true;
	}

	/**
	 * Gives a {@link Player} an item.
	 *
	 * @param player {@link Player} to give them item to
	 * @param item {@link ItemStack} to give the player
	 * @return Leftovers, if any
	 */
	public static Map<Integer, ItemStack> giveItem(Player player, ItemStack item)
	{
		Validate.notNull(player, "player cannot be null!");
		return addItems(player.getInventory(), item);
	}

	/**
	 * Gives a {@link Player} items.
	 *
	 * @param player {@link Player} to give them item to
	 * @param items {@link ItemStack}s to give the player
	 * @return Leftovers, if any
	 */
	public static Map<Integer, ItemStack> giveItems(Player player, ItemStack... items)
	{
		Validate.notNull(player, "player cannot be null!");
		Validate.noNullElements(items, "items cannot contain null elements!");

		return addItems(player.getInventory(), items);
	}

	/**
	 * Gets the amount of {@link ItemStack}s in an inventory with a given type
	 * and data.
	 *
	 * @param inventory Inventory containing the items
	 * @param type Material of the item
	 * @param dat Item data
	 * @return The amount of items
	 */
	public static int amount(Inventory inventory, Material type, short dat)
	{
		Validate.notNull(inventory, "inventory cannot be null!");
		Validate.notNull(type, "type cannot be null!");

		int ret = 0;
		ItemStack[] items = inventory.getContents();
		for (int slot = 0; slot < items.length; slot++)
		{
			ItemStack item = items[slot];
			if (item != null && item.getType() != Material.AIR)
			{
				Material mat = item.getType();
				short duration = item.getDurability();
				int amt = item.getAmount();
				if (mat == type)
				{
					if (dat == -1 || dat == duration)
						ret += amt;
				}
			}
		}

		return ret;
	}

	/**
	 * Removes items from an inventory.
	 *
	 * @param inventory Inventory to remove items from
	 * @param type Type of the items
	 * @param dat Data of the items
	 * @param amt Amount to remove
	 * @throws IllegalArgumentException if {@code amt} is less than 0
	 */
	public static void remove(Inventory inventory, Material type, short dat, int amt)
	{
		Validate.notNull(inventory, "inventory cannot be null!");
		Validate.notNull(type, "type cannot be null!");
		Validate.isTrue(amt > 0, "amt cannot be less than 0!");

		int start = amt;
		if (inventory != null)
		{
			ItemStack[] items = inventory.getContents();
			for (int slot = 0; slot < items.length; slot++)
			{
				if (items[slot] != null)
				{
					Material mat = items[slot].getType();
					short duration = items[slot].getDurability();
					int itmAmt = items[slot].getAmount();
					if ((mat == type) && ((dat == duration) || (dat == - 1)))
					{
						if (amt > 0)
						{
							if (itmAmt >= amt)
							{
								itmAmt -= amt;
								amt = 0;
							}
							else
							{
								amt = start - itmAmt;
								itmAmt = 0;
							}
							if (itmAmt > 0)
							{
								inventory.getItem(slot).setAmount(itmAmt);
							}
							else
							{
								inventory.setItem(slot, null);
							}
						}

						if (amt <= 0)
							return;
					}
				}
			}
		}
	}

	// ---- Internal Methods

	private static Map<Integer, ItemStack> addItems(Inventory inventory, ItemStack... items)
	{
		return addOversizedItems(inventory, 0, items);
	}

	private static Map<Integer, ItemStack> addOversizedItems(Inventory inventory, int oversizedStacks, ItemStack... items)
	{
		Map<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

		ItemStack[] combined = new ItemStack[items.length];
		for (int i = 0; i < items.length; i++)
		{
			if (items[i] == null || items[i].getAmount() < 1)
				continue;

			for (int j = 0; j < combined.length; j++)
			{
				if (combined[j] == null)
				{
					combined[j] = items[i].clone();
					combined[j].setData(items[i].getData());
					break;
				}

				if (combined[j].isSimilar(items[i]))
				{
					combined[j].setAmount(combined[j].getAmount() + items[i].getAmount());
					break;
				}
			}
		}

		for (int i = 0; i < combined.length; i++)
		{
			ItemStack item = combined[i];
			if (item == null)
				continue;

			while (true)
			{
				// Do we already have a stack of it?
				int maxAmount = oversizedStacks > item.getType().getMaxStackSize() ? oversizedStacks : item.getType().getMaxStackSize();
				int firstPartial = firstPartial(inventory, item, maxAmount);

				// Drat! no partial stack
				if (firstPartial == - 1)
				{
					// Find a free spot!
					int firstFree = inventory.firstEmpty();

					if (firstFree == - 1)
					{
						// No space at all!
						leftover.put(i, item);
						break;
					}
					else
					{
						// More than a single stack!
						if (item.getAmount() > maxAmount)
						{
							ItemStack stack = item.clone();
							stack.setData(item.getData());
							stack.setAmount(maxAmount);
							inventory.setItem(firstFree, stack);
							item.setAmount(item.getAmount() - maxAmount);
						}
						else
						{
							// Just store it
							inventory.setItem(firstFree, item);
							break;
						}
					}
				}
				else
				{
					// So, apparently it might only partially fit, well lets do
					// just that
					ItemStack partialItem = inventory.getItem(firstPartial);

					int amount = item.getAmount();
					int partialAmount = partialItem.getAmount();

					// Check if it fully fits
					if (amount + partialAmount <= maxAmount)
					{
						partialItem.setAmount(amount + partialAmount);
						break;
					}

					// It fits partially
					partialItem.setAmount(maxAmount);
					item.setAmount(amount + partialAmount - maxAmount);
				}
			}
		}

		return leftover;
	}

	private static int firstPartial(Inventory inventory, ItemStack item, int maxAmount)
	{
		if (item == null)
			return -1;

		ItemStack[] stacks = inventory.getContents();
		for (int i = 0; i < stacks.length; i++)
		{
			ItemStack cItem = stacks[i];
			if (cItem != null && cItem.getAmount() < maxAmount && cItem.isSimilar(item))
				return i;
		}

		return -1;
	}
}
