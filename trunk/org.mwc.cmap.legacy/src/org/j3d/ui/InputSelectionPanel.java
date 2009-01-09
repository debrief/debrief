/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.ui;

// Standard imports
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;

import org.j3d.util.device.InputDeviceDescriptor;

/**
 * A file filter implementation so that you can grab files of the types
 * that correspond to the loaders available on the system.
 * <P>
 *
 * The class takes information from the filter and builds filtering
 * information from that. It does not maintain a reference to the
 * descriptor.
 * <P>
 *
 * The panel will operate in either a single or multiple select mode. Single
 * select only allows one input device to be selected at a time. Multiple
 * select allows the user to choose as many as they want.
 *
 * @version $Revision: 1.1.1.1 $
 */
public class InputSelectionPanel extends JPanel
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The mode we are running in. True if multiple select */
  private boolean multi = false;

  /**
   * Construct a blank instance of the panel. The panel operates in a single
   * selection mode.
   */
  public InputSelectionPanel()
  {
    super(new GridLayout(2, 1));
  }

  /**
   * Construct a blank instance of the panel that operates in the selected
   * mode.
   *
   * @param multi true if to run in multiple selection mode
   */
  public InputSelectionPanel(boolean multi)
  {
    this.multi = multi;
  }

  /**
   * Set the list of devices to show on the panel. This will clear the list
   * and start with new values. The list is presented in the order given. The
   * first item on the list gets automatically selected.
   *
   * @param devs The list of devices to add
   */
  @SuppressWarnings("unchecked")
	public void setDeviceList(List devs)
  {
    removeAll();
  }

  /**
   * Get the list of selected devices. If the panel is running in single mode
   * then the list is one item long.
   *
   * @return A list of the selected devices.
   */
  @SuppressWarnings("unchecked")
	public List getSelectedItems()
  {
	  return null;
  }

  /**
   * Get the selected item from the list. If the panel is in multi mode, this
   * will be the first selected item.
   *
   * @return The currently selected item
   */
  public InputDeviceDescriptor getSelectedItem()
  {
	return null;
  }

  /**
   * Check to see if the panel is running in multi mode.
   *
   * @return True if the panel is allowing multiple selections
   */
  public boolean isMultiMode()
  {
    return multi;
  }
}

