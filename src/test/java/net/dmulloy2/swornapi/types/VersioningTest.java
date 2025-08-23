/**
 * (c) 2016 dmulloy2
 */
package net.dmulloy2.swornapi.types;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.dmulloy2.swornapi.BukkitTesting;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author dmulloy2
 */

public class VersioningTest
{
	@BeforeAll
	public static void beforeClass()
	{
		BukkitTesting.initializeAll();
	}

	@Test
	public void testSupported()
	{
		Versioning.setVersion(null);
		// assertEquals(Versioning.getVersion(), Version.MC_111);
		assertTrue(Versioning.isSupported());
	}

	@Test
	public void testUnsupported()
	{
		Versioning.setVersion(null);
		BukkitTesting.setBukkitVersion("4.2.0-R6.9-SNAPSHOT");
		assertFalse(Versioning.isSupported());
		BukkitTesting.resetBukkitVersion();
	}

	@Test
	public void testDropped()
	{
		Versioning.setVersion(null);
		BukkitTesting.setBukkitVersion("1.7.10-R0.1-SNAPSHOT");
		assertTrue(Versioning.getVersion().wasDropped());
		assertFalse(Versioning.isSupported());
		BukkitTesting.resetBukkitVersion();
	}
}