package org.mwc.cmap.core.property_support.lengtheditor;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public abstract class MultiControlFocusHandler implements FocusListener {

	private CancellableRunnable myDeferredDeactivate;

	/**
	 * In contrast to {@link FocusListener#focusLost(FocusEvent)}, this method
	 * is called only if the focus is NOT transferred between parts of
	 * multi-control figure specified on construction time.
	 */
	protected abstract void focusReallyLost(FocusEvent e);

	public MultiControlFocusHandler(Control... parts) {
		for (Control next : parts) {
			next.addFocusListener(this);
		}
	}

	public void focusGained(FocusEvent e) {
		if (myDeferredDeactivate != null) {
			myDeferredDeactivate.cancel();
		}
		myDeferredDeactivate = null;
	}

	public void focusLost(final FocusEvent e) {
		myDeferredDeactivate = new CancellableRunnable() {

			@Override
			public void doRun() {
				focusReallyLost(e);
			}
		};
		Display.getCurrent().asyncExec(myDeferredDeactivate);
	}

	private static abstract class CancellableRunnable implements Runnable {

		public boolean myIsCancelled;

		public void cancel() {
			myIsCancelled = true;
		}

		public final void run() {
			if (!myIsCancelled) {
				doRun();
			}
		}

		public abstract void doRun();
	}

}