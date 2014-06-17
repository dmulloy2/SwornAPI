/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.types;

import java.util.Map;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.enchantments.Enchantment;

/**
 * Represents various enchantments with more friendly names.
 * 
 * @author dmulloy2
 */

@Getter
@AllArgsConstructor
public enum EnchantmentType 
{
	ARROW_DAMAGE("power"),
	ARROW_FIRE("fire"),
	ARROW_INFINITE("inf"),
	ARROW_KNOCKBACK("arrowkb"),
	DAMAGE_ALL("sharp"),
	DAMAGE_ARTHROPODS("bane"),
	DAMAGE_UNDEAD("smite"),
	DIG_SPEED("eff"),
	DURABILITY("dura"),
	FIRE_ASPECT("fireaspect"),
	KNOCKBACK("knockback"),
	LOOT_BONUS_BLOCKS("fortune"),
	LOOT_BONUS_MOBS("looting"),
	LUCK("luck"),
	LOOT("loot"),
	OXYGEN("breathing"),
	PROTECTION_ENVIRONMENTAL("prot"),
	PROTECTION_EXPLOSIONS("blast"),
	PROTECTION_FALL("feather"),
	PROTECTION_FIRE("fireprot"),
	PROTECTION_PROJECTILE("proj"),
	SILK_TOUCH("silk"),
	THORNS("thorns"),
	WATER_WORKER("aqua");
	
	private String name;

	/**
	 * Returns a friendlier name of a given {@link Enchantment}.
	 * 
	 * @param enchant {@link Enchantment}
	 * @return Friendlier name.
	 */
	public static String toName(Enchantment enchant)
	{
		for (EnchantmentType e : EnchantmentType.values())
		{
			if (e.toString().equals(enchant.getName()))
				return e.getName();
		}

		return FormatUtil.getFriendlyName(enchant.getName());
	}

	/**
	 * Attempts to get an {@link Enchantment} from a given string.
	 * 
	 * @param enchant Enchantment name
	 * @return The enchantment, or null if none exists.
	 */
	public static Enchantment toEnchantment(String enchant)
	{
		enchant = enchant.toUpperCase();
		
		for (EnchantmentType e : EnchantmentType.values())
		{
			if (e.getName().equalsIgnoreCase(enchant) || e.name().equals(enchant))
				return Enchantment.getByName(e.toString());
		}

		return null;
	}

	/**
	 * Returns a <code>String</code> representation of a {@link Map} of
	 * enchantments.
	 * 
	 * @param enchantments Enchantment map
	 */
	public static String toString(Map<Enchantment, Integer> enchantments)
	{
		StringJoiner joiner = new StringJoiner(", ");
		for (Entry<Enchantment, Integer> entry : enchantments.entrySet())
		{
			joiner.append(EnchantmentType.toName(entry.getKey()) + ":" + entry.getValue());
		}

		return joiner.toString();
	}
}