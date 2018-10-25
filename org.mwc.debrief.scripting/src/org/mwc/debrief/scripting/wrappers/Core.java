package org.mwc.debrief.scripting.wrappers;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;

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

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.OperateFunction;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import junit.framework.TestCase;

public class Core
{

  public static class TestCore extends TestCase
  {
    public void testFindItem()
    {
      Layers layers = new Layers();
      BaseLayer shapes = new BaseLayer();
      shapes.setName("shapes");
      layers.addThisLayer(shapes);

      WorldLocation loc1 = new WorldLocation(1d, 2d, 3d);
      WorldLocation loc2 = new WorldLocation(1d, 2d, 3d);
      
      ShapeWrapper lineShape = new ShapeWrapper("line", new LineShape(loc1,
          loc2), Color.red, null);
      ShapeWrapper rectShape = new ShapeWrapper("rectangle", new RectangleShape(
          loc1, loc2), Color.red, null);
      shapes.add(lineShape);
      shapes.add(rectShape);

      TrackWrapper track = new TrackWrapper();
      track.setName("track");
      layers.addThisLayer(track);

      FixWrapper newFix = new FixWrapper(new Fix(new HiResDate(100000), loc1,
          0d, 0d));
      track.addFix(newFix);
      newFix.setName("fix");

      DLayers dl = new DLayers(layers);
      assertEquals("found it", lineShape, dl.findThis("line"));
      assertEquals("found it", shapes, dl.findThis("shapes"));
      assertEquals("found it", track, dl.findThis("track"));
      assertEquals("found it", newFix, dl.findThis("fix"));
      assertEquals("found it", null, dl.findThis("fezzig"));

    }
  }

  public Core()
  {
    System.out.println("About to start listening");
    listenToMyParts();
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
              if ("org.mwc.debrief.PlotEditor".equals(descriptor)
                  || "org.mwc.debrief.TrackEditor".equals(descriptor))
              {
                IEditorPart instance = editor.getEditor(false);
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

    public HiResDate getTime()
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

    /** descend the tree looking for an item with the
     * specified name
     * @param name what we're looking for
     * @return the matching item (or null)
     */
    public Editable findThis(final String name)
    {
      Editable res = null;
      if (name != null)
      {
        Enumeration<Editable> ele = _layers.elements();
        while (ele.hasMoreElements() && res == null)
        {
          Layer next = (Layer) ele.nextElement();
          if (name.equals(next.getName()))
          {
            res = next;
            break;
          }
          else
          {
            Enumeration<Editable> items = next.elements();
            while (items.hasMoreElements() && res == null)
            {
              Editable item = items.nextElement();
              if (name.equals(item.getName()))
              {
                res = item;
                break;
              }
              else if (item instanceof Layer)
              {
                Layer subLayer = (Layer) item;
                Enumeration<Editable> subItems = subLayer.elements();
                while (subItems.hasMoreElements())
                {
                  Editable subItem = subItems.nextElement();
                  if (name.equals(subItem.getName()))
                  {
                    res = subItem;
                    break;
                  }
                }
              }
            }
          }
        }
      }

      return res;
    }

    public void remove(final Layer layer)
    {
      _layers.removeThisLayer(layer);
    }

    public void add(final Layer layer)
    {
      _layers.addThisLayer(layer);
    }

    public int size()
    {
      return _layers.size();
    }

    public LightweightTrackWrapper findTrack(@ScriptParameter(
        defaultValue = "unset") String name)
    {
      // special handling. if a track isn't provided, return the first
      // one
      LightweightTrackWrapper res = null;
      if ("unset".equals(name) || name == null)
      {
        // ok, just return the first one
        Enumeration<Editable> ele = _layers.elements();
        while (ele.hasMoreElements() && res == null)
        {
          Editable nextE = ele.nextElement();
          if (nextE instanceof LightweightTrackWrapper)
          {
            res = (LightweightTrackWrapper) nextE;
          }
        }
      }
      else
      {
        Layer match = _layers.findLayer(name);
        if (match instanceof LightweightTrackWrapper)
        {
          res = (LightweightTrackWrapper) match;
        }
      }
      return res;
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
    {
      System.err.println("Can't retrieve workbench window");
      return;
    }

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

  protected void fireNewTime(HiResDate date)
  {
    // get broker service
    IEventBroker broker = PlatformUI.getWorkbench().getService(
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

  /**
   * helper application to help track activation/closing of new plots
   */
  private PartMonitor _partMonitor;

  private TimeProvider _timeProvider;
}
