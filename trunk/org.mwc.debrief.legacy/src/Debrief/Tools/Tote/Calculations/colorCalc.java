package Debrief.Tools.Tote.Calculations;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: colorCalc.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: colorCalc.java,v $
// Revision 1.4  2006/03/16 16:01:07  Ian.Mayo
// Override not-applicable statement
//
// Revision 1.3  2004/11/25 10:24:37  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.2  2003/07/28 08:03:36  Ian.Mayo
// Defer creation of our colour property editor until it's needed.
// This is so that our default processing can initialise the editors
// static list of colours (including the custom editor).
//
// Revision 1.1.1.2  2003/07/21 14:49:13  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.1  2003-07-03 15:02:21+01  ian_mayo
// Initial revision
//
// Revision 1.5  2003-03-19 15:37:19+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.4  2003-02-10 16:27:50+00  ian_mayo
// Reflect name change of get wrappable data
//
// Revision 1.3  2003-02-07 15:36:12+00  ian_mayo
// Add accessor flag to indicate is this calculation needs special processing (where data crosses through zero)
//
// Revision 1.2  2002-05-28 09:25:12+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:43+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:31+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:11+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:24+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:49:04  ianmayo
// initial import of files
//
// Revision 1.1  2000-09-14 10:25:01+01  ian_mayo
// Initial revision
//
// Revision 1.3  2000-05-19 11:23:44+01  ian_mayo
// provided n/a result string when secondary watchable not present
//
// Revision 1.2  2000-04-03 10:18:17+01  ian_mayo
// shorten units label
//
// Revision 1.1  1999-10-12 15:34:18+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 09:47:51+01  administrator
// Initial revision
//

import java.text.*;
import Debrief.Tools.Tote.*;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;

public final class colorCalc extends plainCalc
{


  /** colour editor, used to convert a colour to a name
   *
   */
  private static MWC.GUI.Properties.ColorPropertyEditor colEditor = null;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public colorCalc()
  {  
    super(new DecimalFormat("000.0"), "Color", "name");
  }
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  public final double calculate(Watchable primary, Watchable secondary, HiResDate thisTime)
  {
    // get the colour
    java.awt.Color theCol = secondary.getColor();
    return theCol.getRGB();
  }


  public final String update(Watchable primary, Watchable secondary, HiResDate time)
  {
		// check we have data
		if(secondary == null)
			return NOT_APPLICABLE;

    // check we have our colour editor
    if(colEditor == null)
    {
      colEditor = new MWC.GUI.Properties.ColorPropertyEditor();
    }

    // get the colour
    java.awt.Color theCol = secondary.getColor();

    colEditor.setValue(theCol);

    return colEditor.getAsText() ;
  }

  /** does this calculation require special bearing handling (prevent wrapping through 360 degs)
   *
   */
  public final boolean isWrappableData() {
    return false;
  }
}
