/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.cmap.plotViewer.actions;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
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
public class ExportRTF extends CoreEditorAction {
	public static ToolParent _theParent = null;

	/**
	 * ok, store who the parent is for the operation
	 *
	 * @param theParent
	 */
	public static void init(final ToolParent theParent) {
		_theParent = theParent;
	}

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
	public ExportRTF() {
		this(false, true);
	}

	/**
	 * @param toClipboard
	 * @param toFile
	 */
	public ExportRTF(final boolean toClipboard, final boolean toFile) {
		super();
		_writeToClipboard = toClipboard;
		_writeToFile = toFile;
	}

	/**
	 * and execute..
	 */
	@Override
	protected IStatus executeInJob(final IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		final PlainChart theChart = getChart();

		if (_theParent == null) {
			CorePlugin.logError(IStatus.ERROR, "Tool parent missing for Write Metafile", null);
			return Status.CANCEL_STATUS;
		}

		monitor.beginTask("Export image as WMF embedded in RTF", IProgressMonitor.UNKNOWN);

		final WriteMetafile write = new WriteMetafile(_theParent, theChart, _writeToFile) {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * @param mf
			 */
			@Override
			protected void paintToMetafile(final MetafileCanvas mf) {
				final SWTCanvas sc = (SWTCanvas) theChart.getCanvas();
				sc.paintPlot(mf);
			}

		};
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		write.execute();
		if (!write.isWritable()) {
			final Shell shell = getShell();
			MessageDialog.openError(shell, "Error", write.getErrorMessage());
			return Status.OK_STATUS;
		}
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		// ok, do we want to write it to the clipboard?
		if (_writeToClipboard) {
			// try to get the filename
			final String fName = MetafileCanvas.getLastFileName();

			CorePlugin.logError(IStatus.INFO, "About to write WMF to:" + fName, null);

			if (fName != null) {
				// create the clipboard

				// try to copy the wmf to the clipboard
				final ByteArrayOutputStream[] os = new ByteArrayOutputStream[1];
				DataInputStream dis = null;
				try {
					// get the dimensions
					final Dimension dim = MetafileCanvas.getLastScreenSize();

					os[0] = new ByteArrayOutputStream();
					final RTFWriter writer = new RTFWriter(os[0]);
					final File file = new File(fName);
					final byte[] data = new byte[(int) file.length()];
					dis = new DataInputStream(new FileInputStream(file));
					dis.readFully(data);
					writer.writeHeader();
					writer.writeEmfPicture(data, dim.getWidth(), dim.getHeight());
					writer.writeTail();

					CorePlugin.logError(IStatus.INFO, "WMF written", null);

					final Runnable runnable = new Runnable() {

						@Override
						public void run() {
							CorePlugin.logError(IStatus.INFO, "About write to clipboard", null);
							final RTFTransfer rtfTransfer = RTFTransfer.getInstance();
							final Clipboard clipboard = new Clipboard(Display.getDefault());
							final Object[] rtfData = new Object[] { os[0].toString() };
							clipboard.setContents(rtfData, new Transfer[] { rtfTransfer });
							CorePlugin.logError(IStatus.INFO, "Written to clipboard", null);
						}
					};
					if (Display.getCurrent() != null) {
						runnable.run();
					} else {
						Display.getDefault().syncExec(runnable);
					}

				} catch (final Exception e) {
					CorePlugin.logError(IStatus.INFO, "Failed writing WMF", e);

					final IStatus status = new Status(IStatus.ERROR, PlotViewerPlugin.PLUGIN_ID,
							e.getLocalizedMessage(), e);
					PlotViewerPlugin.getDefault().getLog().log(status);
				} finally {
					if (os[0] != null) {
						try {
							os[0].close();
						} catch (final IOException e) {
							// ignore
						}
					}
					if (dis != null) {
						try {
							dis.close();
						} catch (final IOException e) {
							// ignore
						}
					}
				}
			} else {
				CorePlugin.logError(IStatus.INFO, "Target filename missing", null);
				final IStatus status = new Status(IStatus.ERROR, PlotViewerPlugin.PLUGIN_ID, "Target filename missing");
				PlotViewerPlugin.getDefault().getLog().log(status);
			}
		}

		monitor.done();
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

}