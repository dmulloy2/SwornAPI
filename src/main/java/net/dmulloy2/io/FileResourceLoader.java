/**
 * (c) 2014 dmulloy2
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
			} catch (Throwable ex) { }
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
			} catch (Throwable ex) { }
		}

		return super.getResourceAsStream(string);
	}
}