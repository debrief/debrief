/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Oct 28, 2001
 * Time: 6:30:11 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package MWC.GUI.DragDrop;

import java.io.File;

public class FileDropLocationSupport extends FileDropSupport
{
  /***************************************************************
   *  member variables
   ***************************************************************/

  protected FileDropLocationListener _locationListener;



  /** add a class a listener for file drop events
   * @param locationListener the listener, which will be told of receive events
   * @param suffixes the file suffix to accept (including the dot)
   */
  public void setFileDropListener(FileDropLocationListener locationListener, String suffixes) {
    _suffixes = suffixes.toUpperCase();
    _locationListener = locationListener;
  }

  /** remove the class listener for file drop events
   *
   */
  public void removeFileDropListener(FileDropLocationListener locationListener)
  {
    if(_locationListener == locationListener)
      _locationListener = null;
  }


  protected void fileReceived(java.util.Vector<File> theFiles, java.awt.Point thePoint)
  {
    if(_locationListener != null)
      _locationListener.FilesReceived(theFiles, thePoint);
  }

  /***************************************************************
   *  member interface
   ***************************************************************/
  static public interface FileDropLocationListener
  {
    /** process this list of file
     * @param files the list of files
     * @param point the location in the component where the files were dropped
     */
    public void FilesReceived(java.util.Vector<File> files, java.awt.Point point);
  }


}
