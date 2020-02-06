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

import java.util.Date;
import java.util.Objects;

public class ExportNarrativeEntry {
	private final String text;
	private final String dateString;
	private final Date date;
	private final String elapsed;

	public ExportNarrativeEntry(final String text, final String dateString, final String elapsed, final Date date) {
		this.text = text;
		this.dateString = dateString;
		this.elapsed = elapsed;
		this.date = date;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ExportNarrativeEntry other = (ExportNarrativeEntry) obj;
		if (!Objects.equals(dateString, other.dateString)) {
			return false;
		}
		if (!Objects.equals(elapsed, other.elapsed)) {
			return false;
		}
		return (Objects.equals(text, other.text));
	}

	public Date getDate() {
		return date;
	}

	public String getDateString() {
		return dateString;
	}

	public String getElapsed() {
		return elapsed;
	}

	public String getText() {
		return text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateString == null) ? 0 : dateString.hashCode());
		result = prime * result + ((elapsed == null) ? 0 : elapsed.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

}
