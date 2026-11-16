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

package org.mwc.cmap.core;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mwc.cmap.core.CorePlugin.*;
import org.mwc.cmap.core.CorePlugin.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.operations.IUndoable;
import org.mwc.cmap.gridharness.DebriefFormattingOptionsPreferencesPage;
import org.mwc.cmap.gridharness.data.base60.Sexagesimal;
import org.mwc.cmap.gridharness.data.base60.SexagesimalFormat;
import org.mwc.cmap.gridharness.data.base60.SexagesimalSupport;
import org.osgi.framework.BundleContext;
import Debrief.GUI.Frames.Application;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Tools.Tote.Calculations.rangeCalc;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.Algorithms.EarthModel;
import MWC.GUI.Defaults;
import MWC.GUI.Defaults.PreferenceProvider;
import MWC.GUI.ToolParent;
import MWC.GUI.Chart.Painters.CoastPainter;
import MWC.GUI.JFreeChart.NewFormattedJFreeChart;
import MWC.GUI.Tools.Palette.CreateTOPO;
import MWC.GUI.Tools.Palette.CreateVPFLayers;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Features.VPFDatabaseHandler;
import interfaces.IEarthModelProvider;
import org.junit.jupiter.api.Test;

public final class CorePluginTest {
	@Test
	public void testNearGreenwich() {
		String clipboard = "Location\t 26\u00c2\u00b008'04.55\"N 021\u00c2\u00b056'56.52\"E";
		WorldLocation fromClipboard = fromClipboard(clipboard);
		assertEquals(26.134597222222222, fromClipboard.getLat(),
				"Expected 26 degrees, 08 minutes, 0.455 seconds north");
		assertEquals(21.949033333333333, fromClipboard.getLong(),
				"Expected 21 degrees, 56 minutes, 56.52 seconds north");

		clipboard = "Location Attribute with spaces\t 26\u00c2\u00b008'04.55\"N 021\u00c2\u00b056'56.52\"E";
		fromClipboard = fromClipboard(clipboard);
		assertEquals(26.134597222222222, fromClipboard.getLat(),
				"Expected 26 degrees, 08 minutes, 0.455 seconds north");
		assertEquals(21.949033333333333, fromClipboard.getLong(),
				"Expected 21 degrees, 56 minutes, 56.52 seconds north");
	}
}
