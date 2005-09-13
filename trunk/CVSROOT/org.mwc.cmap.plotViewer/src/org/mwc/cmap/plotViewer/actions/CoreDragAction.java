/**
 * 
 */
package org.mwc.cmap.plotViewer.actions;

import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotDragMode;

import MWC.GUI.PlainChart;
import MWC.GUI.Tools.Action;

/**
 * @author ian.mayo
 */
abstract public class CoreDragAction extends CoreEditorAction
{
	/** retrieve an instance of our dragger
	 * 
	 * @return
	 */
	abstract public SWTChart.PlotDragMode getDragMode();
	
	
	final protected void run()
	{
		// find out what the current dragger is
		PlainChart chrs = getChart();
		SWTChart myChart = (SWTChart) chrs;
		SWTChart.PlotDragMode oldMode = myChart.getDragMode();
		
		// create an instance of the new mode
		SWTChart.PlotDragMode newMode = getDragMode();
		
		// create the action
		CoreDragAction.SwitchModeAction theAction = new CoreDragAction.SwitchModeAction(newMode, oldMode, myChart);
		
		// and wrap it
		DebriefActionWrapper daw = new DebriefActionWrapper(theAction);

		// and run it
		CorePlugin.run(daw);
	}
	
	
	/** embed switching drag mode into an action, so we can reverse it
	 * 
	 * @author ian.mayo
	 *
	 */
	public static class SwitchModeAction implements Action
	{
		/** the editor we're controlling
		 * 
		 */
		private SWTChart _editor;
		
		/** the mode we're switching to
		 * 
		 */
		private SWTChart.PlotDragMode _newMode;
		
		/** the mode we're switching from
		 * 
		 */
		private SWTChart.PlotDragMode _oldMode;
	
		public SwitchModeAction(final SWTChart.PlotDragMode newMode,
														final SWTChart.PlotDragMode oldMode,
														final SWTChart editor)
		{
			_editor = editor;
			_newMode = newMode;
			_oldMode = oldMode;
		}
		
		public boolean isUndoable()
		{
			return false;
		}
	
		public boolean isRedoable()
		{
			return false;
		}
	
		public void undo()
		{
			_editor.setDragMode(_oldMode);
		}
	
		public void execute()
		{
			_editor.setDragMode(_newMode);
		}
		
	}	
}