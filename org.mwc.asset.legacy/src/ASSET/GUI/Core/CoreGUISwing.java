/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Oct 26, 2001
 * Time: 11:29:53 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.GUI.Core;

import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GUI.BaseLayer;
import MWC.GUI.Chart.Swing.SwingChart;
import MWC.GUI.Dialogs.SplashScreen;
import MWC.GUI.DragDrop.FileDropSupport;
import MWC.GUI.Layer;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;
import MWC.GUI.Swing.SwingStatusBar;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.Chart.Swing.SwingCursorPosition;
import MWC.GUI.Tools.Swing.MyMetalToolBarUI;
import MWC.GUI.Tools.Swing.SwingToolbar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;

abstract public class CoreGUISwing extends CoreGUI implements
		MyMetalToolBarUI.ToolbarOwner
{

	// /////////////////////////////////////////////
	// member variables
	// /////////////////////////////////////////////

	/**
	 * the whole of the panel we are contained in
	 */
	private JSplitPane _thePanel;

	/**
	 * the left-hand pane which contains the toolbar and message window
	 */
	private JSplitPane _theInfoPanel;

	/**
	 * the holder for the toolbars
	 */
	private JTabbedPane _toolbarHolder;

	/**
	 * the tabbed pane for our behaviour items
	 */
	protected JTabbedPane _behavioursHolder;

	/**
	 * the panel we use to show the current time
	 */
	protected JLabel _theTime;
	/**
	 * support for dropping observers into the props panel
	 */
	protected FileDropSupport _observerDropper = null;
	/**
	 * the set of observers which monitor this scenario
	 */
	private Vector<ScenarioObserver> _myObservers = new Vector<ScenarioObserver>(0, 1);

	// /////////////////////////////////////////////
	// constructor
	// /////////////////////////////////////////////
	/**
	 * constructor, of course
	 * 
	 * @param theParent
	 *          the toolparent = where we control the cursor from
	 */
	protected CoreGUISwing(final ASSETParent theParent)
	{
		super(theParent);

		// create the GUI
		initForm();

		// now build the toolbar
		buildTheInterface();

		// register our custom editors
		registerEditors();

		// just have a go at importing the default layers
		try
		{
			final java.io.File defLayers = new java.io.File("default_layers.xml");
			if (defLayers.exists())
				ASSETReaderWriter.importThis(_theData, new java.io.FileInputStream(
						defLayers));
		} catch (java.io.FileNotFoundException fe)
		{
			MWC.Utilities.Errors.Trace.trace(fe, "Whilst opening Default Layers");
		}

		_thePanel.doLayout();

	}

	/**
	 * register our ASSET-specific Swing editors
	 */
	public static void registerEditors()
	{
		PropertyEditorManager.registerEditor(Color.class,
				MWC.GUI.Properties.Swing.ColorPropertyEditor.class);

		// add our editors
		PropertyEditorManager.registerEditor(
				ASSET.Models.Decision.TargetType.class,
				ASSET.GUI.Editors.SwingTargetTypeEditor.class);
	}

	// /////////////////////////////////////////////
	// member functions
	// /////////////////////////////////////////////

	/**
	 * layout the controls within our panel
	 */
	private void initForm()
	{
		// create the panel
		_thePanel = new JSplitPane();

		// create the main components of the panel
		final SwingChart _theChart = new SwingChart(_theData,
				"images\\in_plot_logo.gif");

		// toolbar - we now have a tabbed panel of toolbars, to save space!
		_toolbarHolder = new MyToolbarHolder();
		_toolbarHolder.setName("Tools");

		// the properties panel
		_theProperties = new SwingPropertiesPanel(_theChart, getUndoBuffer(), super
				.getParent(), this);

		// create the general status bar (used for rng/brg measurements)
		_theStatusBar = new MWC.GUI.Swing.SwingStatusBar(_theProperties,
				getParent());

		// and configure the tote
		final JLabel cursorPos = new JLabel("000 00 00.00 N 000 00 00.00W");
		_theChart.addCursorMovedListener(new SwingCursorPosition(_theChart,
				cursorPos));

		// ///////////////////////////////////////////////
		// pass objects back to parent
		// ////////////////////////////////////////////////

		// inform the parent of the components
		setChart(_theChart);
		setProperties(_theProperties);
		setStatusBar(_theStatusBar);

		/***************************************************************************
		 * time display
		 **************************************************************************/
		_theTime = new JLabel("00:00.00");
		_theTime.setHorizontalAlignment(JLabel.CENTER);
		final Font newF = _theTime.getFont();
		_theTime.setFont(newF.deriveFont(18f));

		/***************************************************************************
		 * put the toobar into a stacked panel (with the current time in the bottom)
		 **************************************************************************/
		final JPanel combination = new JPanel();
		combination.setName("Controls");
		combination.setLayout(new GridLayout(2, 0));
		combination.add(_toolbarHolder);
		combination.add(_theTime);

		// ////////////////////////////////////////////////////
		// property editing bits
		// ///////////////////////////////////////////////////
		final JPanel thePropertiesHolder = new JPanel();
		thePropertiesHolder.setLayout(new BorderLayout());

		// try to put the properties into a toolbar parent
		thePropertiesHolder.add((Component) _theProperties, BorderLayout.CENTER);

		// Put in container to hold two text boxes at the foot of the page
		final JToolBar statuses = new JToolBar();
		statuses.setUI(new MWC.GUI.Tools.Swing.MyMetalToolBarUI(this));
		statuses.add((Component) _theStatusBar);
		statuses.add(cursorPos);
		thePropertiesHolder.add(statuses, BorderLayout.SOUTH);

		// ///////////////////////////////////////////////
		// ASSET bits
		// ///////////////////////////////////////////////
		final MyToolbarHolder assetHolder = new MyToolbarHolder();

		// put the toolbar combination panel in first
		assetHolder.add(combination);

		/**
		 * add any extra gui components
		 * 
		 */
		addUniqueComponents(assetHolder);

		// /////////////////////////////////////////////////////
		// layout bits
		// /////////////////////////////////////////////////////

		// set up the info panel
		_theInfoPanel = new JSplitPane();
		_theInfoPanel.setTopComponent(assetHolder);
		_theInfoPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		_theInfoPanel.setBottomComponent(thePropertiesHolder);

		// put the bits in the main panel
		_thePanel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		_thePanel.setLeftComponent(_theInfoPanel);
		_thePanel.setRightComponent(_theChart.getPanel());
		_thePanel.setContinuousLayout(false);
		_thePanel.setOneTouchExpandable(true);

		_thePanel.setDividerLocation(_theInfoPanel.getMinimumSize().width);

		assetHolder.doLayout();
		_thePanel.doLayout();

		// setup the listener for files being dropped
		_observerDropper = new FileDropSupport();
		_observerDropper.setFileDropListener(new FileDropSupport.FileDropListener()
		{
			public void FilesReceived(final Vector<File> files)
			{
				observerDropped(files);
			}
		}, ".XML");

		_observerDropper.addComponent(_theTime);

		// lastly the listener for the mouse wheel (zoom)
		setupMouseWheel();
	}

	/**
	 * setup the mouse wheel handler part of it
	 */
	private void setupMouseWheel()
	{
		final java.awt.event.MouseWheelListener theWheelListener = new java.awt.event.MouseWheelListener()
		{
			public void mouseWheelMoved(final java.awt.event.MouseWheelEvent e)
			{

				// find out if we are doing our "special" processing
				if (e.getModifiers() == MouseEvent.CTRL_MASK)
				{
					// set the scale factor

					double scale = 1.1;
					// are we zooming in our out?
					if (e.getWheelRotation() > 0)
						scale = 1 / scale;

					// set busy
					getParent().setCursor(java.awt.Cursor.WAIT_CURSOR);

					// create our action
					final Action action = new MWC.GUI.Tools.Chart.ZoomOut.ZoomOutAction(
							getChart(), getChart().getCanvas().getProjection().getDataArea(),
							scale);

					// do the zoom
					action.execute();

					// remember this action
					getParent().addActionToBuffer(action);

					// and restore the cursor
					getParent().restoreCursor();
				} else
				{
					// // we are moving forward/backward in time
					// final int rot = e.getWheelRotation();
					// final boolean fwd = (rot > 0);
					//
					// // are we doing a large step?
					// final boolean large_step = (e.getModifiers() ==
					// MouseEvent.SHIFT_MASK);
				}

			}
		};

		// and listen to the plt
		getChart().getPanel().addMouseWheelListener(theWheelListener);
	}

	/**
	 * show the splash-screen
	 */
	public static void showSplash(final Frame parent, final String imageName)
	{
		// do the splash
		new SplashScreen(parent, imageName, "Built:"
				+ ASSET.GUI.VersionInfo.getVersion(), Color.LIGHT_GRAY);
	}

	/**
	 * allow our child classes to add new gui components
	 */
	abstract protected void addUniqueComponents(
			CoreGUISwing.MyToolbarHolder assetHolder);

	/**
	 * return the Swing panel we are contained in
	 * 
	 * @return a Panel representing the View
	 */
	public Component getPanel()
	{
		return _thePanel;
	}

	public PropertiesPanel getProperties()
	{
		return _theProperties;
	}

	/**
	 * get ready to close, set all local references to null, to assist garbage
	 * collection
	 */
	public void close()
	{

		// get the parent to close first
		super.close();

		// now tidy up the object we manage
		if (_thePanel != null)
		{
			_thePanel.removeAll();
		}
		if (_theInfoPanel != null)
		{
			_theInfoPanel.removeAll();
		}
		if (_theToolbar != null)
		{
			((SwingToolbar) _theToolbar).removeAll();
		}
		if (_toolbarHolder != null)
		{
			_toolbarHolder.removeAll();
		}
		if (_theProperties != null)
		{
			((SwingPropertiesPanel) _theProperties).removeAll();
			((SwingPropertiesPanel) _theProperties).closeMe();

		}
		if (_theStatusBar != null)
		{
			((SwingStatusBar) _theStatusBar).removeAll();
		}
	}

	/**
	 * member method to create a toolbar button for this tool
	 * 
	 * @param item
	 *          the description of this tool
	 */
	protected void addThisTool(final MWC.GUI.Tools.MenuItemInfo item)
	{
		// see which toolbar this is on
		final String toolbar = item.getMenuName();

		SwingToolbar thisToolbar = null;

		// check if we have a toolbar for this tool
		final int index = _toolbarHolder.indexOfTab(toolbar);

		if (index == -1)
		{
			// we obviously have to create this toolbar, go for it
			thisToolbar = new SwingToolbar(MWC.GUI.Toolbar.HORIZONTAL, toolbar, this);
			thisToolbar.setLayout(new GridLayout(1, 0));

			// we also put it into a panel, to assist when its floating
			final JPanel jp = new JPanel();
			jp.setLayout(new BorderLayout());
			jp.setName(toolbar);
			jp.add("West", thisToolbar);

			// finally add the panel to the toolbar
			_toolbarHolder.add(toolbar, jp);
		} else
		{
			final JPanel holder = (JPanel) _toolbarHolder.getComponentAt(index);
			// find our component
			final int len = holder.getComponentCount();
			for (int i = 0; i < len; i++)
			{
				final Component cp = holder.getComponent(i);
				if (cp.getName().equals(holder.getName()))
				{
					thisToolbar = (SwingToolbar) cp;
					break;
				}
			}
			// thisToolbar = (SwingToolbar)_toolbarHolder.getComponentAt(index);
		}

		if (thisToolbar == null)
		{
			return;
		}

		// see if this is an action button, or it toggles as part of a group
		if (item.getToggleGroup() == null)
		{
			thisToolbar.addTool(item.getTool(), item.getShortCut(), item
					.getMnemonic());
		} else
		{
			thisToolbar.addToggleTool(item.getMenuName(), item.getTool(), item
					.getShortCut(), item.getMnemonic());
		}
	}

	/**
	 * our own implementation of a tabbed toolbar - the only difference is that
	 * when we drop it, we check that what we're receiving is in fact a dropped
	 * toolbar and set it's name correctly
	 * 
	 * @author administrator
	 */
	public static class MyToolbarHolder extends JTabbedPane
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor for the MyToolbarHolder object
		 */
		public MyToolbarHolder()
		{
			// shrink the font a little
			this.setFont(this.getFont().deriveFont(10.0f));
		}

		/**
		 * Description of the Method
		 * 
		 * @param title
		 *          Description of Parameter
		 * @param component
		 *          Description of Parameter
		 * @return Description of the Returned Value
		 */
		public Component add(final String title, final Component component)
		{
			Component res = null;
			// check if we are receiving a dropped toolbar
			if (title.equals("North"))
			{
				// a floating toolbar is being replaced - what can we do?
				res = super.add(component.getName(), component);
				this.setSelectedComponent(res);
			} else
			{
				// this is a normal drop operation, continue as normal
				res = super.add(title, component);
			}
			return res;
		}

	}

	/**
	 * class to put a GUI into a frame
	 */
	public static class ASSETParent implements ToolParent
	{
		/**
		 * the frame we plot
		 */
		private JFrame _frame;

		/**
		 * the undo buffer we manage
		 */
		final MWC.GUI.Undo.UndoBuffer _undoBuffer = new MWC.GUI.Undo.UndoBuffer();

		/**
		 * the application properties file we read from
		 */
		private MWC.GUI.Dialogs.ApplicationProperties _appProps;

		public ASSETParent(final JFrame frame)
		{
			_frame = frame;

			// sort out the application properties
			try
			{
				final String header = System.getProperty("line.separator")
						+ "#ASSET Properties File";
				_appProps = new MWC.GUI.Dialogs.ApplicationProperties("asset.prp",
						header);
			} catch (java.io.IOException e)
			{
				MWC.Utilities.Errors.Trace.trace(e);
			}
		}

		public void setCursor(final int theCursor)
		{
			_frame.setCursor(Cursor.getPredefinedCursor(theCursor));
		}

		public void setTitle(String name)
		{
			_frame.setTitle(name);
		}

		public void restoreCursor()
		{
			_frame.setCursor(Cursor.getDefaultCursor());
		}

		public void addActionToBuffer(final Action theAction)
		{
			_undoBuffer.add(theAction);
		}

		public String getProperty(final String name)
		{
			return _appProps.getProperty(name);
		}

		public java.util.Map<String, String> getPropertiesLike(final String pattern)
		{
			return _appProps.getPropertiesLike(pattern);
		}

		public void setProperty(String name, String value)
		{
			_appProps.setProperty(name, value);
		}

		public void logError(int status, String text, Exception e)
		{
			System.err.println("ASSET Problem:" + text);
			e.printStackTrace();
		}
	}

	protected void setTime(final String val)
	{
		_theTime.setText(val);
	}

	public void observerDropped(final Vector<File> files)
	{
		final Iterator<File> ii = files.iterator();
		while (ii.hasNext())
		{
			final File file = (File) ii.next();
			// read in this file

			try
			{
				Vector<ScenarioObserver> theObservers = ASSETReaderWriter.importThisObserverList(file
						.getName(), new java.io.FileInputStream(file));

				// check we have a layer for the observers
				Layer observers = checkObserverHolder();

				// add these observers to our scenario
				for (int i = 0; i < theObservers.size(); i++)
				{
					// get the next observer
					ScenarioObserver observer = (ScenarioObserver) theObservers
							.elementAt(i);

					// setup the observer
					observer.setup(_theScenario);

					// and add it to our list
					observers.add(observer);
				}

				_myObservers.addAll(theObservers);
			} catch (java.io.FileNotFoundException fe)
			{
				MWC.Utilities.Errors.Trace.trace(fe,
						"Reading in dragged participant file");
			}
		}
	}

	/**
	 * check that we have a layer for our observers
	 * 
	 * @return the layer (or a fresh one)
	 */
	private Layer checkObserverHolder()
	{
		Layer res = null;

		// do we have a layer for observers?
		res = _theData.findLayer("Observers");

		if (res == null)
		{
			res = new BaseLayer();
			res.setName("Observers");
			_theData.addThisLayer(res);
		}

		return res;

	}
}
