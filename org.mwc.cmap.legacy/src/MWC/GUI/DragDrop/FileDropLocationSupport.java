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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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
  public void setFileDropListener(final FileDropLocationListener locationListener, final String suffixes) {
    _suffixes = suffixes.toUpperCase();
    _locationListener = locationListener;
  }

  /** remove the class listener for file drop events
   *
   */
  public void removeFileDropListener(final FileDropLocationListener locationListener)
  {
    if(_locationListener == locationListener)
      _locationListener = null;
  }


  protected void fileReceived(final java.util.Vector<File> theFiles, final java.awt.Point thePoint)
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
