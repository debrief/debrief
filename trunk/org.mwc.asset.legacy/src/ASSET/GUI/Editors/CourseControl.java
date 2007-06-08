package ASSET.GUI.Editors;

import java.awt.*;
import javax.swing.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class CourseControl extends JPanel {

  //////////////////////////////////////////////////////////////////////
  // UI components
  //////////////////////////////////////////////////////////////////////
  private JPanel centrePanel = new JPanel();
  private GridLayout gridLayout2 = new GridLayout();
  private JLabel jLabel6 = new JLabel();
  private JLabel jLabel7 = new JLabel();
  private JLabel demCourseVal = new JLabel();
  private JLabel curCourseVal = new JLabel();

  //////////////////////////////////////////////////////////////////////
  // my parameters
  //////////////////////////////////////////////////////////////////////
  private double curCourseRads = 0.785;
  private double demCourseRads = 0.334;

  public CourseControl() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    centrePanel.setLayout(gridLayout2);
    gridLayout2.setRows(2);
    jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel6.setText("dem:");
    jLabel7.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabel7.setText("cur:");
    demCourseVal.setFont(new java.awt.Font("Dialog", 0, 18));
    demCourseVal.setForeground(Color.red);
    demCourseVal.setText("000");
    curCourseVal.setFont(new java.awt.Font("Dialog", 0, 18));
    curCourseVal.setText("000");
    jLabel1.setFont(new java.awt.Font("Dialog", 0, 45));
    jLabel1.setText("   ");
    jLabel2.setText("              ");
    jLabel3.setText("              ");
    jLabel4.setFont(new java.awt.Font("Dialog", 0, 45));
    jLabel4.setText(" ");
    this.add(centrePanel, BorderLayout.CENTER);
    centrePanel.add(jLabel6, null);
    centrePanel.add(demCourseVal, null);
    centrePanel.add(jLabel7, null);
    centrePanel.add(curCourseVal, null);
    this.add(jLabel1, BorderLayout.SOUTH);
    this.add(jLabel2, BorderLayout.WEST);
    this.add(jLabel3, BorderLayout.EAST);
    this.add(jLabel4, BorderLayout.NORTH);
  }

  private java.text.DecimalFormat courseF = new java.text.DecimalFormat("000.0");
  private BorderLayout borderLayout1 = new BorderLayout();
  private JLabel jLabel1 = new JLabel();
  private JLabel jLabel2 = new JLabel();
  private JLabel jLabel3 = new JLabel();
  private JLabel jLabel4 = new JLabel();

  public void setDemCourse(final double val)
  {
    demCourseVal.setText(courseF.format(val));
    demCourseRads = MWC.Algorithms.Conversions.Degs2Rads(val);
    this.repaint();
  }
  public void setCourse(final double val)
  {
    curCourseVal.setText(courseF.format(val));
    curCourseRads = MWC.Algorithms.Conversions.Degs2Rads(val);
    this.repaint();

  }

  public void paint(final java.awt.Graphics graph)
  {

    super.paint(graph);

    // where is our tl corner
    final java.awt.Point myTL = this.getLocation();


    // draw our outer circle
    final java.awt.Dimension mySize = this.getSize();

    // calculate the smaller size
    final int diam = Math.min(mySize.width , mySize.height) - 30;

    // determine the centre
    final java.awt.Point centre = new java.awt.Point(mySize.width/2, mySize.height/2);


    centre.translate(myTL.x, myTL.y);

    // determine the tl corner
    final java.awt.Point tl = new java.awt.Point(centre.x - diam/2, centre.y - diam/2);


    graph.setColor(java.awt.Color.black);

    // draw the circle
    graph.drawOval(tl.x, tl.y, diam, diam);

    // draw the spokes
    double sinVal = Math.sin(curCourseRads);
    double cosVal = Math.cos(curCourseRads);

    // produce the point
    int innerRad = (diam / 2) - 3;
    int outerRad = (diam / 2) + 5;

    final java.awt.Point innerCur = new java.awt.Point((int)(innerRad * sinVal),
                                              -(int)(innerRad * cosVal));
    final java.awt.Point outerCur = new java.awt.Point((int)(outerRad * sinVal),
                                              -(int)(outerRad * cosVal));

    // and plot it
    graph.setColor(java.awt.Color.black);
    innerCur.translate(centre.x, centre.y);
    outerCur.translate(centre.x, centre.y);
    graph.drawLine(innerCur.x, innerCur.y, outerCur.x, outerCur.y);

    // now the outer
    sinVal = Math.sin(demCourseRads);
    cosVal = Math.cos(demCourseRads);

    // produce the point
    innerRad = (diam / 2) - 3;
    outerRad = (diam / 2) + 5;

    final java.awt.Point innerDem = new java.awt.Point((int)(innerRad * sinVal),
                                                  -(int)(innerRad * cosVal));
    final java.awt.Point outerDem = new java.awt.Point((int)(outerRad * sinVal),
                                                 -(int)(outerRad * cosVal));

    // and plot it
    graph.setColor(java.awt.Color.red);
    innerDem.translate(centre.x, centre.y);
    outerDem.translate(centre.x, centre.y);
    graph.drawLine(innerDem.x, innerDem.y, outerDem.x, outerDem.y);

  }

}