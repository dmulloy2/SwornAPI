package net.dmulloy2.swornapi.config;

import lombok.Getter;

import io.papermc.paper.registry.RegistryKey;

import org.bukkit.Keyed;

public enum KnownRegistry
{
	ATTRIBUTE(RegistryKey.ATTRIBUTE),
	ENTITY_TYPE(RegistryKey.ENTITY_TYPE),
	ENCHANTMENT(RegistryKey.ENCHANTMENT),
	BLOCK(RegistryKey.BLOCK),
	ITEM(RegistryKey.ITEM),
	MOB_EFFECT(RegistryKey.MOB_EFFECT),
	PARTICLE(RegistryKey.PARTICLE_TYPE),
	POTION(RegistryKey.POTION),
	SOUND(RegistryKey.SOUND_EVENT),
	;

	@Getter
	private final RegistryKey<? extends Keyed> key;

	KnownRegistry(RegistryKey<? extends Keyed> key)
	{
		this.key = key;
	}
}
