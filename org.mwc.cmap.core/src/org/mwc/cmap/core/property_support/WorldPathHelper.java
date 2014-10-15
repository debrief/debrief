/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core.property_support;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.mwc.cmap.core.CorePlugin;
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
	public CellEditor getCellEditorFor(final Composite parent)
	{
		return new EditPathDialogCellEditor(parent);
	}

	public ILabelProvider getLabelFor(final Object currentValue)
	{
		final ILabelProvider label1 = new LabelProvider()
		{
			public String getText(final Object element)
			{
				final WorldPath wp = (WorldPath) element;
				return wp.getPoints().size() + " Points";
			}

			public Image getImage(final Object element)
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

		PolygonEditorView _myEditor = null;

		/**
		 * constructor - just pass on to parent
		 * 
		 * @param cellParent
		 */
		public EditPathDialogCellEditor(final Composite cellParent)
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
		protected Object openDialogBox(final Control cellEditorWindow)
		{

			// ditch our current editor, if we have one
			if (_myEditor != null)
			{
				_myEditor.stopPainting();
				_myEditor = null;
			}

			final IWorkbench wb = PlatformUI.getWorkbench();
			final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			final IWorkbenchPage page = win.getActivePage();

			final Object output = null;

			final String plotId = "org.mwc.cmap.core.editor_views.PolygonEditorView";
			try
			{
				final IViewPart polyEditor = page.showView(plotId);
				if (polyEditor != null)
				{
					_myEditor = (PolygonEditorView) polyEditor;
					_myEditor.setPolygon((WorldPath) doGetValue());
				}

			}
			catch (final PartInitException e)
			{
				CorePlugin.logError(Status.ERROR,
						"Whilst creating WorldPathHelper", e);
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
		protected Button createButton(final Composite parent)
		{
			final Button result = super.createButton(parent);
			result.setText("Edit");
			return result;
		}

		protected Object doGetValue()
		{
			final WorldPath res = (WorldPath) super.doGetValue();
			return res;
		}

		protected void doSetValue(final Object value)
		{
			final WorldPath myData = (WorldPath) value;
			final WorldPath toStore = myData;// new WorldPath(myData);

			super.doSetValue(toStore);
		}

		public void deactivate()
		{
			// try to get our editor to ditch, if we can.
			// ditch our current editor, if we have one
//			if (_myEditor != null)
//			{
//				_myEditor.stopPainting();
//				_myEditor = null;
//			}
			
	//		super.deactivate();
		}
	}
}