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
package org.mwc.debrief.track_shift.ambiguity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.track_shift.ambiguity.LegOfCuts.WhichBearing;
import org.mwc.debrief.track_shift.ambiguity.LegOfCuts.WhichPeriod;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.Conversions;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;

public class AmbiguityResolver {
	static class LegPermutation {
		final private double[] coreSlopeEarly;
		final private double[] ambigSlopeEarly;
		final private double[] coreSlopeLate;
		final private double[] ambigSlopeLate;

		final private LegOfCuts leg;

		public double coreBefore;
		public double ambigBefore;
		public double coreAfter;
		public double ambigAfter;

		public LegPermutation(final LegOfCuts leg, final double[] coreSlopeEarly, final double[] ambigSlopeEarly,
				final double[] coreSlopeLate, final double[] ambigSlopeLate) {
			this.leg = leg;
			this.coreSlopeEarly = coreSlopeEarly;
			this.ambigSlopeEarly = ambigSlopeEarly;
			this.coreSlopeLate = coreSlopeLate;
			this.ambigSlopeLate = ambigSlopeLate;
		}

	}

	public static class LegsAndZigs {
		final List<LegOfCuts> legs;
		final LegOfCuts zigCuts;

		public LegsAndZigs(final List<LegOfCuts> legs, final LegOfCuts zigCuts) {
			this.legs = legs;
			this.zigCuts = zigCuts;
		}

		public List<LegOfCuts> getLegs() {
			return legs;
		}

		public LegOfCuts getZigs() {
			return zigCuts;
		}

	}

	static class PermScore {

		final private WhichBearing lastB;
		final private WhichBearing thisB;
		final double thisScore;
		private final LegOfCuts lastLeg;
		private final LegOfCuts thisLeg;

		public PermScore(final LegPermutation lastPerm, final LegPermutation thisPerm, final WhichBearing lastB,
				final WhichBearing thisB) {
			this.lastLeg = lastPerm.leg;
			this.thisLeg = thisPerm.leg;
			this.lastB = lastB;
			this.thisB = thisB;

			// retrieve the bearings
			final double beforeBearing;
			switch (lastB) {
			case CORE:
				beforeBearing = lastPerm.coreAfter;
				break;
			case AMBIGUOUS:
			default:
				beforeBearing = lastPerm.ambigAfter;
				break;
			}

			final double afterBearing;
			switch (thisB) {
			case CORE:
				afterBearing = thisPerm.coreBefore;
				break;
			case AMBIGUOUS:
			default:
				afterBearing = thisPerm.ambigBefore;
				break;
			}

			// ok, sort out the minimum angle between the two. Note: the curves are
			// unwrapped, so the difference may be several turns
			thisScore = Math.abs(Conversions.degsDifference(beforeBearing, afterBearing));
		}

		@Override
		public String toString() {
			return "( " + lastB + "-" + thisB + " :" + (int) thisScore + ")";
		}
	}

	public static class ResolvedLeg {
		public final WhichBearing bearing;
		public final LegOfCuts leg;

		public ResolvedLeg(final LegOfCuts leg, final WhichBearing bearing) {
			this.leg = leg;
			this.bearing = bearing;
		}
	}

	/**
	 * helper class that cache's the total score for the list.
	 *
	 * @author Ian
	 *
	 */
	private static class ScoreList extends ArrayList<PermScore> {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		private double _score = 0;
		private boolean closed = false;

		@Override
		public void add(final int index, final PermScore element) {
			if (closed) {
				throw new IllegalArgumentException("Cannot add more items once score is generated");
			}
			super.add(index, element);
		}

		@Override
		public boolean add(final PermScore e) {
			if (closed) {
				throw new IllegalArgumentException("Cannot add more items once score is generated");
			}
			return super.add(e);
		}

		@Override
		public boolean addAll(final Collection<? extends PermScore> c) {
			if (closed) {
				throw new IllegalArgumentException("Cannot add more items once score is generated");
			}
			return super.addAll(c);
		}

		@Override
		public boolean addAll(final int index, final Collection<? extends PermScore> c) {
			if (closed) {
				throw new IllegalArgumentException("Cannot add more items once score is generated");
			}
			return super.addAll(index, c);
		}

		public void finalise() {
			double total = 0;
			for (final PermScore item : this) {
				total += item.thisScore;
			}
			_score = total;

			closed = true;
		}

		/**
		 * this method should only be called when no more items are to be added
		 *
		 * @return
		 */
		private double getScore() {
			return _score;
		}

	}

	static final long OS_TURN_MIN_TIME_INTERVAL = 180L;

	static final double OS_TURN_MIN_COURSE_CHANGE = 10d;

	/**
	 * come up with a chunk size that doesn't leave to few iterations at the end
	 *
	 * @param len
	 * @return
	 */
	static int chunkFor(final int len, final int minChunk) {
		// improve this - so they're always at least 10, but less than 20

		final int MAX_CHUNK_SIZE = 20;

		final int res = -1;
		if (len < MAX_CHUNK_SIZE) {
			return len;
		} else {
			for (int i = MAX_CHUNK_SIZE; i >= minChunk; i--) {
				if ((len % i == 0) || (len % i > minChunk)) {
					return i;
				}
			}
		}
		return res;
	}

	private static void ditchBearingsForThisLeg(final LegOfCuts leg, final WhichBearing bearing) {
		for (final SensorContactWrapper cut : leg) {
			// cool, we have a course - we can go for it. remember the bearings
			final double bearing1 = cut.getBearing();
			final double bearing2 = cut.getAmbiguousBearing();

			switch (bearing) {
			case CORE:
				cut.setBearing(bearing1);
				cut.setAmbiguousBearing(bearing2);
				break;
			case AMBIGUOUS:
			default:
				cut.setBearing(bearing2);
				cut.setAmbiguousBearing(bearing1);
				break;
			}

			// remember we're morally ambiguous
			cut.setHasAmbiguousBearing(false);
		}
	}

	private static void doLog(final Logger logger, final String msg, final HiResDate time) {
		/*
		 * if (logger != null) { final String timeStr = time != null ?
		 * time.getDate().toString() : "";
		 *
		 * logger.log(Level.INFO, timeStr + " " + msg); }
		 */
	}

	private static Comparator<ScoreList> getPermComparator() {
		return new Comparator<ScoreList>() {

			@Override
			public int compare(final ScoreList d1, final ScoreList d2) {
				final int res;

				final double d1Total = d1.getScore();
				final double d2Total = d2.getScore();

				if (d1Total < d2Total) {
					res = -1;
				} else if (d1Total > d2Total) {
					res = 1;
				} else {
					res = 0;
				}

				return res;

			}

		};
	}

	private static List<LegPermutation> getPermutations(final List<LegOfCuts> legs) {
		final List<LegPermutation> listOfPermutations = new ArrayList<LegPermutation>();

		// ok, loop through the legs
		LegOfCuts previousLeg = null;
		LegPermutation lastPerm = null;
		for (final LegOfCuts leg : legs) {

			// generate the curves
			final double[] coreSlopeEarly = leg.getCurve(WhichPeriod.EARLY, WhichBearing.CORE);
			final double[] ambigSlopeEarly = leg.getCurve(WhichPeriod.EARLY, WhichBearing.AMBIGUOUS);
			final double[] coreSlopeLate = leg.getCurve(WhichPeriod.LATE, WhichBearing.CORE);
			final double[] ambigSlopeLate = leg.getCurve(WhichPeriod.LATE, WhichBearing.AMBIGUOUS);

			// now put them into a permutation
			final LegPermutation thisPerm = new LegPermutation(leg, coreSlopeEarly, ambigSlopeEarly, coreSlopeLate,
					ambigSlopeLate);

			// special handling. if this has already been resolved, we can skip it
			// have we already processed a leg?
			if (previousLeg != null) {
				final long midTime = midTimeFor(previousLeg, leg);

				// core permutations
				lastPerm.coreAfter = valueAt(midTime, lastPerm.coreSlopeLate);
				thisPerm.coreBefore = valueAt(midTime, thisPerm.coreSlopeEarly);

				// ambig permutations
				if (thisPerm.ambigSlopeEarly != null) {
					thisPerm.ambigBefore = valueAt(midTime, thisPerm.ambigSlopeEarly);
				}

				if (lastPerm.ambigSlopeLate != null) {
					lastPerm.ambigAfter = valueAt(midTime, lastPerm.ambigSlopeLate);
				}
			}

			// store the leg permutation
			listOfPermutations.add(thisPerm);
			lastPerm = thisPerm;

			// special handling.
			previousLeg = leg;
		}
		return listOfPermutations;
	}

	private static long midTimeFor(final LegOfCuts lastLeg, final LegOfCuts leg) {
		final long startTime = lastLeg.get(lastLeg.size() - 1).getDTG().getDate().getTime();
		final long endTime = leg.get(0).getDTG().getDate().getTime();

		// and the mid-way value
		return startTime + (endTime - startTime) / 2;
	}

	private static boolean nearNorth(final double bearing) {
		return Math.abs(bearing) < 20 || Math.abs(bearing) > 340;
	}

	/**
	 * walk through the list of legs, a chunk at a time
	 *
	 * @param legs bearing data, sliced into legs
	 * @return list of resolved legs of data
	 */
	public static List<ResolvedLeg> resolve(final List<LegOfCuts> legs) {

		final int totalLen = legs.size();

		final int chunk = chunkFor(totalLen, 9);

		// prepare the results
		final List<ResolvedLeg> res = new ArrayList<ResolvedLeg>();

		// now work through in chunk
		int thisStart = 0;
		while (thisStart < totalLen) {
			final int lastIndex = Math.min(totalLen, thisStart + chunk);
			final List<LegOfCuts> thisChunk = legs.subList(thisStart, lastIndex);
			final List<ResolvedLeg> resolved = resolveLegacy(thisChunk);
			res.addAll(resolved);
			thisStart += chunk;
		}
		return res;
	}

	private static List<ResolvedLeg> resolveLegacy(final List<LegOfCuts> legs) {
		final List<ResolvedLeg> res = new ArrayList<ResolvedLeg>();

		// get all the permutations for this set of legs
		final List<LegPermutation> listOfPermutations = getPermutations(legs);

		// ok, now work through the permutations
		final List<ScoreList> overallScores = new ArrayList<ScoreList>();

		// the recursive algorithm we use has a limit of 20 legs,
		// else we will run out of heap space.
		// so, limit it to 20
		if (listOfPermutations.size() > 20) {
			CorePlugin.showMessage("Resolve Ambiguity",
					"Sorry, there is a limit of 20 legs. Please Filter to Time Period to reduce the number of legs being resolved");
			return null;
		}

		// ok, produce a list of permutations, with scores
		walkScores(listOfPermutations, 1, null, null, overallScores);

		// we now need to sort them into ascending order
		final Comparator<ScoreList> sorter = getPermComparator();
		Collections.sort(overallScores, sorter);

		// get the best performing one
		final ArrayList<PermScore> winner = overallScores.get(0);

		// ok, set the legs to the correct permutation
		boolean firstZig = true;
		for (final PermScore zig : winner) {
			if (firstZig) {
				// SPECIAL CASE:
				// for the first zig we handle the previous leg

				// ditch the side we don't want
				ditchBearingsForThisLeg(zig.lastLeg, zig.lastB);

				// remember the leg
				res.add(new ResolvedLeg(zig.lastLeg, zig.lastB));

				firstZig = false;
			}

			// for all legs we handle the following leg

			// ditch the side we don't want
			ditchBearingsForThisLeg(zig.thisLeg, zig.thisB);

			// remember the leg
			res.add(new ResolvedLeg(zig.thisLeg, zig.thisB));
		}

		return res;
	}

	private static double shortAngle(final double brg1, final double brg2) {
		return Conversions.degsDifference(brg2, brg1);
	}

	/**
	 * During a turn the difference between the bearing and ambiguous bearing go
	 * crazy. Exploit this behaviour to find periods when the array is staedy.
	 *
	 * @param sensor                the sensor to process
	 * @param minZig                above this value we treat it as a zig
	 * @param minBoth               below this value we treat it as a leg
	 * @param minLegLength          we need at least this period of cuts to
	 *                              interpret steady bearings as O/S leg
	 * @param logger                where to log the results
	 * @param scores                a time-series of scores, to be plotted in the
	 *                              zone chart
	 * @param trackPeriod           the extent of the parent track, since we don't
	 *                              want to plot outside this
	 * @param osTurnMinCourseChange
	 * @param osTurnMinTimeDelta
	 * @return A collection of legs and zigs.
	 */
	private static LegsAndZigs sliceSensorIntoLegsUsingAmbiguity(final SensorWrapper sensor, final double minZig,
			final double minBoth, final double minLegLength, final Logger logger, final TimeSeries scores,
			final TimePeriod trackPeriod, final Double osTurnMinCourseChange, final Long osTurnMinTimeDelta,
			final Integer allowedLegs) {
		final List<LegOfCuts> legs = new ArrayList<LegOfCuts>();
		final LegOfCuts zigs = new LegOfCuts();

		if (scores != null) {
			scores.clear();
		}

		final Enumeration<Editable> enumer = sensor.elements();
		Double lastDelta = null;
		SensorContactWrapper lastCut = null;
		HiResDate lastTime = null;
		LegOfCuts thisLeg = null;
		LegOfCuts thisZig = null;
		Double lastCourse = null;
		SensorContactWrapper firstCut = null;
		final LegOfCuts possLeg = new LegOfCuts();

		while (enumer.hasMoreElements() && (allowedLegs == null || legs.size() <= allowedLegs)) {
			final SensorContactWrapper cut = (SensorContactWrapper) enumer.nextElement();

			final boolean hasAmbig = !Double.isNaN(cut.getAmbiguousBearing());

			if (cut.getVisible() && hasAmbig && trackPeriod.contains(cut.getDTG())) {
				// ok, TA data
				double delta = cut.getAmbiguousBearing() - cut.getBearing();

				final HiResDate time = cut.getDTG();

				// if (time.getDate().toString().contains("12:32:00"))
				// {
				// System.out.println("here");
				// }

				// is this the first cut?
				if (lastDelta == null) {
					// store it. we'll add it to whatever type of data we build
					firstCut = cut;
				} else {
					// just check that we're not getting some jitter about zero
					if (nearNorth(cut.getAmbiguousBearing()) || nearNorth(cut.getBearing())) {
						// ok, we may be near North, so we may be jittering
						// around zero. See if the has been a huge delta jump
						if (delta - lastDelta > 180) {
							// ok, we're now in 360 domain. Subtract 360 to
							// put us back in the old domain
							delta -= 360;
						} else if (delta - lastDelta < -180) {
							// we've moved to 0 domain. Add 360 to get back in
							// the 360 domain
							delta += 360;
						}
					}

					double valueDelta = delta - lastDelta;

					// if we're not already in a turn, then any
					// monster delta will prob be related to domain
					if (thisLeg != null) {
						valueDelta = Conversions.signedDegs(valueDelta);
					}

					// ok, work out the change rate
					final long timeDeltaMillis = time.getDate().getTime() - lastTime.getDate().getTime();
					final long timeDeltaSecs = timeDeltaMillis / 1000L;

					final double gapRate = Math.abs(valueDelta / timeDeltaSecs);

					// and the delta bearing rate

					final double brgDelta = shortAngle(cut.getBearing(), lastCut.getBearing());
					double ambigBrgDelta = shortAngle(cut.getAmbiguousBearing(), lastCut.getAmbiguousBearing());
					final double sysDelta = ambigBrgDelta - brgDelta;
					if (sysDelta > 180) {
						ambigBrgDelta = -ambigBrgDelta;
					}

					final double brgRate = brgDelta / timeDeltaSecs;

					final boolean TRIP_ZIG = Math.signum(brgDelta) == Math.signum(ambigBrgDelta)
							&& Math.abs(brgRate) > minBoth;

					// introduce an extra test, for if there's a large period of missing data
					boolean MISSING_CUTS = false;

					if (osTurnMinCourseChange != null && osTurnMinTimeDelta != null) {
						final Watchable[] nearest = sensor.getHost().getNearestTo(time);
						if (nearest != null && nearest.length > 0) {
							final double course = MWC.Algorithms.Conversions.Rads2Degs(nearest[0].getCourse());

							if (lastCourse != null) {
								final double courseDelta = Math.abs(shortAngle(course, lastCourse));
								MISSING_CUTS = courseDelta > osTurnMinCourseChange
										&& timeDeltaSecs > osTurnMinTimeDelta;
								if (MISSING_CUTS) {
									// ok, we've had a large course change, and it's been a long
									// time since we last had any data. Assume we're now on a new leg.
									// we can't create a zig for the list, since we don't have
									// any cuts to put into it.
									thisLeg = null;
								}
							}

							// ok, remember it
							lastCourse = course;
						}
					}

					if (scores != null) {
						final FixedMillisecond sec = new FixedMillisecond(time.getDate().getTime());
						final TimeSeriesDataItem item = new TimeSeriesDataItem(sec, gapRate);
						scores.addOrUpdate(item);
					}

					if (logger != null) {
						final String stats = " brg:" + (int) cut.getBearing() + " ambig:"
								+ (int) cut.getAmbiguousBearing() + " step (secs)" + (int) timeDeltaSecs
								+ " gap delta rate:" + gapRate + " lastBrg:" + (int) lastCut.getBearing() + " brg:"
								+ ((int) cut.getBearing()) + " brg delta:" + brgRate + " gap rate:" + gapRate
								+ " OS zig trip:" + TRIP_ZIG;
						doLog(logger, stats, time);
					}

					// note: we ignore the gap rate if we know we've got missing cuts.

					if (TRIP_ZIG || (gapRate > minZig && !MISSING_CUTS)) {
						// ok, were we on a straight leg?
						if (thisLeg != null) {
							// close the leg
							thisLeg = null;
							doLog(logger, " End leg.", time);
						}

						// ok, were we're in a zig?
						if (thisZig == null) {
							// not in a zig. Put us in a zig
							thisZig = new LegOfCuts();
							doLog(logger, " New zig.", time);
						}

						// do we have any pending cuts?
						if (!possLeg.isEmpty()) {
							doLog(logger, " Did have poss straight cuts. Drop them, we're in a turn", time);

							// ok, we have a couple of cuts that look like they're straight.
							// well, they're not. they're actually in a turn
							thisZig.addAll(possLeg);

							// and clear the list
							possLeg.clear();
						}

						// if we have a pending first cut,
						// we should store it
						if (firstCut != null) {
							thisZig.add(firstCut);
							firstCut = null;
						}

						thisZig.add(cut);
					} else {
						boolean straightCutHandled = false;

						if (thisZig != null) {
							// hmm, we were in a turn, and now things are straight.
							// but, we want to allow a number of low-rate-change
							// entries, just in cases there's a coincidental
							// couple of steady cuts during the turn.
							final long thisTime = cut.getDTG().getDate().getTime();

							if (stillCacheing(possLeg, thisTime, (long) minLegLength)) {
								doLog(logger, " Poss straight leg. Cache it.", time);

								// ok, we'll add this to the list
								possLeg.add(cut);

								straightCutHandled = true;
							} else {
								// ok, we were in a turn, now we know we're on a straight leg.
								// finish the zig
								zigs.addAll(thisZig);

								doLog(logger, " Zig ended.", time);

								// close the leg
								thisZig = null;
							}
						}

						if (!straightCutHandled) {
							// ok, we're in a leg
							if (thisLeg == null) {
								doLog(logger, " New Leg.", time);

								thisLeg = new LegOfCuts();

								// right. We've allowed a couple of potential cuts
								// but, we've ended up on a straight leg. Add the stored
								// cuts to the leg
								if (!possLeg.isEmpty()) {
									doLog(logger, " Have poss straight leg cuts.", time);
									thisLeg.addAll(possLeg);
									possLeg.clear();
								}

								legs.add(thisLeg);
							}

							// if we have a pending first cut,
							// we should store it
							if (firstCut != null) {
								thisLeg.add(firstCut);
								firstCut = null;
							}

							thisLeg.add(cut);
						}
					}
				}
				lastDelta = delta;
				lastTime = time;
				lastCut = cut;
			}

		}

		// ok, do some last minute tidying

		// are we still in a zig?
		if (thisZig != null) {
			doLog(logger, "Finishing zig.", null);

			// store the zig cuts
			zigs.addAll(thisZig);
			thisZig = null;
		}

		// do we have any possible straight leg cuts
		if (!possLeg.isEmpty()) {
			doLog(logger, "Append trailing straight cuts.", null);
			thisLeg = new LegOfCuts();

			thisLeg.addAll(possLeg);
			possLeg.clear();

			legs.add(thisLeg);
		}

		// just check we haven't gone over our allowance. We don't mind doing this,
		// since it lets use ensure we've got all of the previous leg
		if (allowedLegs != null && legs.size() > allowedLegs) {
			legs.remove(legs.get(legs.size() - 1));
		}

		return new LegsAndZigs(legs, zigs);
	}

	public static LegsAndZigs sliceTrackIntoLegsUsingAmbiguity(final TrackWrapper track, final double minZig,
			final double minBoth, final double minLegLength, final Logger logger, final TimeSeries scores,
			final Double osTurnMinCourseChange, final Long osTurnMinTimeInterval, final TimePeriod visiblePeriod,
			final Integer maxLegs) {
		final List<LegOfCuts> legs = new ArrayList<LegOfCuts>();
		final LegOfCuts zigCuts = new LegOfCuts();
		final LegsAndZigs res = new LegsAndZigs(legs, zigCuts);

		// ok, go for it
		final BaseLayer sensors = track.getSensors();
		final Enumeration<Editable> numer = sensors.elements();
		while (numer.hasMoreElements()) {
			final SensorWrapper sensor = (SensorWrapper) numer.nextElement();
			if (sensor.getVisible()) {
				final Integer allowedLegs = maxLegs == null ? null : maxLegs - res.legs.size();

				final LegsAndZigs thisL = sliceSensorIntoLegsUsingAmbiguity(sensor, minZig, minBoth, minLegLength,
						logger, scores, visiblePeriod, osTurnMinCourseChange, osTurnMinTimeInterval, allowedLegs);
				if (thisL.legs.size() > 0) {
					res.legs.addAll(thisL.legs);
				}
				if (thisL.zigCuts.size() > 0) {
					res.zigCuts.addAll(thisL.zigCuts);
				}
			}
		}
		return res;
	}

	/**
	 * we've received another steady cut. Do we have enough cuts to treat it as a
	 * leg?
	 *
	 * @param possLeg   the cuts we've cached so far
	 * @param thisTime  the time of the new cut
	 * @param minLength the min period required to treat it as a leg
	 * @return yes/no
	 */
	private static boolean stillCacheing(final LegOfCuts possLeg, final long thisTime, final long minLength) {
		final boolean res;
		if (possLeg.isEmpty()) {
			res = true;
		} else {
			final long legStart = possLeg.get(0).getDTG().getDate().getTime();
			final long elapsed = (thisTime - legStart) / 1000L;
			res = elapsed < minLength;
		}

		return res;
	}

	/**
	 * trim the supplied value to the 0..360 domain
	 *
	 * @param val    value to trim
	 * @param target value we're comparing against - so we keep the subject value
	 *               fairly clsoe to this one
	 *
	 * @return
	 */
	static double trim(final double val, final Double target) {
		double res = val;

		// ok, get trimming
		if ((target == null || target > -270) && (res < -360d)) {
			res += 360d;
		}
		if ((target == null || target < 270) && (res >= 360d)) {
			res -= 360d;
		}

		// ok, special cases. We're looking for a value near 360, but we've only got
		// the value near 050
		if ((target != null && target > 270) && (res <= 270d)) {
			res += 360d;
		}
		if ((target != null && target < -270) && (res >= -270d)) {
			res -= 360d;
		}
		return res;
	}

	static double valueAt(final long time, final double[] slope) {
		return slope[0] + slope[1] * time + slope[2] * Math.pow(time, 2);
	}

	private static void walkScores(final List<LegPermutation> legs, final int curLeg, final ScoreList thisPermSoFar,
			final PermScore lastScore, final List<ScoreList> finishedPerms) {
		// take a deep copy of the clones, since we want independent copies
		final ScoreList newScores = new ScoreList();
		if (thisPermSoFar != null && !thisPermSoFar.isEmpty()) {
			newScores.addAll(thisPermSoFar);
		}

		// and add a new score, if there is one
		if (lastScore != null) {
			newScores.add(lastScore);
		}

		// have we reached the end?
		if (curLeg < legs.size()) {
			// ok, we can add some scores
			final LegPermutation lastPerm = legs.get(curLeg - 1);
			final LegPermutation thisPerm = legs.get(curLeg);

			// ok, increment the counter
			final int thisCount = curLeg + 1;

			// ok, what was the last leg?
			final WhichBearing lastBearing = lastScore == null ? null : lastScore.thisB;

			// ok, sort out the four permutations

			// if we had a previous leg, was if resolved as CORE?
			if (lastBearing == null || lastBearing == WhichBearing.CORE) {
				walkScores(legs, thisCount, newScores,
						new PermScore(lastPerm, thisPerm, WhichBearing.CORE, WhichBearing.CORE), finishedPerms);

				// do we have ambig data?
				if (thisPerm.ambigSlopeEarly != null) {
					walkScores(legs, thisCount, newScores,
							new PermScore(lastPerm, thisPerm, WhichBearing.CORE, WhichBearing.AMBIGUOUS),
							finishedPerms);
				}
			}

			// if we had a previous leg, was if resolved as AMBIGUOUS?
			if ((lastBearing == null || lastBearing == WhichBearing.AMBIGUOUS) && (lastPerm.ambigSlopeLate != null)) {
				walkScores(legs, thisCount, newScores,
						new PermScore(lastPerm, thisPerm, WhichBearing.AMBIGUOUS, WhichBearing.CORE), finishedPerms);
				if (thisPerm.ambigSlopeEarly != null) {
					walkScores(legs, thisCount, newScores,
							new PermScore(lastPerm, thisPerm, WhichBearing.AMBIGUOUS, WhichBearing.AMBIGUOUS),
							finishedPerms);
				}
			}
		} else {
			// no more to be added, let the list sort out it's total
			newScores.finalise();

			// ok, we've reached the end. Store the score.
			finishedPerms.add(newScores);
		}
	}

}
