// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AWTSession.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: AWTSession.java,v $
// Revision 1.2  2005/12/13 09:04:20  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.1.1.2  2003/07/21 14:47:07  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:38:06+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:27:52+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:20+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:30:10+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:42+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:56+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:45:02  ianmayo
// initial import of files
//
// Revision 1.4  2000-09-27 15:39:21+01  ian_mayo
// provide duff methods
//
// Revision 1.3  2000-04-19 11:25:51+01  ian_mayo
// add closeGUI method
//
// Revision 1.2  2000-01-20 10:07:07+00  ian_mayo
// added application-wide clipboard
//
// Revision 1.1  1999-10-12 15:34:25+01  ian_mayo
// Initial revision
//
// Revision 1.3  1999-07-16 10:01:50+01  administrator
// Nearing end of phase 2
//
// Revision 1.2  1999-07-12 08:09:23+01  administrator
// Property editing added
//
// Revision 1.1  1999-07-07 11:10:19+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:09+01  sm11td
// Initial revision
//
// Revision 1.3  1999-02-04 08:02:22+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.2  1999-02-01 16:08:45+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.1  1999-02-01 14:25:06+00  sm11td
// Initial revision
//
// Revision 1.1  1999-01-31 13:32:57+00  sm11td
// Initial revision
//

package Debrief.GUI.Frames.AWT;

import java.awt.BorderLayout;
import java.awt.Panel;

import Debrief.GUI.Frames.Session;
import Debrief.GUI.Views.AWT.AWTAnalysisView;
import MWC.GUI.ToolParent;
import MWC.GenericData.WorldArea;

/** an AWT implementation of our session */
public final class AWTSession extends Session {

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient ToolParent _theParent;
  
  transient private java.awt.Panel _thePanel;
  transient private AWTAnalysisView _theView;

  private final WorldArea _initialArea = null;

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  
  
  /** create the first AWT View, set to the view in the parent*/
  public AWTSession(final ToolParent theParent,
										final java.awt.datatransfer.Clipboard theClipboard){
    super(theClipboard);

    _theParent = theParent;
    
    initialiseForm(theParent);
  }
  
  
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  public final void initialiseForm(final ToolParent theParent){
    _theParent = theParent;
    _thePanel = new Panel();
    _thePanel.setLayout(new BorderLayout());
    _thePanel.setSize(200,200);
    _thePanel.setName(getName());
    
    _theView = new AWTAnalysisView(_theParent, this);
    
    // set the view in the parent
    addView(_theView);
    
    _thePanel.add("Center", _theView.getPanel());
    
    _thePanel.doLayout();

    if(_initialArea != null){      
      // restore the data area    
      _theView.getChart().getCanvas().getProjection().setDataArea(_initialArea);
      _theView.getChart().getCanvas().getProjection().zoom(0.0);
    }
    
  }
  
	public final void closeGUI()
	{
	}	
  
  /** @return the Panel we are using for this session
   */
  public final Panel getPanel(){
    return _thePanel;
  }
  
  /** repaint the current view
   */
  public final void repaint(){
    _thePanel.repaint();
     super.getCurrentView().update();
  }

  /** @param theName is the string used to name 
   * this session
   */
  protected final void setName(final String theName)
  {
    super.setName(theName);

    // now give the panel the same name
    _thePanel.setName(getName());
  }
  
//  private void writeObject(java.io.ObjectOutputStream out)     throws IOException{
//    out.defaultWriteObject();
//
//    // also write out the projection details
//    WorldArea wa = _theView.getChart().getCanvas().getProjection().getDataArea();
//
//    out.writeObject(wa);
//  }
//
//  private void readObject(java.io.ObjectInputStream in)
//    throws IOException, ClassNotFoundException{
//
//    in.defaultReadObject();
//
//    // retrieve the previous size of the data
//    _initialArea = (WorldArea)in.readObject();
//  }

  protected final boolean wantsToClose()
  {
    // try to do a file save - ask the user
    return true;
  }
  
}
