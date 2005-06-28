/**
 * 
 */
package org.mwc.debrief.core.ChartFeatures;

import org.mwc.cmap.plotViewer.actions.CoreEditorAction;

import Debrief.Tools.Palette.CreateShape;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.*;
import MWC.GUI.Tools.Action;
import MWC.GenericData.*;

/**
 * @author ian.mayo
 *
 */
abstract public class CoreInsertShape extends CoreEditorAction
{
	public static ToolParent _theParent = null;

	/** ok, store who the parent is for the operation
	 * 
	 * @param theParent
	 */
	public static void init(ToolParent theParent)
	{
		_theParent = theParent;
	}
	
	
	/** and execute..
	 * 
	 *
	 */
	protected void run()
	{
		final PlainChart theChart = getChart();

		Action res = getData(theChart);
		res.execute();
			
	}
	

  public final Action getData(PlainChart theChart)
  {
    Action res = null;
    WorldArea wa = theChart.getDataArea();

    // see if we have an area defined
    if(wa != null)
    {

      // get centre of area (at zero depth)
      WorldLocation centre = wa.getCentreAtSurface();

      ShapeWrapper theWrapper = getShape(centre);
      Layers theData = theChart.getLayers();

      Layer theLayer = theData.findLayer("Misc");
      if(theLayer == null)
      {
        theLayer = new BaseLayer();
        theLayer.setName("Misc");
        theData.addThisLayer(theLayer);
      }

       res =  new CreateShape.CreateShapeAction(null,
                                     theLayer,
                                     theWrapper,
                                     theChart.getLayers());
    }
    else
    {
      // we haven't got an area, inform the user
      MWC.GUI.Dialogs.DialogFactory.showMessage("Create Feature",
    "Sorry, we can't create a shape until the area is defined.  Try adding a coastline first");
    }

    return res;
  }


	abstract protected ShapeWrapper getShape(WorldLocation centre);
}