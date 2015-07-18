/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.config;

/**
 * Represents transformations done to config values.
 * @author dmulloy2
 */

public @interface ValueOptions
{
	/**
	 * An array of standard value options.
	 * @return Standard value options
	 */
	ValueOption[] value();

	/**
	 * An array of custom value options. Options provided here must have a
	 * static method, <code>public static Object convert(Object)</code>, but
	 * this is unenforcable due to how Java handles annotations.
	 * @return Custom value options
	 */
	Class<?>[] customOptions() default {};

	/**
	 * Represents a standard value option.
	 * @author dmulloy2
	 */
	public static enum ValueOption
	{
		LOWER_CASE,
		FORMAT,
		LIST_LOWER_CASE,
		PARSE_ITEM,
		PARSE_ITEMS,
		MINUTE_TO_MILLIS,
		SECOND_TO_MILLIS,
		;
	}
}
