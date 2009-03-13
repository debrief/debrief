// Copyright MWC 1999
// $RCSfile: SwingFile.java,v $
// $Author: Ian.Mayo $
// $Log: SwingFile.java,v $
// Revision 1.2  2004/05/25 15:23:35  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:17  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:16  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:40+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:02+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:05+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-08-21 15:17:29+01  administrator
// Allow developer to supply list of suffixes
//
// Revision 1.0  2001-07-17 08:46:20+01  administrator
// Initial revision
//
// Revision 1.2  2001-07-04 20:04:55+01  novatech
// switch to using new, super-clever file saver
//
// Revision 1.1  2001-01-03 13:42:52+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:01  ianmayo
// initial version
//
// Revision 1.8  2000-08-30 14:45:38+01  ian_mayo
// switched back to Java file chooser, multiple select seems to be fixed
//
// Revision 1.7  2000-08-08 12:38:14+01  ian_mayo
// only tell the fileChooser that we have a "last Directory" if we really have one, silly
//
// Revision 1.6  2000-04-19 11:40:41+01  ian_mayo
// minor tidying up
//
// Revision 1.5  2000-04-03 10:54:54+01  ian_mayo
// put in the file filter
//
// Revision 1.4  2000-03-07 10:12:34+00  ian_mayo
// tidying up
//
// Revision 1.3  2000-02-02 14:23:54+00  ian_mayo
// Workarounds to allow use of original Swing fileChooser, because of problems experienced when using IBM jre (also so that both types of dialog [open/save] return File objects rather  than just pathnames)
//
// Revision 1.2  1999-11-25 13:33:30+00  ian_mayo
// changed, to support returning multiple file names, and to allow different GUI implemetnations
//
// Revision 1.1  1999-11-16 17:48:54+00  ian_mayo
// Initial revision
//
// Revision 1.1  1999-10-12 15:36:52+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:43+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-07 11:10:05+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:58+01  sm11td
// Initial revision
//
// Revision 1.1  1999-02-01 14:25:07+00  sm11td
// Initial revision
//

package MWC.GUI.Dialogs.Swing;

import java.io.*;
import javax.swing.*;
//import javax.swing.filechooser.*;

/** AWT implementation of getting a file
 */
public class SwingFile implements MWC.GUI.Dialogs.DialogFactory.FileGetter
{

  private static EFileChooser _myChooser = null;

	public File[] getExistingFile(String filter,
																String description,
																String lastDirectory)
	{

    if(_myChooser == null);
      _myChooser = new EFileChooser();

	  JFileChooser jf = _myChooser;

		// allow multiple selections
		jf.setMultiSelectionEnabled(true);

		if(filter != null)
			jf.setFileFilter(new TextFilter(description, filter));

    // check that we know of an existing last directory, and that it isn't zero length
		if(lastDirectory != null)
      if(lastDirectory.length() > 0)
			  jf.setCurrentDirectory(new File(lastDirectory));

		File[] res = null;

    // try to set as modal

		int state = jf.showOpenDialog(null);

		// see how many files were selected
		File[] files = jf.getSelectedFiles();

		File theFile = jf.getSelectedFile();

		if(state == JFileChooser.APPROVE_OPTION)
		{
			if(files != null)
			{
				if(files.length > 0)
				{
					res = files;
				}
				else
				{
					res = new File[]{theFile};
				}
			}
		}
		else if(state == JFileChooser.CANCEL_OPTION)
		{
			res = null;
		}

    // have a go at storing the new entries
    EFileChooser.saveDirectoryEntries();

		return res;
  }

  public java.io.File getNewFile(String filter,
																String description,
																String lastDirectory){
    if(_myChooser == null)
      _myChooser = new EFileChooser();

	  JFileChooser jf = _myChooser;
		java.io.File res = null;

		if(filter != null){
			jf.setFileFilter(new TextFilter(description, filter));
			jf.setSelectedFile(new File(filter));
		}

		if(lastDirectory != null)
			jf.setCurrentDirectory(new File(lastDirectory));



		int state = jf.showSaveDialog(null);
		java.io.File fl = jf.getSelectedFile();

		if(fl != null &&
			 state == JFileChooser.APPROVE_OPTION)
		{
			res = fl;
		}
		else if(state == JFileChooser.CANCEL_OPTION)
		{
			res = null;
		}

    // have a go at storing the new entries
    EFileChooser.saveDirectoryEntries();

		return res;
  }


	protected class TextFilter extends javax.swing.filechooser.FileFilter
	{
		protected String _myName;
    /** string containing the suffixes we accept - they are comma-separated
     *
     */
		protected String _myType;

    /** constructor
     *  @param myName the name of this file type
     *  @param myType a comma-separated list of suffixes we accept
     */
		public TextFilter(String myName,
											String myType)
		{
			_myName = myName;
			_myType =  myType.toUpperCase();
		}

		public boolean accept(java.io.File p1)
		{
			boolean accept = p1.isDirectory();

			if(!accept)
			{
				String suffix = getSuffix(p1.getPath());
				if(suffix != null)
				{
          // does our list of suffixes contain this characters?
          int index = _myType.indexOf(suffix.toUpperCase());

          // find out if it was found
          if(index != -1)
            accept =  true;
          else
            accept =  false;
				}
			}
			return accept;
		}


		private String getSuffix(String s)
		{
			String suffix = null;
			int i = s.lastIndexOf(".");
			if(i>0 && i < s.length() - 1)
			{
				suffix = s.substring(i+1).toLowerCase();
			}
			return suffix;
		}

		public String getDescription()
		{
			return _myName;
		}
	}

}
