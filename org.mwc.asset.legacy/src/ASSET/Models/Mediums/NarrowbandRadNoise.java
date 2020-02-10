
package ASSET.Models.Mediums;

import MWC.GUI.Editable;
import MWC.GenericData.WorldSpeed;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

public class NarrowbandRadNoise
		implements ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium, java.io.Serializable, Editable {
	//////////////////////////////////////////////////
	// testing
	//////////////////////////////////////////////////
	public static class BBRadNoiseTest extends ASSET.Util.SupportTesting.EditableTesting {

		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			return new NarrowbandRadNoise(12, 12);
		}
	}

	//////////////////////////////////////////////////
	// editable info
	//////////////////////////////////////////////////
	static public class NarrowbandInfo extends EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public NarrowbandInfo(final NarrowbandRadNoise data) {
			super(data, data.getName(), "NarrowbandSensor");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = {
						prop("BaseNoiseLevel", "the base level of narrowband radiated noise (dB)"), };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	//////////////////////////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////////////////////////
	private double _baseNoiseLevel;

	//////////////////////////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////////////////////////

	/** f-nought */
	private final double _freq;

	//////////////////////////////////////////////////////////////////////
	// member methods
	//////////////////////////////////////////////////////////////////////

	/**
	 * my editor
	 */
	EditorType _myEditor;

	public NarrowbandRadNoise(final double baseNoiseLevel, final double freq) {
		_baseNoiseLevel = baseNoiseLevel;
		_freq = freq;
	}

	public double getBaseNoiseLevel() {
		return _baseNoiseLevel;
	}

	public double getBaseNoiseLevelFor(final ASSET.Participants.Status status) {
		return _baseNoiseLevel;
	}

	public double getFrequency() {
		return _freq;
	}

	/****************************************************
	 * editor support
	 ***************************************************/
	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new NarrowbandInfo(this);

		return _myEditor;
	}

	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

	/**
	 * the name of this object
	 *
	 * @return the name of this editable object
	 */
	@Override
	public String getName() {
		return "NarrowbandSensor";
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: NarrowbandRadNoise.java,v $
	 * Revision 1.1  2006/08/08 14:21:45  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:25:53  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.1  2004/10/18 14:58:10  Ian.Mayo
	 * First version.  Working fine
	 *
	 * Revision 1.6  2004/08/31 09:36:37  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 *
	 * @return yes/no
	 */
	@Override
	public boolean hasEditor() {
		return true;
	}

	@Override
	public double radiatedEnergyFor(final ASSET.Participants.Status status, final double absBearingDegs) {
		final double Speed = status.getSpeed().getValueIn(WorldSpeed.M_sec);
		final double res = 0.0000252 * Math.pow(Speed, 5) - 0.001456 * Math.pow(Speed, 4) + 0.02165 * Math.pow(Speed, 3)
				+ 0.04 * Math.pow(Speed, 2) - 0.66 * Speed + getBaseNoiseLevelFor(status);
		return res;
	}

	@Override
	public double reflectedEnergyFor(final ASSET.Participants.Status status, final double absBearingDegs) {
		final double res = 0;
		return res;
	}

	public void setBaseNoiseLevel(final double val) {
		_baseNoiseLevel = val;
	}

	@Override
	public String toString() {
		return getName();
	}

}