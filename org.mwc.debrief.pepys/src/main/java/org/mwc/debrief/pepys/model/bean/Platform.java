package org.mwc.debrief.pepys.model.bean;

import org.mwc.debrief.pepys.model.db.annotation.Filterable;
import org.mwc.debrief.pepys.model.db.annotation.Id;
import org.mwc.debrief.pepys.model.db.annotation.TableName;

@TableName(name = "Platforms")
public class Platform implements AbstractBean {

	@Id
	private String platform_id;
	@Filterable
	private String name;
	@Filterable
	private String identifier;

	public Platform() {

	}

	public String getIdentifier() {
		return identifier;
	}

	public String getName() {
		return name;
	}

	public String getPlatform_id() {
		return platform_id;
	}

	public void setIdentifier(final String identifier) {
		this.identifier = identifier;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPlatform_id(final String platform_id) {
		this.platform_id = platform_id;
	}
	
	public String getTrackName() {
		if (getIdentifier() == null) {
			return name;
		}
		return name + " " + getIdentifier();
	}

}
