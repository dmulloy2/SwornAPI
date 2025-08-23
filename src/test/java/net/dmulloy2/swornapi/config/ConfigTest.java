/**
 * (c) 2016 dmulloy2
 */
package net.dmulloy2.swornapi.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;

import net.dmulloy2.swornapi.BukkitTesting;
import net.dmulloy2.swornapi.SwornPlugin;
import net.dmulloy2.swornapi.handlers.LogHandler;
import net.dmulloy2.swornapi.util.ListUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/**
 * @author dmulloy2
 */

public class ConfigTest
{
	@BeforeAll
	public static void beforeClass() throws Throwable
	{
		BukkitTesting.initializeAll();

		SwornPlugin plugin = mock(SwornPlugin.class);
		when(plugin.getLogHandler()).thenReturn(new LogHandler(plugin, Logger.getGlobal()));

		// Mock the config
		YamlConfiguration config = new YamlConfiguration();
		InputStream stream = Resources.getResource("config.yml").openStream();
		InputStreamReader reader = new InputStreamReader(stream, Charsets.UTF_8);
		config.load(reader);

		when(plugin.getConfig()).thenReturn(config);

		ConfigParser.parse(plugin, Config.class);
	}

	@Test
	public void testStrings()
	{
		assertEquals("succeeded", Config.string);
	}

	@Test
	public void testMaterial()
	{
		assertEquals(Material.GOLDEN_APPLE, Config.material);
	}

	@Test
	public void testMaterials()
	{
		assertEquals(Config.materials, ListUtil.toList(Material.GOLDEN_APPLE, Material.DIAMOND_BLOCK));
	}

	@Test
	public void testItems()
	{
		assertNotNull(Config.items);
		assertEquals(4, Config.items.size());
		
		ItemStack sword = Config.items.get(0);
		assertEquals(Material.DIAMOND_SWORD, sword.getType());
		assertEquals(1, sword.getAmount());
		assertTrue(sword.getEnchantments().containsKey(Enchantment.SHARPNESS));
		assertTrue(sword.getEnchantments().containsKey(Enchantment.UNBREAKING));
		assertTrue(sword.getEnchantments().containsKey(Enchantment.FIRE_ASPECT));
		assertNotNull(sword.getItemMeta());
		assertEquals(sword.getItemMeta().getDisplayName(), ChatColor.DARK_RED.toString() + ChatColor.BOLD.toString() + "Oathbreaker");

		ItemStack apple = Config.items.get(1);
		assertEquals(Material.GOLDEN_APPLE, apple.getType());
		assertEquals(2, apple.getAmount());

		ItemStack flint = Config.items.get(2);
		assertEquals(Material.FLINT_AND_STEEL, flint.getType());
		assertEquals(1, flint.getAmount());

		ItemStack potion = Config.items.get(3);
		assertEquals(Material.POTION, potion.getType());
		assertEquals(2, potion.getAmount());
		assertNotNull(potion.getItemMeta());

		List<PotionEffect> effects = ((PotionMeta) potion.getItemMeta()).getCustomEffects();
		assertEquals(1, effects.size());

		PotionEffect data = effects.get(0);
		assertEquals(PotionEffectType.SPEED, data.getType());
		assertEquals(0, data.getAmplifier());
		assertEquals(9600, data.getDuration());
	}

	private static class Config
	{
		@Key("string")
		public static String string = "failed";

		@Key("material")
		@ValueOptions(ValueOptions.ValueOption.PARSE_MATERIAL)
		public static Material material = Material.ROTTEN_FLESH;

		@Key("materials")
		@ValueOptions(ValueOptions.ValueOption.PARSE_MATERIALS)
		public static List<Material> materials = ListUtil.toList(Material.ROTTEN_FLESH);
	
		@Key("items")
		@ValueOptions(ValueOptions.ValueOption.PARSE_ITEMS)
		public static List<ItemStack> items = ListUtil.toList(new ItemStack(Material.ROTTEN_FLESH));
	}
}