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
package net.dmulloy2.types;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.bukkit.Bukkit;

/**
 * @author dmulloy2
 */

public class Versioning
{
	private Versioning() { }

	private static final List<Version> supported = new ArrayList<>();

	/**
	 * Represents a supported Minecraft version
	 */
	@Getter
	public static enum Version
	{
		/**
		 * Minecraft 1.7, the update that changed the world
		 */
		MC_17("Minecraft 1.7.x", "1.7"),

		/**
		 * Minecraft 1.8, the bountiful update
		 */
		MC_18("Minecraft 1.8.x", "1.8"),

		/**
		 * Minecraft 1.9, the combat update
		 */
		MC_19("Minecraft 1.9.x", "1.9"),

		/**
		 * Minecraft 1.6, the horse update
		 * @deprecated No longer supported
		 */
		@Deprecated
		MC_16("Minecraft 1.6.x"),

		/**
		 * An unsupported Minecraft version
		 */
		UNKNOWN("Unknown"),
		;

		private final String name;
		private final String matcher;

		private Version(String name)
		{
			this.name = name;
			this.matcher = null;
		}

		private Version(String name, String matcher)
		{
			this.name = name;
			this.matcher = matcher;
			supported.add(this);
		}
	}

	private static Version version;

	/**
	 * Gets the {@link Version} that this server is currently running.
	 *
	 * @return The version
	 */
	public static final Version getVersion()
	{
		if (version == null)
		{
			String serverVersion = Bukkit.getVersion();
			for (Version ver : supported)
			{
				if (serverVersion.contains(ver.getMatcher()))
					return version = ver;
			}

			return version = Version.UNKNOWN;
		}

		return version;
	}

	/**
	 * Gets the version string that this server is currently running.
	 *
	 * @return The version string
	 */
	public static String getVersionString()
	{
		return getVersion().getName();
	}

	/**
	 * Whether or not the currently running version is supported.
	 *
	 * @return True if it is supported, false if not
	 */
	public static final boolean isSupported()
	{
		return getVersion() != Version.UNKNOWN;
	}
}
