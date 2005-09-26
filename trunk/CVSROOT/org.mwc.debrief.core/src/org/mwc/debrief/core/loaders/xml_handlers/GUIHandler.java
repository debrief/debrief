package org.mwc.debrief.core.loaders.xml_handlers;

/**
 * Title: Debrief 2000 Description: Debrief 2000 Track Analysis Software
 * Copyright: Copyright (c) 2000 Company: MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.awt.Color;
import java.util.Vector;

import org.mwc.cmap.TimeController.views.TimeController;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.cmap.core.interfaces.IPlotGUI;
import org.mwc.debrief.core.editors.PlotEditor;
import org.w3c.dom.Element;

import Debrief.ReaderWriter.XML.GUI.*;
import Debrief.ReaderWriter.XML.GUIHandler.ComponentDetails;

abstract public class GUIHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

	// static private final java.util.Hashtable _myCreators = new
	// java.util.Hashtable();

	String _primaryTrack = null;

	Vector _secondaryTracks = null;
	

  static private StepperHandler _myStepperHandler;
  

	public GUIHandler(final IPlotGUI plot)
	{
		// inform our parent what type of class we are
		super("gui");

		addHandler(new ToteHandler()
		{
			public void setPrimarySecondary(boolean isPrimary, String trackName)
			{
				// cool, sort out which tracks are on the tote
				if (isPrimary)
					_primaryTrack = trackName;
				else
				{
					if (_secondaryTracks == null)
					{
						_secondaryTracks = new Vector(0, 1);
					}
					_secondaryTracks.add(trackName);
				}
			}
		});

		addHandler(new ComponentHandler()
		{
			public void addComponent(ComponentDetails details)
			{
				addThisComponent(details);
			}
		});
		addHandler(new BackgroundHandler()
		{
			public void setBackgroundColor(Color theColor)
			{
				plot.setBackgroundColor(theColor);
			}
		});

		// collate our list of exporters
		 if(_myStepperHandler == null)
		 _myStepperHandler = new StepperHandler();
	}

	public void elementClosed()
	{
		// TODO Auto-generated method stub
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
	abstract public void assignTracks(String primaryTrack, Vector secondaryTracks);

	private void addThisComponent(ComponentDetails details)
	{
		// // sort out this component
		// String cType = details.type;
		//
		// ComponentCreator cc = (ComponentCreator)_myCreators.get(cType);
		// if(cc != null)
		// {
		// cc.makeThis(details, _analysisView);
		// }
		// else
		// MWC.Utilities.Errors.Trace.trace("XML Handler not found for " + cType);
	}

	// ///////////////////////////////////////////////////////////////////////
	// the constructors for our components
	// ///////////////////////////////////////////////////////////////////////

	public static void exportThis(PlotEditor thePlot, org.w3c.dom.Element parent,
			org.w3c.dom.Document doc)
	{
		// create ourselves
		Element gui = doc.createElement("gui");

		// //////////////////////////////////////////////
		// first the tote
		// //////////////////////////////////////////////
		TrackDataProvider theTracks = (TrackDataProvider) thePlot
				.getAdapter(TrackDataProvider.class);
		ToteHandler.exportTote(theTracks, gui, doc);

		// try to export the other features

		// check the stepper handler
		 if(_myStepperHandler == null)
			 _myStepperHandler = new StepperHandler();
		 
		 // get the object representing the stepper
		 TimeController _controller = null;
		 
		 ComponentDetails stepperD = _myStepperHandler.exportThis(_controller);
		 stepperD.exportTo("Stepper", gui, doc);
		

		BackgroundHandler.exportThis(thePlot.getBackgroundColor(), gui, doc);

		parent.appendChild(gui);
	}

}