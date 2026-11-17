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

package ASSET.Models.Sensor.Lookup;

import java.util.Vector;

import ASSET.NetworkParticipant;
import ASSET.ScenarioType;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Participants.Category;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;

/**
 * Created by IntelliJ IDEA. User: Ian Date: 14-Jan-2004 Time: 21:37:30 To
 * change this template use Options | File Templates.
 */
public class OpticLookupSensor extends LookupSensor {

	/** a sensor with sample values, for demonstration and testing */
	public static OpticLookupSensor getTestOpticSensor() {
		return new OpticLookupSensor(12, 0.05, 10, 1.05, 0.8, new Duration(20, Duration.SECONDS), 0.2,
				new Duration(30, Duration.SECONDS));
	}

	// //////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////

	// ////////////////////////////////////////////////
	// structure to contain optic lookup set
	// ////////////////////////////////////////////////
	public static class OpticEnvironment {
		protected IntegerLookup _attenuation;

		protected StringLookup _visibility;

		protected IntegerTargetTypeLookup _sea_states;

		protected IntegerLookup _lightLevel;

		protected String _name;

		public OpticEnvironment(final IntegerLookup attenuation, final IntegerLookup lightLevel, final String name,
				final IntegerTargetTypeLookup sea_states, final StringLookup visibility) {
			_attenuation = attenuation;
			_lightLevel = lightLevel;
			_name = name;
			_sea_states = sea_states;
			_visibility = visibility;
		}

		public IntegerLookup get_attenuation() {
			return _attenuation;
		}

		public IntegerLookup getLightLevel() {
			return _lightLevel;
		}

		public String getName() {
			return _name;
		}

		public IntegerTargetTypeLookup getSea_states() {
			return _sea_states;
		}

		public StringLookup getVisibility() {
			return _visibility;
		}

		public void setName(final String name) {
			_name = name;
		}

	}

	// //////////////////////////////////////////////////
	// the editor object
	// //////////////////////////////////////////////////
	static public class OpticLookupInfo extends BaseSensorInfo {
		/**
		 * @param data the Layers themselves
		 */
		public OpticLookupInfo(final OpticLookupSensor data) {
			super(data);
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final static int ATTEN_INDEX = 0;

	private final static int VIS_INDEX = 1;

	private final static int SEA_STATE_INDEX = 2;

	private final static int LIGHT_INDEX = 3;

	OpticEnvironment _defaultLookups = null;

	/**
	 * constructor
	 *
	 * @param VDR  variability in detection range
	 * @param TBDO time between detection opportunities (millis)
	 * @param MRF  maximum range factor
	 * @param CRF  classification range factor
	 * @param CTP  classification time period
	 * @param IRF  identification range factor
	 * @param ITP  identification time period
	 */
	public OpticLookupSensor(final int id, final double VDR, final long TBDO, final double MRF, final double CRF,
			final Duration CTP, final double IRF, final Duration ITP) {
		super(id, VDR, TBDO, MRF, CRF, CTP, IRF, ITP, "Optic Lookup");
	}

	/**
	 * calculate the predicted range for this contact. Uses the visual horizon from
	 * Bowditch, The Practical navigator (1995) table 12
	 *
	 * @param ownship
	 * @param target
	 * @param scenario
	 * @param environment
	 * @return
	 */
	@Override
	protected WorldDistance calculateRP(final NetworkParticipant ownship, final NetworkParticipant target,
			final ScenarioType scenario, final EnvironmentType environment, final long time,
			final LookupSensor.LastTargetContact params) {
		WorldDistance res = null;

		// retrieve our parameters
		final double attenVal = ((Double) params.get(ATTEN_INDEX)).doubleValue();
		final double targetVis = ((Double) params.get(VIS_INDEX)).doubleValue();
		final Double seaVal = ((Double) params.get(SEA_STATE_INDEX));
		final double seaStateVal = seaVal.doubleValue();
		final double lightValue = ((Double) params.get(LIGHT_INDEX)).doubleValue();

		// find out the ownship and target height
		final double EARTH_RADIUS = 6371950;
		final double B_FACTOR = 0.8279; // taken from from Bowditch, The Practical
		// navigator (1995) table 12
		final double myHeight = -ownship.getStatus().getLocation().getDepth();
		final double hisHeight = -target.getStatus().getLocation().getDepth();

		// do the calc
		double RP_1 = -1 / attenVal * Math.log(targetVis / seaStateVal) * lightValue;
		final double RP_2 = Math.sqrt(2 * EARTH_RADIUS * myHeight / B_FACTOR)
				+ Math.sqrt(2 * EARTH_RADIUS * hisHeight / B_FACTOR);

		// ian's fudge make the range absolute
		RP_1 = Math.abs(RP_1);

		// and use the min value.
		final double RP = Math.min(RP_1, RP_2);

		// done.
		res = new WorldDistance(RP, WorldDistance.METRES);

		return res;
	}

	@Override
	public boolean canIdentifyTarget() {
		return true; // To change body of implemented methods use File | Settings |
		// File Templates.
	}

	protected OpticEnvironment getDefaultLookups() {
		// produce the visual attenuation lookup
		final IntegerLookup attenuation = new IntegerLookup(
				new int[] { EnvironmentType.VERY_CLEAR, EnvironmentType.CLEAR, EnvironmentType.LIGHT_HAZE,
						EnvironmentType.HAZE, EnvironmentType.MIST, EnvironmentType.FOG },
				new double[] { 8e-5, 2e-4, 5e-4, 1e-3, 2e-3, 4e-3 });

		// and the target visibility
		final StringLookup visibility = new StringLookup(new String[] { Category.Type.CARRIER, Category.Type.FRIGATE,
				Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL }, new double[] { 0.2, 0.2, 0.12, 0.16 },
				new Double(1));

		visibility.setUnknownResult(new Double(0.001));

		final Vector<NamedList> states = new Vector<NamedList>(0, 1);
		states.add(new NamedList(Category.Type.CARRIER, new double[] { 1.00, 1.00, 1.00, 1.00, 1.00, 0.95, 0.90 }));
		states.add(new NamedList(Category.Type.CARRIER, new double[] { 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 0.95 }));
		states.add(new NamedList(Category.Type.SUBMARINE, new double[] { 1.00, 1.00, 0.80, 0.75, 0.70, 0.50, 0.30 }));
		states.add(new NamedList(Category.Type.FISHING_VESSEL,
				new double[] { 1.00, 1.00, 0.96, 0.80, 0.75, 0.70, 0.50, 0.50 }));

		final IntegerTargetTypeLookup sea_states = new IntegerTargetTypeLookup(states, new Double(1.0));

		// and lastly the light level
		final IntegerLookup lightLevel = new IntegerLookup(new int[] { EnvironmentType.DAYLIGHT, EnvironmentType.DUSK,
				EnvironmentType.MOON_NIGHT, EnvironmentType.DARK_NIGHT }, new double[] { 1, 0.4, 0.3, 0.05 });

		final OpticEnvironment defaultEnv = new OpticEnvironment(attenuation, lightLevel, "sample", sea_states,
				visibility);

		return defaultEnv;
	}

	// //////////////////////////////////////////////////////////
	// model support
	// //////////////////////////////////////////////////////////

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new OpticLookupInfo(this);

		return _myEditor;
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 *   $Log: OpticLookupSensor.java,v $
	 *   Revision 1.3  2006/09/21 12:20:44  Ian.Mayo
	 *   Reflect introduction of default names
	 *
	 *   Revision 1.2  2006/09/14 14:11:10  Ian.Mayo
	 *   Source tidying
	 *
	 *   Revision 1.1  2006/08/08 14:21:57  Ian.Mayo
	 *   Second import
	 *
	 *   Revision 1.1  2006/08/07 12:26:06  Ian.Mayo
	 *   First versions
	 *
	 *   Revision 1.23  2004/11/05 15:29:20  Ian.Mayo
	 *   Correct tests to reflect new structure of test-read file
	 *
	 *   Revision 1.22  2004/11/04 09:30:22  Ian.Mayo
	 *   Handle sensors which can't provide range/bearing
	 *   &lt;p/&gt;
	 *   Revision 1.21  2004/10/27 15:13:51  Ian.Mayo
	 *   Reflect changed structure of OpticEnvironment
	 *   &lt;p/&gt;
	 *   Revision 1.20  2004/10/27 14:07:05  Ian.Mayo
	 *   Handle default values better
	 *   &lt;p/&gt;
	 *   Revision 1.19  2004/10/27 13:30:09  Ian.Mayo
	 *   More environment handling
	 *   &lt;p/&gt;
	 *   Revision 1.18  2004/10/26 15:03:38  Ian.Mayo
	 *   Move lookup tables up to environment
	 *   &lt;p/&gt;
	 *   Revision 1.17  2004/10/25 15:30:23  Ian.Mayo
	 *   Start incorporating lookup data tables in environment
	 *   &lt;p/&gt;
	 *   Revision 1.16  2004/09/06 14:20:09  Ian.Mayo
	 *   Provide default icons &amp; properties for sensors
	 *   &lt;p/&gt;
	 *   Revision 1.15  2004/09/06 14:04:18  Ian.Mayo
	 *   Switch to supporting editables in Layer Manager, and showing icon for any editables which have one
	 *   &lt;p/&gt;
	 *   Revision 1.14  2004/09/01 15:42:58  Ian.Mayo
	 *   Extract the double values that bit sooner
	 *   &lt;p/&gt;
	 *   Revision 1.13  2004/08/31 09:37:01  Ian.Mayo
	 *   Rename inner static tests to match signature **Test to make automated testing more consistent
	 *   &lt;p/&gt;
	 *   Revision 1.12  2004/08/26 17:05:39  Ian.Mayo
	 *   Implement more editable properties
	 *   &lt;p/&gt;
	 *   Revision 1.11  2004/08/25 11:21:16  Ian.Mayo
	 *   Remove main methods which just run junit tests
	 *   &lt;p/&gt;
	 *   Revision 1.10  2004/08/23 09:12:33  Ian.Mayo
	 *   Update tests to reflect new detection list processing
	 *   &lt;p/&gt;
	 *   Revision 1.9  2004/08/23 08:06:10  Ian.Mayo
	 *   Implement clearing old detection lists, minor refactoring
	 *   &lt;p/&gt;
	 *   Revision 1.8  2004/08/20 15:08:22  Ian.Mayo
	 *   Part way through changing detection cycle so that it doesn't start afresh each time - each sensor removes it's previous calls the next time it is called (to allow for TBDO)
	 *   &lt;p/&gt;
	 *   Revision 1.7  2004/08/16 10:36:39  Ian.Mayo
	 *   Reflect changed sensor performance model
	 *   &lt;p/&gt;
	 *   Revision 1.6  2004/08/10 13:52:52  Ian.Mayo
	 *   Better comments
	 *   &lt;p/&gt;
	 *   Revision 1.5  2004/08/09 15:49:26  Ian.Mayo
	 *   Refactor category types into Force, Environment, Type sub-classes
	 *   &lt;p/&gt;
	 *   Revision 1.4  2004/05/24 15:05:30  Ian.Mayo
	 *   Commit changes conducted at home
	 *   &lt;p/&gt;
	 *   Revision 1.5  2004/04/22 21:38:19  ian
	 *   Use corrected algs
	 *   &lt;p/&gt;
	 *   Revision 1.4  2004/04/15 22:00:39  ian
	 *   Change visual detection algorithm
	 *   &lt;p/&gt;
	 *   Revision 1.3  2004/04/13 20:53:10  ian
	 *   Provide lookup results for unfound target types
	 *   &lt;p/&gt;
	 *   Revision 1.2  2004/04/08 20:27:29  ian
	 *   Restructured contructor for CoreObserver
	 *   &lt;p/&gt;
	 *   Revision 1.1.1.1  2004/03/04 20:30:54  ian
	 *   no message
	 *   &lt;p/&gt;
	 *   Revision 1.3  2004/02/18 08:59:42  Ian.Mayo
	 *   Tidying
	 *   &lt;p/&gt;
	 *   &lt;p/&gt;
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

	// //////////////////////////////////////////////////////////
	// member methods
	// //////////////////////////////////////////////////////////
	/**
	 * determine the lookup parameters applicable to this sensor
	 *
	 * @param ownship     us
	 * @param target      them
	 * @param scenario    the scenario
	 * @param environment the environment
	 * @param time        current time
	 * @return the set of lookup parameters applicable to this sensor
	 */
	@Override
	protected LookupSensor.LastTargetContact parametersFor(final NetworkParticipant ownship,
			final NetworkParticipant target, final ScenarioType scenario, final EnvironmentType environment,
			final long time) {
		final LookupSensor.LastTargetContact res = new LookupSensor.LastTargetContact();

		OpticEnvironment lookups = environment.getOpticEnvironment();

		if (lookups == null) {
			// bugger. there aren't any. have we created our defaults?
			if (_defaultLookups == null) {
				// nope, better create it
				_defaultLookups = getDefaultLookups();
			}

			lookups = _defaultLookups;
		}

		/**
		 * first the visual attenuation
		 *
		 * @see EnvironmentType.VERY_CLEAR
		 */
		final int currentAtten = environment.getAtmosphericAttentuationFor(time, ownship.getStatus().getLocation());
		final Double attenVal = lookups._attenuation.find(currentAtten);

		/**
		 * now the target visibility
		 *
		 * @see Category.Type.CARRIER
		 */
		final String targetType = target.getCategory().getType();
		final Double targetVis = lookups._visibility.find(targetType);

		// next the sea state value
		final int seaState = environment.getSeaStateFor(time, ownship.getStatus().getLocation());
		final Double seaStateVal = lookups.getSea_states().find(seaState, targetType);

		/**
		 * and lastly the light value
		 *
		 * @see EnvironmentType.DUSK
		 */
		final int lightLevel = environment.getLightLevelFor(time, ownship.getStatus().getLocation());
		final Double lightValue = lookups.getLightLevel().find(lightLevel);

		// and store the parameters
		res.insertElementAt(attenVal, ATTEN_INDEX);
		res.insertElementAt(targetVis, VIS_INDEX);
		res.insertElementAt(seaStateVal, SEA_STATE_INDEX);
		res.insertElementAt(lightValue, LIGHT_INDEX);

		// done
		return res;
	}

}
