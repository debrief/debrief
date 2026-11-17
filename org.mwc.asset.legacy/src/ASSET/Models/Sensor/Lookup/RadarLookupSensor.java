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
import MWC.GenericData.WorldLocation;

/**
 * Created by IntelliJ IDEA. User: Ian Date: 14-Jan-2004 Time: 21:37:30 To
 * change this template use Options | File Templates.
 */
public class RadarLookupSensor extends LookupSensor {

	////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////
	// the structure defining the environmental factors common
	// to radar sensors
	//////////////////////////////////////////////////
	public static class RadarEnvironment {
		public IntegerTargetTypeLookup _seaStates;
		public IntegerTargetTypeLookup _sigmaValues;
		private String _myName;

		//////////////////////////////////////////////////
		// constructor
		//////////////////////////////////////////////////
		public RadarEnvironment(final String name, final IntegerTargetTypeLookup seaStates,
				final IntegerTargetTypeLookup sigmaValues) {
			_myName = name;
			_seaStates = seaStates;
			_sigmaValues = sigmaValues;
		}

		//////////////////////////////////////////////////
		// accessors
		//////////////////////////////////////////////////
		public String getName() {
			return _myName;
		}

		public IntegerTargetTypeLookup getSeaStates() {
			return _seaStates;
		}

		public IntegerTargetTypeLookup getSigmaValues() {
			return _sigmaValues;
		}

		public void setName(final String name) {
			_myName = name;
		}

	}

	////////////////////////////////////////////////////
	// the editor object
	////////////////////////////////////////////////////
	static public class RadarLookupInfo extends BaseSensorInfo {
		/**
		 * @param data the Layers themselves
		 */
		public RadarLookupInfo(final RadarLookupSensor data) {
			super(data);
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * list of integer values detailing a relative bearing
	 */
	public static final int DEAD_AHEAD = 0;
	public static final int BOW = 1;

	public static final int BEAM = 2;

	public static final int QUARTER = 3;

	////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	public static final int ASTERN = 4;

	private final static int SEA_STATE_INDEX = 0;

	private final static int SIGMA_VAL = 1;

	/**
	 * calculate the angle-on-the-bow for the target ship compared to ownship
	 *
	 * @param ownship   our location
	 * @param target    his location
	 * @param hisCourse his course (degs)
	 * @return ATB (degs)
	 */
	protected static double calcAspect(final WorldLocation ownship, final WorldLocation target,
			final double hisCourse) {
		double hisRel = ownship.subtract(target).getBearing();
		hisRel = MWC.Algorithms.Conversions.Rads2Degs(hisRel);
		double res = hisCourse - hisRel;
		if (res > 180)
			res -= 360;
		return res;
	}

	/**
	 * examine the relative geometries of the two participants, return the aspect
	 * setting
	 *
	 * @param ownship
	 * @param target
	 * @param targetCourseDegs
	 * @return
	 */
	protected static int determineAspectOf(final WorldLocation ownship, final WorldLocation target,
			final double targetCourseDegs) {
		// ok, what's the ATB
		double ATB = calcAspect(ownship, target, targetCourseDegs);

		// trim to absolue
		ATB = Math.abs(ATB);

		int res = 0;

		if (ATB < 22.5)
			res = DEAD_AHEAD;
		else if (ATB < 67.5)
			res = BOW;
		else if (ATB < 112.5)
			res = BEAM;
		else if (ATB < 157.5)
			res = QUARTER;
		else
			res = ASTERN;

		return res;
	}

	// the value of K for this sensor
	private final double _k;

	/**
	 * our default environment, when necessary.
	 */
	private RadarEnvironment _defaultLookups;

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
	public RadarLookupSensor(final int id, final double VDR, final long TBDO, final double MRF, final double CRF,
			final Duration CTP, final double IRF, final Duration ITP, final double K) {
		super(id, VDR, TBDO, MRF, CRF, CTP, IRF, ITP, "Radar Lookup");

		// and produce the lookup tables
		setDefaultLookups();

		_k = K;

	}

	/**
	 * calculate the predicted range for this contact
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
		final Double seaStateVal = (Double) params.get(SEA_STATE_INDEX);
		final Double sigmaVal = (Double) params.get(SIGMA_VAL);

		// find out the ownship and target height
		final double EARTH_RADIUS = 6371950;
		final double B_FACTOR = 0.7366; // is back calculated from the radar horizon formula in the
										// Merlin Tacman
		final double myHeight = -ownship.getStatus().getLocation().getDepth();
		final double hisHeight = -target.getStatus().getLocation().getDepth();

		// do the calc
		final double RP_1 = Math.sqrt(2 * EARTH_RADIUS * myHeight / B_FACTOR)
				+ Math.sqrt(2 * EARTH_RADIUS * hisHeight / B_FACTOR);

		// and now for the second range calc
		final double RP_2 = _k * Math.pow(sigmaVal.doubleValue() * seaStateVal.doubleValue(), 0.25);

		// and use the min value.
		final double RP = Math.min(RP_1, RP_2);

		// done.
		res = new WorldDistance(RP, WorldDistance.METRES);

		return res;
	}

	@Override
	public boolean canIdentifyTarget() {
		return false; // To change body of implemented methods use File | Settings | File Templates.
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new RadarLookupInfo(this);
		return _myEditor;
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: RadarLookupSensor.java,v $
	 * Revision 1.3  2006/09/21 12:20:45  Ian.Mayo
	 * Reflect introduction of default names
	 *
	 * Revision 1.2  2006/09/12 15:15:34  Ian.Mayo
	 * Sorting out XML import/export & lookup data structures
	 *
	 * Revision 1.1  2006/08/08 14:21:58  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:26:06  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.21  2004/11/04 09:30:23  Ian.Mayo
	 * Handle sensors which can't provide range/bearing
	 *
	 * Revision 1.20  2004/10/27 15:22:10  Ian.Mayo
	 * Fall back on local lookups if environmental ones not set
	 * <p/>
	 * Revision 1.19  2004/10/27 15:14:13  Ian.Mayo
	 * Reflect changed structure of RadarEnvironment
	 * <p/>
	 * Revision 1.18  2004/10/27 14:07:06  Ian.Mayo
	 * Handle default values better
	 * <p/>
	 * Revision 1.17  2004/10/27 13:30:10  Ian.Mayo
	 * More environment handling
	 * <p/>
	 * Revision 1.16  2004/10/26 10:21:57  Ian.Mayo
	 * Move my lookups back up to Env, minor refactoring
	 * <p/>
	 * Revision 1.15  2004/10/25 15:30:24  Ian.Mayo
	 * Start incorporating lookup data tables in environment
	 * <p/>
	 * Revision 1.14  2004/09/06 14:20:10  Ian.Mayo
	 * Provide default icons & properties for sensors
	 * <p/>
	 * Revision 1.13  2004/09/06 14:04:19  Ian.Mayo
	 * Switch to supporting editables in Layer Manager, and showing icon for any editables which have one
	 * <p/>
	 * Revision 1.12  2004/08/31 09:37:02  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 * <p/>
	 * Revision 1.11  2004/08/26 17:05:40  Ian.Mayo
	 * Implement more editable properties
	 * <p/>
	 * Revision 1.10  2004/08/25 11:21:18  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.9  2004/08/23 09:12:34  Ian.Mayo
	 * Update tests to reflect new detection list processing
	 * <p/>
	 * Revision 1.8  2004/08/23 08:06:12  Ian.Mayo
	 * Implement clearing old detection lists, minor refactoring
	 * <p/>
	 * Revision 1.7  2004/08/20 15:08:23  Ian.Mayo
	 * Part way through changing detection cycle so that it doesn't start afresh each time - each sensor removes it's previous calls the next time it is called (to allow for TBDO)
	 * <p/>
	 * Revision 1.6  2004/08/16 10:10:56  Ian.Mayo
	 * Reflect improved sensor performance
	 * <p/>
	 * Revision 1.5  2004/08/12 11:09:29  Ian.Mayo
	 * Respect observer classes refactored into tidy directories
	 * <p/>
	 * Revision 1.4  2004/08/09 15:50:47  Ian.Mayo
	 * Refactor category types into Force, Environment, Type sub-classes
	 * <p/>
	 * Revision 1.3  2004/05/24 15:05:35  Ian.Mayo
	 * Commit changes conducted at home
	 * <p/>
	 * Revision 1.3  2004/04/22 21:38:19  ian
	 * Use corrected algs
	 * <p/>
	 * Revision 1.2  2004/04/08 20:27:29  ian
	 * Restructured contructor for CoreObserver
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:54  ian
	 * no message
	 * <p/>
	 * Revision 1.2  2004/02/18 08:59:52  Ian.Mayo
	 * Tidying
	 * <p/>
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

	////////////////////////////////////////////////////////////
	// member methods
	////////////////////////////////////////////////////////////
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

		RadarEnvironment lookups = environment.getRadarEnvironment();

		// did we find one?
		if (lookups == null) {
			// nope, do we have defaults?
			if (_defaultLookups == null) {
				// nope, better create them
				setDefaultLookups();
			}

			lookups = _defaultLookups;
		}

		// what sort of target is it?
		final String targetType = target.getCategory().getType();

		// first the vis attenuation
		final int relAspect = determineAspectOf(ownship.getStatus().getLocation(), target.getStatus().getLocation(),
				target.getStatus().getCourse());
		final Double sigmaVal = lookups._sigmaValues.find(relAspect, targetType);

		// now the sea-state effects
		final int seaState = environment.getSeaStateFor(time, ownship.getStatus().getLocation());
		final Double seaStateVal = lookups._seaStates.find(seaState, targetType);

		// and store the parameters
		res.insertElementAt(seaStateVal, SEA_STATE_INDEX);
		res.insertElementAt(sigmaVal, SIGMA_VAL);

		// done
		return res;
	}

	private void setDefaultLookups() {

		// start off with the aspect dependency
		final Vector<NamedList> aspects = new Vector<NamedList>(0, 1);
		// datums.add(new Double())

		aspects.add(new NamedList(Category.Type.CARRIER, new double[] { 2000, 8000, 10000, 8000, 2000 }));
		aspects.add(new NamedList(Category.Type.FRIGATE, new double[] { 1000, 3000, 4000, 3000, 1000 }));
		aspects.add(new NamedList(Category.Type.SUBMARINE, new double[] { 0.5, 0.5, 0.5, 0.5, 0.5 }));
		aspects.add(new NamedList(Category.Type.FISHING_VESSEL, new double[] { 5, 8, 10, 8, 5 }));

		// setup the sigma values
		final IntegerTargetTypeLookup sigmaValues = new IntegerTargetTypeLookup(aspects, new Double(1000d));

		// start off with the sea state
		final Vector<NamedList> states = new Vector<NamedList>(0, 1);

		states.add(new NamedList(Category.Type.FRIGATE, new double[] { 1.00, 1.00, 1.00, 1.00, 1.00, 0.95, 0.90 }));
		states.add(new NamedList(Category.Type.CARRIER, new double[] { 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 0.95 }));
		states.add(new NamedList(Category.Type.SUBMARINE, new double[] { 1.00, 1.00, 0.80, 0.75, 0.70, 0.50, 0.30 }));
		states.add(new NamedList(Category.Type.FISHING_VESSEL,
				new double[] { 1.00, 1.00, 0.96, 0.80, 0.75, 0.70, 0.50, 0.90 }));

		final IntegerTargetTypeLookup seaStates = new IntegerTargetTypeLookup(states, new Double(1.0));

		//
		//
		// IntegerTargetTypeLookup seaStates = new IntegerTargetTypeLookup(new int[]{0,
		// 1, 2, 3, 4, 5,
		// 6},
		// new StringLookup[]{
		// new StringLookup(new String[]{Category.Type.CARRIER, Category.Type.FRIGATE,
		// Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL},
		// new double[]{1, 1, 1, 1}, null),
		// new StringLookup(new String[]{Category.Type.CARRIER, Category.Type.FRIGATE,
		// Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL},
		// new double[]{1, 1, 1, 1}, null),
		// new StringLookup(new String[]{Category.Type.CARRIER, Category.Type.FRIGATE,
		// Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL},
		// new double[]{1, 1, 0.8, 0.9}, null),
		// new StringLookup(new String[]{Category.Type.CARRIER, Category.Type.FRIGATE,
		// Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL},
		// new double[]{1, 1, 0.75, 0.8}, null),
		// new StringLookup(new String[]{Category.Type.CARRIER, Category.Type.FRIGATE,
		// Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL},
		// new double[]{1, 1, 0.7, 0.75}, null),
		// new StringLookup(new String[]{Category.Type.CARRIER, Category.Type.FRIGATE,
		// Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL},
		// new double[]{0.95, 1, 0.5, 0.7}, null),
		// new StringLookup(new String[]{Category.Type.CARRIER, Category.Type.FRIGATE,
		// Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL},
		// new double[]{0.9, 0.95, 0.3, 0.5}, null),
		// }, new Double(1.0));

		_defaultLookups = new RadarEnvironment("sample", seaStates, sigmaValues);

	}
}
