/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.plotViewer.actions;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.plotViewer.PlotViewerPlugin;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;

import MWC.GUI.PlainChart;
import MWC.GUI.Tools.Action;

/**
 * @author ian.mayo
 */
abstract public class CoreDragAction extends CoreEditorAction
{
	/**
	 * retrieve an instance of our dragger
	 * 
	 * @return
	 */
	abstract public SWTChart.PlotMouseDragger getDragMode();

	protected void execute()
	{
		// find out what the current dragger is
		final PlainChart chrs = getChart();
		final SWTChart myChart = (SWTChart) chrs;

		final SWTChart.PlotMouseDragger oldMode = myChart.getDragMode();

		// get rid of the old model
		oldMode.close();

		// create an instance of the new mode
		final SWTChart.PlotMouseDragger newMode = getDragMode();

		// create the action
		final CoreDragAction.SwitchModeAction theAction = new CoreDragAction.SwitchModeAction(
				newMode, myChart);

		// initialise the cursor
		final Cursor normalCursor = newMode.getNormalCursor();
		myChart.getCanvasControl().setCursor(normalCursor);

		// and wrap it
		final DebriefActionWrapper daw = new DebriefActionWrapper(theAction);

		// and run it
		CorePlugin.run(daw);
	}

	/**
	 * embed switching drag mode into an action, so we can reverse it
	 * 
	 * @author ian.mayo
	 * 
	 */
	public static class SwitchModeAction implements Action
	{
		/**
		 * the editor we're controlling
		 * 
		 */
		private final SWTChart _editor;

		/**
		 * the mode we're switching to
		 * 
		 */
		private final SWTChart.PlotMouseDragger _newMode;

		public SwitchModeAction(final SWTChart.PlotMouseDragger newMode,
				final SWTChart editor)
		{
			_editor = editor;
			_newMode = newMode;
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
			// don't bother = we're not undo-able
		}

		public void execute()
		{
			_editor.setDragMode(_newMode);
			
			IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
			for (IEditorReference editorReference:editorReferences) {
				IEditorPart editorPart = editorReference.getEditor(false);
				if (editorPart instanceof IChartBasedEditor) {
					IChartBasedEditor editor = (IChartBasedEditor) editorPart;
					SWTChart chart = editor.getChart();
					if (chart != null) {
						chart.setDragMode(_newMode);
					}
				}
			}

			// ok - store the mode in the core editor
			PlotViewerPlugin.setCurrentMode(_newMode);
		}

	}
}