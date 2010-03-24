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
	@SuppressWarnings("deprecation")
	public void createInitialLayout(IPageLayout layout)
	{
		// Get the editor area.
		String editorArea = layout.getEditorArea();

		// Top left: Resource Navigator view and Bookmarks view placeholder
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f,
			editorArea);
		topLeft.addView(IPageLayout.ID_RES_NAV);
		topLeft.addView(CorePlugin.TIME_CONTROLLER);
		
		// split the time one - so we can insert the track tote
		// Top left: Resource Navigator view and Bookmarks view placeholder
		IFolderLayout midLeft = layout.createFolder("midLeft", IPageLayout.BOTTOM, 0.3f,
				"topLeft");
		midLeft.addView(CorePlugin.TOTE);
		midLeft.addPlaceholder(CorePlugin.STACKED_DOTS);
		midLeft.addPlaceholder(CorePlugin.FREQ_RESIDUALS);
		midLeft.addView(CorePlugin.OVERVIEW_PLOT);
		midLeft.addPlaceholder(CorePlugin.POLYGON_EDITOR);		
		
		// Bottom left: Outline view and Property Sheet view
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.40f,
			"midLeft");
		bottomLeft.addView(CorePlugin.LAYER_MANAGER);
		bottomLeft.addView(IPageLayout.ID_PROP_SHEET);
			
		
		// bottom: placeholder for the xyplot
		IPlaceholderFolderLayout bottomPanel = layout.createPlaceholderFolder("bottom", IPageLayout.BOTTOM, 0.7f, editorArea);
		bottomPanel.addPlaceholder(CorePlugin.XY_PLOT + ":*");
		bottomPanel.addPlaceholder(CorePlugin.PLOT_3d + ":*");
		bottomPanel.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		bottomPanel.addPlaceholder(IPageLayout.ID_TASK_LIST);
    bottomPanel.addPlaceholder(CorePlugin.NARRATIVES2);
    bottomPanel.addPlaceholder(CorePlugin.GRID_EDITOR);
//		bottomPanel.addPlaceholder(CorePlugin.NARRATIVES);
		
		
		// and our view shortcuts
		layout.addShowViewShortcut(CorePlugin.LAYER_MANAGER);
    layout.addShowViewShortcut(CorePlugin.NARRATIVES2);
		layout.addShowViewShortcut(CorePlugin.TIME_CONTROLLER);
		layout.addShowViewShortcut(CorePlugin.TOTE);
		layout.addShowViewShortcut(CorePlugin.STACKED_DOTS);
		layout.addShowViewShortcut(CorePlugin.FREQ_RESIDUALS);
		layout.addShowViewShortcut(CorePlugin.GRID_EDITOR);
		layout.addShowViewShortcut(CorePlugin.OVERVIEW_PLOT);
		// layout.addShowViewShortcut(CorePlugin.PLOT_3d); -- don't show shortcut for 3d, we only open
		// it via action (so we can populate it)  

		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
		layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);

		// and the error log
		layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");
		
		// hey - try to add the 'new plot' to the New menu
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		layout.addNewWizardShortcut("org.mwc.debrief.core.wizards.NewPlotWizard");
		
		// ok - make sure the debrief action sets are visible
		layout.addActionSet("org.mwc.debrief.core");
		layout.addActionSet("org.mwc.cmap.plot3d");
		layout.addActionSet("org.mwc.debrief.track_shift");
	}

	
		
	
}
