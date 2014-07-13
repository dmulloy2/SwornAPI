/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.types;

/**
 * Represents various visibilities for commands
 *
 * @author dmulloy2
 */

public enum CommandVisibility
{
	/**
	 * Visible to everyone
	 */
	ALL,

	/**
	 * Visible to players with a permission
	 */
	PERMISSION,

	/**
	 * Visible to operators
	 */
	OPS,

	/**
	 * Invisible
	 */
	NONE;


}