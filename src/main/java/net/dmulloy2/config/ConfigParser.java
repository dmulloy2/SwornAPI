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
package net.dmulloy2.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.config.ValueOptions.ValueOption;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.ItemUtil;
import net.dmulloy2.util.NumberUtil;
import net.dmulloy2.util.Util;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Parses a configuration class.
 * 
 * @author dmulloy2
 */

public final class ConfigParser
{
	/**
	 * Parses an instance-based configuration.
	 * 
	 * @param plugin Plugin instance
	 * @param object Object to parse
	 */
	public static void parse(SwornPlugin plugin, Object object)
	{
		parse(plugin, object.getClass(), object);
	}

	/**
	 * Parses a static-based configuration.
	 * 
	 * @param plugin Plugin instance
	 * @param clazz Configuration class
	 */
	public static void parse(SwornPlugin plugin, Class<?> clazz)
	{
		parse(plugin, clazz, null);
	}

	@SuppressWarnings("unchecked")
	private static void parse(SwornPlugin plugin, Class<?> clazz, Object object)
	{
		FileConfiguration config = plugin.getConfig();

		for (Field field : clazz.getDeclaredFields())
		{
			if (! field.isAccessible())
				field.setAccessible(true);

			Key key = field.getAnnotation(Key.class);
			if (key != null)
			{
				String path = key.value();

				try
				{
					Object value = config.get(path);
					if (value != null)
					{
						ValueOptions options = field.getAnnotation(ValueOptions.class);
						if (options != null)
						{
							for (ValueOption option : options.value())
							{
								switch (option)
								{
									case FORMAT:
										value = FormatUtil.format(value.toString());
										break;
									case LIST_LOWER_CASE:
										List<String> list = new ArrayList<>();
										for (String line : (List<String>) value)
											list.add(line.toLowerCase());
										value = list;
										break;
									case LOWER_CASE:
										value = value.toString().toLowerCase();
										break;
									case MINUTE_TO_MILLIS:
										value = TimeUnit.MINUTES.toMillis(NumberUtil.toLong(value));
										break;
									case PARSE_ITEM:
										value = ItemUtil.readItem(value.toString(), plugin);
										break;
									case PARSE_ITEMS:
										value = ItemUtil.readItems((List<String>) value, plugin);
										break;
									case SECOND_TO_MILLIS:
										value = TimeUnit.SECONDS.toMillis(NumberUtil.toLong(value));
										break;
									default:
										throw new IllegalArgumentException("Unsupported option: " + option);
								}
							}

							for (Class<?> custom : options.custom())
							{
								Method convert = custom.getMethod("convert", Object.class);
								if (convert.isAccessible())
								{
									value = convert.invoke(null, value);
								}
							}
						}

						field.set(object, value);
					}
				}
				catch (Throwable ex)
				{
					plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "loading value from {0}", path));
				}
			}
		}
	}
}
