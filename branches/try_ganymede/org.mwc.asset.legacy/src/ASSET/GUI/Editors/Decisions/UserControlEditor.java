package ASSET.GUI.Editors.Decisions;

import ASSET.Models.Decision.UserControl;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */


public class UserControlEditor extends MWC.GUI.Properties.Swing.SwingCustomEditor
  implements java.beans.PropertyChangeListener
{

  //////////////////////////////////////////////////////////////////////
  // GUI components
  //////////////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DemandedSliders _course;
  private DemandedSliders _speed;
  private DemandedSliders _depth;
  JCheckBox _isActive;

  //////////////////////////////////////////////////////////////////////
  // drag and drop components
  //////////////////////////////////////////////////////////////////////

  ASSET.Models.Decision.UserControl _userControl;

  public UserControlEditor()
  {
  }

  /**
   * prepare the form
   */
  public void buildGUI()
  {
    this.setLayout(new BorderLayout());
    JPanel hori = new JPanel();
    hori.setLayout(new GridLayout(0, 1));

    _course = new DemandedSliders(0, 360, (int) _userControl.getCourse(), JSlider.HORIZONTAL, "Course")
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			void newDemanded(int val)
      {
        _userControl.setCourse(val);
      }
    };
    _speed = new DemandedSliders(0, 40, (int) _userControl.getSpeed(), JSlider.HORIZONTAL, "Speed")
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			void newDemanded(int val)
      {
        _userControl.setSpeed(val);
      }
    };
    _depth = new DemandedSliders(0, 300, (int) _userControl.getDepth(), JSlider.HORIZONTAL, "Depth")
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			void newDemanded(int val)
      {
        _userControl.setDepth(val);
      }
    };

    hori.add(_course);
    hori.add(_speed);
    hori.add(_depth);

    this.add("Center", hori);

    _isActive = new JCheckBox();
    _isActive.setText("User in control");
    _isActive.setSelected(_userControl.isActive());
    _isActive.addChangeListener(new ChangeListener()
    {
      public void stateChanged(ChangeEvent e)
      {
        _userControl.setActive(_isActive.isSelected());
      }
    });

    this.add("South", _isActive);

  }

  /**
   * yes, we do support a custom editor
   *
   * @return
   */
  public boolean supportsCustomEditor()
  {
    return true;
  }

  /**
   * store the new object
   *
   * @param value
   */
  public void setObject(final Object value)
  {
    setValue(value);
  }

  /**
   * store the new value
   *
   * @param value
   */
  private void setValue(final Object value)
  {
    //
    if (value instanceof ASSET.Models.Decision.UserControl)
    {
      _userControl = (ASSET.Models.Decision.UserControl) value;

      _userControl.addListener(ASSET.Models.Decision.UserControl.UPDATED, this);

      buildGUI();
    }
  }

  /**
   * ok, the user control we watch has changed, update our current values
   *
   * @param pe the event triggering the update
   */
  public void propertyChange(final java.beans.PropertyChangeEvent pe)
  {
    final String type = pe.getPropertyName();
    if (type == ASSET.Models.Decision.UserControl.UPDATED)
    {
      _course.setCurrent((int) _userControl.getCourse());
      _speed.setCurrent((int) _userControl.getSpeed());
      _depth.setCurrent((int) _userControl.getDepth());
    }
  }

  ////////////////////////////////////////////////////
  //  embedded class to provide current/demanded sliders
  ////////////////////////////////////////////////////

  abstract private class DemandedSliders extends JPanel
  {
    /**
     * the demanded vlaue
     */
    JSlider _demanded;
    /**
     * the current value
     */
    private JSlider _current;
    /**
     * label for the demanded value
     */
    private JLabel _demLabel;
    /**
     * label for the current value
     */
    private JLabel _curLabel;

    /**
     * a formatter, to format the text label
     */
    private java.text.DecimalFormat _df = new java.text.DecimalFormat("000");

    /////////////////////////////////////////////////
    // constructor for this item
    /////////////////////////////////////////////////


    /**
     * constructor for a pair of sliders
     *
     * @param min         the slider's max value
     * @param max         the slider's min value
     * @param current     the slider's current value
     * @param orientation whether the slider is horiz or vertical
     * @param title       the title for the value being edited
     */
    public DemandedSliders(int min,
                           int max,
                           int current,
                           int orientation,
                           String title)
    {
      buildGUI(orientation, title, min, max, current);
    }

    private void buildGUI(int orientation, String title,
                          int min, int max, int current)
    {
      this.setLayout(new BorderLayout(0, 1));
      this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

      JPanel demPanel = new JPanel();
      demPanel.setLayout(new BorderLayout());
      _demLabel = new JLabel();
      _demanded = new JSlider(orientation);
      _demanded.setMinimum(min);
      _demanded.setMaximum(max);
      setDemanded(current);
      _demanded.setToolTipText("Demanded " + title);
      _demanded.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
          if (!_demanded.getValueIsAdjusting())
            demandedChanged(_demanded.getValue());
        }
      });

      demPanel.add("West", _demanded);
      demPanel.add("Center", _demLabel);

      _curLabel = new JLabel();
      _current = new JSlider(orientation);
      _current.setMinimum(min);
      _current.setMaximum(max);
      setCurrent(current);
      _current.setToolTipText("Current " + title);
      _current.setEnabled(false);

      JPanel curPanel = new JPanel();
      curPanel.setLayout(new BorderLayout());
      curPanel.add("West", _current);
      curPanel.add("Center", _curLabel);

      this.add("North", demPanel);
      this.add("Center", new JLabel(title, JLabel.CENTER));
      this.add("South", curPanel);
    }

    abstract void newDemanded(int val);

    void demandedChanged(int val)
    {
      _demLabel.setText(_df.format(val));

      // first update the GUI
      _demanded.setValue(val);

      // now update the listener
      newDemanded(val);
    }

    public void setCurrent(int val)
    {
      _current.setValue(val);
      _curLabel.setText(_df.format(val));
    }

    public void setDemanded(int val)
    {
      _demanded.setValue(val);
      _demLabel.setText(_df.format(val));
    }

  }


  public static void main(String[] args)
  {
    UserControl control = new UserControl(270, 12, 40);
    UserControlEditor uc = new UserControlEditor();
    uc.setValue(control);
    JFrame jf = new JFrame("jere");
    jf.setSize(200, 430);
    jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jf.getContentPane().setLayout(new BorderLayout());
    jf.getContentPane().add("Center", uc);
    jf.doLayout();
    jf.setVisible(true);

  }


}