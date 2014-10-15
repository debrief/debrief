/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Tools;

import MWC.GUI.Editable;

/** sub-interface that receives a subject field to operate upon
 * 
 * @author Administrator
 *
 */
public interface SubjectAction
{
	/**
	 * @return boolean flag to describe whether this operation may be undone
	 */
  boolean isUndoable();
  /**
	 * @return boolean flag to indicate whether this action may be redone
	 */
  boolean isRedoable();
  /**
	 * method to produce string describing the activity waiting on the buffer
	 */
  String toString();

	/** do something to the supplied object
	 * 
	 * @param subject what we're doing it to
	 */
	void execute(Editable subject);
	
	/** undo something to the supplied object
	 * 
	 * @param subject what we're undoing it to
	 */
	void undo(Editable subject);
}
