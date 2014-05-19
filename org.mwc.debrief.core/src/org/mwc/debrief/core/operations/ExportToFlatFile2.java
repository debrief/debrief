package org.mwc.debrief.core.operations;

import Debrief.ReaderWriter.FlatFile.FlatFileExporter;

/** exporter for multi-sensor flat file export
 * 
 * @author ian
 *
 */
public class ExportToFlatFile2 extends ExportToFlatFile
{

	private static boolean REQUIRE_TWO_SENSORS = true;
	
	public ExportToFlatFile2()
	{
		super("Export to flat file (SAM twin sensor format)", FlatFileExporter.UPDATED_VERSION, REQUIRE_TWO_SENSORS);
	}
}

