package org.mwc.debrief.multipath.views;

import org.mwc.debrief.multipath.views.MultiPathPresenter.Display.FileHandler;
import org.mwc.debrief.multipath.views.MultiPathPresenter.Display.ValueHandler;

import MWC.TacticalData.Track;


public class MultiPathPresenter
{
	
	/** UI component of multipath analysis
	 * 
	 * @author ianmayo
	 *
	 */
	public static interface Display
	{
		/** interface for anybody that wants to know about files being dropped
		 * 
		 * @author ianmayo
		 *
		 */
		public static interface FileHandler
		{
			/** the specified file has been dropped
			 * 
			 * @param path
			 */
			void newFile(String path);
		}
		
		/** interface for anybody that wants to know about a slider being dragged
		 * 
		 * @author ianmayo
		 *
		 */
		public static interface ValueHandler
		{
			/** the new value on the slider
			 * 
			 * @param val
			 */
			void newValue(double val);
		}
		
		/** let someone know about a new SVP file being dropped
		 * 
		 * @param handler
		 */
		public void addSVPListener(FileHandler handler);
		
		/** let someone know about a new time-delta file being dropped
		 * 
		 * @param handler
		 */
		public void addTimeDeltaListener(FileHandler handler);
		
		/** let someone know about the drag-handle being dragged
		 * 
		 * @param handler
		 */
		public void addDragHandler(ValueHandler handler);
	};
	
	public static interface Model
	{
		/** load the specified SVP data
		 * 
		 * @param array
		 */
		public void setSVP(double[][] array);
		
		/** load the specified interval data
		 * 
		 */
		public void setTimeDelta(double[][] array);
		
		/** load the Sensor track
		 * 
		 */
		public void setSensorTrack(Track track);
		
		/** load the target track
		 * 
		 */
		public void setTargetTrack(Track track);
		
		/** specify the current depth estimate
		 * 
		 */
		public void setDepth(double depth);
		
	}


	private final Display _display;
	private final Model _model;
	

	/** initialise presenter
	 * 
	 * @param display
	 * @param model
	 */
	public MultiPathPresenter(Display display, Model model)
	{
		_display = display;
		_model = model;

		// setup assorted listeners
		_display.addDragHandler(new ValueHandler(){

			@Override
			public void newValue(double val)
			{
			}});
		
		_display.addSVPListener(new FileHandler(){

			public void newFile(String path)
			{
			}});
		
		_display.addTimeDeltaListener(new FileHandler(){

			public void newFile(String path)
			{
				// TODO Auto-generated method stub
				
			}});
		
		// show initial display
		initDisplay();
	}


	private void initDisplay()
	{
	}
	
}
