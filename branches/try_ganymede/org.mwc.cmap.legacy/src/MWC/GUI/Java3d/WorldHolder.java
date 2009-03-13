/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Apr 12, 2002
 * Time: 12:31:19 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package MWC.GUI.Java3d;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;

import MWC.GUI.*;
import MWC.GUI.ETOPO.BathyProvider;
import MWC.GUI.Java3d.GUI.Control3d;
import MWC.GUI.Java3d.Tactical.Participant3D;
import MWC.GUI.Java3d.j3d.*;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Action;
import MWC.GenericData.*;

import com.sun.j3d.utils.universe.SimpleUniverse;

abstract public class WorldHolder extends JPanel implements ToolParent,
		Layers.DataListener
{

  ///////////////////////////
  // member variables
  ///////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the world we manage
	 */
	protected World _myWorld;

	/**
	 * the universe containing our world
	 */
	protected SimpleUniverse _simpleUniverse;

	/**
	 * the canvas which displays the world
	 */
	protected Canvas3D _myCanvas;

	/**
	 * the properties panel we draw ourselves into
	 */
	protected PropertiesPanel _myPanel = null;

	/**
	 * the manager which moves us through the selected views
	 */
	ViewpointManager _viewpointManager = null;

	/**
	 * the toolbar which lets us cycle through the views
	 */
	private SteppingViewpointToolbar _viewpointToolbar = null;

	/**
	 * the 3-d control panel
	 */
	protected Control3d _controller = null;

	/**
	 * the step-control which our chase planes may listen to
	 */
	protected StepperListener.StepperController _stepper;

	/**
	 * the layers object we listen to
	 */
	protected Layers _theLayers;

	/**
	 * the scale factor
	 */
	double z_factor = 5;

	/**
	 * working translation to keep
	 */
	Vector3d translation = new Vector3d();

	/**
	 * the transform group we operate on
	 */
	protected TransformGroup transformGroup = null;

	// /////////////////////////
	// constructor
	// /////////////////////////

	public WorldHolder(PropertiesPanel thePanel, StepperListener.StepperController stepper,
			Layers theLayers, BathyProvider bathyProvider, boolean liteVersion)
	{
		// store the stepper
		_stepper = stepper;

		// store the panel
		_myPanel = thePanel;

		// remember the layers object, and listen to it
		_theLayers = theLayers;
		_theLayers.addDataExtendedListener(this);
		_theLayers.addDataModifiedListener(this);
		_theLayers.addDataReformattedListener(this);

		// create the world
		buildWorld(theLayers.getBounds(), bathyProvider);

		// init the form
		initForm(liteVersion);

	}

	// /////////////////////////
	// member methods
	// /////////////////////////

	protected void buildWorld(WorldArea dataArea, BathyProvider bathyProvider)
	{

		GraphicsEnvironment theEnvironment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsConfiguration[] configs = theEnvironment.getDefaultScreenDevice()
				.getConfigurations();
		GraphicsConfigTemplate3D configTemplate = new GraphicsConfigTemplate3D();

		// REALLY IMPORTANT LINE, WHICH FIXES MATRIX VIDEO CARD PROBLEM
		// taken from Java bug id: 4737013
		configTemplate.setDepthSize(0);

		// GraphicsDevice screenDevice =
		// GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		// GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
		// GraphicsConfiguration gc = screenDevice.getBestConfiguration(template);

		// _myCanvas = new Canvas3D(gc)
		GraphicsConfiguration thisConf = configTemplate.getBestConfiguration(configs);
		_myCanvas = new Canvas3D(thisConf)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Dimension getMinimumSize()
			{
				return new Dimension(100, 100);
			}
		};
		_myWorld = new World(_myCanvas, dataArea, bathyProvider);

		// SimpleUniverse is a Convenience Utility class - create it here since we
		// need to use it when we detach the world
		_simpleUniverse = new SimpleUniverse(_myCanvas);
	}

	public void finish()
	{

		// set the back clipping bit
		_simpleUniverse.getViewer().getView().setBackClipDistance(3000);

		// This will move the ViewPlatform back a bit
		_simpleUniverse.getViewingPlatform().setNominalViewingTransform();
		// _simpleUniverse.addBranchGraph(_myWorld);

		// finish setting up the views
		getViewportManager().setViewInfo(_myCanvas.getView(), getWorld()._parentTransform);

		// finish setting up the 3-d controller
		_controller.setTransform(getWorld()._parentTransform, getWorld().getProjection());

		// and transport ourselves to one of the views
		_viewpointToolbar.selectFirstView();

	}

	protected void initForm(boolean liteVersion)
	{
		//
		setLayout(new GridLayout(1, 0));

		// put the south panels into a box
		Box southPanel = new Box(BoxLayout.X_AXIS)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Dimension getMaximumSize()
			{
				return super.getMinimumSize();
			}
		};
		southPanel.add(getMouseControls());
		southPanel.add(getPresets(liteVersion));
		southPanel.add(get3dControls());

		// split the interface
		JSplitPane holderSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		holderSplit.setOneTouchExpandable(true);
		_myCanvas.setSize(400, 400);
		holderSplit.setResizeWeight(1d);

		// insert our components into the panel
		holderSplit.add(_myCanvas);
		holderSplit.add(southPanel);
		this.add(holderSplit);

	}

	// ////////////////////////////
	// add a new feature which we should provide a chase-plane view for
	// ///////////////////////////
	public void addTrack(Participant3D track)
	{
		// wrap the object in a transform which returns the chase-plane view
		ChasePlaneTransform ttf = new ChasePlaneTransform(track, _myWorld);
		ViewpointData vd = new ViewpointData(track.getName(), 0, ttf);
		vd.userData = track;

		// put this track into our view holder
		_viewpointToolbar.appendViewpoint(vd);

		// store the track
		// _myWorld.addThisTrack(track);

		// and update the track
		if (_stepper != null)
		{
			HiResDate time_now = _stepper.getCurrentTime();
			track.updated(time_now);
		}
	}

	// /////////////////////////
	// embedded class to provide a transform based on the current location of a
	// participant
	// /////////////////////////

	private static class ChasePlaneTransform extends TransformGroup
	{
		Participant3D _myTrack = null;

		World _theWorld = null;

		public ChasePlaneTransform(Participant3D track, World theWorld)
		{
			this._myTrack = track;
			_theWorld = theWorld;
		}

		public void getTransform(Transform3D d)
		{
			// get the location
			WorldLocation loc = _myTrack.getLocation();
			double course_rads = _myTrack.getCourse();

			// just check if a location was returned
			if (loc == null)
			{
				super.getTransform(d);
				return;
			}

			// produce a transform from the location
			Point3d p3 = _theWorld.toScreen(loc);
			Transform3D t3 = new Transform3D();
			t3.setTranslation(new Vector3d(p3));

			// produce a rotation from the course
			double cosR = Math.cos(course_rads);
			double sinR = Math.sin(course_rads);
			Matrix3d rotation_matrix = new Matrix3d(cosR, 0, sinR, 0, 1, 0, -sinR, 0, cosR);
			t3.setRotation(rotation_matrix);

			// now produce an offset to represent the "eye" behind the observer
			Point3d p4 = new Point3d(0, 0.5, 3);
			Vector3d pointWrapper = new Vector3d(p4);
			Matrix3d m3 = new Matrix3d();
			m3.setRow(0, pointWrapper);

			// multiply by the rotation
			m3.mul(rotation_matrix);

			// get the point back
			m3.getRow(0, pointWrapper);

			// and put in into a point
			p4.set(pointWrapper);

			// move p4 up a little
			p4.add(p3);
			t3.lookAt(p4, p3, new Vector3d(0, 1, 0));

			// ok, return the result
			d.set(t3);
		}
	}

	protected JComponent get3dControls()
	{
		_controller = new Control3d();
		JPanel controlHolder = new JPanel();
		controlHolder.setLayout(new BorderLayout());
		// controlHolder.add("North", new JLabel("3D Controls", JLabel.CENTER));
		controlHolder.add("Center", _controller);
		controlHolder.setBorder(BorderFactory.createRaisedBevelBorder());

		return controlHolder;

	}

	private Icon loadIcon(String path)
	{
		Icon res = null;

		java.lang.ClassLoader loader = getClass().getClassLoader();
		java.net.URL myURL = null;
		if (loader != null)
		{
			myURL = loader.getResource(path);
			if (myURL != null)
				res = new ImageIcon(myURL);
		}

		return res;
	}

	protected JComponent getMouseControls()
	{
		JPanel mouseControls = new JPanel();
		mouseControls.setLayout(new BorderLayout());
		JLabel mouseImage = null;
		mouseControls.add("North", new JLabel("Mouse Controls", JLabel.CENTER));

		Icon mouseIcon = loadIcon("images/mouse_help.gif");

		if (mouseImage == null)
			mouseImage = new JLabel(mouseIcon);

		mouseControls.add("Center", mouseImage);
		mouseImage
				.setToolTipText("This diagram shows which mouse-button is used to control which type of motion on the plot");
		mouseImage.setBorder(BorderFactory.createEtchedBorder());

		mouseControls.setBorder(BorderFactory.createRaisedBevelBorder());
		return mouseControls;
	}

	private class SteppingViewpointToolbar extends ViewpointToolbar implements
			StepperListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private StepperListener.StepperController _myStepper;

		public SteppingViewpointToolbar(ViewpointData[] vps,
				StepperListener.StepperController stepper)
		{
			super(vps);
			_myStepper = stepper;
			if (_myStepper != null)
				_myStepper.addStepperListener(this);
		}

		public void doClose()
		{
			if (_myStepper != null)
				_myStepper.removeStepperListener(this);
		}

		public void newTime(HiResDate oldDTG, HiResDate newDTG, CanvasType canvas)
		{
			try
			{

				// is our current viewpoint a dynamic one?
				ViewpointData _theCurrentView = getCurrent();

				TransformGroup tg = _theCurrentView.viewTg;

				if (tg instanceof ChasePlaneTransform)
				{
					// so, we are in a chase plane. update the view
					if (viewpointListener != null)
					{
						// tell the manager that we want to jump to the next step. We were
						// using a time-delta of zero,
						// but when moving forward very quickly using the wheel the plot was
						// falling over. When
						// using the step-button, the problem didn't arise. So, we're
						// slowing the transitions down
						// just a little, and it seems to have fixed the problem. Cool.
						// I suspect this was a processing bug in the 3-d library.
						_viewpointManager.setTransitionTime(10);

						viewpointListener.viewpointSelected(_theCurrentView);

						// restore the standard transition time
						_viewpointManager.setTransitionTime(2000);
					}
				}
			}
			catch (Exception e)
			{
				MWC.Utilities.Errors.Trace.trace(e, "Whilst 3-d stepping");
			}

		}

		public void steppingModeChanged(boolean on)
		{
			// ignore
		}

	}

	protected JComponent getPresets(boolean liteVersion)
	{
		JPanel presetPanel = new JPanel();
		presetPanel.setLayout(new BorderLayout());

		// sort out the view transitions
		_viewpointToolbar = new SteppingViewpointToolbar(createViews(), _stepper);

		_viewpointManager = new ViewpointManager();
		_viewpointManager.setToolbar(_viewpointToolbar);

		_viewpointToolbar.setBorder(BorderFactory.createRaisedBevelBorder());
		presetPanel.add("Center", _viewpointToolbar);

		if (!liteVersion)
		{
			JButton editThis = new JButton(loadIcon("images/properties.gif"));
			editThis.setToolTipText("To edit the 3D view properties");
			editThis.setMargin(new Insets(1, 1, 1, 1));
			editThis.setBorder(BorderFactory.createRaisedBevelBorder());
			editThis.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					_myPanel.addEditor(_myWorld.getWorldPlottingOptions().getInfo(), null);
				}
			});
			presetPanel.add("South", editThis);
		}

		// collate the icons

		return presetPanel;
	}

	/**
	 * create the list of transitions we view
	 */
	protected ViewpointData[] createViews()
	{
		ViewpointData[] res = new ViewpointData[3];
		Transform3D t3 = new Transform3D();
		t3.lookAt(new Point3d(0, 5, 140), new Point3d(0, 5, 0), new Vector3d(0, 1, 0));
		res[0] = new ViewpointData("Surface look", 0, new TransformGroup(t3));
		t3.lookAt(new Point3d(0, -5, 140), new Point3d(0, -5, 0), new Vector3d(0, 1, 0));
		res[1] = new ViewpointData("Underwater look", 0, new TransformGroup(t3));
		// t3.lookAt(new Point3d(0,140,0), new Point3d(0,0,0), new Vector3d(0,0,-1)
		// );
		t3.lookAt(new Point3d(0, 840, 0), new Point3d(0, 0, 0), new Vector3d(0, 0, -1));
		res[2] = new ViewpointData("Top down", 0, new TransformGroup(t3));
		return res;
	}

	/**
	 * get the world item we store
	 */
	public World getWorld()
	{
		return _myWorld;
	}

	/**
	 * get our universe
	 */
	public SimpleUniverse getUniverse()
	{
		return _simpleUniverse;
	}

	/**
	 * get the _viewpointManager for the list of viewports
	 */
	public ViewpointManager getViewportManager()
	{
		return _viewpointManager;
	}

	public static WorldHolder createWorldFrame(final WorldHolder world)
	{
		JFrame fr = new JFrame("world viewer");
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.getContentPane().setLayout(new BorderLayout());
		fr.getContentPane().add("Center", world);
		fr.setSize(900, 400);

		fr.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				world.doClose();
			}
		});

		fr.setVisible(true);
		// fr.pack();

		return world;
	}

	/**
	 * close operation, stop all listeners
	 */
	public void doClose()
	{
		_myWorld.doClose();
		_controller.doClose();
		_viewpointToolbar.doClose();

		// stop listening to the layers
		_theLayers.removeDataExtendedListener(this);
		_theLayers.removeDataReformattedListener(this);
		_theLayers.removeDataModifiedListener(this);

	}

	// /////////////////////////
	// ToolParent support
	// /////////////////////////
	public void addActionToBuffer(Action theAction)
	{
		//
	}

	public Map<String, String> getPropertiesLike(String pattern)
	{
		return null;
	}

	public String getProperty(String name)
	{
		return null;
	}

	public void restoreCursor()
	{
	}

	public void setCursor(int theCursor)
	{
		//
	}

	public void setProperty(String name, String value)
	{
		//
	}

	/**
	 * remove this track
	 */
	public void removeThisTrack(Participant3D track)
	{
		// first, remove it from the view toolbar
		// get the list of data in the combo box
		DefaultComboBoxModel model = _viewpointToolbar.getViewpointModel();
		for (int i = 0; i < model.getSize(); i++)
		{
			Object o = model.getElementAt(i);
			ViewpointData vd = (ViewpointData) o;
			if (vd.userData == track)
			{
				model.removeElementAt(i);
				break;
			}
		}
	}

	/**
	 * utility class to make us zoom and and out of the plot. This method is of
	 * particular use to the mouse-wheel zoom functions
	 * 
	 * @param amountRotated
	 */
	public void doZoom(int amountRotated)
	{

		Transform3D currXform = new Transform3D();

		// get the transform group
		transformGroup = getWorld().getTransform();

		transformGroup.getTransform(currXform);

		translation.z = amountRotated * z_factor;

		Transform3D transformX = new Transform3D();

		transformX.set(translation);

		currXform.mul(transformX, currXform);

		transformGroup.setTransform(currXform);
	}

	/**
	 * @param status
	 * @param text
	 * @param e
	 */
	public void logError(int status, String text, Exception e)
	{
		System.out.println("Error:" + text);
	}

	// /////////////////////////
	// testing code
	// /////////////////////////

}
