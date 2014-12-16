/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.integration;

import java.util.logging.Level;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.util.Util;

import org.bukkit.plugin.Plugin;

/**
 * @author dmulloy2
 */

public abstract class DependencyProvider<T extends Plugin>
{
	protected String name;
	protected T dependency;
	protected boolean enabled;

	protected final SwornPlugin handler;

	@SuppressWarnings("unchecked")
	public DependencyProvider(SwornPlugin handler, String name)
	{
		this.handler = handler;
		this.name = name;

		if (dependency == null && ! enabled)
		{
			try
			{
				dependency = (T) handler.getServer().getPluginManager().getPlugin(name);
				if (dependency != null)
				{
					enabled = true;
					handler.getLogHandler().log("{0} integration successful.");
				}
			}
			catch (Throwable ex)
			{
				handler.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "hooking into " + name));
			}
		}
	}

	public T getDependency()
	{
		if (dependency == null)
			throw new RuntimeException(name + " dependency does not exist.");
		return dependency;
	}

	public boolean isEnabled()
	{
		return enabled;
	}
}
