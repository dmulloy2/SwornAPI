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

import java.util.logging.Level;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.util.Util;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A dependency provider for optional {@link Plugin} dependencies.
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

public class DependencyProvider<T extends Plugin>
{

	protected T dependency;
	protected boolean enabled;

	private final int minVersion;
	protected final String name;
	protected final SwornPlugin handler;

	public DependencyProvider(final SwornPlugin handler, final String name)
	{
		this(handler, name, new int[0]);
	}

	public DependencyProvider(final SwornPlugin handler, final String name, int... minVersion)
	{
		this.handler = checkNotNull(handler, "handler cannot be null!");
		this.name = checkNotNull(name, "name cannot be null!");
		this.minVersion = condense(minVersion);

		handler.getServer().getPluginManager().registerEvents(new Listener()
		{
			@EventHandler
			public void onPluginEnable(PluginEnableEvent event)
			{
				if (dependency == null && event.getPlugin().getName().equals(name))
				{
					enable();
				}
			}

			@EventHandler
			public void onPluginDisable(PluginDisableEvent event)
			{
				if (dependency != null && event.getPlugin().getName().equals(name))
				{
					disable();
					handler.getLogHandler().log("{0} integration disabled.", name);
				}
			}
		}, handler);

		enable();
	}

	@SuppressWarnings("unchecked")
	protected void enable()
	{
		if (dependency != null)
		{
			return;
		}

		try
		{
			dependency = (T) handler.getServer().getPluginManager().getPlugin(name);
			if (dependency != null && versionCheck())
			{
				enabled = true;
				onEnable();
				handler.getLogHandler().log("{0} integration successful.", name);
			}
		}
		catch (Throwable ex)
		{
			handler.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "hooking into " + name));
		}
	}

	protected void disable()
	{
		onDisable();
		enabled = false;
		dependency = null;
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
	 * Gets the dependency.
	 * 
	 * @return The dependency
	 * @throws NullPointerException if the dependency does not exist.
	 */
	public T getDependency()
	{
		return checkNotNull(dependency, name + " dependency does not exist.");
	}

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
		return enabled && dependency != null;
	}

	// ---- Version Checking

	private boolean versionCheck()
	{
		if (dependency != null)
		{
			String version = dependency.getDescription().getVersion();
			if (version.contains("-"))
			{
				version = version.split("-")[0];
			}

			int condensed = condense(version);
			return condensed >= minVersion;
		}

		return false;
	}

	private int condense(String version)
	{
		// assume that they will all (at least roughly) follow semver
		int major, minor = 0, patch = 0;

		String[] split = version.split("\\.");
		major = Integer.parseInt(split[0]);

		if (split.length > 1)
		{
			minor = Integer.parseInt(split[1]);
		}

		if (split.length > 2)
		{
			patch = Integer.parseInt(split[2]);
		}

		return condense(major, minor, patch);
	}

	private int condense(int... numbers)
	{
		int major = numbers.length > 0 ? numbers[0] : 0;
		int minor = numbers.length > 1 ? numbers[1] : 0;
		int patch = numbers.length > 2 ? numbers[2] : 0;

		return (10000 * major) + (100 * minor) + patch;
	}
}
