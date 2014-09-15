/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.chat.BaseComponent;
import net.dmulloy2.chat.ClickEvent;
import net.dmulloy2.chat.ComponentBuilder;
import net.dmulloy2.chat.HoverEvent;
import net.dmulloy2.chat.HoverEvent.Action;
import net.dmulloy2.chat.TextComponent;
import net.dmulloy2.types.CommandVisibility;
import net.dmulloy2.types.IPermission;
import net.dmulloy2.types.StringJoiner;
import net.dmulloy2.util.ChatUtil;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.ListUtil;
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
	protected SwornPlugin plugin;

	protected CommandSender sender;
	protected Player player;
	protected String args[];

	protected String name;
	protected String description;

	protected IPermission permission;
	protected CommandVisibility visibility = CommandVisibility.PERMISSION;

	protected @Deprecated List<String> requiredArgs;
	protected @Deprecated List<String> optionalArgs;

	protected SyntaxMap syntax;
	protected List<String> aliases;

	protected boolean hasSubCommands;
	protected boolean mustBePlayer;
	protected boolean usesPrefix;

	public Command(SwornPlugin plugin)
	{
		this.plugin = plugin;
		this.syntax = new SyntaxMap();
		this.aliases = new ArrayList<String>(2);

		this.requiredArgs = new LegacySyntax(true);
		this.optionalArgs = new LegacySyntax(false);
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

		if (syntax.requiredSize() > args.length)
		{
			invalidArgs();
			return;
		}

		if (! isVisibleTo(sender))
		{
			if (visibility == CommandVisibility.PERMISSION)
			{
				StringJoiner hoverText = new StringJoiner("\n");
				hoverText.append(FormatUtil.format("&4Permission:\n"));
				hoverText.append(getPermissionString());

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

	protected final boolean hasPermission()
	{
		return hasPermission(permission);
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
		ChatUtil.sendMessage(player, components);
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

	public final String getUsageTemplate(boolean displayHelp)
	{
		StringBuilder ret = new StringBuilder();
		ret.append("&b/");

		if (plugin.getCommandHandler().usesCommandPrefix() && usesPrefix)
			ret.append(plugin.getCommandHandler().getCommandPrefix() + " ");

		ret.append(name);

		for (Entry<String, Boolean> entry : syntax)
		{
			if (entry.getValue())
				ret.append(String.format(" &3<%s>", entry.getKey()));
			else
				ret.append(String.format(" &3[%s]", entry.getKey()));
		}

		if (displayHelp)
			ret.append(" &e" + description);

		return FormatUtil.format(ret.toString());
	}

	public final BaseComponent[] getFancyUsageTemplate()
	{
		return getFancyUsageTemplate(false);
	}

	public final BaseComponent[] getFancyUsageTemplate(boolean list)
	{
		String prefix = list ? "- " : "";
		String usageTemplate = getUsageTemplate(false);

		ComponentBuilder builder = new ComponentBuilder(ChatColor.AQUA + prefix + usageTemplate);

		StringBuilder hoverTextBuilder = new StringBuilder();
		hoverTextBuilder.append(usageTemplate + ":\n");

		StringJoiner description = new StringJoiner("\n");
		for (String s : getDescription())
			description.append(ChatColor.YELLOW + s);
		hoverTextBuilder.append(FormatUtil.format(description.toString()));

		if (permission != null)
		{
			hoverTextBuilder.append("\n\n");
			hoverTextBuilder.append(ChatColor.DARK_RED + "Permission:");
			hoverTextBuilder.append("\n" + getPermissionString());
		}

		String hoverText = hoverTextBuilder.toString();

		HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverText));
		builder.event(hoverEvent);

		ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ChatColor.stripColor(usageTemplate));
		builder.event(clickEvent);

		return builder.create();
	}

	public List<String> getDescription()
	{
		return ListUtil.toList(description);
	}

	// ---- Sub Commands

	public final boolean hasSubCommands()
	{
		return hasSubCommands;
	}

	public List<? extends Command> getSubCommands()
	{
		return null;
	}

	public final List<String> getSubCommandHelp(boolean displayHelp)
	{
		List<String> ret = new ArrayList<>();

		for (Command cmd : getSubCommands())
		{
			ret.add(cmd.getUsageTemplate(displayHelp));
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

		for (Command cmd : getSubCommands())
		{
			ret.add(cmd.getFancyUsageTemplate(list));
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
		if (args.length >= arg)
			ret = NumberUtil.toInt(args[arg]);

		if (msg && ret == - 1)
			err("&c{0} &4is not a number.", args[arg]);

		return ret;
	}

	protected final double argAsDouble(int arg, boolean msg)
	{
		double ret = -1.0D;
		if (args.length >= arg)
			ret = NumberUtil.toDouble(args[arg]);

		if (msg && ret == -1.0D)
			err("&c{0} &4is not a number.", args[arg]);

		return ret;
	}

	protected final boolean argAsBoolean(int arg)
	{
		return Util.toBoolean(args[arg]);
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

	// ---- Utility

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

	protected final void addRequiredArg(String arg)
	{
		syntax.addRequired(arg);
	}

	protected final void addOptionalArg(String arg)
	{
		syntax.addOptional(arg);
	}

	class SyntaxMap extends LinkedHashMap<String, Boolean> implements Iterable<Entry<String, Boolean>>
	{
		private static final long serialVersionUID = 1L;

		public final void addRequired(String syntax)
		{
			super.put(syntax, true);
		}

		public final void addOptional(String syntax)
		{
			super.put(syntax, false);
		}

		public final int requiredSize()
		{
			int required = 0;
			for (Entry<String, Boolean> entry : this)
			{
				if (entry.getValue())
					required++;
			}
			return required;
		}

		@Override
		public Iterator<Entry<String, Boolean>> iterator()
		{
			return entrySet().iterator();
		}
	}

	class LegacySyntax extends ArrayList<String>
	{
		private static final long serialVersionUID = 1L;

		private final boolean required;
		public LegacySyntax(boolean required)
		{
			this.required = required;
		}

		@Override
		public final boolean add(String string)
		{
			syntax.put(string, required);
			return super.add(string);
		}
	}
}