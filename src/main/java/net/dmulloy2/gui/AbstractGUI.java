/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.gui;

import net.dmulloy2.util.FormatUtil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

/**
 * @author dmulloy2
 */

public abstract class AbstractGUI
{
	private final Player player;
	public AbstractGUI(Player player)
	{
		this.player = player;
		this.setup();
	}

	private final void setup()
	{
		Inventory inventory = Bukkit.createInventory(player, getSize(), FormatUtil.format(getTitle()));
		stock(inventory);

		player.openInventory(inventory);
	}
	
	// ---- Required Methods
	
	public abstract int getSize();

	public abstract String getTitle();

	public abstract void stock(Inventory inventory);

	// ---- Events
	
	public void onInventoryClick(InventoryClickEvent event) { }

	public void onInventoryClose(InventoryCloseEvent event) { }
}