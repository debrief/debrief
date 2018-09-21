package org.mwc.debrief.core.wizards.dynshapes;

import org.eclipse.jface.wizard.Wizard;

import Debrief.Wrappers.DynamicShapeWrapper;

abstract public class DynamicShapeWizard extends Wizard
{

  /** get the dynamic shape wrapper specific to this shape
   * 
   * @return the object created by wizard fields
   */
  abstract public DynamicShapeWrapper getDynamicShapeWrapper();

}
