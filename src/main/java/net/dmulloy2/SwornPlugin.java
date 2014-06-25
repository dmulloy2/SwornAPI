/**
 * SwornAPI - a common API for MineSworn and Shadowvolt plugins.
 * Copyright (C) - 2014 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import net.dmulloy2.commands.CmdHelp;
import net.dmulloy2.commands.Command;
import net.dmulloy2.handlers.CommandHandler;
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.handlers.PermissionHandler;
import net.dmulloy2.types.LazyLocation;
import net.dmulloy2.types.SimpleVector;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main SwornAPI class. Plugins utilizing this API should extend this class.
 * 
 * @author dmulloy2
 */

public abstract class SwornPlugin extends JavaPlugin
{
	protected @Getter PermissionHandler permissionHandler;
	protected @Getter CommandHandler commandHandler;
	protected @Getter LogHandler logHandler;

	protected @Getter String prefix;

	public List<String> getExtraHelp()
	{
		return new ArrayList<>();
	}

	private final CmdHelp cmdHelp = new CmdHelp(this);
	public Command getHelpCommand()
	{
		return cmdHelp;
	}

	private static boolean registered = false;
	public static final void checkRegistrations()
	{
		if (! registered)
		{
			ConfigurationSerialization.registerClass(LazyLocation.class);
			ConfigurationSerialization.registerClass(SimpleVector.class);
			registered = true;
		}
	}
}