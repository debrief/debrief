/**
 * 
 */
package org.mwc.debrief.track_shift.views;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;

import Debrief.Tools.Tote.Watchable;
import Debrief.Tools.Tote.WatchableList;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Plottable;
import MWC.GUI.JFreeChart.ColouredDataItem;
import MWC.GenericData.HiResDate;

public final class StackedDotHelper
{
	/**
	 * the track being dragged
	 */
	private TrackWrapper _primaryTrack;

	/**
	 * the secondary track we're monitoring
	 */
	private TrackWrapper _secondaryTrack;

	/**
	 * the set of points to watch on the primary track
	 */
	private Vector<Doublet> _primaryDoublets;

	// ////////////////////////////////////////////////
	// CONSTRUCTOR
	// ////////////////////////////////////////////////

	/**
	 * the set of points to watch on the secondary track
	 */
	private Vector<Doublet> _secondaryDoublets;

	// ////////////////////////////////////////////////
	// MEMBER METHODS
	// ////////////////////////////////////////////////

	/**
	 * constructor - takes a set of layers, within which it identifies the track
	 * wrappers
	 * 
	 * @param theData
	 *          the set of data to provide stacked dots for
	 * @param myTrack
	 *          the track being dragged
	 */
	StackedDotHelper()
	{
	}

	private Vector<Doublet> getDoublets(final TrackWrapper sensorHost,
			final TrackWrapper targetTrack, boolean onlyVis)
	{
		final Vector<Doublet> res = new Vector<Doublet>(0, 1);

		// ok, cycle through the sensor points on the host track
		final Enumeration<Editable> iter = sensorHost.elements();
		while (iter.hasMoreElements())
		{
			final Plottable pw = (Plottable) iter.nextElement();

			if (pw.getVisible())
			{
				if (pw instanceof SensorWrapper)
				{
					final SensorWrapper sw = (SensorWrapper) pw;

					// right, work through the contacts in this sensor
					final Enumeration<Editable> theContacts = sw.elements();
					while (theContacts.hasMoreElements())
					{
						final SensorContactWrapper scw = (SensorContactWrapper) theContacts
								.nextElement();

						final boolean thisVis = scw.getVisible();
						if (!onlyVis || (onlyVis && thisVis))
						{

							final Watchable[] matches = targetTrack.getNearestTo(scw
									.getDTG());
							FixWrapper targetFix = null;
							final int len = matches.length;
							if (len > 0)
							{
								for (int i = 0; i < len; i++)
								{
									final Watchable thisOne = matches[i];
									if (thisOne instanceof FixWrapper)
									{
										targetFix = (FixWrapper) thisOne;
										continue;
									}
								}
							}
							if (targetFix != null)
							{
								// ok. found match. store it
								final Doublet thisDub = new Doublet(scw, targetFix
										.getLocation());
								res.add(thisDub);
							}
						} // if this sensor contact is visible
					} // looping through these sensor contacts
				} // is this is a sensor wrapper
			} // if this item is visible
		} // looping through the items on this track

		return res;
	}

	/**
	 * ok, our track has been dragged, calculate the new series of offsets
	 * @param linePlot 
	 * @param dotPlot 
	 * @param onlyVis 
	 * @param holder 
	 * @param logger 
	 * 
	 * @param currentOffset
	 *          how far the current track has been dragged
	 */
	public void updateFrequencyData(XYPlot dotPlot, XYPlot linePlot, TrackManager tracks, boolean onlyVis, Composite holder, ErrorLogger logger)
	{
		// ok, find the track wrappers
		if (_secondaryTrack == null)
			initialise(tracks, false, onlyVis, holder, logger);

		// did it work?
		if (_secondaryTrack == null)
			return ;

		if (_primaryDoublets == null)
			return ;

		// ok - the tracks have moved. better update the doublets
		updateDoublets(onlyVis);

		// create the collection of series
		final TimeSeriesCollection errorSeries = new TimeSeriesCollection();
		final TimeSeriesCollection actualSeries = new TimeSeriesCollection();

		// produce a dataset for each track
		final TimeSeries errorValues = new TimeSeries(_primaryTrack
				.getName());
		
		final TimeSeries measuredValues = new TimeSeries("Measured");
		final TimeSeries calculatedValues = new TimeSeries("Calculated");

		// ok, run through the points on the primary track
		Iterator<Doublet> iter = _primaryDoublets.iterator();
		while (iter.hasNext())
		{
			final Doublet thisD = iter.next();

			final Color thisColor = thisD.getColor();
			final double measuredBearing = thisD.getMeasuredBearing();
			final double calculatedBearing = thisD.getCalculatedBearing(null,null);
			final double thisError = thisD.calculateError(measuredBearing, calculatedBearing);
			final HiResDate currentTime = thisD.getDTG();

			// create a new, correctly coloured data item
			// HI-RES NOT DONE - should provide FixedMicrosecond structure
			final ColouredDataItem newError = new ColouredDataItem(
					new FixedMillisecond(currentTime.getDate().getTime()), thisError,
					thisColor, false, null);

			final ColouredDataItem mBearing = new ColouredDataItem(
					new FixedMillisecond(currentTime.getDate().getTime()), measuredBearing,
					thisColor, false, null);

			final ColouredDataItem cBearing = new ColouredDataItem(
					new FixedMillisecond(currentTime.getDate().getTime()), calculatedBearing,
					thisColor, true, null);

			try
			{
				// and add them to the series			
				errorValues.add(newError);
				measuredValues.add(mBearing);
				calculatedValues.add(cBearing);
			}
			catch (final SeriesException e)
			{
				// hack: we shouldn't be allowing this exception. Look at why we're
				// getting the same
				// time period being entered twice for this track.

				// Stop catching the error, load Dave W's holistic approach plot file,
				// and check the track/fix which is causing the problem.

				// e.printStackTrace(); //To change body of catch statement use File |
				// Settings | File Templates.
			}

		}

		// ok, add these new series
		errorSeries.addSeries(errorValues);
		
		actualSeries.addSeries(measuredValues);
		actualSeries.addSeries(calculatedValues);

		dotPlot.setDataset(errorSeries);
		linePlot.setDataset(actualSeries);
	}

	/**
	 * initialise the data, check we've got sensor data & the correct number of
	 * visible tracks
	 * 
	 * @param showError
	 * @param onlyVis 
	 * @param holder
	 */
	void initialise(TrackManager tracks, boolean showError, boolean onlyVis, Composite holder, ErrorLogger logger)
	{

		// have we been created?
		if (holder == null)
			return;

		// are we visible?
		if (holder.isDisposed())
			return;

		_secondaryTrack = null;
		_primaryTrack = null;

		// do we have some data?
		if (tracks == null)
		{
			// output error message
			logger.logError(IStatus.INFO, "Please open a Debrief plot", null);
			// showMessage("Sorry, a Debrief plot must be selected", showError);
			return;
		}

		// check we have a primary track
		final WatchableList priTrk = tracks.getPrimaryTrack();
		if (priTrk == null)
		{
			logger.logError(IStatus.INFO, "A primary track must be placed on the Tote",
					null);
			return;
		}
		else
		{
			if (!(priTrk instanceof TrackWrapper))
			{
				logger.logError(IStatus.INFO, "The primary track must be a vehicle track",
						null);
				return;
			}
			else
				_primaryTrack = (TrackWrapper) priTrk;
		}

		// now the sec track
		final WatchableList[] secs = tracks.getSecondaryTracks();

		// any?
		if ((secs == null) || (secs.length == 0))
		{
			logger.logError(IStatus.INFO,
					"A secondary track must be present on the tote", null);
			return;
		}

		// too many?
		if (secs.length > 1)
		{
			logger.logError(IStatus.INFO, "Only 1 secondary track may be on the tote",
					null);
			return;
		}

		// correct sort?
		final WatchableList secTrk = secs[0];
		if (!(secTrk instanceof TrackWrapper))
		{
			logger.logError(IStatus.INFO, "The secondary track must be a vehicle track",
					null);
			return;
		}
		else
		{
			_secondaryTrack = (TrackWrapper) secTrk;
		}

		if (_primaryTrack.getSensors() == null)
		{
			logger.logError(IStatus.INFO, "There must be sensor data available", null);
			return;
		}
		
		// must have worked, hooray
		logger.logError(IStatus.INFO,"Bearing error", null);

		// ok, get the positions
		updateDoublets(onlyVis);

	}

	/**
	 * clear our data, all is finished
	 */
	public void reset()
	{
		if (_primaryDoublets != null)
			_primaryDoublets.removeAllElements();
		_primaryDoublets = null;
		if (_secondaryDoublets != null)
			_secondaryDoublets.removeAllElements();
		_secondaryDoublets = null;
		_primaryTrack = null;
		_secondaryTrack = null;
	}

	/**
	 * go through the tracks, finding the relevant position on the other track.
	 * 
	 */
	private void updateDoublets(boolean onlyVis)
	{
		// ok - we're now there
		// so, do we have primary and secondary tracks?
		if (_primaryTrack != null && _secondaryTrack != null)
		{
			// cool sort out the list of sensor locations for these tracks
			_primaryDoublets = getDoublets(_primaryTrack, _secondaryTrack, onlyVis);
			_secondaryDoublets = getDoublets(_secondaryTrack, _primaryTrack, onlyVis);
		}
	}

}