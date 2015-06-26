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
package net.dmulloy2;

import java.util.List;

import lombok.Getter;
import net.dmulloy2.commands.Command;
import net.dmulloy2.handlers.CommandHandler;
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.handlers.PermissionHandler;
import net.dmulloy2.types.Reloadable;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main SwornAPI class. Plugins utilizing this API should extend this class.
 *
 * @author dmulloy2
 */

public abstract class SwornPlugin extends JavaPlugin implements Reloadable
{
	protected @Getter PermissionHandler permissionHandler;
	protected @Getter CommandHandler commandHandler;
	protected @Getter LogHandler logHandler;

	/**
	 * Gets this plugin's prefix. Defaults to {@link ChatColor#YELLOW}.
	 * @return This plugin's prefix
	 */
	public String getPrefix()
	{
		return ChatColor.YELLOW.toString();
	}

	/**
	 * Gets any extra lines to be displayed in the help menu.
	 * @return Any extra lines, or null if none
	 */
	public List<String> getExtraHelp()
	{
		return null;
	}

	/**
	 * Gets this plugin's custom help command, if applicable.
	 * @return Custom help command, or null if unapplicable.
	 */
	public Command getHelpCommand()
	{
		return null;
	}

	/**
	 * Gets this plugin's default command if applicable. The default command is
	 * run if the first argument is not a valid command. This has no effect when
	 * prefixed commands are not used.
	 * @return The default command, or null if unapplicable.
	 */
	public Command getDefaultCommand()
	{
		return null;
	}

	/**
	 * Exposes this plugin's ClassLoader.
	 * @see JavaPlugin#getClassLoader()
	 */
	public ClassLoader classLoader()
	{
		return super.getClassLoader();
	}

	@Override
	public void reload()
	{
		
	}
}