/**
 * SwornAPI - common API for MineSworn and Shadowvolt plugins
 * Copyright (C) 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.swornapi.exception;

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

	@Override
	public String toString()
	{
		Throwable cause = getCause();
		return cause != null ? cause.toString() : getMessage();
	}
}
