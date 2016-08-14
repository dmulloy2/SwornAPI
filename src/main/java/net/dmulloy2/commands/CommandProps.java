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
package net.dmulloy2.commands;

import net.dmulloy2.types.IPermission;

import org.bukkit.ChatColor;

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
	private ChatColor defaultColor = ChatColor.YELLOW;
	// TODO Usage templates?
	// TODO Error formatting?
	private String helpHeader = "&3---- &e{0} Commands &3- &e{1}&3/&e{2} &3----";
	private String helpFooter = "";
	private IPermission reloadPerm = null;

	public CommandProps() { }
}