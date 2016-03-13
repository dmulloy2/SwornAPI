/**
 * SwornAPI - common API for MineSworn and Shadowvolt plugins
 * Copyright (C) 2016 dmulloy2
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
package net.dmulloy2.types;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dmulloy2.util.FormatUtil;

import org.apache.commons.lang.Validate;
import org.bukkit.potion.PotionEffect;

/**
 * Represents various potion types with friendlier names.
 *
 * @author dmulloy2
 */

@Getter
@AllArgsConstructor
public enum PotionType
{
    WATER("water"),
    MUNDANE("mundane"),
    THICK("thick"),
    AWKWARD("awkward"),
    NIGHT_VISION("nvg"),
    INVISIBILITY("invis"),
    JUMP("jump"),
    FIRE_RESISTANCE("fireres"),
    SPEED("speed"),
    SLOWNESS("slow"),
    WATER_BREATHING("waterbreath"),
    INSTANT_HEAL("heal"),
    INSTANT_DAMAGE("damage"),
    POISON("poison"),
    REGEN("regen"),
    STRENGTH("strength"),
    WEAKNESS("weak"),
    LUCK("luck"),
	;

	private final String name;

	/**
	 * Finds a PotionType from a given string matcher
	 * @param matcher String matcher
	 * @return The PotionType, or null if none exists
	 */
	public static PotionType find(String matcher)
	{
		Validate.notNull(matcher, "Matcher cannot be null!");
		matcher = matcher.toLowerCase();

		for (PotionType type : values())
		{
			if (type.getName().equals(matcher) || type.name().toLowerCase().equals(matcher))
				return type;
		}

		return null;
	}

	/**
	 * Gets the Bukkit PotionType equivalent of this PotionType
	 * @return The Bukkit equivalent
	 */
	public org.bukkit.potion.PotionType getBukkit()
	{
		return org.bukkit.potion.PotionType.valueOf(name());
	}

	/**
	 * Returns a String representation of a {@link Collection} of
	 * PotionEffects
	 *
	 * @param effects Collection of potion effects.
	 */
	public static String toString(Collection<PotionEffect> effects)
	{
		Validate.notNull(effects, "effects cannot be null!");

		StringJoiner joiner = new StringJoiner(", ");
		for (PotionEffect effect : effects)
		{
			joiner.append(FormatUtil.getFriendlyName(effect.getType()));
		}

		return joiner.toString();
	}
}