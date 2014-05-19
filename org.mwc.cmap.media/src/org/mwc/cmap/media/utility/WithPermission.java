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
