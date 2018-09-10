/**
 * 
 */
package org.mwc.debrief.core.wizards.dynshapes;

import java.awt.Color;
import java.text.ParseException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.Wizard;
import org.mwc.cmap.core.CorePlugin;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.DynamicShapeWrapper;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * @author Ayesha
 *
 */
public class DynamicPolygonWizard extends Wizard
{

  private DynamicShapeTimingsPage _shapeTimingsPage;
  private DynamicPolygonBoundsPage _boundsPage;
  private DynamicShapeStylingPage _stylingPage;
  private Date _startDate;

  private Layers _layers;
  private DynamicShapeWrapper _dynamicShape;
  public DynamicPolygonWizard(Layers theLayers,Date startDate)
  {
    _layers = theLayers;
    _startDate = startDate;
  }
  @Override
  public void addPages()
  {
    _shapeTimingsPage = new DynamicShapeTimingsPage("Timings","Polygon",_startDate);
    _boundsPage = new DynamicPolygonBoundsPage("Bounds");
    _stylingPage = new DynamicShapeStylingPage("Styling", "Polygon");
    addPage(_shapeTimingsPage);
    addPage(_boundsPage);
    addPage(_stylingPage);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  @Override
  public boolean performFinish()
  {
    Date startTime = _shapeTimingsPage.getStartTime();
    
    PolygonShape polygon = createPolygonShape(_boundsPage.getCoordinates());
    
    final Color theColor = ImportReplay.replayColorFor(_stylingPage.getSymbology());
    _dynamicShape = new DynamicShapeWrapper(_stylingPage.getShapeLabel(),polygon,theColor,new HiResDate(startTime),"rectangle");
    return true;
  }
  
  private PolygonShape createPolygonShape(String text) {
    Vector<PolygonNode> coordinates = new Vector<PolygonNode>();
    final PolygonShape  polygon = new PolygonShape(coordinates);
  
    StringTokenizer st = new StringTokenizer(text);
  
    while (st.hasMoreTokens())
    {
      // meet the label
      final String sts = st.nextToken();
      double latDeg;
      double latMin;
      double latSec;
      char latHem,longHem;
      double longDeg;
      double longMin,longSec;
      if (Character.isDigit(sts.charAt(0)))
      {
        try
        {
          // now the location
          latDeg = MWCXMLReader.readThisDouble(sts);
          latMin = MWCXMLReader.readThisDouble(st.nextToken());
          latSec = MWCXMLReader.readThisDouble(st.nextToken());

          /**
           * now, we may have trouble here, since there may not be a space
           * between the hemisphere character and a 3-digit latitude value - so
           * BE CAREFUL
           */
          final String vDiff = st.nextToken();
          if (vDiff.length() > 3)
          {
            // hmm, they are combined
            latHem = vDiff.charAt(0);
            final String secondPart = vDiff.substring(1, vDiff.length());
            longDeg = MWCXMLReader.readThisDouble(secondPart);
          }
          else
          {
            // they are separate, so only the hem is in this one
            latHem = vDiff.charAt(0);
            longDeg = MWCXMLReader.readThisDouble(st.nextToken());
          }
          longMin = MWCXMLReader.readThisDouble(st.nextToken());
          longSec = MWCXMLReader.readThisDouble(st.nextToken());
          longHem = st.nextToken().charAt(0);

          // we have our first location, create it
          final WorldLocation theLoc = new WorldLocation(latDeg, latMin,
              latSec, latHem, longDeg, longMin, longSec, longHem, 0);
          final PolygonNode newNode = new PolygonNode("1",
              theLoc, polygon);
          polygon.add(newNode);
        }catch(ParseException pe) {
          CorePlugin.logError(Status.ERROR, "Parse exception creating polygonShape", pe);
          pe.printStackTrace();
        }
      }
    }
    return polygon;
  }
  
  public DynamicShapeWrapper getDynamicShapeWrapper()
  {
    return _dynamicShape;
  }
}
