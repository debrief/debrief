package com.planetmayo.debrief.satc_rcp.views;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.planetmayo.debrief.satc.model.contributions.ATBForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.AlterationLegForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.BaseAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution.BMeasurement;
import com.planetmayo.debrief.satc.model.contributions.ContributionBuilder;
import com.planetmayo.debrief.satc.model.contributions.ContributionDataType;
import com.planetmayo.debrief.satc.model.contributions.CourseAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.FrequencyMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.FrequencyMeasurementContribution.FMeasurement;
import com.planetmayo.debrief.satc.model.contributions.LocationAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.LocationForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.RangeForecastContribution.ROrigin;
import com.planetmayo.debrief.satc.model.contributions.SpeedAnalysisContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.support.TestSupport;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class TestHarnessView extends ViewPart
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.planetmayo.debrief.satc.views.SampleView";

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view, or
	 * ignore it and always show the same content (like Task List, for example).
	 */

	private IBoundsManager boundsManager;

	private Action _restartAction;
	private Action _stepAction;
	private Action _clearAction;
	private Action _playAction;
	private Action _populateShortAction;
	private Action _populateLongAction;
	private Action _populateGoodAction;
	private Action _liveAction;
	private Action _testOne;

	private TestSupport _testSupport;

	private XStream _xStream;
	private Shell _shell;

	/**
	 * The constructor.
	 */
	public TestHarnessView()
	{
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		_shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		initializeXstream();

		Composite form = new Composite(parent, SWT.NONE);
		_testSupport = new TestSupport();

		makeActions();
		contributeToActionBars();

		// disable our controls, until we find a genny
		boundsManager = SATC_Activator.getDefault().getService(
				IBoundsManager.class, true);
		startListeningTo();

		Composite checkBoxForm = new Composite(form, SWT.NONE);

		FillLayout verticalLayout = new FillLayout(SWT.VERTICAL);

		// insert the diagnostics panels
		Group group1 = new Group(checkBoxForm, SWT.SHADOW_ETCHED_IN);
		group1.setLayout(verticalLayout);
		group1.setText("Constrain problem space");

		Group group2 = new Group(checkBoxForm, SWT.SHADOW_ETCHED_IN);
		group2.setLayout(verticalLayout);
		group2.setText("Generate Solutions");

		checkBoxForm.setLayout(new FillLayout(SWT.HORIZONTAL));
		final Button btn1 = new Button(group1, SWT.CHECK);
		btn1.setText("Show all bounds");
		btn1.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{

				GeoSupport.getProblemDiagnostics()
						.setShowAllBounds(btn1.getSelection());
			}
		});
		final Button btn2 = new Button(group1, SWT.CHECK);
		btn2.setText("Show leg start bounds");
		btn2.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				GeoSupport.getProblemDiagnostics().setShowLegEndBounds(
						btn2.getSelection());
			}
		});
		final Button btn3 = new Button(group2, SWT.CHECK);
		btn3.setText("Show points");
		btn3.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				GeoSupport.getSolutionDiagnostics().setShowPoints(btn3.getSelection());
			}
		});
		final Button btn4 = new Button(group2, SWT.CHECK);
		btn4.setText("Show achievable points");
		btn4.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				GeoSupport.getSolutionDiagnostics().setShowAchievablePoints(
						btn4.getSelection());
			}
		});

		final Button btn5 = new Button(group2, SWT.CHECK);
		btn5.setText("Show all routes");
		btn5.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				GeoSupport.getSolutionDiagnostics().setShowRoutes(btn5.getSelection());
			}
		});
		final Button btn6 = new Button(group2, SWT.CHECK);
		btn6.setText("Show routes with scores");
		btn6.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				GeoSupport.getSolutionDiagnostics().setShowRoutesWithScores(
						btn6.getSelection());
			}
		});
		final Button btn7 = new Button(group2, SWT.CHECK);
		btn7.setText("Show recommended solution");
		btn7.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				GeoSupport.getSolutionDiagnostics().setShowRecommendedSolutions(
						btn7.getSelection());
			}
		});

		Group saveLoadGroup = new Group(form, SWT.SHADOW_ETCHED_IN);
		saveLoadGroup.setLayout(new RowLayout());
		saveLoadGroup.setText("Save/Load Analysis");
		form.setLayout(verticalLayout);

		Button save = new Button(saveLoadGroup, SWT.BUTTON1);
		save.setText("Save");
		save.addMouseListener(new MouseListener()
		{

			private List<BaseContribution> contributions;

			@Override
			public void mouseUp(MouseEvent e)
			{

			}

			@Override
			public void mouseDown(MouseEvent e)
			{
				contributions = new ArrayList<BaseContribution>();
				for (BaseContribution baseContribution : boundsManager
						.getContributions())
				{
					contributions.add(baseContribution);
				}

				String xml = _xStream.toXML(contributions);
				String filename = "";
				try
				{
					FileDialog dialog = new FileDialog(_shell, SWT.SAVE);
					dialog.setFilterExtensions(new String[]
					{ "*.xml" });

					filename = dialog.open();
					if (filename == null)
					{
						return;
					}
				}
				catch (Exception E)
				{
					E.printStackTrace();
				}

				try
				{
					BufferedWriter out = new BufferedWriter(new FileWriter(filename));
					out.write(xml);
					out.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent e)
			{

			}
		});

		Button load = new Button(saveLoadGroup, SWT.BUTTON1);
		load.setText("Load");
		load.addMouseListener(new MouseListener()
		{

			@Override
			public void mouseUp(MouseEvent e)
			{

			}

			@Override
			public void mouseDown(MouseEvent e)
			{
				FileDialog dialog = new FileDialog(_shell, SWT.OPEN);
				dialog.setFilterExtensions(new String[]
				{ "*.xml" });
				String fileSelected = dialog.open();

				if (fileSelected != null)
				{
					try
					{
						FileInputStream fstream = new FileInputStream(fileSelected);
						Object stream = _xStream.fromXML(fstream);
						Collection<BaseContribution> contributionList = null;
						if (stream instanceof Collection)
						{
							contributionList = (Collection<BaseContribution>) stream;
						}

						boundsManager.clear();
						for (BaseContribution contribution : contributionList)
						{
							boundsManager.addContribution(contribution);

						}
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}

				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent e)
			{

			}
		});
		// and get the form to handle it's layout
		form.pack();

	}

	private void initializeXstream()
	{
		_xStream = new XStream();
		_xStream.alias(AlterationLegForecastContribution.class.getSimpleName(),
				AlterationLegForecastContribution.class);
		_xStream.alias(ATBForecastContribution.class.getSimpleName(),
				ATBForecastContribution.class);
		_xStream.alias(BaseAnalysisContribution.class.getSimpleName(),
				BaseAnalysisContribution.class);
		_xStream.alias(BaseContribution.class.getSimpleName(),
				BaseContribution.class);
		_xStream.alias(BearingMeasurementContribution.class.getSimpleName(),
				BearingMeasurementContribution.class);
		_xStream.alias(ContributionBuilder.class.getSimpleName(),
				ContributionBuilder.class);
		_xStream.alias(ContributionDataType.class.getSimpleName(),
				ContributionDataType.class);
		_xStream.alias(CourseAnalysisContribution.class.getSimpleName(),
				CourseAnalysisContribution.class);
		_xStream.alias(CourseForecastContribution.class.getSimpleName(),
				CourseForecastContribution.class);
		_xStream.alias(FrequencyMeasurementContribution.class.getSimpleName(),
				FrequencyMeasurementContribution.class);
		_xStream.alias(LocationAnalysisContribution.class.getSimpleName(),
				LocationAnalysisContribution.class);
		_xStream.alias(LocationForecastContribution.class.getSimpleName(),
				LocationForecastContribution.class);
		_xStream.alias(RangeForecastContribution.class.getSimpleName(),
				RangeForecastContribution.class);
		_xStream.alias(SpeedAnalysisContribution.class.getSimpleName(),
				SpeedAnalysisContribution.class);
		_xStream.alias(SpeedForecastContribution.class.getSimpleName(),
				SpeedForecastContribution.class);
		_xStream.alias(StraightLegForecastContribution.class.getSimpleName(),
				StraightLegForecastContribution.class);
		_xStream.alias(BMeasurement.class.getSimpleName(), BMeasurement.class);
		_xStream.alias(ROrigin.class.getSimpleName(), ROrigin.class);
		_xStream.alias(FMeasurement.class.getSimpleName(), FMeasurement.class);
	}

	private void enableControls(boolean enabled)
	{
		_clearAction.setEnabled(enabled);
		_populateShortAction.setEnabled(enabled);
		_populateLongAction.setEnabled(enabled);
		_populateGoodAction.setEnabled(enabled);
		_restartAction.setEnabled(enabled);
		_stepAction.setEnabled(enabled);
		_playAction.setEnabled(enabled);
		_liveAction.setEnabled(enabled);

	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(_clearAction);
		manager.add(_restartAction);
		manager.add(_populateShortAction);
		manager.add(_populateLongAction);
		manager.add(_populateGoodAction);
		manager.add(_stepAction);
		manager.add(_playAction);
		manager.add(_liveAction);
		manager.add(_testOne);
	}

	private void loadSampleData(boolean useLong)
	{
		_testSupport.loadSampleData(useLong);
	}

	private void loadGoodData()
	{
		_testSupport.loadGoodData();
	}

	private void makeActions()
	{
		_populateShortAction = new Action()
		{
			@Override
			public void run()
			{
				loadSampleData(false);
			}
		};
		_populateShortAction.setText("Pop Short");
		_populateShortAction.setToolTipText("Load some sample data");

		_populateLongAction = new Action()
		{
			@Override
			public void run()
			{
				loadSampleData(true);
			}
		};
		_populateLongAction.setText("Pop Long");
		_populateLongAction.setToolTipText("Load some sample data");

		_populateGoodAction = new Action()
		{
			@Override
			public void run()
			{
				loadGoodData();
			}
		};
		_populateGoodAction.setText("Pop Good");
		_populateGoodAction.setToolTipText("Load some good data");

		_clearAction = new Action()
		{
			@Override
			public void run()
			{
				// clear the bounded states
				boundsManager.clear();
			}
		};
		_clearAction.setText("Clear");
		_clearAction.setToolTipText("Clear the track generator contributions");

		_testOne = new Action()
		{
			@Override
			public void run()
			{
				_testSupport.nextTest();
			}
		};
		_testOne.setText("1");

		_liveAction = new Action("Live", SWT.TOGGLE)
		{

			@Override
			public void run()
			{
				boundsManager.setLiveRunning(_liveAction.isChecked());
			}
		};
		_liveAction.setChecked(true);

		_restartAction = new Action()
		{
			@Override
			public void run()
			{
				// clear the bounded states
				boundsManager.restart();
			}
		};
		_restartAction.setText("Restart");
		_restartAction.setToolTipText("Reset the track generator");

		_stepAction = new Action()
		{
			@Override
			public void run()
			{
				boundsManager.step();
			}
		};
		_stepAction.setText("Step");
		_stepAction.setToolTipText("Process the next contribution");

		_playAction = new Action()
		{
			@Override
			public void run()
			{
				boundsManager.run();
			}
		};
		_playAction.setText("Play");
		_playAction.setToolTipText("Process all contributions");

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
	}

	protected void startListeningTo()
	{
		enableControls(true);

		_testSupport.setGenerator(boundsManager);

		// sort out the 'live' setting
		_liveAction.setChecked(boundsManager.isLiveEnabled());
	}

	protected void stopListeningTo()
	{
		// ok, we can disable our buttons
		enableControls(false);
	}

}