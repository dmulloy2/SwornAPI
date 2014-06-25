/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.handlers;

import net.dmulloy2.types.Reloadable;

/**
 * @author dmulloy2
 */

public abstract class IntegrationHandler implements Reloadable
{
	public abstract void setup();

	@Override
	public void reload()
	{
		//
	}
}