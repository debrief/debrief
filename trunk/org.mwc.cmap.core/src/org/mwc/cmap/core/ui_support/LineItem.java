package org.mwc.cmap.core.ui_support;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.PreferencesUtil;

import MWC.Utilities.TextFormatting.BriefFormatLocation;

/**
 * class representing line of text which may be plased on the status bar
 * 
 * @author ian.mayo
 * 
 */
public class LineItem extends ControlContribution {
	Label label;

	String _lastText = " 00" + BriefFormatLocation.DEGREE_SYMBOL
			+ "00\'00.00\"N 000" + BriefFormatLocation.DEGREE_SYMBOL
			+ "00\'00.00\"W ";

	/**
	 * tooltip to show when hovering over panel
	 * 
	 */
	private final String _tooltip;

	/**
	 * preferences dialog id to open when user double-clicks
	 * 
	 */
	private final String _prefId;

	/**
	 * constructor - get going
	 * 
	 * @param id
	 */
	public LineItem(String id, String template, String tooltip, String prefId) {
		super(id);
		_prefId = prefId;
		_tooltip = tooltip;
		_lastText = template;
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#isDynamic()
	 */
	public boolean isDynamic() {
		return true;
	}

<<<<<<< .mine
	public void setText(String val) {
		if (label == null) {
		} else if (label.isDisposed()) {
		} else {
			label.setText(val);
		}
=======
	public void setText(String val)
	{
		// handle strange instance where we don't yet have a label.
		if(label != null)
			label.setText(val);
>>>>>>> .r1843

		_lastText = val;
	}

	public boolean isDisposed() {
		boolean res = true;
		if (label != null)
			res = label.isDisposed();

		return res;
	}

	/**
	 * @see org.eclipse.jface.action.ControlContribution#createControl(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createControl(final Composite parent) {
		if (label != null) {
			label.dispose();
			label = null;
		}

		label = new Label(parent, SWT.RIGHT | SWT.BORDER);
		label.setText(_lastText);
		label.setToolTipText(_tooltip);
		label.setSize(550, 20);
		if (_prefId != null) {
			label.addMouseListener(new MouseAdapter() {
				public void mouseDoubleClick(MouseEvent e) {
					// do the parent bits
					super.mouseDoubleClick(e);

					// do our bits
					Display dis = Display.getCurrent();
					PreferenceDialog dial = PreferencesUtil
							.createPreferenceDialogOn(dis.getActiveShell(),
									_prefId, null, null);
					dial.open();
				}
			});
		}

		return label;
	}

}
