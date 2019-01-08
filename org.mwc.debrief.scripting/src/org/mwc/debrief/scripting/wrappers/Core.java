package org.mwc.debrief.scripting.wrappers;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ease.modules.ScriptParameter;
import org.eclipse.ease.modules.WrapToScript;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.core.editors.PlotEditor;

import MWC.GUI.TabPanel.OS;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import junit.framework.TestCase;

/**
 * Core class that exposes methods related with the plot and basic creators
 * 
 * @author Ian Mayo
 *
 */
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
    static final String MACOS_testFontName = "Dialog";

    static final int durationValue = 12;
    static final int durationUnits = Duration.HOURS;

    static final long timeLong = 2000000L;

    public void testCreateColor()
    {
      final Color color = createColorRGB(testRed, testGreen, testBlue);
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
      if (OS.isMacintosh())
      {
        // note: the mac mangles the font, since the one we're
        // asking for isn't available, so it uses a similar one
        assertEquals("Testing Font Name", MACOS_testFontName, font
            .getFontName());
      }
      else
      {
        assertEquals("Testing Font Name", testFontName, font.getFontName());
      }
      assertEquals("Testing Font Style", testFontStyle, font.getStyle());
      assertEquals("Testing Font Size", testFontSize, font.getSize());
    }

    public void testCreateDateCalendarFormat()
    {
      final Calendar now = Calendar.getInstance();
      final HiResDate created = createDateCalendarFormat(now.get(Calendar.YEAR),
          now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), now.get(
              Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(
                  Calendar.SECOND));

      final Calendar calendarCreated = Calendar.getInstance();
      calendarCreated.setTime(created.getDate());
      assertEquals("Correct date for testCreateDateCalendarFormat", now,
          calendarCreated);
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
   * @return a color object <br />
   *         // @type java.awt.Color
   * 
   */
  @WrapToScript
  public static Color createColorRGB(final int red, final int green,
      final int blue)
  {
    return new Color(red, green, blue);
  }

  /**
   * create a new date, using indicated millis since epoch
   * 
   * @param date
   *          elapsed millis since epoch (1st Jan 1970)
   * @return // @type MWC.GenericData.HiResDate
   * 
   */
  @WrapToScript
  public static HiResDate createDate(final long date)
  {
    return new HiResDate(date);
  }

  /**
   * create a date from this String value
   * 
   * @param date
   *          the string to process
   * @param formatter
   *          the format for the string
   * @return // @type MWC.GenericData.HiResDate
   * @throws ParseException
   *           if the parsing fails
   */
  @WrapToScript
  public static HiResDate createDateFromString(final String date,
      final SimpleDateFormat formatter) throws ParseException
  {
    return new HiResDate(formatter.parse(date));
  }

  /**
   * Returns a High Resolution date from values given in the calendar format
   * 
   * @param year
   *          the value used to set the YEAR calendar field.
   * @param month
   *          the value used to set the MONTH calendar field. Month value is 0-based. e.g., 0 for
   *          January.
   * @param date
   *          Field number to indicate the day of the month. This is a synonym for DATE. The first
   *          day of the month has value 1.
   * @param hourOfDay
   *          Field number to indicate the hour of the morning or afternoon. HOUR is used for the
   *          12-hour clock (0 - 11). Noon and midnight are represented by 0, not by 12. E.g., at
   *          10:04:15.250 PM the HOUR is 10.
   * @param minute
   *          Field number for get and set indicating the minute within the hour. E.g., at
   *          10:04:15.250 PM the MINUTE is 4
   * @param second
   *          Field number for get and set indicating the second within the minute. E.g., at
   *          10:04:15.250 PM the SECOND is 15.
   * @return // @type MWC.GenericData.HiResDate
   */
  @WrapToScript
  public static HiResDate createDateCalendarFormat(final int year,
      final int month, final int date, final int hourOfDay, final int minute,
      final int second)
  {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month, date, hourOfDay, minute, second);
    return new HiResDate(calendar.getTime());
  }

  /**
   * microsecond units
   * 
   */
  @WrapToScript
  static public final int DUR_MICROSECONDS = 0;

  /**
   * millis units
   * 
   */
  @WrapToScript
  static public final int DUR_MILLISECONDS = 1;

  /**
   * seconds units
   * 
   */
  @WrapToScript
  static public final int DUR_SECONDS = 2;

  /**
   * minute units
   * 
   */
  @WrapToScript
  static public final int DUR_MINUTES = 3;

  /**
   * hour units
   * 
   */
  @WrapToScript
  static public final int DUR_HOURS = 4;

  /**
   * day units
   * 
   */
  @WrapToScript
  static public final int DUR_DAYS = 5;

  /**
   * Function that creates a duration given a value and the unit
   * 
   * @see MWC.GenericData.Duration
   * @param value
   *          Value of the duration.
   * @param units
   *          Unit of the duration as an integer. Options available: MICROSECONDS = 0, MILLISECONDS
   *          = 1, SECONDS = 2, MINUTES = 3, HOURS = 4, DAYS = 5
   * @return // @type MWC.GenericData.Duration duration
   */
  @WrapToScript
  public static Duration createDuration(final int value, final int units)
  {
    return new Duration(value, units);
  }

  /**
   * Function that creates a font object given a font name as string, an style and size.
   * 
   * @see java.awt.Font#Font(String, int, int)
   * @param fontName
   *          Font name as String. For example: "Serif.plain"
   * @param style
   *          Style of the font. For example: java.awt.Font.PLAIN
   * @param size
   *          Size of the font created.
   * @return // @type java.awt.Font
   */
  @WrapToScript
  public static Font createFont(final String fontName, final int style,
      final int size)
  {
    return new Font(fontName, style, size);
  }

  /**
   * Function that returns the active plot (Editor).
   * 
   * @see org.mwc.debrief.scripting.wrappers.Plot
   * @return // @type org.mwc.debrief.scripting.wrappers.Plot
   */
  @WrapToScript
  public static Plot getActivePlot()
  {
    return getPlot(null);
  }

  /**
   * Method that returns a plot given its name.
   * 
   * @see org.mwc.debrief.scripting.wrappers.Plot
   * @param filename
   *          Name of the plot editor.
   * @return // @type org.mwc.debrief.scripting.wrappers.Plot
   */
  @WrapToScript
  public static Plot getPlot(@ScriptParameter(
      defaultValue = "unset") final String filename)
  {
    final Plot answer[] = new Plot[1];
    Display.getDefault().syncExec(new Runnable()
    {
      @Override
      public void run()
      {

        final IWorkbench workbench = PlatformUI.getWorkbench();

        // special case: if no filename is provided, and active editor is valid,
        // then we'll return it
        if ("unset".equals(filename) || filename == null)
        {
          final IWorkbenchWindow window = PlatformUI.getWorkbench()
              .getActiveWorkbenchWindow();
          final IWorkbenchPage page = window.getActivePage();
          final IEditorPart editor = page.getActiveEditor();
          final IEditorSite editorSite = editor.getEditorSite();
          final String id = editorSite.getId();
          if (validDescriptor(id))
          {
            answer[0] = new Plot((PlotEditor) editor);
            return;
          }
        }

        // ok, loop through, and find the first editor that either
        // matches the filenma,e
        final IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
        for (final IWorkbenchWindow window : windows)
        {
          if (window != null)
          {
            final IWorkbenchPage[] pages = window.getPages();
            for (final IWorkbenchPage page : pages)
            {
              final IEditorReference[] editors = page.getEditorReferences();

              // first check for matching filename
              if (filename != null)
              {
                for (final IEditorReference editor : editors)
                {
                  final String descriptor = editor.getId();
                  if (filename.equals(editor.getName()))
                  {
                    // ok, we either didn't have an editor name, or this matches
                    if (validDescriptor(descriptor))
                    {
                      final IEditorPart instance = editor.getEditor(false);
                      if (instance != null)
                      {
                        answer[0] = new Plot((PlotEditor) instance);
                        return;
                      }
                    }
                  }
                }
              }

              // still here? this time check for suitable editor type
              for (final IEditorReference editor : editors)
              {
                final String descriptor = editor.getId();
                if (filename == null || "unset".equals(filename))
                {
                  // ok, we either didn't have an editor name, or this matches
                  if (validDescriptor(descriptor))
                  {
                    final IEditorPart instance = editor.getEditor(false);
                    if (instance != null)
                    {
                      answer[0] = new Plot((PlotEditor) instance);
                      return;
                    }
                  }
                }
              }
            }
          }
        }
      }
    });
    return answer[0];
  }

  private static boolean validDescriptor(final String descriptor)
  {
    return "org.mwc.debrief.PlotEditor".equals(descriptor)
        || "org.mwc.debrief.TrackEditor".equals(descriptor);
  }

  /**
   * helper application to help track activation/closing of new plots
   */
  private PartMonitor _partMonitor;

  private TimeProvider _timeProvider;

  /**
   * Dummy constructor.
   */
  public Core()
  {
    listenToMyParts();
  }

  /**
   * set a new time
   * 
   * @param date
   *          the new time to jump to
   */
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
