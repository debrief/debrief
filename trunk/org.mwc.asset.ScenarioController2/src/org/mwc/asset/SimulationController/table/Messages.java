package org.mwc.asset.SimulationController.table;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "org.mwc.asset.SimulationController.table.messages"; //$NON-NLS-1$

	public static String SimulationTable_1;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
