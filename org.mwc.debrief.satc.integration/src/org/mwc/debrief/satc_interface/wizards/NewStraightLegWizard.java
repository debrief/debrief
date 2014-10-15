/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.satc_interface.wizards;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.mwc.debrief.satc_interface.wizards.CourseConstraintsWizardPage.CourseConstraintsObject;
import org.mwc.debrief.satc_interface.wizards.SpeedConstraintsWizardPage.SpeedConstraintsObject;

import MWC.GenericData.TimePeriod;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;

/**
 * This is a sample new wizard. Its role is to create a new file resource in the
 * provided container. If the container resource (a folder or a project) is
 * selected in the workspace when the wizard is opened, it will accept it as the
 * target container. The wizard creates one file with the extension "xml". If a
 * sample multi-page editor (also available as a template) is registered for the
 * same extension, it will be able to open it.
 */

public class NewStraightLegWizard extends Wizard implements INewWizard
{
	private CourseConstraintsWizardPage _courseWizard;
	private SpeedConstraintsWizardPage _speedWizard;
	private LegNameWizardPage _nameWizard;

	private ISelection selection;
	private TimePeriod period;
	private ArrayList<BaseContribution> _contributions;

	/**
	 * Constructor for NewPlotWizard.
	 * 
	 * @param period
	 * @param course
	 * @param speed
	 */
	public NewStraightLegWizard(TimePeriod period)
	{
		super();
		this.period = period;
	}
	
	

	@Override
	public void createPageControls(Composite pageContainer)
	{
		super.createPageControls(pageContainer);
		
		_speedWizard.setPresent(false);
		_courseWizard.setPresent(false);
	}



	/**
	 * Adding the page to the wizard.
	 */

	@Override
	public void addPages()
	{
		_nameWizard = new LegNameWizardPage(selection);
		_courseWizard = new CourseConstraintsWizardPage(selection,
				new CourseForecastContribution());
		_speedWizard = new SpeedConstraintsWizardPage(selection,
				new SpeedForecastContribution());

		addPage(_nameWizard);
		addPage(_courseWizard);
		addPage(_speedWizard);
	}

	/**
	 * The worker method. It will find the container, create the file if missing
	 * or just replace its contents, and open the editor on the newly created
	 * file.
	 */

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(final IWorkbench workbench,
			final IStructuredSelection selection1)
	{
		this.selection = selection1;
	}


	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	@Override
	public boolean performFinish()
	{
		_contributions = new ArrayList<BaseContribution>();
		
		StraightLegForecastContribution straight = new StraightLegForecastContribution();
		straight.setName(_nameWizard.getName());
		straight.setStartDate(period.getStartDTG().getDate());
		straight.setFinishDate(period.getEndDTG().getDate());
		_contributions.add(straight);

		// have a course?
		CourseConstraintsObject courseO = (CourseConstraintsObject) _courseWizard
				.getEditable();
		if (courseO != null)
		{
			CourseForecastContribution theCourse = courseO.getContribution();
			theCourse.setStartDate(period.getStartDTG().getDate());
			theCourse.setFinishDate(period.getEndDTG().getDate());
			_contributions.add(theCourse);
		}

		// have a speed
		SpeedConstraintsObject speedO = (SpeedConstraintsObject) _speedWizard
				.getEditable();
		if (speedO != null)
		{
			SpeedForecastContribution theSpeed = speedO.getContribution();
			theSpeed.setStartDate(period.getStartDTG().getDate());
			theSpeed.setFinishDate(period.getEndDTG().getDate());
			_contributions.add(theSpeed);
		}

		return true;
	}

	public String getName()
	{
		return _nameWizard.getEditable().getName();
	}

	public LegNameWizardPage getNameWizard()
	{
		return _nameWizard;
	}
	
	public ArrayList<BaseContribution> getContributions()
	{
		return _contributions;
	}

	public CourseConstraintsWizardPage getCourseWizard()
	{
		return _courseWizard;
	}

	public SpeedConstraintsWizardPage getSpeedWizard()
	{
		return _speedWizard;
	}

}