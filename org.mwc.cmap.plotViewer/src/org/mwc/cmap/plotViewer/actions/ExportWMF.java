/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.plotViewer.actions;

import java.awt.Dimension;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;

import com.pietjonas.wmfwriter2d.ClipboardCopy;

import MWC.GUI.*;
import MWC.GUI.Canvas.MetafileCanvas;
import MWC.GUI.Tools.Chart.WriteMetafile;

/**
 * @author ian.mayo
 */
public class ExportWMF extends CoreEditorAction
{
	public static ToolParent _theParent = null;

	/**
	 * whether to put the image on the clipboard
	 */
	private boolean _writeToClipboard = false;

	/**
	 * whether to put the image into the working export directory
	 */
	private boolean _writeToFile = false;

	/**
	 * @param toClipboard
	 * @param toFile
	 */
	public ExportWMF()
	{
		this(false, true);
	}
	
	/**
	 * @param toClipboard
	 * @param toFile
	 */
	public ExportWMF(final boolean toClipboard, final boolean toFile)
	{
		super();
		_writeToClipboard = toClipboard;
		_writeToFile = toFile;
	}

	/**
	 * ok, store who the parent is for the operation
	 * 
	 * @param theParent
	 */
	public static void init(final ToolParent theParent)
	{
		_theParent = theParent;
	}

	/**
	 * and execute..
	 */
	protected void execute()
	{
		final PlainChart theChart = getChart();

		if (_theParent == null)
		{
			CorePlugin.logError(Status.ERROR, "Tool parent missing for Write Metafile", null);
			return;
		}

		final WriteMetafile write = new WriteMetafile(_theParent, theChart, _writeToFile)
		{

			/**
			 * @param mf
			 */
			protected void paintToMetafile(final MetafileCanvas mf)
			{
				final SWTCanvas sc = (SWTCanvas) theChart.getCanvas();
				sc.paintPlot(mf);
			}

		};
		write.execute();

		// ok, do we want to write it to the clipboard?
		if (_writeToClipboard)
		{
			// try to get the filename
			final String fName = MetafileCanvas.getLastFileName();

			if (fName != null)
			{
				// create the clipboard

				// try to copy the wmf to the clipboard
				try
				{
					// get the dimensions
					final Dimension dim = MetafileCanvas.getLastScreenSize();

					final ClipboardCopy cc = new ClipboardCopy();
					cc.copyWithPixelSize(fName, dim.width, dim.height, false);

				}
				catch(final UnsatisfiedLinkError le)
				{
					CorePlugin.logError(Status.ERROR,"Failed to find clipboard dll", le);
					CorePlugin.showMessage("Export to WMF", "Sorry, unable to produce WMF. This is due to a dll missing, or an non-Windows PC");
				}
				catch (final Exception e)
				{
					e.printStackTrace();
				}
			}
			else
				System.err.println("Target filename missing");
		}
	}

}