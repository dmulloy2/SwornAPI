/**
 * (c) 2016 dmulloy2
 */
package net.dmulloy2.exception;

import lombok.Getter;
import net.dmulloy2.util.FormatUtil;

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

	public static enum Reason
	{
		INPUT,
		SYNTAX,
		VALIDATE,
		;
	}
}