/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.gui;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import net.dmulloy2.SwornPlugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * @author dmulloy2
 */

public class GUIHandler implements Listener
{
	private static Map<String, AbstractGUI> openGUI;
	private static final @Getter GUIHandler instance = new GUIHandler();

	private GUIHandler()
	{
		openGUI = new HashMap<>();
	}

	public static void registerEvents(SwornPlugin plugin)
	{
		plugin.getServer().getPluginManager().registerEvents(instance, plugin);
	}

	public static void openGUI(Player player, AbstractGUI gui)
	{
		openGUI.put(player.getName(), gui);
	}

	public static boolean isBrowsingGUI(Player player)
	{
		return openGUI.containsKey(player.getName());
	}

	public static void removeBrowsing(Player player)
	{
		openGUI.remove(player.getName());
	}

	public static AbstractGUI getGUI(Player player)
	{
		return openGUI.get(player.getName());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event)
	{
		if (event.getInventory().getHolder() instanceof Player)
		{
			Player player = (Player) event.getInventory().getHolder();
			if (GUIHandler.isBrowsingGUI(player))
			{
				AbstractGUI gui = GUIHandler.getGUI(player);
				gui.onInventoryClick(event);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClose(InventoryCloseEvent event)
	{
		if (event.getInventory().getHolder() instanceof Player)
		{
			Player player = (Player) event.getInventory().getHolder();
			if (GUIHandler.isBrowsingGUI(player))
			{
				AbstractGUI gui = GUIHandler.getGUI(player);
				gui.onInventoryClose(event);

				removeBrowsing(player);
			}
		}
	}
}