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
 * 
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 *    This Source Code Form is subject to the terms of the Mozilla Public
 *    License, v. 2.0. If a copy of the MPL was not distributed with this
 *    file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 *    Copyright (c) 2015, MPL meiskam
 */
package net.dmulloy2.types;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * Represents a skull type not included in Spigot/Minecraft.
 * @author meiskam
 */

@Getter
public enum CustomSkullType
{
	SPIDER("MHF_Spider", "Kelevra_V"), // Thanks Marc Watson
	ENDERMAN("MHF_Enderman", "Violit"), // Thanks Marc Watson
	BLAZE("MHF_Blaze", "Blaze_Head"), // Thanks Marc Watson
	HORSE("gavertoso"), // Thanks Glompalici0us
	SQUID("MHF_Squid", "squidette8"), // Thanks Marc Watson
	SILVERFISH("Xzomag", "AlexVMiner"), // Thanks XlexerX
	ENDER_DRAGON("KingEndermen", "KingEnderman"), // Thanks SethBling
	SLIME("HappyHappyMan", "Ex_PS3Zocker"), // Thanks SethBling
	IRON_GOLEM("MHF_Golem", "zippie007"), // Thanks Marc Watson
	MUSHROOM_COW("MHF_MushroomCow", "Mooshroom_Stew"), // Thanks Marc Watson
	BAT("bozzobrain", "coolwhip101"), // Thanks incraftion.com
	PIG_ZOMBIE("MHF_PigZombie", "ManBearPigZombie", "scraftbrothers5"), // Thanks Marc Watson
	SNOWMAN("Koebasti", "scraftbrothers2"), // Thanks MrLeikermoser
	GHAST("MHF_Ghast", "_QuBra_", "blaiden"), // Thanks Marc Watson
	PIG("MHF_Pig", "XlexerX", "scrafbrothers7"), // Thanks Marc Watson
	VILLAGER("MHF_Villager", "Kuvase", "Villager", "scraftbrothers9"), // Thanks Marc Watson
	SHEEP("MHF_Sheep", "SGT_KICYORASS", "Eagle_Peak"), // Thanks Marc Watson
	COW("MHF_Cow", "VerifiedBernard", "CarlosTheCow"), // Thanks Marc Watson
	CHICKEN("MHF_Chicken", "scraftbrothers1"), // Thanks Marc Watson
	OCELOT("MHF_Ocelot", "scraftbrothers3"), // Thanks Marc Watson
	WITCH("scrafbrothers4"), // Thanks SuperCraftBrothers.com
	MAGMA_CUBE("MHF_LavaSlime"), // Thanks Marc Watson
	WOLF("Pablo_Penguin", "Budwolf"), // I still need an official wolf head if anyone wants to provide one
	CAVE_SPIDER("MHF_CaveSpider"), // Thanks Marc Watson
	RABBIT("rabbit2077"), // Thanks IrParadox
	GUARDIAN("Guardian", "creepypig7", "Creepypig7"); // Thanks lee3kfc

	private final String owner;
	private final String[] toConvert;

	private CustomSkullType(String owner, String... toConvert)
	{
		this.owner = owner;
		this.toConvert = toConvert;
	}

	private static final Map<String, CustomSkullType> MAP;
	static
	{
		MAP = new HashMap<String, CustomSkullType>();
		for (CustomSkullType type : values())
		{
			MAP.put(type.name().toLowerCase(), type);
			MAP.put(type.getOwner().toLowerCase(), type);
			for (String owner : type.getToConvert())
			{
				MAP.put(owner.toLowerCase(), type);
			}
		}
	}

	/**
	 * Get the CustomSkullType associated with a given key.
	 * @param key Mob type, owner, etc.
	 * @return The CustomSkullType, or null if not found
	 */
	public static CustomSkullType get(String key)
	{
		return MAP.get(key.toLowerCase());
	}
}