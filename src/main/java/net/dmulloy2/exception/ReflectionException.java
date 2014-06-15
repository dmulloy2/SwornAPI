/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.exception;

/**
 * @author dmulloy2
 */

public class ReflectionException extends Exception
{
	private static final long serialVersionUID = - 355857662220280587L;

	public ReflectionException(String message)
	{
		super(message);
	}

	public ReflectionException(String message, Throwable cause)
	{
		super(message, cause);
	}
}