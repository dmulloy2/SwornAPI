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
package net.dmulloy2.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.chat.BaseComponent;
import net.dmulloy2.chat.ComponentBuilder;
import net.dmulloy2.commands.Command;
import net.dmulloy2.util.FormatUtil;

import org.apache.commons.lang.Validate;
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

	private final SwornPlugin plugin;
	public CommandHandler(SwornPlugin plugin)
	{
		this.plugin = plugin;
		this.registeredCommands = new ArrayList<>();
	}

	/**
	 * Registers a non-prefixed {@link Command}.
	 *
	 * @param command Non-prefixed {@link Command} to register.
	 */
	public void registerCommand(Command command)
	{
		Validate.notNull(command, "command cannot be null!");
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
		Validate.notNull(command, "command cannot be null!");
		if (commandPrefix != null)
			registeredPrefixedCommands.add(command);
	}

	/**
	 * Gets a {@link List} of all registered non-prefixed commands.
	 *
	 * @return The list
	 */
	public List<Command> getRegisteredCommands()
	{
		return registeredCommands;
	}

	/**
	 * Gets a {@link List} of all registered prefixed commands.
	 *
	 * @return The list
	 */
	public List<Command> getRegisteredPrefixedCommands()
	{
		return registeredPrefixedCommands;
	}

	/**
	 * Gets the command prefix.
	 *
	 * @return The prefix, or null if not used
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
		Validate.notEmpty(commandPrefix, "prefix cannot be null or empty!");
		this.commandPrefix = commandPrefix;
		this.registeredPrefixedCommands = new ArrayList<>();

		plugin.getCommand(commandPrefix).setExecutor(this);
	}

	/**
	 * Whether or not the command prefix is used.
	 *
	 * @return True if the command prefix is used, false if not
	 */
	public boolean usesCommandPrefix()
	{
		return commandPrefix != null;
	}

	/**
	 * Gets a {@link Command} by name.
	 *
	 * @param name Command name
	 * @return Command, or null if not found
	 */
	public final Command getCommand(String name)
	{
		Validate.notNull(name, "name cannot be null!");
		for (Command command : registeredPrefixedCommands)
		{
			if (name.equalsIgnoreCase(command.getName()) || command.getAliases().contains(name.toLowerCase()))
				return command;
		}

		for (Command command : registeredCommands)
		{
			if (name.equalsIgnoreCase(command.getName()) || command.getAliases().contains(name.toLowerCase()))
				return command;
		}

		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args)
	{
		if (args.length > 0)
		{
			String name = args[0];
			args = Arrays.copyOfRange(args, 1, args.length);

			Command command = getCommand(name);
			if (command != null)
			{
				command.execute(sender, args);
				return true;
			}

			if (sender instanceof Player)
			{
				List<BaseComponent[]> templates = getHelpCommand().getFancyUsageTemplate();
				String error = FormatUtil.format("&cError: &4Unknown command \"&c{0}&4\". Try ", name);
				new ComponentBuilder(error).addAll(templates.get(0)).send(sender);
			}
			else
			{
				List<String> templates = getHelpCommand().getUsageTemplate(false);
				sender.sendMessage(FormatUtil.format("&cError: &4Unknown command \"&c{0}&4\". Try {1}", name, templates.get(0)));
			}
		}
		else
		{
			getHelpCommand().execute(sender, args);
		}

		return true;
	}

	public final Command getHelpCommand()
	{
		if (plugin.getHelpCommand() != null)
			return plugin.getHelpCommand();

		return getCommand("help");
	}
}