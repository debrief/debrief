package com.planetmayo.debrief.satc.support;

public interface LogService
{
	void error(String message);

	void error(String message, Exception ex);

	void info(String message);

	void info(String message, Exception ex);

	void warn(String message);

	void warn(String message, Exception ex);
}
