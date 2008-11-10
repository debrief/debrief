package MWC.GUI.AWT;

import java.awt.*;
import java.awt.event.*;

public class CloseableFrame extends Frame
{
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
