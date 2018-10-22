package org.mwc.debrief.scripting.wrappers;

import java.awt.Color;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mwc.debrief.core.editors.PlotEditor;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.BaseLayer;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

public class DebriefObjects
{

  /* */
  public static WorldLocation createLocation(double dLat, double dLong,
      double depth)
  {
    return new WorldLocation(dLat, dLong, depth);
  }
  
  public static WorldVector createVector(double distM, double bearingDegs)
  {
    return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(bearingDegs),
        new WorldDistance(distM, WorldDistance.METRES), null);
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

  private static PlotEditor getEditor()
  {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
    for (IWorkbenchWindow window : windows)
    {
      if (window != null)
      {
        IWorkbenchPage[] pages = window.getPages();
        for(IWorkbenchPage page : pages)
        {
          IEditorPart editor = page.getActiveEditor();
          if (editor != null && editor instanceof PlotEditor)
          {
            return (PlotEditor) editor;
          }
        }
      }
    }
    return null;
  }

  public WorldArea getArea()
  {
    PlotEditor editor = getEditor();
    if(editor != null)
    {
      PlainProjection proj = (PlainProjection) editor.getAdapter(PlainProjection.class);
      return proj.getDataArea();
    }
    return null;
  }
  
  public WorldLocation getCentre()
  {
    WorldArea area = getArea();
    if(area != null)
    {
      return area.getCentre();
    }
    else
    {
      return null;
    }
  }
  
  public static DLayers getLayers()
  {
    PlotEditor editor = getEditor();
    if (editor != null)
    {
      Layers layers = (Layers) editor.getAdapter(Layers.class);
      if (layers != null)
      {
        return new DLayers(layers);
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

  public static class DLayers
  {
    private final Layers _layers;

    public DLayers(final Layers layers)
    {
      _layers = layers;
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

    public Layer createLayer(String name)
    {
      Layer newLayer = new BaseLayer();
      newLayer.setName(name);
      _layers.addThisLayer(newLayer);
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
}
