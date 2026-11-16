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

package org.mwc.asset.scenariocontroller2;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mwc.asset.scenariocontroller2.MultiScenarioPresenter.*;
import org.mwc.asset.scenariocontroller2.MultiScenarioPresenter.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import javax.xml.xpath.XPathExpressionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.mwc.asset.scenariocontroller2.views.MultiScenarioView.UIDisplay;
import org.mwc.asset.scenariocontroller2.views.ScenarioWrapper;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.EditableWrapper;
import ASSET.ScenarioType;
import ASSET.GUI.CommandLine.CommandLine.ASSETProgressMonitor;
import ASSET.GUI.CommandLine.MultiScenarioCore;
import ASSET.GUI.CommandLine.MultiScenarioCore.InstanceWrapper;
import ASSET.Scenario.ScenarioRunningListener;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.Scenario.Observers.RecordToFileObserverType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.ASSETReaderWriter;
import ASSET.Util.XML.ScenarioHandler;
import ASSET.Util.XML.Control.StandaloneObserverListHandler;
import ASSET.Util.XML.Control.Observers.ScenarioControllerHandler;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import org.junit.jupiter.api.Test;

public class MultiScenarioPresenterTest {

	/**
	 * a stand-in for an interface: every method does nothing and returns null,
	 * false or zero, except getUI(), which returns the given value
	 */
	@SuppressWarnings("unchecked")
	private static <T> T stub(final Class<T> type, final Object getUIResult) {
		return (T) java.lang.reflect.Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type },
				(proxy, method, args) -> {
					if ("getUI".equals(method.getName())) {
						return getUIResult;
					}
					final Class<?> r = method.getReturnType();
					if (r == boolean.class) {
						return false;
					} else if (r.isPrimitive() && r != void.class) {
						return 0;
					}
					return null;
				});
	}


	@SuppressWarnings("synthetic-access")
	@Test
	public final void testRelativePathMethod() {
		final UIDisplay ui = stub(UIDisplay.class, null);
		final MultiScenarioDisplay display = stub(MultiScenarioDisplay.class, ui);
		final MultiScenarioPresenter pres = new MultiScenarioPresenter(display, null);

		assertEquals(false, pres.isRelativePath(new File("c:\\test.rep")), "failed to recognise drive");
		assertEquals(false, pres.isRelativePath(new File("\\test.rep")), "failed to root designator");
		assertEquals(false, pres.isRelativePath(new File("\\\\test.rep")), "failed to root designator");
		assertEquals(false, pres.isRelativePath(new File("//test.rep")), "failed to root designator");
		assertEquals(false, pres.isRelativePath(new File("////test.rep")), "failed to root designator");
		assertEquals(true, pres.isRelativePath(new File("test.rep")), "failed to recognise absolute ref");
		assertEquals(true, pres.isRelativePath(new File("./test.rep")), "failed to recognise relative ref");
		assertEquals(true, pres.isRelativePath(new File("../test.rep")), "failed to recognise parent ref");
	}
}
