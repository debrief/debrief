package org.mwc.cmap.NarrativeViewer.app;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {
	private static Activator ourInstance;

	public static Activator getInstance() {
		return ourInstance;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		ourInstance = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		ourInstance = null;
		super.stop(context);
	}
}