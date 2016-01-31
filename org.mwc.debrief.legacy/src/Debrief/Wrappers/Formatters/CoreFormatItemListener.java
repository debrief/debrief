package Debrief.Wrappers.Formatters;

import java.util.HashMap;
import java.util.Map;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.Layers.INewLayerListener;
import MWC.GUI.PlainWrapper;
import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;

public abstract class CoreFormatItemListener extends PlainWrapper implements
    INewItemListener, INewLayerListener
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String _layerName;
  private String _sym;
  private long _interval;
  private Map<String, Long> _lastTimes = new HashMap<String, Long>();
  private String _formatName;

  public CoreFormatItemListener(String name, String layerName,
      String symbology, long interval)
  {
    _formatName = name;
    _layerName = layerName;
    _sym = symbology;
    _interval = interval;
  }

  @Override
  public void paint(CanvasType dest)
  {
    // don't bother, it can't be plotted
  }

  @Override
  public String getName()
  {
    return _formatName;
  }

  @Override
  public WorldArea getBounds()
  {
    return null;
  }

  @Override
  public boolean hasEditor()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void newItem(Layer parent, Editable item)
  {
    if (!(item instanceof FixWrapper))
    {
      return;
    }

    final String hisName = parent.getName();

    if (_layerName != null && !_layerName.equals(parent.getName()))
    {
      return;
    }
    if (_sym != null)
    {
      if (parent instanceof TrackWrapper)
      {
        // match the symbology
        return;
      }
    }

    if (item instanceof TimeStampedDataItem)
    {
      Long lastTime = _lastTimes.get(hisName);
      TimeStampedDataItem tsd = (TimeStampedDataItem) item;
      long thisTime = tsd.getDTG().getDate().getTime();
      if (lastTime == null || thisTime > lastTime + _interval)
      {
        // store the new time
        _lastTimes.put(hisName, thisTime);

        // and apply the formatting
        applyFormat((FixWrapper) item);
      }
    }
  }

  abstract protected void applyFormat(FixWrapper fix);

  @Override
  public void newLayer(Layer layer)
  {
    if (layer instanceof TrackWrapper)
    {
      formatTrack((TrackWrapper) layer, new HiResDate(_interval));
    }
  }

  abstract protected void formatTrack(TrackWrapper track, HiResDate interval);

}