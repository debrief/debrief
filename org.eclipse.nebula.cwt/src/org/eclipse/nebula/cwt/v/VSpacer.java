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

import org.eclipse.swt.graphics.Point;

public class VSpacer extends VControl {

	public VSpacer(final VPanel panel, final int style) {
		super(panel, style);
	}

	@Override
	public Point computeSize(final int hint, final int hint2, final boolean changed) {
		return new Point(1, 1);
	}

	@Override
	public Type getType() {
		return VControl.Type.Spacer;
	}

	@Override
	public void setBounds(final int x, final int y, final int width, final int height) {
	}

}
