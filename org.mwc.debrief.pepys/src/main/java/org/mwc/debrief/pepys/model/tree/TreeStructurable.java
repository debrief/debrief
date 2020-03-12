package org.mwc.debrief.pepys.model.tree;

import org.mwc.debrief.pepys.model.bean.Datafile;
import org.mwc.debrief.pepys.model.bean.Platform;
import org.mwc.debrief.pepys.model.bean.SensorType;

public interface TreeStructurable {
	public Platform getPlatform();
	public SensorType getSensorType();
	public Datafile getDatafile();
}
