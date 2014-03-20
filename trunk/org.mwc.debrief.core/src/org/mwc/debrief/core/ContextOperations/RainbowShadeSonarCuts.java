/**  Class providing functionality to shade a series of sensor contacts. When there is a dense block of 
 * sensor contacts, it can be different to distinguish the "flow" of contacts.
 * 
 */
package org.mwc.debrief.core.ContextOperations;

import java.util.ArrayList;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class RainbowShadeSonarCuts implements RightClickContextItemGenerator {

	/**
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	public void generate(final IMenuManager parent, final Layers theLayers,
			final Layer[] parentLayers, final Editable[] subjects) {

		ArrayList<SensorContactWrapper> cuts = new ArrayList<SensorContactWrapper>();
		SensorWrapper theSensor = null;

		Layer parentLayer = null;

		if (parentLayers != null) {
			if (parentLayers.length == 1) {
				parentLayer = parentLayers[0];
			}
		}

		// are they items we're interested in?
		for (int i = 0; i < subjects.length; i++) {
			final Editable thisE = subjects[i];
			if (thisE instanceof SensorWrapper) {
				// just check that there's only one item selected
				if (subjects.length == 1) {
					theSensor = (SensorWrapper) thisE;
				}
			} else if (thisE instanceof SensorContactWrapper) {
				cuts.add((SensorContactWrapper) thisE);
			}
		}

		// ok, do we have a single sensor?
		if (theSensor != null) {
			// right,stick in a separator
			parent.add(new Separator());

			final SensorWrapper theSensorFinal = theSensor;
			final Layer parentFinal = parentLayer;

			// create this operation
			final String title1 = "Shade in rainbow colors";
			final Action doRainbowShade = new Action(title1) {
				public void run() {
					final IUndoableOperation theAction = new ShadeCutsOperation(
							title1, theLayers, parentFinal, theSensorFinal,
							new RainbowShade());
					CorePlugin.run(theAction);
				}
			};
			parent.add(doRainbowShade);
			// create this operation
			final String title2 = "Shade in blue-red spectrum";
			final Action doBlueShade = new Action(title2) {
				public void run() {
					final IUndoableOperation theAction = new ShadeCutsOperation(
							title2, theLayers, parentFinal, theSensorFinal,
							new BlueShade());
					CorePlugin.run(theAction);
				}
			};
			parent.add(doBlueShade);

		} else if (cuts.size() > 0) {
			// right,stick in a separator
			parent.add(new Separator());

			// convert the list to an array
			SensorContactWrapper[] listTemplate = new SensorContactWrapper[] {};
			final SensorContactWrapper[] list = cuts.toArray(listTemplate);
			final Layer parentFinal = parentLayer;

			// create this operation
			final String title1 = "Shade in rainbow colors";
			final Action doRainbowShade = new Action(title1) {
				public void run() {
					final IUndoableOperation theAction = new ShadeCutsOperation(
							title1, theLayers, parentFinal, list,
							new RainbowShade());
					CorePlugin.run(theAction);
				}
			};
			parent.add(doRainbowShade);
			// create this operation
			final String title2 = "Shade in blue-red spectrum";
			final Action doBlueShade = new Action(title2) {
				public void run() {
					final IUndoableOperation theAction = new ShadeCutsOperation(
							title2, theLayers, parentFinal, list,
							new BlueShade());
					CorePlugin.run(theAction);
				}
			};
			parent.add(doBlueShade);

		}
	}

	private static interface ShadeOperation {
		/**
		 * apply shading to the supplied cut
		 * 
		 * @param cut
		 *            the cut to shade
		 */
		public void shadeThis(SensorContactWrapper cut);
	}

	/**
	 * provide shading through the blue-red spectrum
	 * 
	 * @author ian
	 * 
	 */
	private static class BlueShade implements ShadeOperation {

		@Override
		public void shadeThis(SensorContactWrapper cut) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * provide shading through the full rainbow spectrum
	 * 
	 * @author ian
	 * 
	 */
	private static class RainbowShade implements ShadeOperation {

		@Override
		public void shadeThis(SensorContactWrapper cut) {
			// TODO Auto-generated method stub

		}

	}

	private static class ShadeCutsOperation extends CMAPOperation {

		/**
		 * the parent to update on completion
		 */
		private final Layers _layers;
		private final Layer _parent;
		@SuppressWarnings("unused")
		private final SensorContactWrapper[] _subjects;
		@SuppressWarnings("unused")
		private final ShadeOperation _shader;

		public ShadeCutsOperation(final String title, final Layers theLayers,
				final Layer parentLayer, final SensorContactWrapper[] subjects,
				final ShadeOperation shader) {
			super(title);
			_layers = theLayers;
			_parent = parentLayer;
			_subjects = subjects;
			_shader = shader;
		}

		public ShadeCutsOperation(final String title, final Layers theLayers,
				final Layer parentLayer, final SensorWrapper sensor,
				final ShadeOperation shader) {
			super(title);
			_layers = theLayers;
			_parent = parentLayer;
			_shader = shader;
			SensorContactWrapper[] sample = new SensorContactWrapper[] {};
			_subjects = sensor.getItemsBetween(sensor.getStartDTG(),
					sensor.getEndDTG()).toArray(sample);
		}

		public IStatus execute(final IProgressMonitor monitor,
				final IAdaptable info) throws ExecutionException {
			// TODO: actually shade the items

			// TODO: loop through items

			// TODO: pass each item to the shader object

			// ok, done - let the layers object declare what has been edited
			fireModified();

			return Status.OK_STATUS;
		}

		@Override
		public boolean canRedo() {
			return false;
		}

		@Override
		public boolean canUndo() {
			return false;
		}

		private void fireModified() {
			if (_parent != null) {
				_layers.fireReformatted(_parent);
			} else {
				_layers.fireReformatted(null);
			}
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor,
				final IAdaptable info) throws ExecutionException {
			CorePlugin.logError(Status.INFO,
					"Undo not permitted for merge operation", null);
			return null;
		}
	}
}
