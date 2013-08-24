package MWC.GUI.Tools.Chart;


import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;
import MWC.GenericData.WorldArea;

public class ZoomOut extends PlainTool
{
  
  /** my copy of the chart, so that I can tell it to update
   */
  protected PlainChart _theChart;
  
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  public ZoomOut(final ToolParent theParent, 
                 final PlainChart theChart){      
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
    final WorldArea oldArea = _theChart.getCanvas().getProjection().getDataArea();
    return new ZoomOutAction(_theChart, oldArea, 2.0);
  }


  ///////////////////////////////////////////////////////
  // store action information
  ///////////////////////////////////////////////////////
  public static class ZoomOutAction implements Action{
    private final PlainChart _theChart;  
    private final WorldArea _oldArea;
    private final double _zoomFactor;

    public ZoomOutAction(final PlainChart theChart,
                          final WorldArea oldArea,
                         final double zoomFactor){
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
      // no, don't bother - the chart catches the data-area change
   //   _theChart.update();
    }
  }
  
  
}
