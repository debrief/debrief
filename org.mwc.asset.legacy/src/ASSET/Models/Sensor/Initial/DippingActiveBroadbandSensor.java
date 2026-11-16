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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import ASSET.Models.Sensor.DeployableSensor;
import ASSET.Models.Vessels.Helo;
import ASSET.Participants.DemandedSensorStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian.Mayo
 * Date: 03-Sep-2003
 * Time: 09:55:35
 * Log:  $Log:
 *
 */

/**
 * Implementation of a dipping active broadband sensor - such as the ADS
 * deployed by Merlin
 */
public class DippingActiveBroadbandSensor extends ActiveBroadbandSensor implements DeployableSensor {
	//////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////

	//////////////////////////////////////////////////
	// editable properties
	//////////////////////////////////////////////////
	static public class DippingActiveBroadbandSensorInfo extends BaseSensorInfo {

		/**
		 * constructor for editable details
		 *
		 * @param data the object we're going to edit
		 */
		public DippingActiveBroadbandSensorInfo(final DippingActiveBroadbandSensor data) {
			super(data);
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Name", "the name of this sensor"),
						prop("AirLowerRate", "the rate at which the sensor descends in air"),
						prop("WaterLowerRate", "the rate at which the sensor descends in water"),
						prop("AirRaiseRate", "the rate at which the sensor ascends in air"),
						prop("WaterRaiseRate", "the rate at which the sensor ascends in water"),
						prop("DemandedDeployDepth", "the depth to deploy the sensor to"),
						prop("RaisePause", "the period to wait for before pulling the sensor into the host"),
						prop("LowerPause", "the period to wait for before the sensor leaves the host"), };
				return res;
			} catch (final IntrospectionException e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static final String ADS_DEPLOYED = "ADS_DEPLOYED";

	public static final String ADS_HOUSED = "ADS_HOUSED";

	public static final String ADS_AT_DEPTH = "ADS_AT_DEPTH";

	/**
	 * the period which we've waited already (secs)
	 */
	protected double _remainingPausePeriod = 0;

	/**
	 * the depth we are instructed to deploy to
	 */
	private double _demandedDeployDepth;

	/**
	 * the current deployment depth
	 */
	protected double _cableLength;

	/**
	 * the lower rate in water
	 */
	private double _waterLowerRate;

	/**
	 * the lower rate in air (m/sec)
	 */
	private double _airLowerRate;

	/**
	 * the raise rate in water (m/sec)
	 */
	private double _waterRaiseRate;

	/**
	 * the raise rate in air (m/sec)
	 */
	private double _airRaiseRate;

	/**
	 * the pause prior to lowering the sensor (secs)
	 */
	protected double _lowerPause;

	//////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////

	/**
	 * the pause prior to housing the sensor (secs)
	 */
	protected double _raisePause;

	/**
	 * the host id
	 */
	private int _hostId = -1;

	private EditorType _myEditor;

	public DippingActiveBroadbandSensor(final int id) {
		super(id, "Dipping_Active");

		// initialise the cable length
		_cableLength = 0;
	}

	/**
	 * constructor for dipping sensor
	 *
	 * @param id
	 * @param airLowerRate   how fast we lower in air (m/sec)
	 * @param airRaiseRate   how fast we raise in air (m/sec)
	 * @param lowerPause     how long we pause before housing
	 * @param raisePause     how long we pause before lowering
	 * @param waterLowerRate how fast we raise in water (m/sec)
	 * @param waterRaiseRate how fast we lower in water (m/sec)
	 */
	public DippingActiveBroadbandSensor(final int id, final WorldSpeed airLowerRate, final WorldSpeed airRaiseRate,
			final Duration lowerPause, final Duration raisePause, final WorldSpeed waterLowerRate,
			final WorldSpeed waterRaiseRate) {
		this(id);
		this._airLowerRate = airLowerRate.getValueIn(WorldSpeed.M_sec);
		this._airRaiseRate = airRaiseRate.getValueIn(WorldSpeed.M_sec);
		this._lowerPause = lowerPause.getValueIn(Duration.SECONDS);
		this._raisePause = raisePause.getValueIn(Duration.SECONDS);
		this._waterLowerRate = waterLowerRate.getValueIn(WorldSpeed.M_sec);
		this._waterRaiseRate = waterRaiseRate.getValueIn(WorldSpeed.M_sec);
	}

	//////////////////////////////////////////////////
	// getter/setters
	//////////////////////////////////////////////////
	public WorldSpeed getAirLowerRate() {
		return new WorldSpeed(_airLowerRate, WorldSpeed.M_sec);
	}

	public WorldSpeed getAirRaiseRate() {
		return new WorldSpeed(_airRaiseRate, WorldSpeed.M_sec);
	}

	public double getCableLength() {
		return _cableLength;
	}

	public double getDemandedDeployDepth() {
		return _demandedDeployDepth;
	}

	/**
	 * retrieve the host id
	 */
	@Override
	public int getHostId() {
		return _hostId;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new DippingActiveBroadbandSensorInfo(this);

		return _myEditor;
	}

	/**
	 * retrieve the current location of the sensor
	 */
	@Override
	public WorldLocation getLocation(final WorldLocation hostLocation) {
		// create a new location where we've descended by the cable length
		final WorldLocation res = hostLocation.add(new WorldVector(0, 0, _cableLength));
		return res;
	}

	public Duration getLowerPause() {
		return new Duration(_lowerPause, Duration.SECONDS);
	}

	public Duration getRaisePause() {
		return new Duration(_raisePause, Duration.SECONDS);
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: DippingActiveBroadbandSensor.java,v $
	 * Revision 1.2  2006/09/21 12:20:41  Ian.Mayo
	 * Reflect introduction of default names
	 *
	 * Revision 1.1  2006/08/08 14:21:54  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:26:03  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.8  2004/09/24 11:08:11  Ian.Mayo
	 * Tidy test names
	 *
	 * Revision 1.7  2004/09/06 14:20:06  Ian.Mayo
	 * Provide default icons & properties for sensors
	 * <p/>
	 * Revision 1.6  2004/08/31 09:36:56  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 * <p/>
	 * Revision 1.5  2004/08/27 09:53:32  Ian.Mayo
	 * Continue to implement testing
	 * <p/>
	 * Revision 1.4  2004/08/26 17:05:36  Ian.Mayo
	 * Implement more editable properties
	 * <p/>
	 * Revision 1.3  2004/08/25 11:21:09  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.2  2004/05/24 15:06:21  Ian.Mayo
	 * Commit changes conducted at home
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:54  ian
	 * no message
	 * <p/>
	 * Revision 1.1  2004/02/16 13:41:40  Ian.Mayo
	 * Renamed class structure
	 * <p/>
	 * Revision 1.12  2003/11/05 09:19:08  Ian.Mayo
	 * Include MWC Model support
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
	}

	public WorldSpeed getWaterLowerRate() {
		return new WorldSpeed(_waterLowerRate, WorldSpeed.M_sec);
	}

	public WorldSpeed getWaterRaiseRate() {
		return new WorldSpeed(_waterRaiseRate, WorldSpeed.M_sec);
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
	 * handle the demanded change in sensor lineup
	 *
	 * @param status
	 */
	@Override
	public void inform(final DemandedSensorStatus status) {
		// handle being switched off/on by the core class
		super.inform(status);

		// now handle the depth
		if (status.getDeployDepth() != null) {
			_demandedDeployDepth = status.getDeployDepth().doubleValue();
		}
	}

	/**
	 * whether the sensor is at the demanded depth
	 *
	 * @param myDepth the platform depth/altitude
	 * @return yes/no
	 */
	protected boolean isAtDepth(final double myDepth) {
		// what's our altitude?
		final double alt = Math.abs(myDepth);

		// what's the buoy depth?
		final double depth = _cableLength - alt;

		// are we there?
		final boolean res = (depth == _demandedDeployDepth);

		return res;
	}

	/**
	 * whether the sensor is currently stowed. Stowage depends on the cable length
	 * being zero and no remaining pause period
	 *
	 * @return yes/no
	 */
	protected boolean isHoused() {
		return (_cableLength == 0) && (_remainingPausePeriod == 0);
	}

	/**
	 * move the body down through the air/water column
	 *
	 * @param period how long to descend for
	 * @param depth  the current depth/height of the host platform
	 */
	protected void lower(final long period, final double depth) {

		// get the period in millis
		double period_secs = period / 1000d;

		// have we included the pause yet?
		if (_remainingPausePeriod > 0) {
			double pauseObtained = _remainingPausePeriod;

			// yes - we need to include the pause -
			pauseObtained = Math.min(pauseObtained, period_secs);

			// shrink the remaining time
			period_secs -= pauseObtained;

			// and update how long we have paused for
			_remainingPausePeriod -= pauseObtained;

		}

		// how long will we descend in air?
		double airTime = 0;

		// are we currently in air?
		if (_cableLength < Math.abs(depth)) {
			// how much more do we need to extend in air?
			final double airExtensionRequired = Math.abs(depth) - _cableLength;

			// yes, how long will it take to drop in air?
			airTime = airExtensionRequired / _airLowerRate;

			// trim it
			airTime = Math.min(airTime, period_secs);

			// drop by the air dist
			_cableLength += airTime * _airLowerRate;
		}

		final double waterTime = period_secs - airTime;

		// do we have any time left
		if (waterTime > 0) {
			// how much further will we have to go?
			final double depthChange = _demandedDeployDepth - (_cableLength - Math.abs(depth));

			// how long will it take to get to the demanded depth
			double depthChangeTime = depthChange / _waterLowerRate;

			// and trim it
			depthChangeTime = Math.min(depthChangeTime, waterTime);

			// and do the drop
			_cableLength += depthChangeTime * _waterLowerRate;

		}

	}

	/**
	 * move the body up through the air/water column
	 *
	 * @param period how long to ascend for
	 * @param depth  the current depth/height of the host platform
	 */
	protected void raise(final long period, final double depth) {

		// get the period in millis
		double period_secs = period / 1000d;

		// how long will we ascend in water?
		double waterTime = 0;

		// are we currently in air?
		if (_cableLength > Math.abs(depth)) {
			// how much more do we need to extend in water?
			final double waterContractionRequired = _cableLength - Math.abs(depth);

			// yes, how long will it take to drop in water?
			waterTime = waterContractionRequired / _waterRaiseRate;

			// trim it
			waterTime = Math.min(waterTime, period_secs);

			// drop by the air dist
			_cableLength -= waterTime * _waterRaiseRate;
		}

		final double airTime = period_secs - waterTime;

		// do we have any time left
		if (airTime > 0) {
			// how much further will we have to go?
			final double depthChange = _cableLength;

			// how long will it take to get to the demanded depth
			double depthChangeTime = depthChange / _airRaiseRate;

			// and trim it
			depthChangeTime = Math.min(depthChangeTime, airTime);

			// and do the drop
			_cableLength -= depthChangeTime * _airRaiseRate;

		}

		// SPECIAL PROCESSING
		// the raise pause is inserted just before we house the sensor - so
		// keep the sensor just below the housing for the pause period

		// is it in all the way yet?
		if (_cableLength == 0) {
			if (_remainingPausePeriod > 0) {
				double pauseObtained = _remainingPausePeriod;

				// yes - we need to include the pause -
				pauseObtained = Math.min(pauseObtained, period_secs);

				// shrink the remaining time
				period_secs -= pauseObtained;

				// and update how long we have paused for
				_remainingPausePeriod -= pauseObtained;
			}
		}
	}

	public void setAirLowerRate(final WorldSpeed airLowerRate) {
		this._airLowerRate = airLowerRate.getValueIn(WorldSpeed.M_sec);
	}

	public void setAirRaiseRate(final WorldSpeed airRaiseRate) {
		this._airRaiseRate = airRaiseRate.getValueIn(WorldSpeed.M_sec);
	}

	public void setDemandedDeployDepth(final double demandedDeployDepth) {
		this._demandedDeployDepth = demandedDeployDepth;
	}

	private void setLoweringPausePeriod() {
		_remainingPausePeriod = _lowerPause;
	}

	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

	public void setLowerPause(final Duration lowerPause) {
		this._lowerPause = lowerPause.getValueIn(Duration.SECONDS);
	}

	//////////////////////////////////////////////////
	// property editing
	//////////////////////////////////////////////////

	public void setRaisePause(final Duration raisePause) {
		this._raisePause = raisePause.getValueIn(Duration.SECONDS);
	}

	/**
	 * set the period to wait prior to housing the body
	 */
	private void setRaisingPausePeriod() {
		_remainingPausePeriod = _raisePause;
		// todo: insert the sea state variable
	}

	public void setWaterLowerRate(final WorldSpeed waterLowerRate) {
		this._waterLowerRate = waterLowerRate.getValueIn(WorldSpeed.M_sec);
	}

	public void setWaterRaiseRate(final WorldSpeed waterRaiseRate) {
		this._waterRaiseRate = waterRaiseRate.getValueIn(WorldSpeed.M_sec);
	}

	//////////////////////////////////////////////////
	// member methods
	//////////////////////////////////////////////////
	/**
	 * if this sensor has a dynamic behaviour, update it according to the demanded
	 * status
	 *
	 * @param myDemandedStatus
	 * @param myStatus
	 * @param newTime
	 */
	@Override
	public void update(final DemandedStatus myDemandedStatus, final Status myStatus, final long newTime) {
		// handle the active/inactive requests
		super.update(myDemandedStatus, myStatus, newTime);

		// remember the host id
		_hostId = myStatus.getId();

		if (myDemandedStatus == null) {
			// hey, just ignore it
		} else {
			// are we meant to be heading for the dip?
			if (myDemandedStatus.is(ADS_DEPLOYED)) {
				// yes - are we stationary yet?
				if (myStatus.is(Helo.HoverStates.IN_HOVER)) {
					// is this the start of the deployment
					if (this.isHoused()) {
						// insert the lowering delay
						setLoweringPausePeriod();
					}

					// yes, start deploying
					lower(newTime - myStatus.getTime(), myStatus.getLocation().getDepth());

					// update the status to show it's not housed
					myStatus.unset(ADS_HOUSED);

					// and update the actual status
					myStatus.set(ADS_DEPLOYED);

					// did we finish ?
					if (isAtDepth(myStatus.getLocation().getDepth())) {
						// remove from dem status
						myDemandedStatus.unset(ADS_DEPLOYED);

						myStatus.set(ADS_AT_DEPTH);

						// and reset the pause period
						_remainingPausePeriod = _lowerPause;

					} else {
						// nothing to update in the state as yet
					}
				} else {
					// no, we're not in hover - carry on waiting
				}
			} else if (myDemandedStatus.is(ADS_HOUSED)) {

				// indicate we're no longer deploying
				myStatus.unset(ADS_AT_DEPTH);

				// have we just started to raise?
				if (this._remainingPausePeriod == 0)
					setRaisingPausePeriod();

				// just check we're not already housed
				if (!isHoused()) {
					// yes, start reeling
					raise(newTime - myStatus.getTime(), myStatus.getLocation().getDepth());

					// did we finish?
					if (isHoused()) {

						// yes,
						// remove from dem status
						myDemandedStatus.unset(ADS_HOUSED);

						// and update the actual status
						myStatus.set(ADS_HOUSED);

						// update the status to show it's not deployed
						myStatus.unset(ADS_DEPLOYED);

					}
				}
			} else {
				// hey, we're not being told to raise or lower the body - we must
				// be off doing something else.
				// Reset stuff to leave ourselves in a tidy state
				// todo: find out what to reset
			}
		}

	}

}
