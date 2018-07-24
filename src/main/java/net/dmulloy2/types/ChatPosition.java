/**
 * SwornAPI - common API for MineSworn and Shadowvolt plugins
 * Copyright (C) 2016 dmulloy2
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
package net.dmulloy2.types;

import lombok.Getter;

/**
 * Represents the currently supported chat positions.
 * @author dmulloy2
 */

@Getter
public enum ChatPosition
{
	/**
	 * Player chat (in chat box)
	 */
	CHAT(0),

	/**
	 * System messages (in chat box)
	 */
	SYSTEM(1),

	/**
	 * Action bar (above hotbar)
	 */
	ACTION_BAR(2),
	;

	private final byte value;

	ChatPosition(int value)
	{
		this.value = (byte) value;
	}
}
