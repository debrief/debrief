package org.mwc.debrief.dis.listeners.impl;

import java.awt.Color;

import org.mwc.debrief.dis.listeners.IDISFixListener;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.DebriefColors;
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
      final short domain, final short category, final boolean isOSAT,
      final double dLat, final double dLong, final double depth,
      final double courseRads, final double speedMS, final int damage)
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
        if (domain == 4)
        {
          track.setSymbolType(SymbolFactory.SCALED_SUBMARINE);
          track.setSymbolLength(new WorldDistance(100, WorldDistance.METRES));
          track.setSymbolWidth(new WorldDistance(20, WorldDistance.METRES));
        }
        else if (domain == 3)
        {
          track.setSymbolType(SymbolFactory.SCALED_FRIGATE);
          track.setSymbolLength(new WorldDistance(100, WorldDistance.METRES));
          track.setSymbolWidth(new WorldDistance(20, WorldDistance.METRES));
        }
        else if (domain == 0)
        {
          track.setSymbolType(SymbolFactory.CIRCLE);
        }

        if (kind == 2)
        {
          track.setSymbolType(SymbolFactory.TORPEDO);
        }
        else if(kind == 8)
        {
          track.setSymbolType(SymbolFactory.DATUM);
        }
        else if(kind == IDISFixListener.OSAT_TRACK || kind == IDISFixListener.NON_OSAT_TRACK)
        {
          // ok, this is OSAT track
          track.setSymbolType(SymbolFactory.DATUM);
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

        fw.resetName();

        Color col = colorFor(force);

        // see if it's a track 
        if (kind == IDISFixListener.OSAT_TRACK
            || kind == IDISFixListener.NON_OSAT_TRACK)
        {
          // ok, and is it the OSAT track?
          if (isOSAT)
          {
            fw.setColor(DebriefColors.ORANGE);
          }
        }
        else
        {
          // shade according to appearance
          // darken the fix, if necessary
          if (damage > 0)
          {
            for (int i = 0; i < damage; i++)
            {
              col = col.darker();
            }
            fw.setColor(col);
          }
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
      theCol = DebriefColors.RED;
      break;
    case BLUE:
      theCol = DebriefColors.BLUE;
      break;
    case GREEN:
      theCol = DebriefColors.GREEN;
      break;
    case OTHER:
      theCol = DebriefColors.YELLOW;
      break;
    default:
      System.err.println("NO, NO FORCE FOUND");
      theCol = Color.yellow;
    }
    return theCol;
  }
}
