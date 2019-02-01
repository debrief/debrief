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

/**
 * Title: Debrief 2000 Description: Debrief 2000 Track Analysis Software
 * Copyright: Copyright (c) 2000 Company: MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.awt.Color;
import java.util.Vector;

import org.mwc.cmap.core.DataTypes.Temporal.*;
import org.mwc.debrief.core.editors.PlotEditor;
import org.mwc.debrief.core.editors.painters.LayerPainterManager;
import org.w3c.dom.Element;

import Debrief.ReaderWriter.XML.GUI.*;
import Debrief.ReaderWriter.XML.GUIHandler.ComponentDetails;
import MWC.TacticalData.TrackDataProvider;
import MWC.TacticalData.temporal.ControllableTime;
import MWC.TacticalData.temporal.TimeControlPreferences;
import MWC.TacticalData.temporal.TimeProvider;

abstract public class SWTGUIHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

	/**
	 * interface for gui components that store their preferences to file
	 * 
	 * @author ian.mayo
	 * 
	 */
	public interface ComponentCreator
	{
		/**
		 * ok - store this component
		 * 
		 * @param details
		 *          the parsed xml for this component
		 * @param controller
		 *          object responsible for how we show/manage time
		 * @param painterMgr
		 *          object responsible for how plot is shown
		 * @param timeController
		 *          object responsible for current DTG
		 */
		public void makeThis(
				Debrief.ReaderWriter.XML.GUIHandler.ComponentDetails details,
				final TimeControlPreferences controller,
				final LayerPainterManager painterMgr,
				final ControllableTime timeController);

	}

	private final java.util.Hashtable<String, StepperHandler> _myCreators = new java.util.Hashtable<String, StepperHandler>();

	String _primaryTrack = null;

	Vector<String> _secondaryTracks = null;

	static private StepperHandler _myStepperHandler;

	public SWTGUIHandler(final PlotEditor thePlot)
	{
		// inform our parent what type of class we are
		super("gui");

		addHandler(new ToteHandler()
		{
			public void setPrimarySecondary(final boolean isPrimary, final String trackName)
			{
				// cool, sort out which tracks are on the tote
				if (isPrimary)
					_primaryTrack = trackName;
				else
				{
					if (_secondaryTracks == null)
					{
						_secondaryTracks = new Vector<String>(0, 1);
					}
					_secondaryTracks.add(trackName);
				}
			}
		});

		addHandler(new ComponentHandler()
		{
			public void addComponent(final ComponentDetails details)
			{
				addThisComponent(details, thePlot);
			}
		});

		addHandler(new BackgroundHandler()
		{
			public void setBackgroundColor(final Color theColor)
			{
				thePlot.setBackgroundColor(theColor);
			}
		});

		// collate our list of exporters
		if (_myStepperHandler == null)
		{
			_myStepperHandler = new StepperHandler();
		}
		_myCreators.put("Stepper", _myStepperHandler);
	}

	public void elementClosed()
	{
		super.elementClosed();

		// right - store the tracks
		assignTracks(_primaryTrack, _secondaryTracks);

		// and ditch the working vars
		_primaryTrack = null;
		_secondaryTracks = null;
	}

	/**
	 * store the current primary and secondary tracks for this plot
	 * 
	 * @param primaryTrack
	 *          primary track name
	 * @param secondaryTracks
	 *          list of tracks name for secondary tracks
	 */
	abstract public void assignTracks(String primaryTrack,
			Vector<String> secondaryTracks);

	void addThisComponent(final ComponentDetails details, final PlotEditor thePlot)
	{
		// sort out this component
		final String cType = details.type;

		final ComponentCreator cc = (ComponentCreator) _myCreators.get(cType);
		if (cc != null)
		{
			// ok, get the bits ready
			final TimeControlPreferences controller = (TimeControlPreferences) thePlot
					.getAdapter(TimeControlPreferences.class);
			final LayerPainterManager painterMgr = (LayerPainterManager) thePlot
					.getAdapter(LayerPainterManager.class);
			final ControllableTime timeController = (ControllableTime) thePlot
					.getAdapter(ControllableTime.class);

			if (painterMgr == null)
			{
				System.err.println("WARNING - PLOT NOT PRODUCING PainterManager. This should not happen in production");
			}
			else
				cc.makeThis(details, controller, painterMgr, timeController);
		}
		else
			MWC.Utilities.Errors.Trace.trace("XML Handler not found for " + cType);
	}

	// ///////////////////////////////////////////////////////////////////////
	// the constructors for our components
	// ///////////////////////////////////////////////////////////////////////

	public static void exportThis(final PlotEditor thePlot, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc)
	{
		// create ourselves
		final Element gui = doc.createElement("gui");

		// //////////////////////////////////////////////
		// first the tote
		// //////////////////////////////////////////////
		final TrackDataProvider theTracks = (TrackDataProvider) thePlot
				.getAdapter(TrackDataProvider.class);
		ToteHandler.exportTote(theTracks, gui, doc);

		// try to export the other features

		// check the stepper handler
		if (_myStepperHandler == null)
			_myStepperHandler = new StepperHandler();

		// get the object representing the stepper
		final TimeControlPreferences controller = (TimeControlPreferences) thePlot
				.getAdapter(TimeControlPreferences.class);
		final LayerPainterManager painter = (LayerPainterManager) thePlot
				.getAdapter(LayerPainterManager.class);
		final TimeProvider timeProvider = (TimeProvider) thePlot
				.getAdapter(TimeProvider.class);
		if (controller != null)
		{
			final ComponentDetails stepperD = _myStepperHandler.exportThis(controller,
					painter, timeProvider, doc);
			stepperD.exportTo("Stepper", gui, doc);
		}

		BackgroundHandler.exportThis(thePlot.getBackgroundColor(), gui, doc);

		parent.appendChild(gui);
	}

}