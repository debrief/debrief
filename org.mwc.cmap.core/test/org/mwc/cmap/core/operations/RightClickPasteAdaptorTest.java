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

package org.mwc.cmap.core.operations;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mwc.cmap.core.operations.RightClickPasteAdaptor.*;
import org.mwc.cmap.core.operations.RightClickPasteAdaptor.*;
import java.awt.Color;
import java.util.Enumeration;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.RightClickCutCopyAdaptor.EditableTransfer;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeSetWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanEnumerate;
import MWC.GUI.DynamicLayer;
import MWC.GUI.DynamicPlottable;
import MWC.GUI.Editable;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Renamable;
import MWC.GUI.Tools.Operations.RightClickPasteAdaptor.NeedsTidyingOnPaste;
import MWC.GenericData.WorldLocation;
import org.junit.jupiter.api.Test;

public class RightClickPasteAdaptorTest {
	@Test
	public void testCreateAction() {
		final Layers layers = new Layers();
		final BaseLayer layer1 = new BaseLayer();
		layer1.setName("name");
		final BaseLayer layer2 = new BaseLayer();
		layer2.setName("name");
		final BaseLayer layer3 = new BaseLayer();
		layer3.setName("name");
		final Editable[] items = new Editable[] { layer1, layer2, layer3 };
		final Clipboard clipboard = null;
		final Layer destination = null;

		final PasteItem action = createAction(destination, layers, clipboard, items);

		assertEquals(0, layers.size(), "Check empty");

		action.run();

		assertEquals(3, layers.size(), "Check not empty");
	}

	@Test
	public void testDynamicInValid() {
		final Layers layers = new Layers();
		final BaseLayer layer1 = new BaseLayer();
		layer1.setName("name");
		final BaseLayer layer2 = new BaseLayer();
		layer2.setName("name");
		final BaseLayer layer3 = new BaseLayer();
		layer3.setName("name");
		final Editable[] items = new Editable[] { layer1, layer2, layer3 };
		final Clipboard clipboard = null;
		final DynamicLayer destination = new DynamicLayer();

		final PasteItem action = createAction(destination, layers, clipboard, items);

		assertNull(action, "failed to create action");
	}

	@Test
	public void testDynamicValid() {
		final Layers layers = new Layers();
		final DynamicTrackShapeSetWrapper layer1 = new DynamicTrackShapeSetWrapper("title");
		layer1.setName("name");
		final Editable[] items = new Editable[] { layer1 };
		final Clipboard clipboard = null;
		final DynamicLayer destination = new DynamicLayer();
		final PasteItem action = createAction(destination, layers, clipboard, items);
		assertNull(action, "failed to create action");
	}

	@Test
	public void testLabels() {
		final Layers layers = new Layers();
		final BaseLayer shapes = new BaseLayer();
		final LabelWrapper label = new LabelWrapper("label", new WorldLocation(1d, 1d, 1d), Color.red);
		final LabelWrapper label2 = new LabelWrapper("label", new WorldLocation(1d, 1d, 1d), Color.red);
		final LabelWrapper label3 = new LabelWrapper("label", new WorldLocation(1d, 1d, 1d), Color.red);
		final Editable[] items = new Editable[] { label, label2, label3 };
		final Layer destination = shapes;
		final PasteItem paste = new PasteItem(items, destination, layers);

		assertEquals(0, shapes.size(), "starts empty");

		paste.run();

		assertEquals(3, shapes.size(), "now not empty");

		final Enumeration<Editable> numer = shapes.elements();
		final Editable i1 = numer.nextElement();
		final Editable i2 = numer.nextElement();
		final Editable i3 = numer.nextElement();

		assertEquals("Copy of Copy of label", i1.getName(), "was renamed");
		assertEquals("Copy of label", i2.getName(), "was renamed");
		assertEquals("label", i3.getName(), "was renamed");
	}

	@Test
	public void testLayers() {
		final Layers layers = new Layers();
		final BaseLayer layer1 = new BaseLayer();
		layer1.setName("name");
		final BaseLayer layer2 = new BaseLayer();
		layer2.setName("name");
		final BaseLayer layer3 = new BaseLayer();
		layer3.setName("name");
		final Editable[] items = new Editable[] { layer1, layer2, layer3 };
		final Clipboard clipboard = null;
		final Layer destination = null;
		final PasteItem paste = new PasteLayer(items, clipboard, destination, layers);

		assertEquals(0, layers.size(), "starts empty");

		paste.run();

		assertEquals(3, layers.size(), "now not empty");

		final Enumeration<Editable> numer = layers.elements();
		final Editable i1 = numer.nextElement();
		final Editable i2 = numer.nextElement();
		final Editable i3 = numer.nextElement();

		assertEquals("Copy of Copy of name", i3.getName(), "was renamed");
		assertEquals("Copy of name", i2.getName(), "was renamed");
		assertEquals("name", i1.getName(), "was renamed");
	}
}
