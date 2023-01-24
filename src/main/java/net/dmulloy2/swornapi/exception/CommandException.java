/**
 * (c) 2016 dmulloy2
 */
package net.dmulloy2.swornapi.exception;

import net.dmulloy2.swornapi.util.FormatUtil;

import lombok.Getter;

/**
 * @author dmulloy2
 */
public class CommandException extends RuntimeException
{
	private static final long serialVersionUID = - 3677819495994061443L;

	private final @Getter Reason reason;

	public CommandException(Reason reason, String message, Object... args)
	{
		super(FormatUtil.format(message, args));
		this.reason = reason;
	}

	public CommandException(Reason reason, Throwable cause, String message, Object... args)
	{
		super(FormatUtil.format(message, args), cause);
		this.reason = reason;
	}

	public CommandException(Reason reason)
	{
		this(reason, "");
	}

	public enum Reason
	{
		/**
		 * Breaks out of command execution without a message
		 */
		BREAK,
		/**
		 * Invalid argument input
		 */
		INPUT,
		/**
		 * Invalid syntax supplied
		 */
		SYNTAX,
		/**
		 * Thrown by checkArgument and checkNotNull
		 */
		VALIDATE
	}
}