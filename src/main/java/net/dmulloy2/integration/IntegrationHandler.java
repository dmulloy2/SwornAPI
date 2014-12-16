/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.integration;

import net.dmulloy2.types.Reloadable;

/**
 * @author dmulloy2
 */

@Deprecated
public abstract class IntegrationHandler implements Reloadable
{
	public abstract void setup();

	public abstract boolean isEnabled();

	@Override
	public void reload()
	{
		//
	}
}