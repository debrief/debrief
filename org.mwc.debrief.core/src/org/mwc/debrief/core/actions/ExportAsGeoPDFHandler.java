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
import java.sql.Timestamp;
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.geotools.gt2plot.WorldImageLayer;
import org.mwc.cmap.plotViewer.actions.CoreEditorAction;
import org.mwc.cmap.plotViewer.actions.IChartBasedEditor;
import org.mwc.debrief.core.editors.PlotEditor;

import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.GeoPDF.AbstractGeoPDFBuilder.GeoPDFConfiguration;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import Debrief.ReaderWriter.GeoPDF.GeoPDF;
import Debrief.ReaderWriter.GeoPDF.GeoPDFSegmentedBuilder;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GUI.Shapes.ChartBoundsWrapper;
import MWC.GenericData.TimePeriod;
import MWC.TacticalData.temporal.TimeControlPreferences;

public class ExportAsGeoPDFHandler extends CoreEditorAction {

	final int MAX_AMOUNT_STEPS_ALLOWED = 100;

	@Override
	protected void execute() {
		try {
			final PlainChart theChart = getChart();
			final Layers theLayers = theChart.getLayers();
			final TimePeriod period = calculatePeriod(theLayers);
			final GeoPDFConfiguration configuration = new GeoPDFConfiguration();
			final IChartBasedEditor editorBasedEditor = getEditor();

			if (editorBasedEditor instanceof PlotEditor) {
				final PlotEditor actualEditor = (PlotEditor) editorBasedEditor;
				final TimeControlPreferences pref = (TimeControlPreferences) actualEditor
						.getAdapter(TimeControlPreferences.class);
				if (pref != null) {
					configuration.setStepDeltaMilliSeconds(pref.getSmallStep().getMillis());
					configuration.setDateFormat(pref.getDTGFormat());
					configuration.setStepSpeedMilliSeconds(pref.getAutoInterval().getMillis());
				}
			}
			loadBackgroundLayers(theLayers, configuration);
			configuration.setViewportArea(theChart.getCanvas().getProjection().getVisibleDataArea());
			configuration.setLandscape(theChart.getScreenSize().getWidth() > theChart.getScreenSize().getHeight());
			configuration.setStartTime(period.getStartDTG());
			configuration.setEndTime(period.getEndDTG());
			configuration.setTempFolder(new Timestamp(System.currentTimeMillis()).getTime() + "");

			if ((period.getEndDTG().getMicros() - period.getStartDTG().getMicros()) / 1000
					/ configuration.getStepDeltaMilliSeconds() > MAX_AMOUNT_STEPS_ALLOWED) {
				MessageBox dialog = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.OK);
				dialog.setText("GeoPDF Export");
				dialog.setMessage("GeoPDF contains more than " + MAX_AMOUNT_STEPS_ALLOWED
						+ " time steps, which may affect performance.  If performance in the exported GeoPDF isn't satisfactory, please use a larger time step.");

				// open dialog and await user selection
				dialog.open();
			}

			final FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
			dialog.setFilterNames(new String[] { "PDF Files", "All Files (*.*)" });
			dialog.setFilterExtensions(new String[] { "*.pdf", "*.*" });

			final String userFileName = dialog.open();
			if (userFileName != null && !userFileName.isEmpty()) {
				configuration.setPdfOutputPath(userFileName);
				final GeoPDFSegmentedBuilder builder = new GeoPDFSegmentedBuilder();
				GeoPDF geoPdf = builder.build(theLayers, configuration);
				Application.logError3(ToolParent.INFO,
						"GeoPDF- Compose files, background and environment are ready to be compiled.", null, false);
				builder.generatePDF(geoPdf, configuration);
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
	
	public TimePeriod calculatePeriod(final Layers theLayers) {
		TimePeriod ans = null;
		
		/**
		 * Let's iterate over all the layers to find the Tracks to export
		 */
		final Enumeration<Editable> enumerationNonInteractive = theLayers.elements();
		while (enumerationNonInteractive.hasMoreElements()) {
			final Editable currentEditable = enumerationNonInteractive.nextElement();
			if (currentEditable instanceof LightweightTrackWrapper) {

				final LightweightTrackWrapper currentTrack = (LightweightTrackWrapper) currentEditable;

				/**
				 * Let's draw only visible tracks.
				 */
				if (currentTrack.getVisible()) {
					final TimePeriod currentPeriod = currentTrack.getVisiblePeriod();
					if (ans == null) {
						ans = currentPeriod;
					}else {
						ans.extend(currentPeriod.getStartDTG());
						ans.extend(currentPeriod.getEndDTG());
					}
				}
			}
			
		}
		
		return ans;
	}

	public void loadBackgroundLayers(final Layers theLayers, final GeoPDFConfiguration configuration) {

		final Enumeration<Editable> enume = theLayers.elements();
		while (enume.hasMoreElements()) {
			final Editable currentEditable = enume.nextElement();
			if (currentEditable instanceof WorldImageLayer) {
				final WorldImageLayer tif = (WorldImageLayer) currentEditable;
				if (ChartBoundsWrapper.WORLDIMAGE_TYPE.equals(tif.getDataType())) {
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
							Application.logError3(ToolParent.INFO, "GeoPDF-Ignoring background " + tif.getName()
									+ " because it has a type non-geotif or file not found", null, false);
						}
					}

				}
			}
		}
	}
}
