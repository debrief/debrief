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
package org.mwc.debrief.track_shift.zig_detector.ownship;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.mwc.debrief.track_shift.TrackShiftActivator;
import org.mwc.debrief.track_shift.zig_detector.IOwnshipLegDetector;
import org.mwc.debrief.track_shift.zig_detector.Precision;

public class PeakTrackingOwnshipLegDetector implements IOwnshipLegDetector {

	private static final long MIN_LEG_LENGTH = 120000L;

	/**
	 * see if 0.5 represents straight legs or zigs
	 *
	 * @param legId
	 * @param courseDeltas
	 * @return
	 */
	static boolean isHalfLeg(final double[] legId, final double[] courseDeltas) {
		double halfTotal = 0;
		int halfCount = 0;
		double wholeTotal = 0;
		int wholeCount = 0;
		for (int i = 0; i < legId.length; i++) {
			final double thisDelta = Math.abs(courseDeltas[i]);
			if (legId[i] % 1 == 0) {
				wholeTotal += thisDelta;
				wholeCount++;
			} else {
				halfTotal += thisDelta;
				halfCount++;
			}
		}

		final double meanWhole = wholeTotal / wholeCount;
		final double meanHalf = halfTotal / halfCount;

		if (Double.isNaN(meanWhole) || Double.isNaN(meanHalf)) {
			final TrackShiftActivator def = TrackShiftActivator.getDefault();
			if (def != null) {
				def.getLog().log(new Status(IStatus.WARNING,
						"Unable to accurately determine if OS Leg is on 0.5 or 1.0 marker", null));
			} else {
				System.err.println("Unable to accurately determine if OS Leg is on 0.5 or 1.0 marker");
			}
		}

		final boolean res;
		if (meanWhole < meanHalf) {
			res = false;
		} else {
			res = true;
		}

		return res;
	}

	public static double[] makeContinuous(final double[] raw) {
		final double[] res = new double[raw.length];

		for (int i = 0; i < raw.length; i++) {
			final double thisCourse = raw[i];

			if (i == 0) {
				res[i] = thisCourse;
			} else {
				final double lastCourse = res[i - 1];
				final double thisDiff = thisCourse - lastCourse;
				if (Math.abs(thisDiff) > 180d) {
					// ok, we've flippped
					if (thisDiff > 180) {
						// ok, deduct 360
						res[i] = thisCourse - 360d;
					} else {
						// ok, add 360
						res[i] = thisCourse + 360d;
					}
				} else {
					res[i] = thisCourse;
				}
			}
		}

		return res;
	}

	private double[] getChanges(final double[] courseDeltas) {
		final int len = courseDeltas.length;
		final double[] courseChanges = new double[len];
		for (int i = 0; i < len; i++) {
			// look forward
			final double thisDelta = courseDeltas[i];
			if (thisDelta > 0) {
				courseChanges[i] = 1;
			} else {
				courseChanges[i] = -1;
			}
		}
		return courseChanges;
	}

	private double[] getDeltas(final double[] courses) {
		final int len = courses.length;
		final double[] courseDeltas = new double[len];
		for (int i = 0; i < len; i++) {
			// look forward
			if (i < len - 1) {
				final double thisCourse = courses[i];
				final double nextCourse = courses[i + 1];
				courseDeltas[i] = nextCourse - thisCourse;
			} else {
				courseDeltas[i] = 0;
			}
		}
		return courseDeltas;
	}

	private double[] getDiff(final double[] downValues, final double[] upValues) {
		final int len = downValues.length;
		final double[] diff = new double[len];
		for (int i = 0; i < len; i++) {
			diff[i] = Math.abs(downValues[i] - upValues[i]);
		}
		return diff;
	}

	private double[] getDownMax(final double[] peaks, final double[] courses, final double maxCourse) {
		final int len = peaks.length;
		final double[] downMax = new double[len];
		for (int i = 0; i < len; i++) {
			if (i < len - 1) {
				if (peaks[i] == 1) {
					// ok, min point, take the course
					downMax[i] = courses[i];
				} else {
					if (i > 0) {
						// nope, use the previous
						downMax[i] = downMax[i - 1];
					} else {
						downMax[i] = maxCourse;
					}
				}
			} else {
				// populate with min/max values
				downMax[i] = maxCourse;
			}
		}
		return downMax;
	}

	private double[] getDownMin(final double[] peaks, final double[] courses, final double minCourse) {
		final int len = peaks.length;
		final double[] downMin = new double[len];
		for (int i = 0; i < len; i++) {
			if (peaks[i] == -1) {
				// ok, min point, take the course
				downMin[i] = courses[i];
			} else {
				if (i > 0) {
					// nope, use the previous
					downMin[i] = downMin[i - 1];
				} else {
					// use the minimum
					downMin[i] = minCourse;
				}
			}
		}
		return downMin;
	}

	private double[] getLegIds(final boolean[] overThreshold) {
		final int len = overThreshold.length;
		final double[] overT = new double[len];
		for (int i = 0; i < len; i++) {
			final double thisLeg;
			if (i == 0) {
				thisLeg = 0.5;
			} else {
				final double lastLeg = overT[i - 1];
				if (overThreshold[i] != overThreshold[i - 1]) {
					thisLeg = lastLeg + 0.5;
				} else {
					thisLeg = lastLeg;
				}
			}

			overT[i] = thisLeg;
		}
		return overT;
	}

	private boolean[] getOverThreshold(final double[] diffMin, final double[] diffMax, final double tolerance) {
		final int len = diffMin.length;
		final boolean[] overT = new boolean[len];
		for (int i = 0; i < len; i++) {
			overT[i] = diffMin[i] > tolerance && diffMax[i] > tolerance;
		}
		return overT;
	}

	private double[] getPeaks(final double[] courseDeltas) {
		final int len = courseDeltas.length;
		final double[] peaks = new double[len];
		for (int i = 0; i < len; i++) {
			final double thisPeak;
			if (i == 0) {
				thisPeak = 0;
			} else {
				final double thisDelta = courseDeltas[i];
				final double lastDelta = courseDeltas[i - 1];
				if (thisDelta != lastDelta) {
					if (thisDelta > 0 && lastDelta < 0) {
						thisPeak = -1;
					} else {
						thisPeak = 1;
					}
				} else {
					thisPeak = 0;
				}
			}
			peaks[i] = thisPeak;
		}
		return peaks;
	}

	private double[] getUpMax(final double[] peaks, final double[] courses, final double maxCourse) {
		final int len = peaks.length;
		final double[] upMax = new double[len];
		for (int i = len - 1; i >= 0; i--) {
			if (i == len - 1) {
				upMax[i] = maxCourse;
			} else {
				if (peaks[i] == 1) {
					// ok, min point, take the course
					upMax[i] = courses[i + 1];
				} else {
					// nope, use the next
					upMax[i] = upMax[i + 1];
				}
			}
		}
		return upMax;
	}

	private double[] getUpMin(final double[] peaks, final double[] courses, final double minCourse) {
		final int len = peaks.length;
		final double[] upMin = new double[len];
		for (int i = len - 1; i >= 0; i--) {
			if (i == len - 1) {
				upMin[i] = minCourse;
			} else {
				if (peaks[i] == -1) {
					// ok, min point, take the course
					upMin[i] = courses[i + 1];
				} else {
					// nope, use the next
					upMin[i] = upMin[i + 1];
				}
			}
		}
		return upMin;
	}

	@Override
	public List<LegOfData> identifyOwnshipLegs(final long[] times, final double[] rawSpeeds, final double[] rawCourses,
			final int minsOfAverage, final Precision precision) {
		final ArrayList<LegOfData> legs = new ArrayList<LegOfData>();

		// ok, see if we can find the precision
		final double COURSE_TOLERANCE;

		switch (precision) {
		case HIGH:
		case MEDIUM:
		case LOW:
		default:
			COURSE_TOLERANCE = 6; // degs
			break;
		}

		// 1. make continuous dataset
		final double[] courses = makeContinuous(rawCourses);
		final int len = courses.length;

		// collate data
		double minCourse = Double.MAX_VALUE;
		double maxCourse = Double.MIN_VALUE;

		// down first
		for (int i = 0; i < len; i++) {
			final double thisC = courses[i];
			if (thisC < minCourse) {
				minCourse = thisC;
			}
			if (thisC > maxCourse) {
				maxCourse = thisC;
			}
		}

		// collate the course changes
		final double[] courseDeltas = getDeltas(courses);
		final double[] courseChanges = getChanges(courseDeltas);
		final double[] peaks = getPeaks(courseChanges);

		final double[] downMin = getDownMin(peaks, courses, minCourse);
		final double[] downMax = getDownMax(peaks, courses, maxCourse);
		final double[] upMin = getUpMin(peaks, courses, minCourse);
		final double[] upMax = getUpMax(peaks, courses, maxCourse);

		// right, now diff them
		final double[] diffMin = getDiff(downMin, upMin);
		final double[] diffMax = getDiff(downMax, upMax);

		final boolean[] overThreshold = getOverThreshold(diffMin, diffMax, COURSE_TOLERANCE);

		final double[] legId = getLegIds(overThreshold);

		// find out if 0.5 marks straight legs or zigs
		final boolean halfIsLeg = isHalfLeg(legId, courseDeltas);

		// ok, convert them into legs
		LegOfData thisL = null;
		for (int i = 0; i < len; i++) {
			if (i > 0) {
				final double lastLeg = legId[i - 1];
				final double thisLeg = legId[i];

				if (thisLeg != lastLeg) {
					final boolean isWhole = thisLeg % 1 == 0;
					if (halfIsLeg == isWhole) {
						// end of leg

						// do we know the leg?
						if (thisL == null) {
							if (legs.size() == 0) {
								// ok, first leg. don't worry. Start the leg back at the first entry
								thisL = new LegOfData("" + thisLeg);
								thisL.tStart = times[0];
							} else {

								// hey, we shouldn't get here
								throw new RuntimeException("Ownship leg slicing failed: Should have an open leg ready");
							}
						}

						// finish the leg
						thisL.tEnd = times[i];

						// ok, is the leg long enough?
						if (thisL.tEnd - thisL.tStart > MIN_LEG_LENGTH) {
							legs.add(thisL);
						}
						thisL = null;
					} else {
						// start of next leg
						if (thisL != null) {
							// hey, we shouldn't get here
							throw new RuntimeException("Ownship leg slicing failed: Should not have pending open leg");
						}
						thisL = new LegOfData("" + thisLeg);
						thisL.tStart = times[i];
					}
				}
			}
		}

		// ok, do we have a trailing leg?
		if (thisL != null) {
			thisL.tEnd = times[len - 1];
			legs.add(thisL);
		}

		return legs;
	}
}
