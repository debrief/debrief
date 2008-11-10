package MWC.Utilities.ReaderWriter.XML.Features;

import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GUI.Chart.Painters.LocalGridPainter;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 19-Oct-2004
 * Time: 09:34:43
 * To change this template use File | Settings | File Templates.
 */
abstract public class LocalGridHandler extends GridHandler
{
  private final static String ORIGIN = "Origin";
  private final static String MY_TYPE = "LocalGrid";
  private static final String PLOT_ORIGIN = "PlotOrigin";

  private WorldLocation _myOrigin = null;
  private boolean _plotOrigin = true;

  public LocalGridHandler()
  {
    super(MY_TYPE);

    addAttributeHandler(new HandleBooleanAttribute(PLOT_ORIGIN)
    {
      public void setValue(String name, boolean value)
      {
        _plotOrigin = value;
      }
    });

    addHandler(new LocationHandler(ORIGIN)
    {
      public void setLocation(WorldLocation res)
      {
        //To change body of implemented methods use File | Settings | File Templates.
        _myOrigin = res;
      }
    });

  }

  /**
   * get the grid object itself (we supply this method so that it can be overwritten, by the LocalGrid painter for example
   *
   * @return
   */
  protected GridPainter getGrid()
  {
    LocalGridPainter local = new LocalGridPainter();
    local.setOrigin(_myOrigin);
    local.setPlotOrigin(_plotOrigin);
    _myOrigin = null;
    _plotOrigin = true;
    return local;
  }

  /**
   * export this grid
   *
   * @param plottable the grid we're going to export
   * @param parent
   * @param doc
   */
  public void exportThisPlottable(MWC.GUI.Plottable plottable, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {

    MWC.GUI.Chart.Painters.LocalGridPainter theGrid = (MWC.GUI.Chart.Painters.LocalGridPainter) plottable;
    Element gridElement = doc.createElement(MY_TYPE);

    // get the parent to export itself
    exportGridAttributes(gridElement, theGrid, doc);

    // and the location
    LocationHandler.exportLocation(theGrid.getOrigin(), ORIGIN, gridElement, doc);
    
    // and whether to plot the origin
    gridElement.setAttribute(PLOT_ORIGIN, writeThis(theGrid.getPlotOrigin()));


    // done.
    parent.appendChild(gridElement);
  }

}
