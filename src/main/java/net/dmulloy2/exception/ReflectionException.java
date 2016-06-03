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
package net.dmulloy2.exception;

/**
 * An {@link Exception} that occurs when dealing with reflection.
 *
 * @author dmulloy2
 * @deprecated Use {@link ReflectiveOperationException} instead
 */
@Deprecated
public class ReflectionException extends RuntimeException
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
}
