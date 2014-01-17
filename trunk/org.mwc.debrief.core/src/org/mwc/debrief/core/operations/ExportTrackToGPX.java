package org.mwc.debrief.core.operations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import org.mwc.debrief.core.gpx.ImportGPX;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

public class ExportTrackToGPX implements RightClickContextItemGenerator
{

	@Override
	public void generate(final IMenuManager parent, final Layers theLayers,
			final Layer[] parentLayers, final Editable[] subjects) 
	{
		int layersValidForConvertToTrack = 0;

		// right, work through the subjects
		for (int i = 0; i < subjects.length; i++)
		{
			final Editable thisE = subjects[i];
			if (thisE instanceof TrackWrapper)
			{
				// cool, go for it!
				layersValidForConvertToTrack++;
			}
		}

		// ok, is it worth going for?
		if (layersValidForConvertToTrack > 0)
		{
			final String title;
			if (layersValidForConvertToTrack > 1)
				title = "Export tracks to GPX";
			else
				title = "Export track to GPX";

			// yes, create the action
			final Action exportToCloud = new Action(title)
			{
				public void run()
				{
					// ok, go for it.
					// sort it out as an operation
					final IUndoableOperation exportTrack = new ExportTrack(title, subjects);

					// ok, stick it on the buffer
					runIt(exportTrack);
				}
			};

			// right,stick in a separator
			parent.add(new Separator());

			// ok - flash up the menu item
			parent.add(exportToCloud);
		}
		
	}
	
	/**
	 * put the operation firer onto the undo history. We've refactored this into a
	 * separate method so testing classes don't have to simulate the CorePlugin
	 * 
	 * @param operation
	 */
	protected void runIt(final IUndoableOperation operation)
	{
		CorePlugin.run(operation);
	}
	
	private static class ExportTrack extends CMAPOperation
	{

		private final Editable[] _subjects;

		public ExportTrack(final String title, final Editable[] subjects)
		{
			super(title);
			_subjects = subjects;
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, 
				final IAdaptable info) throws ExecutionException 
		{			
			final List<TrackWrapper> tracks = new ArrayList<TrackWrapper>(); 

			// right, get going through the track
			for (int i = 0; i < _subjects.length; i++)
			{
				final Editable thisE = _subjects[i];
				if (thisE instanceof TrackWrapper)
				{
					tracks.add((TrackWrapper) thisE);
				}
			}
			
			// if there's only one track - use it in the filename
			final String fileHeader;
			if(tracks.size() == 1)
			{
				fileHeader = tracks.get(0).getName() + "_export";
			}
			
			else
				fileHeader = "debrief_export";
			
			final File someFile = new File(fileHeader + ".gpx");			
			ImportGPX.doExport(tracks, someFile);
			
			return Status.OK_STATUS;
		}
		
		@Override
		public boolean canUndo()
		{
			return false;
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, 
				final IAdaptable info) throws ExecutionException 
		{
			// ignore
			return null;
		}
	}

}
