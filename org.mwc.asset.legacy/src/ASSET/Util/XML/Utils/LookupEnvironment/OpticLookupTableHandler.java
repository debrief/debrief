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
import ASSET.Models.Sensor.Lookup.LookupSensor.StringLookup;
import ASSET.Models.Sensor.Lookup.OpticLookupSensor;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 26-Oct-2004 Time: 09:24:08 To
 * change this template use File | Settings | File Templates.
 */
abstract public class OpticLookupTableHandler extends MWCXMLReader {
	public static final String TARGET_SEA_STATE_SET = "TargetSeaStateSet";
	public static final String TARGET_SEA_STATE_DATUM = "TargetSeaStateDatum";
	public static final String[] SEA_STATE_HEADINGS = { "SeaState_0", "SeaState_1", "SeaState_2", "SeaState_3",
			"SeaState_4", "SeaState_5", "SeaState_6", "SeaState_7", "SeaState_8", "SeaState_9", "SeaState_10" };
	private static final String VIS_ATTEN = "VisualAttenuationDatum";
	private static final String[] VIS_HEADINGS = { "VeryClear", "Clear", "LightHaze", "Haze", "Mist", "Fog" };

	private static final String LIGHT_LEVEL = "LightLevelDatum";
	private static final String[] LIGHT_HEADINGS = { "Daylight", "Dusk", "MoonlitNight", "DarkNight" };

	private static final String TARGET_VIS = "TargetVisibilitySet";
	private static final String TARGET_VIS_DATUM = "TargetVisibilityDatum";

	private static final String NAME_ATTRIBUTE = "Name";
	private static final String VISIBILITY = "Visibility";

	public static void exportThis(final String type, final OpticLookupSensor.OpticEnvironment optic,
			final Element parent, final Document doc) {
		// ok, put us into the element
		final org.w3c.dom.Element envElement = doc.createElement(type);

		// get on with the name attribute
		envElement.setAttribute(NAME_ATTRIBUTE, optic.getName());

		// now the child bits
		IntegerDatumHandler.exportThis(VIS_ATTEN, optic.get_attenuation(), envElement, doc, VIS_HEADINGS);

		final StringLookup atten = optic.getVisibility();
		if (atten != null) {
			StringSetHandler.exportThis(TARGET_VIS, TARGET_VIS_DATUM, VISIBILITY, atten, envElement, doc);
		}

		final IntegerTargetTypeLookup states = optic.getSea_states();
		if (states != null) {
			IntegerTargetTypeLookupHandler.exportThis(TARGET_SEA_STATE_SET, TARGET_SEA_STATE_DATUM, SEA_STATE_HEADINGS,
					states, envElement, doc);
		}

		IntegerDatumHandler.exportThis(LIGHT_LEVEL, optic.getLightLevel(), envElement, doc, LIGHT_HEADINGS);

		// and hang us off the parent
		parent.appendChild(envElement);

	}

	LookupSensor.IntegerLookup _attenuation;

	LookupSensor.StringLookup _visibility;

	LookupSensor.IntegerTargetTypeLookup _sea_states;
	LookupSensor.IntegerLookup _lightLevel;

	String _myName = null;

	public OpticLookupTableHandler(final String myType) {
		super(myType);

		addAttributeHandler(new HandleAttribute(NAME_ATTRIBUTE) {
			@Override
			public void setValue(final String name, final String value) {
				_myName = value;
			}
		});

		addHandler(
				new IntegerTargetTypeLookupHandler(TARGET_SEA_STATE_SET, TARGET_SEA_STATE_DATUM, SEA_STATE_HEADINGS) {
					@Override
					public void setLookup(final LookupSensor.IntegerTargetTypeLookup val) {
						_sea_states = val;
					}
				});

		addHandler(new IntegerDatumHandler(VIS_ATTEN, VIS_HEADINGS) {
			@Override
			public void setDatums(final LookupSensor.IntegerLookup myValues) {
				_attenuation = myValues;
			}
		});

		addHandler(new IntegerDatumHandler(LIGHT_LEVEL, LIGHT_HEADINGS) {
			@Override
			public void setDatums(final LookupSensor.IntegerLookup myValues) {
				_lightLevel = myValues;
			}
		});

		addHandler(new StringSetHandler(TARGET_VIS, TARGET_VIS_DATUM, VISIBILITY) {
			@Override
			public void setDatums(final LookupSensor.StringLookup myValues) {
				_visibility = myValues;
			}
		});

	}

	@Override
	public void elementClosed() {
		final OpticLookupSensor.OpticEnvironment res = new OpticLookupSensor.OpticEnvironment(_attenuation, _lightLevel,
				_myName, _sea_states, _visibility);

		setOpticEnvironment(res);

		_attenuation = null;
		_lightLevel = null;
		_sea_states = null;
		_visibility = null;
		_myName = null;
	}

	abstract public void setOpticEnvironment(OpticLookupSensor.OpticEnvironment env);
}
