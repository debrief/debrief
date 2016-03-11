package org.mwc.debrief.dis.listeners.impl;

import java.awt.Color;

import org.mwc.debrief.dis.listeners.IDISFixListener;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.Symbols.SymbolFactory;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class DebriefFixListener extends DebriefCoreListener implements
    IDISFixListener
{

  public DebriefFixListener(IDISContext context)
  {
    super(context);
  }

  @Override
  public void add(final long time, short exerciseId, long id,
      final String theName, final short force, final short kind,
      final short domain, final short category, final boolean isHighlighted,
      final double dLat, final double dLong, final double depth, final double courseRads, final double speedMS, final int damage)
  {
    super.addNewItem(exerciseId, theName, new ListenerHelper()
    {

      @Override
      public Layer createLayer()
      {
        TrackWrapper track = new TrackWrapper();
        track.setName(theName);

        Color theCol = colorFor(force);

        Color newCol = theCol; // colorFor(theName);
        // ok, give it some color
        track.setColor(newCol);
        track.setSymbolColor(newCol);

        // see if we can exploit the domain
        if (kind == 1)
        {
          if (domain == 4)
          {
            track.setSymbolType(SymbolFactory.SCALED_SUBMARINE);
            track.setSymbolLength(new WorldDistance(100, WorldDistance.METRES));
            track.setSymbolWidth(new WorldDistance(20, WorldDistance.METRES));
          }
          else if (domain == 0)
          {
            track.setSymbolType(SymbolFactory.SCALED_FRIGATE);
            track.setSymbolLength(new WorldDistance(100, WorldDistance.METRES));
            track.setSymbolWidth(new WorldDistance(20, WorldDistance.METRES));
          }
        }
        else if (kind == 2)
        {
          track.setSymbolType(SymbolFactory.TORPEDO);
        }

        return track;
      }

      @Override
      public Plottable createItem()
      {
        WorldLocation loc = new WorldLocation(dLat, dLong, depth);
        HiResDate date = new HiResDate(time);
        Fix newF = new Fix(date, loc, courseRads, speedMS);
        FixWrapper fw = new FixWrapper(newF);
        
//        if(isHighlighted)
//        {
//          fw.setLineShowing(true);
//        }
//        else
//        {
//          fw.setLineShowing(false);
//        }
        
        fw.resetName();

        // darken the fix, if necessary
        if (damage > 0)
        {
          Color col = colorFor(force);
          for (int i = 0; i < damage; i++)
          {
            col = col.darker();
          }
          fw.setColor(col);
        }
        return fw;
      }
    });
  }

  private Color colorFor(final short force)
  {
    Color theCol = null;
    switch (force)
    {
    case RED:
      theCol = Color.red;
      break;
    case BLUE:
      theCol = Color.blue;
      break;
    case GREEN:
      theCol = Color.green;
      break;
    case OTHER:
      theCol = Color.orange;
      break;
    default:
      System.err.println("NO, NO FORCE FOUND");
      theCol = Color.yellow;
    }
    return theCol;
  }
}
