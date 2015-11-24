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
package net.dmulloy2.gui;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.NumberUtil;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

/**
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
		if (title.length() > 32)
		{
			title = title.substring(0, 31);
			title = title + "\u2026";
		}

		Inventory inventory = Bukkit.createInventory(player, size, title);
		stock(inventory);

		player.openInventory(inventory);
	}

	public final Player getPlayer()
	{
		return player;
	}

	// ---- Required Methods

	public abstract int getSize();

	public abstract String getTitle();

	public abstract void stock(Inventory inventory);

	// ---- Messaging

	protected final void err(String msg, Object... args)
	{
		sendMessage("&cError: &4" + FormatUtil.format(msg, args));
	}

	protected final void sendpMessage(String message, Object... objects)
	{
		sendMessage(plugin.getPrefix() + message, objects);
	}

	protected final void sendMessage(String message, Object... objects)
	{
		player.sendMessage(ChatColor.YELLOW + FormatUtil.format(message, objects));
	}

	// ---- Events

	/**
	 * Called when an InventoryClickEvent is called.
	 * @param event The event
	 */
	public void onInventoryClick(InventoryClickEvent event) { }

	/**
	 * Called when an InventoryCloseEvent is called.
	 * @param event The event
	 */
	public void onInventoryClose(InventoryCloseEvent event) { }
}
