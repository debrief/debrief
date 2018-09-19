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

import java.util.List;
import java.util.StringTokenizer;

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
import Debrief.Wrappers.DynamicShapeWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainWrapper;
import MWC.TacticalData.NarrativeEntry;

/**
 * Generates a paste REP from clipboard action.
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class GeneratePasteRepClipboard implements RightClickContextItemGenerator
{

  /* (non-Javadoc)
   * @see org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator#generate(org.eclipse.jface.action.IMenuManager, MWC.GUI.Layers, MWC.GUI.Layer[], MWC.GUI.Editable[])
   */
  @Override
  public void generate(IMenuManager parent, Layers theLayers,
      Layer[] parentLayers, Editable[] subjects)
  {
    //see if there is nothing selected 
    if(subjects.length==0) {
      final Clipboard clip = CorePlugin.getDefault().getClipboard();
      final Object val = clip.getContents(TextTransfer.getInstance());
      if(val!=null) {
        final String clipBoardContent = (String)val;
        //See if there is plain text on the clipboard
        if(isContentImportable(clipBoardContent)) {
          parent.add(createAction(theLayers, clipBoardContent));
        }

      }
    }
  }
  
  private Action createAction(Layers theLayers, String clipboardContent) {
    final Action doPasteAction = new Action("Paste REP")
    {
      @Override
      public void run()
      {
        PasteRepOperation operation = new PasteRepOperation("Paste from clipboard",theLayers, clipboardContent );
        CorePlugin.run(operation);
      }
    };
    //doPasteAction.setImageDescriptor();
    doPasteAction.setToolTipText("Paste REP from clipboard");
    return doPasteAction;
  }


  private static class PasteRepOperation extends CMAPOperation{

    private String _contentToImport;
    private Layers _tempLayers;
    private Layers _layers;
    private List<Editable> addedElements;
    private ImportReplay _tracker;
    
    public PasteRepOperation(String title,Layers theLayers,String contentToImport)
    {
      super(title);
      _contentToImport = contentToImport;
      _layers = theLayers;
    }
    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      _tracker = new ImportReplay();
      _tempLayers = new Layers();
      //import to a temp layers object
      _tracker.setLayers(_tempLayers);
      addedElements = _tracker.importThis(_contentToImport,_layers);
      return Status.OK_STATUS;
    }
   
    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      //iterate through the addedElements and see what got added and remove them
      for(Editable element:addedElements) {
        if(element instanceof Layer) {
          _layers.removeThisLayer((Layer)element);
        }
        else {
          Layer layer = null;
          if(element instanceof DynamicShapeWrapper) {
            layer = (Layer)_layers.findLayer(((DynamicShapeWrapper)element).getTrackName());
          }
          else if (element instanceof SensorContactWrapper)
          {
            layer = (Layer)_layers.findLayer(((SensorContactWrapper)element).getTrackName());
          }
          else if (element instanceof DynamicTrackShapeWrapper)
          {
            layer = (Layer)_layers.findLayer(((DynamicTrackShapeWrapper)element).getTrackName());
          }
          else if (element instanceof TMAContactWrapper)
          {
            layer = (Layer)_layers.findLayer(((TMAContactWrapper)element).getTrackName());
          }
          else if (element instanceof NarrativeEntry)
          {
            layer = _tracker.getLayerFor(ImportReplay.NARRATIVE_LAYER);
          }
          else {
            layer= null;
          }
          if(layer!=null) {
            _layers.removeThisEditable(null, element);
          }
        }
      }
      _layers.fireExtended();
      addedElements.clear();
      return Status.OK_STATUS;
    }
  }

  private boolean isContentImportable(final String content) {

    boolean proceed=false;
    StringTokenizer tokens = new StringTokenizer(content,"\\r?\\n");
    int lineCount = 0;
    while(tokens.hasMoreTokens() && lineCount<=6) {
      String line = tokens.nextToken().trim();
      if(line.startsWith(";") && !line.startsWith(";;")) {
        StringTokenizer lineTokens = new StringTokenizer(line);
        if(lineTokens.hasMoreTokens()) {
          String firstWord = lineTokens.nextToken();
          switch(firstWord) {
            case ";CIRCLE:":
            case ";DYNAMIC_CIRCLE:":
            case ";RECT:":
            case ";DYNAMIC_RECT:":
            case ";BRG:":
            case ";DYNAMIC_POLY:":
            case ";ELLIPSE:":
            case ";FORMAT_FIX:":
            case ";FORMAT_LAYER_HIDE:":
            case ";TEXT:":
            case ";LINE:":
            case ";FORMAT_TRACK_NAME_AT_END:":
            case ";NARRATIVE:":
            case ";NARRATIVE2:":
            case ";PERIODTEXT:":
            case ";POLY:":
            case ";POLYLINE:":
            case ";SENSOR:":
            case ";SENSOR2:":
            case ";SENSOR3:":
            case ";SENSOR_ARC:":
            case ";TIMETEXT:":
            case ";TMA_POS:":
            case ";TMA_RB:":
            case";VECTOR:":
            case ";WHEEL:":
              proceed = true;break;
            default:proceed=false;break;
          }
        }
      }
      else {
        StringTokenizer lineTokens = new StringTokenizer(line);
        if(lineTokens.hasMoreTokens()) {
          String firstWord = lineTokens.nextToken();
          if(firstWord.matches("\\d{6}+")) {
            proceed=true;
          }
        }
      }
      lineCount++;
    }
    return proceed;
  }
}
