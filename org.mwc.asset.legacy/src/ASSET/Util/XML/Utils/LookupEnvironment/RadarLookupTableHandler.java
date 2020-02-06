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

package ASSET.Util.XML.Utils.LookupEnvironment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.LookupSensor.IntegerTargetTypeLookup;
import ASSET.Models.Sensor.Lookup.RadarLookupSensor;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 26-Oct-2004 Time: 09:24:08 To
 * change this template use File | Settings | File Templates.
 */
abstract public class RadarLookupTableHandler extends MWCXMLReader {
	private static final String NAME_ATTRIBUTE = "Name";
	public static final String TARGET_ASPECT_SET = "TargetAspectSet";
	private static final String TARGET_ASPECT_DATUM = "TargetAspectDatum";
	public static final String[] ASPECT_HEADINGS = { "DeadAhead", "Bow", "Beam", "Quarter", "Astern" };

	public static final String TARGET_SEA_STATE_SET = "TargetSeaStateSet";
	public static final String TARGET_SEA_STATE_DATUM = "TargetSeaStateDatum";
	public static final String[] SEA_STATE_HEADINGS = { "SeaState_0", "SeaState_1", "SeaState_2", "SeaState_3",
			"SeaState_4", "SeaState_5", "SeaState_6", "SeaState_7", "SeaState_8", "SeaState_9", "SeaState_10" };

	public static void exportThis(final String type, final RadarLookupSensor.RadarEnvironment radar,
			final Element parent, final Document doc) {
		// ok, put us into the element
		final org.w3c.dom.Element envElement = doc.createElement(type);

		// get on with the name attribute
		envElement.setAttribute(NAME_ATTRIBUTE, radar.getName());

		// and the child components
		final IntegerTargetTypeLookup aspects = radar.getSigmaValues();
		if (aspects != null) {
			IntegerTargetTypeLookupHandler.exportThis(TARGET_ASPECT_SET, TARGET_ASPECT_DATUM, ASPECT_HEADINGS, aspects,
					envElement, doc);
		}

		final IntegerTargetTypeLookup states = radar.getSeaStates();
		if (states != null) {
			IntegerTargetTypeLookupHandler.exportThis(TARGET_SEA_STATE_SET, TARGET_SEA_STATE_DATUM, SEA_STATE_HEADINGS,
					states, envElement, doc);
		}

		// and hang us off the parent
		parent.appendChild(envElement);

	}

	LookupSensor.IntegerTargetTypeLookup _targetAspect;
	LookupSensor.IntegerTargetTypeLookup _targetSeaState;

	String _myName = null;

	public RadarLookupTableHandler(final String myType) {
		super(myType);

		addAttributeHandler(new HandleAttribute(NAME_ATTRIBUTE) {
			@Override
			public void setValue(final String name, final String value) {
				_myName = value;
			}
		});

		addHandler(new IntegerTargetTypeLookupHandler(TARGET_ASPECT_SET, TARGET_ASPECT_DATUM, ASPECT_HEADINGS) {
			@Override
			public void setLookup(final LookupSensor.IntegerTargetTypeLookup val) {
				_targetAspect = val;
			}
		});

		addHandler(
				new IntegerTargetTypeLookupHandler(TARGET_SEA_STATE_SET, TARGET_SEA_STATE_DATUM, SEA_STATE_HEADINGS) {
					@Override
					public void setLookup(final LookupSensor.IntegerTargetTypeLookup val) {
						_targetSeaState = val;
					}
				});
	}

	@Override
	public void elementClosed() {
		final RadarLookupSensor.RadarEnvironment res = new RadarLookupSensor.RadarEnvironment(_myName, _targetSeaState,
				_targetAspect);

		setRadarEnvironment(res);

		_targetSeaState = null;
		_targetAspect = null;
		_myName = null;
	}

	abstract public void setRadarEnvironment(RadarLookupSensor.RadarEnvironment env);

}
