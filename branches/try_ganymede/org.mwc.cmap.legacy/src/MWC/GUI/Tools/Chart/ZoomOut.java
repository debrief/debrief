package MWC.GUI.Tools.Chart;


import MWC.GUI.Tools.*;
import MWC.GUI.*;
import MWC.GenericData.*;

public class ZoomOut extends PlainTool
{
  
  /** my copy of the chart, so that I can tell it to update
   */
  protected PlainChart _theChart;
  
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  public ZoomOut(ToolParent theParent, 
                 PlainChart theChart){      
    super(theParent, "Zoom Out", "images/zoomout.gif");
    // remember the chart we are acting upon
    _theChart = theChart;
  }

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  public Action getData()
  {
    // get the current data area
    WorldArea oldArea = _theChart.getCanvas().getProjection().getDataArea();
    return new ZoomOutAction(_theChart, oldArea, 2.0);
  }


  ///////////////////////////////////////////////////////
  // store action information
  ///////////////////////////////////////////////////////
  public static class ZoomOutAction implements Action{
    private PlainChart _theChart;  
    private WorldArea _oldArea;
    private double _zoomFactor;

    public ZoomOutAction(PlainChart theChart,
                          WorldArea oldArea,
                         double zoomFactor){
      _theChart = theChart;
      _oldArea = oldArea;
      _zoomFactor = zoomFactor;
    }

    public boolean isRedoable(){
      return true;
    }
    
    
    public boolean isUndoable(){
      return true;
    }
                 
    public String toString(){
      return "Zoom out";
    }                                        
    
    public void undo()      
    {
      // set the data area for the chart to the old area
      _theChart.getCanvas().getProjection().setDataArea(_oldArea);
      
      // get the projection to refit-itself
      _theChart.getCanvas().getProjection().zoom(0.0);
      
      // and redraw the whole plot
      _theChart.update();
    }
    
    public void execute(){
      // set the new zoom factor (which will trigger a resize operation
      _theChart.getCanvas().getProjection().zoom(_zoomFactor);
      
      // and trigger a redraw
      _theChart.update();
    }
  }
  
  
}
