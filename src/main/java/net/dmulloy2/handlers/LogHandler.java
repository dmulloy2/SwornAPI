/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.handlers;

import java.util.logging.Level;

import lombok.AllArgsConstructor;
import net.dmulloy2.SwornPlugin;
import net.dmulloy2.util.FormatUtil;

/**
 * Handles logging and formatting through the plugin's logger.
 *
 * @author dmulloy2
 */

@AllArgsConstructor
public class LogHandler
{
	private final SwornPlugin plugin;

	/**
	 * Logs a formatted message to console with a given level.
	 *
	 * @param level Logging {@link Level}.
	 * @param msg Message to log.
	 * @param objects Objects to format in.
	 */
	public final void log(Level level, String msg, Object... objects)
	{
		plugin.getLogger().log(level, FormatUtil.format(msg, objects));
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
		if (plugin.getConfig().getBoolean("debug", false))
		{
			log(level, "[Debug] " + msg, objects);
		}
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
}