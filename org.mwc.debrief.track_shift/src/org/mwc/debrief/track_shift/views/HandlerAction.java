package org.mwc.debrief.track_shift.views;

import org.eclipse.jface.action.Action;

abstract class HandlerAction extends Action
{

  public HandlerAction()
  {

  }

  public void run()
  {
    excecute();

    // do actions refresh
    refreah();

  }

  public abstract void excecute();

  public abstract void refreah();

}