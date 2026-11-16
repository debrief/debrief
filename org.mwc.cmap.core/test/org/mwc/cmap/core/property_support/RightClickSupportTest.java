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

package org.mwc.cmap.core.property_support;

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.cmap.core.property_support.RightClickSupport.*;
import org.mwc.cmap.core.property_support.RightClickSupport.*;
import java.awt.Color;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.dnd.Clipboard;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.RightClickCutCopyAdaptor;
import org.mwc.cmap.core.operations.RightClickPasteAdaptor;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Editable.CategorisedPropertyDescriptor;
import MWC.GUI.Editable.EditorType;
import MWC.GUI.FireExtended;
import MWC.GUI.FireReformatted;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GUI.Tools.SubjectAction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.Utilities.ReaderWriter.PlainImporterBase;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class RightClickSupportTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testAdditionalNonePresent() {
		final ShapeWrapper sw = new ShapeWrapper("rect",
				new RectangleShape(new WorldLocation(12.1, 12.3, 12), new WorldLocation(1.1, 1.1, 12)), Color.red,
				new HiResDate(2222));
		final Editable[] editables = new Editable[] { sw };
		final MenuManager menu = new MenuManager("Holder");

		RightClickSupport.getDropdownListFor(menu, editables, null, null, null, true);

		boolean foundTransparent = false;

		// note: this next test may return 4 if run from within IDE,
		// some contributions provided by plugins
		assertEquals(2, menu.getSize(), 2, "Has items");

		final IContributionItem[] items = menu.getItems();
		for (int i = 0; i < items.length; i++) {
			final IContributionItem thisI = items[i];
			if (thisI instanceof MenuManager) {
				final MenuManager subMenu = (MenuManager) thisI;
				final IContributionItem[] subItems = subMenu.getItems();
				for (int j = 0; j < subItems.length; j++) {
					final IContributionItem subI = subItems[j];
					if (subI instanceof ActionContributionItem) {
						final ActionContributionItem ac = (ActionContributionItem) subI;
						final String theName = ac.getAction().getText();
						if (theName.equals("Semi transparent")) {
							foundTransparent = true;
						}
					}
				}
			}
		}

		assertTrue(foundTransparent, "The additional bean info got processed!");
	}

	@Test
	public final void testAdditionalSomePresent() {
		final LabelWrapper lw = new LabelWrapper("Some label", new WorldLocation(1.1, 1.1, 12), Color.red);
		final Editable[] editables = new Editable[] { lw };
		final MenuManager menu = new MenuManager("Holder");

		RightClickSupport.getDropdownListFor(menu, editables, null, null, null, true);

		// note: this next test may return 4 if run from within IDE,
		// some contributions provided by plugins
		assertEquals(2, menu.getSize(), 2, "Has items");

	}

	@Test
	public final void testIntersection() {
		try {
			final PropertyDescriptor[] demo = new PropertyDescriptor[] {};
			final PropertyDescriptor[] pa = new PropertyDescriptor[] {
					new PropertyDescriptor("Color", FixWrapper.class),
					new PropertyDescriptor("Font", FixWrapper.class),
					new PropertyDescriptor("Label", FixWrapper.class),
					new PropertyDescriptor("LabelShowing", FixWrapper.class),
					new PropertyDescriptor("Visible", FixWrapper.class) };
			final PropertyDescriptor[] pb = new PropertyDescriptor[] {
					new PropertyDescriptor("Color", FixWrapper.class),
					new PropertyDescriptor("Font", FixWrapper.class),
					new PropertyDescriptor("Label", FixWrapper.class),
					new PropertyDescriptor("LabelShowing", FixWrapper.class),
					new PropertyDescriptor("SymbolShowing", FixWrapper.class), };
			final PropertyDescriptor[] pc = new PropertyDescriptor[] {
					new PropertyDescriptor("LabelShowing", FixWrapper.class),
					new PropertyDescriptor("SymbolShowing", FixWrapper.class), };
			final PropertyDescriptor[] pd = new PropertyDescriptor[] {};

			PropertyDescriptor[] res = getIntersectionFor(pa, pb, demo);
			assertNotNull(res, "failed to find intersection");
			assertEquals(4, res.length, "Failed to find correct num");
			res = getIntersectionFor(res, pc, demo);
			assertNotNull(res, "failed to find intersection");
			assertEquals(1, res.length, "Failed to find correct num");
			res = getIntersectionFor(pa, pd, demo);
			assertNotNull(res, "failed to find intersection");
			assertEquals(0, res.length, "Failed to find correct num");
			res = getIntersectionFor(pd, pa, demo);
			assertNotNull(res, "failed to find intersection");
			assertEquals(0, res.length, "Failed to find correct num");
		} catch (final IntrospectionException e) {
			CorePlugin.logError(IStatus.ERROR, "Whilst doing tests", e);
			assertTrue(false, "threw some error");
		}
	}

	@Test
	public final void testPropMgt() {
		final Editable itemOne = new FixWrapper(new Fix(new HiResDate(122333), new WorldLocation(1, 2, 3), 12, 14));
		final Editable itemTwo = new FixWrapper(new Fix(new HiResDate(122334), new WorldLocation(1, 2, 5), 13, 12));
		final Editable itemThree = new SensorWrapper("alpha");
		final Editable[] lst = new Editable[] { itemOne, itemTwo };
		final Editable[] lst2 = new Editable[] { itemOne, itemThree };
		final Editable[] lst3 = new Editable[] { itemThree, itemOne, itemThree };
		final Editable[] lst4 = new Editable[] { itemThree, itemThree };
		final Editable[] lst5 = new Editable[] { itemOne };
		assertEquals(2, lst.length, "no data");
		PropertyDescriptor[] props = RightClickSupport.getCommonPropertiesFor(lst);
		assertNotNull(props, "found some data");
		assertEquals(14, props.length, "found right matches");
		props = RightClickSupport.getCommonPropertiesFor(lst2);
		assertNotNull(props, "found some data");
		assertEquals(1, props.length, "found right matches");
		props = RightClickSupport.getCommonPropertiesFor(lst3);
		assertNotNull(props, "found some data");
		assertEquals(1, props.length, "found right matches");
		props = RightClickSupport.getCommonPropertiesFor(lst4);
		assertNotNull(props, "found some data");
		assertEquals(10, props.length, "found right matches");
		props = RightClickSupport.getCommonPropertiesFor(lst5);
		assertNotNull(props, "found some data");
		assertEquals(14, props.length, "found right matches");
	}
}
