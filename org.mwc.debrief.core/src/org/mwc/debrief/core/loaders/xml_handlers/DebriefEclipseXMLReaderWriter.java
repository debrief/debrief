/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */

package org.mwc.debrief.core.loaders.xml_handlers;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.debrief.core.editors.PlotEditor;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

import MWC.GUI.Layers;
import MWC.Utilities.ReaderWriter.DebriefXMLReaderException;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * @author IAN MAYO
 * @version 1
 */
public final class DebriefEclipseXMLReaderWriter extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReaderWriter
{

	/**
	 * Creates new XMLReaderWriter
	 */
	public DebriefEclipseXMLReaderWriter()
	{
	}

	// ///////////////////////////////////////////////////////////////////
	//
	// ////////////////////////////////////////////////////////////////////

	/**
	 * handle the import of XML data, creating a new session for it
	 * @throws DebriefXMLReaderException 
	 */
	public final void importThis(final String fName,
			final java.io.InputStream is, final Layers destination,
			final IControllableViewport view, final PlotEditor plot) throws DebriefXMLReaderException
	{
		// create the handler for this type of data
		final MWCXMLReader handler = new PlotHandler(fName, destination, view, plot);

		// import the datafile into this set of layers
		importThis(fName, is, handler);
	}

	/**
	 * exporting the session
	 * 
	 * @param version
	 *          the version number of Debrief that's doing the export
	 */
	static public void exportThis(final PlotEditor thePlot,
			final java.io.OutputStream os, final String version)
	{
		// first put the plot into an XML document
		try
		{
			final Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			final org.w3c.dom.Element plot = PlotHandler.exportPlot(thePlot, doc,
					version);
			doc.appendChild(plot);

			outputContent(os, doc);
		}
		catch (final DOMException e)
		{
			CorePlugin.logError(Status.ERROR, "Whilst export Debrief plot", e);

		}
		catch (final ParserConfigurationException e)
		{
			CorePlugin.logError(Status.ERROR, "Whilst exporting Debrief plot", e);
		}
	}

	/**
	 * exporting the session
	 */
	static public void exportThis(final Layers theLayers,
			final java.io.OutputStream os)
	{
		// first put the plot into an XML document
		try
		{
			final Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			final org.w3c.dom.Element plot = PlotHandler.exportPlot(theLayers, doc);
			doc.appendChild(plot);

			outputContent(os, doc);

		}
		catch (final DOMException e)
		{
			CorePlugin.logError(Status.ERROR, "Whilst export Debrief plot", e);

		}
		catch (final ParserConfigurationException e)
		{
			CorePlugin.logError(Status.ERROR, "Whilst exporting Debrief plot", e);
		}
	}

	/**
	 * ok - we've got our output in a doc, write it to the specified stream
	 * 
	 * @param os
	 *          - where we're writing to
	 * @param doc
	 *          - the content we're outputting
	 */
	private static void outputContent(final java.io.OutputStream os,
			final Document doc)
	{
		// and now export it.
		// this way of exporting the dom came from sample code in the Xerces 2.6.2
		// download
		try
		{
			final TransformerFactory tF = TransformerFactory.newInstance();
			final Transformer tr = tF.newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");

			final DOMSource source = new DOMSource(doc);
			final StreamResult result = new StreamResult(os);

			tr.transform(source, result);
		}
		catch (final TransformerException e)
		{
			CorePlugin.logError(Status.ERROR, "Failed to export document to file", e);
		}
	}

}
