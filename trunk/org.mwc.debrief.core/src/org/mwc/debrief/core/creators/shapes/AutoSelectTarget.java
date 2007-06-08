/**
 * 
 */
package org.mwc.debrief.core.creators.shapes;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.*;

/**
 * @author ian.mayo
 *
 */
public class AutoSelectTarget implements IWorkbenchWindowActionDelegate
{
	
	private static boolean _ticked = true; 

	public static boolean getAutoSelectTarget()
	{
		return _ticked;
	}
	
	public static void setAutoSelectTarget(boolean yes)
	{
		_ticked = yes;
	}
	
	/**
	 * 
	 */
	public void dispose()
	{
	}

	/**
	 * @param window
	 */
	public void init(IWorkbenchWindow window)
	{
	}

	/**
	 * @param action
	 */
	public void run(IAction action)
	{
		_ticked = !_ticked;
		System.out.println("state is now:" + _ticked);
	}

	/**
	 * @param action
	 * @param selection
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

}
