package org.mwc.cmap.core.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.*;
import org.eclipse.core.runtime.*;
import org.mwc.cmap.core.CorePlugin;

import MWC.GUI.Tools.Action;

public class DebriefChartModifiedActionWrapper extends AbstractOperation
{

	final private Action _myAction;

	/** constructor, so that we can wrap our action
	 * 
	 * @param theAction
	 */
	public DebriefChartModifiedActionWrapper(Action theAction)
	{
		super(theAction.toString());
		
		// put in the global context, for some reason
		super.addContext(CorePlugin.CMAP_CONTEXT);
		
		_myAction = theAction;
	}
	
	//////////////////////////////////////////////////////////////
	// eclipse action bits
	//////////////////////////////////////////////////////////////
	
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
	{
		_myAction.execute();
		
		return Status.OK_STATUS;
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
	{
		if(_myAction.isRedoable())
			_myAction.execute();
		
		return Status.OK_STATUS;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
	{
		if(_myAction.isUndoable())
			_myAction.undo();

		return Status.OK_STATUS;
	}

	/**
	 * @return
	 */
	public boolean canExecute()
	{
		return true;
	}

	/**
	 * @return
	 */
	public boolean canRedo()
	{
		return _myAction.isRedoable();
	}

	/**
	 * @return
	 */
	public boolean canUndo()
	{
		return _myAction.isUndoable();
	}


	/**
	 * @return
	 */
	public String toString()
	{
		return _myAction.toString();
	}
	
	


}
