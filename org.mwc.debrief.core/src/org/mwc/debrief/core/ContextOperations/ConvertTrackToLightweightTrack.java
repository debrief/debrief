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

import java.awt.Color;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;

/**
 * @author ian.mayo
 *
 */
public class ConvertTrackToLightweightTrack implements RightClickContextItemGenerator {

	static class ConvertIt extends CMAPOperation {

		private final Layers _layers;
		private final Editable[] _subjects;

		private Vector<LightweightTrackWrapper> _newLightweights;
		private Vector<TrackWrapper> _oldTracks;
		private final BaseLayer _targetLayer;

		public ConvertIt(final String title, final Layers layers, final Editable[] subjects, final BaseLayer target) {
			super(title);
			_layers = layers;
			_subjects = subjects;
			_targetLayer = target;
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			_newLightweights = new Vector<LightweightTrackWrapper>();
			_oldTracks = new Vector<TrackWrapper>();

			// suspend firing updates, to improve performance
			_layers.suspendFiringExtended(true);

			try {
				// right, get going through the track
				for (int i = 0; i < _subjects.length; i++) {
					final Editable thisE = _subjects[i];
					if (thisE instanceof TrackWrapper) {
						final TrackWrapper oldTrack = (TrackWrapper) thisE;

						// generate the new object
						final LightweightTrackWrapper newTrack = new LightweightTrackWrapper(oldTrack.getName(),
								oldTrack.getVisible(), oldTrack.getNameVisible(), oldTrack.getColor(),
								oldTrack.getLineStyle());

						_newLightweights.add(newTrack);
						_oldTracks.add(oldTrack);

						// switch off the old layer
						oldTrack.setVisible(false);

						// put it into the layer
						_targetLayer.add(newTrack);

						newTrack.setName(oldTrack.getName());
						final Color hisColor = oldTrack.getCustomColor();
						if (hisColor != null) {
							newTrack.setColor(hisColor);
						} else {
							newTrack.setColor(DebriefColors.GOLD);
						}

						final Enumeration<Editable> numer = oldTrack.getPositionIterator();
						while (numer.hasMoreElements()) {
							final FixWrapper fix = (FixWrapper) numer.nextElement();
							newTrack.add(fix);
						}

						// actually, ditch the old track
						_layers.removeThisLayer(oldTrack);
					}
				}
			} finally {
				// allow updates to flow
				_layers.suspendFiringExtended(false);

				// sorted, do the update
				_layers.fireExtended();
			}

			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			// forget about the new tracks
			for (final Iterator<LightweightTrackWrapper> iter = _newLightweights.iterator(); iter.hasNext();) {
				final LightweightTrackWrapper trk = iter.next();
				_targetLayer.removeElement(trk);
			}

			for (final TrackWrapper t : _oldTracks) {
				t.setVisible(true);

				_layers.addThisLayer(t);
			}

			// and clear the new tracks item
			_newLightweights.removeAllElements();
			_newLightweights = null;

			_oldTracks.removeAllElements();
			_oldTracks = null;

			return Status.OK_STATUS;
		}

	}

	private static class NameDialog extends Dialog {
		private Text nameField;
		private String nameString;

		public NameDialog(final Shell parentShell) {
			super(parentShell);
		}

		@Override
		protected void cancelPressed() {
			nameField.setText("");
			super.cancelPressed();
		}

		@Override
		protected void configureShell(final Shell newShell) {
			super.configureShell(newShell);
			newShell.setText("Please provide layer name");
		}

		@Override
		protected Control createDialogArea(final Composite parent) {
			final Composite comp = (Composite) super.createDialogArea(parent);

			final GridLayout layout = (GridLayout) comp.getLayout();
			layout.numColumns = 2;

			final Label nameLabel = new Label(comp, SWT.RIGHT);
			nameLabel.setText("Layer name:");
			nameField = new Text(comp, SWT.SINGLE | SWT.BORDER);

			final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
			nameField.setLayoutData(data);

			return comp;
		}

		public String getName() {
			return nameString;
		}

		@Override
		protected void okPressed() {
			nameString = nameField.getText();
			super.okPressed();
		}
	}

	/**
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	@Override
	public void generate(final IMenuManager parent, final Layers theLayers, final Layer[] parentLayers,
			final Editable[] subjects) {
		int layersValidForConvertToLightweight = 0;

		// right, work through the subjects
		for (int i = 0; i < subjects.length; i++) {
			final Editable thisE = subjects[i];
			if (thisE instanceof TrackWrapper) {
				// ok, we've started...
				layersValidForConvertToLightweight++;
			} else {
				return;
			}
		}

		// ok, is it worth going for?
		if (layersValidForConvertToLightweight > 0) {
			final String title;
			if (layersValidForConvertToLightweight > 1)
				title = "tracks";
			else
				title = "track";

			// right,stick in a separator
			parent.add(new Separator());

			final MenuManager listing = new MenuManager("Convert to lightweight " + title + " in...");

			// ok, determine list of suitable targets
			final Enumeration<Editable> ele = theLayers.elements();
			while (ele.hasMoreElements()) {
				final Editable ed = ele.nextElement();
				if (ed instanceof BaseLayer) {
					final BaseLayer target = (BaseLayer) ed;

					// yes, create the action
					final Action convertToTrack = new Action(target.getName()) {
						@Override
						public void run() {
							// ok, go for it.
							// sort it out as an operation
							final IUndoableOperation convertToTrack1 = new ConvertIt(title, theLayers, subjects,
									target);

							// ok, stick it on the buffer
							runIt(convertToTrack1);
						}
					};

					// ok - flash up the menu item
					listing.add(convertToTrack);
				}
			}

			// and a spare one, which creates a new layer
			final Action convertToTrackInNewLayer = toNewLayer(theLayers, subjects, title);

			// ok - flash up the menu item
			listing.add(convertToTrackInNewLayer);

			// done
			parent.add(listing);

		}

	}

	/**
	 * put the operation firer onto the undo history. We've refactored this into a
	 * separate method so testing classes don't have to simulate the CorePlugin
	 *
	 * @param operation
	 */
	protected void runIt(final IUndoableOperation operation) {
		CorePlugin.run(operation);
	}

	private Action toNewLayer(final Layers theLayers, final Editable[] subjects, final String title) {
		return new Action("New layer...") {
			@Override
			public void run() {
				// get the name
				final NameDialog dialog = new NameDialog(new Shell());
				dialog.open();
				final String name = dialog.getName();
				if (name != null) {
					final String tName = name.trim();

					// create the layer
					final BaseLayer layer = new BaseLayer();
					layer.setName(tName);

					// store it
					theLayers.addThisLayer(layer);

					// ok, go for it.
					// sort it out as an operation
					final IUndoableOperation convertToTrack1 = new ConvertIt(title, theLayers, subjects, layer);

					// ok, stick it on the buffer
					runIt(convertToTrack1);
				}

			}
		};
	}
}
