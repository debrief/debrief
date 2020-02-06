/****************************************************************************
* Copyright (c) 2008, 2009 Jeremy Dowdall
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
*****************************************************************************/

package org.eclipse.nebula.cwt.v;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;

public class VPanel extends VControl {

	Composite widget;
	List<VControl> children = new ArrayList<VControl>();
	private VLayout layout = null;
	private boolean isTopLevel = false;
	private Listener topLevelListener;

	public VPanel(final Composite parent, final int style) {
		this((VPanel) null, style & ~SWT.BORDER);

		isTopLevel = true;

		composite = parent;

		topLevelListener = new Listener() {
			@Override
			public void handleEvent(final Event event) {
				switch (event.type) {
				case SWT.Dispose:
					dispose(false);
					break;
//				case SWT.FocusIn:
//					setFocus();
//					break;
				case SWT.Paint:
					paintControl(event);
					break;
				}
			}
		};

		composite.addListener(SWT.Dispose, topLevelListener);
		composite.addListener(SWT.FocusIn, topLevelListener);
		composite.addListener(SWT.Paint, topLevelListener);

		composite.setLayout(new Layout() {
			@Override
			protected Point computeSize(final Composite composite, final int wHint, final int hHint,
					final boolean flushCache) {
				return VPanel.this.computeSize(wHint, hHint, flushCache);
			}

			@Override
			protected void layout(final Composite composite, final boolean flushCache) {
				VPanel.this.setBounds(composite.getClientArea());
				VPanel.this.layout(flushCache);
			}
		});

		VTracker.addTopLevelPanel(this);
		composite.setData("cwt_vcontrol", this);
	}

	public VPanel(final VPanel panel, final int style) {
		super(panel, style);
		marginTop = marginBottom = marginLeft = marginRight = 0;
		setLayout(new VGridLayout());
		setPainter(new VPanelPainter());
	}

	void addChild(final VControl child) {
		if (!children.contains(child)) {
			children.add(child);
		}
	}

	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		return layout.computeSize(this, wHint, hHint, changed);
	}

	@Override
	public void dispose() {
		dispose(true);
	}

	/**
	 * If the dispose request comes from the Composite via the topLevelListener,
	 * then do not dispose the Composite again - controls recieving the Composite's
	 * dispose event after this VPanel will be in danger of dealing with a disposed
	 * control before they are ready.
	 * 
	 * @param disposeComposite
	 */
	private void dispose(final boolean disposeComposite) {
		if (isTopLevel) {
			if (composite != null && !composite.isDisposed()) {
				composite.removeListener(SWT.Dispose, topLevelListener);
				composite.removeListener(SWT.FocusIn, topLevelListener);
				composite.removeListener(SWT.Paint, topLevelListener);
			}
		}
		for (final VControl child : children.toArray(new VControl[children.size()])) {
			child.dispose();
		}
		super.dispose();
		if (isTopLevel && disposeComposite) {
			if (composite != null && !composite.isDisposed()) {
				composite.dispose();
			}
		}
	}

	public int getBorderWidth() {
		if (isTopLevel) {
			return composite.getBorderWidth();
		} else {
			return 1;
		}
	}

	public VControl[] getChildren() {
		return children.toArray(new VControl[children.size()]);
	}

	public VControl getControl(final int x, final int y) {
		return getControl(x, y, false);
	}

	public VControl getControl(final int x, final int y, final boolean includePanels) {
		if (bounds.contains(x, y)) {
			for (final ListIterator<VControl> iter = children.listIterator(children.size()); iter.hasPrevious();) {
				final VControl child = iter.previous();
				if (child.getVisible() && child.getBounds().contains(x, y)) {
					if (includePanels && child instanceof VPanel) {
						return ((VPanel) child).getControl(x, y, true);
					} else {
						return child;
					}
				}
			}
			return this;
		}
		return null;
	}

	public VLayout getLayout() {
		return layout;
	}

	@SuppressWarnings("unchecked")
	public <T extends VLayout> T getLayout(final Class<T> clazz) {
		return (T) layout;
	}

	@Override
	public Type getType() {
		return VControl.Type.Panel;
	}

	@Override
	public Composite getWidget() {
		if (widget != null) {
			return widget;
		}
		if (parent != null) {
			return parent.getWidget();
		}
		return composite;
	}

	public void layout() {
		layout(true);
	}

	public void layout(final boolean changed) {
		layout.layout(this, changed);
		redraw();
	}

	void move(final VControl above, final VControl below) {
		if (above == null) {
			children.remove(below);
			children.add(below);
		} else if (below == null) {
			children.remove(above);
			children.add(0, above);
		} else {
			final int ix = children.indexOf(below);
			children.remove(above);
			children.add(ix, above);
		}
	}

	@Override
	protected boolean redrawOnActivate() {
		return false;
	}

	@Override
	protected boolean redrawOnDeactivate() {
		return false;
	}

	void removeChild(final VControl child) {
		children.remove(child);
	}

	void removeVChild(final VControl vchild) {
		children.remove(vchild);
	}

	@Override
	public void setActivatable(final boolean activatable) {
		super.setActivatable(activatable);
		for (final VControl child : children) {
			child.setActivatable(activatable);
		}
	}

	@Override
	public void setBounds(final int x, final int y, final int width, final int height) {
		super.setBounds(x, y, width, height);
		layout();
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		for (final VControl child : children) {
			child.setEnabled(enabled);
		}
	}

	@Override
	protected boolean setFocus(final boolean focus) {
		if (focus) {
			for (final VControl child : children) {
				if (VTracker.instance().setFocusControl(child)) {
					return true;
				}
			}
			return false;
		} else {
			for (final VControl child : children) {
				child.setFocus(false);
			}
			return true;
		}
	}

	public void setLayout(final VLayout layout) {
		this.layout = layout;
	}

	@Override
	public void setLayoutData(final GridData data) {
		if (isTopLevel) {
			composite.setLayoutData(data);
		} else {
			super.setLayoutData(data);
		}
	}

	@Override
	public void setSize(final Point size) {
		super.setSize(size);
		layout();
	}

	@Override
	public void setVisibility(final int visibility) {
		for (final VControl child : children) {
			child.setVisibility(visibility);
		}
		super.setVisibility(visibility);
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		for (final VControl child : children) {
			if (child instanceof VNative) {
				child.setVisible(visible);
			}
		}
	}

	public void setWidget(final Composite widget) {
		this.widget = widget;
		this.widget.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(final Event event) {
			}
		});
//		this.widget.addListener(SWT.FocusIn, new Listener() {
//			public void handleEvent(Event event) {
//				VPanel.this.getWidget().setFocus();
//			}
//		});
	}

	public void sort(final Comparator<VControl> comparator) {
		Collections.sort(children, comparator);
	}

}
