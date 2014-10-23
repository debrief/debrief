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
package MWC.Utilities.ReaderWriter.XML.Features;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import MWC.GUI.S57.S57Layer;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.PlottableExporter;

abstract public class S57Handler extends MWCXMLReader implements PlottableExporter
{
	private static final String VISIBLE = "Visible";

	private static final String MY_TYPE = "s57";

	private static final String SOURCE_FILE = "SourceFile";

	boolean _isVisible;

	protected String _myName = null;

	protected String _sourceFile;

	public S57Handler()
	{
		this(MY_TYPE);
	}

	public S57Handler(final String theType)
	{
		// inform our parent what type of class we are
		super(theType);

		addAttributeHandler(new HandleBooleanAttribute(VISIBLE)
		{
			public void setValue(final String name, final boolean value)
			{
				_isVisible = value;
			}
		});

		addAttributeHandler(new HandleAttribute("Name")
		{
			public void setValue(final String name, final String val)
			{
				_myName = val;
			}
		});
		addAttributeHandler(new HandleAttribute(SOURCE_FILE)
		{
			public void setValue(final String name, final String val)
			{
				_sourceFile = val;
			}
		});

	}

	public void elementClosed()
	{
		// create a Grid from this data
		final MWC.GUI.S57.S57Layer csp = getGrid();
		csp.setVisible(_isVisible);

		// check the source file exists
		if (_sourceFile != null)
		{
			final File tstFile = new File(_sourceFile);
			if (tstFile.exists())
				csp.setSourceFile(_sourceFile);
		}

		addPlottable(csp);

		// reset our variables
		_isVisible = false;
		_myName = null;
	}

	/**
	 * get the grid object itself (we supply this method so that it can be
	 * overwritten, by the LocalGrid painter for example
	 * 
	 * @return
	 */
	protected S57Layer getGrid()
	{
		return new S57Layer();
	}

	abstract public void addPlottable(MWC.GUI.Plottable plottable);

	/**
	 * export this grid
	 * 
	 * @param plottable
	 *          the grid we're going to export
	 * @param parent
	 * @param doc
	 */
	public void exportThisPlottable(final MWC.GUI.Plottable plottable,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{

		final S57Layer theGrid = (S57Layer) plottable;
		final Element gridElement = doc.createElement(MY_TYPE);

		exportLayer(gridElement, theGrid, doc);

		parent.appendChild(gridElement);
	}

	/**
	 * utility class which appends the other grid attributes
	 * 
	 * @param gridElement
	 *          the element to put the grid into
	 * @param theGrid
	 *          the grid to export
	 * @param doc
	 *          the document it's all going into
	 */
	private static void exportLayer(final Element gridElement, final S57Layer theS57, final Document doc)
	{
		// do the visibility
		gridElement.setAttribute(VISIBLE, writeThis(theS57.getVisible()));
		gridElement.setAttribute(SOURCE_FILE, theS57.getSourceFile());
	}

}