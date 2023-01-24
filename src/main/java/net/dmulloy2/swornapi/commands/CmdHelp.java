/**
 * SwornAPI - common API for MineSworn and Shadowvolt plugins
 * Copyright (C) 2016 dmulloy2
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
package net.dmulloy2.swornapi.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.dmulloy2.swornapi.SwornPlugin;
import net.dmulloy2.swornapi.types.CommandVisibility;
import net.dmulloy2.swornapi.util.FormatUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Generic help command. This is a modified implementation of
 * {@link PaginatedCommand} with support for fancy text formatting.
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
		
		if (isPlayer())
		{
			for (BaseComponent[] components : getPage(index))
				player.spigot().sendMessage(components);
			return;
		}

		// Fall back to legacy help
		for (String line : getLegacyPage(index))
			sendMessage(line);
	}

	public String getHeader()
	{
		return props().getHelpHeader();
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

		String footer = props().getHelpFooter();
		if (! footer.isEmpty())
			lines.add(footer);

		return lines;
	}

	public List<BaseComponent[]> getHeader(int index)
	{
		return getLegacyHeader(index).stream().map(TextComponent::fromLegacyText).collect(Collectors.toList());
	}

	public List<String> getLegacyHeader(int index)
	{
		List<String> ret = new ArrayList<>();

		ret.add(format(getHeader(), plugin.getName(), index, getPageCount()));

		List<String> extraHelp = plugin.getExtraHelp();
		if (extraHelp != null)
		{
			for (String extra : extraHelp)
				ret.add(FormatUtil.format(extra));
		}

		ret.add(format("{b}Key: {h}<required> [optional]"));
		return ret;
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
		String footer = props().getHelpFooter();
		return TextComponent.fromLegacyText(format(footer));
	}

	private List<BaseComponent[]> getHelpMenu()
	{
		List<BaseComponent[]> ret = new ArrayList<>();

		for (Command cmd : plugin.getCommandHandler().getRegisteredPrefixedCommands())
		{
			if (cmd.isVisibleTo(sender))
			{
				ret.addAll(cmd.getFancyUsageTemplate(true));

				if (cmd.hasSubCommands())
					ret.addAll(cmd.getFancySubCommandHelp(true));
			}
		}

		for (Command cmd : plugin.getCommandHandler().getRegisteredCommands())
		{
			if (cmd.isVisibleTo(sender))
			{
				ret.addAll(cmd.getFancyUsageTemplate(true));

				if (cmd.hasSubCommands())
					ret.addAll(cmd.getFancySubCommandHelp(true));
			}
		}

		return ret;
	}

	private List<String> getLegacyHelpMenu()
	{
		List<String> ret = new ArrayList<>();

		for (Command cmd : plugin.getCommandHandler().getRegisteredPrefixedCommands())
		{
			if (cmd.isVisibleTo(sender))
			{
				ret.addAll(cmd.getUsageTemplate(true));

				if (cmd.hasSubCommands())
					ret.addAll(cmd.getSubCommandHelp(true));
			}
		}

		for (Command cmd : plugin.getCommandHandler().getRegisteredCommands())
		{
			if (cmd.isVisibleTo(sender))
			{
				ret.addAll(cmd.getUsageTemplate(true));

				if (cmd.hasSubCommands())
					ret.addAll(cmd.getSubCommandHelp(true));
			}
		}

		return ret;
	}
}
