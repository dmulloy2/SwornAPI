/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.handlers;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.io.FileResourceLoader;

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

	/**
	 * @deprecated Use {@link #getMessage(String)} instead
	 */
	@Deprecated
	public ResourceBundle getMessages()
	{
		return messages;
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