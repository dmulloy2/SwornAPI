/**
 * (c) 2016 dmulloy2
 */
package net.dmulloy2.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.dmulloy2.BukkitTesting;
import net.dmulloy2.types.Versioning.Version;

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
		assertEquals(Versioning.getVersion(), Version.MC_19);
		assertTrue(Versioning.isSupported());
	}

	@Test
	public void testUnsupported()
	{
		Versioning.setVersion(null);
		BukkitTesting.setBukkitVersion("42.0.0-R2.0-SNAPSHOT");
		assertFalse(Versioning.isSupported());
		BukkitTesting.resetBukkitVersion();
	}
}