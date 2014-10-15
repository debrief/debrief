/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.media;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.nebula.widgets.formattedtext.DateTimeFormatter;
import org.mwc.cmap.media.utility.DateUtils;

public class PlanetmayoFormats {
	private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final String TIME_FORMAT_PATTERN = "HH:mm:ss";
	private static final String FILE_NAME_FORMAT_PATTERN = "yyyy_MM_dd_HH_mm_ss";
	
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);
	private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(TIME_FORMAT_PATTERN);
	private static SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat(FILE_NAME_FORMAT_PATTERN);
	private static DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatter(DATE_FORMAT_PATTERN);
	
	private static final String[] supportedImageFormats = {"png", "jpg", "jpeg", "tif", "tiff"}; 
	
	private static PlanetmayoFormats instance;
	
	static {
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
		TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
		FILE_NAME_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
		DATE_TIME_FORMATTER.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	public static PlanetmayoFormats getInstance() {		
		if (instance == null) {
			synchronized (PlanetmayoFormats.class) {
				if (instance == null) {
					instance = new PlanetmayoFormats();
				}
			}
		}
		return instance;
	}
	
	public DateTimeFormatter getDateTimeFormatter() {
		return DATE_TIME_FORMATTER;
	}
	
	public SimpleDateFormat getDateFormat() {
		return DATE_FORMAT;
	}
	
	private SimpleDateFormat getFilenameFormat() {
		return FILE_NAME_FORMAT;
	}
	
	public SimpleDateFormat getTimeFormat() {
		return TIME_FORMAT;
	}
	
	public String[] getSupportedImageFormats() {
		return supportedImageFormats.clone(); 
	}
	
	public boolean isSupportedImage(String imageName) {
		if (imageName == null) {
			return false;
		}
		imageName = imageName.toLowerCase();
		for (String supportedFormat : supportedImageFormats) {
			if (imageName.endsWith(supportedFormat)) {
				return true;
			}
		}
		return false;
	}
	
	public Date parseDateFromFileName(String fileName) {
		int extensionIndex = fileName.lastIndexOf('.');
		if (extensionIndex != -1) {
			fileName = fileName.substring(0, extensionIndex);
    	}
		try {
			Date result = getFilenameFormat().parse(fileName);
			DateUtils.removeMilliSeconds(result);
			return result;
		} catch (ParseException ex) {
			return null;
		}
	}
	
	public String encodeDateInFileName(Date date, String extension) {
		if (date == null) {
			throw new IllegalArgumentException("date can't be null");
		}
		return getFilenameFormat().format(date) + "." + extension;
	}
}
