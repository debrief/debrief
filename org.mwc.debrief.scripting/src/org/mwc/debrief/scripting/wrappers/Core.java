package org.mwc.debrief.scripting.wrappers;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ease.modules.ScriptParameter;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.core.editors.PlotEditor;

import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import junit.framework.TestCase;

public class Core
{

  public static class TestCore extends TestCase
  {
    static final int testRed = 200;
    static final int testGreen = 210;
    static final int testBlue = 220;

    static final int testFontSize = 10;
    static final int testFontStyle = Font.PLAIN;
    static final String testFontName = "Serif.plain";

    static final int durationValue = 12;
    static final int durationUnits = Duration.HOURS;

    static final long timeLong = 2000000L;

    public void testCreateColor()
    {
      final Color color = createColor(testRed, testGreen, testBlue);
      assertEquals("Testing Red Color", testRed, color.getRed());
      assertEquals("Testing Green Color", testGreen, color.getGreen());
      assertEquals("Testing Blue Color", testBlue, color.getBlue());
    }

    public void testCreateDate()
    {
      final HiResDate date = createDate(timeLong);
      assertEquals("Testing Date", timeLong, date.getDate().getTime());
    }

    public void testCreateDuration()
    {
      final Duration duration = createDuration(durationValue, durationUnits);
      assertEquals("Testing Duration Value with the given units", durationValue,
          duration.getValueIn(durationUnits), 1e-5);
    }

    public void testCreateFont()
    {
      final Font font = createFont(testFontName, testFontStyle, testFontSize);
      assertEquals("Testing Font Name", testFontName, font.getFontName());
      assertEquals("Testing Font Style", testFontStyle, font.getStyle());
      assertEquals("Testing Font Size", testFontSize, font.getSize());
    }
  }

  /**
   * Creates an opaque sRGB color with the specified red, green, and blue values in the range (0 -
   * 255). The actual color used in rendering depends on finding the best match given the color
   * space available for a given output device. Alpha is defaulted to 255.
   *
   * @param red
   *          the red component
   * @param green
   *          the green component
   * @param blue
   *          the blue component
   * @return
   */
  public static Color createColor(final int red, final int green,
      final int blue)
  {
    return new Color(red, green, blue);
  }

  /*
   * Here is how to provide default value: @ScriptParameter(defaultValue="-1")
   */
  public static HiResDate createDate(final long date)
  {
    return new HiResDate(date);
  }

  public static Duration createDuration(final int value, final int units)
  {
    return new Duration(value, units);
  }

  public static Font createFont(final String fontName, final int style,
      final int size)
  {
    return new Font(fontName, style, size);
  }

  public static Plot getActivePlot()
  {
    return getPlot(null);
  }

  public static Plot getPlot(@ScriptParameter(
      defaultValue = "unset") final String filename)
  {
    final IWorkbench workbench = PlatformUI.getWorkbench();
    final IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
    for (final IWorkbenchWindow window : windows)
    {
      if (window != null)
      {
        final IWorkbenchPage[] pages = window.getPages();
        for (final IWorkbenchPage page : pages)
        {
          final IEditorReference[] editors = page.getEditorReferences();
          for (final IEditorReference editor : editors)
          {
            final String descriptor = editor.getId();
            if (filename == null || "unset".equals(filename) || filename.equals(
                editor.getName()))
            {
              // ok, we either didn't have an editor name, or this matches
              if ("org.mwc.debrief.PlotEditor".equals(descriptor)
                  || "org.mwc.debrief.TrackEditor".equals(descriptor))
              {
                final IEditorPart instance = editor.getEditor(false);
                if (instance != null)
                {
                  return new Plot((PlotEditor) instance);
                }
              }
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * helper application to help track activation/closing of new plots
   */
  private PartMonitor _partMonitor;

  private TimeProvider _timeProvider;

  public Core()
  {
    System.out.println("About to start listening");
    listenToMyParts();
  }

  protected void fireNewTime(final HiResDate date)
  {
    // get broker service
    final IEventBroker broker = PlatformUI.getWorkbench().getService(
        IEventBroker.class);

    // fire the event, if we have a broker
    if (broker != null)
    {
      final String EVENT_NAME = "info/debrief/newTime";

      // tell them about new time
      broker.post(EVENT_NAME, date.getDate().getTime());
    }
    else
    {
      System.err.println("Could not retrieve Platform broker");
    }
  }

  private void listenToMyParts()
  {
    if (_partMonitor != null)
    {
      return;
    }

    final Plot dEditor = getPlot(null);
    if (dEditor == null)
    {
      System.err.println("Couldn't get editor");
      return;
    }

    final PlotEditor editor = dEditor.getPlot();
    final IWorkbenchWindow window = editor.getSite().getPage()
        .getWorkbenchWindow();

    if (window == null)
    {
      System.err.println("Can't retrieve workbench window");
      return;
    }

    _partMonitor = new PartMonitor(window.getPartService());

    final PropertyChangeListener listener = new PropertyChangeListener()
    {

      @Override
      public void propertyChange(final PropertyChangeEvent evt)
      {
        final HiResDate date = (HiResDate) evt.getNewValue();
        fireNewTime(date);
      }
    };

    // Listen for anyone that can provide time
    _partMonitor.addPartListener(TimeProvider.class, PartMonitor.ACTIVATED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final TimeProvider provider = (TimeProvider) part;

            if (!provider.equals(_timeProvider))
            {
              // changed.
              if (_timeProvider != null)
              {
                _timeProvider.removeListener(listener,
                    TimeProvider.TIME_CHANGED_PROPERTY_NAME);
              }

              _timeProvider = provider;
              _timeProvider.addListener(listener,
                  TimeProvider.TIME_CHANGED_PROPERTY_NAME);
            }
          }
        });

    // Listen for anyone that can provide time
    _partMonitor.addPartListener(TimeProvider.class, PartMonitor.CLOSED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final TimeProvider provider = (TimeProvider) part;

            if (provider.equals(_timeProvider))
            {
              // changed.
              _timeProvider.removeListener(listener,
                  TimeProvider.TIME_CHANGED_PROPERTY_NAME);
            }
          }
        });

  }
}
