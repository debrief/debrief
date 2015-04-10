package MWC.Algorithms.Editors;

import org.eclipse.core.commands.operations.IUndoContext;

public interface IUndoable
{
	IUndoContext getUndoContext();
}
