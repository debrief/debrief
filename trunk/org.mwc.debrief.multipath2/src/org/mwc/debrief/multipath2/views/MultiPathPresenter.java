package org.mwc.debrief.multipath2.views;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Status;
import org.jfree.data.time.TimeSeries;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.debrief.multipath2.model.MultiPathModel;
import org.mwc.debrief.multipath2.model.SVP;
import org.mwc.debrief.multipath2.model.TimeDeltas;
import org.mwc.debrief.multipath2.views.MultiPathPresenter.Display.FileHandler;
import org.mwc.debrief.multipath2.views.MultiPathPresenter.Display.ValueHandler;

import MWC.GenericData.WatchableList;

public class MultiPathPresenter
{

	/**
	 * UI component of multipath analysis
	 * 
	 * @author ianmayo
	 * 
	 */
	public static interface Display
	{
		/**
		 * interface for anybody that wants to know about files being dropped
		 * 
		 * @author ianmayo
		 * 
		 */
		public static interface FileHandler
		{
			/**
			 * the specified file has been dropped
			 * 
			 * @param path
			 */
			void newFile(String path);
		}

		/**
		 * interface for anybody that wants to know about a slider being dragged
		 * 
		 * @author ianmayo
		 * 
		 */
		public static interface ValueHandler
		{
			/**
			 * the new value on the slider
			 * 
			 * @param val
			 */
			void newValue(double val);
		}

		/**
		 * let someone know about a new SVP file being dropped
		 * 
		 * @param handler
		 */
		public void addSVPListener(FileHandler handler);

		/**
		 * let someone know about a new time-delta file being dropped
		 * 
		 * @param handler
		 */
		public void addTimeDeltaListener(FileHandler handler);

		/**
		 * let someone know about the drag-handle being dragged
		 * 
		 * @param handler
		 */
		public void addDragHandler(ValueHandler handler);

		/** see if anybody can provide tracks
		 * 
		 * @return
		 */
		public TrackDataProvider getDataProvider();

		/** there's a problem, show it to the user
		 * 
		 * @param string
		 */
		public void showError(String string);

		/** show these data series
		 * 
		 * @param _measuredSeries
		 * @param calculated
		 */
		public void display(TimeSeries _measuredSeries, TimeSeries calculated);

		/** indicate whether the slider should be enabled
		 * 
		 * @param b yes/no
		 */
		public void setEnabled(boolean b);

		public void setSVPName(String fName);

		public void setIntervalName(String fName);
	};

	private final Display _display;
	private final MultiPathModel _model;
	private TimeSeries _measuredSeries = null;
	private SVP _svp = null;
	private TimeDeltas _times = null;

	/**
	 * initialise presenter
	 * 
	 * @param display
	 * @param model
	 */
	public MultiPathPresenter(Display display)
	{
		_display = display;
		_model = new MultiPathModel();

		// setup assorted listeners
		_display.addDragHandler(new ValueHandler()
		{

			@Override
			public void newValue(double val)
			{
				updateCalc(val);
			}
		});

		_display.addSVPListener(new FileHandler()
		{

			public void newFile(String path)
			{

				try
				{
					_svp = new SVP();

					_svp.load(path);
					
					// get the filename
					File file = new File(path);
					String  fName = file.getName();
					_display.setSVPName(fName);


				}
				catch (NumberFormatException e)
				{
					CorePlugin.logError(Status.ERROR, "time-delta formatting problem", e);
					_svp = null;
				}
				catch (IOException e)
				{
					CorePlugin.logError(Status.ERROR, "time-delta file-read problem", e);
					_svp = null;
				}

				// check if UI should be enabled
				checkEnablement();

			}
		});

		_display.addTimeDeltaListener(new FileHandler()
		{

			public void newFile(String path)
			{
				try
				{
					_times = new TimeDeltas();

					_times.load(path);

					// reset the measured series
					_measuredSeries = null;
					
					// get the filename
					File file = new File(path);
					String  fName = file.getName();
					_display.setIntervalName(fName);
					
				}
				catch (NumberFormatException e)
				{
					CorePlugin.logError(Status.ERROR, "time-delta formatting problem", e);
					_times = null;
				}
				catch (IOException e)
				{
					CorePlugin.logError(Status.ERROR, "time-delta file-read problem", e);
					_times = null;
				}

				// check if UI should be enabled
				checkEnablement();

			}
		});
		
		// lastly, check if we're enabled.
		checkEnablement();
	}

	/**
	 * see if we have sufficient data to enable the display
	 * 
	 */
	protected void checkEnablement()
	{

		if ((_svp == null) || (_times == null))
		{
			// disable it
			_display.setEnabled(false);
		}
		else
		{
			// enable it
			_display.setEnabled(true);
		}
	}

	protected void disableUI()
	{
		// TODO Auto-generated method stub

	}

	protected void updateCalc(double val)
	{		
		// do we have our measured series?
		if (_measuredSeries == null)
			_measuredSeries = _model.getMeasuredProfileFor(_times);

		// do we have a tote?
		TrackDataProvider tv = _display.getDataProvider();

		// do we only have one seconary?
		if (tv.getSecondaryTracks().length > 1)
		{
			_display.showError("Too many secondary tracks");
		}
		else if(tv.getPrimaryTrack() == null)
		{
			_display.showError("Needs a primary track (or location)");
		}
		else
		{
			WatchableList primary = tv.getPrimaryTrack();
			WatchableList secondary = tv.getSecondaryTracks()[0];
			TimeSeries calculated = _model.getCalculatedProfileFor(primary,
					secondary, _svp, _times, val);
	
			// ok, ready to plot
			_display.display(_measuredSeries, calculated);
		}

	}
}
