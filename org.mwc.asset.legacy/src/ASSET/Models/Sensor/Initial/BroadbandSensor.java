
package ASSET.Models.Sensor.Initial;

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

import ASSET.NetworkParticipant;
import ASSET.Models.Environment.EnvironmentType;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

public class BroadbandSensor extends InitialSensor {

	////////////////////////////////////////////////////
	// the editor object
	////////////////////////////////////////////////////
	static public class BroadbandInfo extends BaseSensorInfo {
		/**
		 * @param data the Layers themselves
		 */
		public BroadbandInfo(final BroadbandSensor data) {
			super(data);
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = { prop("Name", "the name of this broadband sensor"),
						prop("DetectionAperture", "the size of the aperture of this sensor"),
						prop("Working", "whether this sensor is in use"), };
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

	///////////////////////////////////
	// member variables
	//////////////////////////////////
	/**
	 * the detection aperture in this sensor
	 */
	private double _myDetectionAperture = 120;

	/**
	 * ************************************************* constructor
	 * *************************************************
	 */
	public BroadbandSensor(final int id) {
		super(id, "BB");
	}

	public BroadbandSensor(final int id, final String defaultName) {
		super(id, defaultName);
	}

	// allow an 'overview' test, just to check if it is worth all of the above
	// processing
	@Override
	protected boolean canDetectThisType(final NetworkParticipant ownship, final ASSET.ParticipantType other,
			final EnvironmentType env) {
		return other.radiatesThisNoise(getMedium());
	}

	/**
	 * Whether this sensor cannot be used to positively identify a target
	 */
	@Override
	public boolean canIdentifyTarget() {
		return false;
	}

	@Override
	protected double getBkgndNoise(final ASSET.Models.Environment.EnvironmentType environment,
			final MWC.GenericData.WorldLocation host, final double absBearingDegs) {

		double res;

		// use the environment to determine the loss
		res = environment.getBkgndNoise(EnvironmentType.BROADBAND_PASSIVE, host, absBearingDegs);

		return res;
	}

	public double getDetectionAperture() {
		return _myDetectionAperture;
	}

	@Override
	protected double getDI(final double courseDegs, final double absBearingDegs) {
		// double res = 74;
		double res = 34; // URICK page 43, for cylindrical sensor 60 inches diameter @ 15Khz

		// convert the bearing to relative bearing
		double relBrg = absBearingDegs - courseDegs;

		// check it's now going round the long way
		if (relBrg > 180)
			relBrg -= 360;

		if (relBrg < -180)
			relBrg += 360;

		if (Math.abs(relBrg) > _myDetectionAperture) {
			res = 00;
		}

		return res;
	}

	/**
	 * the estimated range for a detection of this type (where applicable)
	 */
	@Override
	public WorldDistance getEstimatedRange() {
		return new WorldDistance(1000, WorldDistance.YARDS);
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public MWC.GUI.Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new BroadbandInfo(this);

		return _myEditor;
	}

	@Override
	protected double getLoss(final ASSET.Models.Environment.EnvironmentType environment,
			final MWC.GenericData.WorldLocation target, final MWC.GenericData.WorldLocation host) {
		double res = 0;

		// use the environment to determine the loss
		res = environment.getLossBetween(EnvironmentType.BROADBAND_PASSIVE, target, host);
		return res;
	}

	@Override
	public int getMedium() {
		return EnvironmentType.BROADBAND_PASSIVE;
	}

	@Override
	protected double getOSNoise(final ASSET.ParticipantType ownship, final double absBearingDegs) {
		return ownship.getSelfNoiseFor(getMedium(), absBearingDegs);
	}

	@Override
	protected double getRD(final NetworkParticipant host, final NetworkParticipant target) {
		return 2;
	}

	@Override
	protected double getTgtNoise(final ASSET.ParticipantType target, final double absBearingDegs) {
		return target.getRadiatedNoiseFor(getMedium(), absBearingDegs);
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: BroadbandSensor.java,v $
	 * Revision 1.3  2006/11/06 16:08:51  Ian.Mayo
	 * Hard-code detection range so we get immediate contact
	 *
	 * Revision 1.2  2006/09/21 12:20:41  Ian.Mayo
	 * Reflect introduction of default names
	 *
	 * Revision 1.1  2006/08/08 14:21:54  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:26:02  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.8  2004/11/03 15:42:07  Ian.Mayo
	 * More support for MAD sensors, better use of canDetectThis method
	 *
	 * Revision 1.7  2004/10/14 13:38:51  Ian.Mayo
	 * Refactor listening to sensors - so that we can listen to a sensor & it's detections in the same way that we can listen to a participant
	 * <p/>
	 * Revision 1.6  2004/09/06 14:20:05  Ian.Mayo
	 * Provide default icons & properties for sensors
	 * <p/>
	 * Revision 1.5  2004/08/31 09:36:55  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 * <p/>
	 * Revision 1.4  2004/08/26 17:05:35  Ian.Mayo
	 * Implement more editable properties
	 * <p/>
	 * Revision 1.3  2004/08/25 11:21:08  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.2  2004/05/24 15:06:19  Ian.Mayo
	 * Commit changes conducted at home
	 * <p/>
	 * Revision 1.2  2004/03/25 22:46:55  ian
	 * Reflect new simple environment constructor
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:54  ian
	 * no message
	 * <p/>
	 * Revision 1.1  2004/02/16 13:41:39  Ian.Mayo
	 * Renamed class structure
	 * <p/>
	 * Revision 1.4  2003/11/05 09:19:07  Ian.Mayo
	 * Include MWC Model support
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
	}

	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

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

	/**
	 * does this sensor return the course of the target?
	 */
	@Override
	public boolean hasTgtCourse() {
		return true;
	}

	public void setDetectionAperture(final double val) {
		_myDetectionAperture = val;
	}

}