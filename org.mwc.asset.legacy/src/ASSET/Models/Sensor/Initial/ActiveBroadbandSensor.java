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

package ASSET.Models.Sensor.Initial;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.Models.SensorType;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

public class ActiveBroadbandSensor extends BroadbandSensor implements SensorType.ActiveSensor {
	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	public static class ActiveBBTest extends SupportTesting.EditableTesting {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public ActiveBBTest(final String val) {
			super(val);
		}

		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			return new ActiveBroadbandSensor(12);
		}

		public void testHeloDetection() {

			// set up the Ssk
			final ASSET.Models.Vessels.SSK ssk = new ASSET.Models.Vessels.SSK(12);
			final ASSET.Participants.Status sskStat = new ASSET.Participants.Status(12, 0);
			final WorldLocation origin = new WorldLocation(0, 0, 0);
			sskStat.setLocation(origin.add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(5), 40)));
			sskStat.setSpeed(new WorldSpeed(18, WorldSpeed.M_sec));
			ssk.setStatus(sskStat);

			// ok, setup the ssk radiation
			final ASSET.Models.Mediums.BroadbandRadNoise brn = new ASSET.Models.Mediums.BroadbandRadNoise(134);
			final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics rc = new ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
			rc.add(EnvironmentType.BROADBAND_PASSIVE, brn);
			ssk.setRadiatedChars(rc);

			// now setup the helo
			final ASSET.Models.Vessels.Helo merlin = new ASSET.Models.Vessels.Helo(33);
			final ASSET.Participants.Status merlinStat = new ASSET.Participants.Status(33, 0);
			merlinStat.setLocation(origin);
			merlinStat.setSpeed(new WorldSpeed(1, WorldSpeed.Kts));
			merlin.setStatus(merlinStat);

			// and it's sensor
			final ASSET.Models.Sensor.SensorList fit = new ASSET.Models.Sensor.SensorList();
			final ActiveBroadbandSensor bs = new ActiveBroadbandSensor(34);
			bs.setSourceLevel(210);
			fit.add(bs);
			merlin.setSensorFit(fit);

			// now setup the su
			final ASSET.Models.Vessels.Surface ff = new ASSET.Models.Vessels.Surface(31);
			final ASSET.Participants.Status ffStat = new ASSET.Participants.Status(31, 0);
			final WorldLocation sskLocation = ssk.getStatus().getLocation();
			ffStat.setLocation(sskLocation.add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(1), -40)));
			ffStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			ff.setStatus(ffStat);
			ff.setSensorFit(fit);
			final ASSET.Models.Mediums.BroadbandRadNoise ff_brn = new ASSET.Models.Mediums.BroadbandRadNoise(15);
			final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics ff_rc = new ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
			ff_rc.add(EnvironmentType.BROADBAND_PASSIVE, ff_brn);
			ff.setSelfNoise(ff_rc);
			ff.setRadiatedChars(ff_rc);

			// try a detection
			final ASSET.Models.Environment.CoreEnvironment env = new ASSET.Models.Environment.SimpleEnvironment(1, 1,
					1);
			ASSET.Models.Detection.DetectionEvent dt;
			dt = bs.detectThis(env, merlin, ssk, 0, null);
			assertTrue("helo able to detect SSK", dt != null);

			dt = bs.detectThis(env, ff, ssk, 0, null);
			assertTrue("frigate able to detect SSK", dt != null);

		}

	}

	////////////////////////////////////////////////////
	// the editor object
	////////////////////////////////////////////////////
	static public class ActiveBroadbandInfo extends BaseSensorInfo {
		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public ActiveBroadbandInfo(final ActiveBroadbandSensor data) {
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
						prop("Working", "whether this sensor is in use"),
						prop("SourceLevel", "source level of this sensor"), };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}

	}

	////////////////////////////////////////////////////
	// member constructor
	////////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////
	// member objects
	////////////////////////////////////////////////////
	private double _sourceLevel; // dB

	////////////////////////////////////////////////////
	// accessors
	////////////////////////////////////////////////////

	public ActiveBroadbandSensor(final int id) {
		super(id, "Active BB");
	}

	public ActiveBroadbandSensor(final int id, final String defaultName) {
		super(id, defaultName);
	}

	// allow an 'overview' test, just to check if it is worth all of the above
	// processing
	@Override
	protected boolean canDetectThisType(final NetworkParticipant ownship, final ParticipantType other,
			final EnvironmentType env) {
		return other.radiatesThisNoise(EnvironmentType.BROADBAND_PASSIVE);
	}

	@Override
	protected double getBkgndNoise(final EnvironmentType environment, final WorldLocation host,
			final double absBearingDegs) {
		return super.getBkgndNoise(environment, host, absBearingDegs);
	}

	////////////////////////////////////////////////////
	// member methods
	////////////////////////////////////////////////////

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new ActiveBroadbandInfo(this);

		return _myEditor;
	}

	@Override
	protected double getLoss(final EnvironmentType environment, final WorldLocation target, final WorldLocation host) {
		// we just double the normal loss to get the two-way loss
		return 2 * super.getLoss(environment, target, host);
	}

	@Override
	public int getMedium() {
		return EnvironmentType.BROADBAND_ACTIVE;
	}

	/**
	 * when running active sonar, we only determine the broadband radiated noise for
	 * ownship (we don't measure the bb_active noise)
	 *
	 * @param ownship
	 * @param absBearingDegs
	 * @return
	 */
	@Override
	protected double getOSNoise(final ParticipantType ownship, final double absBearingDegs) {
		return ownship.getSelfNoiseFor(EnvironmentType.BROADBAND_PASSIVE, absBearingDegs);
	}

	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

	/**
	 * @return the source level (in dB)
	 */
	@Override
	public double getSourceLevel() {
		return _sourceLevel;
	}

	////////////////////////////////////////////////////
	// editor support
	////////////////////////////////////////////////////

	@Override
	protected double getTgtNoise(final ASSET.ParticipantType target, final double absBearingDegs) {
		// here we return the source level for this sensor
		return _sourceLevel;
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: ActiveBroadbandSensor.java,v $
	 * Revision 1.2  2006/09/21 12:20:40  Ian.Mayo
	 * Reflect introduction of default names
	 *
	 * Revision 1.1  2006/08/08 14:21:53  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:26:02  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.7  2004/11/03 15:42:06  Ian.Mayo
	 * More support for MAD sensors, better use of canDetectThis method
	 *
	 * Revision 1.6  2004/09/06 14:20:03  Ian.Mayo
	 * Provide default icons & properties for sensors
	 * <p/>
	 * Revision 1.5  2004/08/31 09:36:52  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 * <p/>
	 * Revision 1.4  2004/08/26 16:27:19  Ian.Mayo
	 * Implement editable properties
	 * <p/>
	 * Revision 1.3  2004/08/25 11:21:06  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.2  2004/05/24 15:06:16  Ian.Mayo
	 * Commit changes conducted at home
	 * <p/>
	 * Revision 1.2  2004/03/25 22:46:55  ian
	 * Reflect new simple environment constructor
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:54  ian
	 * no message
	 * <p/>
	 * Revision 1.1  2004/02/16 13:41:38  Ian.Mayo
	 * Renamed class structure
	 * <p/>
	 * Revision 1.5  2003/11/05 09:19:05  Ian.Mayo
	 * Include MWC Model support
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

	/**
	 * @param sourceLevel the source level (in dB)
	 * @param sourceLevel the source level (in dB)
	 */
	public void setSourceLevel(final double sourceLevel) {
		this._sourceLevel = sourceLevel;
	}

}
