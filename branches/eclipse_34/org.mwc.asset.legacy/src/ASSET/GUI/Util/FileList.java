package ASSET.GUI.Util;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import javax.swing.*;
import java.awt.dnd.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;

public class FileList extends JPanel implements FilenameFilter , DragGestureListener, DragSourceListener
{
  private JList _myList = new JList();
  private String _myDirectory;

  private DragSource dragSource = new DragSource();
  private DragGestureRecognizer recogniser =
      dragSource.createDefaultDragGestureRecognizer(_myList, DnDConstants.ACTION_COPY_OR_MOVE, this);

  static private final String file_separator = System.getProperty("file.separator");

  final private String _mySuffix;

  public FileList(final String directory, final String title, final String suffix)
  {
    // store the directory
    _myDirectory = directory;

    if(suffix != null)
      _mySuffix = suffix;
    else
      _mySuffix = "xml";


    // set the name of the tab
    setName(title);

    // initialise the form
    initForm();

    // and refresh the list
    refreshForm();
  }

  private void initForm()
  {
    // create list object
    this.setLayout(new BorderLayout());
    this.add(_myList, BorderLayout.CENTER);
  }

  private void refreshForm()
  {
    // create the file
    final File _myDir = new File(_myDirectory);

    final String[] fList = _myDir.list(this);

    _myList.removeAll();

    final java.util.Vector vec = new java.util.Vector(0,1);

    // check we've received data
    if(fList != null)
    {

      // step through, but removing suffix
      for(int i=0;i<fList.length; i++)
      {
        String str = fList[i];
        str = str.substring(0, str.length()-4);
        vec.addElement(str);
      }

      _myList.setListData(vec);
    }
  }

  private String getCurrentItem()
  {
    return (String)_myList.getSelectedValue();
  }

  public boolean accept(File dir, final String name)
  {
    return  name.toLowerCase().endsWith(_mySuffix);
  }

  public void dragGestureRecognized(final DragGestureEvent dge)
   {
      String sel = getCurrentItem() + "." + _mySuffix;
      // append our file suffix
      sel = _myDirectory + file_separator + sel;

      dge.startDrag(DragSource.DefaultCopyDrop, new StringSelection(sel), this);
   }

  public void dragDropEnd(DragSourceDropEvent dsde)
   { }
  public void dragEnter(DragSourceDragEvent dsde)
    { }
  public void dragExit(DragSourceEvent dse)
   {  }
  public void dragOver(DragSourceDragEvent dsde)
   {  }
  public void dropActionChanged(DragSourceDragEvent dsde)
   {  }

}
