/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package MWC.GUI.Shapes.Symbols;

import static MWC.GUI.Shapes.Symbols.SymbolFactory.*;
import static org.junit.jupiter.api.Assertions.*;

import MWC.GUI.Shapes.Symbols.SymbolFactory.*;
import org.junit.jupiter.api.Test;


/**
 * ************************************************* testing
 * *************************************************
 */
public class SymbolFactoryTest {

	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * test the string swapping
	 */
	@Test
	public void testFindFromChar() {
		final String cA = "A";
		final String ps = SymbolFactory.createSymbolFromId(cA);
		assertEquals(ps, AIRCRAFT, "aircraft symbol returned");

		String newChar = SymbolFactory.findIdForSymbolType(AIRCRAFT);
		assertEquals(newChar, cA, "correct sym found");

		newChar = SymbolFactory.findIdForSymbolType("AIRCRAFT");
		assertEquals(newChar, cA, "correct sym found");

	}

	/**
	 * Testing string swapping for SVG symbols
	 */
	@Test
	public void testSVGFromChar() {
		final String torpedoCharacter = "s";
		final String torpedoSymbolGenerated = SymbolFactory.createSymbolFromId(torpedoCharacter);
		final String expectedTorpedoSymbol = SVG_FORMAT_PREFIX + ":torpedo";
		assertEquals(torpedoSymbolGenerated, expectedTorpedoSymbol, "Torpedo symbol returned");

		final String torpedoCharacterGenerated = SymbolFactory.findIdForSymbolType(expectedTorpedoSymbol);
		assertEquals(torpedoCharacter, torpedoCharacterGenerated, "correct symbol found");
	}

	/**
	 * friend_air is indexed as 'y', but must still be available by name
	 */
	@Test
	public void testFriendAirFromChar() {
		final String expectedSymbol = SVG_FORMAT_PREFIX + ":friend_air";
		assertEquals(expectedSymbol, SymbolFactory.createSymbolFromId("y"), "Friend air symbol returned");
		assertEquals("y", SymbolFactory.findIdForSymbolType(expectedSymbol), "correct symbol found");
		assertNotNull(SymbolFactory.createSymbol(expectedSymbol), "still available by name");
		// characters after the indexed range are not mapped
		assertNull(SymbolFactory.createSymbolFromId("z"), "z not indexed");
	}

}
