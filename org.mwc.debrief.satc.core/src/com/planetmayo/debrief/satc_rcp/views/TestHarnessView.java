/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc_rcp.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.IntegerFormatter;
import org.eclipse.nebula.widgets.formattedtext.LongFormatter;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.generator.ISolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.generator.impl.Solver;
import com.planetmayo.debrief.satc.model.generator.impl.SwitchableSolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.impl.bf.BFSolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.impl.ga.GAParameters;
import com.planetmayo.debrief.satc.model.generator.impl.ga.GASolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.impl.sa.SAParameters;
import com.planetmayo.debrief.satc.model.generator.impl.sa.SASolutionGenerator;
import com.planetmayo.debrief.satc.model.manager.ISolversManager;
import com.planetmayo.debrief.satc.model.manager.ISolversManagerListener;
import com.planetmayo.debrief.satc.support.TestSupport;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.calculator.GeoCalculatorType;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.planetmayo.debrief.satc_rcp.io.XStreamIO;
import com.planetmayo.debrief.satc_rcp.io.XStreamIO.XStreamReader;
import com.planetmayo.debrief.satc_rcp.io.XStreamIO.XStreamWriter;
import com.planetmayo.debrief.satc_rcp.model.SpatialViewSettings;
import com.planetmayo.debrief.satc_rcp.ui.UIListener;
import com.planetmayo.debrief.satc_rcp.ui.UIUtils;

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
	private Shell _shell;
	private Action _restartAction;
	private Action _stepAction;
	private Action _clearAction;
	private Action _playAction;
	private Action _populateGoodAction;
	private Action _liveAction;
	private Action _saveAction;
	private Action _loadAction;
	private Action _useFastCalculator;
	
	private Button _useGAButton;
	private Button _useSAButton;

	private Composite _currentSolutionGeneratorComposite;
	private Composite _saParametersComposite;
	private Composite _gaParametersComposite;
	
	private ComboViewer _solvers;
	
	private DataBindingContext _context;
	private Binder<GASolutionGenerator> _gaBinder;
	private Binder<SASolutionGenerator> _saBinder;	
	private List<Control> bfGraphControls = new ArrayList<Control>();
	private List<Control> gaGraphControls = new ArrayList<Control>();
	private PropertyChangeListener _liveRunningListener;

	private ISolver _activeSolver;
	private ISolversManager _solversManager;
	private ISolversManagerListener solversManagerListener;	
	private int solverNumber = 1;
	private boolean _settingActiveSolver;
	
	private SpatialViewSettings _spatialSettings;
	
	public TestHarnessView() 
	{
		_solversManager = SATC_Activator.getDefault().getService(ISolversManager.class, false);
		_spatialSettings = SATC_Activator.getDefault().getService(SpatialViewSettings.class, true);
	}

	@Override
	public void createPartControl(Composite parent)
	{
		_shell = parent.getShell();
		_context = new DataBindingContext();
		
		makeActions();
		fillLocalToolBar(getViewSite().getActionBars().getToolBarManager());
		solversManagerListener = UIListener.wrap(parent.getDisplay(), 
				ISolversManagerListener.class, new ISolversManagerListener()
				{					
					@Override
					public void solverCreated(ISolver solver)
					{
						_solvers.add(solver);
					}
					
					@Override
					public void activeSolverChanged(ISolver activeSolver)
					{
						setActiveSolver(activeSolver);				
					}
				}
		);
		_solversManager.addSolversManagerListener(solversManagerListener);
		
		Composite form = new Composite(parent, SWT.NONE);
		createSolversGroup(form);
		createSpatialOptionsGroup(form);
		createAlgorithmsGroup(form);
		
		setActiveSolver(_solversManager.getActiveSolver());
		form.setLayout(UIUtils.createGridLayoutWithoutMargins(1, false));
		form.pack();
	}	
	
	@Override
	public void dispose()
	{
		super.dispose();
		_solversManager.removeSolverManagerListener(solversManagerListener);
		_context.dispose();
	}
	
	@Override
	public void setFocus()
	{
	}
	
	private void createSolversGroup(Composite parent)
	{
		Group solversGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		solversGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		solversGroup.setText("Solvers");
		solversGroup.setLayout(new GridLayout(3, false));
		
		UIUtils.createLabel(solversGroup, "Solvers", new GridData());
		_solvers = new ComboViewer(solversGroup);
		_solvers.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_solvers.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element)
			{
				return ((ISolver) element).getName();
			}
		});
		_solvers.addPostSelectionChangedListener(new ISelectionChangedListener()
		{			
			@Override
			public void selectionChanged(SelectionChangedEvent e)
			{
				if (_settingActiveSolver)
				{
					return;
				}
				StructuredSelection selection = (StructuredSelection) e.getSelection();
				ISolver solver = (ISolver) selection.getFirstElement();
				_solversManager.setActiveSolver(solver);
			}
		});
		for (ISolver solver : _solversManager.getAvailableSolvers())
		{
			_solvers.add(solver);
		}
		
		Button newSolver = new Button(solversGroup, SWT.PUSH);
		newSolver.setText("New");
		newSolver.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				ISolver newSolver = _solversManager.createSolver("Solver " + solverNumber);
				solverNumber++;
				_solvers.setSelection(new StructuredSelection(newSolver));
			}
		});
	}

	private void createSpatialOptionsGroup(Composite parent) 
	{
		Composite checkBoxForm = new Composite(parent, SWT.NONE);
		checkBoxForm.setLayoutData(new GridData(GridData.FILL_BOTH));
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
				_spatialSettings.setShowAllBounds(btn1.getSelection());
			}
		});
		final Button btn2a = new Button(group1, SWT.CHECK);
		btn2a.setText("Show leg start bounds");
		btn2a.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				_spatialSettings.setShowLegEndBounds(btn2a.getSelection());
			}
		});
		final Button btn2b = new Button(group1, SWT.CHECK);
		btn2b.setText("Show target solution");
		btn2b.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				_spatialSettings.setShowTargetSolution(btn2b.getSelection());
			}
		});
		
		final Button btn3 = new Button(group2, SWT.CHECK);
		btn3.setText("Show points");
		btn3.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				_spatialSettings.setShowPoints(btn3.getSelection());
			}
		});
		bfGraphControls.add(btn3);
		
		final Button btn4 = new Button(group2, SWT.CHECK);
		btn4.setText("Show achievable points");
		btn4.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				_spatialSettings.setShowAchievablePoints(
						btn4.getSelection());
			}
		});
		bfGraphControls.add(btn4);

		final Button btn5 = new Button(group2, SWT.CHECK);
		btn5.setText("Show all routes");
		btn5.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				_spatialSettings.setShowRoutes(btn5.getSelection());
			}
		});
		bfGraphControls.add(btn5);
		
		final Button btn6a = new Button(group2, SWT.CHECK);
		btn6a.setText("Show routes with scores");
		btn6a.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				_spatialSettings.setShowRoutesWithScores(
						btn6a.getSelection());
			}
		});
		bfGraphControls.add(btn6a);
		
		final Button btn6b = new Button(group2, SWT.CHECK);
		btn6b.setText("Show generated points");
		btn6b.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				_spatialSettings.setShowRoutePoints(
						btn6b.getSelection());
			}
		});
		bfGraphControls.add(btn6b);
		
		final Button btn6c = new Button(group2, SWT.CHECK);
		btn6c.setText("Show labels for generated points");
		btn6c.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				_spatialSettings.setShowRoutePointLabels(
						btn6c.getSelection());
			}
		});
		bfGraphControls.add(btn6c);
		
		final Button btn7 = new Button(group2, SWT.CHECK);
		btn7.setText("Show intermediate GA");
		btn7.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				_spatialSettings.setShowIntermediateGASolutions(
						btn7.getSelection());
			}
		});
		btn7.setVisible(false);
		gaGraphControls.add(btn7);
		
		final Button btn8 = new Button(group2, SWT.CHECK);
		btn8.setText("Show recommended solution");
		btn8.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				_spatialSettings.setShowRecommendedSolutions(
						btn8.getSelection());
			}
		});
	}

	private void createAlgorithmsGroup(Composite parent)
	{
		Group gaGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		gaGroup.setLayoutData(new GridData(GridData.FILL_BOTH));		
		gaGroup.setText("Genetic algorithm parameters");
		gaGroup.setLayout(UIUtils.createGridLayoutWithoutMargins(1, false));		

		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;		
		Composite composite = UIUtils.createEmptyComposite(gaGroup, new RowLayout(SWT.HORIZONTAL), gridData);
		
		_useGAButton = new Button(composite, SWT.CHECK);
		_useGAButton.setText("Use GA");
		_useGAButton.setLayoutData(new RowData(150, SWT.DEFAULT));
		
		_useSAButton = new Button(composite, SWT.CHECK);
		_useSAButton.setSelection(false);
		_useSAButton.setText("Use Simulated Annealing");
		
		_currentSolutionGeneratorComposite = new Composite(gaGroup, SWT.NONE);
		_currentSolutionGeneratorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		_currentSolutionGeneratorComposite.setLayout(new StackLayout());

		_gaParametersComposite = new Composite(_currentSolutionGeneratorComposite, SWT.NONE);
		_gaParametersComposite.setLayout(new GridLayout(2, false));
		createGAParameters(_gaParametersComposite);
		
		_saParametersComposite = new Composite(_currentSolutionGeneratorComposite, SWT.NONE);
		_saParametersComposite.setLayout(new GridLayout(2, false));
		createSAParameters(_saParametersComposite);		

		addChangeGeneratorListeners();		
	}
	
	private void createGAParameters(Composite gaParameters) 
	{
		UIUtils.createLabel(gaParameters, "Consider alterations:", new GridData());		
		final Button considerAlteringLegs = new Button(gaParameters, SWT.CHECK);
		considerAlteringLegs.setLayoutData(new GridData());		
		
		UIUtils.createLabel(gaParameters, "Population size:", new GridData());
		final FormattedText populationSize = new FormattedText(gaParameters);
		populationSize.setFormatter(new LongFormatter());
		populationSize.getControl().setLayoutData(new GridData(100, SWT.DEFAULT));
		
		UIUtils.createLabel(gaParameters, "Elitizm:", new GridData());
		final FormattedText elitizm = new FormattedText(gaParameters);
		elitizm.setFormatter(new LongFormatter());
		elitizm.getControl().setLayoutData(new GridData(100, SWT.DEFAULT));
		
		UIUtils.createLabel(gaParameters, "Stagnation steps:", new GridData());
		final FormattedText stagnation = new FormattedText(gaParameters);
		stagnation.setFormatter(new LongFormatter());
		stagnation.getControl().setLayoutData(new GridData(100, SWT.DEFAULT));
		
		UIUtils.createLabel(gaParameters, "Timeout:", new GridData());
		final FormattedText timeout = new FormattedText(gaParameters);
		timeout.setFormatter(new IntegerFormatter("###,##0"));
		timeout.getControl().setLayoutData(new GridData(100, SWT.DEFAULT));
		
		UIUtils.createLabel(gaParameters, "Mutation Prob:", new GridData());
		final FormattedText mutation = new FormattedText(gaParameters);
		mutation.setFormatter(new NumberFormatter("0.0#"));
		mutation.getControl().setLayoutData(new GridData(100, SWT.DEFAULT));
		
		UIUtils.createLabel(gaParameters, "Timeout between iteration:    ", new GridData());
		final FormattedText timeoutIteration = new FormattedText(gaParameters);
		timeoutIteration.setFormatter(new LongFormatter());
		timeoutIteration.getControl().setLayoutData(new GridData(100, SWT.DEFAULT));		
		
		_gaBinder = new Binder<GASolutionGenerator>(_context)
		{

			@Override
			protected void doBind(GASolutionGenerator gaSolutionGenerator)
			{
				GAParameters parameters = gaSolutionGenerator.getParameters();
				add(_context.bindValue(
						WidgetProperties.text(SWT.Modify).observeDelayed(100, populationSize.getControl()),
						BeansObservables.observeValue(parameters, GAParameters.POPULATION_SIZE)
				));
				add(_context.bindValue(
						WidgetProperties.text(SWT.Modify).observeDelayed(100, elitizm.getControl()),
						BeansObservables.observeValue(parameters, GAParameters.ELITIZM)
				));
				add(_context.bindValue(
						WidgetProperties.text(SWT.Modify).observeDelayed(100, stagnation.getControl()),
						BeansObservables.observeValue(parameters, GAParameters.STAGNATION_STEPS)
				));
				add(_context.bindValue(
						WidgetProperties.text(SWT.Modify).observeDelayed(100, timeout.getControl()),
						BeansObservables.observeValue(parameters, GAParameters.TIMEOUT)
				));
				add(_context.bindValue(
						WidgetProperties.text(SWT.Modify).observeDelayed(100, mutation.getControl()),
						BeansObservables.observeValue(parameters, GAParameters.MUTATION_PROBABILITY)
				));		
				add(_context.bindValue(
						WidgetProperties.text(SWT.Modify).observeDelayed(100, timeoutIteration.getControl()),
						BeansObservables.observeValue(parameters, GAParameters.TIMEOUT_BETWEEN_ITERATIONS)
				));
				add(_context.bindValue(
						WidgetProperties.selection().observe(considerAlteringLegs), 
						BeansObservables.observeValue(parameters, GAParameters.USE_ALTERING_LEGS))
				);
			}
		};
	}
	
	private void createSAParameters(Composite parent) 
	{
		UIUtils.createLabel(parent, "Start temperature:", new GridData());
		final FormattedText startTemperature = new FormattedText(parent);
		startTemperature.setFormatter(new NumberFormatter("0.0##"));
		startTemperature.getControl().setLayoutData(new GridData(100, SWT.DEFAULT));
		
		UIUtils.createLabel(parent, "End temperature:", new GridData());
		final FormattedText endTemperature = new FormattedText(parent);
		endTemperature.setFormatter(new NumberFormatter("0.0##"));
		endTemperature.getControl().setLayoutData(new GridData(100, SWT.DEFAULT));
		
		UIUtils.createLabel(parent, "Parallel threads:", new GridData());
		final FormattedText parallelThreads = new FormattedText(parent);
		parallelThreads.setFormatter(new LongFormatter());
		parallelThreads.getControl().setLayoutData(new GridData(100, SWT.DEFAULT));
		
		UIUtils.createLabel(parent, "Iterations in thread:", new GridData());
		final FormattedText iterations = new FormattedText(parent);
		iterations.setFormatter(new LongFormatter());
		iterations.getControl().setLayoutData(new GridData(100, SWT.DEFAULT));
		
		GridData checkboxData = new GridData();
		checkboxData.horizontalSpan = 2;
		final Button startOnCenter = new Button(parent, SWT.CHECK);
		startOnCenter.setText("Start on center");
		startOnCenter.setLayoutData(checkboxData);
		
		final Button joinedIterations = new Button(parent, SWT.CHECK);
		joinedIterations.setText("Joined iterations");
		joinedIterations.setLayoutData(checkboxData);		
		
		_saBinder = new Binder<SASolutionGenerator>(_context)
		{

			@Override
			protected void doBind(SASolutionGenerator saSolutionGenerator)
			{
				add(_context.bindValue(
						WidgetProperties.text(SWT.Modify).observeDelayed(100, startTemperature.getControl()),
						BeansObservables.observeValue(saSolutionGenerator.getParameters(), SAParameters.START_TEMPRATURE)
				));
				add(_context.bindValue(
						WidgetProperties.text(SWT.Modify).observeDelayed(100, endTemperature.getControl()),
						BeansObservables.observeValue(saSolutionGenerator.getParameters(), SAParameters.END_TEMPRATURE)
				));
				add(_context.bindValue(
						WidgetProperties.text(SWT.Modify).observeDelayed(100, parallelThreads.getControl()),
						BeansObservables.observeValue(saSolutionGenerator.getParameters(), SAParameters.PARALLEL_THREADS)
				));
				add(_context.bindValue(
						WidgetProperties.text(SWT.Modify).observeDelayed(100, iterations.getControl()),
						BeansObservables.observeValue(saSolutionGenerator.getParameters(), SAParameters.ITERATIONS_IN_THREAD)
				));
				add(_context.bindValue(
						WidgetProperties.selection().observe(startOnCenter),
						BeansObservables.observeValue(saSolutionGenerator.getParameters(), SAParameters.START_ON_CENTER)
				));
				add(_context.bindValue(
						WidgetProperties.selection().observe(joinedIterations),
						BeansObservables.observeValue(saSolutionGenerator.getParameters(), SAParameters.JOINED_ITERATIONS)
				));
			}
		};
	}	
	
	private void addChangeGeneratorListeners() 
	{
		_useGAButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (_activeSolver != null && (_activeSolver.getSolutionGenerator() 
						instanceof SwitchableSolutionGenerator))
				{		
					SwitchableSolutionGenerator g = (SwitchableSolutionGenerator)
							_activeSolver.getSolutionGenerator();
					if (_useGAButton.getSelection())
					{
						g.switchToGA();
					}
					else
					{
						g.switchToBF();
					}
					selectSolutionGenerator(g);
				}
			}
		});		
		_useSAButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (_activeSolver != null && (_activeSolver.getSolutionGenerator() 
						instanceof SwitchableSolutionGenerator))
				{		
					SwitchableSolutionGenerator g = (SwitchableSolutionGenerator)
							_activeSolver.getSolutionGenerator();
					if (_useSAButton.getSelection())
					{
						g.switchToSA();
					}
					else
					{
						g.switchToBF();
					}
					selectSolutionGenerator(g);
				}
			}
		});			
		
	}

	private void setActiveSolver(ISolver solver)
	{
		_settingActiveSolver = true;
		try {
			if (_activeSolver != null) 
			{
				((Solver) _activeSolver).removePropertyChangeListener(
						ISolver.LIVE_RUNNING, 
						_liveRunningListener
				);
			}
			_activeSolver = solver;
			_gaBinder.clear();
			_saBinder.clear();

			boolean enabled = solver != null;
			_clearAction.setEnabled(enabled);
			_populateGoodAction.setEnabled(enabled);
			_restartAction.setEnabled(enabled);
			_stepAction.setEnabled(enabled);
			_playAction.setEnabled(enabled);
			_liveAction.setEnabled(enabled);
			_saveAction.setEnabled(enabled);
			_loadAction.setEnabled(enabled);
			_useGAButton.setEnabled(false);
			_useSAButton.setEnabled(false);
			
			if (solver != null) {
				_solvers.setSelection(new StructuredSelection(solver));
				if (solver.getSolutionGenerator() instanceof SwitchableSolutionGenerator) {
					_useGAButton.setEnabled(true);
					_useSAButton.setEnabled(true);
				}
				selectSolutionGenerator(solver.getSolutionGenerator());
				
				_liveAction.setChecked(_activeSolver.isLiveRunning());
				_liveRunningListener = new PropertyChangeListener()
				{
					
					@Override
					public void propertyChange(PropertyChangeEvent evt)
					{
						_liveAction.setChecked(_activeSolver.isLiveRunning());
					}
				};
				((Solver) _activeSolver).addPropertyChangeListener(
						ISolver.LIVE_RUNNING, 
						_liveRunningListener
				);						
			}
		}
		finally
		{
			_settingActiveSolver = false;
		}
	}
	
	private void selectSolutionGenerator(ISolutionGenerator solutionGenerator)
	{
		_saBinder.clear();
		_gaBinder.clear();
		
		_useSAButton.setSelection(false);
		_useGAButton.setSelection(false);
		if (solutionGenerator instanceof SwitchableSolutionGenerator) 
		{
			solutionGenerator = ((SwitchableSolutionGenerator) solutionGenerator)
					.getCurrentGenerator();
		}
		StackLayout layout = (StackLayout) _currentSolutionGeneratorComposite.getLayout();
		layout.topControl = null;
		setVisible(false, bfGraphControls);
		setVisible(false, gaGraphControls);
		
		if (solutionGenerator instanceof SASolutionGenerator)
		{
			_useSAButton.setSelection(true);
			_saBinder.bind((SASolutionGenerator) solutionGenerator);
			layout.topControl = _saParametersComposite;
		}
		if (solutionGenerator instanceof GASolutionGenerator)
		{
			_useGAButton.setSelection(true);
			_gaBinder.bind((GASolutionGenerator) solutionGenerator);
			layout.topControl = _gaParametersComposite;
			setVisible(true, gaGraphControls);
		}
		if (solutionGenerator instanceof BFSolutionGenerator)
		{
			setVisible(true, bfGraphControls);
		}
		_currentSolutionGeneratorComposite.layout();
	}
	
	private void setVisible(boolean visible, List<Control> controls)
	{
		for (Control control : controls)
		{
			control.setVisible(visible);
		}
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(_useFastCalculator);
		manager.add(new Separator());
		
		manager.add(_clearAction);
		manager.add(_restartAction);
		manager.add(new Separator());
		
		manager.add(_populateGoodAction);
		manager.add(new Separator());
		
		manager.add(_stepAction);
		manager.add(_playAction);
		manager.add(_liveAction);		
		manager.add(new Separator());
		
		manager.add(_saveAction);
		manager.add(_loadAction);
	}

	private void makeActions()
	{
		_populateGoodAction = new Action()
		{
			@Override
			public void run()
			{
				new TestSupport().loadGoodData(_activeSolver);
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
				_activeSolver.clear();
			}
		};
		_clearAction.setText("Clear");
		_clearAction.setToolTipText("Clear the track generator contributions");

		_liveAction = new Action("Live", SWT.TOGGLE)
		{

			@Override
			public void run()
			{
				_activeSolver.setLiveRunning(_liveAction.isChecked());
			}
		};

		_restartAction = new Action()
		{
			@Override
			public void run()
			{
				// clear the bounded states
				_activeSolver.getBoundsManager().restart();
			}
		};
		_restartAction.setText("Restart");
		_restartAction.setToolTipText("Reset the track generator");

		_stepAction = new Action()
		{
			@Override
			public void run()
			{
				_activeSolver.getBoundsManager().step();
			}
		};
		_stepAction.setText("Step");
		_stepAction.setToolTipText("Process the next contribution");

		_playAction = new Action()
		{
			@Override
			public void run()
			{
				_activeSolver.run(true, true);
			}
		};
		_playAction.setText("Play");
		_playAction.setToolTipText("Process all contributions");

		_saveAction = new Action("Save"){

			@Override
			public void run()
			{
				doSave();
			}
		};
		_saveAction.setToolTipText("Save solver to file");
		
		_loadAction = new Action("Load"){

			@Override
			public void run()
			{
				doLoad();
			}
		};
		_loadAction.setToolTipText("Load solver from file");
		
		_useFastCalculator = new Action("Fast Calc", SWT.TOGGLE)
		{

			@Override
			public void run()
			{
				GeoSupport.setCalculatorType(_useFastCalculator.isChecked() ? 
						GeoCalculatorType.FAST : GeoCalculatorType.ORIGINAL);				
			}			
		};
		_useFastCalculator.setChecked(true);
	}

	private void doLoad()
	{
		FileDialog dialog = new FileDialog(_shell, SWT.OPEN);
		dialog.setFilterExtensions(new String[]
		{ "*.xml" });
		String fileSelected = dialog.open();

		if (fileSelected != null)
		{
			InputStream inputStream;
			try
			{
				inputStream = new FileInputStream(fileSelected);
				XStreamReader reader = XStreamIO.newReader(inputStream, fileSelected);
				if (reader.isLoaded()) 
				{
					_activeSolver.load(reader);
					_activeSolver.run(true, false);
				}
			}
			catch (FileNotFoundException e)
			{
				LogFactory.getLog().error("Can't read input file:" + fileSelected, e);
			}
		}
	}

	private void doSave()
	{
		FileDialog dialog = new FileDialog(_shell, SWT.SAVE);
		dialog.setFilterExtensions(new String[]
		{ "*.xml" });

		String filename = dialog.open();
		if (filename == null)
		{
			return;
		}

  	XStreamWriter writer = XStreamIO.newWriter();
  	_activeSolver.save(writer);
		try
		{
			writer.process(new FileOutputStream(filename));
		}
		catch (FileNotFoundException e)
		{
			LogFactory.getLog().error("Can't find output file", e);
		}
	}	
	
	private abstract static class Binder<T> 
	{
		protected List<Binding> bindings;
		protected DataBindingContext context;
		
		public Binder(DataBindingContext context)
		{
			this.context = context;
			bindings = new ArrayList<Binding>();
		}
		
		protected void add(Binding binding)
		{
			bindings.add(binding);
		}
		
		public void clear() 
		{
			for (Binding binding : bindings)
			{
				context.removeBinding(binding);
			}
			bindings.clear();			
		}
		
		public void bind(T objectToBind) 
		{
			clear();
			doBind(objectToBind);
		}
		
		protected abstract void doBind(T objectToBind);
	}
}