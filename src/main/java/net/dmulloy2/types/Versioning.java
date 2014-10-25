/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.types;

import lombok.AllArgsConstructor;
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
	@AllArgsConstructor
	public static enum Version
	{
		UNKNOWN("Unknown"),
		MC_16("Minecraft 1.6.x"),
		MC_17("Minecraft 1.7.x"),
		MC_18("Minecraft 1.8.x"),
		;

		private final String name;
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
			String versionString = Bukkit.getVersion();
			if (versionString.contains("1.8"))
				version = Version.MC_18;
			else if (versionString.contains("1.7"))
				version = Version.MC_17;
			else if (versionString.contains("1.6"))
				version = Version.MC_16;
			else
				version = Version.UNKNOWN;
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