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
package net.dmulloy2.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author dmulloy2
 */

public class FileResourceLoader extends ClassLoader
{
	private transient final File dataFolder;
	public FileResourceLoader(ClassLoader classLoader, JavaPlugin plugin)
	{
		super(classLoader);
		this.dataFolder = plugin.getDataFolder();
	}

	@Override
	public URL getResource(String string)
	{
		File file = new File(dataFolder, string);
		if (file.exists())
		{
			try
			{
				return file.toURI().toURL();
			} catch (Throwable ignored) { }
		}

		return super.getResource(string);
	}

	@Override
	public InputStream getResourceAsStream(String string)
	{
		File file = new File(dataFolder, string);
		if (file.exists())
		{
			try
			{
				return new FileInputStream(file);
			} catch (Throwable ignored) { }
		}

		return super.getResourceAsStream(string);
	}
}
