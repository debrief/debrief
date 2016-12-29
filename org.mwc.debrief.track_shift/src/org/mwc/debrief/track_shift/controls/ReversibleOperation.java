package org.mwc.debrief.track_shift.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.ICompositeOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class ReversibleOperation extends AbstractOperation implements
    ICompositeOperation
{

  private List<IUndoableOperation> operations;
  protected static final int MODE_EXECUTE = 1;
  protected static final int MODE_REDO = 2;
  protected static final int MODE_UNDO = 3;

  public ReversibleOperation(String label)
  {
    super(label);
    operations = new ArrayList<IUndoableOperation>();
  }

  @Override
  public IStatus execute(IProgressMonitor monitor, IAdaptable info)
      throws ExecutionException
  {

    operations = Collections.unmodifiableList(operations); /*
                                                            * closes the door
                                                            */

    return iterate(MODE_EXECUTE, operations.iterator(), monitor, info);
  }

  @Override
  public IStatus redo(IProgressMonitor monitor, IAdaptable info)
      throws ExecutionException
  {
    return iterate(MODE_REDO, operations.iterator(), monitor, info);
  }

  @Override
  public IStatus undo(IProgressMonitor monitor, IAdaptable info)
      throws ExecutionException
  {

    List<IUndoableOperation> reverseOperations =
        new ArrayList<IUndoableOperation>(operations);
    Collections.reverse(reverseOperations);

    return iterate(MODE_UNDO, reverseOperations.iterator(), monitor, info);
  }

  protected IStatus iterate(final int mode,
      final Iterator<IUndoableOperation> iter, final IProgressMonitor monitor,
      final IAdaptable info) throws ExecutionException
  {

    IStatus status = doIterate(mode, iter, monitor, info);
    refresh();
    return status;

  }

  protected IStatus doIterate(int mode, Iterator<IUndoableOperation> iter,
      IProgressMonitor monitor, IAdaptable info) throws ExecutionException
  {
    IStatus status = Status.OK_STATUS;
    while (iter.hasNext())
    {
      IUndoableOperation op = iter.next();
      switch (mode)
      {
      case MODE_EXECUTE:
        status = op.execute(monitor, info);
        break;
      case MODE_REDO:
        status = op.redo(monitor, info);
        break;
      case MODE_UNDO:
        status = op.undo(monitor, info);
        break;
      default:
        throw new IllegalStateException();
      }
      if (status.getSeverity() != IStatus.OK)
        return status;
    }
    return status;
  }

  /* Sub-operations: */

  public void add(IUndoableOperation operation)
  {
    operations.add(operation);
  }

  public void remove(IUndoableOperation operation)
  {
    operations.remove(operation);
  }

  public int getOperationCount()
  {
    return operations.size();
  }

  protected void refresh()
  {

  }
}