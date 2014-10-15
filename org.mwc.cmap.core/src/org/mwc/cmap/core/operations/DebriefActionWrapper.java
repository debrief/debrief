/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.*;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Tools.Action;

public class DebriefActionWrapper extends CMAPOperation
{

	final private Action _myAction;

	final private Layers _myLayers;

	private final Layer _changedLayer;

	/**
	 * constructor, so that we can wrap our action
	 * 
	 * @param theAction
	 */
	public DebriefActionWrapper(final Action theAction, final Layers theLayers, final Layer changedLayer)
	{
		super(theAction.toString());

		_myLayers = theLayers;
		_changedLayer = changedLayer;
		_myAction = theAction;
	}

	/**
	 * constructor, so that we can wrap our action
	 * 
	 * @param theAction
	 */
	public DebriefActionWrapper(final Action theAction)
	{
		this(theAction, null, null);
	}

	// ////////////////////////////////////////////////////////////
	// eclipse action bits
	// ////////////////////////////////////////////////////////////

	public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException
	{
		_myAction.execute();

		// hey, fire update
		if (_myLayers != null)
			_myLayers.fireModified(_changedLayer);

		return Status.OK_STATUS;
	}

	public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException
	{
		if (_myAction.isRedoable())
		{
			// nope, just fire away....
			_myAction.execute();

			// and fire update
			if (_myLayers != null)
				_myLayers.fireModified(_changedLayer);

		}

		return Status.OK_STATUS;
	}

	public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException
	{
		if (_myAction.isUndoable())
		{
			_myAction.undo();

			// and fire update
			if (_myLayers != null)
				_myLayers.fireModified(_changedLayer);
		}

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
