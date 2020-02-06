/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
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
