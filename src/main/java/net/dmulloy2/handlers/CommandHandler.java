/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.chat.BaseComponent;
import net.dmulloy2.chat.ComponentBuilder;
import net.dmulloy2.chat.TextComponent;
import net.dmulloy2.commands.CmdHelp;
import net.dmulloy2.commands.Command;
import net.dmulloy2.util.ChatUtil;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

/**
 * Handles commands. This supports both prefixed and non-prefixed commands.
 * 
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

	/**
	 * Registers a non-prefixed {@link Command}.
	 * 
	 * @param command Non-prefixed {@link Command} to register.
	 */
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

	/**
	 * Registers a prefixed {@link Command}. The commandPrefix must be set for
	 * this method to work.
	 * 
	 * @param command Prefixed {@link Command} to register.
	 */
	public void registerPrefixedCommand(Command command)
	{
		if (commandPrefix != null)
			registeredPrefixedCommands.add(command);
	}

	/**
	 * @return A {@link List} of all registered non-prefixed commands.
	 */
	public List<Command> getRegisteredCommands()
	{
		return registeredCommands;
	}

	/**
	 * @return A {@link List} of all registered prefixed commands.
	 */
	public List<Command> getRegisteredPrefixedCommands()
	{
		return registeredPrefixedCommands;
	}

	/**
	 * @return The command prefix.
	 */
	public String getCommandPrefix()
	{
		return commandPrefix;
	}

	/**
	 * Sets the command prefix. This method must be called before any prefixed
	 * commands are registered.
	 * 
	 * @param commandPrefix Command prefix
	 */
	public void setCommandPrefix(String commandPrefix)
	{
		this.commandPrefix = commandPrefix;
		this.registeredPrefixedCommands = new ArrayList<Command>();

		plugin.getCommand(commandPrefix).setExecutor(this);
	}

	/**
	 * @return whether or not the command prefix is used.
	 */
	public boolean usesCommandPrefix()
	{
		return commandPrefix != null;
	}

	/**
	 * {@inheritDoc}
	 */
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

			BaseComponent[] components = new ComponentBuilder(FormatUtil.format("&cError: &4Unknown command \"&c{0}&4\". Try "))
				.addAll(helpCommand.getFancyUsageTemplate())
				.create();

			sendFancyMessage(sender, components);
		}
		else
		{
			helpCommand.execute(sender, args);
		}

		return true;
	}

	private void sendFancyMessage(CommandSender sender, BaseComponent[] components)
	{
		if (sender instanceof Player)
		{
			try
			{
				ChatUtil.sendMessage((Player) sender, components);
				return;
			} catch (Throwable ex) { }
		}

		sender.sendMessage(TextComponent.toLegacyText(components));
	}
}