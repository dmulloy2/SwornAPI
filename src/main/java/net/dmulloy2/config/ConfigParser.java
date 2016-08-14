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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import net.dmulloy2.SwornPlugin;
import net.dmulloy2.config.ValueOptions.ValueOption;
import net.dmulloy2.types.MyMaterial;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.ItemUtil;
import net.dmulloy2.util.MaterialUtil;
import net.dmulloy2.util.NumberUtil;
import net.dmulloy2.util.Util;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Parses a configuration class.
 * 
 * @author dmulloy2
 */

public final class ConfigParser
{
	public static final int TICKS_PER_SECOND = 20;
	
	/**
	 * Parses an instance-based configuration.
	 * 
	 * @param plugin Plugin instance
	 * @param object Object to parse
	 */
	public static void parse(SwornPlugin plugin, Object object)
	{
		parse(plugin, plugin.getConfig(), object.getClass(), object);
	}

	/**
	 * Parses a static-based configuration.
	 * 
	 * @param plugin Plugin instance
	 * @param clazz Configuration class
	 */
	public static void parse(SwornPlugin plugin, Class<?> clazz)
	{
		parse(plugin, plugin.getConfig(), clazz, null);
	}

	public static void parse(SwornPlugin plugin, FileConfiguration config, Object object)
	{
		parse(plugin, config, object.getClass(), object);
	}

	public static void parse(SwornPlugin plugin, FileConfiguration config, Class<?> clazz)
	{
		parse(plugin, config, clazz, null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void parse(SwornPlugin plugin, FileConfiguration config, Class<?> clazz, Object object)
	{
		for (Field field : clazz.getDeclaredFields())
		{
			if (! field.isAccessible())
				field.setAccessible(true);

			Object def = null;

			try
			{
				def = field.get(object);
			}
			catch (Throwable ex)
			{
				plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "accessing field {0}", field));
			}

			Key key = field.getAnnotation(Key.class);
			if (key != null)
			{
				String path = key.value();
				Object value = null;

				try
				{
					value = config.get(path);
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
										List<String> original = (List<String>) value;
										List<String> lower = new ArrayList<>(original.size());
										Iterator<String> iter = original.iterator();
										while (iter.hasNext())
										{
											lower.add(iter.next().toLowerCase());
											iter.remove();
										}

										value = lower;
										break;
									case LIST_UPPER_CASE:
										original = (List<String>) value;
										List<String> upper = new ArrayList<>(original.size());
										iter = original.iterator();
										while (iter.hasNext())
										{
											upper.add(iter.next().toLowerCase());
											iter.remove();
										}

										value = upper;
										break;
									case LOWER_CASE:
										value = value.toString().toLowerCase();
										break;
									case MINUTE_TO_MILLIS:
										value = TimeUnit.MINUTES.toMillis(NumberUtil.toLong(value));
										break;
									case MINUTE_TO_TICKS:
										value = TimeUnit.MINUTES.toSeconds(NumberUtil.toLong(value)) * TICKS_PER_SECOND;
										break;
									case PARSE_ENUM:
										value = Enum.valueOf((Class<? extends Enum>) field.getType(), value.toString().toUpperCase().replace(" ", "_").replace(".", "_"));
										break;
									// Item parsing handles null values on its own
									case PARSE_ITEM:
										value = ItemUtil.readItem(value.toString(), plugin);
										break;
									case PARSE_ITEMS:
										value = ItemUtil.readItems((List<String>) value, plugin);
										break;
									// Check for nulls with materials
									case PARSE_MATERIAL:
										value = MaterialUtil.getMaterial(value.toString());
										if (value == null && ! options.allowNull())
										{
											plugin.getLogHandler().log(Level.WARNING, "Failed to read material \"{0}\" from {1}. Defaulting to {2}", value, path, def);
											value = def;
										}

										break;
									case PARSE_MATERIALS:
										List<Material> materials = new ArrayList<>();
										for (Object element : (List<Object>) value)
										{
											String string = element.toString();
											Material material = MaterialUtil.getMaterial(string);
											if (material == null && ! options.allowNull())
											{
												plugin.getLogHandler().log(Level.WARNING, "Failed to read material \"{0}\" from {1}", string, path);
											}
											else
											{
												materials.add(material);
											}
										}

										value = materials;
										break;
									case PARSE_MY_MATERIAL:
										value = MyMaterial.fromString(value.toString());
										if (value == null && ! options.allowNull())
										{
											plugin.getLogHandler().log(Level.WARNING, "Failed to read MyMaterial \"{0}\" from {1}. Defaulting to {2}", value, path, def);
											value = def;
										}
									case SECOND_TO_MILLIS:
										value = TimeUnit.SECONDS.toMillis(NumberUtil.toLong(value));
										break;
									case SECOND_TO_TICKS:
										value = NumberUtil.toLong(value) * TICKS_PER_SECOND;
										break;
									default:
										throw new IllegalStateException("Unsupported option: " + option);
								}
							}

							for (Class<?> custom : options.custom())
							{
								Method convert = custom.getMethod("convert", Object.class);
								if (convert.isAccessible())
								{
									try
									{
										value = convert.invoke(null, value);
									}
									catch (Throwable ex)
									{
										plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "converting {0} using {1}", path, custom.getName()));
									}
								}
							}
						}

						try
						{
							field.set(object, value);
						}
						catch (IllegalArgumentException ex)
						{
							plugin.getLogHandler().log(Level.WARNING, "\"{0}\" is the wrong type: expected {1}, but got {2}", path, field.getType(), value.getClass().getName());
							plugin.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "setting {0} to {1}", field, value));
						}
					}
				}
				catch (ClassCastException ex)
				{
					plugin.getLogHandler().log(Level.WARNING, "\"{0}\" is the wrong type: expected {1}, but got {2}", path, field.getType(), value.getClass().getName());
					plugin.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "setting {0} to {1}", field, value));
				}
				catch (Throwable ex)
				{
					plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "loading value from {0}", path));
				}
			}
		}
	}
}