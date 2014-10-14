
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

package MWC.GUI.DragDrop;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/** class proving support for drag and drop of files
 * from Windows to the indicated Java (AWT or JFC) component
 * @author Ian Mayo
 * @version 1
 */
public class FileDropSupport implements DropTargetListener
{


  ///////////////////////////////////////////////
  // nested interfaces
  /////////////////////////////////////////////////
  /**
   * @param files  */	/** interface to be supported by classes which want to listen out for files being dropped
   */
  static public interface FileDropListener
  {
    /** process this list of file
     * @param files the list of files
     */
    public void FilesReceived(java.util.Vector<File> files);
  }

  //////////////////////////////////////////
  // member variables
  //////////////////////////////////////////
  protected HashMap<Component, DropTarget> targets;
  protected String _suffixes;
  protected FileDropListener _listener;


  //////////////////////////////////////////
  // constructor
  //////////////////////////////////////////


  public void addComponent(final java.awt.Component comp)
  {
    // set ourselves as a drag and drop listener for this component
    final DropTarget dropTarget = new DropTarget(comp, DnDConstants.ACTION_COPY, this);

    if(targets == null)
      targets = new HashMap<Component, DropTarget>();

    targets.put(comp, dropTarget);

  }

  //////////////////////////////////////////
  // member functions
  //////////////////////////////////////////

  /** remove the indicated component
   *
   */
  public void removeComponent(final java.awt.Component comp)
  {
    // get this target
    final DropTarget thisOne = (DropTarget) targets.get(comp);

    // remove this listener
    if(thisOne != null)
    {
      thisOne.removeDropTargetListener(this);
    }

    // and remove the target itself
    targets.remove(comp);
  }

  /** add a class a listener for file drop events
   * @param listener the listener, which will be told of receive events
   * @param suffixes the file suffix to accept (including the dot)
   */
  public void setFileDropListener(final FileDropListener listener, final String suffixes) {
    _suffixes = suffixes.toUpperCase();
    _listener = listener;
  }

  /** remove the class listener for file drop events
   *
   */
  
  public void removeFileDropListener(final FileDropListener listener)
  {
    if(_listener == listener)
      _listener = null;
  }

  private void checkThis(final java.awt.dnd.DropTargetDragEvent p1)
  {
    if(p1.isDataFlavorSupported(DataFlavor.stringFlavor))
    {
      p1.acceptDrag(java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE);
    }
  }

  public void dragOver(final java.awt.dnd.DropTargetDragEvent p1) {
    checkThis(p1);
  }

  public void dropActionChanged(final java.awt.dnd.DropTargetDragEvent p1) {
    checkThis(p1);
  }

  public void dragEnter(final java.awt.dnd.DropTargetDragEvent p1) {
    checkThis(p1);
  }

  protected void fileReceived(final java.util.Vector<File> theFiles, final java.awt.Point thePoint)
  {
    if(_listener != null)
      _listener.FilesReceived(theFiles);
  }

  @SuppressWarnings("unchecked")
	public void drop(final java.awt.dnd.DropTargetDropEvent p1) {
    // fire this drop event to all targets
    final Transferable tra = p1.getTransferable();

    final Vector<File> res = new Vector<File>(0,1);

    // find out if string data is being dropped
    if(tra.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
    {
      p1.acceptDrop(DnDConstants.ACTION_COPY);
      try
      {
        final List<java.io.File> lt = (List<java.io.File>)tra.getTransferData(DataFlavor.javaFileListFlavor);
        final Iterator<java.io.File> enumer=lt.iterator();
        while(enumer.hasNext())
        {
          final java.io.File file = (java.io.File)enumer.next();
          if(isValid(file.getName()))
          {
            res.addElement(file);
          }
        }

        // tell the dropper that the operation is complete, to save the "Explorer"
        // window hanging when we take a while to process the drop
        p1.dropComplete(true);

        // check we have processed files
        if(res.size() > 0)
        {
          fileReceived(res, p1.getLocation());
        }

      }
      catch(final Exception e)
      {
        e.printStackTrace();
      }
    }
    else if(tra.isDataFlavorSupported(DataFlavor.stringFlavor))
    {
      p1.acceptDrop(DnDConstants.ACTION_COPY);

      try
      {
        final String lt = (String)tra.getTransferData(DataFlavor.stringFlavor);

        final java.io.File file = new java.io.File(lt);
        if(isValid(file.getName()))
        {
          res.addElement(file);
        }

        // tell the dropper that the operation is complete, to save the "Explorer"
        // window hanging when we take a while to process the drop
        p1.dropComplete(true);

        // check we have processed files
        if(res.size() > 0)
        {
          fileReceived(res, p1.getLocation());
        }

      }
      catch (final UnsupportedFlavorException e)
      {
        System.out.println("NOT SUPPORTED");
      }
      catch (final IOException e)
      {
      }
    }
    else
    {
      p1.rejectDrop();
    }

  }

  private boolean isValid(final String name)
  {
    // check the suffix
    String theSuffix=null;
    final int pos = name.lastIndexOf(".");
    theSuffix = name.substring(pos, name.length()).toUpperCase();

    // check if this matches
    final int index = _suffixes.indexOf(theSuffix);
    if(index != -1)
      return true;
    else
      return false;
  }

  public void dragExit(final java.awt.dnd.DropTargetEvent p1) {
  }


}