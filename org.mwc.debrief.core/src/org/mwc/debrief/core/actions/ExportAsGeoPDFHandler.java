package org.mwc.debrief.core.actions;

import java.util.Enumeration;

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
			e.printStackTrace();
		}

	}

	public void loadBackgroundLayers(final Layers theLayers, final GeoPDFConfiguration configuration) {
		final Enumeration<Editable> enume = theLayers.elements();
		while(enume.hasMoreElements()) {
			final Editable currentEditable = enume.nextElement();
			if (currentEditable instanceof WorldImageLayer) {
				final WorldImageLayer tif = (WorldImageLayer) currentEditable;
				if ("GeoTiff".equals(tif.getDataType())) {
					configuration.addBackground(tif.getFilename());
				}
			}
		}
	}
}
