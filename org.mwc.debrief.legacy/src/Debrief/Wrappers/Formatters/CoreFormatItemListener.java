package Debrief.Wrappers.Formatters;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
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
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class CoreFormatItemListener extends PlainWrapper implements
    INewItemListener, INewLayerListener
{

  public static class TestMe extends TestCase
  {
    public void testRegularIntervals()
    {
      CoreFormatItemListener cf =
          new CoreFormatItemListener("Test", null, null, 5000L, true,
              Attribute.SYMBOL)
          {

            /**
         * 
         */
            private static final long serialVersionUID = 1L;

            @Override
            protected void formatTrack(TrackWrapper track, HiResDate interval)
            {
              // skip
            }

            @Override
            protected void applyFormat(FixWrapper fix)
            {
              fix.setSymbolShowing(true);
            }
          };
      TrackWrapper tw = new TrackWrapper();
      tw.setName("Name");
      FixWrapper f1 = createFix(4000);
      FixWrapper f2 = createFix(5000);
      FixWrapper f3 = createFix(6000);
      FixWrapper f4 = createFix(10000);
      FixWrapper f5 = createFix(12000);

      cf.newItem(tw, f1);
      cf.newItem(tw, f2);
      cf.newItem(tw, f3);
      cf.newItem(tw, f4);
      cf.newItem(tw, f5);

      assertTrue(f1.getSymbolShowing());
      assertTrue(f2.getSymbolShowing());
      assertFalse(f3.getSymbolShowing());
      assertTrue(f4.getSymbolShowing());
      assertFalse(f5.getSymbolShowing());
    }

    public void testIntervals()
    {
      CoreFormatItemListener cf =
          new CoreFormatItemListener("Test", null, null, 5000L, false,
              Attribute.SYMBOL)
          {

            /**
         * 
         */
            private static final long serialVersionUID = 1L;

            @Override
            protected void formatTrack(TrackWrapper track, HiResDate interval)
            {
              // skip
            }

            @Override
            protected void applyFormat(FixWrapper fix)
            {
              fix.setSymbolShowing(true);
            }
          };
      TrackWrapper tw = new TrackWrapper();
      tw.setName("Name");
      FixWrapper f1 = createFix(4000);
      FixWrapper f2 = createFix(5000);
      FixWrapper f3 = createFix(9000);
      FixWrapper f4 = createFix(10000);
      FixWrapper f5 = createFix(14100);

      cf.newItem(tw, f1);
      cf.newItem(tw, f2);
      cf.newItem(tw, f3);
      cf.newItem(tw, f4);
      cf.newItem(tw, f5);

      assertTrue(f1.getSymbolShowing());
      assertFalse(f2.getSymbolShowing());
      assertTrue(f3.getSymbolShowing());
      assertFalse(f4.getSymbolShowing());
      assertTrue(f5.getSymbolShowing());
    }

    private FixWrapper createFix(int time)
    {
      Fix newF =
          new Fix(new HiResDate(time), new WorldLocation(2, 2, 0), 22, 33);
      FixWrapper fw = new FixWrapper(newF);
      return fw;

    }
  }

  public static enum Attribute
  {
    LABEL, SYMBOL, ARROW
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String _layerName;
  private String _sym;
  private long _interval;
  private Map<String, Long> _lastTimes = new HashMap<String, Long>();
  private String _formatName;
  private Attribute _type;

  private boolean _regularInterval;

  public CoreFormatItemListener(String name, String layerName,
      String symbology, long interval, boolean regularInterval, Attribute type)
  {
    _formatName = name;
    _layerName = layerName;
    _sym = symbology;
    _interval = interval;
    _regularInterval = regularInterval;
    _type = type;
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
  public String toString()
  {
    return getName();
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
      if (lastTime == null || thisTime >= lastTime + _interval)
      {
        // do we need to clip the time to a regular interval?
        if (_regularInterval)
        {
          // ok - store the last instance of
          thisTime = thisTime - (thisTime % _interval);
        }

        // store the new time
        _lastTimes.put(hisName, thisTime);

        // and apply the formatting
        applyFormat((FixWrapper) item);
      }
    }
  }

  protected void applyFormat(FixWrapper fix)
  {
    switch (_type)
    {
    case ARROW:
      fix.setArrowShowing(true);
      break;
    case SYMBOL:
      fix.setSymbolShowing(true);
      break;
    case LABEL:
      fix.setLabelShowing(true);
      break;
    }
  }

  @Override
  public void newLayer(Layer layer)
  {
    if (layer instanceof TrackWrapper)
    {
      formatTrack((TrackWrapper) layer, new HiResDate(_interval));
    }
  }

  protected void formatTrack(TrackWrapper track, HiResDate interval)
  {
    switch (_type)
    {
    case ARROW:
      track.setArrowFrequency(interval);
      break;
    case SYMBOL:
      track.setSymbolFrequency(interval);
      break;
    case LABEL:
      track.setLabelFrequency(interval);
      break;
    }
  }

  public static Attribute valueOf(String attributeType)
  {
    Attribute res = null;
    switch (attributeType)
    {
    case "ARROW":
      res = Attribute.ARROW;
      break;
    case "SYMBOL":
      res = Attribute.SYMBOL;
      break;
    case "LABEL":
      res = Attribute.LABEL;
      break;
    }
    return res;
  }

}