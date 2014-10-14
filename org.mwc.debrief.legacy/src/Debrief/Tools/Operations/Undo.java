/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
  final Application _theParent;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public Undo(final Application theParent){
    super(theParent, "Undo", null);
    
    _theParent = theParent;
  }
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  public final Action getData()
  {
    final Session theSession = _theParent.getCurrentSession();
    
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
    
    public UndoAction(final UndoBuffer theBuffer){
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
