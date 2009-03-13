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
import java.io.File;
import javax.swing.filechooser.FileFilter;

// Application specific imports
import org.j3d.util.device.FileLoaderDescriptor;

/**
 * A file filter implementation so that you can grab files of the types
 * that correspond to the loaders available on the system.
 * <P>
 *
 * The class takes information from the filter and builds filtering
 * information from that. It does not maintain a reference to the
 * descriptor. The filter will accept matching files and also all directories
 * so that you can navigate the directory structure.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class LoaderFileFilter extends FileFilter
{
  /** The description of the file filter */
  private String description;

  /** The extension matching this loader type */
  private String extension;

  /** The descriptor this file is using */
  private FileLoaderDescriptor descriptor;

  /**
   * Construct an instance of the filter based on the given device
   * description.
   *
   * @param fld The file loader description to base the filter on
   */
  public LoaderFileFilter(FileLoaderDescriptor fld)
  {
    descriptor = fld;

    description = fld.getDescription();
    extension = fld.getExtension();

    description += " (*." + extension + ")";
  }

  /**
   * Decide whether to accept this file based on the filter type.
   *
   * @param f The file to test for suitability
   * @return true if the file passes the filter
   */
  public boolean accept(File f)
  {
    boolean ret_val = false;

    if(f.isDirectory())
      ret_val = true;
    else
    {
      String name = f.getName();
      int index = name.lastIndexOf('.');

      // Filename without an extension?
      if(index != -1)
      {
        String ext = name.substring(index + 1);

        ret_val = extension.equals(ext);
      }
    }

    return ret_val;
  }

  /**
   * Return a description string of the this filter (The file type)
   * supported by this filter.
   *
   * @return a String describing this filter
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Get the descriptor used by this file filter so that we can ask for the
   * needed loader later on.
   *
   * @return The descriptor this represents
   */
  public FileLoaderDescriptor getDescriptor()
  {
    return descriptor;
  }
}

