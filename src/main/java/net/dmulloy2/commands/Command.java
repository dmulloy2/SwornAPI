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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dmulloy2.SwornPlugin;
import net.dmulloy2.chat.BaseComponent;
import net.dmulloy2.chat.ChatUtil;
import net.dmulloy2.chat.ClickEvent;
import net.dmulloy2.chat.ComponentBuilder;
import net.dmulloy2.chat.HoverEvent;
import net.dmulloy2.chat.HoverEvent.Action;
import net.dmulloy2.chat.TextComponent;
import net.dmulloy2.types.CommandVisibility;
import net.dmulloy2.types.IPermission;
import net.dmulloy2.types.StringJoiner;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.NumberUtil;
import net.dmulloy2.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Represents a commmand. This class provides useful methods for execution,
 * permission and argument manipulation, and messaging.
 *
 * @author dmulloy2
 */

public abstract class Command implements CommandExecutor
{
	protected final SwornPlugin plugin;

	protected CommandSender sender;
	protected Player player;
	protected String args[];

	protected String name;
	protected String description;

	protected IPermission permission;
	protected CommandVisibility visibility = CommandVisibility.PERMISSION;

	protected List<SubCommand> subCommands;
	protected Command parent;

	protected List<Syntax> syntaxes;
	protected List<String> aliases;

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

	public final void execute(CommandSender sender, String[] args)
	{
		if (! subCommands.isEmpty() && args.length != 0)
		{
			for (SubCommand subCommand : subCommands)
			{
				if (subCommand.argMatchesIdentifier(args[0]))
				{
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
					break syntax;
			}

			invalidSyntax(args);
			return;
		}

		if (! isVisibleTo(sender))
		{
			if (visibility == CommandVisibility.PERMISSION)
			{
				StringJoiner hoverText = new StringJoiner("\n");
				hoverText.append(FormatUtil.format("&4Permission:"));
				hoverText.append(FormatUtil.format("&r{0}", getPermissionString()));

				ComponentBuilder builder = new ComponentBuilder(FormatUtil.format("&cError: &4You do not have "));
				builder.append(FormatUtil.format("&cpermission")).event(new HoverEvent(Action.SHOW_TEXT, hoverText.toString()));
				builder.append(FormatUtil.format(" &4to perform this command!"));
				sendMessage(builder.create());
				return;
			}
			else
			{
				err("You cannot use this command!");
				return;
			}
		}

		try
		{
			perform();
		}
		catch (Throwable ex)
		{
			String stack = Util.getUsefulStack(ex, "executing command " + name);
			plugin.getLogHandler().log(Level.WARNING, stack);

			String error = FormatUtil.format("&cError: &4Encountered an exception executing this command: ");

			ComponentBuilder builder = new ComponentBuilder(error);
			builder.append(FormatUtil.format("&c{0}", ex.toString())).event(new HoverEvent(Action.SHOW_TEXT, stack));
			sendMessage(builder.create());
		}
	}

	public abstract void perform();

	protected final boolean isPlayer()
	{
		return sender instanceof Player;
	}

	// ---- Permission Management

	protected final boolean hasPermission(CommandSender sender, IPermission permission)
	{
		return plugin.getPermissionHandler().hasPermission(sender, permission);
	}

	protected final boolean hasPermission(IPermission permission)
	{
		return hasPermission(sender, permission);
	}

	protected final String getPermissionString(IPermission permission)
	{
		return plugin.getPermissionHandler().getPermissionString(permission);
	}

	public final String getPermissionString()
	{
		return getPermissionString(permission);
	}

	public final boolean isVisibleTo(CommandSender sender)
	{
		switch (visibility)
		{
			case ALL:
				return true;
			case NONE:
				return false;
			case OPS:
				return sender.isOp();
			default:
				return hasPermission(sender, permission);
		}
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
		sender.sendMessage(ChatColor.YELLOW + FormatUtil.format(message, objects));
	}

	protected final void err(CommandSender sender, String msg, Object... args)
	{
		sendMessage(sender, "&cError: &4" + FormatUtil.format(msg, args));
	}

	protected final void sendpMessage(CommandSender sender, String message, Object... objects)
	{
		sendMessage(sender, plugin.getPrefix() + message, objects);
	}

	protected final void sendMessage(CommandSender sender, String message, Object... objects)
	{
		sender.sendMessage(ChatColor.YELLOW + FormatUtil.format(message, objects));
	}

	// ---- Fancy Messaging

	protected final void sendMessage(BaseComponent... components)
	{
		sendMessage(sender, components);
	}

	protected final void sendMessage(CommandSender sender, BaseComponent... components)
	{
		ChatUtil.sendMessage(sender, components);
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

	public List<String> getUsageTemplate(boolean displayHelp)
	{
		List<String> ret = new ArrayList<>();

		for (int i = 0; i < syntaxes.size(); i++)
		{
			Syntax syntax = syntaxes.get(i);
			StringBuilder line = new StringBuilder();
			line.append("&b/");

			if (plugin.getCommandHandler().usesCommandPrefix() && usesPrefix)
				line.append(plugin.getCommandHandler().getCommandPrefix() + " ");

			line.append(name);

			for (Argument arg : syntax)
			{
				if (arg.isRequired())
					line.append(String.format(" &3<%s>", arg.getArgument()));
				else
					line.append(String.format(" &3[%s]", arg.getArgument()));
			}

			if (displayHelp && i == 0)
				line.append(" &e" + description);

			ret.add(FormatUtil.format(line.toString()));
		}

		return ret;
	}

	public List<BaseComponent[]> getFancyUsageTemplate()
	{
		return getFancyUsageTemplate(false);
	}

	public List<BaseComponent[]> getFancyUsageTemplate(boolean list)
	{
		List<BaseComponent[]> ret = new ArrayList<>();

		for (int i = 0; i < syntaxes.size(); i++)
		{
			Syntax syntax = syntaxes.get(i);
			StringBuilder templateBuilder = new StringBuilder();
			templateBuilder.append("&b/");

			if (plugin.getCommandHandler().usesCommandPrefix() && usesPrefix)
				templateBuilder.append(plugin.getCommandHandler().getCommandPrefix() + " ");

			templateBuilder.append(name);

			for (Argument arg : syntax)
			{
				if (arg.isRequired())
					templateBuilder.append(String.format(" &3<%s>", arg.getArgument()));
				else
					templateBuilder.append(String.format(" &3[%s]", arg.getArgument()));
			}

			String template = FormatUtil.format(templateBuilder.toString());
			String prefix = list ? i == 0 ? "- " : "  " : "";
			ComponentBuilder builder = new ComponentBuilder(ChatColor.AQUA + prefix + template);

			StringBuilder hoverTextBuilder = new StringBuilder();
			hoverTextBuilder.append(template + ":\n");

			for (int a = 0; a < syntax.size(); a++)
			{
				Argument arg = syntax.get(a);
				String explanation = arg.getExplanation();
				if (explanation != null)
				{
					String argument = arg.getArgument();
					if (arg.isRequired())
						hoverTextBuilder.append(FormatUtil.format("&3  <{0}>: &e{1}\n", argument, explanation));
					else
						hoverTextBuilder.append(FormatUtil.format("&3  [{0}]: &e{1}\n", argument, explanation));
				}

				if (a != 0 && a == syntax.size() - 1)
					hoverTextBuilder.append("\n");
			}

			StringJoiner description = new StringJoiner("\n");
			for (String s : getDescription())
				description.append(ChatColor.YELLOW + s);
			hoverTextBuilder.append(FormatUtil.format(capitalizeFirst(description.toString())));

			if (permission != null)
			{
				hoverTextBuilder.append("\n\n");
				hoverTextBuilder.append(ChatColor.DARK_RED + "Permission:");
				hoverTextBuilder.append("\n" + getPermissionString());
			}

			String hoverText = hoverTextBuilder.toString();

			HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverText));
			builder.event(hoverEvent);

			ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ChatColor.stripColor(template));
			builder.event(clickEvent);

			ret.add(builder.create());
		}

		return ret;
	}

	private List<String> descriptionList;

	public List<String> getDescription()
	{
		if (descriptionList == null)
			descriptionList = Arrays.asList(description);
		return descriptionList;
	}

	// ---- Sub Commands

	protected final void addSubCommand(SubCommand command)
	{
		subCommands.add(command);
	}

	protected final Command getParentCommand()
	{
		return parent;
	}

	protected final boolean hasSubCommands()
	{
		return ! subCommands.isEmpty();
	}

	protected final List<SubCommand> getSubCommands()
	{
		return subCommands;
	}

	protected final List<String> getSubCommandHelp(boolean displayHelp)
	{
		List<String> ret = new ArrayList<>();

		for (SubCommand cmd : getSubCommands())
		{
			ret.addAll(cmd.getUsageTemplate(displayHelp));
		}

		return ret;
	}

	public final List<BaseComponent[]> getFancySubCommandHelp()
	{
		return getFancySubCommandHelp(false);
	}

	public final List<BaseComponent[]> getFancySubCommandHelp(boolean list)
	{
		List<BaseComponent[]> ret = new ArrayList<>();

		for (SubCommand cmd : getSubCommands())
		{
			ret.addAll(cmd.getFancyUsageTemplate(list));
		}

		return ret;
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

	protected final int argAsInt(int arg, boolean msg)
	{
		int ret = -1;
		if (args.length > arg)
			ret = NumberUtil.toInt(args[arg]);

		if (msg && ret == - 1)
			err("&c{0} &4is not a number.", args[arg]);

		return ret;
	}

	protected final double argAsDouble(int arg, boolean msg)
	{
		double ret = -1.0D;
		if (args.length > arg)
			ret = NumberUtil.toDouble(args[arg]);

		if (msg && ret == -1.0D)
			err("&c{0} &4is not a number.", args[arg]);

		return ret;
	}

	protected boolean argAsBoolean(int arg)
	{
		return argAsBoolean(arg, false);
	}

	protected boolean argAsBoolean(int arg, boolean def)
	{
		return args.length > arg ? Util.toBoolean(args[arg]) : def;
	}

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

	protected String capitalizeFirst(String string)
	{
		return Character.toUpperCase(string.charAt(0)) + string.substring(1);
	}

	// ---- Utility

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

	// ---- Syntax

	protected final void invalidSyntax()
	{
		invalidSyntax(args);
	}

	protected final void invalidSyntax(String[] args)
	{
		Syntax closest = findClosest(args);
		String invalidSyntax = FormatUtil.format("&cError: &4Invalid syntax! Missing: &c");
		ComponentBuilder builder = new ComponentBuilder(invalidSyntax);

		List<Argument> missing = closest.missingSyntax(args.length);
		for (int i = 0; i < missing.size(); i++)
		{
			Argument arg = missing.get(i);
			String line = "&c" + arg.getArgument();
			if (i != 0)
				line = "&4, " + line;

			builder.append(FormatUtil.format(line));
			String explanation = arg.getExplanation();
			if (explanation != null)
				builder.event(new HoverEvent(Action.SHOW_TEXT, FormatUtil.format("&4{0}:\n&f{1}", arg.getArgument(), explanation)));
		}

		sendMessage(builder.create());
	}

	private final Syntax findClosest(String[] args)
	{
		if (syntaxes.size() == 1 || args.length == 0)
			return defaultSyntax();

		// Find the closest match
		Syntax closest = null;
		int delta = -1;

		for (Syntax syntax : syntaxes)
		{
			int curDelta = Math.abs(syntax.size() - args.length);
			if (curDelta < delta || curDelta == -1)
			{
				closest = syntax;
				delta = curDelta;
			}

			if (curDelta == 0)
				break;
		}

		return closest != null ? closest : defaultSyntax();
	}

	private final Syntax defaultSyntax()
	{
		return syntaxes.get(0);
	}

	protected final void addArgument(String arg, String explanation, boolean required)
	{
		Syntax syntax = syntaxes.get(syntaxes.size() - 1);
		syntax.add(new Argument(arg, explanation, required));
	}

	protected final void addRequiredArg(String arg)
	{
		addArgument(arg, null, true);
	}

	protected final void addRequiredArg(String arg, String explanation)
	{
		addArgument(arg, explanation, true);
	}

	protected final void addOptionalArg(String arg)
	{
		addArgument(arg, null, false);
	}

	protected final void addOptionalArg(String arg, String explanation)
	{
		addArgument(arg, explanation, false);
	}

	@Data
	@AllArgsConstructor
	public class Argument
	{
		private final String argument;
		private final String explanation;
		private final boolean required;
	}

	public class Syntax extends ArrayList<Argument>
	{
		private static final long serialVersionUID = 1L;

		public final int requiredSize()
		{
			int required = 0;
			for (Argument arg : this)
			{
				if (arg.isRequired())
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
				if (arg.isRequired() == required)
				{
					if (i == index)
						return arg;
					i++;
				}
			}

			return null;
		}
	}

	public class SyntaxBuilder
	{
		private final List<Syntax> syntaxes;

		public SyntaxBuilder()
		{
			this.syntaxes = new ArrayList<>();
			syntaxes.add(new Syntax());
		}

		public SyntaxBuilder newSyntax()
		{
			syntaxes.add(new Syntax());
			return this;
		}

		public SyntaxBuilder requiredArg(String arg)
		{
			add(arg, null, true);
			return this;
		}

		public SyntaxBuilder requiredArg(String arg, String explanation)
		{
			add(arg, explanation, true);
			return this;
		}

		public SyntaxBuilder optionalArg(String arg)
		{
			add(arg, null, false);
			return this;
		}

		public SyntaxBuilder optionalArg(String arg, String explanation)
		{
			add(arg, explanation, false);
			return this;
		}

		public SyntaxBuilder add(String arg, String explanation, boolean required)
		{
			Syntax current = syntaxes.get(syntaxes.size() - 1);
			current.add(new Argument(arg, explanation, required));
			return this;
		}

		public List<Syntax> build()
		{
			return syntaxes;
		}
	}
}