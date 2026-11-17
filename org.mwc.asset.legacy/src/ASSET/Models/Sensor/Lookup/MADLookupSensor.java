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


import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Participants.Category;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;

/**
 * Created by IntelliJ IDEA. User: Ian Date: 14-Jan-2004 Time: 21:37:30 To
 * change this template use Options | File Templates.
 */
public class MADLookupSensor extends LookupSensor {

	////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////
	// structure to contain optic lookup set
	//////////////////////////////////////////////////
	public static class MADEnvironment {
		protected StringLookup _targetSize;
		protected String _name;

		public MADEnvironment(final String name, final StringLookup visibility) {
			_name = name;
			_targetSize = visibility;
		}

		public String getName() {
			return _name;
		}

		public StringLookup getTargetVisibility() {
			return _targetSize;
		}

		public void setName(final String name) {
			_name = name;
		}

	}

	////////////////////////////////////////////////////
	// the editor object
	////////////////////////////////////////////////////
	static public class MADLookupInfo extends BaseSensorInfo {
		/**
		 * @param data the Layers themselves
		 */
		public MADLookupInfo(final MADLookupSensor data) {
			super(data);
		}
	}

	////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final static int VIS_INDEX = 0;

	MADEnvironment _defaultLookups = null;

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
	public MADLookupSensor(final int id, final double VDR, final long TBDO, final double MRF, final double CRF,
			final Duration CTP, final double IRF, final Duration ITP) {
		super(id, VDR, TBDO, MRF, CRF, CTP, IRF, ITP, "MAD Sensor");
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
			final LastTargetContact params) {
		WorldDistance res = null;

		// retrieve our parameter
		final double targetVis = ((Double) params.get(VIS_INDEX)).doubleValue();

		// and use the min value.
		final double RP = targetVis;

		// done.
		res = new WorldDistance(RP, WorldDistance.METRES);

		return res;
	}

	/**
	 * see if we can detect this class of target
	 *
	 * @param ownship
	 * @param target
	 * @param env
	 * @return
	 */
	@Override
	protected boolean canDetectThisType(final NetworkParticipant ownship, final ParticipantType target,
			final EnvironmentType env) {
		// first sort out the environment
		MADEnvironment lookups = env.getMADEnvironment();

		if (lookups == null) {
			// bugger. there aren't any. have we created our defaults?
			if (_defaultLookups == null) {
				// nope, better create it
				_defaultLookups = getDefaultLookups();
			}

			lookups = _defaultLookups;
		}

		final boolean res = lookups._targetSize.containsValueFor(target.getCategory().getType());

		return res;
	}

	/**
	 * whether a MAD sensor can be used to ID a target
	 *
	 * @return
	 */
	@Override
	public boolean canIdentifyTarget() {
		return false;
	}

	/**
	 * convenience method to indicate if this sensor produces a bearing from the
	 * sensor to the target. Very few sensors do not produce bearing.
	 *
	 * @return yes/no
	 */
	@Override
	protected boolean canProduceBearing() {
		return false;
	}

	/**
	 * whether this sensor produces range in its output
	 *
	 * @return yes/no
	 */
	@Override
	public boolean canProduceRange() {
		return false;
	}

	protected MADEnvironment getDefaultLookups() {
		// and the target visibility
		final StringLookup visibility = new StringLookup(new String[] { Category.Type.CARRIER, Category.Type.FRIGATE,
				Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL }, new double[] { 3000, 2000, 500, 400 },
				new Double(1000));

		visibility.setUnknownResult(new Double(600));
		final MADEnvironment defaultEnv = new MADEnvironment("sample", visibility);

		return defaultEnv;
	}

	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new MADLookupInfo(this);

		return _myEditor;
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: MADLookupSensor.java,v $
	 * Revision 1.2  2006/09/21 12:20:44  Ian.Mayo
	 * Reflect introduction of default names
	 *
	 * Revision 1.1  2006/08/08 14:21:57  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:26:05  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.5  2004/11/05 15:29:19  Ian.Mayo
	 * Correct tests to reflect new structure of test-read file
	 *
	 * Revision 1.4  2004/11/04 09:30:22  Ian.Mayo
	 * Handle sensors which can't provide range/bearing
	 * <p/>
	 * Revision 1.3  2004/11/03 15:57:34  Ian.Mayo
	 * More MAD testing, & only produce bearing from sensor capable of it.
	 * <p/>
	 * Revision 1.2  2004/11/03 15:42:10  Ian.Mayo
	 * More support for MAD sensors, better use of canDetectThis method
	 * <p/>
	 * Revision 1.1  2004/11/02 20:51:46  ian
	 * Implement MAD sensors
	 * <p/>
	 * Revision 1.21  2004/10/27 15:13:51  Ian.Mayo
	 * Reflect changed structure of MADEnvironment
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
	protected LastTargetContact parametersFor(final NetworkParticipant ownship, final NetworkParticipant target,
			final ScenarioType scenario, final EnvironmentType environment, final long time) {
		final LastTargetContact res = new LastTargetContact();

		MADEnvironment lookups = environment.getMADEnvironment();

		if (lookups == null) {
			// bugger. there aren't any. have we created our defaults?
			if (_defaultLookups == null) {
				// nope, better create it
				_defaultLookups = getDefaultLookups();
			}

			lookups = _defaultLookups;
		}

		/**
		 * now the target visibility
		 *
		 * @see ASSET.Participants.Category.Type.CARRIER
		 */
		final String targetType = target.getCategory().getType();
		final Double targetVis = lookups._targetSize.find(targetType);

		// and store the parameters
		res.insertElementAt(targetVis, VIS_INDEX);

		// done
		return res;
	}

}
