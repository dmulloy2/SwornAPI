/**
 * (c) 2016 dmulloy2
 */
package net.dmulloy2.config;

import static org.junit.Assert.assertEquals;
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

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
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
		assertEquals(Config.item, new ItemStack(Material.GOLDEN_APPLE, 5));
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
	
		@Key("item")
		@ValueOptions(ValueOption.PARSE_ITEM)
		public static ItemStack item = new ItemStack(Material.ROTTEN_FLESH);
	}
}