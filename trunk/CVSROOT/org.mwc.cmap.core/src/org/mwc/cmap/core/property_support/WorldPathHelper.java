/**
 * 
 */
package org.mwc.cmap.core.property_support;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.mwc.cmap.core.editor_views.PolygonEditorView;

import MWC.GenericData.WorldPath;

public class WorldPathHelper extends EditorHelper
{

	/**
	 * constructor..
	 */
	public WorldPathHelper()
	{
		super(WorldPath.class);
	}

	/**
	 * create an instance of the cell editor suited to our data-type
	 * 
	 * @param parent
	 * @return
	 */
	public CellEditor getCellEditorFor(Composite parent)
	{
		return new EditPathDialogCellEditor(parent);
	}

	public ILabelProvider getLabelFor(Object currentValue)
	{
		ILabelProvider label1 = new LabelProvider()
		{
			public String getText(Object element)
			{
				WorldPath wp = (WorldPath) element;
				return wp.toString();
			}

			public Image getImage(Object element)
			{
				return null;
			}

		};
		return label1;
	}

	/**
	 * custom cell editor which re-purposes button used to open dialog as a paste
	 * button
	 * 
	 * @author ian.mayo
	 */
	private static class EditPathDialogCellEditor extends DialogCellEditor
	{
		/**
		 * constructor - just pass on to parent
		 * 
		 * @param cellParent
		 */
		public EditPathDialogCellEditor(Composite cellParent)
		{
			super(cellParent);
		}

		/**
		 * override operation triggered when button pressed. We should strictly be
		 * opening a new dialog, instead we're looking for a valid location on the
		 * clipboard. If one is there, we paste it.
		 * 
		 * @param cellEditorWindow
		 *          the parent control we belong to
		 * @return
		 */
		protected Object openDialogBox(Control cellEditorWindow)
		{
			
			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			IWorkbenchPage page = win.getActivePage();
			
			Object output = null;
			
			String plotId = "org.mwc.cmap.core.editor_views.PolygonEditorView";
			try
			{
				IViewPart polyEditor = page.showView(plotId);		
				if (polyEditor != null)
				{
					PolygonEditorView pev = (PolygonEditorView) polyEditor;
					pev.setPolygon((WorldPath) doGetValue());
				}				
				
			}
			catch (PartInitException e)
			{
				e.printStackTrace();
			}			

			return output;
		}

		/**
		 * Creates the button for this cell editor under the given parent control.
		 * <p>
		 * The default implementation of this framework method creates the button
		 * display on the right hand side of the dialog cell editor. Subclasses may
		 * extend or reimplement.
		 * </p>
		 * 
		 * @param parent
		 *          the parent control
		 * @return the new button control
		 */
		protected Button createButton(Composite parent)
		{
			Button result = super.createButton(parent);
			result.setText("Edit");
			return result;
		}
	}
}