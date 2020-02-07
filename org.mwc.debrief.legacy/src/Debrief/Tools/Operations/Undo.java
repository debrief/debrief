/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package Debrief.Tools.Operations;

import Debrief.GUI.Frames.Application;
import Debrief.GUI.Frames.Session;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;
import MWC.GUI.Undo.UndoBuffer;

public final class Undo extends PlainTool {
	///////////////////////////////////////////////////////
	// store action information
	///////////////////////////////////////////////////////
	final class UndoAction implements Action {
		/**
		 * the buffer we are 'doing'
		 */
		final UndoBuffer _theBuffer;

		public UndoAction(final UndoBuffer theBuffer) {
			_theBuffer = theBuffer;
		}

		@Override
		public final void execute() {
			if (_theBuffer != null) {
				_theBuffer.undo();

				_theParent.getCurrentSession().repaint();
			}
		}

		@Override
		public final boolean isRedoable() {
			return false;
		}

		@Override
		public final boolean isUndoable() {
			return false;
		}

		@Override
		public final String toString() {
			return null;
		}

		@Override
		public final void undo() {
			// scrap item
		}

	}

	/**
		 *
		 */
	private static final long serialVersionUID = 1L;

	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////
	final Application _theParent;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////
	public Undo(final Application theParent) {
		super(theParent, "Undo", "images/24/undo.png");

		_theParent = theParent;
	}

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////
	@Override
	public final Action getData() {
		final Session theSession = _theParent.getCurrentSession();

		if (theSession != null)
			return new UndoAction(theSession.getUndoBuffer());
		else
			return null;
	}

}
