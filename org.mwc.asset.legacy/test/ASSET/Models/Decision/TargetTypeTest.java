package ASSET.Models.Decision;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Models.Decision.TargetType.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ASSET.Models.Decision.TargetType.*;
import java.util.Iterator;
import java.util.Vector;
import ASSET.Participants.Category;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////
// member variables
// ////////////////////////////////////////////////////////////////////

public class TargetTypeTest {

	@Test
	public void testAND() {
		final TargetType tt = new TargetType();
		tt.setANDOperation(true);
		tt.addTargetType(Category.Force.BLUE);
		tt.addTargetType(Category.Environment.AIRBORNE);

		final Category newCat = new Category(Category.Force.GREEN, Category.Environment.SUBSURFACE,
				Category.Type.FISHING_VESSEL);
		assertFalse(tt.matches(newCat), "don't match - no matching types");

		newCat.setEnvironment(Category.Environment.AIRBORNE);
		assertFalse(tt.matches(newCat), "don't match - not all types");

		newCat.setForce(Category.Force.BLUE);
		assertTrue(tt.matches(newCat), "matches - all types");
	}

	@Test
	public void testOR_1() {
		final TargetType tt = new TargetType();
		tt.setANDOperation(false);
		tt.addTargetType(Category.Force.BLUE);
		tt.addTargetType(Category.Environment.AIRBORNE);

		final Category newCat = new Category(Category.Force.GREEN, Category.Environment.SUBSURFACE,
				Category.Type.FISHING_VESSEL);
		assertFalse(tt.matches(newCat), "don't match - no matching types");

		newCat.setEnvironment(Category.Environment.AIRBORNE);
		assertTrue(tt.matches(newCat), "matches - some types");

		newCat.setForce(Category.Force.BLUE);
		assertTrue(tt.matches(newCat), "matches - all types");
	}

	@Test
	public void testOR_2() {
		final TargetType tt = new TargetType();
		tt.setANDOperation(false);
		tt.addTargetType(Category.Force.BLUE);
		tt.addTargetType(Category.Force.GREEN);

		final Category newCat = new Category(Category.Force.RED, Category.Environment.SUBSURFACE,
				Category.Type.FISHING_VESSEL);
		assertFalse(tt.matches(newCat), "don't match - no matching types");

		newCat.setEnvironment(Category.Force.BLUE);
		assertTrue(tt.matches(newCat), "matches - some types");
	}

}
