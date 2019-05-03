package org.mwc.debrief.scripting;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ScriptingActivator extends AbstractUIPlugin implements
    BundleActivator
{

  private static BundleContext context;

  static protected BundleContext getContext()
  {
    return context;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start(final BundleContext bundleContext) throws Exception
  {
    super.start(bundleContext);
    ScriptingActivator.context = bundleContext;
    System.out.println("ACTIVATOR STARTUP");
  }

  /*
   * (non-Javadoc)
   *
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(final BundleContext bundleContext) throws Exception
  {
    super.stop(bundleContext);
    ScriptingActivator.context = null;
  }

}
