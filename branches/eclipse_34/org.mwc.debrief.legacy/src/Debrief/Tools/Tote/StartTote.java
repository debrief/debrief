package Debrief.Tools.Tote;

import MWC.GUI.Tools.*;
import MWC.GUI.*;

public final class StartTote extends PlainTool
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  private final PlainChart _theChart;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  public StartTote(ToolParent theParent,
                   PlainChart theChart)
  {
    super(theParent, "Step Forward", null);
    _theChart = theChart;
  }
  

  public final void execute()
  {
    _theChart.update();
  }  
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  public final Action getData()
  {
    // return the product
    return null;
  }

}
