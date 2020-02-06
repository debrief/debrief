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
package org.eclipse.nebula.cwt.svg;

import org.eclipse.swt.graphics.GC;

/**
 * An SvgUse is an svg graphical element that uses another, previously defined
 * graphical element to paint to the graphics context with its own set of styles
 * and transforms.
 * <p>
 * See also: <a href=
 * "http://www.w3.org/TR/SVG/struct.html#UseElement">http://www.w3.org/TR/SVG/struct.html#UseElement</a>
 * </p>
 */
public class SvgUse extends SvgGraphic {

	String linkId;
	float x;
	float y;
	Float w;
	Float h;

	SvgUse(final SvgContainer container, final String id) {
		super(container, id);
	}

	@Override
	public void apply(final GC gc) {
		final SvgGraphic graphic = getGraphic();
		if (graphic != null) {
			// TODO: proxy container?
			final SvgContainer c = graphic.getContainer();
			graphic.setContainer(getContainer());
			graphic.apply(gc);
			graphic.setContainer(c);
		}
	}

	@Override
	SvgFill getFill() {
		final SvgGraphic graphic = getGraphic();
		if (graphic != null) {
			return graphic.getFill();
		}
		return null;
	}

	private SvgGraphic getGraphic() {
		final Object def = getFragment().getElement(linkId);
		if (def instanceof SvgGraphic) {
			return (SvgGraphic) def;
		}
		return null;
	}

	@Override
	SvgStroke getStroke() {
		final SvgGraphic graphic = getGraphic();
		if (graphic != null) {
			return graphic.getStroke();
		}
		return null;
	}

}
