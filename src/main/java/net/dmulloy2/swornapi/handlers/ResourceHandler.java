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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;

import net.dmulloy2.swornapi.SwornPlugin;
import net.dmulloy2.swornapi.io.FileResourceLoader;

/**
 * @author dmulloy2
 */

public class ResourceHandler
{
	private Locale locale;
	private ResourceBundle messages;

	private final SwornPlugin plugin;
	public ResourceHandler(SwornPlugin plugin)
	{
		this(plugin, plugin.classLoader());
	}

	public ResourceHandler(SwornPlugin plugin, ClassLoader classLoader)
	{
		this.plugin = plugin;

		try
		{
			if (plugin.getConfig().isSet("locale"))
				locale = Locale.forLanguageTag(plugin.getConfig().getString("locale"));
			if (locale == null)
				locale = Locale.getDefault();

			messages = ResourceBundle.getBundle("messages", locale, new FileResourceLoader(classLoader, plugin));
		}
		catch (MissingResourceException ex)
		{
			plugin.getLogHandler().log(Level.SEVERE, "Could not find resource bundle: {0}", ex.getKey());
		}
	}

	private boolean bundleWarning;

	/**
	 * Gets a message from the message file with a given key.
	 *
	 * @param key Message key
	 * @return The message
	 */
	public final String getMessage(String key)
	{
		if (messages == null)
		{
			if (! bundleWarning)
			{
				plugin.getLogHandler().log(Level.WARNING, "Messages bundle is missing!");
				bundleWarning = true;
			}

			return "<Missing message bundle>";
		}

		try
		{
			return messages.getString(key);
		}
		catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, "Message for key \"{0}\" is missing!", key);
			return "<Missing message \"" + key + "\">";
		}
	}
}
