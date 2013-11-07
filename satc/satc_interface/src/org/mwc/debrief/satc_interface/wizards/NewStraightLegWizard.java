package org.mwc.debrief.satc_interface.wizards;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.mwc.debrief.satc_interface.wizards.CourseConstraintsWizardPage.CourseConstraintsObject;
import org.mwc.debrief.satc_interface.wizards.SpeedConstraintsWizardPage.SpeedConstraintsObject;

import MWC.GenericData.TimePeriod;

import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.ISolver;

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
	private final ISolver solver;
	private TimePeriod period;

	/**
	 * Constructor for NewPlotWizard.
	 * 
	 * @param period
	 * @param course
	 * @param speed
	 */
	public NewStraightLegWizard(ISolver solver, TimePeriod period)
	{
		super();
		this.solver = solver;
		this.period = period;
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

		StraightLegForecastContribution straight = new StraightLegForecastContribution();
		straight.setName(_nameWizard.getName());
		straight.setStartDate(period.getStartDTG().getDate());
		straight.setFinishDate(period.getEndDTG().getDate());
		solver.getContributions().addContribution(straight);

		// have a course?
		CourseConstraintsObject courseO = (CourseConstraintsObject) _courseWizard
				.getEditable();
		if (courseO != null)
		{
			CourseForecastContribution theCourse = courseO.getContribution();
			theCourse.setStartDate(period.getStartDTG().getDate());
			theCourse.setFinishDate(period.getEndDTG().getDate());
			solver.getContributions().addContribution(theCourse);
		}

		// have a speed
		SpeedConstraintsObject speedO = (SpeedConstraintsObject) _speedWizard
				.getEditable();
		if (courseO != null)
		{
			SpeedForecastContribution theSpeed = speedO.getContribution();
			theSpeed.setStartDate(period.getStartDTG().getDate());
			theSpeed.setFinishDate(period.getEndDTG().getDate());
			solver.getContributions().addContribution(theSpeed);
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

	public CourseConstraintsWizardPage getCourseWizard()
	{
		return _courseWizard;
	}

	public SpeedConstraintsWizardPage getSpeedWizard()
	{
		return _speedWizard;
	}

}