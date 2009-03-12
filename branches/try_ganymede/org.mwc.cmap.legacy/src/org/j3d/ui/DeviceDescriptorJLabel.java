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
import javax.swing.JLabel;

// Application specific imports
import org.j3d.util.device.DeviceDescriptor;

/**
 * A derived version of JLabel that represents any generic
 * {@link org.j3d.util.device.DeviceDescriptor}
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class DeviceDescriptorJLabel extends JLabel
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * Construct an instance of the label based on the given device
   * description.
   *
   * @param fld The file loader description to base the filter on
   */
  public DeviceDescriptorJLabel(DeviceDescriptor dd)
  {
      setText(dd.getName());
      setToolTipText(dd.getDescription());
  }
}

