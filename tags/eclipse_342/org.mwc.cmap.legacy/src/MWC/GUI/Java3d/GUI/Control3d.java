/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Apr 16, 2002
 * Time: 3:03:59 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package MWC.GUI.Java3d.GUI;

import MWC.GUI.Java3d.WatchableTransformGroup;
import MWC.GUI.Java3d.DoubleProjection;
import MWC.GUI.Properties.Swing.SwingBoundedIntegerEditor;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GUI.Tools.Swing.RepeaterButton;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.media.j3d.Transform3D;
import javax.vecmath.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/** Control3D is a component which allows the display and editing of the current view on the 3d World
 * The view components are updated to reflect mouse-driven view changes.
 */

public class Control3d extends JPanel implements WatchableTransformGroup.TransformListener{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	///////////////////////////
  // member variables
  ///////////////////////////
  /** the transform we are monitoring
   *
   */
  WatchableTransformGroup _myTransform = null;

  /** the label used to display the course
   *
   */
  SteppingEditor _courseStepEditor = null;

  /** the label used to display the depth
   *
   */
  SteppingEditor _depthStepEditor = null;

  /** the label used to display the zoom
   *
   */
  SwingBoundedIntegerEditor _zoomEditor = null;

  /** the pan editor
   *
   */
  SwingBoundedIntegerEditor _panEditor = null;

  /** whether we are already processing a value change
   *
   */
  boolean _adjusting = false;

  /** the projection object (used to convert depth)
   *
   */
  protected DoubleProjection _theProjection = null;

  ///////////////////////////
  // constructor
  ///////////////////////////
  public Control3d() {
    initForm();
  }


  ///////////////////////////
  // member methods
  ///////////////////////////

  public void setTransform(WatchableTransformGroup tg, DoubleProjection theProjection)
  {
    // store the view
    _myTransform = tg;

    // listen to the view
    _myTransform.addListener(this);

    // store the projection
    _theProjection = theProjection;

  }

  protected void initForm()
  {


    JPanel dpthHolder = new JPanel();
    dpthHolder.setLayout(new BorderLayout());
    JLabel dpthLbl = new JLabel("Depth (m)", JLabel.RIGHT);
    dpthHolder.add("Center", dpthLbl);
    _depthStepEditor = new SteppingEditor(0, 10, 0, 0, false)
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void newValue(int val)
      {
        diveTo(val);
      }
    };
    dpthHolder.add("East",_depthStepEditor);

    // course
    JPanel crseHolder = new JPanel();
    crseHolder.setLayout(new BorderLayout());
    crseHolder.add("Center", new JLabel("Direction (\u00b0)", JLabel.RIGHT));
    _courseStepEditor = new SteppingEditor(0, 5, 0, 360, true)
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void newValue(int val)
      {
        turnTo(val);
      }
    };
    crseHolder.add("East", _courseStepEditor);

    // zoom
    JPanel zoomHolder = new JPanel();
    zoomHolder.setLayout(new BorderLayout());
    zoomHolder.add("Center", new JLabel("Zoom", JLabel.RIGHT));
    _zoomEditor = new SwingBoundedIntegerEditor(false,new Dimension(80, 20))
    {
      public void stateChanged(ChangeEvent p1)
      {
        super.stateChanged(p1);
        BoundedInteger bt = (BoundedInteger)_zoomEditor.getValue();
        if(!_adjusting)
        {
          zoomTo(bt.getCurrent());
        }
      }
    };
    zoomHolder.add("East",_zoomEditor.getCustomEditor());

    _adjusting = true;
    _zoomEditor.setValue(new BoundedInteger(150, 0, 250));
    _zoomEditor.setTicks(50, 150);
    _adjusting = false;

    // pan
    JPanel panHolder = new JPanel();
    panHolder.setLayout(new BorderLayout());
    panHolder.add("Center", new JLabel("Pan", JLabel.RIGHT));
    _panEditor = new SwingBoundedIntegerEditor(false, new Dimension(80, 60))
    {
      public void stateChanged(ChangeEvent p1)
      {
        super.stateChanged(p1);
        BoundedInteger bt = (BoundedInteger)_panEditor.getValue();
        if(!_adjusting)
        {
          panTo(bt.getCurrent());
        }
      }
    };
    panHolder.add("East",_panEditor.getCustomEditor());
    _adjusting = true;
    _panEditor.setValue(new BoundedInteger(0, -100, 100));
    _panEditor.setTicks(25, 100);
    _adjusting = false;


    // layout the controls
    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

    JPanel p1 = new JPanel();
    p1.setLayout(new GridLayout(0,1));
    JPanel p2 = new JPanel();
    p2.setLayout(new GridLayout(0,1));

    p1.add(dpthHolder);
    p1.add(crseHolder);

    p2.add(panHolder);
    p2.add(zoomHolder);

    add(p1);
    add(new javax.swing.JSeparator(JSeparator.VERTICAL));
    add(p2);

//    add(dpthHolder);
//    add(panHolder);
//    add(crseHolder);
//    add(zoomHolder);
  }

  /** process a new demanded depth
   *
   */
  protected void diveTo(double val)
  {

    // invert the depth value, to give height
    val = -val;

    // convert to screen depth
    double depth = val / _theProjection.getDepthScale();

    Transform3D transformX = new Transform3D();
    Transform3D currXform = new Transform3D();

    // get the current transform
    _myTransform.getTransform(currXform);

    // get the current depth
    Vector3d currentTrans = new Vector3d();
    currXform.get(currentTrans);
    double curDepth = currentTrans.y;

    // calculate the delta
    double depthDelta = depth - curDepth;

    // produce a new translation
    Vector3d newTrans = new Vector3d();

    newTrans.y = depthDelta;

    transformX.set(newTrans);

    currXform.mul(transformX, currXform);

    _adjusting = true;
    _myTransform.setTransform(currXform);
    _adjusting = false;

  }

  /** process a new demanded zoom
   *
   */
  protected void zoomTo(double val)
  {

    // invert the zoom factor
    val = -val;

    Transform3D transformX = new Transform3D();
    Transform3D currXform = new Transform3D();

    // get the current transform
    _myTransform.getTransform(currXform);

    // get the current depth
    Vector3d currentTrans = new Vector3d();
    currXform.get(currentTrans);
    double curDepth = currentTrans.z;

    // calculate the delta
    double depthDelta = val - curDepth;

    // produce a new translation
    Vector3d newTrans = new Vector3d();

    newTrans.z = depthDelta;

    transformX.set(newTrans);

    currXform.mul(transformX, currXform);

    _adjusting = true;
    _myTransform.setTransform(currXform);
    _adjusting = false;

  }

  /** process a new demanded pan
   *
   */
  protected void panTo(double val)
  {
    // invert the pan
    val = -val;

    Transform3D transformX = new Transform3D();
    Transform3D currXform = new Transform3D();

    // get the current transform
    _myTransform.getTransform(currXform);

    // get the current depth
    Vector3d currentTrans = new Vector3d();
    currXform.get(currentTrans);
    double curDepth = currentTrans.x;

    // calculate the delta
    double depthDelta = val - curDepth;

    // produce a new translation
    Vector3d newTrans = new Vector3d();

    newTrans.x = depthDelta;

    transformX.set(newTrans);

    currXform.mul(transformX, currXform);

    _adjusting = true;
    _myTransform.setTransform(currXform);
    _adjusting = false;

  }


  /** process a new demanded heading
   *
   */
  protected void turnTo(double val)
  {
    Transform3D transformY = new Transform3D();
    Transform3D currXform = new Transform3D();

    // get the current transform
    _myTransform.getTransform(currXform);

    /** handle special case where requesting direction of 90 degs causes the transform to go unstable, oooh!
     *
     */
    if(val == 90)
    {
      val = 89.999;
    }
    if(val == 270)
    {
      val = 269.999;
    }

    // a rotation about x it is then
    double y_angle = Math.toRadians(val);

    // find the current rotation
    Vector3d euler =  getEuler(currXform);
    double cur_y = euler.y;
    cur_y = Math.toRadians(cur_y);

    // calculate the change in rotation
    double y_delta = y_angle - cur_y;

    // a rotation about x it is then
    transformY.rotY(y_delta);

    Matrix4d mat = new Matrix4d();

    // Remember old matrix
    currXform.get(mat);

    // Translate to origin
    currXform.setTranslation(new Vector3d(0.0,0.0,0.0));

    currXform.mul(transformY, currXform);

    // Set old translation back
    Vector3d translation = new	Vector3d(mat.m03, mat.m13, mat.m23);
    currXform.setTranslation(translation);

    // prevent mad loop
    _adjusting = true;

    // Update xform
    _myTransform.setTransform(currXform);

    _adjusting = false;
  }

  /** form is closing - clear everything out
   *
   */
  public void doClose()
  {
    _myTransform.removeListener(this);
    _myTransform = null;
  }

  /** a new transform has been produced in the parent, handle it
   *
   */
  public void newTransform(Transform3D trans) {


    // break down the transform into 3 angles
    Vector3d euler = getEuler(trans);

    // get the heading as an integer
    int y_int = (int)euler.y;

    Vector3f slation = new Vector3f();
    trans.get(slation);

    double depth = slation.y;

    // convert depth to world coordinates
    depth *= _theProjection.getDepthScale();

    // invert the depth, to give height
    depth = -depth;

    // determine the zoom
    int zoom = -(int)slation.z;

    // determine the current pan
    int pan = -(int)slation.x;

    // set adjusting
    if(!_adjusting)
    {
      _adjusting = true;
      _courseStepEditor.setCurrent(y_int);
      _depthStepEditor.setCurrent((int)depth);
      _zoomEditor.setCurrent(zoom);
      _panEditor.setCurrent(pan);
      _adjusting = false;
    }
  }


  protected Vector3d getEuler(Transform3D transform)
  {
    Vector3d res = new Vector3d();

    double[] mat = new double[16];

    transform.get(mat);

    double angle_x, angle_y, angle_z;
    angle_y =  Math.asin( mat[2]);        /* Calculate Y-axis angle */

    double C           =  Math.cos( angle_y );


    if ( Math.abs( C ) > 0.005 )             /* Gimball lock? */
    {
      double trx      =  mat[10] / C;           /* No, so get X-axis angle */
      double trY      = -mat[6]  / C;

      angle_x  = Math.atan2( trY, trx );
      angle_x = Math.toDegrees(angle_x);

      trx      =  mat[0] / C;            /* Get Z-axis angle */
      trY      = -mat[1] / C;

      angle_z  = Math.atan2( trY, trx );
      angle_z = Math.toDegrees(angle_z);
    }
    else                                 /* Gimball lock has occurred */
    {
      angle_x  = 0;                      /* Set X-axis angle to zero */

      double trx      =  mat[5];                 /* And calculate Z-axis angle */
      double trY      =  mat[4];

      angle_z  = Math.atan2( trY, trx );
      angle_z = Math.toDegrees(angle_z);
    }

    // handle our "special case" where rotation directions do not show north/south
    if(mat[0] < 0)
      angle_y = Math.PI - angle_y;

    //
    angle_y = Math.toDegrees(angle_y); // convert to degs

    /* return only positive angles in [0,360] */
    if (angle_x < 0) angle_x += 360;
    if (angle_y < 0) angle_y += 360;
    if (angle_z < 0) angle_z += 360;

    res.x = angle_x;
    res.y = angle_y;
    res.z = angle_z;

    return res;
  }

  ///////////////////////////
  // editor
  ///////////////////////////
  abstract private static class SteppingEditor extends JPanel
  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	///////////////////////////
    // member variables
    ///////////////////////////
    /** the label which shows the data
     *
     */
    protected JLabel _myLabel = null;

    /** the step size
     *
     */
    private int _delta;

    /** the min value
     *
     */
    private int _min;

    /** the max value
     *
     */
    private int _max;

    /** whether we cycle through the values
     *
     */
    private boolean _cycle;

    /** the current value
     *
     */
    private int _current;

    /** the text formatter
     *
     */
    static final private java.text.DecimalFormat _textFormatter = new java.text.DecimalFormat(" 000;-000");

    ///////////////////////////
    // constructor
    ///////////////////////////
    /**
     *
     * @param current the current value in the stepper
     * @param delta the step size to use
     * @param min the min value to display (or zero to not constrain values)
     * @param max the max value to display (or zero to not constrain values)
     * @param cycle whether to cycle around the values
     */
    public SteppingEditor(int current, int delta, int min, int max, boolean cycle)
    {
      _current = current;
      _delta = delta;
      _min = min;
      _max = max;
      _cycle = cycle;

      initForm();

      resetData();
    }

    ///////////////////////////
    // member methods
    ///////////////////////////


    /** doDecide whether to constrain values displayed (they're not
     * constrained if both min and max are set to zero).
     *
     * @return
     */
    public boolean constrainValues()
    {
      boolean res = true;

      if((_min == 0)&&(_max==0))
      {
        res = false;
      }

      return res;
    }

    /** allow the current value to be set
     *
     */
    public void setCurrent(int newVal)
    {

      if(constrainValues())
      {
        // are we allowed to cycle?
        if(_cycle)
        {
          // are we going past the max value?
          if(newVal > _max)
          {
            newVal = _min + (newVal - _max);
          }
          // are we going past the max value?
          if(newVal < _min)
          {
            newVal = _max - (newVal - _min);
          }
        }
        else
        {
          // just trim it to the limits
          newVal = Math.min(newVal, _max);
          newVal = Math.max(newVal, _min);
        }
      }

      _current = newVal;

      // set the display
      resetData();

    }

    void doStep(boolean up)
    {
      int thisStep = _delta;

      // is this up or down?
      if(!up)
      {
        thisStep = -thisStep;
      }

      // calculate the new value
      int newVal = _current + thisStep;

      // and update the current value
      setCurrent(newVal);

      // inform the listener
      newValue(_current);

    }

    /** build the form
     *
     */
    public void initForm()
    {
      // set our layout
      this.setLayout(new FlowLayout());
      _myLabel = new JLabel(" ", JLabel.RIGHT);
      _myLabel.setForeground(Color.black);
      Font labelFont = new Font("Monospaced", Font.BOLD,(int)(_myLabel.getFont().getSize() * 1.6));
      _myLabel.setFont(labelFont);

      //
    //  this.set

      JButton downBtn = new RepeaterButton("-", "images/zoomout.gif");
  //    downBtn.setMargin(new Insets(0,0,0,0));
      downBtn.setToolTipText("decrease this value");
      downBtn.addActionListener(new ActionListener()
      {
        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e)
        {
          doStep(false);
        }
      });
      JButton upBtn = new RepeaterButton("+", "images/zoomin.gif");
      upBtn.setToolTipText("increase this value");
   //   upBtn.setMargin(new Insets(0,0,0,0));
      upBtn.addActionListener(new ActionListener()
      {
        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e)
        {
          doStep(true);
        }
      });

      // insert the components
      add(downBtn);
      add(_myLabel);
      add(upBtn);

    }

    /** reset the data values
     *
     */
    public void resetData()
    {
      _myLabel.setText(_textFormatter.format(_current));
    }

    /** the value has changed!
     *
     */
    abstract public void newValue(int val);
  }
}
