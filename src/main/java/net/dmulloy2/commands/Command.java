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
package net.dmulloy2.commands;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.annotation.Nullable;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.exception.CommandException;
import net.dmulloy2.exception.CommandException.Reason;
import net.dmulloy2.types.CommandVisibility;
import net.dmulloy2.types.IPermission;
import net.dmulloy2.types.StringJoiner;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.ListUtil;
import net.dmulloy2.util.Util;

/**
 * Represents a command. This class provides useful methods for execution,
 * permission and argument manipulation, and messaging.
 *
 * @author dmulloy2
 */

public abstract class Command implements CommandExecutor
{
	protected final SwornPlugin plugin;

	protected CommandSender sender;
	protected Player player;
	protected String[] args;

	protected String name;
	protected String description;

	protected IPermission permission;
	protected CommandVisibility visibility = CommandVisibility.PERMISSION;

	private final List<Command> subCommands;
	protected Command parent;

	protected List<Syntax> syntaxes;
	protected List<String> aliases;

	protected Syntax syntax;

	protected boolean mustBePlayer;
	protected boolean usesPrefix;

	public Command(SwornPlugin plugin)
	{
		this.plugin = plugin;
		this.aliases = new ArrayList<>();
		this.subCommands = new ArrayList<>();
		this.syntaxes = new ArrayList<>();
		syntaxes.add(new Syntax());
	}

	// ---- Execution

	@Override
	public final boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args)
	{
		execute(sender, args);
		return true;
	}

	/**
	 * Executes this command with a given sender and arguments. This method
	 * performs all of the permission and argument length checks before passing
	 * the call to {@link #perform()}.
	 * 
	 * @param sender Sender of this command
	 * @param args Arguments
	 */
	public final void execute(CommandSender sender, String[] args)
	{
		if (! subCommands.isEmpty() && args.length != 0)
		{
			for (Command subCommand : subCommands)
			{
				if (subCommand.argMatchesIdentifier(args[0]))
				{
					args = Arrays.copyOfRange(args, 1, args.length);
					subCommand.execute(sender, args);
					return;
				}
			}
		}

		this.sender = sender;
		this.args = args;
		if (sender instanceof Player)
			player = (Player) sender;

		if (mustBePlayer && ! isPlayer())
		{
			err("You must be a player to perform this command!");
			return;
		}

		syntax:
		{
			for (Syntax syntax : syntaxes)
			{
				if (syntax.requiredSize() <= args.length)
				{
					this.syntax = syntax;
					break syntax;
				}
			}

			invalidSyntax(args);
			return;
		}

		if (! isVisibleTo(sender))
		{
			if (visibility == CommandVisibility.PERMISSION)
				hasPermission(permission, true);
			else
				err("You cannot use this command!");
			return;
		}

		try
		{
			prePerform();
			perform();
		}
		catch (CommandException ex)
		{
			switch (ex.getReason())
			{
				case BREAK:
					break;
				case INPUT:
				case VALIDATE:
					err(ex.getMessage());
					break;
				case SYNTAX:
					invalidSyntax(args);
					break;
			}
		}
		catch (Throwable ex)
		{
			String stack = Util.getUsefulStack(ex, "executing command " + name);
			plugin.getLogHandler().log(Level.WARNING, stack);

			String error = FormatUtil.format("&cError: &4Encountered an exception executing this command: ");

			ComponentBuilder builder = new ComponentBuilder(error);
			BaseComponent[] hoverStr = TextComponent.fromLegacyText(stack.replace("\t", "    "));
			builder.append(FormatUtil.format("&c{0}", ex.toString())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverStr));
			sendMessage(builder.create());
		}
	}

	/**
	 * Executed right before perform. Useful if plugins are making the same check for each execution.
	 */
	public void prePerform() { }

	/**
	 * Performs this command after permission and argument length checks.
	 */
	public abstract void perform();

	/**
	 * Whether or not the sender of this command is a {@link Player}.
	 * 
	 * @return True if they are, false if not.
	 */
	protected final boolean isPlayer()
	{
		return sender instanceof Player;
	}

	// ---- Permission Management

	/**
	 * Whether or not a given command sender has a given permission.
	 * 
	 * @param sender Sender to check
	 * @param permission Permission to check for
	 * @param message Whether or not to send an error
	 * @return True if they have it, false if not
	 */
	protected final boolean hasPermission(CommandSender sender, IPermission permission, boolean message)
	{
		Validate.notNull(sender, "sender cannot be null!");

		if (! plugin.getPermissionHandler().hasPermission(sender, permission))
		{
			if (message)
			{
				StringJoiner hoverTextBuilder = new StringJoiner("\n");
				hoverTextBuilder.append(FormatUtil.format("&4Permission:"));
				hoverTextBuilder.append(FormatUtil.format("&r{0}", getPermissionString(permission)));
				BaseComponent[] hoverText = TextComponent.fromLegacyText(hoverTextBuilder.toString());

				ComponentBuilder builder = new ComponentBuilder(FormatUtil.format("&cError: &4You do not have "));
				builder.append(FormatUtil.format("&cpermission")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
				builder.append(FormatUtil.format(" &4to perform this command!"));
				sendMessage(sender, builder.create());
			}

			return false;
		}

		return true;
	}

	/**
	 * Whether or not a given command sender has a given permission.
	 * 
	 * @param sender Sender to check
	 * @param permission Permission to check for
	 * @return True if they have it, false if not
	 */
	protected final boolean hasPermission(CommandSender sender, IPermission permission)
	{
		return hasPermission(sender, permission, false);
	}

	/**
	 * Whether or not the sender of this command has a given permission.
	 * 
	 * @param permission Permission to check for
	 * @param message Whether or not to send an error
	 * @return True if they have it, false if not.
	 */
	protected final boolean hasPermission(IPermission permission, boolean message)
	{
		return hasPermission(sender, permission, message);
	}

	/**
	 * Whether or not the sender of this command has a given permission.
	 * 
	 * @param permission Permission to check for
	 * @return True if they have it, false if not.
	 */
	protected final boolean hasPermission(IPermission permission)
	{
		return hasPermission(sender, permission, false);
	}

	/**
	 * Gets the full permission string of a given permission.
	 * 
	 * @param permission Permission
	 * @return The full string
	 */
	protected final String getPermissionString(IPermission permission)
	{
		Validate.notNull(permission, "permission cannot be null!");

		return plugin.getPermissionHandler().getPermissionString(permission);
	}

	/**
	 * Gets the full permission string of this command's permission.
	 * 
	 * @return The full string
	 */
	public final String getPermissionString()
	{
		return getPermissionString(permission);
	}

	/**
	 * Whether or not this command is visible to a given command sender. The
	 * output depends on {@link #visibility}
	 * 
	 * @param sender Sender to check
	 * @return True if it is, false if not
	 */
	public final boolean isVisibleTo(CommandSender sender)
	{
		Validate.notNull(sender, "sender cannot be null!");

		return switch (visibility) {
			case ALL -> true;
			case PERMISSION -> hasPermission(sender, permission, false);
			case OPS -> sender.isOp();
			case NONE -> false;
			default -> throw new IllegalStateException("Unsupported command visibility: " + visibility);
		};
	}

	// ---- Messaging

	protected final CommandProps props()
	{
		return plugin.props();
	}

	protected final String format(String message, Object... args)
	{
		return props().format(message, args);
	}

	/**
	 * Sends an error message to the command sender.
	 * 
	 * @param message Message to send
	 * @param args Objects to format in
	 */
	protected final void err(String message, Object... args)
	{
		Validate.notNull(message, "message cannot be null!");
		sender.sendMessage(format(props().getErrorPrefix() + message, args));
	}

	/**
	 * Sends a prefixed message to the command sender.
	 * 
	 * @param message Message to send
	 * @param args Objects to format in
	 */
	protected final void sendpMessage(String message, Object... args)
	{
		Validate.notNull(message, "message cannot be null!");
		sender.sendMessage(format(plugin.getPrefix() + message, args));
	}

	/**
	 * Sends a message to the command sender.
	 * 
	 * @param message Message to send
	 * @param args Objects to format in
	 */
	protected final void sendMessage(String message, Object... args)
	{
		Validate.notNull(message, "message cannot be null");
		sender.sendMessage(format(props().getBaseColor() + message, args));
	}

	/**
	 * Sends an error message to a given command sender.
	 * 
	 * @param sender Sender to send the message to
	 * @param message Message to send
	 * @param args Objects to format in
	 */
	protected final void err(CommandSender sender, String message, Object... args)
	{
		Validate.notNull(sender, "sender cannot be null!");
		Validate.notNull(message, "message cannot be null!");

		sender.sendMessage(format(props().getErrorPrefix() + message, args));
	}

	/**
	 * Sends a prefixed message to a given command sender.
	 * 
	 * @param sender Sender to send the message to
	 * @param message Message to send
	 * @param args Objects to format in
	 */
	protected final void sendpMessage(CommandSender sender, String message, Object... args)
	{
		Validate.notNull(sender, "sender cannot be null!");
		Validate.notNull(message, "message cannot be null!");

		sender.sendMessage(format(plugin.getPrefix() + message, args));
	}

	/**
	 * Sends a message to a given command sender.
	 * 
	 * @param sender Sender to send the message to
	 * @param message Message to send
	 * @param args Objects to format in
	 */
	protected final void sendMessage(CommandSender sender, String message, Object... args)
	{
		Validate.notNull(sender, "sender cannot be null!");
		Validate.notNull(message, "message cannot be null!");

		sender.sendMessage(format(props().getBaseColor() + message, args));
	}

	// ---- Fancy Messaging

	/**
	 * Sends a JSON message to the command sender.
	 * @param components JSON message to send
	 * @deprecated Use Spigot API
	 */
	@Deprecated
	protected final void sendMessage(net.dmulloy2.chat.BaseComponent... components)
	{
		sendMessage(sender, components);
	}

	/**
	 * Sends a JSON message to a given command sender.
	 * @param sender Sender to send the message to
	 * @param components JSON message to send
	 * @deprecated Use Spigot API
	 */
	@Deprecated
	protected final void sendMessage(CommandSender sender, net.dmulloy2.chat.BaseComponent... components)
	{
		net.dmulloy2.chat.ChatUtil.sendMessage(sender, components);
	}

	protected final void sendMessage(CommandSender sender, ChatMessageType position, BaseComponent... components)
	{
		if (sender instanceof Player player)
		{
			player.spigot().sendMessage(position, components);
		}
		else
		{
			sender.sendMessage(TextComponent.toLegacyText(components));
		}
	}

	protected final void sendMessage(ChatMessageType position, BaseComponent... components)
	{
		sendMessage(sender, position, components);
	}

	protected final void sendMessage(CommandSender sender, BaseComponent... components)
	{
		sendMessage(sender, ChatMessageType.SYSTEM, components);
	}

	protected final void sendMessage(BaseComponent... components)
	{
		sendMessage(sender, components);
	}

	// ---- Help

	/**
	 * Gets the name of this command
	 * 
	 * @return The name of this command
	 */
	public final String getName()
	{
		return name;
	}

	/**
	 * Gets a list of aliases for this command
	 * 
	 * @return The list of aliases
	 */
	public final List<String> getAliases()
	{
		return aliases;
	}

	/**
	 * Gets a basic usage template for this command
	 * 
	 * @param displayHelp Whether or not to display the discription
	 * @return The usage template
	 */
	public List<String> getUsageTemplate(boolean displayHelp)
	{
		List<String> ret = new ArrayList<>();

		for (int i = 0; i < syntaxes.size(); i++)
		{
			Syntax syntax = syntaxes.get(i);
			StringBuilder line = new StringBuilder();
			line.append("{a}/");

			if (plugin.getCommandHandler().usesCommandPrefix() && usesPrefix)
				line.append(plugin.getCommandHandler().getCommandPrefix()).append(" ");

			if (parent != null)
				line.append(parent.getName()).append(" ");

			line.append(name);

			for (Argument arg : syntax)
			{
				if (arg.required())
					line.append(String.format(" {h}<%s>", arg.argument()));
				else
					line.append(String.format(" {h}[%s]", arg.argument()));
			}

			if (displayHelp && i == 0)
				line.append(" {b}").append(description);

			ret.add(format(line.toString()));
		}

		return ret;
	}

	/**
	 * Gets a fancy usage template for this command
	 * 
	 * @return The usage template
	 */
	public List<BaseComponent[]> getFancyUsageTemplate()
	{
		return getFancyUsageTemplate(false);
	}

	/**
	 * Gets a fancy usage template for this command
	 * 
	 * @param list Whether or not it is part of a list
	 * @return The usage template
	 */
	public List<BaseComponent[]> getFancyUsageTemplate(boolean list)
	{
		List<BaseComponent[]> ret = new ArrayList<>();

		for (int i = 0; i < syntaxes.size(); i++)
		{
			Syntax syntax = syntaxes.get(i);
			StringBuilder templateBuilder = new StringBuilder();
			templateBuilder.append("{a}/");

			if (plugin.getCommandHandler().usesCommandPrefix() && usesPrefix)
				templateBuilder.append(plugin.getCommandHandler().getCommandPrefix()).append(" ");

			if (parent != null)
				templateBuilder.append(parent.getName()).append(" ");

			templateBuilder.append(name);

			for (Argument arg : syntax)
			{
				if (arg.required())
					templateBuilder.append(String.format(" {h}<%s>", arg.argument()));
				else
					templateBuilder.append(String.format(" {h}[%s]", arg.argument()));
			}

			String template = format(templateBuilder.toString());
			String prefix = list ? i == 0 ? "- " : "  " : "";
			ComponentBuilder builder = new ComponentBuilder(format("{a}" + prefix + template));

			StringBuilder hoverTextBuilder = new StringBuilder();
			hoverTextBuilder.append(template).append(":\n");

			for (int a = 0; a < syntax.size(); a++)
			{
				Argument arg = syntax.get(a);
				String explanation = arg.explanation();
				if (explanation != null)
				{
					String argument = arg.argument();
					if (arg.required())
						hoverTextBuilder.append(format("{h}  <{0}>: {b}{1}\n", argument, explanation));
					else
						hoverTextBuilder.append(format("{h}  [{0}]: {b}{1}\n", argument, explanation));
				}

				if (a != 0 && a == syntax.size() - 1)
					hoverTextBuilder.append("\n");
			}

			StringJoiner description = new StringJoiner("\n");
			for (String s : getDescription())
				description.append("{b}" + s);
			hoverTextBuilder.append(format(capitalizeFirst(description.toString())));

			if (permission != null)
			{
				hoverTextBuilder.append("\n\n");
				hoverTextBuilder.append(ChatColor.DARK_RED).append("Permission:");
				hoverTextBuilder.append("\n").append(getPermissionString());
			}

			Text hoverText = new Text(hoverTextBuilder.toString());

			HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText);
			builder.event(hoverEvent);

			ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ChatColor.stripColor(template));
			builder.event(clickEvent);

			ret.add(builder.create());
		}

		return ret;
	}

	private List<String> descriptionList;

	/**
	 * Gets the description for this command
	 * @return The description
	 */
	public List<String> getDescription()
	{
		if (descriptionList == null)
			descriptionList = ListUtil.toList(description);
		return descriptionList;
	}

	// ---- Sub Commands

	/**
	 * Adds a sub-command to this command
	 * 
	 * @param command Command to add
	 */
	protected final void addSubCommand(Command command)
	{
		command.parent = this;
		subCommands.add(command);
	}

	protected boolean argMatchesIdentifier(String arg)
	{
		if (arg.equalsIgnoreCase(name))
			return true;

		for (String alias : aliases)
		{
			if (arg.equalsIgnoreCase(alias))
				return true;
		}

		return false;
	}

	/**
	 * Gets this command's parent. Will be null if this command is independent.
	 * 
	 * @return The parent, or null if none
	 */
	protected final Command getParentCommand()
	{
		return parent;
	}

	/**
	 * Whether or not this command has sub-commands
	 * 
	 * @return True if it does, false if not
	 */
	protected final boolean hasSubCommands()
	{
		return ! subCommands.isEmpty();
	}

	/**
	 * Gets a list of sub-commands to this command.
	 * 
	 * @return The list
	 */
	protected final List<Command> getSubCommands()
	{
		return subCommands;
	}

	/**
	 * Gets help for this command's sub-commands.
	 * 
	 * @param displayHelp Whether or not to display the description
	 * @return A list of usage templates
	 */
	protected final List<String> getSubCommandHelp(boolean displayHelp)
	{
		List<String> ret = new ArrayList<>();

		for (Command cmd : getSubCommands())
		{
			ret.addAll(cmd.getUsageTemplate(displayHelp));
		}

		return ret;
	}

	/**
	 * Gets fancy help for this command's sub-commands.
	 * 
	 * @return A list of fancy usage templates
	 */
	public final List<BaseComponent[]> getFancySubCommandHelp()
	{
		return getFancySubCommandHelp(false);
	}

	/**
	 * Gets fancy help for this command's sub-commands.
	 * 
	 * @param list Whether or not they're part of a list
	 * @return A list of fancy usage templates
	 */
	public final List<BaseComponent[]> getFancySubCommandHelp(boolean list)
	{
		List<BaseComponent[]> ret = new ArrayList<>();

		for (Command cmd : getSubCommands())
		{
			ret.addAll(cmd.getFancyUsageTemplate(list));
		}

		return ret;
	}

	// ---- Argument Manipulation

	/**
	 * Whether or not a given array contains a given argument
	 * 
	 * @param arg Argument to search for
	 * @param aliases Aliases to search
	 * @return True if it does, false if not
	 */
	protected final boolean argMatchesAlias(String arg, String... aliases)
	{
		for (String s : aliases)
		{
			if (arg.equalsIgnoreCase(s))
				return true;
		}

		return false;
	}

	/**
	 * Gets an argument as an integer
	 * 
	 * @param index Argument index
	 * @param msg Whether or not to show an error
	 * @return The integer, or -1 if parsing failed
	 */
	protected final int argAsInt(int index, boolean msg)
	{
		if (args.length <= index)
			throw new CommandException(Reason.SYNTAX);

		String arg = args[index];

		try
		{
			return Integer.parseInt(arg);	
		}
		catch (NumberFormatException ex)
		{
			if (msg) throw new CommandException(Reason.INPUT, "&c{0} &4is not a number.", arg);
			else return -1;
		}
	}

	/**
	 * Gets an argument as a double
	 * 
	 * @param index Argument index
	 * @param msg Whether or not to show an error
	 * @return The double, or -1.0D if parsing failed
	 */
	protected final double argAsDouble(int index, boolean msg)
	{
		if (args.length <= index)
			throw new CommandException(Reason.SYNTAX);
		
		String arg = args[index];

		try
		{
			return Double.parseDouble(arg);
		}
		catch (NumberFormatException ex)
		{
			if (msg) throw new CommandException(Reason.INPUT, "&c{0} &4is not a number.", arg);
			else return -1.0D;
		}
	}

	/**
	 * Gets an argument as a boolean
	 * 
	 * @param arg Argument index
	 * @return The boolean
	 */
	protected boolean argAsBoolean(int arg)
	{
		return argAsBoolean(arg, false);
	}

	/**
	 * Gets an argument as a boolean, falling back to the default if it's out of
	 * range
	 * 
	 * @param arg Argument index
	 * @param def Default value
	 * @return The boolean
	 */
	protected boolean argAsBoolean(int arg, boolean def)
	{
		return args.length > arg ? Util.toBoolean(args[arg]) : def;
	}

	protected Player getPlayer(int index)
	{
		return getPlayer(index, true);
	}

	protected Player getPlayer(int index, boolean message)
	{
		if (args.length <= index)
			throw new CommandException(Reason.SYNTAX);

		String arg = args[index];
		Player player = Util.matchPlayer(arg);
		return message ? checkNotNull(player, "Player \"&c{0}&4\" not found!", arg) : player;
	}

	protected <T> T checkNotNull(@Nullable T value, String message, Object... args)
	{
		if (value == null)
			throw new CommandException(Reason.VALIDATE, message, args);
		return value;
	}

	protected void checkArgument(boolean argument, String message, Object... args)
	{
		if (! argument)
			throw new CommandException(Reason.VALIDATE, message, args);
	}

	protected void checkPermission(CommandSender sender, IPermission permission)
	{
		if (! hasPermission(sender, permission, true))
			throw new CommandException(Reason.BREAK);
	}

	protected void stopExecution()
	{
		throw new CommandException(Reason.BREAK);
	}

	/**
	 * Combines the arguments from {@code start} to {@code args.length} with
	 * spaces
	 * 
	 * @param start Starting index
	 * @return The resulting string
	 */
	protected final String getFinalArg(int start)
	{
		StringBuilder ret = new StringBuilder();
		for (int i = start; i < args.length; i++)
		{
			if (i != start)
				ret.append(" ");

			ret.append(args[i]);
		}

		return ret.toString();
	}

	/**
	 * Capitalizes the first letter of a given string
	 * 
	 * @param string String to capitalize
	 * @return The string
	 */
	protected String capitalizeFirst(String string)
	{
		return FormatUtil.capitalizeFirst(string);
	}

	// ---- Utility

	/**
	 * Gets the name of a given command sender from the sender's perspective.
	 * This method supports all known command senders.
	 * 
	 * @param sender Sender of the command
	 * @param target Who the message is meant for
	 * @param subject Whether or not it's the subject of the sentence
	 * @return The name of the command sender
	 */
	protected final String getName(CommandSender sender, CommandSender target, boolean subject)
	{
		Validate.notNull(sender, "sender cannot be null!");

		if (sender.equals(target))
		{
			return subject ? "You" : "yourself";
		}

		if (sender instanceof Player)
		{
			return sender.getName();
		}
		else if (sender instanceof ConsoleCommandSender)
		{
			return "Console";
		}
		else if (sender instanceof BlockCommandSender commandBlock)
		{
			Location location = commandBlock.getBlock().getLocation();
			return FormatUtil.format("CommandBlock ({0}, {1}, {2})", location.getBlockX(), location.getBlockY(), location.getBlockZ());
		}
		else if (sender instanceof CommandMinecart minecart)
		{
			Location location = minecart.getLocation();
			return FormatUtil.format("Minecart ({0}, {1}, {2})", location.getBlockX(), location.getBlockY(), location.getBlockZ());
		}
		else if (sender instanceof Entity)
		{
			return FormatUtil.getFriendlyName(((Entity) sender).getType());
		}
		else
		{
			return sender.getName();
		}
	}

	/**
	 * Gets the name of a given command sender from the sender's perspective.
	 * This method supports all kinds of command senders.
	 * 
	 * @param target Sender to get the name of
	 * @return The name of the command sender
	 * @see #getName(CommandSender, CommandSender, boolean)
	 */
	protected final String getName(CommandSender target)
	{
		return getName(sender, target, false);
	}

	// ---- Syntax

	/**
	 * Displays the invalid syntax message
	 */
	protected final void invalidSyntax()
	{
		invalidSyntax(args);
	}

	/**
	 * Displays the invalid syntax message for a given array of arguments.
	 * 
	 * @param args Arguments
	 */
	protected final void invalidSyntax(String[] args)
	{
		Syntax closest = findClosest(args);
		String invalidSyntax = FormatUtil.format("&cError: &4Invalid syntax! Missing: &c");
		ComponentBuilder builder = new ComponentBuilder(invalidSyntax);

		List<Argument> missing = closest.missingSyntax(args.length);
		for (int i = 0; i < missing.size(); i++)
		{
			Argument arg = missing.get(i);
			String line = "&c" + arg.argument();
			if (i != 0)
				line = "&4, " + line;

			builder.append(FormatUtil.format(line));
			String explanation = arg.explanation();
			if (explanation != null)
			{
				Text hoverText = new Text(FormatUtil.format("&4{0}:\n&f{1}", arg.argument(), explanation));
				builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
			}
		}

		sendMessage(builder.create());
	}

	/**
	 * Finds the closest Syntax match for a given array of arguments.
	 * 
	 * @param args Arguments to find syntax for
	 * @return The syntax, defaulting to {@link #defaultSyntax()}
	 */
	private Syntax findClosest(String[] args)
	{
		if (syntaxes.size() == 1 || args.length == 0)
			return defaultSyntax();

		// Find the closest match
		Syntax closest = null;
		int delta = -1;

		for (Syntax syntax : syntaxes)
		{
			int curDelta = Math.abs(syntax.size() - args.length);
			if (curDelta < delta || delta == -1)
			{
				closest = syntax;
				delta = curDelta;
			}

			if (curDelta == 0)
				break;
		}

		return closest != null ? closest : defaultSyntax();
	}

	/**
	 * Gets the default Syntax.
	 * 
	 * @return The default syntax
	 */
	private Syntax defaultSyntax()
	{
		return syntaxes.get(0);
	}

	/**
	 * Adds a argument to the current syntax.
	 * 
	 * @param arg Argument name
	 * @param explanation Short description for the argument
	 * @param required Whether or not it is required
	 */
	protected final void addArgument(String arg, String explanation, boolean required)
	{
		Syntax syntax = syntaxes.get(syntaxes.size() - 1);
		syntax.add(new Argument(arg, explanation, required));
	}

	/**
	 * Adds a required argument to the current syntax.
	 * 
	 * @param arg Argument name
	 */
	protected final void addRequiredArg(String arg)
	{
		addArgument(arg, null, true);
	}

	/**
	 * Adds a required argument to the current syntax.
	 * 
	 * @param arg Argument name
	 * @param explanation Short description for the argument
	 */
	protected final void addRequiredArg(String arg, String explanation)
	{
		addArgument(arg, explanation, true);
	}

	/**
	 * Adds an optional argument to the current syntax.
	 * 
	 * @param arg Argument name
	 */
	protected final void addOptionalArg(String arg)
	{
		addArgument(arg, null, false);
	}

	/**
	 * Adds an optional argument to the current syntax.
	 * 
	 * @param arg Argument name
	 * @param explanation Short description for the argument
	 */
	protected final void addOptionalArg(String arg, String explanation)
	{
		addArgument(arg, explanation, false);
	}

	public record Argument(String argument, String explanation, boolean required) {
	}

	public static class Syntax extends ArrayList<Argument>
	{
		@Serial
		private static final long serialVersionUID = 1L;

		public final int requiredSize()
		{
			int required = 0;
			for (Argument arg : this)
			{
				if (arg.required())
					required++;
			}

			return required;
		}

		public final List<Argument> missingSyntax(int size)
		{
			List<Argument> ret = new ArrayList<>();

			int required = requiredSize();
			for (int i = size; i < required; i++)
				ret.add(get(i, true));

			return ret;
		}

		public final Argument get(int index, boolean required)
		{
			int i = 0;

			for (Argument arg : this)
			{
				if (arg.required() == required)
				{
					if (i == index)
						return arg;
					i++;
				}
			}

			return null;
		}
	}

	/**
	 * Utility class for easily creating multiple sets of Syntax.
	 * 
	 * @author dmulloy2
	 */
	public class SyntaxBuilder
	{
		private final List<Syntax> syntaxes;

		/**
		 * Creates a new SyntaxBuilder
		 */
		public SyntaxBuilder()
		{
			this.syntaxes = new ArrayList<>();
			syntaxes.add(new Syntax());
		}

		/**
		 * Switches to a new Syntax, saving the previous one
		 * 
		 * @return This, for chanining
		 */
		public SyntaxBuilder newSyntax()
		{
			syntaxes.add(new Syntax());
			return this;
		}

		/**
		 * Adds a required argument to the current Syntax
		 * 
		 * @param arg Argument name
		 * @return This, for chaining
		 */
		public SyntaxBuilder requiredArg(String arg)
		{
			add(arg, null, true);
			return this;
		}

		/**
		 * Adds a required argument to the current Syntax
		 * 
		 * @param arg Argument name
		 * @param explanation Short description for the argument
		 * @return This, for chaining
		 */
		public SyntaxBuilder requiredArg(String arg, String explanation)
		{
			add(arg, explanation, true);
			return this;
		}

		/**
		 * Adds an optional argument to the current Syntax
		 * 
		 * @param arg Argument name
		 * @return This, for chaining
		 */
		public SyntaxBuilder optionalArg(String arg)
		{
			add(arg, null, false);
			return this;
		}

		/**
		 * Adds an optional argument to the current Syntax
		 * 
		 * @param arg Argument name
		 * @param explanation Short description for the argument
		 * @return This, for chaining
		 */
		public SyntaxBuilder optionalArg(String arg, String explanation)
		{
			add(arg, explanation, false);
			return this;
		}

		/**
		 * Adds an argument to the current Syntax
		 * 
		 * @param arg Argument name
		 * @param explanation Short description for the argument
		 * @param required Whether or not the argument is required
		 * @return This, for chaining
		 */
		public SyntaxBuilder add(String arg, String explanation, boolean required)
		{
			Syntax current = syntaxes.get(syntaxes.size() - 1);
			current.add(new Argument(arg, explanation, required));
			return this;
		}

		/**
		 * Compiles the Syntaxes into a single list
		 * 
		 * @return The list
		 */
		public List<Syntax> build()
		{
			return syntaxes;
		}
	}
}