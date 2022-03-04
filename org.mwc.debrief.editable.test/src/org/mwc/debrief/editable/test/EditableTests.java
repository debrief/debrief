/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.editable.test;

import java.awt.Color;
import java.beans.BeanDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Date;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.widgets.Display;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.mwc.cmap.core.ui_support.swt.SWTCanvasAdapter;
import org.mwc.cmap.naturalearth.Activator;
import org.mwc.cmap.naturalearth.wrapper.NELayer;
import org.mwc.debrief.satc_interface.data.SATC_Solution;
import org.mwc.debrief.satc_interface.data.wrappers.BMC_Wrapper;
import org.mwc.debrief.satc_interface.data.wrappers.FMC_Wrapper;
import org.osgi.framework.Bundle;

import com.bbn.openmap.layer.vpf.LibrarySelectionTable;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.CoreMeasurementContribution.CoreMeasurement;
import com.planetmayo.debrief.satc.model.contributions.FrequencyMeasurementContribution;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.manager.ISolversManager;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

import ASSET.GUI.SuperSearch.Plotters.SSGuiSupport;
import ASSET.GUI.Workbench.Plotters.ScenarioParticipantWrapper;
import ASSET.Models.Decision.Movement.RectangleWander;
import ASSET.Models.Vessels.SSN;
import ASSET.Participants.CoreParticipant;
import ASSET.Scenario.MultiForceScenario;
import ASSET.Util.SupportTesting;
import Debrief.GUI.Tote.StepControl;
import Debrief.GUI.Tote.Painters.PainterManager;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.PolygonWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.PlanningSegment;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.SplittableLayer;
import MWC.GUI.Editable;
import MWC.GUI.Editable.EditorType;
import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.Griddable;
import MWC.GUI.Chart.Painters.ETOPOPainter;
import MWC.GUI.ETOPO.ETOPO_2_Minute;
import MWC.GUI.JFreeChart.NewFormattedJFreeChart;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Shapes.ChartFolio;
import MWC.GUI.Shapes.CircleShape;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
import MWC.GUI.VPF.CoverageLayer;
import MWC.GUI.VPF.DebriefFeatureWarehouse;
import MWC.GUI.VPF.FeaturePainter;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import junit.framework.TestCase;

public class EditableTests extends TestCase {

	private static final String ORG_MWC_CMAP_LEGACY = "org.mwc.cmap.legacy";

	/**
	 * Process UI input but do not return for the specified time interval.
	 *
	 * @param waitTimeMillis the number of milliseconds
	 */
	protected static void delay(final long waitTimeMillis) {
		final Display display = Display.getCurrent();

		// If this is the user interface thread, then process input
		if (display != null) {
			final long endTimeMillis = System.currentTimeMillis() + waitTimeMillis;
			while (System.currentTimeMillis() < endTimeMillis) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.update();
		}

		// Otherwise perform a simple sleep
		else {
			try {
				Thread.sleep(waitTimeMillis);
			} catch (final InterruptedException e) {
				// ignored
			}
		}
	}

	/**
	 * test helper, to check that all of the object property getters/setters are
	 * there
	 *
	 * @param toBeTested
	 */
	public static void testTheseParameters(final Editable toBeTested) {
		if (toBeTested == null)
			return;

		// check if we received an object
		System.out.println("testing " + toBeTested.getClass());

		assertNotNull("Found editable object", toBeTested);

		final Editable.EditorType et = toBeTested.getInfo();

		if (et == null) {
			fail("no editor type returned for");
			return;
		}

		// first see if we return a custom bean descriptor
		final BeanDescriptor desc = et.getBeanDescriptor();

		// did we get one?
		if (desc != null) {
			final Class<?> editorClass = desc.getCustomizerClass();
			if (editorClass != null) {
				Object newInstance = null;
				try {
					newInstance = editorClass.newInstance();
				} catch (final InstantiationException e) {
					e.printStackTrace(); // To change body of catch statement use File
											// | Settings | File Templates.
				} catch (final IllegalAccessException e) {
					e.printStackTrace(); // To change body of catch statement use File
											// | Settings | File Templates.
				}
				// check it worked
				assertNotNull("we didn't create the custom editor for:", newInstance);
			} else {
				// there isn't a dedicated editor, try the custom ones.
				// do the edits
				PropertyDescriptor[] pd = null;
				try {
					pd = et.getPropertyDescriptors();
				} catch (final Exception e) {
					org.mwc.debrief.editable.test.Activator.log(e);
					fail("problem fetching property editors for " + toBeTested.getClass());
				}

				if (pd == null) {
					fail("problem fetching property editors for " + toBeTested.getClass());
					return;
				}

				final int len = pd.length;
				if (len == 0) {
					System.out.println("zero property editors found for " + toBeTested + ", " + toBeTested.getClass());
					return;
				}

				// griddable property descriptors
				if (et instanceof Griddable) {
					pd = null;
					try {
						pd = ((Griddable) et).getGriddablePropertyDescriptors();
					} catch (final Exception e) {
						org.mwc.debrief.editable.test.Activator.log(e);
						fail("problem fetching griddable property editors for " + toBeTested.getClass());
					}

					if (pd == null) {
						fail("problem fetching griddable property editors for " + toBeTested.getClass());
						return;
					}
				}
				// the method names are checked when creating PropertyDescriptor
				// we haven't to test them

			} // whether there was a customizer class
		} // whether there was a custom bean descriptor

		// now try out the methods
		final MethodDescriptor[] methods = et.getMethodDescriptors();
		if (methods != null) {
			for (int thisM = 0; thisM < methods.length; thisM++) {
				final MethodDescriptor method = methods[thisM];
				final Method thisOne = method.getMethod();
				final String theName = thisOne.getName();
				assertNotNull(theName);
			}
		}
	}

	public static void waitForJobs(final long maxIdle) {
		final long start = System.currentTimeMillis();
		while (!Job.getJobManager().isIdle()) {
			delay(1000);
			if ((System.currentTimeMillis() - start) > maxIdle) {
				final Job[] jobs = Job.getJobManager().find(null);
				final StringBuffer buffer = new StringBuffer();
				for (final Job job : jobs) {
					if (job.getThread() != null) {
						buffer.append(job.getName()).append(" (").append(job.getClass()).append(")\n");
					}
				}
				if (buffer.length() > 0)
					throw new RuntimeException("Invalid jobs found:" + buffer.toString()); //$NON-NLS-1$
			}
		}
	}

	public EditableTests(final String testName) {
		super(testName);
	}

	private Editable getEditable(final IType type) {
		Editable editable = null;
		switch (type.getFullyQualifiedName()) {
		case "Debrief.Wrappers.SensorWrapper":
			final SensorWrapper sensor = new SensorWrapper("tester");
			final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

			// and create the list of sensor contact data items
			cal.set(2001, 10, 4, 4, 4, 0);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 23);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
					null, null, 1, sensor.getName()));
			editable = sensor;
			break;
		case "MWC.GUI.Shapes.ChartFolio":
			editable = new ChartFolio(false, Color.white);
			break;
		case "org.mwc.cmap.naturalearth.wrapper.NELayer":
			editable = new NELayer(Activator.getDefault().getDefaultStyleSet());
			break;
		case "MWC.GUI.VPF.CoverageLayer$ReferenceCoverageLayer":
			final LibrarySelectionTable LST = null;
			final DebriefFeatureWarehouse myWarehouse = new DebriefFeatureWarehouse();
			final FeaturePainter fp = new FeaturePainter("libref", "Coastline");
			fp.setVisible(true);
			editable = new CoverageLayer.ReferenceCoverageLayer(LST, myWarehouse, "libref", "libref", "Coastline", fp);
			break;
		case "MWC.GUI.Chart.Painters.ETOPOPainter":
			editable = new ETOPOPainter("etopo", null);
			break;
		case "MWC.GUI.ETOPO.ETOPO_2_Minute":
			editable = new ETOPO_2_Minute("etopo");
			break;
		case "MWC.GUI.ExternallyManagedDataLayer":
			editable = new ExternallyManagedDataLayer("test", "test", "test");
			break;
		case "MWC.GUI.Shapes.CircleShape":
			editable = new CircleShape(new WorldLocation(2d, 2d, 2d), 2d);
			break;
		case "Debrief.Wrappers.Track.SplittableLayer":
			editable = new SplittableLayer(true);
			break;
		case "org.mwc.debrief.satc_interface.data.SATC_Solution":
			final ISolversManager solvMgr = SATC_Activator.getDefault().getService(ISolversManager.class, true);
			final ISolver newSolution = solvMgr.createSolver("test");
			editable = new SATC_Solution(newSolution);
			break;
		case "MWC.GUI.Shapes.PolygonShape":
			editable = new PolygonShape(null);
			break;
		case "ASSET.GUI.Painters.NoiseSourcePainter":
			editable = new ASSET.GUI.Painters.NoiseSourcePainter.PainterTest().getEditable();
			break;
		case "ASSET.GUI.Painters.ScenarioNoiseLevelPainter":
			editable = new ASSET.GUI.Painters.ScenarioNoiseLevelPainter.NoiseLevelTest().getEditable();
			break;
		case "ASSET.GUI.Workbench.Plotters.ScenarioParticipantWrapper":
			editable = new ScenarioParticipantWrapper(new SSN(12), null);
			break;
		case "Debrief.Wrappers.PolygonWrapper":
			// get centre of area
			WorldLocation centre = new WorldLocation(12, 12, 12);
			// create the shape, based on the centre
			final Vector<PolygonNode> path2 = new Vector<PolygonNode>();
			final PolygonShape newShape = new PolygonShape(path2);
			// and now wrap the shape
			final PolygonWrapper theWrapper = new PolygonWrapper("New Polygon", newShape, PlainShape.DEFAULT_COLOR,
					null);
			// store the new point
			newShape.add(new PolygonNode("1", centre, (PolygonShape) theWrapper.getShape()));
			editable = theWrapper;
			break;
		case "Debrief.Wrappers.Track.AbsoluteTMASegment":
			WorldSpeed speed = new WorldSpeed(5, WorldSpeed.Kts);
			double course = 33;
			WorldLocation origin = new WorldLocation(12, 12, 12);
			final HiResDate startTime = new HiResDate(11 * 60 * 1000);
			final HiResDate endTime = new HiResDate(17 * 60 * 1000);
			editable = new AbsoluteTMASegment(course, speed, origin, startTime, endTime);
			break;
		case "Debrief.Wrappers.Track.RelativeTMASegment":
			speed = new WorldSpeed(5, WorldSpeed.Kts);
			course = 33;
			final WorldVector offset = new WorldVector(12, 12, 0);
			editable = new RelativeTMASegment(course, speed, offset, null, null, null);
			break;
		case "Debrief.Wrappers.Track.PlanningSegment":
			speed = new WorldSpeed(5, WorldSpeed.Kts);
			course = 33;
			final WorldDistance worldDistance = new WorldDistance(5, WorldDistance.MINUTES);
			editable = new PlanningSegment("test", course, speed, worldDistance);
			break;
		case "org.mwc.debrief.satc_interface.data.wrappers.BMC_Wrapper":
			BearingMeasurementContribution bmc = new BearingMeasurementContribution();
			bmc.setName("Measured bearing");
			bmc.setAutoDetect(false);
			editable = new BMC_Wrapper(bmc);
			break;
		case "org.mwc.debrief.satc_interface.data.wrappers.FMC_Wrapper":
			FrequencyMeasurementContribution fmc = new FrequencyMeasurementContribution();
			fmc.setName("Measured frequence");
			editable = new FMC_Wrapper(fmc);
			break;
		case "Debrief.Wrappers.SensorContactWrapper":
			origin = new WorldLocation(0, 0, 0);
			editable = new SensorContactWrapper("blank track", new HiResDate(new java.util.Date().getTime()),
					new WorldDistance(1, WorldDistance.DEGS), 55d, origin, DebriefColors.RED, "my label", 1,
					"theSensorName");
			break;
		case "Debrief.Wrappers.FixWrapper":
			final Fix fx = new Fix(new HiResDate(12, 0), new WorldLocation(2d, 2d, 2d), 2d, 2d);
			final TrackWrapper tw = new TrackWrapper();
			tw.setName("here ew arw");
			final FixWrapper ed = new FixWrapper(fx);
			ed.setTrackWrapper(tw);
			editable = ed;
			break;
		case "Debrief.Wrappers.ShapeWrapper":
			centre = new WorldLocation(2d, 2d, 2d);
			editable = new ShapeWrapper("new ellipse", new EllipseShape(centre, 0,
					new WorldDistance(0, WorldDistance.DEGS), new WorldDistance(0, WorldDistance.DEGS)),
					DebriefColors.RED, null);
			break;
		case "Debrief.GUI.Tote.Painters.PainterManager":
			final StepControl stepper = new Debrief.GUI.Tote.Swing.SwingStepControl(null, null, null, null, null, null);
			editable = new PainterManager(stepper);
			break;
		case "Debrief.Wrappers.TMAContactWrapper":
			origin = new WorldLocation(2, 2, 0);
			final HiResDate theDTG = new HiResDate(new java.util.Date().getTime());
			final EllipseShape theEllipse = new EllipseShape(origin, 45, new WorldDistance(10, WorldDistance.DEGS),
					new WorldDistance(5, WorldDistance.DEGS));
			editable = new TMAContactWrapper("blank sensor", "blank track", theDTG, origin, 5d, 6d, 1d, Color.pink,
					"my label", theEllipse, "some symbol");
			break;
		case "MWC.GUI.JFreeChart.NewFormattedJFreeChart":
			final XYPlot plot = new XYPlot();
			final DefaultXYItemRenderer renderer = new DefaultXYItemRenderer();
			plot.setRenderer(renderer);
			editable = new NewFormattedJFreeChart("test", new java.awt.Font("Dialog", 0, 18), plot, false, null);
			break;
		case "org.mwc.cmap.core.ui_support.swt.SWTCanvasAdapter":
			final Editable[] edit = new Editable[1];
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					edit[0] = new SWTCanvasAdapter(null);
				}
			});
			editable = edit[0];
			break;
		case "ASSET.Models.Decision.Conditions.OrCondition":
			System.out.println(type.getFullyQualifiedName() + " hasn't public constructor.");
			return null;
		case "ASSET.Models.Decision.Movement.RectangleWander":
			final WorldLocation topLeft = SupportTesting.createLocation(0, 10000);
			final WorldLocation bottomRight = SupportTesting.createLocation(10000, 0);
			final WorldArea theArea = new WorldArea(topLeft, bottomRight);
			editable = new RectangleWander(theArea, "rect wander");
			break;
		case "org.mwc.debrief.satc_interface.data.wrappers.BMC_Wrapper$BearingMeasurementWrapper":
			bmc = new BearingMeasurementContribution();
			bmc.setName("Measured bearing");
			bmc.setAutoDetect(false);
			CoreMeasurement cm = new CoreMeasurement(new Date());
			final BMC_Wrapper bmcw = new BMC_Wrapper(bmc);
			editable = bmcw.new BearingMeasurementWrapper(cm);
			break;
		case "org.mwc.debrief.satc_interface.data.wrappers.FMC_Wrapper$FrequencyMeasurementEditable":
			fmc = new FrequencyMeasurementContribution();
			fmc.setName("Measured frequence");
			final FMC_Wrapper fmcw = new FMC_Wrapper(fmc);
			cm = new CoreMeasurement(new Date());
			editable = fmcw.new FrequencyMeasurementEditable(cm);
			break;
		case "ASSET.GUI.SuperSearch.Plotters.SSGuiSupport$ParticipantListener":
			final SSGuiSupport ssgs = new SSGuiSupport();
			ssgs.setScenario(new MultiForceScenario());
			editable = new SSGuiSupport.ParticipantListener(new CoreParticipant(12), ssgs);
			break;
		case "ASSET.GUI.Workbench.Plotters.BasePlottable":
			// skip it
			return null;
		case "MWC.TacticalData.GND.GTrack":
			// skip it
			return null;
		default:
			break;
		}

		if (editable != null) {
			return editable;
		}
		Class<?> clazz;
		try {
			clazz = Class.forName(type.getFullyQualifiedName());
		} catch (final ClassNotFoundException e1) {
			// e1.printStackTrace();
			System.out.println("CNFE " + e1.getMessage() + " " + type.getFullyQualifiedName());
			return null;
		}
		try {
			@SuppressWarnings("unused")
			final Method infoMethod = clazz.getDeclaredMethod("getInfo", new Class[0]);
		} catch (final Exception e) {
			return null;
		}
		try {
			editable = (Editable) clazz.newInstance();
		} catch (final Exception e) {
			final Constructor<?>[] constructors = clazz.getConstructors();
			for (final Constructor<?> constructor : constructors) {
				try {
					final Class<?>[] paramTypes = constructor.getParameterTypes();
					final Object[] params = new Object[paramTypes.length];
					for (int i = 0; i < paramTypes.length; i++) {
						final Class<?> paramType = paramTypes[i];
						if (HiResDate.class.equals(paramType)) {
							params[i] = new HiResDate(new Date());
						} else if (WorldDistance.class.equals(paramType)) {
							params[i] = new WorldDistance(12d, WorldDistance.DEGS);
						} else if (WorldSpeed.class.equals(paramType)) {
							params[i] = new WorldSpeed(12, WorldSpeed.M_sec);
						} else if (WorldLocation.class.equals(paramType)) {
							params[i] = new WorldLocation(12, 12, 12);
						} else if ("java.lang.String".equals(paramType.getName())) {
							params[i] = "test";
						} else if (!paramType.isPrimitive()) {
							params[i] = null;
						} else {
							if (paramType.equals(int.class)) {
								params[i] = new Integer("0");
							}
							if (paramType.equals(boolean.class)) {
								params[i] = Boolean.FALSE;
							}
							if (paramType.equals(long.class)) {
								params[i] = new Long("0");
							}
							if (paramType.equals(double.class)) {
								params[i] = new Double("0");
							}
							if (paramType.equals(float.class)) {
								params[i] = new Float("0");
							}
							if (paramType.equals(short.class)) {
								params[i] = new Short("0");
							}
						}
					}
					editable = (Editable) constructor.newInstance(params);
					break;
				} catch (final Exception e1) {
					// ignore
					// System.out.println(e1.getMessage());
					// e1.printStackTrace();
				}
			}
		}
		if (editable == null) {
			System.out.println("Can't instantiate type " + type.getFullyQualifiedName());
		}
		return editable;
	}

	private void openProjects(final File rootFile) throws CoreException {
		final File[] files = rootFile.listFiles(new FileFilter() {

			@Override
			public boolean accept(final File pathname) {
				if (!pathname.isDirectory()) {
					return false;
				}
				if (!pathname.getName().startsWith("org.mwc")) {
					return false;
				}
				if (pathname.getName().endsWith(".test") || pathname.getName().endsWith(".tests")
						|| pathname.getName().endsWith(".test2") || pathname.getName().endsWith(".feature")
						|| pathname.getName().endsWith(".site") || pathname.getName().endsWith(".media")
						|| pathname.getName().endsWith(".GNDManager")) {
					return false;
				}
				return true;
			}
		});
		if (files == null) {
			return;
		}
		for (final File file : files) {
			final File projectFile = new File(file, ".project");
			if (!projectFile.isFile()) {
				continue;
			}
			IProject project;
			final IPath path = new Path(projectFile.getAbsolutePath());
			final IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(path);
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
			project.create(description, null);
			project.open(null);
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		}
	}

	private void setAutoBuilding(final boolean flag) {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IWorkspaceDescription description = workspace.getDescription();
		description.setAutoBuilding(flag);
		try {
			workspace.setDescription(description);
		} catch (final CoreException e) {
			org.mwc.debrief.editable.test.Activator.log(e);
		}
	}

	/**
	 * Perform pre-test initialization
	 *
	 * @throws Exception
	 *
	 * @see TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		final Bundle bundle = Platform.getBundle(ORG_MWC_CMAP_LEGACY);
		final URL cmapLegacyURL = FileLocator.resolve(bundle.getEntry("/"));
		final File cmapLegacy = new File(cmapLegacyURL.getPath());
		File rootFile;
		if (cmapLegacy.isDirectory()) {
			rootFile = cmapLegacy.getParentFile();
		} else {
			final URL homeURL = FileLocator.resolve(Platform.getInstallLocation().getURL());
			final File home = new File(homeURL.getPath());
			// org.mwc.debrief.editable.test/target/work/../../..
			rootFile = home.getParentFile().getParentFile().getParentFile();
		}
		if (!rootFile.exists()) {
			return;
		}
		setAutoBuilding(false);
		openProjects(rootFile);
		setAutoBuilding(true);
		ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);
	}

	/**
	 * Perform post-test clean up
	 *
	 * @throws Exception
	 *
	 * @see TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		// Dispose of the test fixture
		waitForJobs();
		setAutoBuilding(false);
		final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (final IProject project : projects) {
			try {
				project.delete(false, true, null);
			} catch (final Exception e) {
				// ignore
			}
		}
		setAutoBuilding(false);
	}

	/**
	 * Run the editable properties
	 *
	 * @throws CoreException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public void testEditable()
			throws CoreException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		final IProgressMonitor monitor = new NullProgressMonitor();
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(ORG_MWC_CMAP_LEGACY);
		IJavaProject javaProject = null;
		if (project.exists()) {
			javaProject = JavaCore.create(project);
			javaProject.open(monitor);
		}
//		else
//		{
//			SearchablePluginsManager manager = PDECore.getDefault().getSearchablePluginsManager();
//			javaProject = manager.getProxyProject();
//		}
		final IType editableType = javaProject.findType("MWC.GUI.Editable");
		final ITypeHierarchy hierarchy = editableType.newTypeHierarchy(null);
		hierarchy.refresh(monitor);
		final IType[] subTypes = hierarchy.getAllSubtypes(editableType);
		for (final IType type : subTypes) {
			if (type.isClass() && !Flags.isAbstract(type.getFlags()) && Flags.isPublic(type.getFlags())) {
				// System.out.println(type.getFullyQualifiedName());
				final Editable editable = getEditable(type);
				if (editable == null) {
					continue;
				}
				EditorType info = null;
				try {
					info = editable.getInfo();
				} catch (final Exception e) {
					System.out.println("Info issue " + type.getFullyQualifiedName() + " " + e.getMessage());
					continue;
				}
				if (info == null) {
					continue;
				}
				// System.out.println("Testing " + type.getFullyQualifiedName());
				if ("org.mwc.cmap.core.ui_support.swt.SWTCanvasAdapter".equals(type.getFullyQualifiedName())) {
					final Editable ed = editable;
					Display.getDefault().syncExec(new Runnable() {

						@Override
						public void run() {
							testTheseParameters(ed);
						}
					});
				} else {
					testTheseParameters(editable);
				}
			}
		}
	}

	/**
	 * Wait until all background tasks are complete
	 */
	public void waitForJobs() {
		waitForJobs(60 * 60 * 1000);
	}

}
