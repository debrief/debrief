// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingAnalysisView.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: SwingAnalysisView.java,v $
// Revision 1.4  2005/12/13 09:04:30  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.3  2004/11/26 11:37:48  Ian.Mayo
// Moving closer, supporting checking for time resolution
//
// Revision 1.2  2004/08/09 09:41:32  Ian.Mayo
// Lots of Idea tidying
//
// Revision 1.1.1.2  2003/07/21 14:47:35  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.13  2003-07-01 09:57:00+01  ian_mayo
// Implement inspector recommendations
//
// Revision 1.12  2003-06-05 16:30:32+01  ian_mayo
// set default alternate mouse mode in chart (panning)
//
// Revision 1.11  2003-03-24 11:05:56+00  ian_mayo
// Refactor OverviewChart
//
// Revision 1.10  2003-03-19 15:37:54+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.9  2003-03-14 09:39:20+00  ian_mayo
// Add zoom-in, improve Pan tools
//
// Revision 1.8  2003-03-14 08:36:20+00  ian_mayo
// Use Pan tool in overview chart, rather than doing it ourselves
//
// Revision 1.7  2003-03-10 14:13:18+00  ian_mayo
// Remove unused imports, improve calcs for plotting screen rectangle
//
// Revision 1.6  2003-03-10 10:24:06+00  ian_mayo
// Add Overview chart
//
// Revision 1.5  2003-03-06 16:28:22+00  ian_mayo
// Provide checkbox for whether overview chart enabled, experiment with Wash over overview chart
//
// Revision 1.4  2003-03-06 15:57:54+00  ian_mayo
// Improve efficiency of how we paint rectangle in overview chart
//
// Revision 1.3  2003-03-06 15:28:55+00  ian_mayo
// More overview improvements
//
// Revision 1.2  2002-05-28 12:28:06+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:14+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:47+01  ian_mayo
// Initial revision
//
// Revision 1.13  2002-02-18 20:14:53+00  administrator
// Factor out mouse support to child class, but make initForm method protected so that child class can call it before adding mouse support
//
// Revision 1.12  2002-02-18 12:33:44+00  administrator
// Remember the listener, and remove it when the session closes
//
// Revision 1.11  2002-02-18 09:21:27+00  administrator
// Add support for MouseWheel
//
// Revision 1.10  2002-01-22 15:29:44+00  administrator
// Reflect changed signature in Toolbar so that it can float
//
// Revision 1.9  2001-10-03 16:05:02+01  administrator
// Add creation of new Relative painter
//
// Revision 1.8  2001-09-09 08:40:33+01  administrator
// Now, I know this is the product of running "Tidy up" from JEdit, I don't know if there were any outstanding changes from before this.
//
// Revision 1.7  2001-08-21 12:15:44+01  administrator
// Improve tidying
//
// Revision 1.6  2001-08-17 08:00:31+01  administrator
// Clear up memory leaks
//
// Revision 1.5  2001-08-06 14:39:16+01  administrator
// set the UI of the Toolbar to our SPECIAL ui
//
// Revision 1.4  2001-07-27 17:06:49+01  administrator
// remove video testing code
//
// Revision 1.3  2001-07-24 17:00:08+01  administrator
// add our screen recorder
//
// Revision 1.2  2001-07-23 11:52:18+01  administrator
// Make the font a little smaller for the Toolbar
//
// Revision 1.1  2001-07-17 16:22:54+01  administrator
// delete unnecessary testing code
//
// Revision 1.0  2001-07-17 08:41:36+01  administrator
// Initial revision
//
// Revision 1.7  2001-07-16 15:37:12+01  novatech
// remove testing code
//
// Revision 1.6  2001-07-12 12:15:49+01  novatech
// Pass the ToolParent to the properties window
//
// Revision 1.5  2001-07-05 12:10:34+01  novatech
// correct ambiguity over which HORIZONTAL constant we are referring to
//
// Revision 1.4  2001-07-05 11:51:07+01  novatech
// use a higher level "Add to properties panel" method, which looks after remembering the data object we are editing
//
// Revision 1.3  2001-06-14 15:43:04+01  novatech
// switch to using a TabbedToolbar, using our own internal implementation
//
// Revision 1.2  2001-06-14 11:55:50+01  novatech
// pass properties to status bar using constructor
//
// Revision 1.1  2001-01-03 13:40:48+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:46:38  ianmayo
// initial import of files
//
// Revision 1.20  2000-10-10 12:21:19+01  ian_mayo
// provide support for drag & drop
//
// Revision 1.19  2000-09-26 09:50:18+01  ian_mayo
// support for relative plotting
//
// Revision 1.18  2000-08-16 15:56:06+01  ian_mayo
// tidied up sizing of SplitPanels
//
// Revision 1.17  2000-08-16 14:11:45+01  ian_mayo
// general tidying up
//
// Revision 1.16  2000-07-07 09:58:28+01  ian_mayo
// experiment with better creation of toolbar pane
//
// Revision 1.15  2000-07-05 16:35:45+01  ian_mayo
// whitespace
//
// Revision 1.14  2000-05-19 11:24:46+01  ian_mayo
// pass undoBuffer around, to undo TimeFilter operations
//
// Revision 1.13  2000-04-19 11:27:48+01  ian_mayo
// remove Properties and Panel, implement Close method to clear local storage
//
// Revision 1.12  2000-04-03 10:41:36+01  ian_mayo
// new SwingTote constructor
//
// Revision 1.11  2000-03-27 14:44:44+01  ian_mayo
// swap the order in which painters are added to the painterManager
//
// Revision 1.10  2000-03-17 13:38:34+00  ian_mayo
// Revised interface, draggable toolbars
//
// Revision 1.9  2000-03-14 15:01:40+00  ian_mayo
// switch to toolbar display of 3 rows from 5
//
// Revision 1.8  2000-03-14 09:47:30+00  ian_mayo
// pass the Layers to the Tote, so that it can auto-assign
//
// Revision 1.7  2000-03-07 14:48:15+00  ian_mayo
// optimised algorithms
//
// Revision 1.6  2000-02-04 15:52:30+00  ian_mayo
// move cursor pos out of tote panel into status panel
//
// Revision 1.5  2000-01-12 15:35:45+00  ian_mayo
// change number of tools drawn across toolbar
//
// Revision 1.4  1999-12-03 14:41:34+00  ian_mayo
// pass the tote to the painter
//
// Revision 1.3  1999-11-26 15:50:47+00  ian_mayo
// tidying up
//
// Revision 1.2  1999-11-25 16:54:56+00  ian_mayo
// implementing Swing components
//
// Revision 1.1  1999-11-23 11:51:03+00  ian_mayo
// Initial revision
//


package Debrief.GUI.Views.Swing;

import Debrief.GUI.Frames.Session;
import Debrief.GUI.Frames.Swing.SwingSession;
import Debrief.GUI.Tote.Swing.SwingStepControl;
import Debrief.GUI.Views.AnalysisView;
import MWC.GUI.Chart.Swing.SwingChart;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Chart.Pan;
import MWC.GUI.Tools.Chart.Swing.SwingCursorPosition;
import MWC.GUI.Tools.Swing.SwingToolbar;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Swing implementation of analysis view.
 *
 * @author administrator
 */
public class SwingAnalysisView extends AnalysisView
{

  ///////////////////////////////////////////////
  // member variables
  ///////////////////////////////////////////////

  /**
   * the whole of the panel we are contained in.
   */
  private JSplitPane _thePanel;

  /**
   * the left-hand pane which contains the toolbar and message window.
   */
  private JSplitPane _theInfoPanel;

  /**
   * the tote/properties panel.
   */
  private SwingPropertiesPanel _theProperties;

  /**
   * the status bar at the foot of the page.
   */
  private MWC.GUI.Swing.SwingStatusBar _theStatusBar;

  /**
   * the holder for the toolbars.
   */
  private javax.swing.JTabbedPane _toolbarHolder;

  /**
   * the thingy which looks after the painters - keep a reference so that we can
   * explicitly clear it our later.
   */
  private Debrief.GUI.Tote.Painters.PainterManager _painterManager;

  /**
   * the chart providing an overview of the data.
   */
  private SwingChart _overviewChart;



  ///////////////////////////////////////////////
  // constructor
  ///////////////////////////////////////////////
  /**
   * constructor, of course.
   *
   * @param theParent  the toolparent = where we control the cursor from
   * @param theSession the Session to put in the view
   */
  public SwingAnalysisView(final ToolParent theParent,
                           final SwingSession theSession)
  {
    super(theParent, theSession);

    // create the GUI
    initForm(theSession);

    // now build the toolbar
    buildTheInterface();


    addExtraToolbarButtons();


    createPainters(theSession);


  }



  ///////////////////////////////////////////////
  // member functions
  ///////////////////////////////////////////////

  /**
   * return the Swing panel we are contained in.
   *
   * @return a Panel representing the View
   */
  public final Component getPanel()
  {
    return _thePanel;
  }


  /**
   * get ready to close, set all local references to null, to assist garbage
   * collection.
   */
  public void close()
  {
    // clear out this list
    _painterManager.closeMe();
    _painterManager = null;


    // get the parent to close first
    super.close();

    // now tidy up the object we manage
    if (_thePanel != null)
    {
      _thePanel.removeAll();
    }
    if (_theInfoPanel != null)
    {
      _theInfoPanel.removeAll();
    }
    if (_toolbarHolder != null)
    {
      _toolbarHolder.removeAll();
    }
    if (_theProperties != null)
    {
      _theProperties.removeAll();
      _theProperties.closeMe();

    }
    if (_theStatusBar != null)
    {
      _theStatusBar.removeAll();
    }

  }

  /**
   * member method to add any extra toolbar buttons we're after.
   */
  private void addExtraToolbarButtons()
  {
    // create the manual/auto button
    final JCheckBox overviewBtn = new SwingStepControl.ImageCheckbox("View overview", "images/overview.gif", "images/overview_.gif");
    overviewBtn.setMargin(new Insets(0, 5, 0, 0));
    overviewBtn.setSelected(false);
    overviewBtn.addItemListener(new ItemListener()
    {
      public void itemStateChanged(final ItemEvent e)
      {
        _overviewChart.getPanel().setVisible(!_overviewChart.getPanel().isVisible());
      }
    });
    overviewBtn.setToolTipText("Show/Hide overview chart");

    // find the right toolbar tab
    final int index = _toolbarHolder.indexOfTab("View");
    final JPanel holder = (JPanel) _toolbarHolder.getComponentAt(index);
    // find our component
    final int len = holder.getComponentCount();
    for (int i = 0; i < len; i++)
    {
      final Component cp = holder.getComponent(i);
      if (cp.getName().equals(holder.getName()))
      {
        final SwingToolbar thisToolbar = (SwingToolbar) cp;
        thisToolbar.add(overviewBtn);
        break;
      }
    }
  }


  /**
   * member method to create a toolbar button for this tool.
   *
   * @param item the description of this tool
   */
  protected final void addThisTool(final MWC.GUI.Tools.MenuItemInfo item)
  {
    // see which toolbar this is on
    final String toolbar = item.getMenuName();

    SwingToolbar thisToolbar = null;

    // check if we have a toolbar for this tool
    final int index = _toolbarHolder.indexOfTab(toolbar);

    if (index == -1)
    {
      // we obviously have to create this toolbar, go for it
      thisToolbar = new SwingToolbar(MWC.GUI.Toolbar.HORIZONTAL, toolbar, (SwingSession) super._theSession);
      thisToolbar.setLayout(new GridLayout(1, 0));

      // we also put it into a panel, to assist when its floating
      final JPanel jp = new JPanel();
      jp.setLayout(new BorderLayout());
      jp.setName(toolbar);
      jp.add("West", thisToolbar);

      // finally add the panel to the toolbar
      _toolbarHolder.add(toolbar, jp);
    }
    else
    {
      final JPanel holder = (JPanel) _toolbarHolder.getComponentAt(index);
      // find our component
      final int len = holder.getComponentCount();
      for (int i = 0; i < len; i++)
      {
        final Component cp = holder.getComponent(i);
        if (cp.getName().equals(holder.getName()))
        {
          thisToolbar = (SwingToolbar) cp;
          break;
        }
      }
      //   thisToolbar = (SwingToolbar)_toolbarHolder.getComponentAt(index);
    }

    if (thisToolbar == null)
    {
      return;
    }

    // see if this is an action button, or it toggles as part of a group
    if (item.getToggleGroup() == null)
    {
      thisToolbar.addTool(item.getTool(),
                          item.getShortCut(),
                          item.getMnemonic());
    }
    else
    {
      thisToolbar.addToggleTool(item.getToggleGroup(),
                                item.getTool(),
                                item.getShortCut(),
                                item.getMnemonic());
    }
  }


  /**
   * Description of the Method.
   *
   * @param theSession Description of Parameter
   */
  private void createPainters(final SwingSession theSession)
  {

    // create our painters
    final Debrief.GUI.Tote.Painters.SnailPainter sp =
      new Debrief.GUI.Tote.Painters.SnailPainter(getChart(),
                                                 theSession.getData(),
                                                 getTote());
    final Debrief.GUI.Tote.Painters.TotePainter tp
      = new Debrief.GUI.Tote.Painters.TotePainter(getChart(),
                                                  theSession.getData(),
                                                  getTote());
    final Debrief.GUI.Tote.Painters.RelativePainter rp =
      new Debrief.GUI.Tote.Painters.RelativePainter(getChart(),
                                                    theSession.getData(),
                                                    getTote());


    // add the painters to the manager
    _painterManager = new Debrief.GUI.Tote.Painters.PainterManager(getTote().getStepper());
    _painterManager.addPainter(tp);
    _painterManager.addPainter(sp);
    _painterManager.addPainter(rp);
    _painterManager.setCurrentListener(tp);
  }


  /**
   * layout the controls within our panel.
   *
   * @param theSession Description of Parameter
   */
  void initForm(final Session theSession)
  {
    // create the panel
    _thePanel = new JSplitPane();

    // create the main components of the panel
    final SwingChart _theChart = new SwingChart(theSession.getData());

    // toolbar - we now have a tabbed panel of toolbars, to save space!
    _toolbarHolder = new MyToolbarHolder();

    // the properties panel
    _theProperties = new SwingPropertiesPanel(_theChart,
                                              theSession.getUndoBuffer(),
                                              super.getParent(),
                                              (SwingSession) theSession);

    // the tote itself (part of the properties panel)
    final Debrief.GUI.Tote.Swing.SwingTote _theTote = new
      Debrief.GUI.Tote.Swing.SwingTote(_theProperties,
                                       theSession.getData(),
                                       _theChart,
                                       theSession.getUndoBuffer(),
                                       (SwingSession) theSession, getParent());

    // create the overview chart aswell
    _overviewChart = new SwingOverviewChart(theSession.getData(), _theChart, this.getParent());

    // start off with the overview chart hidden
    _overviewChart.getPanel().setVisible(false);

    // try to put another border around the chart
    final JComponent cp = (JComponent) _overviewChart.getPanel();
    cp.setBorder(BorderFactory.createEtchedBorder());

    final JPanel overviewHolder = new JPanel()
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Dimension getPreferredSize()
      {
        return new Dimension(200, 200);
      }
    };

    overviewHolder.setBorder(BorderFactory.
                             createTitledBorder(BorderFactory.createLoweredBevelBorder(),
                                                "Overview Chart", TitledBorder.LEFT,
                                                TitledBorder.TOP,
                                                overviewHolder.getFont().deriveFont(8)));
    overviewHolder.setLayout(new BorderLayout());
    overviewHolder.add("Center", _overviewChart.getPanel());

    // put the tote & overview into a holder
    final JPanel toteHolder = new JPanel();
    toteHolder.setName("Tote");
    toteHolder.setLayout(new BorderLayout());
    toteHolder.add("North", _theTote.getPanel());
    toteHolder.add("Center", overviewHolder);

    // put the tote into the Properties panel - as a "Non-removable" item
    // try putting the tote into a toolbar
    _theProperties.addThisPanel(toteHolder);

    // tell the canvas projector where to get it's relative plotting data from
    _theChart.getCanvas().getProjection().setRelativeProjectionParent(_theTote);

    // create the general status bar (used for rng/brg measurements)
    _theStatusBar = new MWC.GUI.Swing.SwingStatusBar(_theProperties, getParent());

    // and configure the tote
    final JLabel cursorPos = new JLabel("000 00 00.00 N 000 00 00.00W");
    _theChart.addCursorMovedListener(new SwingCursorPosition(_theChart, cursorPos));

    final Pan myPanner = new Pan(_theChart, this.getParent(), null, true);
    myPanner.execute();

    /////////////////////////////////////////////////
    // pass objects back to parent
    //////////////////////////////////////////////////

    // inform the parent of the components
    setChart(_theChart);
    setProperties(_theProperties);
    setTote(_theTote);
    setStatusBar(_theStatusBar);

    //////////////////////////////////////////////////////
    // property editing bits
    /////////////////////////////////////////////////////
    final JPanel thePropertiesHolder = new JPanel();
    thePropertiesHolder.setLayout(new BorderLayout());

    // try to put the properties into a toolbar parent
    thePropertiesHolder.add(_theProperties, BorderLayout.CENTER);

    // Put in container to hold two text boxes at the foot of the page
    final JToolBar statuses = new JToolBar();
    statuses.setUI(new MWC.GUI.Tools.Swing.MyMetalToolBarUI((SwingSession) super._theSession));
    statuses.add(_theStatusBar);
    statuses.add(cursorPos);
    thePropertiesHolder.add(statuses, BorderLayout.SOUTH);

    ///////////////////////////////////////////////////////
    // layout bits
    ///////////////////////////////////////////////////////

    // set up the info panel
    _theInfoPanel = new JSplitPane();
    _theInfoPanel.setTopComponent(_toolbarHolder);
    _theInfoPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
    _theInfoPanel.setBottomComponent(thePropertiesHolder);

    // put the bits in the main panel
    _thePanel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    _thePanel.setLeftComponent(_theInfoPanel);
    _thePanel.setRightComponent(_theChart.getPanel());
    _thePanel.setContinuousLayout(false);
    _thePanel.setOneTouchExpandable(true);

    _thePanel.setDividerLocation(_theInfoPanel.getMinimumSize().width);

    _thePanel.doLayout();

    /////////////////////////////////////////////////
    // drag drop support bits
    /////////////////////////////////////////////////
    _dropSupport.addComponent(_theChart.getPanel());

  }


  /**
   * our own implementation of a tabbed toolbar - the only difference is that
   * when we drop it, we check that what we're receiving is in fact a dropped
   * toolbar and set it's name correctly.
   *
   * @author administrator
   */
  private static final class MyToolbarHolder extends JTabbedPane
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;


		/**
     * Constructor for the MyToolbarHolder object.
     */
    public MyToolbarHolder()
    {
      // shrink the font a little
      this.setFont(this.getFont().deriveFont(10.0f));
    }


    /**
     * Add the component to this view.
     *
     * @param title     Description of Parameter
     * @param component Description of Parameter
     * @return Description of the Returned Value
     */
    public final Component add(final String title, final Component component)
    {
      final Component res;
      // check if we are receiving a dropped toolbar
      if (title.equals("North"))
      {
        // a floating toolbar is being replaced - what can we do?
        res = super.add(component.getName(), component);
        this.setSelectedComponent(res);
      }
      else
      {
        // this is a normal drop operation, continue as normal
        res = super.add(title, component);
      }
      return res;
    }

  }

}

