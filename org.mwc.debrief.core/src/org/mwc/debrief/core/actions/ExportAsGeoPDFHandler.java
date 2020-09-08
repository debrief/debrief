package org.mwc.debrief.core.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.mwc.cmap.plotViewer.actions.CoreEditorAction;

import Debrief.ReaderWriter.GeoPDF.GeoPDF;
import Debrief.ReaderWriter.GeoPDF.GeoPDFBuilder;
import Debrief.ReaderWriter.GeoPDF.GeoPDFBuilder.GeoPDFConfiguration;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;

public class ExportAsGeoPDFHandler extends CoreEditorAction {

	@Override
	protected void execute() {
		try {
			final PlainChart theChart = getChart();
			final Layers theLayers = theChart.getLayers();
			final GeoPDFConfiguration configuration = new GeoPDFConfiguration();
			configuration.setViewportArea(theChart.getProjectionArea());

			configuration.setBackground("C:\\Users\\saulh\\eclipse-workspace\\debrief\\org.mwc.cmap.combined.feature\\root_installs\\sample_data\\SP27GTIF.tif");

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

}
