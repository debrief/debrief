package Debrief.Tools.Tote.Calculations;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: plainCalc.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.6 $
// $Log: plainCalc.java,v $
// Revision 1.6  2006/06/16 10:54:09  Ian.Mayo
// allow getTitle to be overridden (for slant calc)
//
// Revision 1.5  2006/03/16 16:01:07  Ian.Mayo
// Override not-applicable statement
//
// Revision 1.4  2005/12/13 09:04:55  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.3  2004/11/29 15:43:38  Ian.Mayo
// Allow units to be over-ridden
//
// Revision 1.2  2004/11/25 10:24:38  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:49:15  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:37:15+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 09:25:11+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:43+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:33+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:12+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:25+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:49:04  ianmayo
// initial import of files
//
// Revision 1.1  2000-09-14 10:25:02+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-10-12 15:34:19+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:02:58+01  administrator
// Initial revision
//

import java.text.*;

import Debrief.Tools.Tote.*;
import MWC.GenericData.HiResDate;

abstract public class plainCalc implements toteCalculation
{
  protected static final String NOT_APPLICABLE = " ";
	/////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  NumberFormat _myPattern;
  private final String _myTitle;
  private String _myUnits;
  
  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public plainCalc(NumberFormat pattern,
                   String myTitle,
                   String myUnits)
  {
    _myPattern = pattern;
    _myTitle = myTitle;
    _myUnits = myUnits;
  }
  
  public plainCalc(String myTitle,
                   String myUnits)
  {
    this(new DecimalFormat("000"), myTitle, myUnits);
  }
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  abstract public String update(Watchable primary, Watchable secondary, HiResDate time);
  abstract public double calculate(Watchable primary, Watchable secondary, HiResDate thisTime);
  
  public String getTitle()
  {
    return _myTitle;
  }
  
  public final void setPattern(NumberFormat format)
  {
    _myPattern = format;
  }

  public void setUnits(String units)
  {
    this._myUnits = units;
  }

  public String getUnits()
  {
    return _myUnits;
  }
  
  public String toString()
  {
    return getTitle();
  }
}
