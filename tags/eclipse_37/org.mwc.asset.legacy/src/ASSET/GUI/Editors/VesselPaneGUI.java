package ASSET.GUI.Editors;

import ASSET.Models.Movement.HighLevelDemandedStatus;
import ASSET.Models.Movement.SimpleDemandedStatus;
import MWC.GUI.Swing.MultiLineLabel;
import MWC.GenericData.WorldSpeed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

public class VesselPaneGUI extends JToolBar
{

  //////////////////////////////////////////////////////////////////////
  // UI COMPONENTS
  //////////////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel bottomHolder = new JPanel();
  private GridLayout gridLayout1 = new GridLayout();
  private JPanel topButtons = new JPanel();
  private JPanel bottomButtons = new JPanel();
  private JButton statusBtn = new SmallButton();
  private JButton detectionsBtn = new SmallButton();
  private JButton behaviorBtn = new SmallButton();
  private JButton movementBtn = new SmallButton();
  private JButton radiatedBtn = new SmallButton();
  private JButton detectionBtn = new SmallButton();
  private JPanel centerPanel = new JPanel();
  private JPanel depthHolder = new JPanel();
  private BorderLayout borderLayout2 = new BorderLayout();
  private BorderLayout borderLayout3 = new BorderLayout();
  private JPanel depthSliderHolders = new JPanel();
  private GridLayout gridLayout2 = new GridLayout();
  private JProgressBar demDepth = new JProgressBar();
  private JProgressBar curDepth = new JProgressBar();
  private JPanel depthLblHolder = new JPanel();
  private JLabel depthLbl = new JLabel();
  private GridLayout gridLayout3 = new GridLayout();
  private JLabel jLabel1 = new JLabel();
  private JPanel speedHolder = new JPanel();
  private JPanel sliderHolder = new JPanel();
  private JProgressBar curSpeed = new JProgressBar();
  private JProgressBar demSpeed = new JProgressBar();
  private GridLayout gridLayout4 = new GridLayout();
  private BorderLayout borderLayout4 = new BorderLayout();
  private JPanel spdLblHolder = new JPanel();
  private GridLayout gridLayout5 = new GridLayout();
  private JLabel speedLbl = new JLabel();
  private JLabel jLabel3 = new JLabel();
  private JLabel jLabel4 = new JLabel();
  private JLabel jLabel2 = new JLabel();
  final JPanel courseHolder = new JPanel();
  final JLabel courseLbl = new JLabel();
  final BorderLayout courseborderLayout5 = new BorderLayout();
  final JPanel coursePanel = new JPanel();
  private JLabel curCourse = new JLabel();
  final JLabel courseLabel6 = new JLabel();
  private JLabel demCourse = new JLabel();
  final JLabel courseLabel5 = new JLabel();
  final GridLayout courseGrid = new GridLayout();
  private JLabel curDepthVal = new JLabel();
  private JPanel depthValHolder = new JPanel();
  private JLabel demDepthVal = new JLabel();
  private GridLayout gridLayout7 = new GridLayout();
  private JPanel SpdValHolder = new JPanel();
  private JLabel demSpeedVal = new JLabel();
  private JLabel curSpeedVal = new JLabel();
  private JPanel fuelHolder = new JPanel();
  private JPanel fuelLblHolder = new JPanel();
  private JPanel fuelSliderHolders = new JPanel();
  private JLabel curFuelVal = new JLabel();
  private JPanel fuelValHolder = new JPanel();
  private JProgressBar curFuel = new JProgressBar();
  private GridLayout gridLayout8 = new GridLayout();
  private BorderLayout borderLayout6 = new BorderLayout();
  private GridLayout gridLayout9 = new GridLayout();
  private JLabel fuelLabel = new JLabel();
  private MultiLineLabel curActivity = new MultiLineLabel();
  private GridLayout gridLayout6 = new GridLayout();

  private CourseControl courseControl = new CourseControl();
  private java.util.Vector<ActionListener> _myListeners = new java.util.Vector<ActionListener>(0, 1);

  //////////////////////////////////////////////////////////////////////
  // my own components
  //////////////////////////////////////////////////////////////////////

  public VesselPaneGUI()
  {
    try
    {
      initForm();
      this.setName("Status");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  private void initForm() throws Exception
  {
    this.setFloatable(true);
    this.setBorderPainted(true);

    this.setLayout(borderLayout1);
    bottomHolder.setLayout(gridLayout1);
    statusBtn.setText("Status");
    statusBtn.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        fireEvent(VesselPane.STATUS);
      }
    });
    detectionsBtn.setText("Detections");
    detectionsBtn.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        fireEvent(VesselPane.DETECTIONS);
      }
    });
    behaviorBtn.setText("Behavior");
    behaviorBtn.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        fireEvent(VesselPane.DECISION);
      }
    });
    movementBtn.setText("Movement");
    movementBtn.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        fireEvent(VesselPane.MOVEMENT);
      }
    });
    detectionBtn.setText("Sensor");
    detectionBtn.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        fireEvent(VesselPane.SENSORS);
      }
    });
    radiatedBtn.setText("Noise Levels");
    radiatedBtn.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        fireEvent(VesselPane.RADIATED_NOISE);
      }
    });
    /*  behaviorBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          fireEvent(VesselPane.SENSORS);
        }
      });*/
    gridLayout1.setRows(2);
    centerPanel.setLayout(borderLayout3);
    depthHolder.setLayout(borderLayout2);
    depthSliderHolders.setLayout(gridLayout2);
    demDepth.setOrientation(JProgressBar.VERTICAL);
    demDepth.setValue(12);
    curDepth.setOrientation(JProgressBar.VERTICAL);
    curDepth.setValue(12);
    depthLbl.setToolTipText("");
    depthLbl.setHorizontalAlignment(SwingConstants.CENTER);
    depthLbl.setText("Depth");
    depthLblHolder.setLayout(gridLayout3);
    gridLayout3.setRows(2);
    jLabel1.setText("dem cur ");
    sliderHolder.setLayout(gridLayout4);
    gridLayout4.setRows(2);
    speedHolder.setLayout(borderLayout4);
    demSpeed.setValue(23);
    curSpeed.setValue(33);
    spdLblHolder.setLayout(gridLayout5);
    gridLayout5.setRows(2);
    speedLbl.setText("Speed");
    jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel3.setText("cur ");
    jLabel4.setText(" ");
    jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel2.setText("dem ");


    curDepthVal.setHorizontalAlignment(SwingConstants.CENTER);
    curDepthVal.setText("00");
    demDepthVal.setHorizontalAlignment(SwingConstants.CENTER);
    demDepthVal.setText("00");
    SpdValHolder.setLayout(gridLayout7);
    demSpeedVal.setText("00");
    curSpeedVal.setText("00");
    gridLayout7.setRows(2);
    gridLayout7.setColumns(1);
    fuelHolder.setLayout(borderLayout6);
    fuelLblHolder.setLayout(gridLayout8);
    fuelSliderHolders.setLayout(gridLayout9);
    curFuelVal.setText("00");
    curFuel.setOrientation(JProgressBar.VERTICAL);
    curFuel.setValue(12);
    fuelLabel.setToolTipText("");
    fuelLabel.setHorizontalAlignment(SwingConstants.CENTER);
    fuelLabel.setText("Fuel");

    /** setup the activity holder which goes at the top of the panel
     *
     */
    curActivity.setText("waiting");
    curActivity.setFont(curActivity.getFont().deriveFont(0.8f * curActivity.getFont().getSize()));
    curActivity.setRows(3);


    depthValHolder.setLayout(gridLayout6);
//    guiBtn.setText("GUI");
//    guiBtn.addActionListener(new java.awt.event.ActionListener()
//    {
//      public void actionPerformed(final ActionEvent e)
//      {
//        guiBtn_actionPerformed(e);
//      }
//    });
    this.add(bottomHolder, BorderLayout.SOUTH);
    bottomHolder.add(topButtons, null);
//    topButtons.add(guiBtn, null);
    topButtons.add(statusBtn, null);
    topButtons.add(detectionsBtn, null);
    topButtons.add(radiatedBtn, null);
    bottomHolder.add(bottomButtons, null);
    bottomButtons.add(detectionBtn, null);
    bottomButtons.add(behaviorBtn, null);
    bottomButtons.add(movementBtn, null);
    this.add(centerPanel, BorderLayout.CENTER);
    centerPanel.add(depthHolder, BorderLayout.EAST);
    depthHolder.add(depthSliderHolders, BorderLayout.CENTER);
    depthSliderHolders.add(demDepth, null);
    depthSliderHolders.add(curDepth, null);
    depthHolder.add(depthLblHolder, BorderLayout.NORTH);
    depthLblHolder.add(depthLbl, null);
    depthLblHolder.add(jLabel1, null);
    depthHolder.add(depthValHolder, BorderLayout.SOUTH);
    depthValHolder.add(demDepthVal, null);
    depthValHolder.add(curDepthVal, null);
    centerPanel.add(speedHolder, BorderLayout.SOUTH);
    speedHolder.add(sliderHolder, BorderLayout.CENTER);
    sliderHolder.add(demSpeed, null);
    sliderHolder.add(curSpeed, null);
    speedHolder.add(spdLblHolder, BorderLayout.WEST);
    spdLblHolder.add(speedLbl, null);
    spdLblHolder.add(jLabel2, null);
    spdLblHolder.add(jLabel4, null);
    spdLblHolder.add(jLabel3, null);
    speedHolder.add(SpdValHolder, BorderLayout.EAST);
    SpdValHolder.add(demSpeedVal, null);
    SpdValHolder.add(curSpeedVal, null);

    //////////
    // the course
    //////////
    /*
    centerPanel.add(courseHolder, BorderLayout.CENTER);
    courseHolder.add(courseLbl, BorderLayout.SOUTH);
    courseHolder.add(coursePanel, BorderLayout.CENTER);
    coursePanel.add(courseLabel5, null);
    coursePanel.add(demCourse, null);
    coursePanel.add(courseLabel6, null);
    coursePanel.add(curCourse, null);
    courseLbl.setHorizontalAlignment(SwingConstants.CENTER);
    courseLbl.setText("Course");
    courseHolder.setLayout(courseborderLayout5);
    curCourse.setText("0");
    courseLabel6.setText("cur:");
    demCourse.setText("0");
    courseLabel5.setText("dem:");
    coursePanel.setLayout(courseGrid);
    courseGrid.setRows(2);
*/
    centerPanel.add(courseControl, BorderLayout.CENTER);

    // the fuel
    centerPanel.add(fuelHolder, BorderLayout.WEST);
    fuelHolder.add(fuelSliderHolders, BorderLayout.CENTER);
    fuelSliderHolders.add(curFuel, null);
    fuelHolder.add(fuelLblHolder, BorderLayout.NORTH);
    fuelLblHolder.add(fuelLabel, null);
    fuelHolder.add(fuelValHolder, BorderLayout.SOUTH);
    fuelValHolder.add(curFuelVal, null);
    this.add(curActivity, BorderLayout.NORTH);
  }

  private java.text.DecimalFormat courseF = new java.text.DecimalFormat("000.0");
  private java.text.DecimalFormat valF = new java.text.DecimalFormat("00");

  public void setStatus(final ASSET.Participants.Status status)
  {
    final double dpth = -status.getLocation().getDepth();
    curDepth.setValue((int) dpth);
    curDepthVal.setText(valF.format(dpth));

    final double spd = status.getSpeed().getValueIn(WorldSpeed.M_sec);
    curSpeed.setValue((int) spd);
    curSpeedVal.setText(valF.format(spd));

    final double crse = status.getCourse();
    curCourse.setText(courseF.format(crse));

    final double fuel = status.getFuelLevel();
    curFuel.setValue((int) fuel);
    curFuelVal.setText(valF.format(fuel));

    courseControl.setCourse(crse);
  }

  public void setDemandedStatus(final ASSET.Participants.DemandedStatus demStatus)
  {
    if (demStatus != null)
    {
      // is this a simple demanded status?
      if (demStatus instanceof SimpleDemandedStatus)
      {
        SimpleDemandedStatus sds = (SimpleDemandedStatus) demStatus;

        final double spd = sds.getSpeed();
        demSpeed.setValue((int) spd);
        demSpeedVal.setText(valF.format(spd));

        final double dpth = sds.getHeight();
        demDepth.setValue((int) dpth);
        demDepthVal.setText(valF.format(dpth));

        final double crse = sds.getCourse();
        demCourse.setText(courseF.format(crse));
        courseControl.setDemCourse(crse);
      }
      else
      {
        HighLevelDemandedStatus hds = (HighLevelDemandedStatus) demStatus;
        WorldSpeed ws = hds.getSpeed();
        if (ws != null)
        {
          demSpeed.setValue((int) ws.getValueIn(WorldSpeed.Kts));
          demSpeedVal.setText(valF.format(ws.getValueIn(WorldSpeed.Kts)));
        }
        else
        {
          demSpeedVal.setText("n/a");
          demSpeed.setValue(0);
        }

        demDepthVal.setToolTipText("n/a");
        demCourse.setToolTipText("n/a");
        courseControl.setDemCourse(0);
      }
    }
  }

  public void setActivity(final String val)
  {
    curActivity.setText(val);
  }

  public void setVesselName(final String val)
  {
    setName(val);
  }

  public void showFuel(final boolean val)
  {
    fuelHolder.setVisible(val);
  }

  private class SmallButton extends JButton
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SmallButton()
    {
      this.setMargin(new Insets(1, 1, 1, 1));
    }
  }

  public void addActionListener(final java.awt.event.ActionListener listener)
  {
    _myListeners.add(listener);
  }

  public void removeActionListener(final java.awt.event.ActionListener listener)
  {
    _myListeners.remove(listener);
  }

  void fireEvent(final String type)
  {
    final ActionEvent e = new ActionEvent(this, 0, type);
    final java.util.Enumeration<ActionListener> enumer = _myListeners.elements();
    while (enumer.hasMoreElements())
    {
      final java.awt.event.ActionListener l = (java.awt.event.ActionListener) enumer.nextElement();
      l.actionPerformed(e);
    }
  }

  //  void statusBtn_actionPerformed(ActionEvent e) {
  //    fireEvent(VesselPane.STATUS);
  //  }
  //
  //  void behaviorBtn_actionPerformed(ActionEvent e) {
  //    fireEvent(VesselPane.DECISION);
  //  }
  //
  //  void detectionsBtn_actionPerformed(ActionEvent e) {
  //    fireEvent(VesselPane.DETECTIONS);
  //  }
  //
  //  void movementBtn_actionPerformed(ActionEvent e) {
  //    fireEvent(VesselPane.MOVEMENT);
  //  }
  //
  //  void detectionBtn_actionPerformed(ActionEvent e) {
  //    fireEvent(VesselPane.SENSORS);
  //  }
  //
  //   void radidatedBtn_actionPerformed(ActionEvent e) {
  //     fireEvent(VesselPane.RADIATED_NOISE);
  //   }


}