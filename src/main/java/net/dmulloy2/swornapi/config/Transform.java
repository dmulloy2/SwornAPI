package net.dmulloy2.swornapi.config;

public enum Transform
{
	FORMAT,
	LIST_LOWER_CASE,
	LIST_UPPER_CASE,
	LOWER_CASE,
	MINUTE_TO_MILLIS,
	MINUTE_TO_TICKS,
	PARSE_ENUM,
	/**
	 * @deprecated Prefer {@link #PARSE_ITEM} with a modern item section
	 */
	@Deprecated
	PARSE_LEGACY_ITEM,
	PARSE_ITEM,
	/**
	 * @deprecated Prefer {@link #PARSE_ITEMS} with a modern item section
	 */
	@Deprecated
	PARSE_LEGACY_ITEMS,
	PARSE_ITEMS,
	/**
	 * @deprecated Prefer {@link TransformRegistry} with {@link org.bukkit.Material},
	 * {@link org.bukkit.inventory.ItemType} or {@link org.bukkit.block.BlockType
	 */
	@Deprecated
	PARSE_MATERIAL,
	/**
	 * @deprecated Prefer {@link TransformRegistry} with {@link org.bukkit.Material},
	 * {@link org.bukkit.inventory.ItemType} or {@link org.bukkit.block.BlockType
	 */
	@Deprecated
	PARSE_MATERIALS,
	SECOND_TO_MILLIS,
	SECOND_TO_TICKS
}
