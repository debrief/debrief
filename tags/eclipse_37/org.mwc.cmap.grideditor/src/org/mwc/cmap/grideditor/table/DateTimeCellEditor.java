package org.mwc.cmap.grideditor.table;

import java.util.Date;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.mwc.cmap.gridharness.data.FormatDateTime;
import org.mwc.cmap.gridharness.views.MultiControlFocusHandler;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;

public class DateTimeCellEditor extends CellEditor {

	private static final GregorianCalendar CALENDAR_TEMPLATE;

	private static final TimeZone DEFAULT_TIME_ZONE;

	private DateTime myDateUI;

	private DateTime myTimeUI;

	private Composite myPanel;

	private Date myDeferredValue;

	public DateTimeCellEditor(Composite parent) {
		super(parent);
	}

	@Override
	protected Control createControl(Composite parent) {
		myPanel = new Composite(parent, SWT.NONE);
		RowLayout rows = new RowLayout();
		rows.marginLeft = rows.marginRight = 0;
		rows.marginTop = rows.marginBottom = 0;
		rows.fill = false;
		rows.spacing = 0;
		rows.pack = true;
		myPanel.setLayout(rows);

		myDateUI = new DateTime(myPanel, SWT.DATE | SWT.MEDIUM);
		myTimeUI = new DateTime(myPanel, SWT.TIME | SWT.MEDIUM);

		TraverseListener onEscapeCloser = new TraverseListener() {

			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
					DateTimeCellEditor.this.focusLost();
				}
			}
		};

		myDateUI.addTraverseListener(onEscapeCloser);
		myTimeUI.addTraverseListener(onEscapeCloser);
		myPanel.addTraverseListener(onEscapeCloser);

		new MultiControlFocusHandler(myDateUI, myTimeUI) {

			@Override
			protected void focusReallyLost(FocusEvent e) {
				if (myDateUI.isDisposed() || myTimeUI.isDisposed()) {
					return;
				}
				DateTimeCellEditor.this.focusLost();
			}
		};

		if (myDeferredValue != null) {
			pushValueToUI(myDeferredValue);
			myDeferredValue = null;
		}
		return myPanel;
	}

	@Override
	protected Object doGetValue() {
		if (myPanel == null) {
			return null;
		}
		GregorianCalendar calendar = (GregorianCalendar) CALENDAR_TEMPLATE.clone();
		calendar.setTimeZone(DEFAULT_TIME_ZONE);
		calendar.set(myDateUI.getYear(), myDateUI.getMonth(), myDateUI.getDay(), myTimeUI.getHours(), myTimeUI.getMinutes(), myTimeUI.getSeconds());
		calendar.set(Calendar.MILLISECOND, 0);
		Date result = calendar.getTime();
		return result;
	}

	@Override
	protected void doSetFocus() {
		if (myDateUI != null) {
			myDateUI.setFocus();
		}
	}

	@Override
	protected void doSetValue(Object value) {
		if (false == value instanceof Date) {
			value = new Date();
		}
		Date date = (Date) value;
		if (myDateUI == null) {
			myDeferredValue = date;
		} else {
			pushValueToUI(date);
		}
	}

	DateTime getTimeUI() {
		return myTimeUI;
	}

	private void pushValueToUI(Date value) {
		GregorianCalendar calendar = (GregorianCalendar) CALENDAR_TEMPLATE.clone();
		calendar.setTime(value);
		calendar.setTimeZone(DEFAULT_TIME_ZONE);
		myDateUI.setYear(calendar.get(Calendar.YEAR));
		myDateUI.setMonth(calendar.get(Calendar.MONTH));
		myDateUI.setDay(calendar.get(Calendar.DATE));
		myTimeUI.setHours(calendar.get(Calendar.HOUR_OF_DAY));
		myTimeUI.setMinutes(calendar.get(Calendar.MINUTE));
		myTimeUI.setSeconds(calendar.get(Calendar.SECOND));
	}

	static {
		CALENDAR_TEMPLATE = new GregorianCalendar();
		DEFAULT_TIME_ZONE = TimeZone.getTimeZone(FormatDateTime.DEFAULT_TIME_ZONE_ID);
		CALENDAR_TEMPLATE.setTimeZone(DEFAULT_TIME_ZONE);
	}

}
