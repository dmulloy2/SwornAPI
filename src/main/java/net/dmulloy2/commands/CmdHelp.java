/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.chat.BaseComponent;
import net.dmulloy2.chat.ComponentBuilder;
import net.dmulloy2.chat.TextComponent;
import net.dmulloy2.exception.ReflectionException;
import net.dmulloy2.types.StringJoiner;
import net.dmulloy2.util.ChatUtil;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.entity.Player;

/**
 * Generic help command.
 * 
 * @author dmulloy2
 */

public class CmdHelp extends Command
{
	protected int linesPerPage, pageArgIndex;

	public CmdHelp(SwornPlugin plugin)
	{
		super(plugin);
		this.name = "help";
		this.optionalArgs.add("page");
		this.description = "Shows " + plugin.getName() + " help";
		this.linesPerPage = 6;

		this.usesPrefix = true;
	}

	@Override
	public void perform()
	{
		int index = 1;
		if (args.length > pageArgIndex)
		{
			try
			{
				index = Integer.parseInt(args[pageArgIndex]);
				if (index < 1 || index > getPageCount())
					throw new IndexOutOfBoundsException();
			}
			catch (NumberFormatException ex)
			{
				err("&c{0} &4is not a number.", args[0]);
				return;
			}
			catch (IndexOutOfBoundsException ex)
			{
				err("&4There is no page with the index &c{0}&4.", args[0]);
				return;
			}
		}

		for (BaseComponent[] components : getPage(index))
			sendFancyMessage(components);
	}

	private static boolean exceptionPrinted;

	private void sendFancyMessage(BaseComponent[] components)
	{
		if (sender instanceof Player)
		{
			try
			{
				ChatUtil.sendMessage(player, components);
				return;
			}
			catch (ReflectionException ex)
			{
				if (! exceptionPrinted)
				{
					exceptionPrinted = true;
					plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "fancifying help"));
				}
			}
		}

		sender.sendMessage(TextComponent.toLegacyText(components));
	}

	public int getPageCount()
	{
		return (getListSize() + linesPerPage - 1) / linesPerPage;
	}

	public int getListSize()
	{
		return buildHelpMenu().size();
	}

	public List<BaseComponent[]> getPage(int index)
	{
		List<BaseComponent[]> lines = new ArrayList<BaseComponent[]>();
		lines.add(getHeader(index));
		lines.addAll(getLines((index - 1) * linesPerPage, index * linesPerPage));
		BaseComponent[] footer = getFooter();
		if (footer != null)
			lines.add(footer);
		return lines;
	}

	public BaseComponent[] getHeader(int index)
	{
		StringJoiner header = new StringJoiner("\n");
		header.append(FormatUtil.format("&3====[ &e{0} Commands &3(&e{1}&3/&e{2}&3) ]====", plugin.getName(), index, getPageCount()));
		for (String extra : plugin.getExtraHelp())
			header.append(FormatUtil.format(extra));

		return new ComponentBuilder(header.toString()).create();
	}

	public List<BaseComponent[]> getLines(int startIndex, int endIndex)
	{
		List<BaseComponent[]> lines = new ArrayList<BaseComponent[]>();
		for (int i = startIndex; i < endIndex && i < getListSize(); i++)
		{
			lines.add(buildHelpMenu().get(i));
		}

		return lines;
	}

	public BaseComponent[] getFooter()
	{
		return TextComponent.fromLegacyText(FormatUtil.format("&eHover to see command information. Click to insert into chat."));
	}

	private final List<BaseComponent[]> buildHelpMenu()
	{
		List<BaseComponent[]> ret = new ArrayList<BaseComponent[]>();

		for (Command cmd : plugin.getCommandHandler().getRegisteredPrefixedCommands())
		{
			ret.add(cmd.getFancyUsageTemplate(true));
		}

		for (Command cmd : plugin.getCommandHandler().getRegisteredCommands())
		{
			ret.add(cmd.getFancyUsageTemplate(true));
		}

		return ret;
	}
}