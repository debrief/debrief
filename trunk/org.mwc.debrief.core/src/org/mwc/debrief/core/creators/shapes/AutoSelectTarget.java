/**
 * 
 */
package org.mwc.debrief.core.creators.shapes;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author ian.mayo
 *
 */
public class AutoSelectTarget extends AbstractHandler implements IWorkbenchWindowActionDelegate
{
	
	private static boolean _ticked = true; 

	public static boolean getAutoSelectTarget()
	{
		return _ticked;
	}
	
	public static void setAutoSelectTarget(final boolean yes)
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
	public void init(final IWorkbenchWindow window)
	{
	}

	/**
	 * @param action
	 */
	public void run(final IAction action)
	{
		_ticked = !_ticked;
		System.out.println("state is now:" + _ticked);
	}

	/**
	 * @param action
	 * @param selection
	 */
	public void selectionChanged(final IAction action, final ISelection selection)
	{
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{

		Command command = event.getCommand();
		@SuppressWarnings("unused")
		boolean oldValue = HandlerUtil.toggleCommandState(command);
		_ticked = !_ticked;
		return null;
	}
}
