package org.mwc.cmap.core.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.*;
import org.eclipse.core.runtime.*;
import org.mwc.cmap.core.CorePlugin;

/** convenience method for setting up a Debrief operation
 * 
 * @author ian.mayo
 *
 */
abstract public class CMAPOperation extends AbstractOperation
{

	/** constructor - that also sorts out the context
	 * 
	 * @param title
	 */
	public CMAPOperation(String title)
	{
		super(title);
		
		// hey, also set the context - so we have the single list of history items
		super.addContext(CorePlugin.CMAP_CONTEXT);		
	}

	/** instead of having to implement REDO, just call execute
	 * @param monitor
	 * @param info
	 * @return
	 * @throws ExecutionException
	 */
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
	{
		return execute(monitor, info);
	}

	
	
}
