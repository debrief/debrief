/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.asset.netasset2;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.mwc.asset.netCore.core.MClient;
import org.mwc.asset.netasset2.connect.IVConnect;
import org.mwc.asset.netasset2.core.PClient;
import org.mwc.asset.netasset2.part.IVPartControl;
import org.mwc.asset.netasset2.part.IVPartMovement;
import org.mwc.asset.netasset2.time.IVTime;
import org.mwc.asset.netasset2.time.IVTimeControl;
import org.mwc.cmap.core.ui_support.PartMonitor;

import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;

import ASSET.Participants.ParticipantDetectedListener;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private PartMonitor _myPartMonitor;
	private PClient _presenter;

	public ApplicationWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
		super(configurer);

		try {
			final MClient model = new MClient();
			_presenter = new PClient(model);
		} catch (final IOException e) {
			Activator.logError(IStatus.ERROR, "Failed to create network model", e);
		}

		final Logger logger = new Logger() {

			@Override
			public void log(final int level, final String category, final String message, final Throwable ex) {
				Activator.logError(IStatus.INFO, message, ex);
			}
		};
		Log.setLogger(logger);
		Log.set(Log.LEVEL_INFO);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	public void postWindowOpen() {
		super.postWindowOpen();

		// ok, get ready to learn about open windows
		setupListeners();

		// and examine any that are already open
		triggerListeners();

	}

	@Override
	public void preWindowOpen() {
		final IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(800, 700));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);

	}

	@Override
	public boolean preWindowShellClose() {
		// ok, now shut down the server-et-al
		_presenter.disconnect();

		if (_myPartMonitor != null) {
			_myPartMonitor.ditch();
		}

		return super.preWindowShellClose();
	}

	public void setupListeners() {

		// declare the listener
		_myPartMonitor = new PartMonitor(
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getPartService());

		// now listen for types to open
		_myPartMonitor.addPartListener(IVTime.class, PartMonitor.OPENED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object instance, final IWorkbenchPart parentPart) {
				_presenter.addTimer((IVTime) instance);
			}
		});
		_myPartMonitor.addPartListener(IVTimeControl.class, PartMonitor.OPENED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object instance, final IWorkbenchPart parentPart) {
				_presenter.addTimeController((IVTimeControl) instance);
			}
		});
		_myPartMonitor.addPartListener(IVConnect.class, PartMonitor.OPENED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object instance, final IWorkbenchPart parentPart) {
				_presenter.addConnector((IVConnect) instance);
			}
		});
		_myPartMonitor.addPartListener(IVPartControl.class, PartMonitor.OPENED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object instance, final IWorkbenchPart parentPart) {
				_presenter.addPartController((IVPartControl) instance);
			}
		});
		_myPartMonitor.addPartListener(IVPartMovement.class, PartMonitor.OPENED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object instance, final IWorkbenchPart parentPart) {
				_presenter.addPartUpdater((IVPartMovement) instance);
			}
		});
		_myPartMonitor.addPartListener(ParticipantDetectedListener.class, PartMonitor.OPENED,
				new PartMonitor.ICallback() {
					@Override
					public void eventTriggered(final String type, final Object instance,
							final IWorkbenchPart parentPart) {
						_presenter.addPartDetector((ParticipantDetectedListener) instance);
					}
				});

	}

	private void triggerListeners() {
		// ok, cycle through the open views and check if we're after any of them
		@SuppressWarnings("deprecation")
		final IViewPart[] views = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getViews();
		for (int i = 0; i < views.length; i++) {
			final IViewPart iViewPart = views[i];
			_myPartMonitor.partOpened(iViewPart);
		}

	}

}
