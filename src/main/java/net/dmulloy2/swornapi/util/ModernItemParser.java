package net.dmulloy2.swornapi.util;

import java.util.HashMap;
import java.util.Map;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import net.dmulloy2.swornapi.exception.InvalidItemException;

public final class ModernItemParser
{
	private ModernItemParser() {}

	public static ItemStack parseItem(ConfigurationSection section) throws InvalidItemException
	{
		return parseItem(toNestedMap(section));
	}

	@SuppressWarnings("unchecked")
	public static ItemStack parseItem(Map<String, Object> section) throws InvalidItemException
	{
		String typeName = (String) section.get("type");
		if (typeName == null)
			throw new InvalidItemException("Missing material type");

		NamespacedKey typeKey = NamespacedKey.fromString(typeName);
		if (typeKey == null)
			throw new InvalidItemException("Invalid material type " + typeName);

		Material type = Registry.MATERIAL.get(typeKey);
		if (type == null || type == Material.AIR)
			throw new InvalidItemException("Invalid material type" + typeName);

		int amount = (int) section.getOrDefault("amount", 1);
		ItemStack item = ItemStack.of(type, amount);

		if (section.containsKey("enchants"))
		{
			Map<String, Object> enchantments = (Map<String, Object>) section.get("enchants");
			Registry<Enchantment> enchantRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);

			for (Map.Entry<String, Object> entry : enchantments.entrySet())
			{
				String enchantment = entry.getKey();
				int level = (int) entry.getValue();

				NamespacedKey enchKey = NamespacedKey.fromString(enchantment);
				if (enchKey == null)
					throw new InvalidItemException("Invalid enchantment " + enchantment);

				Enchantment ench = enchantRegistry.get(enchKey);
				if (ench != null)
					item.addUnsafeEnchantment(ench, level);
				else
					throw new InvalidItemException("Invalid enchantment " + enchantment);
			}
		}
		else if (section.containsKey("meta"))
		{
			try
			{
				Map<String, Object> metaData = (Map<String, Object>) section.get("meta");
				if (!metaData.containsKey("meta-type"))
					metaData.put("meta-type", "UNSPECIFIC");

				item.setItemMeta(SerializableMeta.deserialize(metaData));
			}
			catch (Throwable ex)
			{
				ex.printStackTrace();
				throw new InvalidItemException("Invalid item meta");
			}
		}

		return item;
	}

	public static Map<String, Object> toNestedMap(ConfigurationSection section)
	{
		Map<String, Object> result = new HashMap<>();
		for (String key : section.getKeys(false))
		{
			Object value = section.get(key);
			if (value instanceof ConfigurationSection sub)
			{
				result.put(key, toNestedMap(sub));
			}
			else
			{
				result.put(key, value);
			}
		}

		return result;
	}

	public static Map<String, Object> serializeItem(ItemStack item)
	{
		Map<String, Object> map = new HashMap<>();
		map.put("type", item.getType().getKey().toString());
		map.put("amount", item.getAmount());

		Map<String, Object> metaMap = item.getItemMeta().serialize();
		String metaType = (String) metaMap.get("meta-type");
		if (metaType == null || metaType.equals("UNSPECIFIC"))
		{
			if (!item.getEnchantments().isEmpty())
			{
				Map<String, Integer> enchantments = new HashMap<>();
				item.getEnchantments().forEach((ench, level) -> enchantments.put(ench.getKey().toString(), level));
				map.put("enchants", enchantments);
			}
		}
		else
		{
			map.put("meta", metaMap);
		}

		return map;
	}
}
