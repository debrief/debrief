package org.mwc.debrief.core.wizards.s2r;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.mwc.debrief.core.wizards.s2r.EnterSolutionPage.SolutionDataItem;
import org.mwc.debrief.core.wizards.s2r.SelectOffsetPage.DataItem;

import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

public class TMAFromSensorWizard extends Wizard
{
  SelectOffsetPage selectOffsetPage;
  EnterSolutionPage enterSolutionPage;
	private double _brgDegs;
	private WorldDistance _range;
	private double _initalCourse;
	private WorldSpeed _initialSpeed;

  
  public TMAFromSensorWizard(double brgDegs, WorldDistance range, double initialCourse, WorldSpeed initialSpeed)
	{
  	_brgDegs = brgDegs;
  	_range = range;
  	_initalCourse = initialCourse;
  	_initialSpeed = initialSpeed;
	}
	public void addPages() {
           selectOffsetPage = new SelectOffsetPage(null);

           // initialise the sensor offset
           DataItem di = (DataItem) selectOffsetPage.createMe();
           di._bearing = _brgDegs;
           if(_range != null)
          	 di._range = _range;
        
           addPage(selectOffsetPage);
                      
           enterSolutionPage = new EnterSolutionPage(null);
           SolutionDataItem d2 = (SolutionDataItem) enterSolutionPage.createMe();
           d2._course = _initalCourse;
           d2._speed	= _initialSpeed;
           
           addPage(enterSolutionPage);
  }
  public boolean performFinish() {
           return true;
  }
  
	@Override
	public IWizardPage getPage(String name)
	{
		return super.getPage(name);
	}
  
  
}
