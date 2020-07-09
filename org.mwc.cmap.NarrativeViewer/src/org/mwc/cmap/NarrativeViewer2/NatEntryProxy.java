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
package org.mwc.cmap.NarrativeViewer2;

import org.eclipse.swt.graphics.Color;
import org.mwc.cmap.core.property_support.ColorHelper;

import MWC.TacticalData.NarrativeEntry;

public class NatEntryProxy implements INatEntry {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	final private DateFormatter dateFormatter;
	final private NarrativeEntry entry;

	public NatEntryProxy(final DateFormatter dateFormatter, final NarrativeEntry entry) {
		this.dateFormatter = dateFormatter;
		this.entry = entry;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final NatEntryProxy other = (NatEntryProxy) obj;
		if (entry == null) {
			if (other.entry != null) {
				return false;
			}
		} else if (!entry.equals(other.entry)) {
			return false;
		}
		return true;
	}

	@Override
	public Color getColor() {
		// convert to SWT color
		return ColorHelper.getColor(entry.getColor());
	}

	public NarrativeEntry getEntry() {
		return entry;
	}

	@Override
	public String getLog() {
		return entry.getEntry();
	}

	@Override
	public String getName() {
		return entry.getTrackName();
	}

	@Override
	public String getTime() {
		return dateFormatter.get(entry.getDTG());
	}

	@Override
	public String getType() {
		return entry.getType();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entry == null) ? 0 : entry.hashCode());
		return result;
	}

}
