package org.mwc.cmap.gt2plot.views;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.gt2plot.Activator;

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

public class ViewTest extends ViewPart
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.mwc.cmap.gt2plot.views.SampleView";

	private Action action1;

	/**
	 * The constructor.
	 */
	public ViewTest()
	{
		try
		{
			URL url = Activator.getDefault().getBundle()
					.getEntry("data/50m_admin_0_countries.shp");
			String filePath = FileLocator.resolve(url).getFile();
			File file = new File(filePath);
			if (!file.exists())
				System.err.println("can't find file!!!");
		}
		catch (IOException e)
		{

		}
//
//		 FileDataStore store = FileDataStoreFinder.getDataStore(file);
//		 SimpleFeatureSource featureSource = store.getFeatureSource();
//
//		// Create a map content and add our shapefile to it
//		MapContent map = new MapContent();
//		map.setTitle("Quickstart");
//
//		Style style = SLD.createSimpleStyle(featureSource.getSchema());
//		Layer layer = new FeatureLayer(featureSource, style);
// map.addLayer(layer);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		Canvas canvas = new Canvas(parent, SWT.NONE);
		canvas.addPaintListener(new PaintListener()
		{

			public void paintControl(PaintEvent e)
			{
				paintMe(e);
			}
		});

		makeActions();
		contributeToActionBars();
	}

	private void paintMe(PaintEvent e)
	{
		double y2 = Math.random() * 120d;
		e.gc.drawLine(20, 40, 80, (int) y2);

	};

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(action1);
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(action1);
	}

	private void makeActions()
	{
		action1 = new Action()
		{
			public void run()
			{
				// _chart.rescale();
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
	}
}