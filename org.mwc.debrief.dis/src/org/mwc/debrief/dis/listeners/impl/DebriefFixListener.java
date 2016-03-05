package org.mwc.debrief.dis.listeners.impl;

import java.awt.Color;

import org.mwc.debrief.dis.listeners.IDISFixListener;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
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
      String eName, final short force, final double dLat,
      final double dLong, final double depth, final double courseDegs,
      final double speedMS, final int damage)
  {
    final String theName;
    if (eName != null)
    {
      theName = eName;
    }
    else
    {
      theName = "DIS_" + id;
    }

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

        return track;
      }

      @Override
      public Plottable createItem()
      {
        WorldLocation loc = new WorldLocation(dLat, dLong, depth);
        HiResDate date = new HiResDate(time);
        Fix newF = new Fix(date, loc, courseDegs, speedMS);
        FixWrapper fw = new FixWrapper(newF);
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
