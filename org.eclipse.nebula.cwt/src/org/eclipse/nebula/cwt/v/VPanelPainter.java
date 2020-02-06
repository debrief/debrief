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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

public class VPanelPainter implements IControlPainter {

	@Override
	public void dispose() {
		// nothing to do
	}

	@Override
	public void paintBackground(final VControl control, final Event e) {
		if (control.background != null && !control.background.isDisposed()) {
			e.gc.setBackground(control.background);
			e.gc.fillRectangle(control.bounds);
		}
	}

	@Override
	public void paintBorders(final VControl control, final Event e) {
		if (control.hasStyle(SWT.BORDER)) {
			if (control.foreground != null) {
				e.gc.setForeground(control.foreground);
			} else {
				e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_WIDGET_BORDER));
			}
			e.gc.drawRectangle(control.bounds.x, control.bounds.y, control.bounds.width - 1, control.bounds.height - 1);
		}
	}

	@Override
	public void paintContent(final VControl control, final Event e) {
		for (final VControl child : ((VPanel) control).getChildren()) {
			child.paintControl(e);
		}
	}

}
