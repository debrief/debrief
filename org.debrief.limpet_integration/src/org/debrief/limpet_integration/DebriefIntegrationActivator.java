package org.debrief.limpet_integration;

import org.debrief.limpet_integration.adapters.DebriefLimpetAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.property_support.RightClickSupport;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider;
import org.osgi.framework.BundleContext;

import ui.LimpetImageHelper;

/**
 * The activator class controls the plug-in life cycle
 */
public class DebriefIntegrationActivator extends AbstractUIPlugin implements
    IStartup
{

  // The plug-in ID
  public static final String PLUGIN_ID = "org.debrief.limpet_integration"; //$NON-NLS-1$

  // The shared instance
  private static DebriefIntegrationActivator plugin;

  /**
   * The constructor
   */
  public DebriefIntegrationActivator()
  {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
   * )
   */
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
    plugin = this;

    CoreViewLabelProvider.addImageHelper(new LimpetImageHelper());

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
   * )
   */
  public void stop(BundleContext context) throws Exception
  {
    plugin = null;
    super.stop(context);
  }

  /**
   * Returns the shared instance
   * 
   * @return the shared instance
   */
  public static DebriefIntegrationActivator getDefault()
  {
    return plugin;
  }

  /**
   * Returns an image descriptor for the image file at the given plug-in
   * relative path
   * 
   * @param path
   *          the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(String path)
  {
    return imageDescriptorFromPlugin(PLUGIN_ID, path);
  }

  @Override
  public void earlyStartup()
  {
    // give the LayerManager our image creator.
    CoreViewLabelProvider.addImageHelper(new LimpetImageHelper());

    // also declare our right-click editor
    DebriefLimpetMenuGenerator genny = new DebriefLimpetMenuGenerator();
    RightClickSupport
        .addAlternateRightClickGenerator(genny);
    
    // also give Debrief an adapter factory
    genny.addAdapterFactory(new DebriefLimpetAdapterFactory());

  }
}
