package MWC.Utilities.ReaderWriter.XML.Features;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.awt.Color;
import java.awt.Font;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import MWC.GUI.Chart.Painters.Grid4WPainter;
import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.FontHandler;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;


abstract public class Grid4WHandler extends MWCXMLReader implements LayerHandler.exporter
{
  private static final String VISIBLE = "Visible";
  private static final String PLOT_LABELS = "PlotLabels";
  private static final String PLOT_LINES = "PlotLines";
  private static final String XDELTA = "xDelta";
  private static final String YDELTA = "yDelta";
  private static final String XMIN = "xMin";
  private static final String XMAX = "xMax";
  private static final String YMIN = "yMin";
  private static final String YMAX = "yMax";
  private static final String ORIGIN = "Origin";
  private static final String ORIENTATION = "Orientation";
  private static final String MY_TYPE = "Grid4W";
	private static final String NAME = "Name";
	private static final String FILL_GRID = "FillGrid";
	private static final String FILL_COLOR = "FillColor";

  java.awt.Color _theColor;
  boolean _isVisible;
  boolean _plotLabels;
  boolean _plotLines;
	protected String _myName = null;
	double _xDelta;
	double _yDelta;
	String _xMin="A";
	String _xMax="D";
	int _yMin=1;
	int _yMax=4;
	double _orientation=0;
	WorldLocation _origin;
	Font _font = null;
	Color _fillColor= null;
	boolean _fillGrid = false;
	

  public Grid4WHandler()
  {
    this(MY_TYPE);
  }

  public Grid4WHandler(String theType)
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
    addAttributeHandler(new HandleDoubleAttribute(XDELTA)
    {
      public void setValue(String name, double value)
      {
        _xDelta = value;
      }
    });
    addAttributeHandler(new HandleDoubleAttribute(YDELTA)
    {
      public void setValue(String name, double value)
      {
        _yDelta = value;
      }
    });
    addAttributeHandler(new HandleDoubleAttribute(ORIENTATION)
    {
      public void setValue(String name, double value)
      {
        _orientation = value;
      }
    });
    addHandler(new LocationHandler(ORIGIN)
    {
      public void setLocation(WorldLocation value)
      {
        _origin = value;
      }
    });
    addAttributeHandler(new HandleAttribute(XMIN)
    {
      public void setValue(String name, String val)
      {
        _xMin  = val;
      }
    });
    addAttributeHandler(new HandleAttribute(XMAX)
    {
      public void setValue(String name, String val)
      {
        _xMax  = val;
      }
    });
    addAttributeHandler(new HandleIntegerAttribute(YMIN)
    {
      public void setValue(String name, int val)
      {
        _yMin  = val;
      }
    });
    addAttributeHandler(new HandleIntegerAttribute(YMAX)
    {
      public void setValue(String name, int val)
      {
        _yMax  = val;
      }
    });
    
    
    
    addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(String name, String val)
      {
        _myName  = val;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(FILL_GRID)
    {
      public void setValue(String name, boolean val)
      {
        _fillGrid = val;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(PLOT_LINES)
    {
      public void setValue(String name, boolean val)
      {
        _plotLines = val;
      }
    });

    addHandler(new ColourHandler()
    {
      public void setColour(java.awt.Color color)
      {
        _theColor = color;
      }
    });
    addHandler(new FontHandler(){

			@Override
			public void setFont(Font res)
			{
				_font = res;
			}});
    addHandler(new ColourHandler(FILL_COLOR)
    {
      public void setColour(java.awt.Color color)
      {
        _fillColor = color;
      }
    });

  }

  public void elementClosed()
  {
    // create a Grid from this data
    MWC.GUI.Chart.Painters.Grid4WPainter csp = new Grid4WPainter(_origin);
    csp.setColor(_theColor);
    csp.setVisible(_isVisible);
    csp.setPlotLabels(_plotLabels);
   	csp.setName(_myName);
    
   	// set the other bits
   	csp.setXMin(_xMin);
   	csp.setXMax(_xMax);
   	csp.setYMin(_yMin);
   	csp.setYMax(_yMax);
   	csp.setOrientation(_orientation);
   	csp.setXDelta(new WorldDistance(_xDelta, WorldDistance.NM));
   	csp.setYDelta(new WorldDistance(_yDelta, WorldDistance.NM));
   	csp.setFillGrid(_fillGrid);
   	csp.setPlotLines(_plotLines);
   	if(_fillColor != null)
   		csp.setFillColor(_fillColor);
   	
   	if(_font != null)
   		csp.setFont(_font);
    	
    addPlottable(csp);

    // reset our variables
    _font = null;
    _origin = null;
    _fillColor= null;
    _theColor = null;
    _plotLabels = false;
    _isVisible = false;
    _myName = null;
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

    MWC.GUI.Chart.Painters.Grid4WPainter theGrid = (MWC.GUI.Chart.Painters.Grid4WPainter) plottable;
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
  protected static void exportGridAttributes(Element gridElement, MWC.GUI.Chart.Painters.Grid4WPainter theGrid,
                                             Document doc)
  {
    // do the visibility
    gridElement.setAttribute(VISIBLE, writeThis(theGrid.getVisible()));
    gridElement.setAttribute(XDELTA, writeThis(theGrid.getXDelta().getValueIn(WorldDistance.NM)));
    gridElement.setAttribute(YDELTA, writeThis(theGrid.getYDelta().getValueIn(WorldDistance.NM)));
    gridElement.setAttribute(XMIN, theGrid.getXMin());
    gridElement.setAttribute(XMAX, theGrid.getXMax());
    gridElement.setAttribute(YMIN, writeThis(theGrid.getYMin().intValue()));
    gridElement.setAttribute(YMAX, writeThis(theGrid.getYMax().intValue()));
    gridElement.setAttribute(ORIENTATION, writeThis(theGrid.getOrientation()));
    gridElement.setAttribute(PLOT_LABELS, writeThis(theGrid.getPlotLabels()));
    gridElement.setAttribute(PLOT_LINES, writeThis(theGrid.getPlotLines()));
    gridElement.setAttribute(FILL_GRID, writeThis(theGrid.getFillGrid()));
    
    // does it have a none-standard name?
    if(theGrid.getName() != GridPainter.GRID_TYPE_NAME)
    {
    	gridElement.setAttribute(NAME, theGrid.getName());
    }

    // do the colour
    ColourHandler.exportColour(theGrid.getColor(), gridElement, doc);
    ColourHandler.exportColour(theGrid.getFillColor(), gridElement, doc, FILL_COLOR);
    LocationHandler.exportLocation(theGrid.getOrigin(), ORIGIN, gridElement, doc);
    FontHandler.exportFont(theGrid.getFont(), gridElement, doc);
  }


}