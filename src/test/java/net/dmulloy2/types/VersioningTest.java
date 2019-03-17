/**
 * (c) 2016 dmulloy2
 */
package net.dmulloy2.types;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.dmulloy2.BukkitTesting;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author dmulloy2
 */

public class VersioningTest
{
	@BeforeClass
	public static void beforeClass()
	{
		BukkitTesting.prepare();
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