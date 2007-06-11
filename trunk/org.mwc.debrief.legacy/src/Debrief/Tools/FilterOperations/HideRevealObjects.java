package Debrief.Tools.FilterOperations;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: HideRevealObjects.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: HideRevealObjects.java,v $
// Revision 1.3  2004/11/25 10:24:27  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.2  2004/11/22 13:41:01  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.1.1.2  2003/07/21 14:48:23  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-03-25 15:54:14+00  ian_mayo
// Implement "Reset me" buttons
//
// Revision 1.3  2003-03-19 15:37:18+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2003-02-07 09:02:41+00  ian_mayo
// remove unnecessary toda comments
//
// Revision 1.1  2002-09-24 10:55:49+01  ian_mayo
// Initial revision
//

import Debrief.Tools.Tote.*;
import java.util.*;

import MWC.GUI.Tools.Action;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;

import javax.swing.*;

/**************************************************************
 * Class which lets user reformat a series of positions in the Track/Time toolbox.
 * Whilst the Toolbox may contain different types of object, this just edits
 * Fixes contained in Tracks
 **************************************************************/

public final class HideRevealObjects implements FilterOperation
{

  /*********************************************************************************************
   * member objects
   **********************************************************************************************/

  /** the selected objects
   *
   */
  private Vector _theObjects = null;

  /** the set of layers which we will update
   *
   */
  private final Layers _theLayers;

  /** the separator we use in the operation description
   *
   */
  private final String _theSeparator = System.getProperties().getProperty("line.separator");

  /*********************************************************************************************
   * constructor
   **********************************************************************************************/

  /** constructor
   * @param theLayers the layers object to be updated on our completion
   */
  public HideRevealObjects(Layers theLayers)
  {
    _theLayers = theLayers;
  }

  /*********************************************************************************************
   * member methods
   **********************************************************************************************/

  public final String getDescription()
  {
    String res = "2. Select objects to be hidden/reveals";
    res += _theSeparator + "3. Press 'Apply' button";
    res += _theSeparator + "4. Select Hide/Reveal from the dialog box which appears";
    res += _theSeparator + 	"====================";
    res += _theSeparator + 	"This operations allows a group of objects to be hidden/revealed";
    return res;
  }

  /** get the property which is to be edited
   *
   */
  private boolean getHideReveal()
  {
    boolean res = false;

    // create the selections
    String[] list= new String[]{"Hide", "Reveal"};

    // find out which one the user wants to edit
    Object val = JOptionPane.showInputDialog(null,
                                             "Do you wish to hide or reveal the selected objects?",
                                             "Hide/Reveal objects",
                                             JOptionPane.QUESTION_MESSAGE,
                                             null,
                                             list,
                                             null);

    String selected = (String)val;
    if(selected.equals("Hide"))
    {
      res = true;
    }
    else
      res = false;

    return res;
  }

  public final void setPeriod(HiResDate startDTG, HiResDate finishDTG)
  {
    // ignore, since we don't mind
  }

  public final void setTracks(Vector selectedTracks)
  {
    // store the objects
    _theObjects = selectedTracks;
  }

  /** the user has pressed RESET whilst this button is pressed
   *
   * @param startTime the new start time
   * @param endTime the new end time
   */
  public void resetMe(HiResDate startTime, HiResDate endTime)
  {
  }

  public final void execute()
  {
  }

  public final Action getData()
  {
    // produce the list of modifications to be made
    HideRevealAction res = null;

    // check we've got some tracks
    if(_theObjects == null)
    {
      MWC.GUI.Dialogs.DialogFactory.showMessage("Hide/Reveal objects", "Please select some objects prior to starting");
      return res;
    }

    // find out what we're doing
    boolean hideIt = getHideReveal();

    // make our symbols and labels visible
    Enumeration iter = _theObjects.elements();
    while(iter.hasMoreElements())
    {
      WatchableList wl = (WatchableList)iter.nextElement();

      if(wl instanceof Plottable)
      {
        Plottable thisP = (Plottable)wl;

        if(res == null)
        {
          res = new HideRevealAction(hideIt, this._theLayers);
        }

        // and store it
        res.changeThisObject(thisP);
      }
    }

    // return the new action
    return res;
  }

  public final String getLabel()
  {
    return "Hide/Reveal objects";
  }

  public final String getImage()
  {
    return null;
  }

  public final void actionPerformed(java.awt.event.ActionEvent p1)
  {
  }

  public final void close()
  {
  }

  ///////////////////////////////////////////////////////
  // store action information
  ///////////////////////////////////////////////////////
  final class HideRevealAction implements Action
  {
    private final Vector _valuesChanged;
    private final boolean _hideIt;
    private final Layers _theLayers;

    public HideRevealAction(boolean hideIt, Layers theLayers)
    {
      _hideIt = hideIt;
      _theLayers = theLayers;
      _valuesChanged = new Vector(0,1);
    }

    /** add an update to a new object
     *
     */
    public final void changeThisObject(Plottable val)
    {
      boolean oldVal = val.getVisible();

      // remember the object and it's old value
      ItemEdit ie = new ItemEdit(val, oldVal);

      // and store it
      _valuesChanged.add(ie);
    }


    /** specify is this is an operation which can be undone
     */
    public final boolean isUndoable()
    {
      return true;
    }

    /** specify is this is an operation which can be redone
     */
    public final boolean isRedoable()
    {
      return true;
    }

    /** return string describing this operation
     * @return String describing this operation
     */
    public final String toString()
    {
      return "Reformat time values";
    }

    /** take the shape away from the layer
     */
    public final void undo()
    {
      Iterator it = _valuesChanged.iterator();
      while(it.hasNext())
      {
        ItemEdit ie = (ItemEdit)it.next();
        Plottable theO = (Plottable)ie._object;
        theO.setVisible(ie._oldValue);
      }

      _theLayers.fireReformatted(null);
    }

    /** make it so!
     */
    public final void execute()
    {
      Iterator it = _valuesChanged.iterator();
      while(it.hasNext())
      {
        ItemEdit ie = (ItemEdit)it.next();
        Plottable theO = (Plottable)ie._object;
        theO.setVisible(!_hideIt);
      }

      _theLayers.fireReformatted(null);
    }

    /** embedded class to store the changes we make
     *
     */
    private final class ItemEdit
    {
      public final Object _object;
      public final boolean _oldValue;

      public ItemEdit(Object object, boolean oldValue)
      {
        _object = object;
        _oldValue = oldValue;
      }
    } // end of ItemEdit class

  } // end of Action Class

}

