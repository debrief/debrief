package Debrief.GUI.Tote.AWT;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AWTStepControl.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: AWTStepControl.java,v $
// Revision 1.3  2004/11/26 11:37:42  Ian.Mayo
// Moving closer, supporting checking for time resolution
//
// Revision 1.2  2004/11/25 10:23:59  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:15  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-03-21 15:43:01+00  ian_mayo
// Replace stuff which shouldn't have been deleted by IntelliJ inspector
//
// Revision 1.3  2003-03-19 15:37:55+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:27:59+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:18+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:30:02+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-01-24 14:23:38+00  administrator
// Reflect change in Layers reformat and modified events which take an indication of which layer has been modified - a step towards per-layer graphics repaints
//
// Revision 1.1  2001-08-31 13:25:00+01  administrator
// to comply with new StepControl signature
//
// Revision 1.0  2001-07-17 08:41:40+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-17 09:48:00+00  novatech
// remove unnecessary import statements
//
// Revision 1.1  2001-01-03 13:40:53+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:45:27  ianmayo
// initial import of files
//
// Revision 1.4  2000-10-03 14:15:39+01  ian_mayo
// provide accessor for propertyPanel, together with abstract method to trigger edit of Highlighter
//
// Revision 1.3  2000-03-27 14:42:17+01  ian_mayo
// add dummy method
//
// Revision 1.2  2000-03-14 09:50:52+00  ian_mayo
// add no-op function
//
// Revision 1.1  1999-10-12 15:34:21+01  ian_mayo
// Initial revision
//
// Revision 1.3  1999-09-14 15:51:25+01  administrator
// automatic time stepping
//
// Revision 1.2  1999-08-26 09:47:36+01  administrator
// <>
//
// Revision 1.1  1999-08-04 10:53:01+01  administrator
// Initial revision
//

import java.awt.*;
import java.awt.event.*;
import Debrief.GUI.Tote.*;
import MWC.GUI.Properties.*;
import MWC.GUI.*;
import MWC.Utilities.TextFormatting.*;
import MWC.GenericData.HiResDate;

public final class AWTStepControl extends StepControl implements java.awt.event.ActionListener
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  private Panel _thePanel;
  private Button _smallFwd;
  private Button _smallBwd;
  private Button _largeFwd;
  private Button _largeBwd;
  private Button _editBtn;
  private Button _startBtn;
  private Button _endBtn;
  private Label _timeTxt;
  private final PropertiesPanel _theEditor;
  Checkbox _autoBtn;



  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  public AWTStepControl(final PropertiesPanel theEditor, ToolParent theParent)
  {
    super(theParent);

    _theEditor = theEditor;

    initForm();
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  protected final void initForm()
  {
    _thePanel = new Panel();
    _thePanel.setLayout(new BorderLayout());

    // the left and right hand sides
    final Panel lhh = new Panel();
    lhh.setLayout(new BorderLayout());
    final Panel lh = new Panel();
    lh.setLayout(new GridLayout(1,0));
    final Panel rh = new Panel();
    rh.setLayout(new GridLayout(1,0));

    // create the edit button
    _editBtn = new Button("?");
    _editBtn.addActionListener(new ActionListener(){
      public void actionPerformed(final ActionEvent e)
      {
        doEdit();
      }
      });

    // create the manual/auto button
    _autoBtn = new Checkbox("Auto");
    _autoBtn.setState(false);
    _autoBtn.addItemListener(new ItemListener(){
      public void itemStateChanged(final ItemEvent e)
      {
        doAuto(_autoBtn.getState());
      }
      });

    // create the simple buttons
    _startBtn = new Button("<-");
    _largeBwd = new Button("<<");
    _smallBwd = new Button("<");
    _timeTxt = new Label("-----", Label.CENTER);
    _smallFwd = new Button(">");
    _largeFwd = new Button(">>");
    _endBtn = new Button("->");

    // add the buttons to the step panel
    lh.add(_editBtn);
    lh.add(_startBtn);
    lh.add(_largeBwd);
    lh.add(_smallBwd);

    // put the left hand components in
    lhh.add("West", _autoBtn);
    lhh.add("East", lh);
    rh.add(_smallFwd);
    rh.add(_largeFwd);
    rh.add(_endBtn);

    // add the other components
    _thePanel.add("Center", _timeTxt);
    _thePanel.add("West", lhh);
    _thePanel.add("East", rh);

    // and the event handlers
    _startBtn.addActionListener(this);
    _largeBwd.addActionListener(this);
    _smallBwd.addActionListener(this);
    _smallFwd.addActionListener(this);
    _largeFwd.addActionListener(this);
    _endBtn.addActionListener(this);
  }

  public final Panel getPanel()
  {
    return _thePanel;
  }

  /** register with the painter manager
   */
  protected final void painterIsDefined()
  {
  }


  // convenience method called from parent class
  protected void doEditPainter()
  {
    doEdit();
  }

  /** respond to update event as triggered by GUI-independent parent
   */
  public final void updateForm(final HiResDate DTG)
  {
    final String val = DebriefFormatDateTime.toStringHiRes(DTG);
    _timeTxt.setText(val);
  }

  /** one of our edit buttons has been pressed
   */
  public final void actionPerformed(final java.awt.event.ActionEvent p1)
  {
    // get the name of the control
    boolean fwd=true;
    boolean large=true;
    final Button b =(Button) p1.getSource();

    // first sort out which set it is
    if((b == _startBtn) || (b == _endBtn))
    {
      if(b == _startBtn)
      {
        super.gotoStart();
      }
      else
        super.gotoEnd();
    }
    else
    {
      if(b == _largeBwd)
         {  fwd = false; large = true;  }
      if(b == _smallBwd)
         {  fwd = false; large = false; }
      if(b == _smallFwd)
         {  fwd = true;  large = false; }
      if(b == _largeFwd)
         {  fwd = true;  large = true;  }

      _goingForward = fwd;
      _largeSteps = large;

      super.doStep(fwd, large);
    }
  }

  /** edit button has been pressed
   */
  void doEdit()
  {
    // has the editor been assigned?
    if(_thePanel != null)
    {
      // get our edit info
      final Editable.EditorType et2 = getInfo();
      // and open it in the panel
      _theEditor.addEditor(et2, null);
    }
  }

  /** set the automatic mode as indicated
   * @param go boolean whether to go auto or not
   */
  void doAuto(final boolean go)
  {

    if(go)
      startTimer();
    else
      stopTimer();
  }

	protected final void formatTimeText()
	{
		// don't bother
	}

  /** accessor method from the parent class
   */
  protected final PropertiesPanel getPropertiesPanel()
  {
    return _theEditor;
  }

  /***************************************************
   * set of methods to control the time displayed in the toolbox (of particular
   * use in remembering the T-Zero time)
   */

  /** get the time in the start slider in the toolbox
   *
   */
  public final HiResDate getToolboxStartTime(){return null;}
  /** get the time in the finish slider in the toolbox
   *
   */
  public final HiResDate getToolboxEndTime(){return null;}
  /** set the time in the start slider in the toolbox
   *
   */
  public final void setToolboxStartTime(final HiResDate val){}
  /** set the time in the start slider in the toolbox
   *
   */
  public final void setToolboxEndTime(final HiResDate val){}


}
