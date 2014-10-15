/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.media.utility;

public class OSUtils {
	
	public static final boolean WIN;
	public static final boolean MAC;
	public static final boolean LINUX;
	public static final boolean IS_64BIT;
	static {
		String os = System.getProperty("os.name").toLowerCase();
		boolean win = false, mac = false, linux = false;
		if (os.indexOf("win") != -1) {
			win = true;
		}
		if (os.indexOf("mac os") != -1) {
			mac = true;
		}
		if (os.indexOf("linux") != -1) {
			linux = true;
		}		
		WIN = win;
		MAC = mac;
		LINUX = linux;
		String jvmArch = System.getProperty("os.arch");
		IS_64BIT = jvmArch != null && jvmArch.contains("64");
	}	
}
