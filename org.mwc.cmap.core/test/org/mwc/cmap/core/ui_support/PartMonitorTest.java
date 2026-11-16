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

package org.mwc.cmap.core.ui_support;

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.cmap.core.ui_support.PartMonitor.*;
import org.mwc.cmap.core.ui_support.PartMonitor.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.Vector;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.mwc.cmap.core.CorePlugin;
import org.junit.jupiter.api.Test;

/////////////////////////////////////////////////
// and the testing code
// ///////////////////////////////////////////////
public class PartMonitorTest {
	/**
	 * wrapper stub used to for testing of PartMonitor
	 *
	 * @author ian.mayo
	 *
	 */
	protected abstract static class TestPart implements IWorkbenchPart {
		@Override
		public void addPropertyListener(final IPropertyListener listener) {
		}

		@Override
		public void createPartControl(final Composite parent) {
		}

		@Override
		public void dispose() {
		}

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public abstract Object getAdapter(Class adapter);

		@Override
		public IWorkbenchPartSite getSite() {
			return null;
		}

		@Override
		public String getTitle() {
			return null;
		}

		@Override
		public Image getTitleImage() {
			return null;
		}

		@Override
		public String getTitleToolTip() {
			return null;
		}

		@Override
		public void removePropertyListener(final IPropertyListener listener) {
		}

		@Override
		public void setFocus() {
		}

	}

	static IPartListener _ipsRegistered = null;

	static boolean _openedCalled = false;

	static boolean _closedCalled = false;

	static Vector<String> _eventNames = new Vector<String>(0, 1);

	static Vector<Class<?>> _eventTypes = new Vector<Class<?>>(0, 1);

	public PartMonitorTest() {
	}

	@Test
	public void testPartMonitor() {
		final IPartService ips = new IPartService() {
			@Override
			public void addPartListener(final IPartListener listener) {
				_ipsRegistered = listener;
			}

			@Override
			public void addPartListener(final IPartListener2 listener) {
			}

			@Override
			public IWorkbenchPart getActivePart() {
				return null;
			}

			@Override
			public IWorkbenchPartReference getActivePartReference() {
				return null;
			}

			@Override
			public void removePartListener(final IPartListener listener) {
				_ipsRegistered = null;
			}

			@Override
			public void removePartListener(final IPartListener2 listener) {
			}
		};

		// right, check that the ips bits are ready
		assertNull(_ipsRegistered, "part monitor not registered yet");
		assertEquals(0, _eventNames.size(), "part monitor not registered yet");
		assertEquals(0, _eventTypes.size(), "part monitor not registered yet");
		assertFalse(_openedCalled, "part monitoring not ready");
		assertFalse(_closedCalled, "part monitoring not ready");

		// and on with the testing
		final PartMonitor pm = new PartMonitor(ips);

		assertEquals(pm, _ipsRegistered, "PartMonitor registered");
		assertNull(pm._myEvents, "PartMonitor empty");

		// ok, try some calls (without any listeners)
		pm.partOpened(new TestPart() {
			@Override
			@SuppressWarnings("rawtypes")
			public Object getAdapter(final Class adapter) {
				return new String("string");
			}
		});

		// right, check that the ips bits are still empty
		assertEquals(0, _eventNames.size(), "part monitor not registered yet");
		assertEquals(0, _eventTypes.size(), "part monitor not registered yet");
		assertFalse(_openedCalled, "part monitoring not ready");
		assertFalse(_closedCalled, "part monitoring not ready");

		// add listeners
		pm.addPartListener(String.class, PartMonitor.OPENED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {
				_openedCalled = true;
				_eventNames.add(type);
				_eventTypes.add(String.class);
			}
		});

		// and add another listener
		pm.addPartListener(String.class, PartMonitor.CLOSED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {
				_closedCalled = true;
				_eventNames.add(type);
				_eventTypes.add(String.class);
			}
		});

		// fire one of the correct type
		pm.partOpened(new TestPart() {
			@Override
			@SuppressWarnings("rawtypes")
			public Object getAdapter(final Class adapter) {
				return new String("string");
			}
		});

		assertEquals(1, _eventNames.size(), "part monitor not registered yet");
		assertEquals(1, _eventTypes.size(), "part monitor not registered yet");
		assertTrue(_openedCalled, "part monitoring not ready");
		assertFalse(_closedCalled, "part monitoring not ready");

		// clear out the lists
		_openedCalled = false;
		_closedCalled = false;
		_eventNames.clear();
		_eventTypes.clear();

		// add more listeners
		pm.addPartListener(Integer.class, PartMonitor.OPENED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {
				_openedCalled = true;
				_eventNames.add(type);
				_eventTypes.add(Integer.class);
			}
		});
		pm.addPartListener(Integer.class, PartMonitor.CLOSED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {
				_closedCalled = true;
				_eventNames.add(type);
				_eventTypes.add(Integer.class);
			}
		});

		// fire the event, just for strings.
		pm.partOpened(new TestPart() {
			@Override
			@SuppressWarnings("rawtypes")
			public Object getAdapter(final Class adapter) {
				Object res = null;
				if (adapter == String.class)
					res = new String("string");
				return res;
			}
		});

		// check events got created (we're still only returning a String object,
		// so the integer one shouldn't get called
		assertEquals(1, _eventNames.size(), "part monitor not registered yet");
		assertEquals(1, _eventTypes.size(), "part monitor not registered yet");
		assertTrue(_openedCalled, "part monitoring not ready");
		assertFalse(_closedCalled, "part monitoring not ready");

		_eventNames.clear();
		_eventTypes.clear();
		_openedCalled = false;
		_closedCalled = false;

		// fire one of both classes
		pm.partOpened(new TestPart() {
			@Override
			@SuppressWarnings("rawtypes")
			public Object getAdapter(final Class adapter) {
				Object res = null;
				if (adapter == String.class) {
					res = new String("stst");
				} else if (adapter == Integer.class) {
					res = new Integer(1);
				}
				return res;
			}
		});

		// check events got created (we're now returning both types - but still only
		// for open event)
		assertEquals(2, _eventNames.size(), "part monitor not registered yet");
		assertEquals(2, _eventTypes.size(), "part monitor not registered yet");
		assertTrue(_openedCalled, "part monitoring not ready");
		assertFalse(_closedCalled, "part monitoring not ready");

		// and clear.
		_eventNames.clear();
		_eventTypes.clear();
		_openedCalled = false;
		_closedCalled = false;

		// fire both events for both classes
		pm.partOpened(new TestPart() {
			@Override
			@SuppressWarnings("rawtypes")
			public Object getAdapter(final Class adapter) {
				Object res = null;
				if (adapter == String.class) {
					res = new String("stst");
				} else if (adapter == Integer.class) {
					res = new Integer(1);
				}
				return res;
			}
		});
		pm.partClosed(new TestPart() {
			@Override
			@SuppressWarnings("rawtypes")
			public Object getAdapter(final Class adapter) {
				Object res = null;
				if (adapter == String.class) {
					res = new String("stst");
				} else if (adapter == Integer.class) {
					res = new Integer(1);
				}
				return res;
			}
		});

		// check events got created (we're now returning both types for both events
		assertEquals(4, _eventNames.size(), "part monitor not registered yet");
		assertEquals(4, _eventTypes.size(), "part monitor not registered yet");
		assertTrue(_openedCalled, "part monitoring not ready");
		assertTrue(_closedCalled, "part monitoring not ready");

		// right, lastly test the ditching part
		pm.dispose(ips);
		assertNull(_ipsRegistered, "part monitor not de-registered");

	}
}
