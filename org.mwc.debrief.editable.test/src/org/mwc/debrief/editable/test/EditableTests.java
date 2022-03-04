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

}
