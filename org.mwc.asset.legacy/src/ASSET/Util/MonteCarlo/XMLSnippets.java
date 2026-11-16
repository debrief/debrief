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

import java.util.Vector;


import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Created by IntelliJ IDEA. User: Ian Date: 24-Sep-2003 Time: 13:51:49 To
 * change this template use Options | File Templates.
 */
public final class XMLSnippets {

	private static final String XML_SNIPPET = "XMLSnippet";

	// //////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////
	final Vector<Node> _mySnippets;

	XMLSnippets() {
		_mySnippets = new Vector<Node>(0, 1);
	}

	// //////////////////////////////////////////////////////////
	// member methods
	// //////////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////
	public XMLSnippets(final Element theContainer) {
		this();

		// retrieve the list of snippets
		final NodeList theSnippets = theContainer.getElementsByTagName(XML_SNIPPET);

		// let's step through them
		final int len = theSnippets.getLength();

		for (int i = 0; i < len; i++) {
			// ok, get the next snippet
			final Element thisNode = (Element) theSnippets.item(i);

			// and now for the sibling (which contains our data)
			Node nextS = thisNode.getFirstChild();

			// aah, it may have been blank space...
			final int theType = nextS.getNodeType();
			if (theType == Node.TEXT_NODE)
				nextS = nextS.getNextSibling();

			// now add them to our choices
			_mySnippets.add(nextS);
		}

	}

	/**
	 * retrieve a specific snippet
	 */
	public final Node get(final int val) {
		return _mySnippets.get(val);
	}

	/**
	 * get a new (random) permutation
	 */
	public final Element getInstance() {
		final int len = _mySnippets.size();
		final int rndIndex = (int) (ASSET.Util.RandomGenerator.nextRandom() * len);

		final Element res = (Element) _mySnippets.get(rndIndex);

		return res;
	}

	/**
	 * how many snippets are there?
	 */
	public final int size() {
		return _mySnippets.size();
	}

}
