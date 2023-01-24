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

import net.dmulloy2.swornapi.types.IPermission;
import net.dmulloy2.swornapi.util.FormatUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author dmulloy2
 */
@Getter
@Setter
@Accessors(chain=true)
public class CommandProps
{
	private String errorPrefix = "&cError: &4";
	private String baseColor = "&e";
	private String accentColor = "&b";
	private String headerColor = "&3";
	private String helpHeader = "&3---- &e{0} Commands &3- &e{1}&3/&e{2} &3----";
	private String helpFooter = "{b}Hover to see command information. Click to insert into chat.";
	private IPermission reloadPerm = null;

	public CommandProps() { }

	public String format(String string, Object... args)
	{
		return FormatUtil.format(string
				.replace("{b}", baseColor)
				.replace("{a}", accentColor)
				.replace("{h}", headerColor),
				args);
	}

	public String formatErr(String string, Object... args)
	{
		return format(errorPrefix + string, args);
	}
}