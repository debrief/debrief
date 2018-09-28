/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.core.ContextOperations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.ReaderWriter.Replay.ImportReplay;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * Generates a paste REP from clipboard action.
 * 
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class GeneratePasteRepClipboard implements RightClickContextItemGenerator
{

  private static class PasteRepOperation extends CMAPOperation
  {

    private final String _contentToImport;
    private Layers _tempLayers;
    private final Layers _layers;

    public PasteRepOperation(final String title, final Layers theLayers,
        final String contentToImport)
    {
      super(title);
      _contentToImport = contentToImport;
      _layers = theLayers;
    }

    @Override
    public IStatus execute(final IProgressMonitor monitor,
        final IAdaptable info) throws ExecutionException
    {

      final ImportReplay tracker = new ImportReplay();
      _tempLayers = new Layers();
      // import to a temp layers object
      tracker.setLayers(_tempLayers);
      final int numLines = getNumLines(_contentToImport);
      if (numLines != -1)
      {
        tracker.importThis(_contentToImport, numLines);
        ImportReplay.injectContent(_tempLayers, _layers, true);
      }

      return Status.OK_STATUS;
    }

    private static final int getNumLines(final String text)
    {
      final String[] lines = text.split("\\r?\\n");
      if (lines != null)
      {
        return lines.length;
      }
      return -1;

    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      ImportReplay.injectContent(_tempLayers, _layers, false);
      return Status.OK_STATUS;
    }
  }

  public static Action createAction(final Layers theLayers,
      final String clipboardContent)
  {
    final Action doPasteAction = new Action("Paste REP from clipboard")
    {
      @Override
      public void run()
      {
        final PasteRepOperation operation = new PasteRepOperation(
            "Paste from clipboard", theLayers, clipboardContent);
        CorePlugin.run(operation);
      }
    };
    doPasteAction.setImageDescriptor(CorePlugin.getImageDescriptor(
        "icons/16/paste.png"));
    doPasteAction.setToolTipText("Paste REP from clipboard");
    
    return doPasteAction;
  }
  
  

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator#generate(
   * org.eclipse.jface.action.IMenuManager, MWC.GUI.Layers, MWC.GUI.Layer[], MWC.GUI.Editable[])
   */
  @Override
  public void generate(final IMenuManager parent, final Layers theLayers,
      final Layer[] parentLayers, final Editable[] subjects)
  {
    // see if there is nothing selected
    if (subjects.length == 0)
    {
      final Clipboard clip = CorePlugin.getDefault().getClipboard();
      final Object val = clip.getContents(TextTransfer.getInstance());
      if (val != null)
      {
        final String clipBoardContent = (String) val;
        // See if there is plain text on the clipboard
        if (ImportReplay.isContentImportable(clipBoardContent))
        {
          parent.add(createAction(theLayers, clipBoardContent));
        }

      }
    }
  }
}
