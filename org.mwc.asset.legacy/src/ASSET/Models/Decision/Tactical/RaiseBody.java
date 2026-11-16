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

package ASSET.Models.Decision.Tactical;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian Mayo
 * Date: August 2003
 * Log:
 *  $Log: RaiseBody.java,v $
 *  Revision 1.1  2006/08/08 14:21:37  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:25:45  Ian.Mayo
 *  First versions
 *
 *  Revision 1.23  2004/11/01 15:54:56  Ian.Mayo
 *  Reflect new signature of Track Plot Observer
 *
 *  Revision 1.22  2004/08/31 09:36:27  Ian.Mayo
 *  Rename inner static tests to match signature **Test to make automated testing more consistent
 *
 *  Revision 1.21  2004/08/26 16:27:08  Ian.Mayo
 *  Implement editable properties
 *
 *  Revision 1.20  2004/08/25 11:20:44  Ian.Mayo
 *  Remove main methods which just run junit tests
 *
 *  Revision 1.19  2004/08/20 13:32:34  Ian.Mayo
 *  Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
 *
 *  Revision 1.18  2004/08/17 14:22:10  Ian.Mayo
 *  Refactor to introduce parent class capable of storing name & isActive flag
 *
 *  Revision 1.17  2004/08/12 11:09:26  Ian.Mayo
 *  Respect observer classes refactored into tidy directories
 *
 *  Revision 1.16  2004/08/09 15:50:35  Ian.Mayo
 *  Refactor category types into Force, Environment, Type sub-classes
 *
 *  Revision 1.15  2004/08/06 12:52:07  Ian.Mayo
 *  Include current status when firing interruption
 *
 *  Revision 1.14  2004/08/06 11:14:29  Ian.Mayo
 *  Introduce interruptable behaviours, and recalc waypoint route after interruption
 *
 *  Revision 1.13  2004/05/24 15:57:15  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.2  2004/04/08 20:27:18  ian
 *  Restructured contructor for CoreObserver
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:52  ian
 *  no message
 *
 *  Revision 1.12  2004/02/18 08:48:11  Ian.Mayo
 *  Sync from home
 *
 *  Revision 1.10  2003/11/05 09:19:56  Ian.Mayo
 *  Include MWC Model support
 *
 *  Revision 1.9  2003/09/19 13:37:56  Ian.Mayo
 *  Switch to Speed and Distance objects instead of just doubles
 *
 *  Revision 1.8  2003/09/18 14:11:52  Ian.Mayo
 *  Make tests work with new World Speed class
 *
 *  Revision 1.7  2003/09/18 12:12:46  Ian.Mayo
 *  Reflect introduction of World Speed
 *
 *  Revision 1.6  2003/09/11 13:40:43  Ian.Mayo
 *  Classes moved around
 *
 *  Revision 1.5  2003/09/09 15:55:45  Ian.Mayo
 *  Change signature of decision model
 *
 *  Revision 1.4  2003/09/04 15:18:37  Ian.Mayo
 *  Switch to optional parameter values for Move
 *
 *
 */

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Sensor.Initial.DippingActiveBroadbandSensor;
import ASSET.Participants.DemandedSensorStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;

/**
 * If we are in the hover, and the dipping sensor is housed, deploy it.
 */
public class RaiseBody extends CoreDecision implements Serializable {

	//////////////////////////////////////////////////
	// editable properties
	//////////////////////////////////////////////////
	static public class RaiseBodyInfo extends EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public RaiseBodyInfo(final RaiseBody data) {
			super(data, data.getName(), "Edit");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Name", "the name of this decision model"), };
				return res;
			} catch (final IntrospectionException e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	//////////////////////////////////////////////////
	// member methods
	//////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private EditorType _myEditor;

	//////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////
	public RaiseBody() {
		super("Raise Body");
	}

	//////////////////////////////////////////////////
	// property editing
	//////////////////////////////////////////////////

	/**
	 * decide the course of action to take, or return null to no be used
	 *
	 * @param status     the current status of the participant
	 * @param detections the current list of detections for this participant
	 * @param monitor    the object which handles weapons release/detonation
	 * @param newTime    the time this decision is to be made
	 */
	@Override
	public DemandedStatus decide(final Status status, final ASSET.Models.Movement.MovementCharacteristics chars,
			final DemandedStatus demStatus, final DetectionList detections, final ScenarioActivityMonitor monitor,
			final long newTime) {
		DemandedStatus res = null;

		String activity = "";

		// is the body still deployed?
		if (!status.is(DippingActiveBroadbandSensor.ADS_HOUSED)) {
			// no, still not at depth - make another request
			res = new SimpleDemandedStatus(newTime, status);
			res.set(DippingActiveBroadbandSensor.ADS_HOUSED);
			res.unset(DippingActiveBroadbandSensor.ADS_DEPLOYED);
			res.unset(DippingActiveBroadbandSensor.ADS_AT_DEPTH);
			final DemandedSensorStatus dss = new DemandedSensorStatus(EnvironmentType.BROADBAND_ACTIVE, true);
			res.add(dss);
			activity = "Still raising";
		} else {
			// ok - we've finished - just let us return null

		}

		super.setLastActivity(activity);

		return res;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new RaiseBodyInfo(this);

		return _myEditor;
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: RaiseBody.java,v $
	 * Revision 1.1  2006/08/08 14:21:37  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:25:45  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.23  2004/11/01 15:54:56  Ian.Mayo
	 * Reflect new signature of Track Plot Observer
	 *
	 * Revision 1.22  2004/08/31 09:36:27  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 *
	 * Revision 1.21  2004/08/26 16:27:08  Ian.Mayo
	 * Implement editable properties
	 * <p/>
	 * Revision 1.20  2004/08/25 11:20:44  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.19  2004/08/20 13:32:34  Ian.Mayo
	 * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
	 * <p/>
	 * Revision 1.18  2004/08/17 14:22:10  Ian.Mayo
	 * Refactor to introduce parent class capable of storing name & isActive flag
	 * <p/>
	 * Revision 1.17  2004/08/12 11:09:26  Ian.Mayo
	 * Respect observer classes refactored into tidy directories
	 * <p/>
	 * Revision 1.16  2004/08/09 15:50:35  Ian.Mayo
	 * Refactor category types into Force, Environment, Type sub-classes
	 * <p/>
	 * Revision 1.15  2004/08/06 12:52:07  Ian.Mayo
	 * Include current status when firing interruption
	 * <p/>
	 * Revision 1.14  2004/08/06 11:14:29  Ian.Mayo
	 * Introduce interruptable behaviours, and recalc waypoint route after interruption
	 * <p/>
	 * Revision 1.13  2004/05/24 15:57:15  Ian.Mayo
	 * Commit updates from home
	 * <p/>
	 * Revision 1.2  2004/04/08 20:27:18  ian
	 * Restructured contructor for CoreObserver
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:52  ian
	 * no message
	 * <p/>
	 * Revision 1.12  2004/02/18 08:48:11  Ian.Mayo
	 * Sync from home
	 * <p/>
	 * Revision 1.10  2003/11/05 09:19:56  Ian.Mayo
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

	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

	/**
	 * indicate to this model that its execution has been interrupted by another
	 * (prob higher priority) model
	 *
	 * @param currentStatus
	 */
	@Override
	public void interrupted(final Status currentStatus) {
		// ignore.
	}

	/**
	 * reset this decision model
	 */
	@Override
	public void restart() {
	}
}
