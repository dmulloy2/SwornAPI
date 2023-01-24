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
package net.dmulloy2.swornapi.types;

import net.dmulloy2.swornapi.util.Util;

import org.bukkit.Location;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * Handles special entities such as Wither Skeletons, Zombie Villagers, and Horses.
 * Necessary because Mojang changed Entities how are structured in 1.11.
 * 
 * @author dmulloy2
 */
@SuppressWarnings("deprecation")
public class SpecialEntities
{
	private static Provider provider;

	static
	{
		try
		{
			ElderGuardian.class.getName();
			provider = new SeparateProvider();
		}
		catch (Throwable ex)
		{
			provider = new CombinedProvider();
		}
	}

	private SpecialEntities() { }

	private interface Provider 
	{
		LivingEntity spawnWitherSkeleton(Location loc);
		LivingEntity spawnZombieVillager(Location loc, Profession profession);
		LivingEntity spawnHorse(Location loc, HorseType type, Horse.Color color,
				Horse.Style style, boolean tame, boolean chest);
		boolean isElderGuardian(Entity entity);
	}

	private static class SeparateProvider implements Provider
	{
		@Override
		public LivingEntity spawnWitherSkeleton(Location loc)
		{
			Objects.requireNonNull(loc, "loc cannot be null");
			Objects.requireNonNull(loc.getWorld(), "loc world cannot be null");

			return (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.WITHER_SKELETON);
		}

		@Override
		public LivingEntity spawnZombieVillager(Location loc, Profession profession)
		{
			Objects.requireNonNull(loc, "loc cannot be null");
			Objects.requireNonNull(loc.getWorld(), "loc world cannot be null");

			ZombieVillager entity = (ZombieVillager) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE_VILLAGER);
			entity.setVillagerProfession(profession);
			return entity;
		}

		@Override
		public LivingEntity spawnHorse(Location loc, HorseType type, Color color, Style style, boolean tame, boolean chest)
		{
			Objects.requireNonNull(loc, "loc cannot be null");
			Objects.requireNonNull(loc.getWorld(), "loc world cannot be null");

			Horse horse = (Horse) loc.getWorld().spawnEntity(loc, type.getEntity());
			horse.setColor(color);
			horse.setStyle(style);
			horse.setTamed(tame);

			if (chest && horse instanceof ChestedHorse)
				((ChestedHorse) horse).setCarryingChest(true);
	
			return horse;
		}

		@Override
		public boolean isElderGuardian(Entity entity)
		{
			return entity instanceof ElderGuardian;
		}
		
	}

	@Getter
	@AllArgsConstructor
	public enum HorseType
	{
		DONKEY(EntityType.DONKEY, Variant.DONKEY),
		NORMAL(EntityType.HORSE, Variant.HORSE),
		LLAMA(EntityType.LLAMA, Variant.LLAMA),
		MULE(EntityType.MULE, Variant.MULE),
		SKELETON(EntityType.SKELETON_HORSE, Variant.SKELETON_HORSE),
		ZOMBIE(EntityType.ZOMBIE_HORSE, Variant.UNDEAD_HORSE)
		;

		private final EntityType entity;
		private final Variant variant;
	}

	private static class CombinedProvider implements Provider
	{
		@Override
		public LivingEntity spawnWitherSkeleton(Location loc)
		{
			Objects.requireNonNull(loc, "loc cannot be null");
			Objects.requireNonNull(loc.getWorld(), "loc world cannot be null");

			Skeleton entity = (Skeleton) loc.getWorld().spawnEntity(loc, EntityType.SKELETON);
			entity.setSkeletonType(SkeletonType.WITHER);
			return entity;
		}

		@Override
		public LivingEntity spawnZombieVillager(Location loc, Profession profession)
		{
			Objects.requireNonNull(loc, "loc cannot be null");
			Objects.requireNonNull(loc.getWorld(), "loc world cannot be null");

			Zombie zombie = (Zombie) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
			zombie.setVillagerProfession(profession);
			return zombie;
		}

		@Override
		public LivingEntity spawnHorse(Location loc, HorseType type, Color color, Style style, boolean tame, boolean chest)
		{
			Objects.requireNonNull(loc, "loc cannot be null");
			Objects.requireNonNull(loc.getWorld(), "loc world cannot be null");

			Horse horse = (Horse) loc.getWorld().spawnEntity(loc, EntityType.HORSE);
			horse.setVariant(type.getVariant());
			horse.setColor(color);
			horse.setStyle(style);
			horse.setTamed(tame);
			horse.setCarryingChest(chest);
			return horse;
		}

		@Override
		public boolean isElderGuardian(Entity entity)
		{
			return entity instanceof Guardian && ((Guardian) entity).isElder();
		}
	}

	/**
	 * Spawns a Wither Skeleton at a given location.
	 * @param loc Location to spawn at
	 * @return The Wither Skeleton
	 */
	public static LivingEntity spawnWitherSkeleton(Location loc)
	{
		return provider.spawnWitherSkeleton(loc);
	}

	/**
	 * Spawns a Zombie Villager at a given location with a given profession.
	 * @param loc Location to spawn at
	 * @param profession Villager profession, random if null
	 * @return The Zombie Villager
	 */
	public static LivingEntity spawnZombieVillager(Location loc, Profession profession)
	{
		if (profession == null) profession = randomElement(Profession.values());

		return provider.spawnZombieVillager(loc, profession);
	}

	/**
	 * Whether or not a given Entity is an Elder Guardian.
	 * @param entity Entity to check
	 * @return True if an elder, false if not
	 */
	public static boolean isElderGuardian(Entity entity)
	{
		return provider.isElderGuardian(entity);
	}

	/**
	 * Spawns a Horse at a given location with the given properties.
	 * @param loc Location to spawn at
	 * @param type Horse Type, null if random
	 * @param color Horse Color, null if random
	 * @param style Horse Style, null if random
	 * @param tame Whether or not the Horse is tamed
	 * @param chest Whether or not the Horse is carrying a chest
	 * @return The Horse
	 */
	public static LivingEntity spawnHorse(Location loc, HorseType type,
			Horse.Color color, Horse.Style style, boolean tame, boolean chest)
	{
		if (type == null) type = randomElement(HorseType.values());
		if (color == null) color = randomElement(Horse.Color.values());
		if (style == null) style = randomElement(Horse.Style.values());

		return provider.spawnHorse(loc, type, color, style, tame, chest);
	}

	private static <E> E randomElement(E[] elements)
	{
		return elements[Util.random(elements.length)];
	}
}