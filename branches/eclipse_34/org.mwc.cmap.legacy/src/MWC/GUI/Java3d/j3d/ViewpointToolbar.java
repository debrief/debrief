/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 * This software comes with the standard NO WARRANTY disclaimer for any
 * purpose. Use it at your own risk. If there's a problem you get to fix it.
 *
 ****************************************************************************/

package MWC.GUI.Java3d.j3d;

// Standard imports
import  javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


/**
 * A toolbar for all view manipulation commands that offers
 * convenient and common code.
 * <p>
 *
 * This toolbar uses images for the button icons rather than text. These are
 * the images used. The path is found relative to the classpath.
 * <ul>
 * <li>Pan:  images/navigation/ButtonForward.gif</li>
 * <li>Tilt: images/navigation/ButtonBack.gif</li>
 * <li>Walk: images/navigation/ButtonHome.gif</li>
 * </ul>
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class ViewpointToolbar extends JPanel
    implements ActionListener, ItemListener
{
    /** The name of the file for the pan cursor image */
//    private static final String NEXT_BUTTON = "images/zoomin.gif";

    /** The name of the file for the tilt cursor image */
//    private static final String PREV_BUTTON = "images/zoomout.gif";

    /** The name of the file for the walk cursor image */
//    private static final String UP_BUTTON = "images/navigation/ButtonHome.gif";


    /** An observer for viewpoint state change information */
    protected ViewpointSelectionListener viewpointListener;

    /** Combo box holding the list of viewpoint data */
    private JComboBox viewpoints;

    /** The model used by the combo box to handle viewpoint data */
    private DefaultComboBoxModel viewpointModel;

//    /** Button representing a move to the next viewpoint */
//    private JButton nextViewpoint;
//
//    /** Button representing a move to the next viewpoint */
//    private JButton prevViewpoint;

    /** Button representing a move to straighten up a viewpoint (lost user) */
  //  private JButton homeViewpoint;

    /**
     * Create a new horizontal viewpoint toolbar with an empty list of
     * viewpoints.
     */
    public ViewpointToolbar()
    {
        this(null);
    }

  /**
   * Create a new viewpoint toolbar that has the given list of viewpoints
   * to be displayed. List may be null and may be changed at a later date.
   * The buttons will be laid out horizontally
   *
   * @param vps The list of viewpoints to use
   */
  public ViewpointToolbar(ViewpointData[] vps)
  {
    if(vps != null)
      viewpointModel = new DefaultComboBoxModel(vps);
    else
      viewpointModel = new DefaultComboBoxModel();

    viewpoints = new JComboBox(viewpointModel);

    // stop this being a lightweight component, since we want it
    // to show over the Canvas3d which is an AWT component
    viewpoints.setLightWeightPopupEnabled(false);

//    viewpoints.setRenderer(new ViewpointCellRenderer(10));
    viewpoints.setRenderer(new ViewpointCellRenderer());
    viewpoints.setEditable(false);
    viewpoints.setMaximumRowCount(10);
    viewpoints.setMinimumSize(new Dimension(60,10)); // yuck!
    viewpoints.setToolTipText("To select another preset view");
    viewpoints.addItemListener(this);

//        Icon icon = ImageLoader.loadIcon(NEXT_BUTTON);
//        nextViewpoint = new JButton(icon);
//        nextViewpoint.setMargin(new Insets(0,0,0,0));
//        nextViewpoint.setToolTipText("Next Viewpoint");
//        nextViewpoint.addActionListener(this);

//        icon = ImageLoader.loadIcon(PREV_BUTTON);
//        prevViewpoint = new JButton(icon);
//        prevViewpoint.setMargin(new Insets(0,0,0,0));
//        prevViewpoint.setToolTipText("Previous Viewpoint");
//        prevViewpoint.addActionListener(this);

//        icon = ImageLoader.loadIcon(UP_BUTTON);
//        homeViewpoint = new JButton(icon);
//        homeViewpoint.setMargin(new Insets(0,0,0,0));
//        homeViewpoint.addActionListener(this);
//        homeViewpoint.setToolTipText("Return to current Viewpoint");

    //     JPanel p1 = new JPanel(new GridLayout(1, 2));
    //   p1.add(nextViewpoint);
    //     p1.add(homeViewpoint);

    setLayout(new BorderLayout());

    //     add(prevViewpoint, BorderLayout.WEST);
    add(viewpoints, BorderLayout.CENTER);
    //    add(p1, BorderLayout.EAST);
  }

    //----------------------------------------------------------
    // Local public methods
    //----------------------------------------------------------

    /**
     * Set the listener for viewpoint change notifications. By setting
     * a value of null it will clear the currently set instance
     *
     * @param l The listener to use for change updates
     */
    public void setViewpointSelectionListener(ViewpointSelectionListener l)
    {
        viewpointListener = l;
    }

    /**
     * Set the viewpoint list to the given array. It removes the current list
     * and replaces it with this list.
     *
     * @param vp The list of viewpoints to use
     */
    public void setViewpoints(ViewpointData[] vp)
    {
        viewpointModel.removeAllElements();

        if(vp != null)
        {
            for(int i = 0; i < vp.length; i++)
                viewpointModel.addElement(vp[i]);

            viewpoints.setSelectedIndex(0);
        }

    }

    /**
     * Add the given viewpoint to the end of the list of available viewpoints.
     *
     * @param vp The data for the new viewpoint
     */
    public void appendViewpoint(ViewpointData vp)
    {
        viewpointModel.addElement(vp);

        // If this is the first item in an empty list, make it selected
        if(viewpoints.getItemCount() == 1)
            viewpoints.setSelectedIndex(0);
    }

    /**
     * Insert the viewpoint at the given position in the list
     *
     * @param index The position to insert it into the list
     * @param vp The data for the new viewpoint
     */
    public void insertViewpoint(int index, ViewpointData vp)
    {
        viewpointModel.insertElementAt(vp, index);
    }

    /**
     * Remove the viewpoint from the list. If the viewpoint is not known, the
     * request is silently ignored
     *
     * @param vp The data that is to be removed
     */
    public void removeViewpoint(ViewpointData vp)
    {
        viewpointModel.removeElement(vp);
    }

    /**
     * Select the given viewpoint in the display
     *
     * @param vp The data that is to be selected
     */
    public void selectViewpoint(ViewpointData vp)
    {
        viewpoints.setSelectedItem(vp);
    }

    /**
     * get the currently selected view
     */
  public ViewpointData getCurrent()
  {
    return (ViewpointData)viewpoints.getSelectedItem();
  }

  /** get the current model
   *
   */
  public DefaultComboBoxModel getViewpointModel()
  {
    return viewpointModel;
  }

  //----------------------------------------------------------
    // Methods required by the ActionListener
    //----------------------------------------------------------

    /**
     * Process an action event on one of the buttons.
     *
     * @param evt The event that caused this method to be called
     */
    public void actionPerformed(ActionEvent evt)
    {
        Object src = evt.getSource();
        int index = viewpoints.getSelectedIndex();

        // Use the index setting here. This will cause the item event to be
        // created and sent so we always use that to send out the values
        // below.
//        if(src == nextViewpoint)
//        {
//            index++;
//
//            if(index >= viewpoints.getItemCount())
//                index = 0;
//
//            viewpoints.setSelectedIndex(index);
//        }
//        else if(src == prevViewpoint)
//        {
//            index--;
//
//            if(index < 0)
//                index = viewpoints.getItemCount() - 1;
//
//            viewpoints.setSelectedIndex(index);
//        }
//        else if(src == homeViewpoint)
//        {
//          ViewpointData data = (ViewpointData)viewpoints.getSelectedItem();
//
//          if(viewpointListener != null)
//              viewpointListener.viewpointSelected(data);
//        }
    }

    //----------------------------------------------------------
    // Methods required by the ItemListener
    //----------------------------------------------------------

  /** select the first item in the list
   *
   */
  public void selectFirstView()
  {
    ViewpointData data = (ViewpointData)viewpoints.getItemAt(0);

    if(viewpointListener != null)
        viewpointListener.viewpointSelected(data);
  }

    /**
     * Listen for item changing events in the comboBox selection
     *
     * @param evt The event that caused this method to be called
     */
    public void itemStateChanged(ItemEvent evt)
    {
        if(evt.getStateChange() != ItemEvent.SELECTED)
            return;

        ViewpointData data = (ViewpointData)viewpoints.getSelectedItem();

        if(viewpointListener != null)
            viewpointListener.viewpointSelected(data);
    }

    //----------------------------------------------------------
    // Methods Overriding Component
    //----------------------------------------------------------

    /**
     * Set the panel enabled or disabled. Overridden to make sure the base
     * components are properly handled.
     *
     * @param enabled true if this component is enabled
     */
    public void setEnabled(boolean enabled)
    {
        viewpoints.setEnabled(enabled);
//        nextViewpoint.setEnabled(enabled);
//        prevViewpoint.setEnabled(enabled);
  //      homeViewpoint.setEnabled(enabled);
    }
}
