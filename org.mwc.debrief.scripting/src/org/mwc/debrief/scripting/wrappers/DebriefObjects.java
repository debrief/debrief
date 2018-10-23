package org.mwc.debrief.scripting.wrappers;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ease.modules.ScriptParameter;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.core.editors.PlotEditor;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.OperateFunction;
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

public class DebriefObjects
{

  public DebriefObjects()
  {
    System.out.println("About to start listening");
    listenToMyParts();
  }

  @Override
  protected void finalize() throws Throwable
  {
    super.finalize();
    System.out.println("DISPOSE");
  }

  public static WorldLocation createLocation(double dLat, double dLong,
      double depth)
  {
    return new WorldLocation(dLat, dLong, depth);
  }

  public static ShapeWrapper createLine(WorldLocation startPt,
      WorldLocation endPt, String name, Color color)
  {
    return new ShapeWrapper(name, new LineShape(startPt, endPt, name),
        color, null);
  }

  public static ShapeWrapper createCircle(WorldLocation centre,
      WorldDistance radius, String name, Color color)
  {
    return new ShapeWrapper(name, new CircleShape(centre, radius, name),
        color, null);
  }

  public static WorldVector createVector(double distM, double bearingDegs)
  {
    return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(bearingDegs),
        new WorldDistance(distM, WorldDistance.METRES), null);
  }

  public static WorldVector createVectorKm(double distKm, double bearingDegs)
  {
    return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(bearingDegs),
        new WorldDistance(distKm, WorldDistance.KM), null);
  }

  public static FixWrapper createFix(HiResDate time, WorldLocation location,
      double courseRads, double speedYps)
  {
    Fix fix = new Fix(time, location, courseRads, speedYps);
    return new FixWrapper(fix);
  }

  public static Color getColor(int red, int green, int blue)
  {
    return new Color(red, green, blue);
  }

  public static DEditor getEditor(@ScriptParameter(
      defaultValue = "unset") String filename)
  {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
    for (IWorkbenchWindow window : windows)
    {
      if (window != null)
      {
        IWorkbenchPage[] pages = window.getPages();
        for (IWorkbenchPage page : pages)
        {
          IEditorReference[] editors = page.getEditorReferences();
          for (IEditorReference editor : editors)
          {
            String descriptor = editor.getId();
            if (filename == null || "unset".equals(filename) || filename.equals(
                editor.getName()))
            {
              // ok, we either didn't have an editor name, or this matches
              if ("org.mwc.debrief.TrackEditor".equals(descriptor))
              {
                return new DEditor((PlotEditor) editor.getEditor(true));
              }
            }
          }
        }
      }
    }
    return null;
  }

  /*
   * Here is how to provide default value: @ScriptParameter(defaultValue="-1")
   */
  public static HiResDate createDate(long date)
  {
    return new HiResDate(date);
  }

  public static class DEditor
  {
    private final PlotEditor _editor;

    public DEditor(PlotEditor editor)
    {
      _editor = editor;
    }

    public void fitToWindow()
    {
      _editor.getChart().rescale();
      getLayers().fireModified();
    }

    public DLayers getLayers()
    {
      if (_editor != null)
      {
        Layers layers = (Layers) _editor.getAdapter(Layers.class);
        if (layers != null)
        {
          return new DLayers(layers);
        }
      }
      return null;

    }

    public WorldArea getArea()
    {
      if (_editor != null)
      {
        PlainProjection proj = (PlainProjection) _editor.getAdapter(
            PlainProjection.class);
        return proj.getDataArea();
      }
      return null;
    }

    public HiResDate getCurrentTime()
    {
      TimeProvider time = (TimeProvider) _editor.getAdapter(TimeProvider.class);
      return time.getTime();
    }

    public WorldLocation getCentre()
    {
      WorldArea area = getArea();
      if (area != null)
      {
        return area.getCentre();
      }
      else
      {
        return null;
      }
    }

  }

  public static class DLayers
  {
    private final Layers _layers;

    public DLayers(final Layers layers)
    {
      _layers = layers;
    }

    public int size()
    {
      return _layers.size();
    }

    public LightweightTrackWrapper findTrack(String name)
    {
      Layer match = _layers.findLayer(name);
      if (match instanceof LightweightTrackWrapper)
      {
        return (LightweightTrackWrapper) match;
      }
      return null;
    }

    public LightweightTrackWrapper[] getTracks()
    {
      final ArrayList<LightweightTrackWrapper> items =
          new ArrayList<LightweightTrackWrapper>();

      OperateFunction function = new OperateFunction()
      {

        @Override
        public void operateOn(Editable item)
        {
          items.add((LightweightTrackWrapper) item);
        }
      };
      _layers.walkVisibleItems(LightweightTrackWrapper.class, function);

      return items.toArray(new LightweightTrackWrapper[]
      {null});
    }

    public Layer createLayer(String name)
    {
      // do we already have it?
      Layer newLayer = _layers.findLayer(name);
      if (newLayer == null)
      {
        newLayer = new BaseLayer();
        newLayer.setName(name);
        _layers.addThisLayer(newLayer);
      }
      return newLayer;
    }

    public Layer findLayer(String name)
    {
      return _layers.findLayer(name, true);
    }

    public void fireModified()
    {
      _layers.fireExtended();
    }

  }

  public LabelWrapper createLabel(WorldLocation location, Color color)
  {
    return new LabelWrapper("Name", location, color);
  }

  /**
   * helper application to help track activation/closing of new plots
   */
  private PartMonitor _partMonitor;

  private TimeProvider _timeProvider;

  private void listenToMyParts()
  {
    if (_partMonitor != null)
    {
      return;
    }

    DEditor dEditor = getEditor(null);
    if (dEditor == null)
    {
      System.err.println("Couldn't get editor");
      return;
    }

    PlotEditor editor = dEditor._editor;
    IWorkbenchWindow window = editor.getSite().getPage().getWorkbenchWindow();

    if (window == null)
      return;

    _partMonitor = new PartMonitor(window.getPartService());

    final PropertyChangeListener listener = new PropertyChangeListener()
    {

      @Override
      public void propertyChange(PropertyChangeEvent evt)
      {
        HiResDate date = (HiResDate) evt.getNewValue();
        fireNewTime(date);
      }
    };

    // Listen for anyone that can provide time
    _partMonitor.addPartListener(TimeProvider.class, PartMonitor.ACTIVATED,
        new PartMonitor.ICallback()
        {
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            TimeProvider provider = (TimeProvider) part;

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
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            TimeProvider provider = (TimeProvider) part;

            if (provider.equals(_timeProvider))
            {
              // changed.
              _timeProvider.removeListener(listener,
                  TimeProvider.TIME_CHANGED_PROPERTY_NAME);
            }
          }
        });

  }

  @Inject
  private IEventBroker broker;

  protected void fireNewTime(HiResDate date)
  {
    System.out.println("CAUGHT NEW TIME:" + date.getDate());

    // find scripts

    // find scripts that listen to NewTime event

    // tell them about new time
    final String EVENT_NAME = "info.debrief.newTime";

    // fire the event, if we have a broker
    if (broker != null)
    {
      broker.post(EVENT_NAME, date.getDate().getTime());
    }
  }
}
