/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.types;

import lombok.Getter;

/**
 * @author dmulloy2
 */

@Getter
public enum PotionType
{
	FIRE_RESISTANCE("fireres"),
	INSTANT_DAMAGE("damage"),
	INSTANT_HEAL("heal"),
	INVISIBILITY("invis"),
	NIGHT_VISION("nvg"),
	POISON("poison"),
	REGEN("regen"),
	SLOWNESS("slow"),
	SPEED("speed"),
	STRENGTH("strength"),
	WATER("water"),
	WEAKNESS("weak");

	private final String name;
	private PotionType(String name)
	{
		this.name = name;
	}

	public static org.bukkit.potion.PotionType toType(String string)
	{
		for (PotionType type : PotionType.values())
		{
			if (type.name.equalsIgnoreCase(string))
				return org.bukkit.potion.PotionType.valueOf(type.toString());
		}

		return null;
	}
}