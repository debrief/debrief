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

package org.mwc.debrief.pepys.model.bean;

import java.sql.Timestamp;
import java.util.Date;

import org.mwc.debrief.pepys.model.db.annotation.FieldName;
import org.mwc.debrief.pepys.model.db.annotation.Id;
import org.mwc.debrief.pepys.model.db.annotation.ManyToOne;
import org.mwc.debrief.pepys.model.db.annotation.OneToOne;
import org.mwc.debrief.pepys.model.db.annotation.TableName;
import org.mwc.debrief.pepys.model.db.annotation.Time;
import org.mwc.debrief.pepys.model.tree.TreeStructurable;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;

@TableName(name = "Comments")
public class Comment implements AbstractBean, TreeStructurable {

	@Id
	private String comment_id;

	@ManyToOne
	@FieldName(name = "platform_id")
	private Platform platform;

	@Time
	private Timestamp time;
	private String comment_type_id;
	private String content;

	@ManyToOne
	@FieldName(name = "source_id")
	private Datafile datafile;

	@OneToOne
	@FieldName(name = "privacy_id")
	private Privacy privacy;
	private Date created_date;

	public Comment() {

	}

	@Override
	public void doImport(final Layers _layers) {
		Layer dest = _layers.findLayer(NarrativeEntry.NARRATIVE_LAYER, true);
		if (dest == null) {
			dest = new NarrativeWrapper(NarrativeEntry.NARRATIVE_LAYER);

			// add it to the manager
			_layers.addThisLayer(dest);
		}

		final NarrativeEntry entry = new NarrativeEntry(getPlatform().getTrackName(), new HiResDate(getTime()),
				getContent());

		// ok, can we provide a track color for it?
		final String source = entry.getTrackName();
		final Layer host = _layers.findLayer(source, true);
		if (host instanceof TrackWrapper) {
			final TrackWrapper tw = (TrackWrapper) host;
			entry.setColor(tw.getColor());
		}

		dest.add(entry);
	}

	public String getComment_id() {
		return comment_id;
	}

	public String getComment_type_id() {
		return comment_type_id;
	}

	public String getContent() {
		return content;
	}

	public Date getCreated_date() {
		return created_date;
	}

	@Override
	public Datafile getDatafile() {
		return datafile;
	}

	@Override
	public Platform getPlatform() {
		return platform;
	}

	public Privacy getPrivacy() {
		return privacy;
	}

	@Override
	public SensorType getSensorType() {
		// A comment doesn't have sensor :(
		return null;
	}

	@Override
	public Date getTime() {
		return time;
	}

	public void setComment_id(final String comment_id) {
		this.comment_id = comment_id;
	}

	public void setComment_type_id(final String comment_type_id) {
		this.comment_type_id = comment_type_id;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public void setCreated_date(final Date created_date) {
		this.created_date = created_date;
	}

	public void setDatafile(final Datafile datafile) {
		this.datafile = datafile;
	}

	public void setPlatform(final Platform platform) {
		this.platform = platform;
	}

	public void setPrivacy(final Privacy privacy) {
		this.privacy = privacy;
	}

	public void setTime(final Timestamp time) {
		this.time = time;
	}

}
