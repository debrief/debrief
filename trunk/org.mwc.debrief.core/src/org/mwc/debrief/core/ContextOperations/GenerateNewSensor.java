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
public class GenerateNewSensor implements RightClickContextItemGenerator
{

	private static class AddSensor extends CMAPOperation
	{

		private Layers _layers;
		private Layer _parent;
		private SensorWrapper _sensorWrapper;

		public AddSensor(Layers layers, Layer parent,
				SensorWrapper sensor)
		{
			super("Create TMA ellipse");
			_parent = parent;
			_sensorWrapper = sensor;
			_layers = layers;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			_parent.add(_sensorWrapper);

			// sorted, do the update
			_layers.fireExtended();

			return Status.OK_STATUS;
		}

		@Override
		public boolean canRedo()
		{
			return false;
		}

		@Override
		public boolean canUndo()
		{
			return true;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
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
	public void generate(IMenuManager parent, Layers theLayers,
			Layer[] parentLayers, final Editable[] subjects)
	{

		// check only one item is selected
		if (subjects.length != 1)
			return;

		Layer host = null;

		// right, go through the items and have a nice look at them
		for (int i = 0; i < subjects.length; i++)
		{
			Editable thisE = subjects[i];

			// is this one we can watch?
			if (thisE instanceof TrackWrapper)
			{
				host = (Layer) thisE;
			}
			else if (thisE instanceof SplittableLayer)
			{
				SplittableLayer sl = (SplittableLayer) thisE;
				
				// right, is this the sensors layer?
				if (sl.getName().equals(TrackWrapper.SENSORS_LAYER_NAME))
				{
					host = parentLayers[i];
				}
			}
		}

		if (host != null)
		{
			{
				// ok, create the action
				Action createSensor = getAction(host, theLayers);

				// ok - set the image descriptor
				createSensor.setImageDescriptor(CorePlugin
						.getImageDescriptor("icons/SensorFit.png"));

				parent.add(new Separator());
				parent.add(createSensor);
			}
		}
	}

	/**
	 * wrap the action generation bits in a convenience method (suitable for
	 * overring in tests)
	 * 
	 * @param candidates
	 *          the sensors to measure the range from
	 * @param primary
	 *          the track to measure to
	 * @return
	 */
	protected Action getAction(final Layer parent, final Layers layers)
	{
		return new Action("Add new sensor")
		{
			public void run()
			{
				// get the supporting data
				NewSensorWizard wizard = new NewSensorWizard();

				WizardDialog dialog = new WizardDialog(Display.getCurrent()
						.getActiveShell(), wizard);
				TrayDialog.setDialogHelpAvailable(true);
				dialog.setHelpAvailable(true);
				dialog.create();
				dialog.open();

				// did it work?
				if (dialog.getReturnCode() == WizardDialog.OK)
				{
					SensorWrapper newSensor = wizard.getSensorWrapper();
					// ok, go for it.
					// sort it out as an operation
					IUndoableOperation addSensor = new AddSensor(layers, parent, newSensor);

					// ok, stick it on the buffer
					CorePlugin.run(addSensor);
				}

				System.out.println("done...");
			}
		};
	}

}