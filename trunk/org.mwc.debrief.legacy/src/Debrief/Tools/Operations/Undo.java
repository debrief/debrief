package Debrief.Tools.Operations;

import Debrief.GUI.Frames.*;
import MWC.GUI.Tools.*;
import MWC.GUI.Undo.UndoBuffer;

public final class Undo extends PlainTool
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  private final Application _theParent;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public Undo(Application theParent){
    super(theParent, "Undo", null);
    
    _theParent = theParent;
  }
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  public final Action getData()
  {
    Session theSession = _theParent.getCurrentSession();
    
    if(theSession != null)
      return new UndoAction(theSession.getUndoBuffer());
    else
      return null;
  }



  ///////////////////////////////////////////////////////
  // store action information
  ///////////////////////////////////////////////////////
  final class UndoAction implements Action{
    /** the buffer we are 'doing'
     */
    final UndoBuffer _theBuffer;
    
    public UndoAction(UndoBuffer theBuffer){
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
      if(_theBuffer != null)
			{
        _theBuffer.undo();
				
				_theParent.getCurrentSession().repaint();
			}
    }

  }

}
