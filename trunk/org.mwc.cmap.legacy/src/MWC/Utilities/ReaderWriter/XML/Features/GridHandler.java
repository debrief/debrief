package MWC.Utilities.ReaderWriter.XML.Features;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.*;

import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.*;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;


abstract public class GridHandler extends MWCXMLReader implements LayerHandler.exporter
{
  private static final String VISIBLE = "Visible";
  private static final String PLOT_LABELS = "PlotLabels";
  private static final String DELTA = "Delta";
  private static final String UNITS = "Units";
  private static final String MY_TYPE = "grid";
	private static final String NAME = "Name";

  java.awt.Color _theColor;
  boolean _isVisible;
  WorldDistance _delta;
  double _deltaDegs;
  boolean _plotLabels;
  String _myUnits = null;
	protected String _myName = null;

  public GridHandler()
  {
    this(MY_TYPE);
  }

  public GridHandler(String theType)
  {
    // inform our parent what type of class we are
    super(theType);

    addAttributeHandler(new HandleBooleanAttribute(VISIBLE)
    {
      public void setValue(String name, boolean value)
      {
        _isVisible = value;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(PLOT_LABELS)
    {
      public void setValue(String name, boolean value)
      {
        _plotLabels = value;
      }
    });
    addAttributeHandler(new HandleDoubleAttribute(DELTA)
    {
      public void setValue(String name, double value)
      {
      	_deltaDegs = value;
      }
    });
    addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(String name, String val)
      {
        _myName  = val;
      }
    });
    addAttributeHandler(new HandleAttribute(UNITS)
    {
      public void setValue(String name, String value)
      {
        _myUnits = value;
      }
    });

    addHandler(new ColourHandler()
    {
      public void setColour(java.awt.Color color)
      {
        _theColor = color;
      }
    });

    addHandler(new WorldDistanceHandler(DELTA){
		
			@Override
			public void setWorldDistance(WorldDistance res)
			{
				_delta = res;
			}
		});
  }

  public void elementClosed()
  {
    // create a Grid from this data
    MWC.GUI.Chart.Painters.GridPainter csp = getGrid();
    csp.setColor(_theColor);
    csp.setVisible(_isVisible);

    // do we have a new units value?
    if(_delta != null)
    {
    	csp.setDelta(_delta);
    }
    else
    {
    	// use the old value
      // do we know our units
      if (_myUnits == null)
      // no, we don't - assume they're in NM
        csp.setDelta(new MWC.GenericData.WorldDistance(_deltaDegs, WorldDistance.NM));
      else
      {
        // yes, we do - best use them
        csp.setDelta(new MWC.GenericData.WorldDistance(_deltaDegs, WorldDistance.getUnitIndexFor(_myUnits)));
      }
    }

    csp.setPlotLabels(_plotLabels);

    // is there a name?
    if(_myName != null)
    	csp.setName(_myName);
    
    addPlottable(csp);

    // reset our variables
    _theColor = null;
    _plotLabels = false;
    _isVisible = false;
    _myUnits = null;
    _myName = null;
    _delta = null;
  }

  /**
   * get the grid object itself (we supply this method so that it can be overwritten, by the LocalGrid painter for example
   *
   * @return
   */
  protected MWC.GUI.Chart.Painters.GridPainter getGrid()
  {
    return new MWC.GUI.Chart.Painters.GridPainter();
  }

  abstract public void addPlottable(MWC.GUI.Plottable plottable);


  /**
   * export this grid
   *
   * @param plottable the grid we're going to export
   * @param parent
   * @param doc
   */
  public void exportThisPlottable(MWC.GUI.Plottable plottable, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {

    MWC.GUI.Chart.Painters.GridPainter theGrid = (MWC.GUI.Chart.Painters.GridPainter) plottable;
    Element gridElement = doc.createElement(MY_TYPE);

    exportGridAttributes(gridElement, theGrid, doc);

    parent.appendChild(gridElement);
  }

  /**
   * utility class which appends the other grid attributes
   *
   * @param gridElement the element to put the grid into
   * @param theGrid     the grid to export
   * @param doc         the document it's all going into
   */
  protected static void exportGridAttributes(Element gridElement, MWC.GUI.Chart.Painters.GridPainter theGrid,
                                             Document doc)
  {
    // do the visibility
    gridElement.setAttribute(VISIBLE, writeThis(theGrid.getVisible()));
    gridElement.setAttribute(UNITS, theGrid.getDelta().getUnitsLabel());
    gridElement.setAttribute(PLOT_LABELS, writeThis(theGrid.getPlotLabels()));
    
    // does it have a none-standard name?
    if(theGrid.getName() != GridPainter.GRID_TYPE_NAME)
    {
    	gridElement.setAttribute(NAME, theGrid.getName());
    }
    
    // and the delta (retaining the units
    WorldDistanceHandler.exportDistance(DELTA, theGrid.getDelta(), gridElement, doc);

    // do the colour
    ColourHandler.exportColour(theGrid.getColor(), gridElement, doc);
  }


}