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

	public ResourceHandler(SwornPlugin plugin)
	{
		this(plugin, plugin.classLoader());
	}

	public ResourceHandler(SwornPlugin plugin, ClassLoader classLoader)
	{
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
			plugin.getLogger().log(Level.SEVERE, "Could not find resource bundle: messages.properties");
		}
	}

	@Deprecated
	public ResourceBundle getMessages()
	{
		return messages;
	}

	public final String getMessage(String key)
	{
		if (messages == null)
			return "Messages file missing!";

		try
		{
			return messages.getString(key);
		} catch (Throwable ex) { }
		return "<Missing Message: " + key + ">";
	}
}