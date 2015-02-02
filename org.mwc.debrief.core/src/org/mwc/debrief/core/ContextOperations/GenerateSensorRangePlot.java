/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.core.ContextOperations;

import java.awt.Color;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.cmap.xyplot.views.XYPlotView;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.Tools.Tote.toteCalculation;
import Debrief.Tools.Tote.Calculations.rangeCalc;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.SplittableLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.JFreeChart.ColouredDataItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

/**
 * embedded class to generate menu-items for creating tactical plot
 */
public class GenerateSensorRangePlot implements RightClickContextItemGenerator
{

	/**
	 * add items to the popup menu (if suitable tracks are selected)
	 * 
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	public void generate(final IMenuManager parent, final Layers theLayers,
			final Layer[] parentLayers, final Editable[] subjects)
	{
		final Vector<SensorWrapper> sensorCandidates = new Vector<SensorWrapper>(0,
				1);
		final Vector<TrackWrapper> trackCandidates = new Vector<TrackWrapper>(0, 1);
		boolean duffItemFound = false;

		// right, go through the items and have a nice look at them
		for (int i = 0; i < subjects.length; i++)
		{
			final Editable thisE = subjects[i];

			// is this one we can watch?
			if (thisE instanceof SensorWrapper)
			{
				// cool, go for it
				sensorCandidates.add((SensorWrapper) thisE);
			}
			else if (thisE instanceof SplittableLayer)
			{
				// aah, this holds children - go through them. It may be the
				// 'sensors' layer
				final SplittableLayer layer = (SplittableLayer) thisE;
				final Enumeration<Editable> children = layer.elements();
				if (children != null)
				{
					while (children.hasMoreElements())
					{
						final Editable thisC = children.nextElement();
						// is this a sensor wrapper layer?
						if (thisC instanceof SensorWrapper)
						{
							sensorCandidates.add((SensorWrapper) thisC);
						}
					}
				}
			}
			else if (thisE instanceof TrackWrapper)
			{
				// aah, have we already got a primary?
				trackCandidates.add((TrackWrapper) thisE);
			}
			else
				duffItemFound = true;
		}

		if ((trackCandidates.size() >= 1) && (sensorCandidates.size() >= 1))
		{
			if (duffItemFound)
			{
				return;
			}
			if ((trackCandidates.size() > 1) && (sensorCandidates.size() > 1))
			{
				return; // we can't generate multiple sensors versus multiple tracks
			}
			else
			{
				// ok, create the action
				final Action viewPlot = getAction(sensorCandidates, trackCandidates);

				// ok - set the image descriptor
				viewPlot.setImageDescriptor(DebriefPlugin
						.getImageDescriptor("icons/document_chart.png"));

				parent.add(new Separator());
				parent.add(viewPlot);
			}
		}
	}

	/**
	 * wrap the action generation bits in a convenience method (suitable for
	 * overring in tests)
	 * 
	 * @param sensorCandidates
	 *          the sensors to measure the range from
	 * @param trackCandidates
	 *          the tracks to measure to
	 * @return
	 */
	protected Action getAction(final Vector<SensorWrapper> sensorCandidates,
			final Vector<TrackWrapper> trackCandidates)
	{
		final String title;
		if (sensorCandidates.size() > 1)
			title = "View sensor range plot (one track versus several sensors)";
		else if (trackCandidates.size() == 1)
			title = "View sensor range plot (one sensor versus one track)";
		else
		{
			title = "View sensor range plot (one sensor versus several tracks)";
		}

		return new Action(title)
		{
			public void run()
			{

				final IWorkbench wb = PlatformUI.getWorkbench();
				final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				final IWorkbenchPage page = win.getActivePage();

				final IEditorPart editor = page.getActiveEditor();

				try
				{
					// ///////////////////////////////////
					// NOW for the time range
					// ///////////////////////////////////

					// have a go at determining the plot id
					final TimeProvider tp = (TimeProvider) editor
							.getAdapter(TimeProvider.class);
					String thePlotId = null;
					if (tp != null)
					{
						thePlotId = tp.getId();
					}

					// calculate the outer range of the time periods
					final Iterator<TrackWrapper> tIter = trackCandidates.iterator();
					TimePeriod outerPeriod = null;
					while (tIter.hasNext())
					{
						final TrackWrapper trackWrapper = (TrackWrapper) tIter.next();
						if (trackWrapper != null)
						{
							if (outerPeriod == null)
							{
								outerPeriod = new TimePeriod.BaseTimePeriod(
										trackWrapper.getStartDTG(), trackWrapper.getEndDTG());
							}
							else
							{
								outerPeriod.extend(trackWrapper.getStartDTG());
								outerPeriod.extend(trackWrapper.getEndDTG());
							}
						}
					}

					final HiResDate startTime = outerPeriod.getStartDTG();
					final HiResDate endTime = outerPeriod.getEndDTG();

					final TimeSeriesCollection theSeriesCollection = new TimeSeriesCollection();

					final boolean multiTrackRun = trackCandidates.size() > 1;

					// is this a multi-track run?
					final String theTitle;

					if (multiTrackRun)
					{
						// multi-track run, go through them
						final Iterator<TrackWrapper> iter = trackCandidates.iterator();
						while (iter.hasNext())
						{
							final TrackWrapper thisTrack = (TrackWrapper) iter.next();

							// do run for this track
							addDataSeries(thisTrack, sensorCandidates, startTime, endTime,
									theSeriesCollection, !multiTrackRun);

						}
						theTitle = "(Sensor) Range to "
								+ sensorCandidates.firstElement().getName() + " vs Time plot";

					}
					else
					{
						final TrackWrapper primaryTrack = trackCandidates.firstElement();

						// single track run, do as normal
						addDataSeries(primaryTrack, sensorCandidates, startTime, endTime,
								theSeriesCollection, !multiTrackRun);
						theTitle = "(Sensor) Range to " + primaryTrack.getName()
								+ " vs Time plot";
					}

					// aah, did it work?
					if (theSeriesCollection.getSeriesCount() >= 1)
					{
						// and the plot itself
						final String plotId = "org.mwc.cmap.xyplot.views.XYPlotView";
						page.showView(plotId, theTitle, IWorkbenchPage.VIEW_ACTIVATE);

						// ok, try to retrieve the view
						final IViewReference plotRef = page.findViewReference(plotId,
								theTitle);
						final XYPlotView plotter = (XYPlotView) plotRef.getView(true);

						// check we were able to retrieve units
						final String units = new rangeCalc().getUnits();
						if (units == null)
						{
							DebriefPlugin
									.logError(
											Status.ERROR,
											"Unable to show sensor plot, preferred range units not available",
											null);
							return;
						}

						final String yLabel = "Range (" + units + ")";

						// create the plot
						plotter.showPlot(theTitle, theSeriesCollection, yLabel, null,
								thePlotId);
					}
					else
					{
						CorePlugin.showMessage("View sensor range plot",
								"Sorry, the track and sensor datasets do not overlap");

					}
				}
				catch (final PartInitException e)
				{
					e.printStackTrace();
				}

			}
		};
	}

	/**
	 * Collate the data points to plot
	 * 
	 * @param primaryTrack
	 *          the primary track
	 * @param theSensors
	 *          the selected set of sensors
	 * @param start_time
	 *          the start time selected
	 * @param end_time
	 *          the end time selected
	 * @param useSensorName
	 *          whether to label the lines with the sensor name or the track name
	 * @return the dataset to plot
	 * @see toteCalculation#isWrappableData
	 * @see toteCalculation#calculate(Watchable primary,Watchable
	 *      secondary,HiResDate thisTime)
	 * @see Debrief.Tools.FilterOperations.ShowTimeVariablePlot3.CalculationHolder#isARelativeCalculation
	 * @see WatchableList#getItemsBetween(HiResDate start,HiResDate end)
	 * @see TimeSeriesCollection#addSeries(BasicTimeSeries series)
	 */
	@SuppressWarnings("unused")
	private static void addDataSeries(final TrackWrapper primaryTrack,
			final Vector<SensorWrapper> theSensors, final HiResDate start_time,
			final HiResDate end_time, final TimeSeriesCollection theSeriesCollection,
			final boolean useSensorName)
	{

		TimeSeriesCollection seriesCollection = theSeriesCollection;
		// calculate the data variables for our tracks
		final Enumeration<SensorWrapper> iter = theSensors.elements();
		while (iter.hasMoreElements())
		{
			final SensorWrapper thisSensor = (SensorWrapper) iter.nextElement();

			// ok, now collate the data
			String nameToUse;
			if (useSensorName)
				nameToUse = thisSensor.getName();
			else
				nameToUse = primaryTrack.getName();
			final TimeSeries thisSeries = new TimeSeries(nameToUse);

			// SPECIAL CASE - is this an empty sensor, created just to produce this
			// plt?
			// if (thisSensor.elements().hasMoreElements())
			if (false)
			{

				// // nope, we have sensor data, calculate a data point for each sensor
				// cut
				//
				// // ////////////////////////////////////////////////////
				// // step through the track
				// //
				// Collection<Editable> sensorFixes = thisSensor.getItemsBetween(
				// start_time, end_time);
				//
				// // have we found any?. Hey, listen here. The "getItemsBetween" method
				// // may
				// // return
				// // data items, but we may still not be able to do the calc (such as
				// if
				// // we
				// // have "NaN" for depth). So
				// // we still do a sanity check at the end of this method to stop us
				// // adding
				// // empty data series to the collection.
				// if (sensorFixes != null)
				// {
				// // ////////////////////////////////////////////////
				// // CASE 3 - both tracks have time data, relative calc
				// // ////////////////////////////////////////////////
				// // yes, we do have DTG data for this track - hooray!
				//
				// // ok, step through the list
				// Iterator<Editable> theseCuts = sensorFixes.iterator();
				//
				// throughThisTrack: while (theseCuts.hasNext())
				// {
				// SensorContactWrapper thisSecondary = (SensorContactWrapper) theseCuts
				// .next();
				//
				// Color thisColor;
				// if (useSensorName)
				// thisColor = thisSecondary.getColor();
				// else
				// thisColor = primaryTrack.getColor();
				//
				// // what's the current time?
				// HiResDate currentTime = thisSecondary.getTime();
				//
				// // is this fix visible?
				// if (thisSecondary.getVisible())
				// {
				// // the point on the primary track we work with
				// Watchable thisPrimary = null;
				//
				// // find the fix on the primary track which is nearest in
				// // time to this one (if we need to)
				// Watchable[] nearList;
				//
				// // temp switch on interpolation
				// Boolean oldInterp = null;
				// if (primaryTrack instanceof TrackWrapper)
				// {
				// TrackWrapper tw = (TrackWrapper) primaryTrack;
				// oldInterp = tw.getInterpolatePoints();
				// tw.setInterpolatePoints(true);
				// }
				//
				// // find it's nearest point on the primary track
				// nearList = primaryTrack.getNearestTo(currentTime);
				//
				// // and restore the interpolate points setting
				// if (oldInterp != null)
				// {
				// TrackWrapper tw = (TrackWrapper) primaryTrack;
				// tw.setInterpolatePoints(oldInterp.booleanValue());
				// }
				//
				// // yes. right, we only perform a calc if we have primary data
				// // for this point
				// if (nearList.length == 0)
				// {
				// // drop out, and wait for the next cycle
				// continue throughThisTrack;
				// }
				// else
				// {
				// thisPrimary = nearList[0];
				// }
				//
				// // ////////////////////////////////////////////////
				// // produce the new calculated value
				// // ////////////////////////////////////////////////
				//
				// WorldDistance range = new WorldDistance(1, WorldDistance.DEGS);
				// WorldLocation trackLoc = thisPrimary.getLocation();
				// WorldLocation sensorLoc = thisSecondary.getCalculatedOrigin(null);
				// range = trackLoc.rangeFrom(sensorLoc, range);
				// double thisVal = range.getValueIn(WorldDistance.METRES);
				//
				// // ////////////////////////////////////////////////
				// // and create the point
				// // ////////////////////////////////////////////////
				//
				// // HI-RES NOT DONE - FixedMillisecond should be converted some-how
				// // to
				// // FixedMicroSecond
				// ColouredDataItem newItem = new ColouredDataItem(
				// new FixedMillisecond(thisSecondary.getDTG().getDate()
				// .getTime()), thisVal, thisColor, true, null);
				//
				// thisSeries.add(newItem);
				// } // whether this point is visible
				// } // stepping through this track
				//
				// } // if this collection actually had data

			}
			else
			{
				// ok, just calculate 'theoretical' sensor locations
				// ////////////////////////////////////////////////////
				// step through the track
				//
				final Collection<Editable> primaryFixes = primaryTrack.getItemsBetween(
						start_time, end_time);

				// have we found any?. Hey, listen here. The "getItemsBetween" method
				// may
				// return
				// data items, but we may still not be able to do the calc (such as if
				// we
				// have "NaN" for depth). So
				// we still do a sanity check at the end of this method to stop us
				// adding
				// empty data series to the collection.
				if (primaryFixes != null)
				{
					// ////////////////////////////////////////////////
					// CASE 3 - both tracks have time data, relative calc
					// ////////////////////////////////////////////////
					// yes, we do have DTG data for this track - hooray!

					// ok, step through the list
					final Iterator<Editable> theseFixes = primaryFixes.iterator();

					while (theseFixes.hasNext())
					{
						final FixWrapper thisPosition = (FixWrapper) theseFixes.next();

						final Color thisColor;

						if (useSensorName)
						{
							// try to find the nearest sensor to toNow
							final Watchable[] thisCut = thisSensor.getNearestTo(thisPosition
									.getDateTimeGroup());
							if ((thisCut != null) && (thisCut.length > 0))
								thisColor = thisCut[0].getColor();
							else
								thisColor = thisSensor.getColor();
						}
						else
							thisColor = thisPosition.getColor();

						// what's the current time?
						final HiResDate currentTime = thisPosition.getTime();

						// is this fix visible?
						if (thisPosition.getVisible())
						{
							// ok, now get the sensor
							final WorldLocation sensorLoc = thisSensor
									.getHost()
									.getBacktraceTo(currentTime, thisSensor.getSensorOffset(),
											thisSensor.getWormInHole()).getLocation();

							// ok, is the sensor's parent track 'live' at this time?
							if (sensorLoc == null)
							{
								// don't bother processing this time-stamp any more
							}
							else
							{

								// ////////////////////////////////////////////////
								// produce the new calculated value
								// ////////////////////////////////////////////////

								WorldDistance range = new WorldDistance(1, WorldDistance.DEGS);
								final WorldLocation trackLoc = thisPosition.getLocation();
								range = trackLoc.rangeFrom(sensorLoc, range);

								rangeCalc rc = new rangeCalc();
								String units = rc.getUnits();

								// check we were able to retrieve units
								if (units == null)
								{
									DebriefPlugin
											.logError(
													Status.ERROR,
													"Unable to show sensor plot, preferred range units not available",
													null);
									return;
								}

								final double thisVal = rangeCalc.convertRange(
										range.getValueIn(WorldDistance.DEGS), units);

								// ////////////////////////////////////////////////
								// and create the point
								// ////////////////////////////////////////////////

								// HI-RES NOT DONE - FixedMillisecond should be converted
								// some-how
								// to
								// FixedMicroSecond
								final ColouredDataItem newItem = new ColouredDataItem(
										new FixedMillisecond(thisPosition.getDTG().getDate()
												.getTime()), thisVal, thisColor, true, null);

								thisSeries.add(newItem);
							}
						} // whether this point is visible
					} // stepping through this track

				} // if this collection actually had data

			}

			// did we find anything?
			if (thisSeries != null)
				if (thisSeries.getItemCount() > 0)
				{
					CorePlugin.logError(Status.OK, "created:" + thisSeries.getItemCount()
							+ " pts", null);
					seriesCollection.addSeries(thisSeries);
				}

		} // looping through the tracks

		if (seriesCollection.getSeriesCount() == 0)
			seriesCollection = null;
	}

	public static class TestCalcs extends TestCase
	{
		protected Vector<SensorWrapper> _candidates;
		protected Vector<TrackWrapper> _primary;

		public void testDumy()
		{

		}

		// TODO FIX-TEST
		public void NtestIt()
		{
			final TrackWrapper ownship = new TrackWrapper();
			ownship.setName("ownship");
			for (int i = 0; i < 35; i++)
			{
				final FixWrapper scw = getFix(i * 4000, new WorldLocation(1, i, 0),
						ownship);
				ownship.add(scw);
			}

			final SensorWrapper sw = new SensorWrapper("s1");
			sw.setHost(ownship);
			for (int i = 0; i < 5; i++)
			{
				final SensorContactWrapper scw = getContact(i * 5000, sw);
				sw.add(scw);
			}
			final SensorWrapper s2 = new SensorWrapper("s12");
			s2.setHost(ownship);
			for (int i = 0; i < 7; i++)
			{
				final SensorContactWrapper scw = getContact(i * 6000, s2);
				s2.add(scw);
			}

			final TrackWrapper target = new TrackWrapper();
			target.setName("target");

			for (int i = 0; i < 25; i++)
			{
				final FixWrapper scw = getFix(i * 5000, new WorldLocation(1, i, 0),
						target);
				target.add(scw);
			}

			final Vector<SensorWrapper> sensors = new Vector<SensorWrapper>();
			sensors.add(sw);
			sensors.add(s2);
			final HiResDate start_time = target.getStartDTG();
			final HiResDate end_time = target.getEndDTG();

			final TimeSeriesCollection data = new TimeSeriesCollection();
			GenerateSensorRangePlot.addDataSeries(target, sensors, start_time,
					end_time, data, true);

			assertNotNull("got dataset", data);
			assertEquals("got correct num of series", 2, data.getSeriesCount());
			final TimeSeries first = data.getSeries(0);
			assertNotNull("got first series", first);
			assertEquals("got data", 5, first.getItemCount());
			final TimeSeries second = data.getSeries(1);
			assertNotNull("got second series", second);
			assertEquals("got data", 7, second.getItemCount());
		}

		// TODO FIX-TEST
		public void NtestMenu()
		{
			final TrackWrapper ownship = new TrackWrapper();
			ownship.setName("trackA");
			for (int i = 0; i < 5; i++)
			{
				final FixWrapper scw = getFix(i * 5000, new WorldLocation(1, i, 0),
						ownship);
				ownship.add(scw);
			}

			final SensorWrapper sw = new SensorWrapper("s1");
			sw.setHost(ownship);
			sw.setSensorOffset(new ArrayLength(200));
			for (int i = 0; i < 5; i++)
			{
				final SensorContactWrapper scw = getContact(i * 5000, sw);
				sw.add(scw);
			}

			final TrackWrapper target = new TrackWrapper();
			target.setName("trackB");

			for (int i = 0; i < 5; i++)
			{
				final FixWrapper scw = getFix(i * 5000, new WorldLocation(1, i, 0),
						target);
				target.add(scw);
			}

			final GenerateSensorRangePlot plot = new GenerateSensorRangePlot()
			{
				protected Action getAction(final Vector<SensorWrapper> candidates,
						final Vector<TrackWrapper> primary)
				{
					_candidates = candidates;
					_primary = primary;
					return new Action("some")
					{
					};
				}
			};
			final Editable[] subjects = new Editable[2];
			subjects[0] = sw;
			subjects[1] = target;
			final IMenuManager parent = new MenuManager();
			plot.generate(parent, null, null, subjects);

			assertNotNull("got menu back", parent);
			final IContributionItem firstOne = parent.getItems()[0];
			assertNotNull("got menu item back", firstOne);
			assertTrue("got separator", firstOne instanceof Separator);
			final IContributionItem secondOne = parent.getItems()[1];
			assertNotNull("got menu item back", secondOne);
			assertTrue("got menu item", secondOne instanceof ActionContributionItem);

			// check items got set
			assertNotNull(_candidates);
			assertEquals(1, _candidates.size());
		}

		private static FixWrapper getFix(final long time, final WorldLocation loc,
				final TrackWrapper track)
		{
			final Fix theFix = new Fix(new HiResDate(time), loc, 0, 0);
			final FixWrapper res = new FixWrapper(theFix);
			res.setTrackWrapper(track);
			return res;
		}

		private static SensorContactWrapper getContact(final long time,
				final SensorWrapper host)
		{
			final SensorContactWrapper res = new SensorContactWrapper();
			res.setDTG(new HiResDate(time));
			res.setSensor(host);
			return res;
		}
	}

}