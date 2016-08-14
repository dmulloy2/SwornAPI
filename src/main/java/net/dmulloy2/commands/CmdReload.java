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
package net.dmulloy2.commands;

import net.dmulloy2.SwornPlugin;

/**
 * Generic plugin reload command
 * @author dmulloy2
 * 
 * @see SwornPlugin#reload()
 * @see CommandProps#setReloadPerm(IPermission)
 */
public class CmdReload extends Command
{
	public CmdReload(SwornPlugin plugin)
	{
		super(plugin);
		this.name = "reload";
		this.description = "Reload " + plugin.getName();
		this.permission = props.getReloadPerm();
		this.usesPrefix = true;
	}

	@Override
	public void perform()
	{
		long start = System.currentTimeMillis();
		
		if (isPlayer())
			plugin.getLogHandler().log("{0} is reloading the plugin", player.getName());
		sendpMessage("Reloading &b{0}&e...", plugin.getName());

		plugin.reload();

		if (isPlayer())
			plugin.getLogHandler().log("{0} reloaded the plugin. Took {1} ms",
					player.getName(), System.currentTimeMillis() - start);
		sendpMessage("Reload complete! Took &b{0} &ems!", System.currentTimeMillis() - start);
	}
}