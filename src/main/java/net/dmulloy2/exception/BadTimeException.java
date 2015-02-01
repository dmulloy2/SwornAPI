/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.exception;

/**
 * An Exception that results from bad time.
 *
 * @author dmulloy2
 */

public class BadTimeException extends RuntimeException
{
	private static final long serialVersionUID = 5846361750537427952L;

	/**
	 * Constructs an empty BadTimeException.
	 */
	public BadTimeException()
	{
		super();
	}

	/**
	 * Constructs a BadTimeException with a given message.
	 *
	 * @param message Exception message
	 */
	public BadTimeException(String message)
	{
		super(message);
	}

	/**
	 * Constructs a BadTimeException with a given message and cause.
	 *
	 * @param message Exception message
	 * @param cause {@link Throwable} cause
	 */
	public BadTimeException(String message, Throwable cause)
	{
		super(message, cause);
	}
}