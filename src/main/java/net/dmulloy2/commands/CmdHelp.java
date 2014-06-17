/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.commands;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.util.FormatUtil;

/**
 * Generic help command.
 * 
 * @author dmulloy2
 */

public class CmdHelp extends PaginatedCommand
{
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
	public int getListSize()
	{
		return buildHelpMenu().size();
	}

	@Override
	public String getHeader(int index)
	{
		return FormatUtil.format("&3====[ &e{0} Commands &3(&e{1}&3/&e{2}&3) ]====", plugin.getName(), index, getPageCount());
	}

	@Override
	public List<String> getLines(int startIndex, int endIndex)
	{
		List<String> lines = new ArrayList<String>();
		for (int i = startIndex; i < endIndex && i < getListSize(); i++)
		{
			lines.add(buildHelpMenu().get(i));
		}

		return lines;
	}

	@Override
	public String getLine(int index)
	{
		return null;
	}

	private final List<String> buildHelpMenu()
	{
		List<String> ret = new ArrayList<String>();

		for (Command cmd : plugin.getCommandHandler().getRegisteredPrefixedCommands())
		{
			if (plugin.getPermissionHandler().hasPermission(sender, cmd.permission))
			{
				ret.add(cmd.getUsageTemplate(true));
			}
		}

		for (Command cmd : plugin.getCommandHandler().getRegisteredCommands())
		{
			if (plugin.getPermissionHandler().hasPermission(sender, cmd.permission))
			{
				ret.add(cmd.getUsageTemplate(true));
			}
		}

		return ret;
	}
}