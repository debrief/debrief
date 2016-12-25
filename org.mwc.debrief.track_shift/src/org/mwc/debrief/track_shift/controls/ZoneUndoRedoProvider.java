package org.mwc.debrief.track_shift.controls;

import org.eclipse.core.commands.operations.IUndoableOperation;

public interface ZoneUndoRedoProvider
{
  void execute(IUndoableOperation operation);
}


