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
package org.mwc.cmap.media.utility;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.ui.activities.IWorkbenchActivitySupport;
import org.mwc.cmap.media.Activator;

public abstract class WithPermission implements Runnable {
	
	public static final String PERMISSION_ACTIVITY_ID = "org.mwc.cmap.media.forbiddenViewActivityId";
	
	@SuppressWarnings("unchecked")
	public WithPermission() {
		IWorkbenchActivitySupport support = Activator.getDefault().getWorkbench().getActivitySupport();
		Set<String> ids = support.getActivityManager().getEnabledActivityIds();
		boolean revoke = false;
		if (! ids.contains(PERMISSION_ACTIVITY_ID)) {
			ids = new HashSet<String>(ids); 
			ids.add(PERMISSION_ACTIVITY_ID);
			support.setEnabledActivityIds(ids);
			revoke = true;
		}
		try {
			run();
		} finally {
			if (revoke) {
				ids.remove(PERMISSION_ACTIVITY_ID);
				support.setEnabledActivityIds(ids);
			}
		}
	}
}
