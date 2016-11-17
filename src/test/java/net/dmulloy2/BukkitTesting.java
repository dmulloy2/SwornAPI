/**
 * (c) 2016 dmulloy2
 */
package net.dmulloy2;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.logging.Logger;

import net.minecraft.server.v1_11_R1.DispenserRegistry;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemFactory;
import org.bukkit.craftbukkit.v1_11_R1.util.Versioning;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author dmulloy2
 */

public class BukkitTesting
{
	private static boolean prepared;
	private static String fakeVersion;

	public static void prepare()
	{
		if (! prepared)
		{
			prepared = true;

			DispenserRegistry.c(); // Basically registers everything

			// Mock the server object
			Server mockedServer = mock(Server.class);

			when(mockedServer.getLogger()).thenReturn(Logger.getLogger("Minecraft"));
			when(mockedServer.getName()).thenReturn("Mock Server");
			when(mockedServer.getVersion()).thenReturn(CraftServer.class.getPackage().getImplementationVersion());
			when(mockedServer.getBukkitVersion()).thenAnswer(new Answer<String>()
			{
				@Override
				public String answer(InvocationOnMock invocation) throws Throwable
				{
					return fakeVersion != null ? fakeVersion : Versioning.getBukkitVersion();
				}
			});

			when(mockedServer.getItemFactory()).thenReturn(CraftItemFactory.instance());
			when(mockedServer.isPrimaryThread()).thenReturn(true);

			// Inject this fake server
			Bukkit.setServer(mockedServer);
		}
	}

	public static void setBukkitVersion(String version)
	{
		fakeVersion = version;
	}

	public static void resetBukkitVersion()
	{
		setBukkitVersion(null);
	}
}