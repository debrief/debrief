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
package org.mwc.debrief.core.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.geotools.gt2plot.WorldImageLayer;
import org.mwc.cmap.plotViewer.actions.CoreEditorAction;

import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.GeoPDF.GeoPDF;
import Debrief.ReaderWriter.GeoPDF.GeoPDFBuilder;
import Debrief.ReaderWriter.GeoPDF.GeoPDFBuilder.GeoPDFConfiguration;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;

public class ExportAsGeoPDFHandler extends CoreEditorAction {

	@Override
	protected void execute() {
		try {
			final PlainChart theChart = getChart();
			final Layers theLayers = theChart.getLayers();
			final GeoPDFConfiguration configuration = new GeoPDFConfiguration();
			loadBackgroundLayers(theLayers, configuration);
			configuration.setViewportArea(theChart.getCanvas().getProjection().getVisibleDataArea());
			configuration.setLandscape(theChart.getScreenSize().getWidth() > theChart.getScreenSize().getHeight());

			final FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
			dialog.setFilterNames(new String[] { "PDF Files", "All Files (*.*)" });
			dialog.setFilterExtensions(new String[] { "*.pdf", "*.*" });

			final String userFileName = dialog.open();
			if (userFileName != null && !userFileName.isEmpty()) {
				configuration.setPdfOutputPath(userFileName);
				GeoPDF geoPdf = GeoPDFBuilder.build(theLayers, configuration);
				Application.logError3(ToolParent.INFO,
						"GeoPDF Compose files, background and environment are ready to be compiled.", null, false);
				GeoPDFBuilder.generatePDF(geoPdf, configuration);
			}

		} catch (Exception e) {
			final MultiStatus status = createMultiStatus(e.getLocalizedMessage(), e);

			ErrorDialog.openError(getShell(), "Error", e.getMessage(), status);
		}

	}

	private static MultiStatus createMultiStatus(String msg, Throwable t) {

		final List<Status> childStatuses = new ArrayList<>();
		final StackTraceElement[] stackTraces = t.getStackTrace();

		for (StackTraceElement stackTrace : stackTraces) {
			Status status = new Status(IStatus.ERROR, "Export GeoPDF", stackTrace.toString());
			childStatuses.add(status);
		}

		final MultiStatus ms = new MultiStatus("org.mwc.debrief.core", IStatus.ERROR,
				childStatuses.toArray(new Status[] {}), t.toString(), t);
		return ms;
	}

	public void loadBackgroundLayers(final Layers theLayers, final GeoPDFConfiguration configuration) {

		final Enumeration<Editable> enume = theLayers.elements();
		while (enume.hasMoreElements()) {
			final Editable currentEditable = enume.nextElement();
			if (currentEditable instanceof WorldImageLayer) {
				final WorldImageLayer tif = (WorldImageLayer) currentEditable;
				if ("GeoTiff".equals(tif.getDataType())) {
					if (new File(tif.getFilename()).exists()) {
						configuration.addBackground(tif.getFilename());
					} else {

						boolean found = false;
						IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
						IWorkbenchPage activePage = window.getActivePage();

						IEditorPart activeEditor = activePage.getActiveEditor();

						if (activeEditor != null) {
							IEditorInput input = activeEditor.getEditorInput();

							IProject project = input.getAdapter(IProject.class);
							if (project == null) {
								IResource resource = input.getAdapter(IResource.class);
								if (resource != null) {
									project = resource.getProject();
									final File fileInProjectFolder = new File(
											project.getLocation().toString() + File.separatorChar + tif.getFilename());
									if (fileInProjectFolder.exists()) {
										found = true;
										configuration.addBackground(fileInProjectFolder.getAbsolutePath());
									}
								}
							}
						}

						if (!found) {
							Application.logError3(ToolParent.INFO, "Ignoring background " + tif.getName()
									+ " because it has a type non-geotif or file not found", null, false);
						}
					}

				}
			}
		}
	}
}
