/****************************************************************************
 * Copyright (c) 2008 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.nebula.cwt.v.IControlPainter;
import org.eclipse.nebula.cwt.v.VButton;
import org.eclipse.nebula.cwt.v.VButtonPainter;
import org.eclipse.nebula.cwt.v.VControl;
import org.eclipse.nebula.cwt.v.VLabelPainter;
import org.eclipse.nebula.cwt.v.VPanel;
import org.eclipse.nebula.cwt.v.VPanelPainter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

public class CDateTimePainter implements IControlPainter {

	protected CDateTime cdt;

	protected IControlPainter buttonPainter;
	protected IControlPainter labelPainter;
	protected IControlPainter panelPainter;

	public CDateTimePainter() {
		buttonPainter = new VButtonPainter();
		labelPainter = new VLabelPainter();
		panelPainter = new VPanelPainter();
	}

	private void defaultPaintBackground(final VControl control, final Event e) {
		switch (control.getType()) {
		case Button:
			buttonPainter.paintBackground(control, e);
			break;
		case Label:
			labelPainter.paintBackground(control, e);
			break;
		case Panel:
			panelPainter.paintBackground(control, e);
			break;
		}
	}

	private void defaultPaintBorders(final VControl control, final Event e) {
		switch (control.getType()) {
		case Button:
			buttonPainter.paintBorders(control, e);
			break;
		case Label:
			labelPainter.paintBorders(control, e);
			break;
		case Panel:
			panelPainter.paintBorders(control, e);
			break;
		}
	}

	private void defaultPaintContent(final VControl control, final Event e) {
		switch (control.getType()) {
		case Button:
			buttonPainter.paintContent(control, e);
			break;
		case Label:
			labelPainter.paintContent(control, e);
			break;
		case Panel:
			panelPainter.paintContent(control, e);
			break;
		}
	}

	@Override
	public void dispose() {
		buttonPainter.dispose();
		labelPainter.dispose();
		panelPainter.dispose();
	}

	protected VPanel getPicker() {
		return cdt.picker;
	}

	protected final int indexOf(final VControl control) {
		final Object obj = control.getData(CDT.Key.Index);
		if (obj instanceof Integer) {
			return (Integer) obj;
		}
		return -1;
	}

	protected final boolean isActive(final VControl control) {
		final Object obj = control.getData(CDT.Key.Active);
		if (obj instanceof Boolean) {
			return (Boolean) obj;
		}
		return false;
	}

	protected final boolean isToday(final VControl control) {
		final Object obj = control.getData(CDT.Key.Today);
		if (obj instanceof Boolean) {
			return (Boolean) obj;
		}
		return false;
	}

	@Override
	public final void paintBackground(final VControl control, final Event e) {
		switch ((CDT.PickerPart) control.getData(CDT.PickerPart)) {
		case ClearButton:
			paintClearButtonBackground(control, e);
			break;
		case DateNow:
			paintDateNowBackground(control, e);
			break;
		case DayButton:
			paintDayButtonBackground(control, e);
			break;
		case DayOfWeekLabel:
			paintDayOfWeekLabelBackground(control, e);
			break;
		case DayOfWeekPanel:
			paintDayOfWeekPanelBackground(control, e);
			break;
		case DayPanel:
			paintDayPanelBackground(control, e);
			break;
		case TodayButton:
			paintFooterButtonBackground(control, e);
			break;
		case FooterPanel:
			paintFooterPanelBackground(control, e);
			break;
		case HeaderPanel:
			paintHeaderPanelBackground(control, e);
			break;
		case MonthLabel:
			paintMonthLabelBackground(control, e);
			break;
		case MonthNext:
			paintMonthNextBackground(control, e);
			break;
		case MonthPrev:
			paintMonthPrevBackground(control, e);
			break;
		case YearLabel:
			paintYearLabelBackground(control, e);
			break;
		case YearNext:
			paintYearNextBackground(control, e);
			break;
		case YearPrev:
			paintYearPrevBackground(control, e);
			break;
		default:
			defaultPaintBackground(control, e);
			break;
		}
	}

	@Override
	public final void paintBorders(final VControl control, final Event e) {
		switch ((CDT.PickerPart) control.getData(CDT.PickerPart)) {
		case ClearButton:
			paintClearButtonBorders(control, e);
			break;
		case DateNow:
			paintDateNowBorders(control, e);
			break;
		case DayButton:
			paintDayButtonBorders(control, e);
			break;
		case DayOfWeekLabel:
			paintDayOfWeekLabelBorders(control, e);
			break;
		case DayOfWeekPanel:
			paintDayOfWeekPanelBorders(control, e);
			break;
		case DayPanel:
			paintDayPanelBorders(control, e);
			break;
		case TodayButton:
			paintFooterButtonBorders(control, e);
			break;
		case FooterPanel:
			paintFooterPanelBorders(control, e);
			break;
		case HeaderPanel:
			paintHeaderPanelBorders(control, e);
			break;
		case MonthLabel:
			paintMonthLabelBorders(control, e);
			break;
		case MonthNext:
			paintMonthNextBorders(control, e);
			break;
		case MonthPrev:
			paintMonthPrevBorders(control, e);
			break;
		case YearLabel:
			paintYearLabelBorders(control, e);
			break;
		case YearNext:
			paintYearNextBorders(control, e);
			break;
		case YearPrev:
			paintYearPrevBorders(control, e);
			break;
		default:
			defaultPaintBorders(control, e);
			break;
		}
	}

	protected void paintClearButtonBackground(final VControl control, final Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintClearButtonBorders(final VControl control, final Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintClearButtonContent(final VControl control, final Event e) {
		defaultPaintContent(control, e);
	}

	@Override
	public final void paintContent(final VControl control, final Event e) {
		switch ((CDT.PickerPart) control.getData(CDT.PickerPart)) {
		case ClearButton:
			paintClearButtonContent(control, e);
			break;
		case DateNow:
			paintDateNowContent(control, e);
			break;
		case DayButton:
			paintDayButtonContent(control, e);
			break;
		case DayOfWeekLabel:
			paintDayOfWeekLabelContent(control, e);
			break;
		case DayOfWeekPanel:
			paintDayOfWeekPanelContent(control, e);
			break;
		case DayPanel:
			paintDayPanelContent(control, e);
			break;
		case TodayButton:
			paintFooterButtonContent(control, e);
			break;
		case FooterPanel:
			paintFooterPanelContent(control, e);
			break;
		case HeaderPanel:
			paintHeaderPanelContent(control, e);
			break;
		case MonthLabel:
			paintMonthLabelContent(control, e);
			break;
		case MonthNext:
			paintMonthNextContent(control, e);
			break;
		case MonthPrev:
			paintMonthPrevContent(control, e);
			break;
		case YearLabel:
			paintYearLabelContent(control, e);
			break;
		case YearNext:
			paintYearNextContent(control, e);
			break;
		case YearPrev:
			paintYearPrevContent(control, e);
			break;
		default:
			defaultPaintContent(control, e);
			break;
		}
	}

	protected void paintDateNowBackground(final VControl control, final Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintDateNowBorders(final VControl control, final Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintDateNowContent(final VControl control, final Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintDayButtonBackground(final VControl control, final Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintDayButtonBorders(final VControl control, final Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintDayButtonContent(final VControl control, final Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintDayOfWeekLabelBackground(final VControl control, final Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintDayOfWeekLabelBorders(final VControl control, final Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintDayOfWeekLabelContent(final VControl control, final Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintDayOfWeekPanelBackground(final VControl control, final Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintDayOfWeekPanelBorders(final VControl control, final Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintDayOfWeekPanelContent(final VControl control, final Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintDayPanelBackground(final VControl control, final Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintDayPanelBorders(final VControl control, final Event e) {
		defaultPaintBorders(control, e);
		final Calendar cal = cdt.getCalendarInstance();
		final VPanel picker = getPicker();
		if (picker instanceof DatePicker) {
			final VButton[] days = ((DatePicker) picker).dayButtons;
			for (int i = 1; i < days.length; i++) {
				final VButton day = days[i];
				cal.setTime(day.getData(CDT.Key.Date, Date.class));
				if (cal.get(Calendar.DAY_OF_MONTH) == 1 && !isActive(day) && !isActive(days[i - 1])) {
					final Rectangle bounds = day.getBounds();
					final Rectangle pbounds = control.getBounds();
					if (indexOf(day) % 7 != 0) {
						e.gc.drawLine(bounds.x, bounds.y, bounds.x, bounds.y + bounds.height);
					}
					if (indexOf(day) > 7) {
						e.gc.drawLine(bounds.x, bounds.y, pbounds.x + pbounds.width, bounds.y);
					}
					e.gc.drawLine(pbounds.x, bounds.y + bounds.height, bounds.x, bounds.y + bounds.height);

					i += 28;
				}
			}
		}
	}

	protected void paintDayPanelContent(final VControl control, final Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintFooterButtonBackground(final VControl control, final Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintFooterButtonBorders(final VControl control, final Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintFooterButtonContent(final VControl control, final Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintFooterPanelBackground(final VControl control, final Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintFooterPanelBorders(final VControl control, final Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintFooterPanelContent(final VControl control, final Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintHeaderPanelBackground(final VControl control, final Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintHeaderPanelBorders(final VControl control, final Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintHeaderPanelContent(final VControl control, final Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintMonthLabelBackground(final VControl control, final Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintMonthLabelBorders(final VControl control, final Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintMonthLabelContent(final VControl control, final Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintMonthNextBackground(final VControl control, final Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintMonthNextBorders(final VControl control, final Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintMonthNextContent(final VControl control, final Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintMonthPrevBackground(final VControl control, final Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintMonthPrevBorders(final VControl control, final Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintMonthPrevContent(final VControl control, final Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintYearLabelBackground(final VControl control, final Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintYearLabelBorders(final VControl control, final Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintYearLabelContent(final VControl control, final Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintYearNextBackground(final VControl control, final Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintYearNextBorders(final VControl control, final Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintYearNextContent(final VControl control, final Event e) {
		defaultPaintContent(control, e);
	}

	protected void paintYearPrevBackground(final VControl control, final Event e) {
		defaultPaintBackground(control, e);
	}

	protected void paintYearPrevBorders(final VControl control, final Event e) {
		defaultPaintBorders(control, e);
	}

	protected void paintYearPrevContent(final VControl control, final Event e) {
		defaultPaintContent(control, e);
	}

	public final void setButtonPainter(final IControlPainter painter) {
		this.buttonPainter = painter;
	}

	void setCDateTime(final CDateTime cdt) {
		this.cdt = cdt;
	}

	public final void setLabelPainter(final IControlPainter painter) {
		this.labelPainter = painter;
	}

	public final void update(final VControl control) {
		switch ((CDT.PickerPart) control.getData(CDT.PickerPart)) {
		case ClearButton:
			updateClearButton(control);
			break;
		case DateNow:
			updateDateNow(control);
			break;
		case DayButton:
			updateDayButton(control);
			break;
		case DayOfWeekLabel:
			updateDayOfWeekLabel(control);
			break;
		case DayOfWeekPanel:
			updateDayOfWeekPanel(control);
			break;
		case DayPanel:
			updateDayPanel(control);
			break;
		case TodayButton:
			updateFooterButton(control);
			break;
		case FooterPanel:
			updateFooterPanel(control);
			break;
		case HeaderPanel:
			updateHeaderPanel(control);
			break;
		case MonthLabel:
			updateMonthLabel(control);
			break;
		case MonthNext:
			updateMonthNext(control);
			break;
		case MonthPrev:
			updateMonthPrev(control);
			break;
		case YearLabel:
			updateYearLabel(control);
			break;
		case YearNext:
			updateYearNext(control);
			break;
		case YearPrev:
			updateYearPrev(control);
			break;
		}
	}

	protected void updateClearButton(final VControl control) {
	}

	protected void updateDateNow(final VControl control) {
		control.setFill(control.getDisplay().getSystemColor(SWT.COLOR_GRAY));
	}

	protected void updateDayButton(final VControl control) {
		if (isToday(control)) {
			control.setForeground(control.getDisplay().getSystemColor(SWT.COLOR_RED));
		} else if (isActive(control)) {
			control.setForeground(control.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		} else {
			control.setForeground(control.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		}
	}

	protected void updateDayOfWeekLabel(final VControl control) {
	}

	protected void updateDayOfWeekPanel(final VControl control) {
		control.setBackground(control.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	protected void updateDayPanel(final VControl control) {
		control.setBackground(control.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	protected void updateFooterButton(final VControl control) {
	}

	protected void updateFooterPanel(final VControl control) {
	}

	protected void updateHeaderPanel(final VControl control) {
	}

	protected void updateMonthLabel(final VControl control) {
	}

	protected void updateMonthNext(final VControl control) {
		control.setFill(control.getDisplay().getSystemColor(SWT.COLOR_GRAY));
	}

	protected void updateMonthPrev(final VControl control) {
		control.setFill(control.getDisplay().getSystemColor(SWT.COLOR_GRAY));
	}

	protected void updateYearLabel(final VControl control) {
	}

	protected void updateYearNext(final VControl control) {
		control.setFill(control.getDisplay().getSystemColor(SWT.COLOR_GRAY));
	}

	protected void updateYearPrev(final VControl control) {
		control.setFill(control.getDisplay().getSystemColor(SWT.COLOR_GRAY));
	}

}
