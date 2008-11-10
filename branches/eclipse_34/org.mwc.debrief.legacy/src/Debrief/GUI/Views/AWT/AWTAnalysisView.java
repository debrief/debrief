// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AWTAnalysisView.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: AWTAnalysisView.java,v $
// Revision 1.3  2005/12/13 09:04:30  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/11/26 11:37:48  Ian.Mayo
// Moving closer, supporting checking for time resolution
//
// Revision 1.1.1.2  2003/07/21 14:47:34  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:38:07+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:09+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:14+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:49+01  ian_mayo
// Initial revision
//
// Revision 1.2  2001-10-08 17:11:51+01  administrator
// Add declaration of relative painter
//
// Revision 1.1  2001-08-06 12:44:21+01  administrator
// Change return type of panel
//
// Revision 1.0  2001-07-17 08:41:36+01  administrator
// Initial revision
//
// Revision 1.3  2001-07-12 12:16:05+01  novatech
// Pass the ToolParent to the child propery editor
//
// Revision 1.2  2001-06-14 11:59:35+01  novatech
// pass new parameters to status bar
//
// Revision 1.1  2001-01-03 13:40:49+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:46:32  ianmayo
// initial import of files
//
// Revision 1.7  2000-09-27 14:47:32+01  ian_mayo
// remove unnecessary import
//
// Revision 1.6  2000-03-14 09:51:11+00  ian_mayo
// pass Layers data to the tote at construction
//
// Revision 1.5  2000-03-07 14:48:14+00  ian_mayo
// optimised algorithms
//
// Revision 1.4  1999-12-03 14:41:35+00  ian_mayo
// pass the tote to the painter
//
// Revision 1.3  1999-11-26 15:50:48+00  ian_mayo
// tidying up
//
// Revision 1.2  1999-11-18 11:17:01+00  ian_mayo
// TabPanel name has changed
//
// Revision 1.1  1999-10-12 15:34:15+01  ian_mayo
// Initial revision
//
// Revision 1.7  1999-08-04 09:45:31+01  administrator
// minor mods, tidying up
//
// Revision 1.6  1999-07-27 11:00:39+01  administrator
// implemented new location for chart-related tools
//
// Revision 1.5  1999-07-27 09:27:48+01  administrator
// general improvements
//
// Revision 1.4  1999-07-16 10:01:50+01  administrator
// Nearing end of phase 2
//
// Revision 1.3  1999-07-12 08:09:22+01  administrator
// Property editing added
//
// Revision 1.2  1999-07-08 13:08:45+01  administrator
// <>
//
// Revision 1.1  1999-07-07 11:10:18+01  administrator
// Initial revision
//
//


package Debrief.GUI.Views.AWT;

import java.awt.*;

import Debrief.GUI.Frames.Session;
import Debrief.GUI.Frames.AWT.AWTSession;
import Debrief.GUI.Views.AnalysisView;
import MWC.GUI.ToolParent;
import MWC.GUI.Chart.AWT.AWTChart;
import MWC.GUI.Properties.AWT.AWTPropertiesPanel;
import MWC.GUI.SplitPanel.*;
import MWC.GUI.TabPanel.AWTTabPanel;
import MWC.GUI.Tools.AWT.AWTToolbar;
import MWC.GUI.Tools.Chart.AWT.AWTCursorPosition;

/** AWT implementation of analysis view
 */
public final class AWTAnalysisView extends AnalysisView {

  ///////////////////////////////////////////////
  // member variables
  ///////////////////////////////////////////////

  /** the whole of the panel we are contained in
   */
  private SplitPanel _thePanel;

  /** the left-hand pane which contains the toolbar and message window
   */
  private SplitPanel _theInfoPanel;

  /** the toolbar in the info panel
   */
  protected Panel _theToolbar;

  /** the zoom display in the info panel
   */
  protected Panel _theOverview;

  /** the tote/properties panel
   */
  protected AWTTabPanel _theProperties;

  /** the status bar at the foot of the page
   */
  private MWC.GUI.AWT.AWTStatusBar _theStatusBar;

  ///////////////////////////////////////////////
  // constructor
  ///////////////////////////////////////////////
  public AWTAnalysisView(final ToolParent theParent, final AWTSession theSession){
    super(theParent, theSession);

    // create the GUI
    initForm(theSession);

    // now build the toolbar
    buildTheInterface();

    // force the panel to do a layout
    _thePanel.doLayout();

  }

  ///////////////////////////////////////////////
  // member functions
  ///////////////////////////////////////////////

  /** return the AWT panel we are contained in
   */
  public final java.awt.Component getPanel(){
    return _thePanel;
  }

  /** layout the controls within our panel
   */
  private void initForm(final Session theSession){
    // create the panel
    _thePanel = new SplitPanel();

    // create the main components of the panel
    final AWTChart _theChart = new AWTChart(theSession.getData());
    final AWTToolbar _theToolbar = new AWTToolbar(AWTToolbar.VERTICAL);
    final AWTPropertiesPanel _theProperties = new AWTPropertiesPanel(_theChart,
                                                               theSession.getUndoBuffer(),
                                                               super.getParent());
    final Debrief.GUI.Tote.AWT.AWTTote _theTote = new
       Debrief.GUI.Tote.AWT.AWTTote(_theProperties, theSession.getData(), getParent());
    _theProperties.addTabPanel("Tote", true, _theTote.getPanel());

    final Debrief.GUI.Tote.Painters.TotePainter sp
      = new Debrief.GUI.Tote.Painters.SnailPainter(_theChart,
                                                  theSession.getData(),
																									_theTote);

    final Debrief.GUI.Tote.Painters.TotePainter tp
      = new Debrief.GUI.Tote.Painters.TotePainter(_theChart,
                                                  theSession.getData(),
																									_theTote);

		final Debrief.GUI.Tote.Painters.RelativePainter  rp  =
			new Debrief.GUI.Tote.Painters.RelativePainter(getChart(),
			theSession.getData(),
			getTote());



    final Debrief.GUI.Tote.Painters.PainterManager pm =
		  new Debrief.GUI.Tote.Painters.PainterManager(_theTote.getStepper());
		pm.addPainter(sp);
		pm.addPainter(tp);
		pm.addPainter(rp);
		pm.setCurrentListener(tp);

    _theStatusBar = new MWC.GUI.AWT.AWTStatusBar(_theProperties, getParent());

    // and configure the tote
    final Label cursorPos = new Label("000 00 00.00 N 000 00 00.00W");
//    _theTote.getStatus().add(cursorPos);
    _theChart.addCursorMovedListener(new AWTCursorPosition(_theChart, cursorPos));

    // inform the parent of the components
    setToolbar(_theToolbar);
    setChart(_theChart);
    setProperties(_theProperties);
    setTote(_theTote);
    setStatusBar(_theStatusBar);

    // now create the utility holders we are using
    _theInfoPanel = new SplitPanel();

    // now insert the items
    _thePanel.add((Canvas)_theChart.getCanvas(),
                  new PaneConstraints("chart",
                                      null,
                                      PaneConstraints.ROOT,
                                      1.0f));

    _thePanel.add(_theInfoPanel,
        new PaneConstraints("infoPanel",
                            "chart",
                            PaneConstraints.LEFT,
                            0.3f));

    _thePanel.add(_theStatusBar,
        new PaneConstraints("statusBar",
                            "chart",
                            PaneConstraints.BOTTOM,
                            0.1f));


    _theInfoPanel.add(_theToolbar,
                      new PaneConstraints("toolbar",
                                          "",
                                          PaneConstraints.ROOT,
                                          1.0f));
    _theInfoPanel.add(_theProperties,
                      new PaneConstraints("properties",
                                          "toolbar",
                                          PaneConstraints.BOTTOM,
                                          0.7f));

    // adjust layout
    _thePanel.doLayout();
  }

}
