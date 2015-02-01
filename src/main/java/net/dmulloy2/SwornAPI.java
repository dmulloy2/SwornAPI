/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2;

import net.dmulloy2.types.LazyLocation;
import net.dmulloy2.types.SimpleVector;

import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * SwornAPI utility class
 * 
 * @author dmulloy2
 */

public class SwornAPI
{
	private static boolean registered = false;
	public static final void checkRegistrations()
	{
		if (! registered)
		{
			ConfigurationSerialization.registerClass(LazyLocation.class);
			ConfigurationSerialization.registerClass(SimpleVector.class);
			registered = true;
		}
	}
}