package Debrief.Wrappers.Formatters;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers.INewItemListener;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import junit.framework.TestCase;

public class SliceTrackFormatListener extends PlainWrapper implements
    INewItemListener
{

  // ///////////////////////////////////////////////////////////
  // info class
  // //////////////////////////////////////////////////////////
  final public class TrackNameInfo extends Editable.EditorType implements
      Serializable
  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public TrackNameInfo(final SliceTrackFormatListener data)
    {
      super(data, data.getName(), "");
    }

    final public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
            {
                displayProp("Name", "Name", "Name for this formatter"),
                displayProp("Interval", "Interval", "Interval to split on"),
                displayProp("Visible", "Active",
                    "Whether this formatter is active"),
                };

        return res;
      }
      catch (final IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  public static class TestMe extends TestCase
  {

    private FixWrapper createFix(int time)
    {
      Fix newF =
          new Fix(new HiResDate(time), new WorldLocation(2, 2, 0), 22, 33);
      FixWrapper fw = new FixWrapper(newF);
      return fw;
    }
    
    public void testTwoNames()
    {
      List<String> names = new ArrayList<String>();
      names.add("Dobbin");
      names.add("T-One");
      names.add("Rover");
      
      INewItemListener cf =
          new SliceTrackFormatListener("Test", 1000L, names);
      
      TrackWrapper tOne = getTrackOne(cf);
      
      // and another track
      TrackWrapper tTwo = getTrackTwo(cf);
      
      // and the end of file processing
      cf.fileComplete();
      
      // check the tracks got split
      assertEquals("two segments", 2, tOne.getSegments().size());
      assertEquals("three segments", 3, tTwo.getSegments().size());
    }
    
    public void testNoNames()
    {
      List<String> names = new ArrayList<String>();
      INewItemListener cf =
          new SliceTrackFormatListener("Test", 1000L, names);
      
      TrackWrapper tOne = getTrackOne(cf);
      
      // and another track
      TrackWrapper tTwo = getTrackTwo(cf);
      
      // and the end of file processing
      cf.fileComplete();
      
      // check the tracks got split
      assertEquals("two segments", 2, tOne.getSegments().size());
      assertEquals("three segments", 3, tTwo.getSegments().size());
    }
    
    public void testOneNames()
    {
      List<String> names = new ArrayList<String>();
      names.add("Dobbin");
      INewItemListener cf =
          new SliceTrackFormatListener("Test", 1000L, names);
      
      TrackWrapper tOne = getTrackOne(cf);
      
      // and another track
      TrackWrapper tTwo = getTrackTwo(cf);
      
      // and the end of file processing
      cf.fileComplete();
      
      // check the tracks got split
      assertEquals("still just one segment", 1, tOne.getSegments().size());
      assertEquals("three segments", 3, tTwo.getSegments().size());
    }
    
    
    public TrackWrapper getTrackTwo(INewItemListener cf)
    {
      TrackWrapper tTwo = new TrackWrapper();
      tTwo.setName("Dobbin");
      
      processFix(cf, tTwo, 2000);
      processFix(cf, tTwo, 2500);
      processFix(cf, tTwo, 5000);
      processFix(cf, tTwo, 5500);
      processFix(cf, tTwo, 6000);
      processFix(cf, tTwo, 6500);
      processFix(cf, tTwo, 8000);
      processFix(cf, tTwo, 8500);
      return tTwo;
    }
    public TrackWrapper getTrackOne(INewItemListener cf)
    {
      TrackWrapper tOne = new TrackWrapper();
      tOne.setName("T-One");
      processFix(cf, tOne, 4000);
      processFix(cf, tOne, 4500);
      processFix(cf, tOne, 5000);
      processFix(cf, tOne, 5500);
      processFix(cf, tOne, 6000);
      processFix(cf, tOne, 6500);
      processFix(cf, tOne, 8000);
      processFix(cf, tOne, 8500);
      return tOne;
    }
    
    
    
    private void processFix(INewItemListener cf, TrackWrapper tw, int time)
    {
      FixWrapper fix = createFix(time);
      tw.addFix(fix);
      cf.newItem(tw, fix, null);
    }
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String _formatName;
  private EditorType _myEditor;
  private long _interval;

  private final List<String> _trackNames;
  private final List<TrackWrapper> _tracksToProcess;

  public SliceTrackFormatListener(final String name,final long interval, 
      final List<String> trackNames)
  {
    _formatName = name;
    _interval = interval;
    _trackNames = trackNames;
    _tracksToProcess = new ArrayList<TrackWrapper>();
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

  public List<String> getTrackNames()
  {
    return _trackNames;
  }
  
  public long getInterval()
  {
    return _interval;
  }

  public void setInterval(final long interval)
  {
    this._interval = interval;
  }

  public void setName(final String name)
  {
    _formatName = name;
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
    return true;
  }

  @Override
  public EditorType getInfo()
  {
    if (_myEditor == null)
    {
      _myEditor = new TrackNameInfo(this);
    }
    return _myEditor;
  }

  @Override
  public void newItem(final Layer parent, final Editable item,
      final String symbology)
  {
    // are we active
    if (!getVisible())
    {
      return;
    }

    // just check if this is actually a new layer call
    if (parent instanceof TrackWrapper)
    {
      boolean addIt = false;
      final TrackWrapper track = (TrackWrapper) parent;
      // do we have any track names?
      if(_trackNames == null || _trackNames.isEmpty())
      {
        // ok, we cam just use it
        addIt = true;
      }
      else
      {
        // check if it's one of our names
        for(String name: _trackNames)
        {
          if(name.equals(track.getName()))
          {
            addIt = true;
            break;
          }
        }
      }
      
      if(addIt)
      {
        if(!_tracksToProcess.contains(track))
        {
          _tracksToProcess.add(track);
        }
      }
    }
  }

  @Override
  public void reset()
  {
    // ignore
  }

  @Override
  public void fileComplete()
  {
    for(TrackWrapper track : _tracksToProcess)
    {
      TrackWrapper_Support.splitTrackAtJumps(track, _interval);
    }
  }
}