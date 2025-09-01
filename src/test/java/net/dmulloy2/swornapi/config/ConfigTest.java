/**
 * (c) 2016 dmulloy2
 */
package net.dmulloy2.swornapi.config;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.dmulloy2.swornapi.BukkitTesting;
import net.dmulloy2.swornapi.SwornPlugin;
import net.dmulloy2.swornapi.handlers.LogHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
	public void testLegacyItems()
	{
		assertNotNull(Config.legacyItems);
		assertEquals(4, Config.legacyItems.size());
		
		ItemStack sword = Config.legacyItems.get(0);
		assertEquals(Material.DIAMOND_SWORD, sword.getType());
		assertEquals(1, sword.getAmount());
		assertTrue(sword.getEnchantments().containsKey(Enchantment.SHARPNESS));
		assertTrue(sword.getEnchantments().containsKey(Enchantment.UNBREAKING));
		assertTrue(sword.getEnchantments().containsKey(Enchantment.FIRE_ASPECT));
		assertNotNull(sword.getItemMeta());
		assertEquals(sword.getItemMeta().getDisplayName(), ChatColor.DARK_RED.toString() + ChatColor.BOLD.toString() + "Oathbreaker");

		ItemStack apple = Config.legacyItems.get(1);
		assertEquals(Material.GOLDEN_APPLE, apple.getType());
		assertEquals(2, apple.getAmount());

		ItemStack flint = Config.legacyItems.get(2);
		assertEquals(Material.FLINT_AND_STEEL, flint.getType());
		assertEquals(1, flint.getAmount());

		ItemStack potion = Config.legacyItems.get(3);
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

	@Test
	public void testModernItems()
	{
		assertNotNull(Config.modernItems);
		assertEquals(4, Config.modernItems.size());

		ItemStack sword = Config.modernItems.get(0);
		assertEquals(Material.DIAMOND_SWORD, sword.getType());
		assertEquals(1, sword.getAmount());
		assertEquals(3, sword.getEnchantments().size());
		assertEquals(5, sword.getEnchantmentLevel(Enchantment.SHARPNESS));
		assertEquals(3, sword.getEnchantmentLevel(Enchantment.UNBREAKING));
		assertEquals(2, sword.getEnchantmentLevel(Enchantment.FIRE_ASPECT));

		ItemStack goldSword = Config.modernItems.get(1);
		assertEquals(Material.GOLDEN_SWORD, goldSword.getType());
		assertEquals(1, goldSword.getAmount());
		assertTrue(goldSword.hasItemMeta());
		// assertEquals(NamedTextColor.GOLD, goldSword.displayName().color());
		// assertTrue(goldSword.displayName().style().hasDecoration(TextDecoration.BOLD));
		// assertEquals("Lightbringer", ((TextComponent) goldSword.displayName()).content());
		assertEquals(1, goldSword.getEnchantments().size());
		assertEquals(3, goldSword.getEnchantmentLevel(Enchantment.UNBREAKING));

		ItemStack apple = Config.modernItems.get(2);
		assertEquals(Material.GOLDEN_APPLE, apple.getType());
		assertEquals(2, apple.getAmount());

		ItemStack flint = Config.modernItems.get(3);
		assertEquals(Material.FLINT_AND_STEEL, flint.getType());
		assertEquals(1, flint.getAmount());
	}

	@Test
	public void testModernItem()
	{
		ItemStack item = Config.modernItem;
		assertEquals(Material.DIAMOND_SWORD, item.getType());
		assertEquals(1, item.getAmount());
		assertEquals(3, item.getEnchantments().size());
		assertEquals(5, item.getEnchantmentLevel(Enchantment.SHARPNESS));
		assertEquals(3, item.getEnchantmentLevel(Enchantment.UNBREAKING));
		assertEquals(2, item.getEnchantmentLevel(Enchantment.FIRE_ASPECT));
		assertNotNull(item.getItemMeta());
	}

	@Test
	public void testRegistries()
	{
		assertEquals(ItemType.DIAMOND_SWORD, Config.fullItemType);
		assertEquals(ItemType.GOLDEN_SWORD, Config.partialItemType);
		assertEquals(BlockType.OAK_DOOR, Config.blockType);
		assertNotNull(Config.itemTypeList);
		assertEquals(2, Config.itemTypeList.size());
		assertEquals(ItemType.GOLDEN_APPLE, Config.itemTypeList.get(0));
		assertEquals(ItemType.DIAMOND_BLOCK, Config.itemTypeList.get(1));
		assertEquals(2, Config.entityTypes.size());
		assertTrue(Config.entityTypes.contains(EntityType.ZOMBIE));
		assertTrue(Config.entityTypes.contains(EntityType.WITHER_SKELETON));
	}

	private static class Config
	{
		@Key("string")
		public static String string = "failed";

		@Key("material")
		@TransformValue(Transform.PARSE_MATERIAL)
		public static Material material = Material.ROTTEN_FLESH;

		@Key("fullItemType")
		@TransformRegistry(KnownRegistry.ITEM)
		public static ItemType fullItemType = ItemType.ROTTEN_FLESH;

		@Key("partialItemType")
		@TransformRegistry(KnownRegistry.ITEM)
		public static ItemType partialItemType = ItemType.ROTTEN_FLESH;

		@Key("blockType")
		@TransformRegistry(KnownRegistry.BLOCK)
		public static BlockType blockType = BlockType.ACACIA_BUTTON;

		@Key("itemTypeList")
		@TransformRegistry(KnownRegistry.ITEM)
		public static List<ItemType> itemTypeList = List.of();

		@Key("legacyItems")
		@TransformValue(Transform.PARSE_LEGACY_ITEMS)
		public static List<ItemStack> legacyItems = List.of();

		@Key("modernItems")
		@TransformValue(Transform.PARSE_ITEMS)
		public static List<ItemStack> modernItems = List.of();

		@Key("modernItem")
		@TransformValue(Transform.PARSE_ITEM)
		public static ItemStack modernItem = new ItemStack(Material.ROTTEN_FLESH);

		@Key("entityTypes")
		@TransformRegistry(KnownRegistry.ENTITY_TYPE)
		public static List<EntityType> entityTypes = List.of();
	}
}