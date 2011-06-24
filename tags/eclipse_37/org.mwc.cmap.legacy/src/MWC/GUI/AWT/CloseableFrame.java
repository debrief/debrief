package MWC.GUI.AWT;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowEvent;

public class CloseableFrame extends Frame
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;




	public CloseableFrame(String theStr){
    super(theStr);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setBackground(Color.lightGray);
  }
  
  
  

  protected void processWindowEvent(WindowEvent p1)
  {
    super.processWindowEvent(p1);
    if(p1.getID() == WindowEvent.WINDOW_CLOSING){
      // close the frame
      this.dispose();
    }
  }
}
