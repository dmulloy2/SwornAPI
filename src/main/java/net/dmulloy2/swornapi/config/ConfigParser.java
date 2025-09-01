/**
 * SwornAPI - common API for MineSworn and Shadowvolt plugins
 * Copyright (C) 2015 dmulloy2
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.swornapi.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import net.dmulloy2.swornapi.SwornPlugin;
import net.dmulloy2.swornapi.exception.InvalidItemException;
import net.dmulloy2.swornapi.util.*;

/**
 * Parses a configuration class.
 *
 * @author dmulloy2
 */

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class ConfigParser
{
	public static final int TICKS_PER_SECOND = 20;

	private final SwornPlugin plugin;
	private final FileConfiguration config;
	private final Class<?> clazz;
	private final Object object;

	private ConfigParser(SwornPlugin plugin, FileConfiguration config, Class<?> clazz, Object object)
	{
		this.plugin = plugin;
		this.config = config;
		this.clazz = clazz;
		this.object = object;
	}

	/**
	 * Parses an instance-based configuration.
	 *
	 * @param plugin Plugin instance
	 * @param object Object to parse
	 */
	public static void parse(SwornPlugin plugin, Object object)
	{
		new ConfigParser(plugin, plugin.getConfig(), object.getClass(), object).parse();
	}

	/**
	 * Parses a static-based configuration.
	 *
	 * @param plugin Plugin instance
	 * @param clazz  Configuration class
	 */
	public static void parse(SwornPlugin plugin, Class<?> clazz)
	{
		new ConfigParser(plugin, plugin.getConfig(), clazz, null).parse();
	}

	public static void parse(SwornPlugin plugin, FileConfiguration config, Object object)
	{
		new ConfigParser(plugin, config, object.getClass(), object).parse();
	}

	public static void parse(SwornPlugin plugin, FileConfiguration config, Class<?> clazz)
	{
		new ConfigParser(plugin, config, clazz, null).parse();
	}

	private Object parseValue(Object value, Object def, String path, Field field, TransformValue options)
	{
		return switch (options.value())
		{
			case FORMAT -> FormatUtil.format(value.toString());
			case LIST_LOWER_CASE ->
			{
				var original = (List<String>) value;
				var lower = new ArrayList<>(original.size());
				for (String s : original)
				{
					lower.add(s.toLowerCase());
				}
				yield lower;
			}
			case LIST_UPPER_CASE ->
			{
				var original = (List<String>) value;
				var upper = new ArrayList<>(original.size());
				for (String s : original)
				{
					upper.add(s.toLowerCase());
				}
				yield upper;
			}
			case LOWER_CASE -> value.toString().toLowerCase(Locale.ENGLISH);
			case MINUTE_TO_MILLIS -> TimeUnit.MINUTES.toMillis(NumberUtil.toLong(value));
			case MINUTE_TO_TICKS -> TimeUnit.MINUTES.toSeconds(NumberUtil.toLong(value)) * TICKS_PER_SECOND;
			case PARSE_ENUM -> Enum.valueOf((Class<? extends Enum>) field.getType(), value.toString().toUpperCase().replace(" ", "_").replace(".", "_"));
			case PARSE_LEGACY_ITEM -> ItemUtil.readItem(value.toString(), plugin);
			case PARSE_LEGACY_ITEMS -> ItemUtil.readItems((List<String>) value, plugin);
			case PARSE_ITEM ->
			{
				if (value instanceof String)
				{
					yield ItemUtil.readItem(value.toString(), plugin);
				} else
				{
					ConfigurationSection section = config.getConfigurationSection(path);

					try
					{
						yield ModernItemParser.parseItem(section);
					} catch (InvalidItemException ex)
					{
						plugin.getLogHandler().warn(ex, "Failed to parse item from {0}", path);
						yield def;
					}
				}
			}
			case PARSE_ITEMS ->
			{
				List<?> entries = (List<?>) value;
				List<ItemStack> items = new ArrayList<>();
				for (int i = 0; i < entries.size(); i++)
				{
					Object element = entries.get(i);
					if (element instanceof String)
					{
						ItemStack item = ItemUtil.readItem(element.toString(), plugin);
						if (item != null || options.allowNull())
						{
							items.add(item);
						}
					}
					else
					{
						try
						{
							items.add(ModernItemParser.parseItem((Map<String, Object>) element));
						} catch (InvalidItemException ex)
						{
							plugin.getLogHandler().warn(ex, "Failed to parse item from {0}.{1}", path, i);
						}
					}
				}

				yield items;
			}
			case PARSE_MATERIAL ->
			{
				var material = Material.matchMaterial(value.toString());
				if (material == null && !options.allowNull())
				{
					plugin.getLogHandler().log(Level.WARNING, "Failed to read material \"{0}\" from {1}. Defaulting to {2}", value, path, def);
					yield def;
				} else
				{
					yield material;
				}
			}
			case PARSE_MATERIALS ->
			{
				List<Material> materials = new ArrayList<>();
				for (Object element : (List<Object>) value)
				{
					String string = element.toString();
					Material material = Material.matchMaterial(string);
					if (material == null && !options.allowNull())
					{
						plugin.getLogHandler().log(Level.WARNING, "Failed to read material \"{0}\" from {1}", string, path);
					} else
					{
						materials.add(material);
					}
				}
				yield materials;
			}
			case SECOND_TO_MILLIS -> TimeUnit.SECONDS.toMillis(NumberUtil.toLong(value));
			case SECOND_TO_TICKS -> NumberUtil.toLong(value) * TICKS_PER_SECOND;
		};
	}

	private Object parseRegistry(String path, Object value, TransformRegistry registryTransform)
	{
		RegistryAccess registryAccess = RegistryAccess.registryAccess();
		RegistryKey<? extends Keyed> registryKey = registryTransform.value().getKey();
		Registry<?> registry = registryAccess.getRegistry(registryKey);

		if (value instanceof List<?> list)
		{
			List<Object> entries = new ArrayList<>();
			for (int i = 0; i < list.size(); i++)
			{
				Object entry = list.get(i);
				String keyStr = entry.toString().toLowerCase();
				NamespacedKey keyValue = NamespacedKey.fromString(keyStr);
				if (keyValue == null)
				{
					plugin.getLogHandler().warn("\"{0}\" is not a valid registry key (from {1}.{2})", keyStr, path, i);
					continue;
				}

				Object registryEntry = registry.get(keyValue);
				if (registryEntry == null && !registryTransform.allowNull())
				{
					plugin.getLogHandler().warn("\"{0}\" is not a valid entry in registry {1} (from {2}.{3})", keyValue, registryKey, path, i);
					continue;
				}

				entries.add(registryEntry);
			}

			return entries;
		}
		else
		{
			String keyStr = value.toString().toLowerCase();
			NamespacedKey keyValue = NamespacedKey.fromString(keyStr);
			if (keyValue == null)
			{
				plugin.getLogHandler().warn("\"{0}\" is not a valid registry key (from {1})", keyStr, path);
				return null;
			}

			value = registry.get(keyValue);
			if (value == null && !registryTransform.allowNull())
			{
				plugin.getLogHandler().warn("\"{0}\" is not a valid entry in registry {1} (from {2})", keyValue, registryKey, path);
				return null;
			}

			return value;
		}
	}

	private void parseField(Field field)
	{
		if (!field.canAccess(object)) field.setAccessible(true);

		Object def = null;

		try
		{
			def = field.get(object);
		} catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "accessing field {0}", field));
		}

		Key key = field.getAnnotation(Key.class);
		if (key == null)
		{
			return;
		}

		String path = key.value();
		Object value = def;

		try
		{
			value = config.get(path);
			if (value == null)
			{
				return;
			}

			TransformRegistry registryTransform = field.getAnnotation(TransformRegistry.class);
			TransformValue standardTransform = field.getAnnotation(TransformValue.class);

			if (registryTransform != null)
			{
				value = parseRegistry(path, value, registryTransform);
			}
			else if (standardTransform != null)
			{
				value = parseValue(value, def, path, field, standardTransform);

				for (Class<?> custom : standardTransform.custom())
				{
					Method convert = custom.getMethod("convert", Object.class);
					if (convert.canAccess(null))
					{
						try
						{
							value = convert.invoke(null, value);
						} catch (Throwable ex)
						{
							plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "converting {0} using {1}", path, custom.getName()));
						}
					}
				}
			}

			try
			{
				field.set(object, value);
			} catch (IllegalArgumentException ex)
			{
				plugin.getLogHandler().log(Level.WARNING, "\"{0}\" is the wrong type: expected {1}, but got {2}", path, field.getType(), value.getClass().getName());
				plugin.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "setting {0} to {1}", field, value));
			}
		} catch (ClassCastException ex)
		{
			plugin.getLogHandler().log(Level.WARNING, "\"{0}\" is the wrong type: expected {1}, but got {2}", path, field.getType(), value.getClass().getName());
			plugin.getLogHandler().debug(Level.WARNING, Util.getUsefulStack(ex, "setting {0} to {1}", field, value));
		} catch (Throwable ex)
		{
			plugin.getLogHandler().log(Level.SEVERE, Util.getUsefulStack(ex, "loading value from {0}", path));
		}
	}

	private void parse()
	{
		for (Field field : clazz.getDeclaredFields())
		{
			parseField(field);
		}
	}
}
