package org.mwc.debrief.lite;

 import java.awt.Container;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import Debrief.GUI.Tote.AnalysisTote;
import Debrief.GUI.Tote.StepControl;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GenericData.WatchableList;

 public class LiteTote extends AnalysisTote
{

   public LiteTote(final Layers theData, final StepControl stepper)
  {
    super(theData);
    setStepper(stepper);
    
    // listen out for layers being added/removed
    theData.addDataExtendedListener(new DataListener() {

      @Override
      public void dataExtended(Layers theData)
      {
        // sort out if our primary is still there.
        WatchableList primary = getPrimary();
        if(primary != null)
        {
          Layer found = theData.findLayer(primary.getName());
          if(found == null)
          {
            // ok, clear the primary
            setPrimary(null);
          }
        }
        
        // also check the secondarires
        final List<WatchableList> toDrop = new ArrayList<WatchableList>();
        for(final WatchableList t: getSecondary())
        {
          final Layer found = theData.findLayer(t.getName());
          if(found == null)
          {
            toDrop.add(t);
          }
        }
        
        // ok, now drop them
        for(final WatchableList t: toDrop)
        {
          removeParticipant(t);
        }
        
      }

      @Override
      public void dataModified(Layers theData, Layer changedLayer)
      {
      }

      @Override
      public void dataReformatted(Layers theData, Layer changedLayer)
      {
        // hmm, maybe repaint
      }});
  }

  @Override
  protected void updateToteMembers()
  {
   
    // see if we can set a primary
    // ok, try to set one
    final Enumeration<Editable> iter = getData().elements();
    while (iter.hasMoreElements())
    {
      final Layer thisL = (Layer) iter.nextElement();
      if (thisL instanceof TrackWrapper)
      {
        final TrackWrapper thisT = (TrackWrapper) thisL;
        if (getPrimary() == null)
        {
          // and now store as primary
          setPrimary(thisT);

          // ok, that may have been a secondary, remove it
          removeParticipant(thisT);
        }
        else if (getPrimary() != thisT)
        {
          Vector<WatchableList> secs = getSecondary();
          if (!secs.contains(thisT))
          {
            setSecondary(thisT);
          }
        }
      }
    }
  }
   
   @Override
  public Container getPanel()
  {
     throw new IllegalArgumentException("Not implemented");
  }

 }