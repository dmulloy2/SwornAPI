/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a key in a standard YAML configuration.
 * @author dmulloy2
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Key
{
	/**
	 * This key's path, in standard YAML format
	 * @return This key's path
	 */
	String value();
}
