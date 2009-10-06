package org.mwc.asset.SimulationController;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.mwc.asset.SimulationController.messages"; //$NON-NLS-1$

	public static String SimControllerPlugin_1;

	public static String SimControllerPlugin_2;

	public static String SimControllerUI_0;

	public static String SimControllerUI_1;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
