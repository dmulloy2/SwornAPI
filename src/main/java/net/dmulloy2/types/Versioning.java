/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.types;

import org.bukkit.Bukkit;

/**
 * @author dmulloy2
 */

public class Versioning
{
	private Versioning() { }

	public static enum Version
	{
		MC_18,
		MC_17,
		MC_16,
		UNKNOWN,
		;
	}

	private static Version version;
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

	private static final Version SUPPORTED = Version.MC_17;
	public static final Version getSupportedVersion()
	{
		return SUPPORTED;
	}

	public static final boolean isSupported()
	{
		return getVersion() == SUPPORTED;
	}
}