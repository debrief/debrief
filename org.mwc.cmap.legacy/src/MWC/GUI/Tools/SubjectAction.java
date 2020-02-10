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

package MWC.GUI.Tools;

import MWC.GUI.Editable;

/**
 * sub-interface that receives a subject field to operate upon
 *
 * @author Administrator
 *
 */
public interface SubjectAction {
	/**
	 * whether to fire data extended on completion. Otherwise, just fire reformatted
	 *
	 * @return yes/no
	 */
	boolean doFireExtended();

	/**
	 * do something to the supplied object
	 *
	 * @param subject what we're doing it to
	 */
	void execute(Editable subject);

	/**
	 * whether this action is enabled
	 *
	 * @return yes/no
	 */
	boolean isEnabled();

	/**
	 * @return boolean flag to indicate whether this action may be redone
	 */
	boolean isRedoable();

	/**
	 * @return boolean flag to describe whether this operation may be undone
	 */
	boolean isUndoable();

	/**
	 * method to produce string describing the activity waiting on the buffer
	 */
	@Override
	String toString();

	/**
	 * undo something to the supplied object
	 *
	 * @param subject what we're undoing it to
	 */
	void undo(Editable subject);
}