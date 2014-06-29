/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.gui;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.util.FormatUtil;

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
		this.player = player;
		this.plugin = plugin;
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
	
	public void onInventoryClick(InventoryClickEvent event) { }

	public void onInventoryClose(InventoryCloseEvent event) { }
}