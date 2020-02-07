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
package Debrief.ReaderWriter.powerPoint.model;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Track data to be added to the pptx file.
 */
public class TrackData {
	private int height;
	private int width;
	private int intervals;
	private String name;
	private int scaleWidth = -1;
	private String scaleUnit = "";
	private long scaleAmount = -1;
	private final ArrayList<ExportNarrativeEntry> narrativeEntries = new ArrayList<>();
	private final ArrayList<Track> tracks = new ArrayList<>();

	private boolean basicFieldComparison(final TrackData other) {
		return !(height != other.height || intervals != other.intervals || width != other.width
				|| (name == null && other.name != null) || !name.equals(other.name));
	}

	private boolean classComparison(final Object obj) {
		return (obj != null && getClass() == obj.getClass());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		final TrackData other = (TrackData) obj;
		return (classComparison(obj) && basicFieldComparison(other) && listFieldComparison(other));
	}

	public int getHeight() {
		return height;
	}

	public int getIntervals() {
		return intervals;
	}

	public String getName() {
		return name;
	}

	public ArrayList<ExportNarrativeEntry> getNarrativeEntries() {
		return narrativeEntries;
	}

	public long getScaleAmount() {
		return scaleAmount;
	}

	public String getScaleUnit() {
		return scaleUnit;
	}

	public int getScaleWidth() {
		return scaleWidth;
	}

	public ArrayList<Track> getTracks() {
		return tracks;
	}

	public int getWidth() {
		return width;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + intervals;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((narrativeEntries == null) ? 0 : narrativeEntries.hashCode());
		result = prime * result + ((tracks == null) ? 0 : tracks.hashCode());
		result = prime * result + width;
		return result;
	}

	private boolean listFieldComparison(final TrackData other) {
		if ((narrativeEntries == null && other.narrativeEntries != null)
				|| !narrativeEntries.equals(other.narrativeEntries)) {
			return false;
		}
		return Objects.equals(tracks, other.tracks);
	}

	public void setHeight(final int height) {
		this.height = height;
	}

	public void setIntervals(final int intervals) {
		this.intervals = intervals;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setScaleAmount(final long scaleAmount) {
		this.scaleAmount = scaleAmount;
	}

	public void setScaleUnit(final String scaleUnit) {
		this.scaleUnit = scaleUnit;
	}

	public void setScaleWidth(final int scaleWidth) {
		this.scaleWidth = scaleWidth;
	}

	public void setWidth(final int width) {
		this.width = width;
	}
}
