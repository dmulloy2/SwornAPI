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

	/**
	 * Represents a supported Minecraft version
	 */
	@Getter
	public static class Version
	{
		private static final List<Version> supported = new ArrayList<>();

		/**
		 * Minecraft 1.7, the update that changed the world
		 */
		public static final Version MC_17 = new Version("Minecraft 1.7.x", "1.7");

		/**
		 * Minecraft 1.8, the bountiful update
		 */
		public static final Version MC_18 = new Version("Minecraft 1.8.x", "1.8");

		/**
		 * Minecraft 1.9, the combat update
		 */
		public static final Version MC_19 = new Version("Minecraft 1.9.x", "1.9");

		/**
		 * Minecraft 1.10, the frostburn update
		 */
		public static final Version MC_110 = new Version("Minecraft 1.10.x", "1.10");

		/**
		 * Minecraft 1.6, the horse update
		 * @deprecated No longer supported
		 */
		@Deprecated
		public static final Version MC_16 = new Version("Minecraft 1.6.x");

		private final String name;
		private final String matcher;
		private final boolean isSupported;

		protected Version(String name)
		{
			this.name = name;
			this.matcher = null;
			this.isSupported = false;
		}

		protected Version(String name, String matcher)
		{
			this.name = name;
			this.matcher = matcher;
			this.isSupported = true;
			supported.add(this);
		}

		/**
		 * Whether or not SwornAPI supports this version.
		 * @return True if it does, false if not
		 */
		public boolean isSupported()
		{
			return isSupported;
		}

		@Override
		public String toString()
		{
			return name + (matcher != null ? "[matcher=" + matcher + "]" : "");
		}

		/**
		 * Gets the list of supported Versions
		 * @return The list
		 */
		public static List<Version> getSupported()
		{
			return supported;
		}
	}

	private static Version version;

	// Adapted from ProtocolLib

	private static Version fromCurrent()
	{
		String version = extractVersion(Bukkit.getBukkitVersion());
		return version != null ? new Version("Minecraft " + version) : new Version("Unknown");
	}

	private static String extractVersion(String text)
	{
		return text.split("-")[0];
	}

	/**
	 * Gets the {@link Version} that this server is currently running.
	 *
	 * @return The version
	 */
	public static Version getVersion()
	{
		if (version == null)
		{
			String serverVersion = Bukkit.getBukkitVersion();
			for (Version ver : Version.getSupported())
			{
				if (serverVersion.contains(ver.getMatcher()))
					return version = ver;
			}

			return version = fromCurrent();
		}

		return version;
	}

	protected static void setVersion(Version version)
	{
		Versioning.version = version;
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
		return getVersion().isSupported();
	}
}