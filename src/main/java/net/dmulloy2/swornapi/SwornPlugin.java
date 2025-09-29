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
package net.dmulloy2.swornapi;

import lombok.Getter;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.dmulloy2.swornapi.commands.Command;
import net.dmulloy2.swornapi.commands.CommandProps;
import net.dmulloy2.swornapi.handlers.CommandHandler;
import net.dmulloy2.swornapi.handlers.LogHandler;
import net.dmulloy2.swornapi.handlers.PermissionHandler;
import net.dmulloy2.swornapi.types.Reloadable;
import net.dmulloy2.swornapi.types.Versioning;
import net.dmulloy2.swornapi.types.Versioning.Version;

/**
 * Main SwornAPI class. Plugins utilizing this API should extend this class.
 *
 * @author dmulloy2
 */

public abstract class SwornPlugin extends JavaPlugin implements Reloadable
{
	@Getter
	protected PermissionHandler permissionHandler;
	@Getter
	protected CommandHandler commandHandler;
	@Getter
	protected LogHandler logHandler;

	protected final CommandProps commandProps = new CommandProps();

	public boolean isPaperPlugin()
	{
		return false;
	}

	/**
	 * Allows the modification of basic command properties like the color scheme.
	 * @return The command properties
	 */
	public CommandProps props()
	{
		return commandProps;
	}

	/**
	 * Gets this plugin's prefix. Defaults to {@link ChatColor#YELLOW}.
	 * @return This plugin's prefix
	 */
	public String getPrefix()
	{
		return ChatColor.YELLOW.toString();
	}

	/**
	 * Gets any extra lines to be displayed in the help menu. By default, this
	 * method searches for extra help in the configuration.
	 * 
	 * @return Any extra lines, or null if none
	 */
	public List<String> getExtraHelp()
	{
		FileConfiguration config = getConfig();
		if (config.isSet("extraHelp"))
			return config.getStringList("extraHelp");
		return null;
	}

	/**
	 * Gets this plugin's help command. Unless this is overriden, it will be
	 * null before commands are registered.
	 * @return Custom help command, or the default if unapplicable.
	 */
	public Command getHelpCommand()
	{
		return commandHandler.getCommand("help");
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

	/**
	 * Checks if the currently running version of Minecraft is supported by
	 * SwornAPI. If not, a warning is printed using the logHandler.
	 */
	public void checkVersion()
	{
		Version version = Versioning.getVersion();
		if (version.wasDropped())
			logHandler.log(Level.WARNING, "This version of {0} no longer supports {1}. Consider updating Spigot.", getName(), version.getName());
		else if (! version.isSupported())
			logHandler.log(Level.WARNING, "This version of {0} does not support {1}. Check for an update!", getName(), version.getName());
	}

	@Override
	public void reload()
	{
		reloadConfig();
	}
}