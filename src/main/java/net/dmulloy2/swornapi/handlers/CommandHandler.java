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
package net.dmulloy2.swornapi.handlers;

import java.util.*;
import java.util.logging.Level;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import net.dmulloy2.swornapi.SwornPlugin;
import net.dmulloy2.swornapi.commands.Command;
import net.dmulloy2.swornapi.util.FormatUtil;
import net.dmulloy2.swornapi.util.Validate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Handles commands. This supports both prefixed and non-prefixed commands.
 *
 * @author dmulloy2
 */

public class CommandHandler implements CommandExecutor, BasicCommand
{
	private String commandPrefix;
	private List<Command> registeredPrefixedCommands;
	private final List<Command> registeredCommands;

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

		if (plugin.isPaperPlugin())
		{
			plugin.registerCommand(command.getName(), command.getDescription(), command.getAliases(), command);
		}
		else
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

	@Override
	public Collection<String> suggest(CommandSourceStack sourceStack, String[] args)
	{
		if (args.length == 1)
		{
			List<String> suggestions = new ArrayList<>();
			String partial = args[0].toLowerCase();

			for (Command command : registeredPrefixedCommands)
			{
				if (command.getName().toLowerCase().startsWith(partial) || command.getAliases().stream().anyMatch(alias -> alias.toLowerCase().startsWith(partial)))
				{
					suggestions.add(command.getName());
				}
			}

			for (Command command : registeredCommands)
			{
				if (command.getName().toLowerCase().startsWith(partial) || command.getAliases().stream().anyMatch(alias -> alias.toLowerCase().startsWith(partial)))
				{
					suggestions.add(command.getName());
				}
			}

			return suggestions;
		}

		if (args.length > 1)
		{
			String name = args[0];
			Command command = getCommand(name);
			if (command != null)
			{
				return command.suggest(sourceStack, Arrays.copyOfRange(args, 1, args.length));
			}
		}

		return Collections.emptyList();
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
	 * @param prefix Command prefix
	 */
	public void setCommandPrefix(String prefix, String... aliases)
	{
		Validate.notEmpty(prefix, "prefix cannot be null or empty!");
		this.commandPrefix = prefix;
		this.registeredPrefixedCommands = new ArrayList<>();

		if (plugin.isPaperPlugin())
		{
			plugin.registerCommand(prefix, List.of(aliases), this);
		}
		else
		{
			plugin.getCommand(prefix).setExecutor(this);
		}
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
	public void execute(CommandSourceStack sourceStack, String[] args)
	{
		execute(sourceStack.getSender(), args);
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args)
	{
		execute(sender, args);
		return true;
	}

	private void execute(CommandSender sender, String[] args)
	{
		if (args.length > 0)
		{
			String name = args[0];
			String[] originalArgs = args;
			args = Arrays.copyOfRange(args, 1, args.length);

			Command command = getCommand(name);
			if (command != null)
			{
				command.execute(sender, args);
				return;
			}

			Command def = plugin.getDefaultCommand();
			if (def != null)
			{
				def.execute(sender, originalArgs);
				return;
			}

			if (sender instanceof Player player)
			{
				List<Component> templates = getHelpCommand().getFancyUsageTemplate();

				Component component = Component.text("Error: ", NamedTextColor.RED)
						.append(Component.text("Unknown command \"", NamedTextColor.DARK_RED))
						.append(Component.text(name, NamedTextColor.RED))
						.append(Component.text("\". Try ", NamedTextColor.DARK_RED))
						.append(templates.getFirst());

				player.sendMessage(component);
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
	}

	public final Command getHelpCommand()
	{
		if (plugin.getHelpCommand() != null)
			return plugin.getHelpCommand();

		return getCommand("help");
	}
}