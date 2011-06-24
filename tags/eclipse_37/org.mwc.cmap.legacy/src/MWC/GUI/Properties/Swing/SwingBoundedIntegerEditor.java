package MWC.GUI.Properties.Swing;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingBoundedIntegerEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: SwingBoundedIntegerEditor.java,v $
// Revision 1.2  2004/05/25 15:29:34  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:26  Ian.Mayo
// Initial import
//
// Revision 1.9  2003-01-09 16:19:07+00  ian_mayo
// Minor tidying
//
// Revision 1.8  2003-01-09 12:06:35+00  ian_mayo
// Include workaround for JDK1.3 problem where JSlider not shown.  We want JDK1.3 to work properly for 3d support under NT4
//
// Revision 1.7  2002-07-09 15:30:22+01  ian_mayo
// Minor mods
//
// Revision 1.6  2002-05-28 09:25:47+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:33+01  ian_mayo
// Initial revision
//
// Revision 1.5  2002-04-26 16:01:59+01  ian_mayo
// Provide no-op constructor
//
// Revision 1.4  2002-04-25 12:33:21+01  ian_mayo
// Allow options for showing label and setting dimensions
//
// Revision 1.3  2002-04-22 08:51:39+01  ian_mayo
// Provide accessor functions for convenience in setting tick marks and current value
//
// Revision 1.2  2002-04-15 14:03:54+01  ian_mayo
// improve layout, so more room for slider
//
// Revision 1.1  2002-04-11 14:01:25+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:31+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-17 09:41:59+00  novatech
// remove unnecessary import statements
//
// Revision 1.1  2001-01-03 13:42:38+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:35  ianmayo
// initial version
//
// Revision 1.1  2000-09-26 10:52:31+01  ian_mayo
// Initial revision
//
// Revision 1.3  2000-02-02 14:25:07+00  ian_mayo
// correct package naming
//
// Revision 1.2  1999-11-23 11:05:03+00  ian_mayo
// further introduction of SWING components
//
// Revision 1.1  1999-11-16 16:07:19+00  ian_mayo
// Initial revision
//
// Revision 1.1  1999-11-16 16:02:29+00  ian_mayo
// Initial revision
//
// Revision 1.2  1999-11-11 18:16:09+00  ian_mayo
// new class, now working
//
// Revision 1.1  1999-10-12 15:36:48+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:05:48+01  administrator
// Initial revision
//

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import MWC.GUI.Properties.BoundedInteger;

public class SwingBoundedIntegerEditor extends
    MWC.GUI.Properties.BoundedIntegerEditor implements javax.swing.event.ChangeListener
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /** slider to store the value
   */
  JSlider _theSlider;

  /** panel to hold everything
   */
  JPanel _theHolder;

  /** label to show current value
   */
  JLabel _theCurrent;

  /** our date formatter
   *
   */
  static java.text.NumberFormat _numFormat = new java.text.DecimalFormat(" 000;-000");

  /** whether to show the current value
   *
   */
  private boolean _showValue = true;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /** create a slider editor
   * @param showValue - whether to show the current value
   * @param preferredSize - the preferred size (ignored)
   */
  public SwingBoundedIntegerEditor(boolean showValue, Dimension preferredSize)
  {
    _showValue = showValue;
  }

  /** no-option constructor, as used from property editors
   *
   */
  public SwingBoundedIntegerEditor()
  {
    this(true, null);
  }
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////


  boolean _hasBeenUpdated = false;

  /** build the editor
   */
  public java.awt.Component getCustomEditor()
  {
    // create the panel
    _theHolder = new JPanel()
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void paint(Graphics g) {
        // we'e got a workaround for JDK1.3 here. It's a JDC Bug when a
        // JSlider is inserted into a JDesktop component.
        if(!_hasBeenUpdated)
        {
          _theSlider.updateUI();
          _hasBeenUpdated = true;
        }
        super.paint(g);
      }
    };

    _theHolder.setLayout(new BorderLayout());

    _theCurrent = new JLabel("00");

    if(_showValue)
      _theHolder.add("West", _theCurrent);

    // create the slider (configure it in the resetData method)
    _theSlider = new JSlider();

    _theHolder.add("Center", _theSlider);

    _theSlider.addChangeListener(this);

    resetData();

    return _theHolder;
  }

  /** put the data into the text fields, if they have been
   * created yet
   */
  public void resetData()
  {
    if(_theHolder != null)
    {
      // put the text into the fields
      if(_myVal != null)
      {
        int startVal = _myVal.getCurrent();
        _theSlider.setMinimum(_myVal.getMin());
        _theSlider.setMaximum(_myVal.getMax());
        _theSlider.setValue(startVal);

        _theCurrent.setText(_numFormat.format(_myVal.getCurrent()));
      }

      // set the default tick marks
     setTicks(5, 10);

      _theSlider.setPaintTicks(true);

      // and configure the slider
     _theSlider.putClientProperty("JSlider.isFilled", Boolean.FALSE);

    }
  }

  public void setCurrent(int val)
  {
    // this is a bit of a round-about way, we update the GUI then update the remaining
    // data from it

    // and update the GUI
    _theSlider.setValue(val);

    // and update our data
    stateChanged(null);

  }

  /** set the tick sliders
   *
   */
  public void setTicks(int minor, int major)
  {
    _theSlider.setMinorTickSpacing(minor);
    _theSlider.setMajorTickSpacing(major);
  }

  public void stateChanged(javax.swing.event.ChangeEvent p1)
  {
    _myVal.setCurrent(_theSlider.getValue());
    _theCurrent.setText(_numFormat.format(_myVal.getCurrent()));
  }

  public static void main(String[] args)
  {
    JFrame jf = new JFrame("test");
    jf.setSize(200, 200);

    SwingBoundedIntegerEditor bt = new SwingBoundedIntegerEditor(true, null);
    BoundedInteger br = new BoundedInteger(12, 0, 20);
    bt.setValue(br);

    JPanel holder = new JPanel();
    holder.setLayout(new BorderLayout());
    jf.getContentPane().add(holder);
    holder.add(bt.getCustomEditor());
    jf.setVisible(true);
  }

}
