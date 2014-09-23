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
import net.dmulloy2.commands.Command;
import net.dmulloy2.handlers.CommandHandler;
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.handlers.PermissionHandler;

import org.bukkit.ChatColor;
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

	public String getPrefix()
	{
		return ChatColor.YELLOW.toString();
	}

	public List<String> getExtraHelp()
	{
		return new ArrayList<>();
	}

	public Command getHelpCommand()
	{
		return null;
	}

	public ClassLoader classLoader()
	{
		return super.getClassLoader();
	}
}