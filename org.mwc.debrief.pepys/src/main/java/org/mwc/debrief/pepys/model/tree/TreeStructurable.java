package org.mwc.debrief.pepys.model.tree;

import java.util.Date;

import org.mwc.debrief.pepys.model.bean.Datafile;
import org.mwc.debrief.pepys.model.bean.Platform;
import org.mwc.debrief.pepys.model.bean.SensorType;

import MWC.GUI.Layers;

public interface TreeStructurable {
	public void doImport(final Layers _layers);

	public Datafile getDatafile();

	public Platform getPlatform();

	public SensorType getSensorType();

	public Date getTime();
}
