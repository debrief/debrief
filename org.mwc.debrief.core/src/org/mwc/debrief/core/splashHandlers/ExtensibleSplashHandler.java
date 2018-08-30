
package org.mwc.debrief.core.splashHandlers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;
import org.osgi.framework.Bundle;

public class ExtensibleSplashHandler extends AbstractSplashHandler
{

  private static final String ABOUT_MAPPINGS = "$nl$/about.mappings"; //$NON-NLS-1$

  // read the product bundle version
  private static String[] loadMappings(final Bundle definingBundle)
  {
    final URL location = FileLocator.find(definingBundle, new Path(
        ABOUT_MAPPINGS), null);
    PropertyResourceBundle bundle = null;
    if (location != null)
    {
      try (InputStream is = location.openStream())
      {
        bundle = new PropertyResourceBundle(is);
      }
      catch (final IOException e)
      {
        bundle = null;
      }
    }

    final ArrayList<String> mappingsList = new ArrayList<>();
    if (bundle != null)
    {
      boolean found = true;
      int i = 0;
      while (found)
      {
        try
        {
          mappingsList.add(bundle.getString(Integer.toString(i)));
        }
        catch (final MissingResourceException e)
        {
          found = false;
        }
        i++;
      }
    }
    final String[] mappings = mappingsList.toArray(new String[mappingsList
        .size()]);
    return mappings;
  }

  private void configureUICompositeBuildBounds()
  {
    final String[] loadMappings = loadMappings(Platform.getProduct()
        .getDefiningBundle());
    final String qualifier = loadMappings[0] + "-" + loadMappings[1];
    final Label build = new Label(getSplash(), SWT.NONE);

    build.setText(qualifier);

    final int x_coord = getSplash().getSize().x - 120;
    final int y_coord = getSplash().getSize().y - 22;

    build.setBounds(x_coord, y_coord, 120, 22);
  }

  /**
   *
   */
  private void configureUISplash()
  {
    // Configure layout
    // GridLayout layout = new GridLayout(1, true);
    getSplash().setLayout(null);
    // Force shell to inherit the splash background
    getSplash().setBackgroundMode(SWT.INHERIT_DEFAULT);
  }

  /**
   *
   */
  private void createUI()
  {
    configureUICompositeBuildBounds();
  }

  /**
   *
   */
  private void doEventLoop()
  {
    final Shell splash = getSplash();
    if (!splash.getDisplay().readAndDispatch())
    {
      splash.getDisplay().sleep();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.splash.AbstractSplashHandler#init(org.eclipse.swt.widgets.Shell)
   */
  @Override
  public void init(final Shell splash)
  {
    // Store the shell
    super.init(splash);
    // Configure the shell layout
    configureUISplash();

    // Create UI
    createUI();
    // Enter event loop and prevent the RCP application from
    // loading until all work is done
    doEventLoop();
  }

}
