package org.mwc.cmap.NarrativeViewer2;

public class NATNarrativeEntry {

	private String date;
	private String time;
	private String name;
	private String type;
	private String log;
	
	public NATNarrativeEntry(String date, String time, String name, String type, String log) {
		this.date = date;
		this.time = time;
		this.name = name;
		this.type = type;
		this.log = log;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}
	
	
	@Override
	public String toString() {
		return "NarrativeEntry [date=" + date + ", time=" + time + ", name=" + name + ", type=" + type + ", log=" + log
				+ "]";
	}
}
