/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import net.dmulloy2.SwornPlugin;

import org.bukkit.entity.HumanEntity;
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
	private static final @Getter GUIHandler instance = new GUIHandler();
	private static Map<UUID, AbstractGUI> openGUI = new HashMap<>();

	/**
	 * Registers GUI events for a given SwornPlugin
	 *
	 * @param plugin Plugin to register events for
	 */
	public static void registerEvents(@NonNull SwornPlugin plugin)
	{
		plugin.getServer().getPluginManager().registerEvents(instance, plugin);
	}

	/**
	 * Opens a given GUI for a given {@link Player}
	 *
	 * @param player Player to open GUI for
	 * @param gui GUI to open
	 */
	public static void openGUI(@NonNull Player player, @NonNull AbstractGUI gui)
	{
		openGUI.put(player.getUniqueId(), gui);
	}

	/**
	 * Whether or not a given {@link Player} is viewing a GUI
	 *
	 * @param player Player in question
	 * @return Whether or not they are viewing a GUI
	 */
	public static boolean isBrowsingGUI(@NonNull Player player)
	{
		return openGUI.containsKey(player.getUniqueId());
	}

	/**
	 * Removes a browsing {@link Player}
	 *
	 * @param player Player to remove
	 */
	public static void removeBrowsing(@NonNull Player player)
	{
		openGUI.remove(player.getUniqueId());
	}

	/**
	 * Gets the GUI a {@link Player} is viewing.
	 *
	 * @param player Player viewing a GUI
	 * @return The GUI, or null if none is found
	 */
	public static AbstractGUI getGUI(@NonNull Player player)
	{
		return openGUI.get(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event)
	{
		HumanEntity clicker = event.getWhoClicked();
		if (clicker instanceof Player)
		{
			Player player = (Player) clicker;
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
		HumanEntity closer = event.getPlayer();
		if (closer instanceof Player)
		{
			Player player = (Player) closer;
			if (GUIHandler.isBrowsingGUI(player))
			{
				AbstractGUI gui = GUIHandler.getGUI(player);
				gui.onInventoryClose(event);
				removeBrowsing(player);
			}
		}
	}
}