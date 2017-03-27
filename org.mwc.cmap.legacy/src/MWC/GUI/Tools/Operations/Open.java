/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.GUI.Tools.Operations;

// Copyright MWC 1999
// $RCSfile: Open.java,v $
// $Author: Ian.Mayo $
// $Log: Open.java,v $
// Revision 1.2  2004/05/25 15:44:05  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:44  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:01+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:05+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:37+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:42:53+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:42+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:43  ianmayo
// initial version
//
// Revision 1.9  2000-09-21 12:22:06+01  ian_mayo
// allow children to access _lastDirectory
//
// Revision 1.8  2000-08-07 14:06:11+01  ian_mayo
// remove d-lines
//
// Revision 1.7  2000-08-07 12:21:36+01  ian_mayo
// tidy icon filename
//
// Revision 1.6  2000-03-14 09:54:52+00  ian_mayo
// use icons for these tools
//
// Revision 1.5  2000-02-02 14:23:55+00  ian_mayo
// Workarounds to allow use of original Swing fileChooser, because of problems experienced when using IBM jre (also so that both types of dialog [open/save] return File objects rather  than just pathnames)
//
// Revision 1.4  1999-11-25 16:23:11+00  ian_mayo
// Handle user cancelling save operation
//


import java.io.File;

import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;


abstract public class Open extends PlainTool
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
	/** the type of file to open
	 */
  String [] _theSuffixs;
	
	/** the last directory we opened from
	 */
	protected String _lastDirectory;
	
	/** the type of file we are importing
	 */
	String _theDescription;
  
  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  /** open an existing data file
   * @param theParent parent application, where we can show the busy cursor
   * @param theLabel the label to put on the button
   * @param theSuffixs file suffix array for type of files we are importing
   * @param theDescription textual description of file type
   */
  public Open(final ToolParent theParent,
              final String theLabel,
              final String  [] theSuffixs,
							final String theDescription){
    super(theParent, theLabel, "images/open.gif");
    
    _theSuffixs = theSuffixs;
		_lastDirectory = "";
		_theDescription = theDescription;
  }
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  
  protected class myFilter implements java.io.FilenameFilter{
    
    public boolean accept(final File p1, final String p2)
    {
      	
    	for (String string : _theSuffixs) {
    		if(p2.endsWith(string))
    			return true;
		}
      return false;
    }
    
  }
  
  /** get a filename from the user
   */
  private java.io.File[] getFileName(){  
	StringBuilder bSufix = new StringBuilder();
	for (int i = 0; i < _theSuffixs.length; i++) {
		if(i!=0)
		{
			bSufix.append(",");
		}
		bSufix.append(_theSuffixs[i]);
		
	}
	
    return MWC.GUI.Dialogs.DialogFactory.getOpenFileName(bSufix.toString(),
																												 _theDescription,
																												 _lastDirectory);
  }                             
  
  /** collate the data ready to perform the operations
   */
  public Action getData()
  {
    
    Action res = null;
    
    // get the filename of the file to import
    final File[] fList = getFileName();
		
		// check the user didn't press cancel		
		if(fList != null)
		{
			// get the first parameter				
			final File fn = fList[0];
    
			// check user didn't press cancel
			if(fn != null)
			{

				// remember the directory of the file
				_lastDirectory = fn.getParent();
				
				if(fn.getName().equals("nullnull"))
				{
				  res = null;
				}
				else
				{    
				  // data is collated, now create 'action' function
				  res = doOpen(fn.getPath());
				}
			}
		}		
    // return the product
    return res;
  }
  
  abstract public Action doOpen(String filename);
}
