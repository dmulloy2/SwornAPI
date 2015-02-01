/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.gui;

import java.util.HashMap;
import java.util.Map;

import net.dmulloy2.SwornPlugin;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author dmulloy2
 */

public class GUIHandler implements Listener
{
	private final Map<String, AbstractGUI> open;

	public GUIHandler(SwornPlugin plugin)
	{
		this.open = new HashMap<>();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * Opens a given GUI for a given {@link Player}.
	 *
	 * @param player Player to open GUI for
	 * @param gui GUI to open
	 */
	public void open(Player player, AbstractGUI gui)
	{
		Validate.notNull(player, "player cannot be null!");
		Validate.notNull(gui, "gui cannot be null!");
		open.put(player.getName(), gui);
	}

	// ---- Listeners

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event)
	{
		HumanEntity clicker = event.getWhoClicked();
		if (clicker instanceof Player)
		{
			Player player = (Player) clicker;
			if (open.containsKey(player.getName()))
			{
				AbstractGUI gui = open.get(player.getName());
				gui.onInventoryClick(event);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClose(InventoryCloseEvent event)
	{
		HumanEntity closer = event.getPlayer();
		if (closer instanceof Player)
		{
			Player player = (Player) closer;
			if (open.containsKey(player.getName()))
			{
				AbstractGUI gui = open.get(player.getName());
				gui.onInventoryClose(event);
				open.remove(player.getName());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		if (open.containsKey(player.getName()))
		{
			player.closeInventory();
			open.remove(player.getName());
		}
	}
}