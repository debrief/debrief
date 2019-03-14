package org.mwc.debrief.lite;

 import java.awt.Container;
import java.util.Enumeration;
import java.util.Vector;

import Debrief.GUI.Tote.AnalysisTote;
import Debrief.GUI.Tote.StepControl;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.WatchableList;

 public class LiteTote extends AnalysisTote
{

   public LiteTote(Layers theData, StepControl stepper)
  {
    super(theData);
    setStepper(stepper);
  }

   @Override
  protected void updateToteMembers()
  {
     // see if we can set a primary
     // ok, try to set one
     final Enumeration<Editable> iter = getData().elements();
     while(iter.hasMoreElements())
     {
       final Layer thisL = (Layer) iter.nextElement();
       if(thisL instanceof TrackWrapper)
       {
         final TrackWrapper thisT = (TrackWrapper) thisL;
         if(getPrimary() == null)
         {
           setPrimary(thisT);
         }
         else if(getPrimary() != thisT)
         {
           Vector<WatchableList> secs = getSecondary();
           if(!secs.contains(thisT))
           {
              setSecondary(thisT);
           }
         }
       }
     }  }

   
   
   @Override
  public Container getPanel()
  {
     throw new IllegalArgumentException("Not implemented");
  }

 }