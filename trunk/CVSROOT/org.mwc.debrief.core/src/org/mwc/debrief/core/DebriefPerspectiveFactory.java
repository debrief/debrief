/**
 * 
 */
package org.mwc.debrief.core;

import org.eclipse.ui.*;
import org.mwc.cmap.core.CorePlugin;

/**
 * @author ian.mayo
 *
 */
public class DebriefPerspectiveFactory implements IPerspectiveFactory
{

	/**
	 * @param layout
	 */
	public void createInitialLayout(IPageLayout layout)
	{
		// Get the editor area.
		String editorArea = layout.getEditorArea();

		// Top left: Resource Navigator view and Bookmarks view placeholder
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.35f,
			editorArea);
		topLeft.addView(IPageLayout.ID_RES_NAV);
		topLeft.addView(CorePlugin.TIME_CONTROLLER);
		topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		
		// split the time one - so we can insert the track tote
		// Top left: Resource Navigator view and Bookmarks view placeholder
		IFolderLayout midLeft = layout.createFolder("midLeft", IPageLayout.BOTTOM, 0.3f,
				"topLeft");
		midLeft.addView(CorePlugin.TOTE);
		midLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);

		// Bottom left: Outline view and Property Sheet view
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.40f,
			"midLeft");
		bottomLeft.addView(IPageLayout.ID_PROP_SHEET);
		bottomLeft.addView(CorePlugin.TOTE);
		bottomLeft.addView(CorePlugin.LAYER_MANAGER);
		
		// bottom: placeholder for the xyplot
		layout.addPlaceholder(CorePlugin.XY_PLOT + ":*", IPageLayout.BOTTOM, 0.7f, editorArea);
		
		
		// and our view shortcuts
		layout.addShowViewShortcut(CorePlugin.LAYER_MANAGER);
		layout.addShowViewShortcut(CorePlugin.NARRATIVES);
		layout.addShowViewShortcut(CorePlugin.TIME_CONTROLLER);
		layout.addShowViewShortcut(CorePlugin.TOTE);
		
		layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
		layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
		
	}

	
		
	
}
