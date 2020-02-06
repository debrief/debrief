
package org.mwc.cmap.core.operations;

import org.eclipse.core.commands.operations.IUndoContext;

public interface IUndoable
{
	IUndoContext getUndoContext();
}
