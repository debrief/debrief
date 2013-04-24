package org.mwc.debrief.gndmanager.Tracks;

import org.bitbucket.es4gwt.shared.elastic.ElasticFacet;

public enum Facet implements ElasticFacet {

	NAME,
	PLATFORM,
	PLATFORM_TYPE,
	SENSOR,
	SENSOR_TYPE,
	TRIAL,
	DATA_TYPE,
	TYPE,
	START,
	END;

	public static final Facet[] SEARCH_PARAMS = { PLATFORM, PLATFORM_TYPE, SENSOR, SENSOR_TYPE, TRIAL, DATA_TYPE };

	public static final Facet[] RESULTS_FIELDS = {	NAME,
													PLATFORM,
													PLATFORM_TYPE,
													SENSOR,
													SENSOR_TYPE,
													TRIAL,
													TYPE,
													START,
													END };

	@Override
	public String toRequestString() {
		switch (this) {
		case NAME:
			return "name";
		case PLATFORM:
			return "platform";
		case PLATFORM_TYPE:
			return "platform_type";
		case SENSOR:
			return "sensor";
		case SENSOR_TYPE:
			return "sensor_type";
		case TRIAL:
			return "trial";
		case DATA_TYPE:
			return "data_type";
		case TYPE:
			return "type";
		case START:
			return "start";
		case END:
			return "end";
		default:
			throw new UnsupportedOperationException("toRequestString() method is not supported for Facet." + name());
		}
	}

	@Override
	public String toString() {
		return toRequestString();
	}

	public String toDisplayString() {
		switch (this) {
		case NAME:
			return "Name";
		case PLATFORM:
			return "Platform";
		case PLATFORM_TYPE:
			return "Platform Type";
		case SENSOR:
			return "Sensor";
		case SENSOR_TYPE:
			return "Sensor Type";
		case TRIAL:
			return "Trial";
		case DATA_TYPE:
			return "Data Type";
		default:
			throw new UnsupportedOperationException("toDisplayString() method is not supported for Facet." + name());
		}
	}

}
