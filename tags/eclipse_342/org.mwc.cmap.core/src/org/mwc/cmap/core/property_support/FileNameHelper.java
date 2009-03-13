/**
 * 
 */
package org.mwc.cmap.core.property_support;

import java.io.File;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

public class FileNameHelper extends EditorHelper
{
	public static class FileDialogCellEditor extends DialogCellEditor
	{

		public FileDialogCellEditor(Composite parent)
		{
			super(parent);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.DialogCellEditor#getDefaultLabel()
		 */
		protected Label getDefaultLabel()
		{
			return super.getDefaultLabel();
		}

		protected Object openDialogBox(Control cellEditorWindow)
		{
			FileDialog ftDialog = new FileDialog(cellEditorWindow.getShell(), SWT.SAVE);
			File theFile = (File) getValue();
			if(theFile != null)
				ftDialog.setFileName(theFile.getAbsolutePath());
			String thisFile = ftDialog.open();
			File resFile = new File(thisFile);

			return resFile;
		}

	}

	public FileNameHelper()
	{
		super(File.class);
	}

	public CellEditor getCellEditorFor(Composite parent)
	{
		CellEditor editor = new FileDialogCellEditor(parent);
		return editor;
	}

	public ILabelProvider getLabelFor(final Object currentValue)
	{
		ILabelProvider label1 = new LabelProvider()
		{
			public String getText(Object element)
			{
				File file = (File) element;
				return file.getAbsolutePath();
			}

			public Image getImage(Object element)
			{
				Image res = null;
				return res;
			}

		};
		return label1;
	}

	public Object translateToSWT(Object value)
	{
		return value;
	}

	public Object translateFromSWT(Object value)
	{
		return value;
	}

}