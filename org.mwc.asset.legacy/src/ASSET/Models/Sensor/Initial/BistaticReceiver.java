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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Mediums.NarrowbandRadNoise;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import MWC.Algorithms.FrequencyCalcs;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

public class BistaticReceiver extends CoreSensor {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * detection threshold
	 *
	 */
	private final double THRESHOLD = 5; // db

	/**
	 * whether to suppress bistatic hits when they would be obscured by direct path
	 */
	private boolean _suppressDirect = true;

	/**
	 * the angle either side (Degs) that we suppress direct path bearings
	 */
	private double _obscureAngle;

	public BistaticReceiver(final int id) {
		super(id, 0, "Bistatic Receiver");
	}

	@Override
	protected boolean canDetectThisType(final NetworkParticipant ownship, final ParticipantType other,
			final EnvironmentType env) {
		// hey, we can probably detect anything that's not airborne
		return !Category.Environment.AIRBORNE.equals(other.getCategory().getEnvironment());
	}

	@Override
	public boolean canIdentifyTarget() {
		return true;
	}

	@Override
	protected boolean canProduceBearing() {
		return true;
	}

	@Override
	protected DetectionEvent detectThis(final EnvironmentType environment, final ParticipantType host,
			final ParticipantType target, final long time, final ScenarioType scenario) {
		DetectionEvent res = null;

		// is this an active buoy?
		if (Category.Type.BUOY.equals(target.getCategory().getType())) {
			// ok, we don't produce detections of buoys
		} else {
			// ok, now see if we can find any active transmitters
			final ArrayList<ParticipantType> transmitters = new ArrayList<ParticipantType>();

			// loop through the participants
			final Collection<ParticipantType> parts = scenario.getListOfVisibleParticipants();

			for (final Iterator<ParticipantType> iterator = parts.iterator(); iterator.hasNext();) {
				final ParticipantType thisP = iterator.next();

				// is it active?
				if (thisP.radiatesThisNoise(EnvironmentType.NARROWBAND)) {
					// ok, remember it
					transmitters.add(thisP);
				}
			}

			// did we find any?
			if (transmitters.size() > 0) {
				// ok, now consider the two way travel journey
				final Iterator<ParticipantType> iter = transmitters.iterator();
				while (iter.hasNext()) {
					final ParticipantType transmitter = iter.next();

					// sort out the locations
					final WorldLocation txLoc = transmitter.getStatus().getLocation();
					final WorldLocation contactLoc = target.getStatus().getLocation();
					final WorldLocation myLoc = getHostLocationFor(host);

					// what's the bearing to the tx from ux?
					double bearingToTxDegs = MWC.Algorithms.Conversions.Rads2Degs(txLoc.bearingFrom(myLoc));
					if (bearingToTxDegs > 180) {
						bearingToTxDegs -= 180;
					}

					// determine the bistatic propagation loss
					final RadiatedCharacteristics txChars = transmitter.getRadiatedChars();
					final NarrowbandRadNoise radNoise = (NarrowbandRadNoise) txChars
							.getMedium(EnvironmentType.NARROWBAND);
					final double radLevel = radNoise.getBaseNoiseLevel();
					final double baseFreq = radNoise.getFrequency();

					// NOTE:: don't can't just combine the two separate propagation losses.
					// Calculate
					// the loss for the overall range
					final double sep1 = txLoc.rangeFrom(contactLoc);
					final double sep2 = contactLoc.rangeFrom(myLoc);
					final double total_sep = sep1 + sep2;
					// convert to yds
					final double rng_yds = MWC.Algorithms.Conversions.Degs2m(total_sep);

					final WorldDistance range = canProduceRange() ? new WorldDistance(sep2, WorldDistance.DEGS) : null;

					// produce a transmission loss
					final float remainingNoise;
					if (rng_yds == 0) {
						// zero range is a special occurence, since it doesn't get through our log calcs
						remainingNoise = (float) radLevel;
					} else {
						// first sort out the spreading
						remainingNoise = (float) (radLevel - (20f * Math.log(rng_yds) / Math.log(10d)));
					}

					// is this sufficient?
					if (remainingNoise > THRESHOLD) {
						final float bearing = (float) MWC.Algorithms.Conversions
								.Rads2Degs(contactLoc.subtract(myLoc).getBearing());

						final boolean obscured;
						if (_suppressDirect) {
							final double trimmedBearing = bearing > 180 ? bearing - 360 : bearing;

							double brgDelta = trimmedBearing - bearingToTxDegs;
							if (brgDelta > 180) {
								brgDelta -= 360d;
							}
							if (brgDelta < -180) {
								brgDelta += 360d;
							}

							if (Math.abs(brgDelta) < _obscureAngle) {
								obscured = true;
							} else {
								obscured = false;
							}
						} else {
							obscured = false;
						}

						if (!obscured) {
							res = new DetectionEvent(time, host.getId(), myLoc, this, range, null, bearing, null,
									remainingNoise, target.getCategory(), null, null, target);

							// start off by changing the freq from the tx to the target
							final double SpeedOfSound = 1500;
							double osHeadingRads = MWC.Algorithms.Conversions
									.Degs2Rads(transmitter.getStatus().getCourse());
							double tgtHeadingRads = MWC.Algorithms.Conversions
									.Degs2Rads(target.getStatus().getCourse());
							double osSpeed = transmitter.getStatus().getSpeed().getValueIn(WorldSpeed.M_sec);
							double tgtSpeed = target.getStatus().getSpeed().getValueIn(WorldSpeed.M_sec);
							double bearingRads = txLoc.subtract(contactLoc).getBearing();
							double freq = FrequencyCalcs.calcPredictedFreqSI(SpeedOfSound, osHeadingRads,
									tgtHeadingRads, osSpeed, tgtSpeed, bearingRads, baseFreq);

							// now change the freq from the target to the rx
							osHeadingRads = MWC.Algorithms.Conversions.Degs2Rads(target.getStatus().getCourse());
							tgtHeadingRads = MWC.Algorithms.Conversions.Degs2Rads(host.getStatus().getCourse());
							osSpeed = target.getStatus().getSpeed().getValueIn(WorldSpeed.M_sec);
							tgtSpeed = host.getStatus().getSpeed().getValueIn(WorldSpeed.M_sec);
							bearingRads = contactLoc.subtract(myLoc).getBearing();
							freq = FrequencyCalcs.calcPredictedFreqSI(SpeedOfSound, osHeadingRads, tgtHeadingRads,
									osSpeed, tgtSpeed, bearingRads, freq);

							// and store the overall result
							res.setFreq((float) freq);

							// ok - this method can only return one detection.
							return res;
						}
					}
				}
			}
		}

		return res;
	}

	@Override
	public WorldDistance getEstimatedRange() {
		return null;
	}

	@Override
	public EditorType getInfo() {
		return null;
	}

	@Override
	public int getMedium() {
		return EnvironmentType.NARROWBAND;
	}

	public double getObscureAngle() {
		return _obscureAngle;
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public boolean hasEditor() {
		return false;
	}

	public boolean isSuppressDirect() {
		return _suppressDirect;
	}

	public void setObscureAngle(final double obscureAngle) {
		this._obscureAngle = obscureAngle;
	}

	public void setSuppressDirect(final boolean suppressDirect) {
		this._suppressDirect = suppressDirect;
	}

	@Override
	public void update(final DemandedStatus myDemandedStatus, final Status myStatus, final long newTime) {
		// don't bother...
	}
}
