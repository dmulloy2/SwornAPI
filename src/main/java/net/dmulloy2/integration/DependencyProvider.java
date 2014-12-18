/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.integration;

import java.util.logging.Level;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.util.Util;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.google.common.base.Preconditions;

/**
 * @author dmulloy2
 */

public class DependencyProvider<T extends Plugin>
{
	protected String name;
	protected T dependency;
	protected boolean enabled;

	protected final SwornPlugin handler;

	@SuppressWarnings("unchecked")
	public DependencyProvider(final SwornPlugin handler, final String name)
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
					onEnable();
					enabled = true;
					handler.getLogHandler().log("{0} integration successful.", name);
				}
			}
			catch (Throwable ex)
			{
				handler.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "hooking into " + name));
			}
		}

		handler.getServer().getPluginManager().registerEvents(new Listener()
		{
			@EventHandler
			public void onPluginEnable(PluginEnableEvent event)
			{
				if (dependency == null && event.getPlugin().getName().equals(name))
				{
					try
					{
						dependency = (T) event.getPlugin();
						onEnable();
						enabled = true;
						handler.getLogHandler().log("{0} integration enabled.", name);
					}
					catch (Throwable ex)
					{
						handler.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "hooking into " + name));
					}
				}
			}

			@EventHandler
			public void onPluginDisable(PluginDisableEvent event)
			{
				if (dependency != null && event.getPlugin().getName().equals(name))
				{
					onDisable();
					enabled = false;
					dependency = null;
					handler.getLogHandler().log("{0} integration disabled.", name);
				}
			}

		}, handler);
	}

	public void onEnable() { }

	public void onDisable() { }

	public T getDependency()
	{
		return Preconditions.checkNotNull(dependency, name + " dependency does not exist.");
	}

	public String getName()
	{
		return Preconditions.checkNotNull(name, "name cannot be null.");
	}

	public boolean isEnabled()
	{
		return enabled;
	}
}