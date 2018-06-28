package info.limpet.stackedcharts.ui.editor;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

public class StackedchartsImages
{
  private StackedchartsImages()
  {
  }

  public final static String GEF_PATH = "icons/gef/"; //$NON-NLS-1$

  private final static ImageRegistry PLUGIN_REGISTRY = new ImageRegistry();

  private static ImageDescriptor create(String prefix, String name)
  {
    return ImageDescriptor.createFromURL(makeImageURL(prefix, name));
  }

  private static URL makeImageURL(String prefix, String name)
  {
    String path = "$nl$/" + prefix + name; //$NON-NLS-1$
    return FileLocator.find(Activator.getDefault().getBundle(), new Path(path),
        null);
  }

  public static Image getImage(ImageDescriptor desc)
  {
    String key = String.valueOf(desc.hashCode());
    Image image = PLUGIN_REGISTRY.get(key);
    if (image == null)
    {
      image = desc.createImage();
      PLUGIN_REGISTRY.put(key, image);
    }
    return image;
  }

  public static final ImageDescriptor DESC_ADD = create(GEF_PATH, "add.png"); //$NON-NLS-1$
  public static final ImageDescriptor DESC_AXIS = create(GEF_PATH, "axis.png"); //$NON-NLS-1$
  public static final ImageDescriptor DESC_CHART =
      create(GEF_PATH, "chart.png"); //$NON-NLS-1$
  public static final ImageDescriptor DESC_CHARTSET = create(GEF_PATH,
      "chartset.png"); //$NON-NLS-1$
  public static final ImageDescriptor DESC_SCATTERSET = create(GEF_PATH,
	      "scatterset.png"); //$NON-NLS-1$
  public static final ImageDescriptor DESC_DELETE = create(GEF_PATH,
      "delete.png"); //$NON-NLS-1$
  public static final ImageDescriptor DESC_PAINT =
      create(GEF_PATH, "paint.png"); //$NON-NLS-1$
  public static final ImageDescriptor DESC_DATASET = create(GEF_PATH,
      "dataset.png"); //$NON-NLS-1$ //FIXME UPDATE DATASET ICON

}
