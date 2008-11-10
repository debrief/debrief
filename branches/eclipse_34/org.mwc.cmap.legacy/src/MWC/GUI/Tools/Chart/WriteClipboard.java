package MWC.GUI.Tools.Chart;


import MWC.GUI.Tools.*;
import MWC.GUI.*;
import MWC.GUI.Canvas.*;
import MWC.GUI.Canvas.Clip.WindowsClipboard;

public class WriteClipboard extends PlainTool
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  /** keep a reference to the chart which we are acting upon*/
  private PlainChart _theChart;  
  private Layers _theData;
  
  /////////////////////////////////////////////////////////
  // constructor
  /////////////////////////////////////////////////////////
  
  /** constructor, stores information ready for when the button
   * finally gets pressed
   * @param theApp the parent application, so we can set cursors
   * @param theChart the chart we are to resize
   */
  public WriteClipboard(ToolParent theParent,PlainChart theChart,Layers theData){      
    super(theParent, "Write Clipboard","images/write_wmf.gif");
    // remember the chart we are acting upon
    _theChart = theChart;
    _theData = theData;
  }
  
  /////////////////////////////////////////////////////////
  // member functions
  /////////////////////////////////////////////////////////  
  public Action getData()
  {
    // don't bother, since we can do it in our execute method    
    return null;
  }

  public void execute(){
    
    // start busy
    setBusy(true);
    
    try{
    
    // create our output metafile
    WindowsClipboard wc = new WindowsClipboard();
    
    // take a copy of the screen size
    java.awt.Dimension oldDim  = _theChart.getCanvas().getProjection().getScreenArea();
    
    // insert a new screen size
    java.awt.Dimension newDim = new java.awt.Dimension(400, 400);
    _theChart.getCanvas().getProjection().setScreenArea(newDim);
    
    // start drawing
    wc.startDraw(null);

    // and set it the new projection in the object
    wc.setProjection(_theChart.getCanvas().getProjection());
    
    
    // sort out the background colour
    wc.setBackgroundColor(java.awt.Color.white);
    
    // ask the canvas to paint the image
    MWC.GUI.Canvas.Swing.SwingCanvas sc = (MWC.GUI.Canvas.Swing.SwingCanvas)_theChart.getCanvas();
    sc.paintIt(wc);

    // replace the screen size
    _theChart.getCanvas().getProjection().setScreenArea(oldDim);
    
    // and finish
    wc.endDraw(null);
    
    }
    catch(java.lang.NoClassDefFoundError e)
    {
      MWC.GUI.Dialogs.DialogFactory.showMessage("Write Operation", 
                                                "Sorry, Windows utilities not found on this installation");
      MWC.Utilities.Errors.Trace.trace(e, "Sorry, Microsoft classes not enabled on this installation");
    }
    catch(java.lang.Exception e)
    {
      MWC.Utilities.Errors.Trace.trace(e);
    }

    // end busy
    setBusy(false);
  }
  

	/** provide method to close (remove all references, help garbage collector)
	 */
	public void close()
	{
		// clear parent
		super.close();

		// now local members
		_theChart = null;
		_theData = null;
	}
  
	protected java.io.Serializable cloneThis(java.io.Serializable item)
	{
		java.io.Serializable res = null;
		try{
			java.io.ByteArrayOutputStream bas = new java.io.ByteArrayOutputStream();
			java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bas);
			oos.writeObject(item);
			// get closure
			oos.close();
			bas.close();
		
			// now get the item
			byte[] bt  = bas.toByteArray();
		
			// and read it back in as a new item
			java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(bt);
		
			// create the reader
			java.io.ObjectInputStream iis = new java.io.ObjectInputStream(bis);
		
			// and read it in
			Object oj = iis.readObject();
		
			// get more closure
			bis.close();
			iis.close();
      
      // get return parameter
      res = (java.io.Serializable) oj;
		
		}
		catch(Exception e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}
		return res;
	}
  
  
}
