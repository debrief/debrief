// Copyright MWC 1999
// $RCSfile: AWTFile.java,v $
// $Author: Ian.Mayo $
// $Log: AWTFile.java,v $
// Revision 1.2  2004/05/25 15:23:29  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:17  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:13  Ian.Mayo
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
// Revision 1.0  2001-07-17 08:46:21+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:52+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:44:30  ianmayo
// initial version
//
// Revision 1.4  2000-02-02 14:23:55+00  ian_mayo
// Workarounds to allow use of original Swing fileChooser, because of problems experienced when using IBM jre (also so that both types of dialog [open/save] return File objects rather  than just pathnames)
//
// Revision 1.3  1999-11-25 13:32:58+00  ian_mayo
// changed to reflect returning multiple file names
//
// Revision 1.2  1999-11-18 11:08:32+00  ian_mayo
// new, to allow AWT or SWING specific behaviour
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

package MWC.GUI.Dialogs.AWT;


import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

/** AWT implementation of getting a file
 */
public class AWTFile implements MWC.GUI.Dialogs.DialogFactory.FileGetter
{
  @SuppressWarnings("deprecation")
	public File[] getExistingFile(String filter,
																	String description,
																	String lastDirectory){
    Frame frm = new Frame("dummy");
    FileDialog fd = new FileDialog(frm, "Load a file");
    fd.setFile(filter);
    fd.setMode(FileDialog.LOAD);
    fd.show();
    frm.dispose();
		
		File[] res = new File[1];
		res[0] = new File(fd.getDirectory() + fd.getFile());
		return res;
  }
  
  @SuppressWarnings("deprecation")
	public java.io.File getNewFile(String filter,
																	String description,
																	String lastDirectory){
    Frame frm = new Frame("dummy");
    FileDialog fd = new FileDialog(frm, "Load a file");
    fd.setFile(filter);
    fd.setMode(FileDialog.SAVE);
    fd.show();
    frm.dispose();
    return new File(fd.getDirectory() + fd.getFile());
  }
  
}
