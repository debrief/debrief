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
