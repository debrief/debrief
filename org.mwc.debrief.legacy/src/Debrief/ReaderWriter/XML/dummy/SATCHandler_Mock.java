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

package Debrief.ReaderWriter.XML.dummy;

import java.awt.Color;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Debrief.GUI.Frames.Application;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.Utilities.ReaderWriter.XML.LayerHandlerExtension;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;

/**
 * mock handler that will read in an SATC_Solution object, but which won't
 * actually load the data
 *
 * @author ian
 *
 */
public class SATCHandler_Mock extends MWCXMLReader implements LayerHandlerExtension {
	private static final String MY_TYPE = "satc_solution";

	private static final String NAME = "NAME";
	private static final String SHOW_BOUNDS = "ShowBounds";
	private static final String ONLY_ENDS = "OnlyPlotEnds";
	private static final String SHOW_SOLUTIONS = "ShowSolutions";
	private static final String SHOW_ALTERATIONS = "ShowAlterationBounds";
	private static final String LIVE_RUNNING = "LiveRunning";

	public SATCHandler_Mock() {
		this(MY_TYPE);
	}

	public SATCHandler_Mock(final String theType) {
		// inform our parent what type of class we are
		super(theType);

		addAttributeHandler(new HandleAttribute(NAME) {
			@Override
			public void setValue(final String name, final String val) {
				// ignore
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_BOUNDS) {
			@Override
			public void setValue(final String name, final boolean value) {
				// ignore
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_ALTERATIONS) {
			@Override
			public void setValue(final String name, final boolean value) {
				// ignore
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(ONLY_ENDS) {
			@Override
			public void setValue(final String name, final boolean value) {
				// ignore
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(LIVE_RUNNING) {
			@Override
			public void setValue(final String name, final boolean value) {
				// ignore
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_SOLUTIONS) {
			@Override
			public void setValue(final String name, final boolean value) {
				// ignore
			}
		});
		addHandler(new ColourHandler() {
			@Override
			public void setColour(final Color res) {
				// ignore
			}
		});
	}

	@Override
	public boolean canExportThis(final Layer subject) {
		return false;
	}

	@Override
	public void elementClosed() {
		Application.logError2(ToolParent.WARNING, "SATC element has been dropped, this is a mock handler", null);
	}

	@Override
	public void exportThis(final Layer theLayer, final Element parent, final Document doc) {
		throw new IllegalArgumentException("This is a mock handler, it cannot be used for export");
	}

	@Override
	public void setLayers(final Layers theLayers) {
		// ignore
	}

}