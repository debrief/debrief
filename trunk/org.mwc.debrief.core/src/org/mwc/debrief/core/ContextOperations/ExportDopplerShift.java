package org.mwc.debrief.core.ContextOperations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.cmap.core.ui_support.wizards.SimplePageListWizard;
import org.mwc.cmap.core.wizards.DirectorySelectorWizardPage;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.ReaderWriter.FlatFile.DopplerShift.DopplerShiftExporter;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.SplittableLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;

/**
 * embedded class to generate menu-items for creating a new sensor
 */
public class ExportDopplerShift implements RightClickContextItemGenerator
{
	private static final String ACTION_NAME = "Export Doppler Shift";
	public static final String FILE_SUFFIX = "csv";

	private static class ExportShift extends CMAPOperation
	{
		private Layer _parent;
		private SensorWrapper _sensorWrapper;

		public ExportShift(Layers layers, Layer parent, SensorWrapper sensor)
		{
			super(ACTION_NAME);
			_parent = parent;
			_sensorWrapper = sensor;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			// sort out the destination file name
			String filePath = null;

			// sort out what type of file it is
			String sensorType = null;

			SimplePageListWizard wizard = new SimplePageListWizard();
			DirectorySelectorWizardPage exportPage = new DirectorySelectorWizardPage("ExportDoppler", 
					"Export Doppler Shift data", 
					"Please select the directory where Debrief will \nplace the exported Doppler shift file.",
					"org.mwc.debrief.core", 
					"images/DopplerEffect.png" );
			wizard.addWizard(exportPage);
			WizardDialog dialog = new WizardDialog(Display.getCurrent()
					.getActiveShell(), wizard);
			dialog.create();
			dialog.open();

			// did it work?
			if (dialog.getReturnCode() == WizardDialog.OK)
			{
				if (exportPage.isPageComplete())
				{
					filePath = exportPage.getFileName();
				}

				DopplerShiftExporter ff = new DopplerShiftExporter();
				WatchableList primaryTrack = null;
				WatchableList[] secondaryTracks = null;
				TimePeriod period = new TimePeriod.BaseTimePeriod(new HiResDate(12),
						new HiResDate(2322));
				String theData = ff.export(primaryTrack, secondaryTracks, period,
						sensorType);

				// now write the data to file
				final String HOST_NAME = "HOST_NAME";// primaryTrack.getName();
				final String HOST_DATE = MWC.Utilities.TextFormatting.FormatRNDateTime
						.toMediumString(period.getStartDTG().getDate().getTime());

				final String fileName = filePath + File.separator + HOST_NAME + "_"
						+ HOST_DATE + "." + FILE_SUFFIX;

				BufferedWriter out = null;
				try
				{
					out = new BufferedWriter(new FileWriter(fileName));
					out.write(theData);
//					CorePlugin.showMessage(ACTION_NAME,
//							"Tracks successfullly exported to Doppler Shift format");
					
					out.flush();
				}
				catch (FileNotFoundException e)
				{
					DebriefPlugin.logError(Status.ERROR, "Unable to find output file:"
							+ fileName, e);

				}
				catch (IOException e)
				{
					DebriefPlugin.logError(Status.ERROR, "Whilst writing to output file:"
							+ fileName, e);
				}
				finally
				{
					try
					{
						if (out != null)
						{
							out.close();
						}
					}
					catch (IOException e)
					{
						DebriefPlugin.logError(Status.ERROR, "Whilst closing output file:"
								+ fileName, e);
					}
				}
			}

			return Status.OK_STATUS;
		}

		@Override
		public boolean canRedo()
		{
			return true;
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
				createSensor.setImageDescriptor(DebriefPlugin
						.getImageDescriptor("icons/export_doppler.png"));

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
		return new Action(ACTION_NAME)
		{
			public void run()
			{
				IUndoableOperation doExport = new ExportShift(layers, parent, null);
				CorePlugin.run(doExport);
			}
		};
	}

}