/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
	public CMAPOperation(final String title)
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
	public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException
	{
		return execute(monitor, info);
	}

	
	
}
