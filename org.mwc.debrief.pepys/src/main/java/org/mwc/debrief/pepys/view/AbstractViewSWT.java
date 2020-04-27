package org.mwc.debrief.pepys.view;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.mwc.cmap.core.custom_widget.CWorldLocation;

public interface AbstractViewSWT {

	Widget getApplyButton();

	CWorldLocation getBottomRightLocation();

	Composite getDataTypesComposite();

	CDateTime getEndDate();

	CDateTime getEndTime();

	Widget getImportButton();

	Text getSearchText();

	CDateTime getStartDate();

	CDateTime getStartTime();

	Widget getTestConnectionButton();

	CWorldLocation getTopLeftLocation();

	CheckboxTreeViewer getTree();

	Widget getUseCurrentViewportButton();

}
