/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.types;

import java.util.List;

import net.dmulloy2.util.ListUtil;

/**
 * Allows the transformation of one object into another.
 *
 * @author dmulloy2
 * @see {@link ListUtil#transform(List, Transformation)}
 */
public interface Transformation<O, T>
{
	/**
	 * Transforms one object into another.
	 *
	 * @param object Object to transform
	 * @return The transformed object
	 */
	public T transform(O object);
}