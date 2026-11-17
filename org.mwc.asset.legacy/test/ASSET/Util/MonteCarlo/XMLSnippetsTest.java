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

package ASSET.Util.MonteCarlo;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Util.MonteCarlo.XMLSnippets.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ASSET.Util.MonteCarlo.XMLSnippets.*;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ASSET.Util.SupportTesting;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class XMLSnippetsTest extends SupportTesting {


	@Test
	public final void testRandom() {
		final XMLSnippets snips = new XMLSnippets();

		Document theD = null;
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			theD = builder.newDocument();
		} catch (final FactoryConfigurationError factoryConfigurationError) {
			factoryConfigurationError.printStackTrace(); // To change body of catch
															// statement use Options |
															// File Templates.
		} catch (final ParserConfigurationException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
									// File Templates.
		}

		// check it worked
		assertNotNull(theD, "created document");

		for (int i = 0; i < 4; i++) {
			if (theD != null) {
				final Element thisE = theD.createElement("a" + (i + 1));
				snips._mySnippets.add(thisE);
			}
		}

		int num1 = 0;
		int num2 = 0;
		int num3 = 0;
		int num4 = 0;

		final int len = 10000;
		for (int i = 0; i < len; i++) {
			final Element newE = snips.getInstance();
			final String nm = newE.getNodeName();
			final int val = Integer.parseInt(new String("" + nm.charAt(1)));

			if (val == 1) {
				num1++;
			}
			if (val == 2) {
				num2++;
			}
			if (val == 3) {
				num3++;
			}
			if (val == 4) {
				num4++;
			}

		}

		assertEquals(num1, 2500, 200, "correct 1s");
		assertEquals(num2, 2500, 200, "correct 2s");
		assertEquals(num3, 2500, 200, "correct 3s");
		assertEquals(num4, 2500, 200, "correct 4s");

	}
}
