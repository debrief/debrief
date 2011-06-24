package org.mwc.debrief.multipath2;

import java.io.IOException;

import org.eclipse.core.runtime.Status;
import org.jfree.data.time.TimeSeries;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.multipath2.MultiPathPresenter.Display.FileHandler;
import org.mwc.debrief.multipath2.model.MultiPathModel;
import org.mwc.debrief.multipath2.model.MultiPathModel.DataFormatException;
import org.mwc.debrief.multipath2.model.RangeValues;

import flanagan.math.MinimisationFunction;

public class MultiPathPresenterTest extends MultiPathPresenter
{
	private static final int OWNSHIP_DEPTH = 30;
	private RangeValues _ranges = null;
	protected String _rangePath;

	/**
	 * initialise presenter
	 * 
	 * @param display
	 * @param model
	 */
	public MultiPathPresenterTest(Display display)
	{
		super(display);

	}

	@Override
	public void bind()
	{
		// do the parent bits
		super.bind();

		// now listen out for ranges being dropped on the slider
		_display.addRangesListener(new FileHandler()
		{
			public void newFile(String path)
			{
				loadRanges(path);
				
				checkEnablement();
			}
		});
	}

	@Override
	protected void checkEnablement()
	{
		// do the parent bit first
		super.checkEnablement();
		
		// check for the range stuff
		if (_rangePath == null)
		{
			// disable it
			_display.setEnabled(false);

			// show error
			_display.setSliderText("Waiting for ranges");
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

	protected void loadRanges(String path)
	{
		try
		{
			_rangePath = path;
			_ranges = new RangeValues();

			_ranges.load(path);
		}
		catch (NumberFormatException e)
		{
			CorePlugin.logError(Status.ERROR,
					"ranges file number formatting problem", e);
			_ranges  = null;
		}
		catch (IOException e)
		{
			CorePlugin.logError(Status.ERROR, "ranges file loading problem", e);
			_ranges  = null;
		}
		catch (DataFormatException e)
		{
			CorePlugin.logError(Status.ERROR, "ranges file data formatting problem",
					e);
			_ranges  = null;
		}
		
		if(_ranges != null)
			updateCalc(100);
	}

	protected TimeSeries getCalculatedProfile(int val)
	{
		TimeSeries calculated = null;

		if (_ranges != null)
			calculated = _model.getCalculatedProfileFor(_ranges, _svp, _times, val,
					OWNSHIP_DEPTH);

		return calculated;
	}
	
	
	protected MinimisationFunction createMiracle()
	{
		return new MultiPathModel.RangedMiracleFunction(_ranges, _svp, _times, OWNSHIP_DEPTH);
	}


}
