/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.types.IPermission;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public abstract class Command implements CommandExecutor
{
	protected SwornPlugin plugin;

	protected CommandSender sender;
	protected Player player;
	protected String args[];

	protected String name;
	protected String description;

	protected IPermission permission;

	protected boolean mustBePlayer;
	protected List<String> requiredArgs;
	protected List<String> optionalArgs;
	protected List<String> aliases;

	protected boolean usesPrefix;

	public Command(SwornPlugin plugin)
	{
		this.plugin = plugin;
		this.requiredArgs = new ArrayList<String>(2);
		this.optionalArgs = new ArrayList<String>(2);
		this.aliases = new ArrayList<String>(2);
	}

	// ---- Execution

	@Override
	public final boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args)
	{
		execute(sender, args);
		return true;
	}

	public final void execute(CommandSender sender, String[] args)
	{
		this.sender = sender;
		this.args = args;
		if (sender instanceof Player)
			player = (Player) sender;

		if (mustBePlayer && ! isPlayer())
		{
			err("You must be a player to perform this command!");
			return;
		}

		if (requiredArgs.size() > args.length)
		{
			invalidArgs();
			return;
		}

		if (! hasPermission())
		{
			err("You must have the permission \"&c{0}&4\" to perform this command!", getPermissionString());
			return;
		}

		try
		{
			perform();
		}
		catch (Throwable e)
		{
			err("Encountered an exception executing this command: &c{0}&4: &c{1}", e.getClass().getName(), e.getMessage());
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(e, "executing command " + name));
		}

		// Clear variables
		this.sender = null;
		this.args = null;
		this.player = null;
	}

	public abstract void perform();

	protected final boolean isPlayer()
	{
		return player != null;
	}

	// ---- Permissions

	protected final boolean hasPermission(IPermission permission)
	{
		return plugin.getPermissionHandler().hasPermission(sender, permission);
	}

	private final boolean hasPermission()
	{
		return hasPermission(permission);
	}

	protected final String getPermissionString(IPermission permission)
	{
		return plugin.getPermissionHandler().getPermissionString(permission);
	}

	private final String getPermissionString()
	{
		return getPermissionString(permission);
	}

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
		sender.sendMessage(FormatUtil.format("&e" + message, objects));
	}

	protected final void sendMessage(Player player, String message, Object... objects)
	{
		player.sendMessage(FormatUtil.format(message, objects));
	}

	protected final void sendpMessage(Player player, String message, Object... objects)
	{
		sendMessage(player, plugin.getPrefix() + message, objects);
	}

	// ---- Help

	public final String getName()
	{
		return name;
	}

	public final List<String> getAliases()
	{
		return aliases;
	}

	public final String getUsageTemplate(final boolean displayHelp)
	{
		StringBuilder ret = new StringBuilder();
		ret.append("&b/");

		if (plugin.getCommandHandler().usesCommandPrefix() && usesPrefix)
			ret.append(plugin.getCommandHandler().getCommandPrefix() + " ");

		ret.append(name);

		for (String s : optionalArgs)
			ret.append(String.format(" &3[%s]", s));

		for (String s : requiredArgs)
			ret.append(String.format(" &3<%s>", s));

		if (displayHelp)
			ret.append(" &e" + description);

		return FormatUtil.format(ret.toString());
	}

	// ---- Argument Manipulation

	protected final boolean argMatchesAlias(String arg, String... aliases)
	{
		for (String s : aliases)
		{
			if (arg.equalsIgnoreCase(s))
				return true;
		}

		return false;
	}

	protected int argAsInt(int arg, boolean msg)
	{
		try
		{
			return Integer.valueOf(args[arg]);
		}
		catch (NumberFormatException ex)
		{
			if (msg)
				err("{0} is not a number.", args[arg]);
			return -1;
		}
	}

	protected final void invalidArgs()
	{
		err("Invalid arguments! Try: {0}", getUsageTemplate(false));
	}

	protected final String getName(CommandSender sender)
	{
		if (sender instanceof BlockCommandSender)
		{
			BlockCommandSender commandBlock = (BlockCommandSender) sender;
			Location location = commandBlock.getBlock().getLocation();
			return FormatUtil.format("CommandBlock ({0}, {1}, {2})", location.getBlockX(), location.getBlockY(), location.getBlockZ());
		}
		else if (sender instanceof ConsoleCommandSender)
		{
			return "Console";
		}
		else
		{
			return sender.getName();
		}
	}
}