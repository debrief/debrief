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
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlPreferences;
import org.mwc.cmap.core.DataTypes.Temporal.TimeManager;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.core.editors.PlotEditor;
import org.mwc.debrief.scripting.wrappers.Layers.DLayers;

import MWC.Algorithms.PlainProjection;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class Core
{

  public static class DEditor
  {
    private final PlotEditor _editor;

    public DEditor(final PlotEditor editor)
    {
      _editor = editor;
    }

    public void fitToWindow()
    {
      _editor.getChart().rescale();
      getLayers().fireModified();
    }

    public WorldArea getArea()
    {
      if (_editor != null)
      {
        final PlainProjection proj = (PlainProjection) _editor.getAdapter(
            PlainProjection.class);
        return proj.getDataArea();
      }
      return null;
    }

    public WorldLocation getCentre()
    {
      final WorldArea area = getArea();
      if (area != null)
      {
        return area.getCentre();
      }
      else
      {
        return null;
      }
    }

    public DLayers getLayers()
    {
      if (_editor != null)
      {
        final MWC.GUI.Layers layers = (MWC.GUI.Layers) _editor.getAdapter(
            MWC.GUI.Layers.class);
        if (layers != null)
        {
          return new DLayers(layers);
        }
      }
      return null;

    }

    public HiResDate getTime()
    {
      final TimeProvider time = (TimeProvider) _editor.getAdapter(
          TimeProvider.class);
      return time.getTime();
    }

    public TimeControlPreferences getTimeControlPreferences()
    {
      if (_editor != null)
      {
        TimeControlPreferences timeControlPreferences =
            (TimeControlPreferences) _editor.getAdapter(
                TimeControlPreferences.class);
        if (timeControlPreferences != null)
        {
          return timeControlPreferences;
        }
      }
      return null;
    }

    public TimeManager getTimeManager()
    {
      if (_editor != null)
      {
        TimeManager timeManager = (TimeManager) _editor.getAdapter(
            ControllableTime.class);
        if (timeManager != null)
        {
          return timeManager;
        }
      }
      return null;
    }

    public Tote getTote()
    {
      if (_editor != null)
      {
        TrackManager trackManager = (TrackManager) _editor.getAdapter(
            TrackManager.class);
        if (trackManager != null)
        {
          return new Tote(trackManager);
        }
      }
      return null;
    }

    public Outline getOutline()
    {
      if (_editor != null)
      {
        IContentOutlinePage outline = (IContentOutlinePage) _editor.getAdapter(
            IContentOutlinePage.class);
        if (outline != null)
        {
          return new Outline(outline);
        }
      }
      return null;
    }

    public PlainProjection getMap()
    {
      if (_editor != null)
      {
        PlainProjection map = (PlainProjection) _editor.getAdapter(
            PlainProjection.class);
        if (map != null)
        {
          return map;
        }
      }
      return null;
    }
  }

  /*
   * Here is how to provide default value: @ScriptParameter(defaultValue="-1")
   */
  public static HiResDate createDate(final long date)
  {
    return new HiResDate(date);
  }

  public static Font createFont(final String fontName, final int style,
      final int size)
  {
    return new Font(fontName, style, size);
  }

  public static Duration createDuration(final int value, final int units)
  {
    return new Duration(value, units);
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
  public static Color getColor(final int red, final int green, final int blue)
  {
    return new Color(red, green, blue);
  }

  public static DEditor getEditor(@ScriptParameter(
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
                  return new DEditor((PlotEditor) instance);
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

    final DEditor dEditor = getEditor(null);
    if (dEditor == null)
    {
      System.err.println("Couldn't get editor");
      return;
    }

    final PlotEditor editor = dEditor._editor;
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
