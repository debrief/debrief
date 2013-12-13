package org.mwc.cmap.media;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.mwc.cmap.media.utility.DateUtils;

public class PlanetmayoFormats {
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String TIME_FORMAT = "HH:mm:ss";
	private static final String FILE_NAME_FORMAT = "yyyy_MM_dd_HH_mm_ss";
	
	private static final String[] supportedImageFormats = {"png", "jpg", "jpeg", "tif", "tiff"}; 
	
	private static PlanetmayoFormats instance;
	
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
	
	public String getDateFormatPattern() {
		return DATE_FORMAT;
	}
	
	public String getFilenameFormatPattern() {
		return FILE_NAME_FORMAT;
	}
	
	public String getTimeFormatPattern() {
		return TIME_FORMAT;
	}
	
	public SimpleDateFormat getDateFormat() {
		return new SimpleDateFormat(DATE_FORMAT);
	}
	
	private SimpleDateFormat getFilenameFormat() {
		return new SimpleDateFormat(FILE_NAME_FORMAT);
	}
	
	public SimpleDateFormat getTimeFormat() {
		return new SimpleDateFormat(TIME_FORMAT);
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
