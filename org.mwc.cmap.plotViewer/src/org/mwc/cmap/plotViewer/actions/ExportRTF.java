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
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.plotViewer.PlotViewerPlugin;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;

import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GUI.Canvas.MetafileCanvas;
import MWC.GUI.Tools.Chart.WriteMetafile;

/**
 * @author ian.mayo
 */
public class ExportRTF extends CoreEditorAction
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
	public ExportRTF()
	{
		this(false, true);
	}
	
	/**
	 * @param toClipboard
	 * @param toFile
	 */
	public ExportRTF(final boolean toClipboard, final boolean toFile)
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
				ByteArrayOutputStream os = null;
				DataInputStream dis = null;
				try
				{
					// get the dimensions
					final Dimension dim = MetafileCanvas.getLastScreenSize();

					os = new ByteArrayOutputStream();
					RTFWriter writer = new RTFWriter(os);
					File file = new File(fName);
			    byte[] data = new byte[(int) file.length()];
			    dis = new DataInputStream(new FileInputStream(file));
			    dis.readFully(data);
			    writer.writeHeader();
			    writer.writeEmfPicture(data, dim.getWidth(), dim.getHeight());
			    writer.writeTail();
					
			    RTFTransfer rtfTransfer = RTFTransfer.getInstance();
			    Clipboard clipboard = new Clipboard(Display.getDefault());
			    Object[] rtfData = new Object[] { os.toString() };
			    clipboard.setContents(rtfData, new Transfer[] {rtfTransfer});
				}
				catch (final Exception e)
				{
					IStatus status = new Status(IStatus.ERROR, PlotViewerPlugin.PLUGIN_ID, e.getLocalizedMessage(), e);
					PlotViewerPlugin.getDefault().getLog().log(status);
				}
				finally {
					if (os != null) {
						try
						{
							os.close();
						} catch (IOException e)
						{
							// ignore
						}
					}
					if (dis != null) {
						try
						{
							dis.close();
						} catch (IOException e)
						{
							// ignore
						}
					}
				}
			}
			else {
				IStatus status = new Status(IStatus.ERROR, PlotViewerPlugin.PLUGIN_ID, "Target filename missing");
				PlotViewerPlugin.getDefault().getLog().log(status);
			}
		}
	}


}