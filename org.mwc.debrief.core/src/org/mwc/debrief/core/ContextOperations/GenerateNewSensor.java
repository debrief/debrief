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

package org.mwc.debrief.core.ContextOperations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.core.wizards.core.NewSensorWizard;

import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.SplittableLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * embedded class to generate menu-items for creating a new sensor
 */
public class GenerateNewSensor implements RightClickContextItemGenerator {

	private static class AddSensor extends CMAPOperation {

		private final Layers _layers;
		private final Layer _parent;
		private final SensorWrapper _sensorWrapper;

		public AddSensor(final Layers layers, final Layer parent, final SensorWrapper sensor) {
			super("Create sensor");
			_parent = parent;
			_sensorWrapper = sensor;
			_layers = layers;
		}

		@Override
		public boolean canRedo() {
			return true;
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			_parent.add(_sensorWrapper);

			// sorted, do the update
			_layers.fireExtended();

			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			_parent.removeElement(_sensorWrapper);
			return Status.OK_STATUS;
		}

	}

	/**
	 * add items to the popup menu (if suitable tracks are selected)
	 *
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	@Override
	public void generate(final IMenuManager parent, final Layers theLayers, final Layer[] parentLayers,
			final Editable[] subjects) {

		// check only one item is selected
		if (subjects.length != 1)
			return;

		Layer host = null;

		// right, go through the items and have a nice look at them
		for (int i = 0; i < subjects.length; i++) {
			final Editable thisE = subjects[i];

			// is this one we can watch?
			if (thisE instanceof TrackWrapper) {
				host = (Layer) thisE;
			} else if (thisE instanceof SplittableLayer) {
				final SplittableLayer sl = (SplittableLayer) thisE;

				// right, is this the sensors layer?
				if (sl.getName().equals(TrackWrapper.SENSORS_LAYER_NAME)) {
					host = parentLayers[i];
				}
			}
		}

		if (host != null) {
			{
				// ok, create the action
				final Action createSensor = getAction(host, theLayers);

				// ok - set the image descriptor
				createSensor.setImageDescriptor(CorePlugin.getImageDescriptor("icons/SensorFit.png"));

				parent.add(new Separator());
				parent.add(createSensor);
			}
		}
	}

	/**
	 * wrap the action generation bits in a convenience method (suitable for
	 * overring in tests)
	 *
	 * @param candidates the sensors to measure the range from
	 * @param primary    the track to measure to
	 * @return
	 */
	protected Action getAction(final Layer parent, final Layers layers) {
		return new Action("Add new sensor") {
			@Override
			public void run() {
				// get the supporting data
				final NewSensorWizard wizard = new NewSensorWizard();

				final WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
				TrayDialog.setDialogHelpAvailable(true);
				dialog.setHelpAvailable(true);
				dialog.create();
				dialog.open();

				// did it work?
				if (dialog.getReturnCode() == Window.OK) {
					final SensorWrapper newSensor = wizard.getSensorWrapper();
					// ok, go for it.
					// sort it out as an operation
					final IUndoableOperation addSensor = new AddSensor(layers, parent, newSensor);

					// ok, stick it on the buffer
					CorePlugin.run(addSensor);
				}

				System.out.println("done...");
			}
		};
	}

}