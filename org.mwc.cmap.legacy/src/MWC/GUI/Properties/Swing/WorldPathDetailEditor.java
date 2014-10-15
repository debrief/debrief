/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Properties.Swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Moveable;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Chart.MoveableDragger;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldVector;

public class WorldPathDetailEditor extends SwingCustomEditor
{

  /***************************************************************
   *  member variables
   ***************************************************************/

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the list we show our path inside
   */
  JList _myList;

  /**
   * the sub-panel we use to show individual points
   */
  private JPanel _subPanel;

  /**
   * the button which switches on dragging of data points
   */
  private DragButton _dragger;

  /**
   * safe copy of the data object we are editing
   */
  private WorldPath _safePath;

  /**
   * the data object we are editing
   */
  private WorldPath _myPath;

  /**
   * the plotter which draws the path
   */
  private PathPlotter _myPlotter = new PathPlotter();

  /**
   * the name of the layer we insert our editor into
   */
  private static final String editorName = "Editors";

  /**
   * our property editor
   */
  SwingWorldLocationPropertyEditor _editor = new SwingWorldLocationPropertyEditor();

  /**
   * the tool parent
   */
  private ToolParent _theParent;

  /**
   * the type of object we will be editing
   */
  private final String _myType;


  /**
   * ************************************************************
   * constructor
   * *************************************************************
   */
  public WorldPathDetailEditor(final WorldPath _myPath, final PlainChart theChart, final PropertiesPanel thePanel, final ToolParent theParent,
                               final String myType)
  {
    _myType = myType;
    setObject(_myPath, theChart, null, thePanel);
    this._theParent = theParent;

    _editor.setChart(theChart);
  }

  /**
   * ************************************************************
   * member methods
   * *************************************************************
   */

  public String getMyType()
  {
    return _myType;
  }

  public void setObject(final Object data)
  {

    // store the path
    setPath((WorldPath) data);

    // take our copy
    _safePath = new WorldPath(getPath());

    // create the form
    createForm();

    // create the layers painter
    showPath();

    // set our data
    updateData();

    // redraw the plot
    getChart().getLayers().fireExtended();

  }


  /**
   * create the form
   */
  private void createForm()
  {

    // create the panel
    setName(getMyType() + " editor");

    // create the information message
    String msg = "1. Use the buttons to the right to add/remove and re-order the points in the " + getMyType() + ".\r\n";
    msg += " 2. Use the Drag button to switch on and off dragging of " + getMyType() + " points.\r\n";
    msg += " 3. Select a point from the list below to edit it in Point Editor (below).\r\n";
    msg += " 4. Use Reset to return to the original " + getMyType() + ".\r\n";
    msg += " 5. Finally use Apply then Close to update the shape.\r\n";

    // create the information panel
    final JTextArea info = new JTextArea();
    info.setBackground(this.getBackground());
    info.setLineWrap(true);
    info.setWrapStyleWord(true);
    info.setText(msg);
    // make the text a little smaller
    final Font infoFont = info.getFont();
    info.setFont(infoFont.deriveFont((float) 10.0));

    // create the top panel (with the new, up, down button)
    final JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BorderLayout());
    final JPanel btnBar = new JPanel();
    btnBar.setLayout(new GridLayout(2, 0));
    infoPanel.add("East", btnBar);
    infoPanel.add("Center", info);

    // and the new, up, down buttons
    final JButton upBtn = createButton("Move current point up", "images/Up.gif", "Up");
    upBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        doUpDown(true);
      }
    });

    final JButton downBtn = createButton("Move current point down", "images/Down.gif", "Down");
    downBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        doUpDown(false);
      }
    });

    final JButton newBtn = createButton("Add new point", "images/NewPin.gif", "New");
    newBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        addNew();
      }
    });

    final JButton deleteBtn = createButton("Delete current point", "images/DeletePin.gif", "Delete");
    deleteBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        deleteCurrent();
      }
    });

    btnBar.add(upBtn);
    btnBar.add(downBtn);
    btnBar.add(newBtn);
    btnBar.add(deleteBtn);


    // create the list
    _myList = new JList();
    _myList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    _myList.setBorder(new javax.swing.border.EtchedBorder());
    _myList.addListSelectionListener(new ListSelectionListener()
    {
      public void valueChanged(final ListSelectionEvent e)
      {
        if (!e.getValueIsAdjusting())
        {
          if (!_myList.isSelectionEmpty())
          {
            final WorldLocation loc = (WorldLocation) _myList.getSelectedValue();
            if (loc != null)
              editThis(_myList.getSelectedValue());
          }
        }
      }
    });

    // show the list
    final JPanel listHolder = new JPanel();
    listHolder.setLayout(new BorderLayout());
    _dragger = new DragButton(getChart(), _theParent);
    listHolder.add("North", _dragger);
    listHolder.add("Center", _myList);


    // create the sub-form
    _subPanel = new JPanel();
    _subPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Point Editor"));
    _subPanel.setName("Point editor");
    _subPanel.setLayout(new BorderLayout());
    _subPanel.add("Center", _editor.getCustomEditor());
    final JButton subApply = new JButton("Apply");
    subApply.setToolTipText("Apply the changes to our " + getMyType());
    subApply.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        final WorldLocation curLoc = (WorldLocation) _myList.getSelectedValue();
        if (curLoc != null)
        {
          curLoc.copy((WorldLocation) _editor.getValue());
          _myList.repaint();

          // also update the point in the path
          final int index = _myList.getSelectedIndex();
          getPath().getLocationAt(index).copy(curLoc);

          // update the plot
          getChart().getLayers().fireReformatted(null);

        }
      }

    });

    // create the bottom toolbar
    final JButton closeBtn = new JButton("Close");
    closeBtn.setToolTipText("Close this panel");
    closeBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        doClose();
      }
    });

    // now the reset button
    final JButton resetBtn = new JButton("Reset");
    resetBtn.setToolTipText("Reset the data");
    resetBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        doReset();
      }
    });

    final JPanel buttonHolder = new JPanel();
    buttonHolder.setLayout(new GridLayout(1, 0));
    buttonHolder.add(closeBtn);
    buttonHolder.add(subApply);
    buttonHolder.add(resetBtn);

    final JPanel bottomHolder = new JPanel();
    bottomHolder.setLayout(new BorderLayout());
    bottomHolder.add("Center", _subPanel);
    bottomHolder.add("South", buttonHolder);

    // put them into the form
    setLayout(new BorderLayout());
    add("North", infoPanel);
    add("Center", listHolder);
    add("South", bottomHolder);

  }


  /**
   * create and format a new button
   */
  private JButton createButton(final String toolTip, final String image, final String title)
  {
    JButton newB = null;

    // first try to get the URL of the image
    final java.lang.ClassLoader loader = getClass().getClassLoader();
    if (loader != null)
    {
      final java.net.URL imLoc = loader.getResource(image);
      if (imLoc != null)
      {
        final ImageIcon im = new ImageIcon(imLoc);
        newB = new JButton(im);
      }
    }

    // just catch any problems
    if (newB == null)
      newB = new JButton(title);

    newB.setMargin(new Insets(0, 0, 0, 0));
    newB.setToolTipText(toolTip);

    return newB;
  }

  /**
   * move the current point up or down
   */
  void doUpDown(final boolean up)
  {

    // can we get the current point
    final WorldLocation curLoc = (WorldLocation) _myList.getSelectedValue();

    if (curLoc != null)
    {
      if (up)
      {
        getPath().moveUpward(curLoc);
      }
      else
      {
        getPath().moveDownward(curLoc);
      }

      // update the data
      updateData();

      // reselect the current point
      _myList.setSelectedValue(curLoc, true);

      // and redraw the plot
      getChart().getLayers().fireReformatted(null);
    }
  }

  /**
   * add a new location, based on the centre of the currently visible area
   */
  void addNew()
  {
    final WorldLocation center = getChart().getDataArea().getCentre();
    getPath().addPoint(center);

    // and update the data
    updateData();

    // select the current point
    _myList.setSelectedValue(center, true);

    // redraw the plot
    getChart().getLayers().fireExtended();

    // inform any listeners
    fireModified("New Point", getPath(), getPath());

  }

  /**
   * add a new location, based on the centre of the currently visible area
   */
  void deleteCurrent()
  {

    // can we get the current point
    final WorldLocation curLoc = (WorldLocation) _myList.getSelectedValue();

    if (curLoc != null)
    {
      getPath().remove(curLoc);

      // and update the data
      updateData();

      // redraw the plot
      getChart().getLayers().fireExtended();

      // inform any listeners
      fireModified("Point removed", getPath(), getPath());

    }
  }


  /**
   * reset the data we are editing
   */
  public void doReset()
  {
    // copy the safe data back into our data
    setPath(new WorldPath(_safePath));

    _myPlotter.update();

    // update the list
    updateData();

    // inform any listeners
    fireModified("Points reset", getPath(), getPath());

    // redraw the plot
    getChart().getLayers().fireReformatted(null);
  }

  /**
   * an item has been selected, edit it
   */
  void editThis(final Object val)
  {
    _editor.setValue(val);
  }

  /**
   * accessor to get the path object
   */
  WorldPath getPath()
  {
    return _myPath;
  }

  /**
   * setter, to set the path object
   */
  private void setPath(final WorldPath path)
  {
    _myPath = path;
  }

  /**
   * create the painter which shows the points in this path
   */
  private void showPath()
  {
    final Layers theLayers = getChart().getLayers();
    Layer bl = theLayers.findLayer(editorName);
    if (bl == null)
    {
      bl = new BaseLayer();
      bl.setName(editorName);
      theLayers.addThisLayer(bl);
    }

    // now add ourselves to the editor layer
    bl.add(_myPlotter);

  }

  /**
   * update our data
   */
  void updateData()
  {

    // find out which one is selected
    final int curSel = _myList.getSelectedIndex();

    // create an object to put the data into
    final Vector<WorldLocationHolder> list = new Vector<WorldLocationHolder>(0, 1);

    if (getPath() != null)
    {
      final int len = getPath().size();
      for (int i = 0; i < len; i++)
      {
        final WorldLocation thisLoc = getPath().getLocationAt(i);
        final WorldLocationHolder holder = new WorldLocationHolder(thisLoc, i + 1);
        list.add(holder);
      }
    }

    // and put the data into the list
    _myList.setListData(list);

    // select the previous item
    if (curSel != -1 && curSel < _myPath.size())
      _myList.setSelectedIndex(curSel);

    // update the list
    _myPlotter.update();
  }


  /**
   * close the form
   */
  public void doClose()
  {
    // remove ourselves from the properties panel
    getPanel().remove(this);

    // remove the layer painter
    final BaseLayer editorLayer = (BaseLayer) getChart().getLayers().findLayer(editorName);
    editorLayer.removeElement(_myPlotter);

    // are there any items remaining in the editors layer?
    if (editorLayer.size() == 0)
    {
      // yes, delete the editors layer
      getChart().getLayers().removeThisLayer(editorLayer);
    }

    // trigger a redraw of the plot
    getChart().getLayers().fireExtended();

    // get rid of the drag button processing
    _dragger.setSelected(false);
    _dragger.doClose();

    // ditch our local data
    _myPath = null;
    _safePath = null;
    _myPlotter = null;
    _myList = null;
    _editor = null;
    _subPanel = null;
    _theParent = null;

    // and close the parent
    super.doClose();
  }

  /**
   * ************************************************************
   * embedded class which shows our list of points
   * *************************************************************
   */
  protected class PathPlotter extends BaseLayer
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
     * paint this list to the canvas
     */
    public void paint(final CanvasType dest)
    {
      Point lastPoint = null;

      final Enumeration<Editable> enumer = this.elements();
      while (enumer.hasMoreElements())
      {
        // get the next location
        final LocationPlotter lp = (LocationPlotter) enumer.nextElement();

        // plot it
        lp.paint(dest);

        // get it's location
        final Point thisPoint = lp.toScreen(dest);

        // have we already plotted the first point?
        if (lastPoint != null)
        {
          dest.drawLine(lastPoint.x, lastPoint.y, thisPoint.x, thisPoint.y);
        }

        // remember the last point
        lastPoint = new Point(thisPoint);
      }

    }

    public void update()
    {
      // clear out the elements
      this.removeAllElements();

      // go through the path
      final int len = getPath().size();

      for (int i = 0; i < len; i++)
      {
        // add the new point
        this.add(new LocationPlotter(i));
      }
    }

  }

  /**
   * ************************************************************
   * embedded class which plots a single world location
   * *************************************************************
   */
  protected class LocationPlotter implements Plottable, Moveable
  {
    int _myIndex;

    public LocationPlotter(final int index)
    {
      _myIndex = index;
    }

    /**
     * apply the necessary movement (during drag)
     */
    public void dragBy(final WorldVector wv)
    {
      //
    }

  	public int compareTo(final Plottable arg0)
  	{
  		final Plottable other = (Plottable) arg0;
  		return this.getName().compareTo(other.getName());
  	}
    /**
     * and apply the final movement
     */
    public void doMove(final WorldLocation start, final WorldLocation end)
    {
      if (getLocation() != null)
      {

        // retrieve the depth
        final double depth = getLocation().getDepth();

        // paste in the lat/long
        getLocation().copy(end);

        // re-apply the depth
        getLocation().setDepth(depth);
      }
    }


    /**
     * paint this object to the specified canvas
     */
    public void paint(final CanvasType dest)
    {
      final Point pt = toScreen(dest);
      dest.setColor(Color.yellow);
      dest.drawRect(pt.x - 1, pt.y - 1, 3, 3);

      dest.drawText("" + (_myIndex + 1), pt.x + 4, pt.y - 2);
    }

    /**
     * get the current origin of this item (to support undo operation)
     */
    public WorldLocation getLocation()
    {
      return getPath().getLocationAt(_myIndex);
    }

    /**
     * get the screen location of this point
     */
    protected Point toScreen(final CanvasType dest)
    {
      return dest.toScreen(getLocation());
    }

    /**
     * find the data area occupied by this item
     */
    public WorldArea getBounds()
    {
      final WorldLocation loc = getLocation();
      return new WorldArea(loc, loc);
    }

    /**
     * it this item currently visible?
     */
    public boolean getVisible()
    {
      return true;
    }

    /**
     * set the visibility of this item
     */
    public void setVisible(final boolean val)
    {
      //
    }

    /**
     * Determine how far away we are from this point.
     * or return null if it can't be calculated
     */
    public double rangeFrom(final WorldLocation other)
    {
      final WorldLocation loc = getPath().getLocationAt(_myIndex);
      return loc.rangeFrom(other);
    }

    /**
     * the name of this object
     *
     * @return the name of this editable object
     */
    public String getName()
    {
      return "Point:" + _myIndex;
    }

    /**
     * whether there is any edit information for this item
     * this is a convenience function to save creating the EditorType data
     * first
     *
     * @return yes/no
     */
    public boolean hasEditor()
    {
      return false;
    }

    /**
     * get the editor for this item
     *
     * @return the BeanInfo data for this editable object
     */
    public Editable.EditorType getInfo()
    {
      return null;
    }
  }

  /**
   * ************************************************************
   * toggle button which let's us drag moveable items on the plot
   * *************************************************************
   */
  private class DragButton extends javax.swing.JToggleButton implements ActionListener
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final MoveableDragger _myDragger;

    public DragButton(final PlainChart chart, final ToolParent parent)
    {
      super("Drag", false);

      // create our dragger, but sub-class the area-selected method so that we can
      // update our GUI after a position has been moved
      _myDragger = new MoveableDragger(chart, parent, "Drag")
      {
        /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void areaSelected(final MWC.GenericData.WorldLocation theLocation, final Point thePoint)
        {
          super.areaSelected(theLocation, thePoint);
          updateData();
        }

      };

      this.addActionListener(this);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      if (this.isSelected())
      {
        // we've been pressed, take control
        _myDragger.execute();
      }
      else
      {
        // we've been released, replace the controls
        _myDragger.finish();
      }
    }

    public void doClose()
    {
      this.removeActionListener(this);
      // ensure that we stop listening to the chart
      _myDragger.finish();
    }
  }

  /**
   * **************************************************************
   * private class which takes a WorldLocation and integer index, and produces
   * string which starts with index
   * **************************************************************
   */
  private class WorldLocationHolder extends WorldLocation
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final int _myIndex;

    public WorldLocationHolder(final WorldLocation location, final int index)
    {
      super(location);
      _myIndex = index;
    }

    public String toString()
    {
      return "" + _myIndex + ": " + super.toString();
    }
  }


}
