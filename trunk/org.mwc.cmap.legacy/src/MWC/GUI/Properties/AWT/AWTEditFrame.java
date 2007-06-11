package MWC.GUI.Properties.AWT;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AWTEditFrame.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: AWTEditFrame.java,v $
// Revision 1.2  2004/05/25 15:29:22  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:25  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:45+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:38+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:32+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:44+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:43+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:24  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:36:46+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:06:03+01  administrator
// Initial revision
//

import java.awt.*;
import java.awt.event.*;
import MWC.GUI.Properties.*;
import java.beans.*;

/////////////////////////////////////////////////////
// frame to pop up, and allow editing
///////////////////////////////////////////////////
    
public class AWTEditFrame extends Dialog implements ActionListener
{
  protected PropertyEditor _pe;
  public AWTEditFrame(Frame parent, PropertyEditor pe)
  {
    super(parent);
    _pe = pe;
    initForm();
    setBackground(SystemColor.control);
    Dimension di = Toolkit.getDefaultToolkit().getScreenSize();
    Rectangle dme = getBounds();
    setLocation( (di.width - dme.width)/2,
                 (di.height - dme.height)/2);
    this.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent e)
      {
        dispose();
      }
      });
        
  }
  public void initForm()
  {
    setLayout(new BorderLayout());
    Component cp = _pe.getCustomEditor();
    add("Center", cp);
    Button fin = new Button("Done");
    fin.addActionListener(this);
    add("South", fin);
    Dimension sz = cp.getPreferredSize();
    setSize(sz.width + 50,
            sz.height + fin.getPreferredSize().height + 50);
  }
      
  public void actionPerformed(ActionEvent e)
  {
    dispose();
  }
}
