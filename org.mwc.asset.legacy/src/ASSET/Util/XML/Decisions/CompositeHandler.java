
package ASSET.Util.XML.Decisions;

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

import ASSET.Models.Decision.Composite;
import ASSET.Models.Decision.Conditions.Condition;
import ASSET.Models.Decision.Responses.ManoeuvreToLocation;
import ASSET.Models.Decision.Responses.Response;
import ASSET.Util.XML.Decisions.Conditions.DetectionForPeriodHandler;
import ASSET.Util.XML.Decisions.Conditions.DetectionHandler;
import ASSET.Util.XML.Decisions.Conditions.ElapsedTimeHandler;
import ASSET.Util.XML.Decisions.Responses.ChangeSensorLineUpHandler;
import ASSET.Util.XML.Decisions.Responses.ManoeuvreToCourseHandler;
import ASSET.Util.XML.Decisions.Responses.ManoeuvreToLocationHandler;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;

/**
 * class to read in a composite behaviour object
 *
 * @see ASSET.Models.Decision.Composite
 * @see ASSET.Models.Decision.Conditions.Condition
 */
abstract class CompositeHandler extends CoreDecisionHandler {

	private final static String type = "Composite";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final Composite bb = (Composite) toExport;

		// output the name
		CoreDecisionHandler.exportThis(bb, thisPart, doc);

		// output the components
		// first the condition
		final Condition con = bb.getCondition();

		if (con instanceof DetectionHandler) {
			DetectionHandler.exportThis(con, thisPart, doc);
		}

		// now the responses

		final Response res = bb.getResponse();
		if (res instanceof ManoeuvreToLocation) {
			ManoeuvreToLocationHandler.exportThis(res, thisPart, doc);
		}

		parent.appendChild(thisPart);

	}

	private Composite _myComposite;
	Condition _myCondition;

	Response _myResponse;

	public CompositeHandler() {
		super("Composite");

		/**
		 * first declare the condition handlers
		 *
		 */
		addHandler(new DetectionHandler() {
			@Override
			public void setCondition(final Condition dec) {
				_myCondition = dec;
			}
		});
		addHandler(new DetectionForPeriodHandler() {
			@Override
			public void setCondition(final Condition dec) {
				_myCondition = dec;
			}
		});
		addHandler(new ElapsedTimeHandler() {
			@Override
			public void setCondition(final Condition dec) {
				_myCondition = dec;
			}
		});

		/**
		 * and now the response handlers
		 *
		 */
		/**
		 * first declare the condition handlers
		 *
		 */
		addHandler(new ManoeuvreToLocationHandler() {
			@Override
			public void setResponse(final Response dec) {
				_myResponse = dec;
			}
		});
		addHandler(new ManoeuvreToCourseHandler() {
			@Override
			public void setResponse(final Response dec) {
				_myResponse = dec;
			}
		});

		addHandler(new ChangeSensorLineUpHandler() {
			@Override
			public void setResponse(final Response dec) {
				_myResponse = dec;
			}
		});

	}

	@Override
	public void elementClosed() {
		// check that we have our data
		_myComposite = new Composite(_myCondition, _myResponse);

		super.setAttributes(_myComposite);

		// finally output it
		setModel(_myComposite);

		// and reset it, ready for the next Composite
		_myComposite = null;
		_myCondition = null;
		_myResponse = null;
	}

	abstract public void setModel(ASSET.Models.DecisionType dec);

}