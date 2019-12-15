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

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;

import lombok.Getter;

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
	public enum Version
	{
		/**
		 * Minecraft 1.15, the bee update
		 */
		MC_115("Minecraft 1.15.x", "1.15"),
		/**
		 * Minecraft 1.14, the village and pillage update
		 */
		MC_114("Minecraft 1.14.x", "1.14"),
		/**
		 * Minecraft 1.13, update aquatic / the flattening
		 */
		MC_113("Minecraft 1.13.x", "1.13"),
		/**
		 * Minecraft 1.12, the world of color update
		 */
		MC_112("Minecraft 1.12.x", "1.12"),
		/**
		 * Minecraft 1.11, the exploration update
		 */
		MC_111("Minecraft 1.11.x", "1.11"),
		/**
		 * Minecraft 1.10, the frostburn update
		 */
		MC_110("Minecraft 1.10.x", "1.10"),
		/**
		 * Minecraft 1.9, the combat update
		 */
		MC_19("Minecraft 1.9.x", "1.9"),
		/**
		 * Minecraft 1.8, the bountiful update
		 */
		MC_18("Minecraft 1.8.x", "1.8"),
		/**
		 * Minecraft 1.7, the update that changed the world. No longer supported.
		 */
		MC_17("Minecraft 1.7.x", "1.7", false),
		/**
		 * Minecraft 1.6, the horse update. No longer supported.
		 */
		MC_16("Minecraft 1.6.x", "1.6", false),
		/**
		 * Generic unknown version. Obviously not supported.
		 */
		UNKNOWN("Minecraft 1.x.x", "N/A", false);

		private String name;
		private final String matcher;

		private final boolean isKnown;
		private final boolean isSupported;

		Version(String name, String matcher)
		{
			this(name, matcher, true);
		}
	
		Version(String name, String matcher, boolean isSupported)
		{
			this.name = name;
			this.matcher = matcher;
			this.isKnown = true;
			this.isSupported = isSupported;
		}

		/**
		 * Whether or not SwornAPI explicitly supports this version.
		 * @return True if it does, false if not
		 */
		public boolean isSupported()
		{
			return isKnown && isSupported;
		}

		/**
		 * Whether or not SwornAPI has dropped support for this version.
		 * @return True if it has, false if it hasn't
		 */
		public boolean wasDropped()
		{
			return isKnown && !isSupported;
		}

		@Override
		public String toString()
		{
			return name + (matcher != null ? "[matcher=" + matcher + "]" : "");
		}

		/**
		 * Gets the list of supported Versions
		 * @return The List
		 * @deprecated Use {@code Version.values()}
		 */
		@Deprecated
		public static List<Version> getSupported()
		{
			return Arrays.asList(values());
		}
	}

	private static Version version;

	// Adapted from ProtocolLib

	private static Version fromCurrent()
	{
		String version = extractVersion(Bukkit.getBukkitVersion());
		Version.UNKNOWN.name = "Minecraft " + version;
		return Version.UNKNOWN;
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
			for (Version ver : Version.values())
			{
				if (serverVersion.contains(ver.getMatcher()))
					return version = ver;
			}

			return version = fromCurrent();
		}

		return version;
	}

	/**
	 * For testing use only
	 * 
	 * @param version The new version
	 */
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
	public static boolean isSupported()
	{
		return getVersion().isSupported();
	}
}