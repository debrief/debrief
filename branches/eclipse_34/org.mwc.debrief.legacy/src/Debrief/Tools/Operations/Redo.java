package Debrief.Tools.Operations;

import Debrief.GUI.Frames.*;
import MWC.GUI.Tools.*;
import MWC.GUI.Undo.UndoBuffer;

public final class Redo extends PlainTool
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  private final Application _theParent;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public Redo(Application theParent){
    super(theParent, "Redo", null);
    
    _theParent = theParent;
  }
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  public final Action getData()
  {
    Session theSession = _theParent.getCurrentSession();
    
    if(theSession != null)
      return new RedoAction(theSession.getUndoBuffer());
    else
      return null;
  }


  ///////////////////////////////////////////////////////
  // store action information
  ///////////////////////////////////////////////////////
  final class RedoAction implements Action{
    /** the buffer we are 'doing'
     */
    final UndoBuffer _theBuffer;
    
    public RedoAction(UndoBuffer theBuffer){
      _theBuffer = theBuffer;
    }
    
    public final boolean isUndoable(){
      return false;
    }

    public final boolean isRedoable(){
      return false;
    }
    
    public final String toString(){
      return null;
    }                                        
    
    public final void undo(){
      // scrap item
    }

    public final void execute(){
      _theBuffer.redo();
    }

  }

}
