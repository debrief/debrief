package org.mwc.debrief.pepys.model.bean;

import org.mwc.debrief.pepys.model.db.annotation.Id;
import org.mwc.debrief.pepys.model.db.annotation.TableName;

@TableName(name = "Platforms")
public class Platform implements AbstractBean {

	@Id
	private int platform_id;
	private String name;
	
	public Platform() {
		
	}

	public int getPlatform_id() {
		return platform_id;
	}

	public void setPlatform_id(int platform_id) {
		this.platform_id = platform_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
