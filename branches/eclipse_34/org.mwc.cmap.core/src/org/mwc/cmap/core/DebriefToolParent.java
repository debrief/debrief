/**
 * 
 */
package org.mwc.cmap.core;

import java.util.*;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mwc.cmap.core.operations.DebriefActionWrapper;

import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.Palette.CreateVPFLayers;

/**
 * @author ian.mayo
 */
public class DebriefToolParent implements ToolParent
{
	/**
	 * the set of preferences we support
	 */
	private IPreferenceStore _myPrefs;
	
	/** the undo buffer we support
	 * 
	 */
	private IOperationHistory _myUndo;

	public DebriefToolParent(IPreferenceStore prefs,
			IOperationHistory undoBuffer)
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
		
		// now add it to the buffer (though we don't need to start with the activate bit)
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
	public Map getPropertiesLike(String pattern)
	{
		Map retMap = new HashMap();

		// SPECIAL PROCESSING. THE ONLY TIME WE USE CURRENTLY USE THIS IS FOR THE VPF PATHS
		if(pattern.equals(CreateVPFLayers.VPF_DATABASE_PROPERTY))
		{
			//
			for(int i=1;i<10;i++)
			{
				String thisVPFPath = pattern + "." + i;
				if(_myPrefs.contains(thisVPFPath))
				{
					// ok, has it been changed from the default?
					if(!_myPrefs.isDefault(thisVPFPath))
						retMap.put(thisVPFPath, _myPrefs.getString(thisVPFPath));
				}
			}
		}
		else
		{
			CorePlugin.logError(Status.ERROR, "Should not be requesting patterned properties", null);
		}
		return retMap;
	}

	/**
	 * @param name
	 * @param value
	 */
	public void setProperty(String name, String value)
	{
		// TODO Auto-generated method stub

	}

	public void logError(int status, String text, Exception e)
	{
		CorePlugin.logError(status, text, e);
	}

}
