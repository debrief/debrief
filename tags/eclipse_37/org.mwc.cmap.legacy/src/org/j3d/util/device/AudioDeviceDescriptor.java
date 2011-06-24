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
 * An descriptor class for AudioDevices
 */
public class AudioDeviceDescriptor extends DeviceDescriptor
{
  /**
   * Construct an instance of this class with the given information about
   * the name, description and class file.
   *
   * @param name The name of the device
   * @param desc A description of the device
   * @param cls The fully qualified name of the class file
   */
  AudioDeviceDescriptor(String name, String desc, String cls)
  {
    super(name, desc, cls);
  }
}
