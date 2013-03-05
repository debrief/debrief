package com.planetmayo.debrief.satc_rcp.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.support.TestSupport;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

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
	private Action _liveAction;
	private Action _testOne;

	private TestSupport _testSupport;

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
		new Label(parent, SWT.None);

		_testSupport = new TestSupport();

		makeActions();
		contributeToActionBars();

		// disable our controls, until we find a genny
		boundsManager = SATC_Activator.getDefault().getService(IBoundsManager.class, true);
		startListeningTo();
	}

	private void enableControls(boolean enabled)
	{
		_clearAction.setEnabled(enabled);
		_populateShortAction.setEnabled(enabled);
		_populateLongAction.setEnabled(enabled);
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
		manager.add(_stepAction);
		manager.add(_playAction);
		manager.add(_liveAction);
		manager.add(_testOne);
	}

	private void loadSampleData(boolean useLong)
	{
		_testSupport.loadSampleData(useLong);
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
		_populateShortAction.setText("Populate Short");
		_populateShortAction.setToolTipText("Load some sample data");

		_populateLongAction = new Action()
		{
			@Override
			public void run()
			{
				loadSampleData(true);
			}
		};
		_populateLongAction.setText("Populate");
		_populateLongAction.setToolTipText("Load some sample data");

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
		_liveAction.setChecked(false);

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