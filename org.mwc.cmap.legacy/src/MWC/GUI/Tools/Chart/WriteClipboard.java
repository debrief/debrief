/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.GUI.Tools.Chart;


import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GUI.Canvas.Clip.WindowsClipboard;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;

public class WriteClipboard extends PlainTool
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  /** keep a reference to the chart which we are acting upon*/
  private PlainChart _theChart;  
  /////////////////////////////////////////////////////////
  // constructor
  /////////////////////////////////////////////////////////
  
  /** constructor, stores information ready for when the button
   * finally gets pressed
   * @param theApp the parent application, so we can set cursors
   * @param theChart the chart we are to resize
   */
  public WriteClipboard(final ToolParent theParent,final PlainChart theChart,final Layers theData){      
    super(theParent, "Write Clipboard","images/write_wmf.gif");
    // remember the chart we are acting upon
    _theChart = theChart;
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
    final WindowsClipboard wc = new WindowsClipboard();
    
    // take a copy of the screen size
    final java.awt.Dimension oldDim  = _theChart.getCanvas().getProjection().getScreenArea();
    
    // insert a new screen size
    final java.awt.Dimension newDim = new java.awt.Dimension(400, 400);
    _theChart.getCanvas().getProjection().setScreenArea(newDim);
    
    // start drawing
    wc.startDraw(null);

    // and set it the new projection in the object
    wc.setProjection(_theChart.getCanvas().getProjection());
    
    
    // sort out the background colour
    wc.setBackgroundColor(DebriefColors.WHITE);
    
    // ask the canvas to paint the image
    final MWC.GUI.Canvas.Swing.SwingCanvas sc = (MWC.GUI.Canvas.Swing.SwingCanvas)_theChart.getCanvas();
    sc.paintIt(wc);

    // replace the screen size
    _theChart.getCanvas().getProjection().setScreenArea(oldDim);
    
    // and finish
    wc.endDraw(null);
    
    }
    catch(final java.lang.NoClassDefFoundError e)
    {
      MWC.GUI.Dialogs.DialogFactory.showMessage("Write Operation", 
                                                "Sorry, Windows utilities not found on this installation");
      MWC.Utilities.Errors.Trace.trace(e, "Sorry, Microsoft classes not enabled on this installation");
    }
    catch(final java.lang.Exception e)
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
	}
  
	protected java.io.Serializable cloneThis(final java.io.Serializable item)
	{
		java.io.Serializable res = null;
		try{
			final java.io.ByteArrayOutputStream bas = new java.io.ByteArrayOutputStream();
			final java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bas);
			oos.writeObject(item);
			// get closure
			oos.close();
			bas.close();
		
			// now get the item
			final byte[] bt  = bas.toByteArray();
		
			// and read it back in as a new item
			final java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(bt);
		
			// create the reader
			final java.io.ObjectInputStream iis = new java.io.ObjectInputStream(bis);
		
			// and read it in
			final Object oj = iis.readObject();
		
			// get more closure
			bis.close();
			iis.close();
      
      // get return parameter
      res = (java.io.Serializable) oj;
		
		}
		catch(final Exception e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}
		return res;
	}
  
  
}
