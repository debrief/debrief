/**
 * 
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
	private IPreferenceStore _myPrefs;

	/**
	 * the undo buffer we support
	 * 
	 */
	private IOperationHistory _myUndo;

	/** convenience object, used to get selected import mode back from the popup dialog
	 * 
	 */
	private static String _selectedImportMode = null;
	

	
	public DebriefToolParent(IPreferenceStore prefs, IOperationHistory undoBuffer)
	{
		_myPrefs = prefs;
		_myUndo = undoBuffer;
	}

	/**
	 * @param theCursor
	 */
	public void setCursor(int theCursor)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	public void restoreCursor()
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @param theAction
	 */
	public void addActionToBuffer(Action theAction)
	{
		// ok, better wrap the action first
		DebriefActionWrapper daw = new DebriefActionWrapper(theAction);

		// now add it to the buffer (though we don't need to start with the activate
		// bit)
		try
		{
			_myUndo.execute(daw, null, null);
		}
		catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param name
	 * @return
	 */
	public String getProperty(String name)
	{
		String res = _myPrefs.getString(name);

		return res;
	}

	/**
	 * @param pattern
	 * @return
	 */
	public Map<String, String> getPropertiesLike(String pattern)
	{
		Map<String, String> retMap = new HashMap<String, String>();

		// SPECIAL PROCESSING. THE ONLY TIME WE USE CURRENTLY USE THIS IS FOR THE
		// VPF PATHS
		if (pattern.equals(CreateVPFLayers.VPF_DATABASE_PROPERTY))
		{
			//
			for (int i = 1; i < 10; i++)
			{
				String thisVPFPath = pattern + "." + i;
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
	public void setProperty(String name, String value)
	{
		_myPrefs.putValue(name, value);

	}

	public void logError(int status, String text, Exception e)
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
				Shell active = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
				// ok, popup our custom dialog, let user decide
				SelectImportModeDialog dialog = new SelectImportModeDialog(active, trackName);
				// store the value
				_selectedImportMode = dialog.open();
			}});
		return _selectedImportMode;
	}
}
