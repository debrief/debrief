package com.planetmayo.debrief.satc.views;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Date;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import com.planetmayo.debrief.satc.SATC_Activator;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContributionTest;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.SpeedForecastContribution;
import com.planetmayo.debrief.satc.model.generator.TrackGenerator;

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

	class NameSorter extends ViewerSorter
	{
	}

	class ViewContentProvider implements IStructuredContentProvider
	{
		@Override
		public void dispose()
		{
		}

		@Override
		public Object[] getElements(Object parent)
		{
			return new String[]
			{ "One", "Two", "Three" };
		}

		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput)
		{
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		@Override
		public Image getColumnImage(Object obj, int index)
		{
			return getImage(obj);
		}

		@Override
		public String getColumnText(Object obj, int index)
		{
			return getText(obj);
		}

		@Override
		public Image getImage(Object obj)
		{
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.planetmayo.debrief.satc.views.SampleView";
	private TableViewer viewer;

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view, or
	 * ignore it and always show the same content (like Task List, for example).
	 */

	private Action _restartAction;
	private Action _stepAction;
	private Action _playAction;
	private Action _populateShortAction;
	private Action _populateLongAction;

	private TrackGenerator _generator;

	/**
	 * The constructor.
	 */
	public TestHarnessView()
	{
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(viewer.getControl(), "com.planetmayo.debrief.satc.viewer");
		makeActions();
		hookContextMenu();
		contributeToActionBars();

		// hey, see if there's a track generator to listen to
		_generator = SATC_Activator.getDefault().getMockEngine().getGenerator();

		if (_generator == null)
			SATC_Activator.log(Status.ERROR, "Failed to find generator", null);
		else
			SATC_Activator.log(Status.INFO, "Found generator:", null);

		// did it work?
		if (_generator != null)
		{
			// ok, we can enable our buttons
			_populateShortAction.setEnabled(true);
			_populateLongAction.setEnabled(true);
			_restartAction.setEnabled(true);
			_stepAction.setEnabled(true);
			_playAction.setEnabled(true);
		}
	}

	private void fillContextMenu(IMenuManager manager)
	{
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(_populateShortAction);
		manager.add(_populateLongAction);
		manager.add(_restartAction);
		manager.add(_stepAction);
		manager.add(_playAction);
	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			@Override
			public void menuAboutToShow(IMenuManager manager)
			{
				TestHarnessView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
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

		_restartAction = new Action()
		{
			@Override
			public void run()
			{
				// clear the bounded states
				_generator.restart();
			}
		};
		_restartAction.setText("Restart");
		_restartAction.setToolTipText("Reset the track generator");

		_stepAction = new Action()
		{
			@Override
			public void run()
			{
				_generator.step();
			}
		};
		_stepAction.setText("Step");
		_stepAction.setToolTipText("Process the next contribution");

		_playAction = new Action()
		{
			@Override
			public void run()
			{
				_generator.run();
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
		viewer.getControl().setFocus();
	}
	

	@SuppressWarnings("deprecation")
	private void loadSampleData(boolean useLong)
	{
		// clear the geneartor first
		_generator.contributions().clear();
		
		// now load some data		
		BearingMeasurementContribution bmc = new BearingMeasurementContribution();
		Bundle bundle = Platform.getBundle(SATC_Activator.PLUGIN_ID);
		final String thePath;
		if(useLong)
			thePath= BearingMeasurementContributionTest.THE_PATH;
		else
			thePath= BearingMeasurementContributionTest.THE_SHORT_PATH;
			
		URL fileURL = bundle
				.getEntry(thePath);
		FileInputStream input;
		try
		{
			input = new FileInputStream(new File(FileLocator.resolve(fileURL)
					.toURI()));
			bmc.loadFrom(input);
			_generator.addContribution(bmc);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SpeedForecastContribution speed = new SpeedForecastContribution();
		speed.setMinSpeed(12);
		speed.setMaxSpeed(43);
		_generator.addContribution(speed);
		
		// hey, how about a time-bounded course constraint?
		CourseForecastContribution course = new CourseForecastContribution();
		course.setStartDate(new Date("2010/Jan/12 00:14:31"));
		course.setFinishDate(new Date("2010/Jan/12 00:18:25"));
		course.setMinCourse(45);
		course.setMaxCourse(81);
		_generator.addContribution(course);
		
		// hey, how about a time-bounded course constraint?
		SpeedForecastContribution speed2 = new SpeedForecastContribution();
		speed2.setStartDate(new Date("2010/Jan/12 00:25:00"));
		speed2.setFinishDate(new Date("2010/Jan/12 00:31:00"));
		speed2.setMinSpeed(8);
		speed2.setMaxSpeed(27);
		_generator.addContribution(course);

		
		
	}

}