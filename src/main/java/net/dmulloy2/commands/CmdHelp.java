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
import java.util.List;
import java.util.logging.Level;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.chat.BaseComponent;
import net.dmulloy2.chat.ChatUtil;
import net.dmulloy2.chat.TextComponent;
import net.dmulloy2.types.CommandVisibility;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

/**
 * Generic help command.
 *
 * @author dmulloy2
 */

public class CmdHelp extends Command
{
	private static final int linesPerPage = 6;
	private static final int pageArgIndex = 0;

	public CmdHelp(SwornPlugin plugin)
	{
		super(plugin);
		this.name = "help";
		this.addOptionalArg("page");
		this.description = "Shows " + plugin.getName() + " help";
		this.visibility = CommandVisibility.ALL;
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
				{
					err("&4There is no page with the index &c{0}&4.", index);
					return;
				}
			}
			catch (NumberFormatException ex)
			{
				err("&c{0} &4is not a number.", args[0]);
				return;
			}
		}

		try
		{
			// Attempt to send fancy help
			for (BaseComponent[] components : getPage(index))
				ChatUtil.sendMessageRaw(sender, components);
		}
		catch (Throwable ex)
		{
			// Fallback to legacy help
			for (String line : getLegacyPage(index))
				sendMessage(line);

			plugin.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "sending help to " + sender.getName()));
		}
	}

	public int getPageCount()
	{
		return (getListSize() + linesPerPage - 1) / linesPerPage;
	}

	public int getListSize()
	{
		return getHelpMenu().size();
	}

	public List<BaseComponent[]> getPage(int index)
	{
		List<BaseComponent[]> lines = new ArrayList<>();

		lines.addAll(getHeader(index));
		lines.addAll(getLines((index - 1) * linesPerPage, index * linesPerPage));

		BaseComponent[] footer = getFooter();
		if (footer != null)
			lines.add(footer);

		return lines;
	}

	public List<String> getLegacyPage(int index)
	{
		List<String> lines = new ArrayList<>();

		lines.addAll(getLegacyHeader(index));
		lines.addAll(getLegacyLines((index - 1) * linesPerPage, index * linesPerPage));

		String footer = getLegacyFooter();
		if (! footer.isEmpty())
			lines.add(footer);

		return lines;
	}

	public List<BaseComponent[]> getHeader(int index)
	{
		return TextComponent.fromLegacyList(getLegacyHeader(index));
	}

	public List<String> getLegacyHeader(int index)
	{
		List<String> header = new ArrayList<>();

		header.add(FormatUtil.format("&3====[ &e{0} Commands &3(&e{1}&3/&e{2}&3) ]====", plugin.getName(), index, getPageCount()));

		if (plugin.getExtraHelp() != null)
		{
			for (String extra : plugin.getExtraHelp())
				header.add(FormatUtil.format(extra));
		}

		header.add(FormatUtil.format("&eKey: &3<required> [optional]"));
		return header;
	}

	public List<BaseComponent[]> getLines(int startIndex, int endIndex)
	{
		List<BaseComponent[]> helpMenu = getHelpMenu();
		List<BaseComponent[]> lines = new ArrayList<>();

		for (int i = startIndex; i < endIndex && i < getListSize(); i++)
		{
			lines.add(helpMenu.get(i));
		}

		return lines;
	}

	public List<String> getLegacyLines(int startIndex, int endIndex)
	{
		List<String> helpMenu = getLegacyHelpMenu();
		List<String> lines = new ArrayList<>();

		for (int i = startIndex; i < endIndex && i < getListSize(); i++)
		{
			lines.add(helpMenu.get(i));
		}

		return lines;
	}

	public BaseComponent[] getFooter()
	{
		return TextComponent.fromLegacyText(FormatUtil.format("&eHover to see command information. Click to insert into chat."));
	}

	public String getLegacyFooter()
	{
		// No footer
		return "";
	}

	private final List<BaseComponent[]> getHelpMenu()
	{
		List<BaseComponent[]> ret = new ArrayList<>();

		for (Command cmd : plugin.getCommandHandler().getRegisteredPrefixedCommands())
		{
			if (cmd.isVisibleTo(sender))
			{
				if (cmd.hasSubCommands())
					ret.addAll(cmd.getFancySubCommandHelp(true));
				else
					ret.addAll(cmd.getFancyUsageTemplate(true));
			}
		}

		for (Command cmd : plugin.getCommandHandler().getRegisteredCommands())
		{
			if (cmd.isVisibleTo(sender))
			{
				if (cmd.hasSubCommands())
					ret.addAll(cmd.getFancySubCommandHelp(true));
				else
					ret.addAll(cmd.getFancyUsageTemplate(true));
			}
		}

		return ret;
	}

	private final List<String> getLegacyHelpMenu()
	{
		List<String> ret = new ArrayList<>();

		for (Command cmd : plugin.getCommandHandler().getRegisteredPrefixedCommands())
		{
			if (cmd.isVisibleTo(sender))
			{
				if (cmd.hasSubCommands())
					ret.addAll(cmd.getSubCommandHelp(true));
				else
					ret.addAll(cmd.getUsageTemplate(true));
			}
		}

		for (Command cmd : plugin.getCommandHandler().getRegisteredCommands())
		{
			if (cmd.isVisibleTo(sender))
			{
				if (cmd.hasSubCommands())
					ret.addAll(cmd.getSubCommandHelp(true));
				else
					ret.addAll(cmd.getUsageTemplate(true));
			}
		}

		return ret;
	}
}
