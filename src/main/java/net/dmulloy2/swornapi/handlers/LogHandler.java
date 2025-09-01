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
package net.dmulloy2.swornapi.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.dmulloy2.swornapi.SwornPlugin;
import net.dmulloy2.swornapi.util.FormatUtil;

/**
 * Handles logging and formatting through the plugin's logger.
 *
 * @author dmulloy2
 */
public class LogHandler
{
	private final SwornPlugin plugin;
	private final Logger logger;

	public LogHandler(SwornPlugin plugin, Logger logger)
	{
		this.plugin = plugin;
		this.logger = logger;
	}

	public LogHandler(SwornPlugin plugin)
	{
		this(plugin, plugin.getLogger());
	}

	/**
	 * Logs a formatted message to console with a given level.
	 *
	 * @param level Logging {@link Level}.
	 * @param msg Message to log.
	 * @param objects Objects to format in.
	 */
	public final void log(Level level, String msg, Object... objects)
	{
		logger.log(level, FormatUtil.format(msg, objects));
	}

	public final void error(Throwable ex, String msg, Object... objects)
	{
		logger.log(Level.SEVERE, FormatUtil.format(msg, objects), ex);
	}

	public final void error(String msg, Object... objects)
	{
		logger.log(Level.SEVERE, FormatUtil.format(msg, objects));
	}

	public final void warn(Throwable ex, String msg, Object... objects)
	{
		logger.log(Level.WARNING, FormatUtil.format(msg, objects), ex);
	}

	public final void warn(String msg, Object... objects)
	{
		logger.log(Level.WARNING, FormatUtil.format(msg, objects));
	}

	/**
	 * Logs a formatted message to console with INFO level.
	 *
	 * @param msg Message to log.
	 * @param objects Objects to format in.
	 */
	public final void log(String msg, Object... objects)
	{
		log(Level.INFO, msg, objects);
	}

	/**
	 * Logs a debug message to console with a given level if <code>debug</code>
	 * is set to <code>true</code> in the config.yml.
	 *
	 * @param level Logging {@link Level}.
	 * @param msg Message to log.
	 * @param objects Objects to format in.
	 */
	public final void debug(Level level, String msg, Object... objects)
	{
		if (isGlobalDebugEnabled() || plugin.getConfig().getBoolean("debug", false))
			log(level, "[Debug] " + msg, objects);
	}

	/**
	 * Logs a debug message to console with the INFO level if <code>debug</code>
	 * is set to <code>true</code> in the config.yml.
	 *
	 * @param msg Message to log.
	 * @param objects Objects to format in.
	 */
	public final void debug(String msg, Object... objects)
	{
		debug(Level.INFO, msg, objects);
	}

	/**
	 * Logs a debug message to the console if the system property
	 * <code>swornapi.debug</code> is true.
	 * 
	 * @param msg Message to log.
	 * @param objects Objects to format in.
	 */
	public static void globalDebug(String msg, Object... objects)
	{
		if (isGlobalDebugEnabled())
			System.out.println("[Debug] " + FormatUtil.format(msg, objects));
	}

	/**
	 * @return true if the system property <code>swornapi.debug</code> is true
	 */
	public static boolean isGlobalDebugEnabled()
	{
		return Boolean.getBoolean("swornapi.debug");
	}
}