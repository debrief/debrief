/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core;

import java.util.*;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.core.ui_support.SelectImportModeDialog;

import Debrief.ReaderWriter.Replay.ImportReplay.ProvidesModeSelector;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.Palette.CreateVPFLayers;

/**
 * @author ian.mayo
 */
public class DebriefToolParent implements ToolParent, ProvidesModeSelector
{
	/**
	 * the set of preferences we support
	 */
	private final IPreferenceStore _myPrefs;

	/**
	 * the undo buffer we support
	 * 
	 */
	private final IOperationHistory _myUndo;

	/** convenience object, used to get selected import mode back from the popup dialog
	 * 
	 */
	private static String _selectedImportMode = null;
	

	
	public DebriefToolParent(final IPreferenceStore prefs, final IOperationHistory undoBuffer)
	{
		_myPrefs = prefs;
		_myUndo = undoBuffer;
	}

	/**
	 * @param theCursor
	 */
	public void setCursor(final int theCursor)
	{

	}

	/**
	 * 
	 */
	public void restoreCursor()
	{

	}

	/**
	 * @param theAction
	 */
	public void addActionToBuffer(final Action theAction)
	{
		// ok, better wrap the action first
		final DebriefActionWrapper daw = new DebriefActionWrapper(theAction);

		// now add it to the buffer (though we don't need to start with the activate
		// bit)
		try
		{
			_myUndo.execute(daw, null, null);
		}
		catch (final ExecutionException e)
		{
			CorePlugin.logError(Status.ERROR,
					"Executing newly added action", e);
		}

	}

	/**
	 * @param name
	 * @return
	 */
	public String getProperty(final String name)
	{
		final String res = _myPrefs.getString(name);

		return res;
	}

	/**
	 * @param pattern
	 * @return
	 */
	public Map<String, String> getPropertiesLike(final String pattern)
	{
		final Map<String, String> retMap = new HashMap<String, String>();

		// SPECIAL PROCESSING. THE ONLY TIME WE USE CURRENTLY USE THIS IS FOR THE
		// VPF PATHS
		if (pattern.equals(CreateVPFLayers.VPF_DATABASE_PROPERTY))
		{
			//
			for (int i = 1; i < 10; i++)
			{
				final String thisVPFPath = pattern + "." + i;
				if (_myPrefs.contains(thisVPFPath))
				{
					// ok, has it been changed from the default?
					if (!_myPrefs.isDefault(thisVPFPath))
						retMap.put(thisVPFPath, _myPrefs.getString(thisVPFPath));
				}
			}
		}
		else
		{
			CorePlugin.logError(Status.ERROR,
					"Should not be requesting patterned properties", null);
		}
		return retMap;
	}

	/**
	 * @param name
	 * @param value
	 */
	public void setProperty(final String name, final String value)
	{
		_myPrefs.putValue(name, value);

	}

	public void logError(final int status, final String text, final Exception e)
	{
		CorePlugin.logError(status, text, e);
	}

	/** popup a dialog to let the user select the import mode
	 * @return selected mode, from ImportReplay
	 */
	public String getSelectedImportMode(final String trackName)
	{
		_selectedImportMode= null;
		final Display current = Display.getDefault();
		current.syncExec(new Runnable(){
			public void run()
			{
				final Shell active = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
				// ok, popup our custom dialog, let user decide
				final SelectImportModeDialog dialog = new SelectImportModeDialog(active, trackName);
				// store the value
				_selectedImportMode = dialog.open();
			}});
		return _selectedImportMode;
	}
}
