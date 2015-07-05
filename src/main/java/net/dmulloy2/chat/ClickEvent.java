/**
 * Copyright (c) 2012, md_5. All rights reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.dmulloy2.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a clickable action.
 * 
 * @author md_5
 */

@Getter
@AllArgsConstructor
final public class ClickEvent
{
	/**
	 * The type of action to preform on click
	 */
	private final Action action;

	/**
	 * Depends on action
	 *
	 * @see Action
	 */
	private final String value;

	public enum Action
	{
		/**
		 * Open a url at the path given by {@code getValue()}
		 */
		OPEN_URL,

		/**
		 * Open a file at the path given by {@code getValue()}
		 */
		OPEN_FILE,

		/**
		 * Run the command given by {@code getValue()}
		 */
		RUN_COMMAND,

		/**
		 * Inserts the string given by {@code getValue()} into the player's text
		 * box
		 */
		SUGGEST_COMMAND
	}

	@Override
	public String toString()
	{
		return String.format("ClickEvent{action=%s, value=%s}", action, value);
	}
}
