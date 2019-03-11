package org.mwc.debrief.lite;

 import java.awt.Container;

 import Debrief.GUI.Tote.AnalysisTote;
import Debrief.GUI.Tote.StepControl;
import MWC.GUI.Layers;

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
  }

   
   
   @Override
  public Container getPanel()
  {
    return null;
  }

 }