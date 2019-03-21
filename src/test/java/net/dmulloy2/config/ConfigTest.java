/**
 * (c) 2016 dmulloy2
 */
package net.dmulloy2.config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;

import net.dmulloy2.BukkitTesting;
import net.dmulloy2.SwornPlugin;
import net.dmulloy2.config.ValueOptions.ValueOption;
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.util.ListUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/**
 * @author dmulloy2
 */

public class ConfigTest
{
	@BeforeClass
	public static void beforeClass() throws Throwable
	{
		BukkitTesting.prepare();

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
		assertEquals(Config.string, "succeeded");
	}

	@Test
	public void testMaterial()
	{
		assertEquals(Config.material, Material.GOLDEN_APPLE);
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
		assertEquals(Config.items.size(), 4);
		
		ItemStack sword = Config.items.get(0);
		assertEquals(sword.getType(), Material.DIAMOND_SWORD);
		assertEquals(sword.getAmount(), 1);
		assertTrue(sword.getEnchantments().containsKey(Enchantment.DAMAGE_ALL));
		assertTrue(sword.getEnchantments().containsKey(Enchantment.DURABILITY));
		assertTrue(sword.getEnchantments().containsKey(Enchantment.FIRE_ASPECT));
		assertNotNull(sword.getItemMeta());
		assertEquals(sword.getItemMeta().getDisplayName(), ChatColor.DARK_RED.toString() + ChatColor.BOLD.toString() + "Oathbreaker");

		ItemStack apple = Config.items.get(1);
		assertEquals(apple.getType(), Material.GOLDEN_APPLE);
		assertEquals(apple.getAmount(), 2);

		ItemStack flint = Config.items.get(2);
		assertEquals(flint.getType(), Material.FLINT_AND_STEEL);
		assertEquals(flint.getAmount(), 1);

		ItemStack potion = Config.items.get(3);
		assertEquals(potion.getType(), Material.POTION);
		assertEquals(potion.getAmount(), 2);
		assertNotNull(potion.getItemMeta());

		PotionData data = ((PotionMeta) potion.getItemMeta()).getBasePotionData();
		assertEquals(data.getType(), PotionType.SPEED);
		assertFalse(data.isUpgraded());
		assertTrue(data.isExtended());
	}

	private static class Config
	{
		@Key("string")
		public static String string = "failed";

		@Key("material")
		@ValueOptions(ValueOption.PARSE_MATERIAL)
		public static Material material = Material.ROTTEN_FLESH;

		@Key("materials")
		@ValueOptions(ValueOption.PARSE_MATERIALS)
		public static List<Material> materials = ListUtil.toList(Material.ROTTEN_FLESH);
	
		@Key("items")
		@ValueOptions(ValueOption.PARSE_ITEMS)
		public static List<ItemStack> items = ListUtil.toList(new ItemStack(Material.ROTTEN_FLESH));
	}
}