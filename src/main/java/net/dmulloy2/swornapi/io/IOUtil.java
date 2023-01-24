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
package net.dmulloy2.swornapi.io;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

/**
 * Util for dealing with IO stuff
 *
 * @author dmulloy2
 */

public class IOUtil
{
	private IOUtil() { }

	/**
	 * Parses the lines of a given file in the proper order.
	 *
	 * @param file File to parse
	 * @return The lines
	 * @throws IOException If parsing fails
	 */
	public static List<String> readLines(File file) throws IOException
	{
		Validate.notNull(file, "file cannot be null!");

		try (Closer closer = new Closer())
		{
			FileInputStream fis = closer.register(new FileInputStream(file));
			DataInputStream dis = closer.register(new DataInputStream(fis));
			InputStreamReader isr = closer.register(new InputStreamReader(dis));
			BufferedReader br = closer.register(new BufferedReader(isr));

			List<String> lines = new ArrayList<>();

			String line;
			while ((line = br.readLine()) != null)
				lines.add(line);

			return lines;
		}
	}

	/**
	 * Writes given lines to a given file in the proper order.
	 *
	 * @param file File to write to
	 * @param lines Lines to write
	 * @throws IOException If writing fails
	 */
	public static void writeLines(File file, List<String> lines) throws IOException
	{
		Validate.notNull(file, "file cannot be null!");
		Validate.notNull(lines, "lines cannot be null!");

		try (Closer closer = new Closer())
		{
			FileWriter fw = closer.register(new FileWriter(file));
			PrintWriter pw = closer.register(new PrintWriter(fw));

			for (String line : lines)
				pw.println(line);
		}
	}

	/**
	 * Returns the given {@link File}'s name with the extension omitted.
	 *
	 * @param file {@link File}
	 * @param extension File extension
	 * @return The file's name with the extension omitted
	 */
	public static String trimFileExtension(File file, String extension)
	{
		Validate.notNull(file, "file cannot be null!");
		Validate.notNull(extension, "extension cannot be null!");

		int index = file.getName().lastIndexOf(extension);
		return index > 0 ? file.getName().substring(0, index) : file.getName();
	}
}
