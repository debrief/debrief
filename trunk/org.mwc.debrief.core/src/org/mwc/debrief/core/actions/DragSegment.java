/**
 * 
 */
package org.mwc.debrief.core.actions;


import java.util.Vector;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Shapes.DraggableItem.LocationConstruct;

/**
 * @author ian.mayo
 */
public class DragSegment extends DragFeature
{
	
	public static class DragMode extends Action
	{
		public DragMode(String title)
		{
			super(title, IAction.AS_RADIO_BUTTON);
		}
	}
	
	private static Vector<Action> _dragModes;
	
	public static Vector<Action> getDragModes(){
		if(_dragModes == null)
		{
			_dragModes = new Vector<Action>();
			org.mwc.debrief.core.actions.DragSegment.DragMode keepCourse = new DragMode("crse"){
				public void run()
				{
					super.run();
					_currentDragMode = this;
				}};
			org.mwc.debrief.core.actions.DragSegment.DragMode keepSpeed = new DragMode("spd"){
			public void run()
			{
				super.run();
				_currentDragMode = this;
			}};
			org.mwc.debrief.core.actions.DragSegment.DragMode keepRange = new DragMode("rng"){
			public void run()
			{
				super.run();
				_currentDragMode = this;
			}};
			org.mwc.debrief.core.actions.DragSegment.DragMode translate = new DragMode("[]"){
			public void run()
			{
				super.run();
				_currentDragMode = this;
			}};
			
			_dragModes.add(keepCourse);
			_dragModes.add(keepSpeed);
			_dragModes.add(keepRange);
			_dragModes.add(translate);
			
			// and initiate the drag
		//	translate.setChecked(true);
			translate.run();
			
		}
		return _dragModes;
	}

	public DragSegment()
	{

	}
	
	protected static DragMode _currentDragMode;
	
	protected void execute()
	{
		// ok, fire our parent
		super.execute();

		// now, try to open the stacked dots view
		try
		{
			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			IWorkbenchPage page = win.getActivePage();
			page.showView(CorePlugin.STACKED_DOTS);

			System.err.println("run mode is:" + _currentDragMode.getText());
			
		}
		catch (PartInitException e)
		{
			CorePlugin.logError(Status.ERROR, "Failed to open stacked dots", e);
		}

		
	}
	
	/** simpler test that just supports tracks
	 * 
	 */
	public void findNearest(Layer thisLayer,
			MWC.GenericData.WorldLocation cursorLoc, java.awt.Point cursorPos,
			LocationConstruct currentNearest, Layer parentLayer)
	{
		// we only act on track wrappers, check if this is one
		if (thisLayer instanceof TrackWrapper)
		{
			TrackWrapper thisTrack = (TrackWrapper) thisLayer;
			// find it's nearest segment
			thisTrack.findNearestSegmentHotspotFor(cursorLoc, cursorPos,
					currentNearest);
		}
	}

	@Override
	public Cursor getDragCursor()
	{
		return new Cursor(Display.getDefault(), DebriefPlugin
				.getImageDescriptor("icons/SelectFeatureHitDown.ico").getImageData(), 4,
				2);
	}
}
