/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dmulloy2
 */

@Getter
@AllArgsConstructor
public enum RainbowColors
{
	RED("c", 0),
	GOLD("6", 1),
	YELLOW("e", 2),
	GREEN("a", 3),
	BLUE("b", 4),
	PINK("d", 5),
	PURPLE("5", 6);

	private static RainbowColors[] byId = new RainbowColors[7];

	private String v;
	private int id;

	public static String getColor(final int id)
	{
		if (byId.length > id)
		{
			return byId[id].getV();
		}

		return null;
	}

	static
	{
		for (RainbowColors r : values())
		{
			if (byId.length > r.id)
			{
				byId[r.id] = r;
			}
		}
	}
}