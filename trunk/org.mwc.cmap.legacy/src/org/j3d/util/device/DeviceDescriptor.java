/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.util.device;

// Standard imports
// none

// Application specific imports
// none

/**
 * An abstract descriptor class for holding information about the different
 * device types.
 */
public abstract class DeviceDescriptor
{
  /** The name of the device */
  private final String name;

  /** A descriptive string of the device and it's capabilities */
  private final String desc;

  /** The fully qualified class name of the device */
  private final String class_name;

  /**
   * The device that this descriptor represents. If the reference is null
   * then it has not been loaded yet.
   */
  private Object device;

  /**
   * Construct an instance of this class with the given information about
   * the name, description and class file.
   *
   * @param name The name of the device
   * @param desc A description of the device
   * @param cls The fully qualified name of the class file
   */
  protected DeviceDescriptor(String name, String desc, String cls)
  {
    this.name = name;
    this.desc = desc;
    class_name = cls;
  }

  /**
   * Get the name of this device. It should be a one line string that is
   * not strictly formatted.
   *
   * @return The name of this class
   */
  public String getName()
  {
    return name;
  }

  /**
   * Get the description string of this device. It could be a multi-line
   * string containing copyright, author or any other random information.
   *
   * @return The description string for this device
   */
  public String getDescription()
  {
    return desc;
  }

  /**
   * Get the class file name of this device. Package private because we don't
   * want the general application writer to get hold of this information.
   *
   * @return The fully qualified class name
   */
  String getClassName()
  {
    return class_name;
  }

  /**
   * Set the device that this descriptor represents. By using a value of
   * null, it will clear the device.
   *
   * @param dev The device instance to set.
   */
  void setDevice(Object dev)
  {
    device = dev;
  }

  /**
   * Get the current device instance that this descriptor represents. If
   * an instance has not yet been created then this returns null.
   *
   * @return The current device instance
   */
  Object getDevice()
  {
    return device;
  }

  /**
   * Check for equality of these two classes. The equality check is based on
   * having the same qualified class name.
   *
   * @param o The object to compare against
   * @return true if these represent the same class
   */
  public boolean equals(Object o)
  {
    boolean ret_val = false;

    if(o instanceof DeviceDescriptor)
    {
      DeviceDescriptor dd = (DeviceDescriptor)o;
      ret_val = class_name.equals(dd.class_name);
    }

    return ret_val;
  }
}
