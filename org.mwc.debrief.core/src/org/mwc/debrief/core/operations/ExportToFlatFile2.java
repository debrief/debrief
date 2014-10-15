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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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

