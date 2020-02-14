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

/**
 * Represents a sub-command
 * @author dmulloy2
 */

public abstract class SubCommand extends Command
{
	public SubCommand(Command parent)
	{
		super(parent.plugin);
		this.parent = parent;
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
}