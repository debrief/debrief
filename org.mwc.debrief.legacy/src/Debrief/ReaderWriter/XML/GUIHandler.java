
package Debrief.ReaderWriter.XML;

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

import java.awt.Color;

import org.w3c.dom.Element;

import Debrief.GUI.Frames.Session;
import Debrief.GUI.Views.AnalysisView;
import Debrief.GUI.Views.PlainView;
import Debrief.ReaderWriter.XML.GUI.BackgroundHandler;
import Debrief.ReaderWriter.XML.GUI.ComponentHandler;
import Debrief.ReaderWriter.XML.GUI.StepperHandler;
import Debrief.ReaderWriter.XML.GUI.ToteHandler;

public final class GUIHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	public static interface ComponentCreator {
		public void makeThis(ComponentDetails details, final Session session);
	}

	static public final class ComponentDetails {
		public final java.util.Hashtable<String, String> properties = new java.util.Hashtable<String, String>();
		public String type = null;

		public final void addProperty(final String name, final String val) {
			properties.put(name, val);
		}

		public final void exportTo(final String title, final org.w3c.dom.Element parent,
				final org.w3c.dom.Document doc) {
			final Element comp = doc.createElement("component");
			comp.setAttribute("Type", title);
			final java.util.Enumeration<String> iter = properties.keys();
			while (iter.hasMoreElements()) {
				final String thisK = iter.nextElement();
				final String value = properties.get(thisK);
				MWC.Utilities.ReaderWriter.XML.Util.PropertyHandler.exportProperty(thisK, value, comp, doc);
			}

			parent.appendChild(comp);
		}
	}

	static private final java.util.Hashtable<String, StepperHandler> _myCreators = new java.util.Hashtable<String, StepperHandler>();

	static private StepperHandler _myStepperHandler;

	public static void exportThis(final Debrief.GUI.Frames.Session session, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final Element gui = doc.createElement("gui");

		////////////////////////////////////////////////
		// first the tote
		////////////////////////////////////////////////
		ToteHandler.exportTote(session, gui, doc);

		// try to export the other features

		// check the stepper handler
		if (_myStepperHandler == null)
			_myStepperHandler = new StepperHandler();
		final ComponentDetails stepperD = _myStepperHandler.exportThis(session);
		if (stepperD != null) {
			stepperD.exportTo("Stepper", gui, doc);
		}

		final PlainView pv = session.getCurrentView();
		if (pv instanceof AnalysisView) {
			final AnalysisView av = (AnalysisView) pv;
			final Color col = av.getChart().getCanvas().getBackgroundColor();
			BackgroundHandler.exportThis(col, gui, doc);
		}

		parent.appendChild(gui);
	}

	final Session _session;

	private AnalysisView _analysisView;

	public GUIHandler(final Session session) {
		// inform our parent what type of class we are
		super("gui");

		_session = session;

		Debrief.GUI.Tote.AnalysisTote _theTote = null;
		final Debrief.GUI.Views.PlainView pv = _session.getCurrentView();
		if (pv instanceof Debrief.GUI.Views.AnalysisView) {
			_analysisView = (Debrief.GUI.Views.AnalysisView) pv;
			_theTote = _analysisView.getTote();
		}

		final MWC.GUI.Layers _theData = _session.getData();

		addHandler(new ToteHandler(_theTote, _theData));
		addHandler(new ComponentHandler() {
			@Override
			public void addComponent(final GUIHandler.ComponentDetails details) {
				addThisComponent(details);
			}
		});
		addHandler(new BackgroundHandler() {
			@Override
			public void setBackgroundColor(final Color theColor) {
				final PlainView pv1 = _session.getCurrentView();
				if (pv1 instanceof AnalysisView) {
					final AnalysisView av = (AnalysisView) pv1;
					av.getChart().getCanvas().setBackgroundColor(theColor);
				}
			}
		});

		// collate our list of exporters
		if (_myStepperHandler == null)
			_myStepperHandler = new StepperHandler();

		_myCreators.put("Stepper", _myStepperHandler);
	}

	/////////////////////////////////////////////////////////////////////////
	// the constructors for our components
	/////////////////////////////////////////////////////////////////////////

	void addThisComponent(final ComponentDetails details) {
		// sort out this component
		final String cType = details.type;

		final ComponentCreator cc = _myCreators.get(cType);
		if (cc != null) {
			cc.makeThis(details, _session);
		} else
			MWC.Utilities.Errors.Trace.trace("XML Handler not found for " + cType);
	}

}