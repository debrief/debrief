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

package org.mwc.debrief.core.editors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.debrief.core.editors.PlotEditor.*;
import org.mwc.debrief.core.editors.PlotEditor.*;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.team.internal.core.history.LocalFileRevision;
import org.eclipse.team.internal.ui.history.FileRevisionEditorInput;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.OpenCheatSheetAction;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlProperties;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.interfaces.INamedItem;
import org.mwc.cmap.core.interfaces.TimeControllerOperation.TimeControllerOperationStore;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.property_support.RightClickSupport;
import org.mwc.cmap.media.utility.OpenVideoPlayerUtil;
import org.mwc.cmap.plotViewer.actions.Pan;
import org.mwc.cmap.plotViewer.actions.Pan.PanMode;
import org.mwc.cmap.plotViewer.actions.RangeBearing;
import org.mwc.cmap.plotViewer.actions.RangeBearing.RangeBearingMode;
import org.mwc.cmap.plotViewer.actions.ZoomIn;
import org.mwc.cmap.plotViewer.actions.ZoomIn.ZoomInMode;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.ContextOperations.RainbowShadeSonarCuts.ShadeCutsOperation;
import org.mwc.debrief.core.ContextOperations.RainbowShadeSonarCuts.ShadeOperation;
import org.mwc.debrief.core.actions.DragComponent;
import org.mwc.debrief.core.actions.DragComponent.DragComponentMode;
import org.mwc.debrief.core.actions.DragFeature;
import org.mwc.debrief.core.actions.DragFeature.DragFeatureMode;
import org.mwc.debrief.core.actions.DragSegment;
import org.mwc.debrief.core.actions.DragSegment.DragSegmentMode;
import org.mwc.debrief.core.actions.RadioHandler;
import org.mwc.debrief.core.editors.painters.LayerPainterManager;
import org.mwc.debrief.core.interfaces.IPlotLoader;
import org.mwc.debrief.core.interfaces.IPlotLoader.BaseLoader;
import org.mwc.debrief.core.interfaces.IPlotLoader.CompleteListener;
import org.mwc.debrief.core.interfaces.IPlotLoader.DeferredPlotLoader;
import org.mwc.debrief.core.loaders.LoaderManager;
import org.mwc.debrief.core.loaders.ReplayLoader;
import org.mwc.debrief.core.loaders.xml_handlers.DebriefEclipseXMLReaderWriter;
import org.mwc.debrief.core.operations.ExportDopplerShift;
import org.mwc.debrief.core.operations.ExportTimeDataToClipboard;
import org.mwc.debrief.core.operations.ExportToFlatFile;
import org.mwc.debrief.core.operations.ExportToFlatFile2;
import org.mwc.debrief.core.preferences.PrefsPage;
import org.osgi.framework.Bundle;
import Debrief.GUI.Tote.Painters.TotePainter;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.DynamicInfillSegment;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.PlainProjection.RelativeProjectionParent;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.CreateEditorForParent;
import MWC.GUI.DynamicPlottable;
import MWC.GUI.Editable;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.TrackDataProvider;
import MWC.TacticalData.TrackDataProvider.TrackDataListener;
import MWC.TacticalData.temporal.ControllablePeriod;
import MWC.TacticalData.temporal.ControllableTime;
import MWC.TacticalData.temporal.PlotOperations;
import MWC.TacticalData.temporal.TimeControlPreferences;
import MWC.TacticalData.temporal.TimeManager;
import MWC.TacticalData.temporal.TimeProvider;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import org.junit.jupiter.api.Test;

public class PlotEditorTest {

	@Test
	public void testAmbig() {
		final SensorWrapper sensor = new SensorWrapper("Some name");

		assertFalse(isAmbiguousData(sensor), "Should not find ambig data");

		// give it some none freq data
		sensor.add(new SensorContactWrapper("the track", new HiResDate(1000000), null, 100d, null, null, null,
				Color.RED, "label", 1, "Some name"));

		assertFalse(isAmbiguousData(sensor), "Should still not find ambig data");

		// and another cut
		sensor.add(new SensorContactWrapper("the track", new HiResDate(1003000), null, 100d, null, null, null,
				Color.RED, "label", 1, "Some name"));

		assertFalse(isAmbiguousData(sensor), "Should still not find ambig data");

		// clear the cuts
		sensor.removeElement(sensor.elements().nextElement());
		sensor.removeElement(sensor.elements().nextElement());

		assertEquals(0, sensor.size(), "now empty");

		// give it some freq data
		sensor.add(new SensorContactWrapper("the track", new HiResDate(1000000), null, 122d, 222d, null, null,
				Color.RED, "label", 1, "Some name"));

		assertTrue(isAmbiguousData(sensor), "Should find ambig data");
	}

	@Test
	public void testBounds() {
		final Layers la = new Layers();
		final TimePeriod bounds = getPeriodFor(la);
		assertNull(bounds, "should not have found any");

		final Layers la2 = null;
		final TimePeriod bounds2 = getPeriodFor(la2);
		assertNull(bounds2, "should not have found any");
	}

	@Test
	public void testFreq() {
		final SensorWrapper sensor = new SensorWrapper("Some name");

		assertFalse(hasFrequencyData(sensor), "Should not find freq data");

		// give it some none freq data
		sensor.add(new SensorContactWrapper("the track", new HiResDate(1000000), null, 100d, 200d, null, null,
				Color.RED, "label", 1, "Some name"));

		assertFalse(hasFrequencyData(sensor), "Should still not find freq data");

		// and another cut
		sensor.add(new SensorContactWrapper("the track", new HiResDate(1003000), null, 100d, 200d, null, null,
				Color.RED, "label", 1, "Some name"));

		assertFalse(hasFrequencyData(sensor), "Should still not find freq data");

		// clear the cuts
		sensor.removeElement(sensor.elements().nextElement());
		sensor.removeElement(sensor.elements().nextElement());

		assertEquals(0, sensor.size(), "now empty");

		// give it some freq data
		sensor.add(new SensorContactWrapper("the track", new HiResDate(1000000), null, null, null, 22d, null,
				Color.RED, "label", 1, "Some name"));

		assertTrue(hasFrequencyData(sensor), "Should find freq data");
	}
}
