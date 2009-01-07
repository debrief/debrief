package ASSET.Util.XML.Control.Observers;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.Observers.TrackPlotObserver;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract class TrackPlotObserverHandler extends CoreFileObserverHandler
{

  private final static String type = "TrackPlotObserver";

  private final static String PIC_WIDTH = "image_width";
  private final static String PIC_HEIGHT = "image_height";
  private final static String SHOW_POS = "show_positions";
  private final static String FINAL_POS = "only_final_positions";
  private final static String SHOW_SCALE = "show_scale";
  private static final String GRID_DELTA = "GridDelta";

  int _myWid = 400;
  int _myHeight = 400;
  WorldDistance _gridDelta;
  boolean _showPositions = true;
  boolean _showScale = true;
  boolean _finalPositions = false;


  public TrackPlotObserverHandler()
  {
    super(type);


    // end of the core attributes - get on with our own ones
    addAttributeHandler(new HandleIntegerAttribute(PIC_WIDTH)
    {
      public void setValue(final String name, final int value)
      {
        _myWid = value;
      }
    });
    addAttributeHandler(new HandleIntegerAttribute(PIC_HEIGHT)
    {
      public void setValue(final String name, final int value)
      {
        _myHeight = value;
      }
    });
    addHandler(new WorldDistanceHandler(GRID_DELTA)
    {
      public void setWorldDistance(final WorldDistance res)
      {
        _gridDelta = res;
      }
    });


    addAttributeHandler(new HandleBooleanAttribute(FINAL_POS)
    {
      public void setValue(final String name, final boolean val)
      {
        _finalPositions = val;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(SHOW_POS)
    {
      public void setValue(final String name, final boolean val)
      {
        _showPositions = val;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(SHOW_SCALE)
    {
      public void setValue(final String name, final boolean val)
      {
        _showScale = val;
      }
    });
  }

  public void elementClosed()
  {
    // create ourselves
    final TrackPlotObserver timeO = new TrackPlotObserver(_directory,
                                                          _myWid,
                                                          _myHeight,
                                                          _fileName,
                                                          _gridDelta,
                                                          _showPositions,
                                                          _showScale,
                                                          _finalPositions,
                                                          _name, _isActive);

    setObserver(timeO);

    super.elementClosed();

    // and reset
    _gridDelta = null;

    // and the other default values
    _myWid = 400;
    _myHeight = 400;
    _showPositions = true;
    _showScale = true;
    _finalPositions = false;
  }


  abstract public void setObserver(ScenarioObserver obs);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final TrackPlotObserver bb = (TrackPlotObserver) toExport;

    // output the parent ttributes
    CoreFileObserverHandler.exportThis(bb, thisPart);

    // output it's attributes
    thisPart.setAttribute(SHOW_SCALE, writeThis(bb.getShowScale()));
    thisPart.setAttribute(SHOW_POS, writeThis(bb.getShowPositions()));
    thisPart.setAttribute(PIC_WIDTH, writeThis(bb.getWidth()));
    thisPart.setAttribute(PIC_WIDTH, writeThis(bb.getWidth()));
    thisPart.setAttribute(FINAL_POS, writeThis(bb.getShowFinalPositions()));

    WorldDistanceHandler.exportDistance(GRID_DELTA, bb.getGridDelta(), thisPart, doc);

    // output it's attributes
    parent.appendChild(thisPart);

    throw new RuntimeException("exporting Track Plot observer not yet implemented!");


  }


}