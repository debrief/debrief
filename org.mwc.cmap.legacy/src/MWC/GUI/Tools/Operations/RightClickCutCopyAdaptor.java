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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlottableSelection;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.Chart.RightClickEdit;
import MWC.GUI.Undo.UndoBuffer;

public class RightClickCutCopyAdaptor implements
    RightClickEdit.PlottableMenuCreator
{
  ///////////////////////////////////
  // member variables
  //////////////////////////////////
  Clipboard _clipboard;
  UndoBuffer _theBuffer;

  ///////////////////////////////////
  // constructor
  //////////////////////////////////
  public RightClickCutCopyAdaptor(final Clipboard clipboard,
      final UndoBuffer theBuffer)
  {
    _clipboard = clipboard;
    _theBuffer = theBuffer;
  }

  ///////////////////////////////////
  // member functions
  //////////////////////////////////
  public void closeMe()
  {
    _theBuffer.close();
    _theBuffer = null;
  }

  /**
   * marker interface for objects that have to reconnect to their child objects after copy/paste
   * operation. An example of this is FixWrapper
   * 
   * @author ian
   *
   */
  public static interface IsTransientForChildren
  {
    public void reconnectChildObjects(Object clonedObject);
  }

  ///////////////////////////////////
  // nested classes
  //////////////////////////////////
  public void createMenu(final javax.swing.JPopupMenu menu, final Editable data,
      final java.awt.Point thePoint,
      final MWC.GUI.Properties.PropertiesPanel thePanel, final Layer theParent,
      final Layers theLayers, final Layer updateLayer)
  {
    CutItem cutter = null;
    CopyItem copier = null;

    // just check is trying to operate on the layers object itself
    if (data instanceof MWC.GUI.Layers)
    {
      // do nothing, we can't copy the layers itself
    }
    else
    {

      // is this a layer
      if (theParent instanceof MWC.GUI.Layers)
      {
        // create the Actions
        cutter = new CutLayer(data, _clipboard, theParent, theLayers,
            updateLayer, _theBuffer);
      }
      else if (theParent == null)
      {
        // create the Actions
        cutter = new CutLayer(data, _clipboard, (Layer) data, theLayers,
            updateLayer, _theBuffer);
      }
      else
      {

        // create the Actions
        cutter = new CutItem(data, _clipboard, theParent, theLayers,
            updateLayer, _theBuffer);
        // create the Actions
        copier = new CopyItem(data, _clipboard, theParent, theLayers,
            updateLayer, _theBuffer);

      }
      // create the menu items

      // add to the menu
      menu.addSeparator();
      menu.add(cutter);

      // try the copier
      if (copier != null)
      {
        menu.add(copier);
      }
    }

  }

  //////////////////////////////////////////////
  //
  /////////////////////////////////////////////////
  public static class CutItem extends javax.swing.JMenuItem implements Action,
      ActionListener, ClipboardOwner
  {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected final Editable _data;
    protected final Clipboard _myClipboard;
    protected final Layer _theParent;
    protected Transferable _oldData;
    protected final Layers _theLayers;
    protected final Layer _updateLayer;
    protected final UndoBuffer _buffer;

    public CutItem(final Editable data, final Clipboard clipboard,
        final Layer theParent, final Layers theLayers, final Layer updateLayer,
        final UndoBuffer buffer)
    {
      // remember parameters
      _buffer = buffer;
      _data = data;
      _myClipboard = clipboard;
      _theParent = theParent;
      _theLayers = theLayers;
      _updateLayer = updateLayer;

      // formatting
      super.setText(toString());

      // and process event
      this.addActionListener(this);
    }

    public boolean isUndoable()
    {
      return true;
    }

    public boolean isRedoable()
    {
      return true;
    }

    public String toString()
    {
      return "Cut " + _data.getName();
    }

    public void undo()
    {
      restoreOldData();

      // is the parent the data object itself?
      if (_theParent == _data)
      {
        _theLayers.addThisLayer((Layer) _data);
      }
      else
      {
        // put the data item back into it's layer
        _theParent.add(_data);
      }

      doUpdate();
    }

    public void execute()
    {
      //
      storeOld();

      // copy in the new data
      final PlottableSelection ps = new PlottableSelection(_data, false);
      _myClipboard.setContents(ps, this);

      // is the parent the data object itself?
      if (_theParent == _data)
      {
        // no, it must be the top layers object
        _theLayers.removeThisLayer((Layer) _data);
      }
      else
      {
        // remove the new data from it's parent
        _theParent.removeElement(_data);
      }

      // fire updates
      doUpdate();

      // and put ourselves on the
      doUndoBuffer();
    }

    protected void doUndoBuffer()
    {
      _buffer.add(this);
    }

    protected void storeOld()
    {
      // get the old data
      _oldData = _myClipboard.getContents(this);
    }

    protected void doUpdate()
    {
      //
      _theLayers.fireExtended();

    }

    protected void restoreOldData()
    {
      // put the old data item back on the clipboard
      _myClipboard.setContents(_oldData, this);
    }

    public void actionPerformed(final ActionEvent p1)
    {
      // do it
      execute();
    }

    public void lostOwnership(final Clipboard p1, final Transferable p2)
    {
      // don't bother
    }
  }

  //////////////////////////////////////////////
  //
  /////////////////////////////////////////////////
  public static class CopyItem extends CutItem
  {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public CopyItem(final Editable data, final Clipboard clipboard,
        final Layer theParent, final Layers theLayers, final Layer updateLayer,
        final UndoBuffer buffer)
    {
      super(data, clipboard, theParent, theLayers, updateLayer, buffer);

      super.setText("Copy " + data.getName());
    }

    public String toString()
    {
      return "Copy " + _data.getName();
    }

    public void undo()
    {
      // put the old data item back on the clipboard
      _myClipboard.setContents(_oldData, this);

      // trigger the refresh
      doUpdate();
    }

    public void execute()
    {

      // store the old data
      storeOld();

      // we stick a pointer to the ACTUAL item on the clipboard - we
      // clone this item when we do a PASTE, so that multiple paste
      // operations can be performed

      // put a wrapper around the item
      final PlottableSelection ps = new PlottableSelection(_data, true);
      _myClipboard.setContents(ps, this);
    }
  }

  //////////////////////////////////////////////
  // override cutItem class, to allow removing a layer
  /////////////////////////////////////////////////
  public static class CutLayer extends CutItem
  {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public CutLayer(final Editable data, final Clipboard clipboard,
        final Layer theParent, final Layers theLayers, final Layer updateLayer,
        final UndoBuffer buffer)
    {
      super(data, clipboard, theParent, theLayers, updateLayer, buffer);
    }

    public String toString()
    {
      return "Cut Layer:" + _data.getName();
    }

    public void undo()
    {
      // put the old data item back on the clipboard
      _myClipboard.setContents(_oldData, this);

      // put the data item back into it's layer
      _theLayers.addThisLayer(_theParent);

      _theLayers.fireExtended();
    }

    public void execute()
    {
      // get the old data
      _oldData = _myClipboard.getContents(this);

      // copy in the new data
      final PlottableSelection ps = new PlottableSelection(_data, false);
      _myClipboard.setContents(ps, this);

      // remove the new data from it's parent
      _theLayers.removeThisLayer(_theParent);

      // and update the chart
      _theLayers.fireModified(null);
    }

  }

}
