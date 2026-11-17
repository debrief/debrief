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

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ASSET.Util.MonteCarlo.XMLVariance.NamespaceContextProvider;

/**
 * list of items in a particular file which may be changed
 */

public final class MultiParticipantGenerator {

	/**
	 * our type
	 */
	public static final String GENERATOR_TYPE = "MultiParticipantGenerator";

	/**
	 * the list of participant variances we manage
	 */
	Vector<ParticipantVariance> _myParticipantVariances = null;

	/**
	 * our document
	 */
	private Document _myDocument;

	/***************************************************************
	 * constructor
	 ***************************************************************/
	/**
	 * constructor, received a stream containing the list of variances we are going
	 * to manage
	 */
	public MultiParticipantGenerator() {
		_myParticipantVariances = new Vector<ParticipantVariance>(0, 1);
	}

	/***************************************************************
	 * member methods
	 ***************************************************************/

	/**
	 * constructor, received a stream containing the list of variances we are going
	 * to manage
	 *
	 * @throws XPathExpressionException
	 */
	public MultiParticipantGenerator(final Document doc) throws XPathExpressionException {
		this();

		this.loadVariances(doc);
	}

	public final Document createNewRandomisedPermutation()
			throws XMLVariance.IllegalExpressionException, XMLVariance.MatchingException {

		// take a clone of the current document
		final Document newDoc = (Document) _myDocument.cloneNode(true);

		// check we have a document
		if (newDoc != null) {
			// and run through the participants
			final Iterator<ParticipantVariance> it = _myParticipantVariances.iterator();

			while (it.hasNext()) {
				// so, get this participant
				final ParticipantVariance o = it.next();

				// get the participant to insert it's variances
				o.populate(newDoc);
			}
		}

		return newDoc;
	}

	public final Document getRawDocument() {
		return _myDocument;
	}

	/**
	 * read in the list of variances, and collate them into our list
	 *
	 * @throws XPathExpressionException
	 */
	void loadVariances(final Document document) throws XPathExpressionException {
		XPathExpression xpe = null;
		;
		try {
			xpe = NamespaceContextProvider.createPath("//" + GENERATOR_TYPE);
		} catch (final XPathExpressionException e) {
			e.printStackTrace();
			return;
		}

		final Object result = xpe.evaluate(document, XPathConstants.NODESET);

		if (result != null) {
			final NodeList theNodes = (NodeList) result;

			final Element el = (Element) theNodes.item(0);

			if (el != null) {
				// build up our list of variances from this document
				final NodeList lis = el.getElementsByTagName("ParticipantVariance");

				final int len = lis.getLength();
				for (int i = 0; i < len; i++) {
					final Element o = (Element) lis.item(i);

					// create this variance
					final ParticipantVariance xv = new ParticipantVariance(o);

					// and store it
					_myParticipantVariances.add(xv);

				}

			}
		}

	}

	/**
	 * set the document we are going to be changing
	 */
	public final void setDocument(final Document rawDoc) {
		_myDocument = rawDoc;
	}

	/**
	 * set the document which we are going to be changing
	 */
	public final void setDocument(final java.io.InputStream istream) {
		Document thisDocument = null;
		try {
			thisDocument = ScenarioGenerator.readDocumentFrom(istream);
		} catch (final SAXException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
									// File Templates.
		}

		// and store it
		setDocument(thisDocument);

		// build up our list of variances from this document
	}

}
