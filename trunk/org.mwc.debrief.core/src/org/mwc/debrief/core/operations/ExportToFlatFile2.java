package org.mwc.debrief.core.operations;

/** exporter for multi-sensor flat file export
 * 
 * @author ian
 *
 */
public class ExportToFlatFile2 extends ExportToFlatFile
{

	private static final String UPGRADED_VERSION = "1.01";
	private static boolean REQUIRE_TWO_SENSORS = true;
	
	public ExportToFlatFile2()
	{
		super("Export to flat file (SAM twin sensor format)", UPGRADED_VERSION, REQUIRE_TWO_SENSORS);
	}
}

