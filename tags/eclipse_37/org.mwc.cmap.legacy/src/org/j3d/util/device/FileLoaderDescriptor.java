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
 * An descriptor class for file loaders.
 * <P>
 * A single file loader instance is used to represent a particular file
 * format type. The descriptor assumes that there is only one file
 * extension and mime type for a given file format type. If there are more
 * than one, it will not work correctly at this point in time.
 *
 * @version $revision$
 */
public class FileLoaderDescriptor extends DeviceDescriptor
{
  /** The mime type this loader can handle */
  private final String content_type;

  /** The file extension associated with this file loader */
  private final String file_extension;

  /**
   * Construct an instance of this class with the given information about
   * the name, description and class file.
   *
   * @param name The name of the device
   * @param desc A description of the device
   * @param cls The fully qualified name of the class file
   * @param type The mime type for this loader
   * @param ext The file extension associated with this type
   */
  FileLoaderDescriptor(String name,
                       String desc,
                       String cls,
                       String type,
                       String ext)
  {
    super(name, desc, cls);

    content_type = type;
    file_extension = ext;
  }

  /**
   * Get the MIME type that this loader can handle.
   *
   * @return The mime type as a string
   */
  public String getContentType()
  {
    return content_type;
  }

  /**
   * Get the file extension associated with the file type that this loader
   * can handle. This is useful for putting in FileOpen dialogs and the
   * like.
   *
   * @return The extension associated with this file type
   */
  public String getExtension()
  {
    return file_extension;
  }
}
