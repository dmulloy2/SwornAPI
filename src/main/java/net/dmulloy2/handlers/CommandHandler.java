/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.commands.CmdHelp;
import net.dmulloy2.commands.Command;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

/**
 * @author dmulloy2
 */

public class CommandHandler implements CommandExecutor
{
	private String commandPrefix;
	private List<Command> registeredPrefixedCommands;
	private List<Command> registeredCommands;

	private CmdHelp helpCommand;

	private final SwornPlugin plugin;
	public CommandHandler(SwornPlugin plugin)
	{
		this.plugin = plugin;
		this.helpCommand = new CmdHelp(plugin);
		this.registeredCommands = new ArrayList<Command>();
	}

	public void registerCommand(Command command)
	{
		PluginCommand pluginCommand = plugin.getCommand(command.getName());
		if (pluginCommand != null)
		{
			pluginCommand.setExecutor(command);
			registeredCommands.add(command);
		}
		else
		{
			plugin.getLogHandler().log(Level.WARNING, "Entry for command {0} is missing in plugin.yml", command.getName());
		}
	}

	public void registerPrefixedCommand(Command command)
	{
		if (commandPrefix != null)
			registeredPrefixedCommands.add(command);
	}

	public List<Command> getRegisteredCommands()
	{
		return registeredCommands;
	}

	public List<Command> getRegisteredPrefixedCommands()
	{
		return registeredPrefixedCommands;
	}

	public String getCommandPrefix()
	{
		return commandPrefix;
	}

	public void setCommandPrefix(String commandPrefix)
	{
		this.commandPrefix = commandPrefix;
		this.registeredPrefixedCommands = new ArrayList<Command>();

		plugin.getCommand(commandPrefix).setExecutor(this);
	}

	public boolean usesCommandPrefix()
	{
		return commandPrefix != null;
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args)
	{
		List<String> argsList = new ArrayList<String>();

		if (args.length > 0)
		{
			String commandName = args[0];
			for (int i = 1; i < args.length; i++)
				argsList.add(args[i]);

			for (Command command : registeredPrefixedCommands)
			{
				if (commandName.equalsIgnoreCase(command.getName()) || command.getAliases().contains(commandName.toLowerCase()))
				{
					command.execute(sender, argsList.toArray(new String[0]));
					return true;
				}
			}

			sender.sendMessage(FormatUtil.format("&cError: &4Unknown command \"&c{0}&4\". Try {1}", args[0],
					helpCommand.getUsageTemplate(false)));
		}
		else
		{
			helpCommand.execute(sender, args);
		}

		return true;
	}
}