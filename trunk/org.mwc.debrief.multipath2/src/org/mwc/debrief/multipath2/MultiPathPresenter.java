package org.mwc.debrief.multipath2;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IMemento;
import org.jfree.data.time.TimeSeries;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.debrief.multipath2.MultiPathPresenter.Display.FileHandler;
import org.mwc.debrief.multipath2.MultiPathPresenter.Display.ValueHandler;
import org.mwc.debrief.multipath2.model.MultiPathModel;
import org.mwc.debrief.multipath2.model.MultiPathModel.CalculationException;
import org.mwc.debrief.multipath2.model.MultiPathModel.DataFormatException;
import org.mwc.debrief.multipath2.model.SVP;
import org.mwc.debrief.multipath2.model.TimeDeltas;

import MWC.GenericData.WatchableList;

public class MultiPathPresenter
{

	private static final String INTERVAL_FILE = "INTERVAL_FILE";
	private static final String SVP_FILE = "SVP_FILE";
	public static final int DEFAULT_DEPTH = 50;
	private static final String DEPTH_VAL = "DEPTH_VAL";

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
			void newValue(int val);
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

		/**
		 * see if anybody can provide tracks
		 * 
		 * @return
		 */
		public TrackDataProvider getDataProvider();

		/**
		 * there's a problem, show it to the user
		 * 
		 * @param string
		 */
		public void showError(String string);

		/**
		 * show these data series
		 * 
		 * @param _measuredSeries
		 * @param calculated
		 */
		public void display(TimeSeries _measuredSeries, TimeSeries calculated);

		/**
		 * indicate whether the slider should be enabled
		 * 
		 * @param b
		 *          yes/no
		 */
		public void setEnabled(boolean b);

		/**
		 * show the SVP filename
		 * 
		 * @param fName
		 */
		public void setSVPName(String fName);

		/**
		 * show the interval filename
		 * 
		 * @param fName
		 */
		public void setIntervalName(String fName);

		/**
		 * feedback on the current slider value
		 * 
		 * @param val
		 */
		public void setSliderText(String text);

		/**
		 * set the value on the slider
		 * 
		 * @param _curDepth
		 */
		public void setSliderVal(int _curDepth);
	};

	private final Display _display;
	private final MultiPathModel _model;
	private TimeSeries _measuredSeries = null;
	private SVP _svp = null;
	private TimeDeltas _times = null;
	protected String _intervalPath;
	protected int _curDepth = DEFAULT_DEPTH;
	protected String _svpPath;
	private ValueHandler _dragHandler;

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

	}

	private void loadSVP(String path)
	{
		try
		{

			// clear the path = we'll overwrite it if we're successful
			_svpPath = null;
			_display.setSVPName("[pending]");

			_svp = new SVP();

			_svp.load(path);

			_svpPath = path;

			// display the filename
			File file = new File(path);
			String fName = file.getName();
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
		catch (DataFormatException e)
		{
			_display.showError("Does not look like SVP data");
		}

		// check if UI should be enabled
		checkEnablement();

	}

	/**
	 * see if we have sufficient data to enable the display
	 * 
	 */
	protected void checkEnablement()
	{

		if (_svpPath == null)
		{
			// disable it
			_display.setEnabled(false);

			// show error
			_display.setSliderText("Waiting for SVP");
		}
		else if (_intervalPath == null)
		{
			// disable it
			_display.setEnabled(false);

			// show error
			_display.setSliderText("Waiting for interval data");
		}
		else
		{
			// just check the init is complete
			if ((_svp != null) && (_times != null))
			{

				// enable it
				_display.setEnabled(true);

				// initialise the slider
				_display.setSliderVal(_curDepth);

				// and give it a sensible start value
				_dragHandler.newValue(_curDepth);
			}
		}
	}

	protected void disableUI()
	{
		// TODO Auto-generated method stub

	}

	protected void updateCalc(int val)
	{

		// do we have a tote?
		TrackDataProvider tv = _display.getDataProvider();

		// do we only have one seconary?
		if (tv == null)
		{
			_display
					.showError("Must have a primary and a secondary track on the tote");
		}
		else if (tv.getPrimaryTrack() == null)
		{
			_display.showError("Needs a primary track (or location)");
		}
		else if (tv.getSecondaryTracks() == null)
		{
			_display.showError("Secondary track missing");
		}
		else if (tv.getSecondaryTracks().length == 0)
		{
			_display.showError("Secondary track missing");
		}
		else if (tv.getSecondaryTracks().length > 1)
		{
			_display.showError("Too many secondary tracks");
		}
		else if (_times == null)
		{
			_display.showError("Waiting for interval data");
		}
		else
		{

			// do we have our measured series?
			if (_measuredSeries == null)
			{
				_measuredSeries = _model.getMeasuredProfileFor(_times);
			}

			// remember the depth
			_curDepth = (int) val;

			// cool, valid data
			_display.setSliderText("Depth:" + val + "m");

			WatchableList primary = tv.getPrimaryTrack();
			WatchableList secondary = tv.getSecondaryTracks()[0];
			try
			{
				TimeSeries calculated = _model.getCalculatedProfileFor(primary,
						secondary, _svp, _times, val);

				// ok, ready to plot
				_display.display(_measuredSeries, calculated);
			}
			catch (CalculationException e)
			{
				_display.showError(e.getMessage());
				CorePlugin.logError(Status.ERROR, "Performing multipath analysis", e);
			}
		}

	}

	public void saveState(IMemento memento)
	{
		// store the filenames
		if (_svpPath != null)
			memento.putString(SVP_FILE, _svpPath);

		if (_intervalPath != null)
			memento.putString(INTERVAL_FILE, _intervalPath);

		memento.putInteger(DEPTH_VAL, _curDepth);
	}

	/**
	 * initialise ourselves from the memento
	 * 
	 */
	public void init(IMemento memento)
	{
		if (memento != null)
		{
			_svpPath = memento.getString(SVP_FILE);
			_intervalPath = memento.getString(INTERVAL_FILE);
			Integer depth = memento.getInteger(DEPTH_VAL);
			if (depth != null)
				_curDepth = depth;
		}
	}

	/**
	 * load the intervals from the supplied file
	 * 
	 * @param path
	 */
	private void loadIntervals(String path)
	{
		try
		{
			// clear the path = we'll overwrite it if we're successful
			_intervalPath = null;
			_display.setIntervalName("[pending]");

			_times = new TimeDeltas();

			_times.load(path);

			_intervalPath = path;

			// reset the measured series
			_measuredSeries = null;

			// get the filename
			File file = new File(path);
			String fName = file.getName();
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
		catch (DataFormatException e)
		{
			_display.showError("Does not look like interval data");
		}

		// check if UI should be enabled
		checkEnablement();
	}

	/**
	 * connect the presenter to the UI component, now that it's initialised
	 * 
	 */
	public void bind()
	{
		// setup assorted listeners
		_dragHandler = new ValueHandler()
		{
			@Override
			public void newValue(int val)
			{
				updateCalc(val);
			}
		};
		_display.addDragHandler(_dragHandler);

		_display.addSVPListener(new FileHandler()
		{
			public void newFile(String path)
			{
				loadSVP(path);
			}
		});

		_display.addTimeDeltaListener(new FileHandler()
		{
			public void newFile(String path)
			{
				loadIntervals(path);
			}
		});

		// aah, do we have any pending files?
		if (_svpPath != null)
			loadSVP(_svpPath);

		if (_intervalPath != null)
			loadIntervals(_intervalPath);

		// lastly, check if we're enabled.
		checkEnablement();
	}
}
