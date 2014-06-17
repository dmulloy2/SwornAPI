/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.handlers;

import net.dmulloy2.types.IPermission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Handles permissions.
 * 
 * @author dmulloy2
 */

public class PermissionHandler
{
	private final String prefix;
	public PermissionHandler(String prefix)
	{
		this.prefix = prefix.toLowerCase() + ".";
	}

	public PermissionHandler(JavaPlugin plugin)
	{
		this(plugin.getName());
	}

	/**
	 * Returns whether or not a {@link CommandSender} has a permission.
	 * 
	 * @param sender Sender to check.
	 * @param permission Permission.
	 * @return Whether or not they have the permission.
	 */
	public final boolean hasPermission(CommandSender sender, IPermission permission)
	{
		return permission == null || hasPermission(sender, getPermissionString(permission));
	}

	/**
	 * Returns whether or not a {@link CommandSender} has a permission.
	 * 
	 * @param sender Sender to check.
	 * @param permission Permission.
	 * @return Whether or not they have the permission.
	 */
	public final boolean hasPermission(CommandSender sender, String permission)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			return player.hasPermission(permission) || player.isOp();
		}

		return true;
	}

	/**
	 * Gets the complete permission string for a given {@link IPermission}.
	 * 
	 * @param permission - Permission to get the node for.
	 * @return The complete permission string.
	 */
	public final String getPermissionString(IPermission permission)
	{
		return prefix + permission.getNode().toLowerCase();
	}
}