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
package net.dmulloy2.swornapi.gui;

import net.dmulloy2.swornapi.SwornPlugin;
import net.dmulloy2.swornapi.util.FormatUtil;
import net.dmulloy2.swornapi.util.NumberUtil;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

/**
 * A clickable chest GUI
 * @author dmulloy2
 */

public abstract class AbstractGUI
{
	protected final Player player;
	protected final SwornPlugin plugin;

	public AbstractGUI(SwornPlugin plugin, Player player)
	{
		Validate.notNull(plugin, "plugin cannot be null");
		Validate.notNull(player, "player cannot be null!");
		this.player = player;
		this.plugin = plugin;
	}

	/**
	 * Sets up this inventory, handling the size and title. This method must be
	 * called for the inventory to actually open.
	 * 
	 * @throws IllegalArgumentException if {@code size < 0}, {@code size > 54},
	 *         or the title is null.
	 */
	protected final void setup()
	{
		// Size checks
		int size = NumberUtil.roundUp(getSize(), 9);
		Validate.isTrue(size > 0, "Inventory size must not be negative!");
		Validate.isTrue(size <= 54, "Inventory size is too large! (" + size + " > 54)");

		// Validate title
		String title = getTitle();
		Validate.notNull(title, "Inventory title cannot be null!");
		title = FormatUtil.format(getTitle());

		Inventory inventory;

		try
		{
			inventory = plugin.getServer().createInventory(player, size, title);
		}
		catch (IllegalArgumentException ex)
		{
			// Truncate the title and add the unicode ...
			title = title.substring(0, 31) + "\u2026";
			inventory = plugin.getServer().createInventory(player, size, title);
		}

		stock(inventory);
		player.openInventory(inventory);
	}

	public final Player getPlayer()
	{
		return player;
	}

	// ---- Required Methods

	/**
	 * Gets the amount of items in this inventory. If this is not a multiple of
	 * 9, it will be rounded up. This number must be less than or equal to 54.
	 * 
	 * @return The amount of items
	 */
	public abstract int getSize();

	/**
	 * Gets the title of this inventory. Before 1.8 this had to be less than 32
	 * characters.
	 * 
	 * @return The title
	 */
	public abstract String getTitle();

	/**
	 * Stocks this inventory with items.
	 * 
	 * @param inventory Inventory to stock
	 */
	public abstract void stock(Inventory inventory);

	// ---- Messaging

	/**
	 * Sends an error message to the player.
	 * 
	 * @param message Message to send
	 * @param args Objects to format in
	 */
	protected final void err(String message, Object... args)
	{
		sendMessage("&cError: &4" + FormatUtil.format(message, args));
	}

	/**
	 * Sends a prefixed message to the player.
	 * 
	 * @param message Message to send
	 * @param objects Objects to format in
	 */
	protected final void sendpMessage(String message, Object... objects)
	{
		sendMessage(plugin.getPrefix() + message, objects);
	}

	/**
	 * Sends a message to the player.
	 * 
	 * @param message Message to send
	 * @param objects Objects to format in
	 */
	protected final void sendMessage(String message, Object... objects)
	{
		player.sendMessage(ChatColor.YELLOW + FormatUtil.format(message, objects));
	}

	// ---- Events

	/**
	 * Called when an InventoryClickEvent is called.
	 * 
	 * @param event The event
	 */
	public void onInventoryClick(InventoryClickEvent event) { }

	/**
	 * Called when an InventoryCloseEvent is called.
	 * 
	 * @param event The event
	 */
	public void onInventoryClose(InventoryCloseEvent event) { }
}
