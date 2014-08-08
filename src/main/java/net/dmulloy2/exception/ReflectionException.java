/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.exception;

/**
 * An {@link Exception} that occurs when dealing with reflection.
 *
 * @author dmulloy2
 */

public class ReflectionException extends Exception
{
	private static final long serialVersionUID = -355857662220280587L;

	/**
	 * Constructs an empty ReflectionException.
	 */
	public ReflectionException()
	{
		super();
	}

	/**
	 * Constructs a ReflectionException with a given message.
	 *
	 * @param message Exception message
	 */
	public ReflectionException(String message)
	{
		super(message);
	}

	/**
	 * Constructs a ReflectionException with a given message and cause.
	 *
	 * @param message Exception message
	 * @param cause {@link Throwable} cause
	 */
	public ReflectionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public static ReflectionException fromThrowable(String message, Throwable ex)
	{
		if (ex instanceof ReflectionException)
			return (ReflectionException) ex;

		return new ReflectionException(message, ex);
	}
}