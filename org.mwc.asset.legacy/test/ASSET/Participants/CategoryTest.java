package ASSET.Participants;

import static ASSET.Participants.Category.*;
import static org.junit.jupiter.api.Assertions.*;

import ASSET.Participants.Category.*;
import java.awt.Color;
import MWC.GUI.Properties.DebriefColors;
import org.junit.jupiter.api.Test;

/**
 * and here's how to test it
 *
 * @author ianmayo
 *
 */
public class CategoryTest {
	@Test
	public void testEquals() {
		final Category catOne = new Category(Force.BLUE, Environment.SURFACE, Type.FRIGATE);
		final Category catTwo = new Category(Force.RED, Environment.SURFACE, Type.FRIGATE);
		final Category catThree = new Category(Force.BLUE, Environment.SURFACE, Type.FRIGATE);

		assertNotSame(catOne, catTwo, "not equal");
		assertTrue(catOne.equals(catThree), " equal");
		assertTrue(catThree.equals(catOne), " equal");
		assertFalse(catOne.equals(catTwo), " equal");
	}
}
