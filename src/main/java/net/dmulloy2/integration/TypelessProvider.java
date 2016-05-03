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
package net.dmulloy2.integration;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.util.Util;

/**
 * A dependency provider for optional {@link Plugin} dependencies that only checks for the existence of a plugin.
 * This provider doesn't provide the Plugin object.
 * <p>
 * In order to avoid a hard dependency, the following precautions should be taken: <br>
 * <ul>
 *   <li>Initialization of Objects of this Class should be wrapped in a try-catch block.</li>
 *   <li>Methods in this class should return Objects that will definitely exist at runtime.</li>
 *     <ul>
 *       <li>Like Bukkit objects, Strings, and Java primitives.</li>
 *     </ul>
 *   <li>Before calling methods from this class, {@link #isEnabled()} should be called.</li>
 * </ul>
 *
 * @author dmulloy2
 */

public class TypelessProvider
{
	protected String name;
	protected boolean enabled;

	protected final SwornPlugin handler;

	public TypelessProvider(final SwornPlugin handler, final String name)
	{
		this.handler = checkNotNull(handler, "handler cannot be null!");
		this.name = checkNotNull(name, "name cannot be null!");

		if (handler.getServer().getPluginManager().isPluginEnabled(name))
		{
			enabled = true;
			onEnable();
			handler.getLogHandler().log("{0} integration successful.", name);
		}

		handler.getServer().getPluginManager().registerEvents(new Listener()
		{
			@EventHandler
			public void onPluginEnable(PluginEnableEvent event)
			{
				if (! enabled && event.getPlugin().getName().equals(name))
				{
					try
					{
						enabled = true;
						onEnable();
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
				if (enabled && event.getPlugin().getName().equals(name))
				{
					onDisable();
					enabled = false;
					handler.getLogHandler().log("{0} integration disabled.", name);
				}
			}

		}, handler);
	}

	/**
	 * Called when the dependency is found or enabled.
	 */
	public void onEnable() { }

	/**
	 * Called when the dependency is disabled.
	 */
	public void onDisable() { }

	/**
	 * Gets this dependency's name.
	 * 
	 * @return The dependency's name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Whether or not this dependency is enabled.
	 * 
	 * @return True if enabled, false if not
	 */
	public boolean isEnabled()
	{
		return enabled;
	}
}