package org.mwc.debrief.core.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.mwc.cmap.geotools.gt2plot.WorldImageLayer;
import org.mwc.cmap.plotViewer.actions.CoreEditorAction;

import Debrief.ReaderWriter.GeoPDF.GeoPDF;
import Debrief.ReaderWriter.GeoPDF.GeoPDFBuilder;
import Debrief.ReaderWriter.GeoPDF.GeoPDFBuilder.GeoPDFConfiguration;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;

public class ExportAsGeoPDFHandler extends CoreEditorAction {

	@Override
	protected void execute() {
		try {
			final PlainChart theChart = getChart();
			final Layers theLayers = theChart.getLayers();
			final GeoPDFConfiguration configuration = new GeoPDFConfiguration();
			loadBackgroundLayers(theLayers, configuration);
			configuration.setViewportArea(theChart.getProjectionArea());
			configuration.setLandscape(theChart.getScreenSize().getWidth() > theChart.getScreenSize().getHeight());
			
			final FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
			dialog.setFilterNames(new String[] { "PDF Files", "All Files (*.*)" });
			dialog.setFilterExtensions(new String[] { "*.pdf", "*.*" });
			
			final String userFileName = dialog.open();
			if (userFileName != null && !userFileName.isEmpty()) {
				configuration.setPdfOutputPath(userFileName);
				GeoPDF geoPdf = GeoPDFBuilder.build(theLayers, configuration);
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

        for (StackTraceElement stackTrace: stackTraces) {
            Status status = new Status(IStatus.ERROR,
                    "Export GeoPDF", stackTrace.toString());
            childStatuses.add(status);
        }

        final MultiStatus ms = new MultiStatus("org.mwc.debrief.core",
                IStatus.ERROR, childStatuses.toArray(new Status[] {}),
                t.toString(), t);
        return ms;
    }

	public void loadBackgroundLayers(final Layers theLayers, final GeoPDFConfiguration configuration) {
		final Enumeration<Editable> enume = theLayers.elements();
		while(enume.hasMoreElements()) {
			final Editable currentEditable = enume.nextElement();
			if (currentEditable instanceof WorldImageLayer) {
				final WorldImageLayer tif = (WorldImageLayer) currentEditable;
				if ("GeoTiff".equals(tif.getDataType()) && new File(tif.getName()).exists()) {
					configuration.addBackground(tif.getFilename());
				}
			}
		}
	}
}
