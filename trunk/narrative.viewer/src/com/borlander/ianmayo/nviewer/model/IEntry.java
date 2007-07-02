package com.borlander.ianmayo.nviewer.model;

public interface IEntry {
	public boolean getVisible();

	public void setVisible(boolean visible);

	public long getTime();

	public String getSource();

	public String getType();

	public String getEntry();
}
