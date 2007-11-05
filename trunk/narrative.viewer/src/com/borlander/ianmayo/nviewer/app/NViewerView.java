package com.borlander.ianmayo.nviewer.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.borlander.ianmayo.nviewer.Column;
import com.borlander.ianmayo.nviewer.ColumnFilter;
import com.borlander.ianmayo.nviewer.NarrativeViewer;
import com.borlander.ianmayo.nviewer.model.IEntryWrapper;
import com.borlander.ianmayo.nviewer.model.TimeFormatter;
import com.borlander.ianmayo.nviewer.model.mock.MockEntryWrapper;

public class NViewerView extends ViewPart {
	public static final String VIEW_ID = "com.borlander.ianmayo.nviewer.app.view";

	private NarrativeViewer myViewer;

	private Composite myButtonPanel;

	private IEntryWrapper myCurrentInput = null;

	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		Composite rootPanel = new Composite(parent, SWT.BORDER);
		rootPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		StackLayout rootPanelLayout = new StackLayout();
		rootPanel.setLayout(rootPanelLayout);

		myViewer = new NarrativeViewer(rootPanel, Activator.getInstance().getPreferenceStore());
		rootPanelLayout.topControl = myViewer;
		
		//the line below contributes the predefined viewer actions onto the view action bar
		myViewer.getViewerActions().fillActionBars(getViewSite().getActionBars());
		
		//all below this line is mockup code
		myButtonPanel = new Composite(parent, SWT.NONE);
		myButtonPanel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
		myButtonPanel.setLayout(new GridLayout(3, true));

		new TestButton("Set input") {
			@Override
			protected void onClick() {
				MockEntryWrapper entryWrapper = new MockEntryWrapper();
				for (int i = 0; i < 30; i++) {
					String entry = "";
					for (int j = 0; j <= i; j++) {
						entry += "entry#" + (j + 1);
						if (j < i) {
							entry += " ";
						}
					}
					entryWrapper.addEntry("source #" + (i % 3 + 1), "type #" + (i % 4 + 1), entry);
				}
				myCurrentInput = entryWrapper;
				myViewer.setInput(myCurrentInput);
			}
		};
		new TestButton("Format time as HH:mm") {
			@Override
			protected void onClick() {
				myViewer.setTimeFormatter(new TimeFormatter() {
					public String format(long time) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(time);
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
						return simpleDateFormat.format(calendar.getTime());
					}
				});
			}
		};
		new TestButton("Format time as HH:mm:ss") {
			@Override
			protected void onClick() {
				myViewer.setTimeFormatter(new TimeFormatter() {
					public String format(long time) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(time);
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
						return simpleDateFormat.format(calendar.getTime());
					}
				});
			}
		};
		new TestButton("Filter source to 'source #2'") {
			@Override
			protected void onClick() {
				Column sourceColumn = myViewer.getModel().getColumnSource();
				ColumnFilter filter = sourceColumn.getFilter();
				filter.setAllowedValues(Collections.singleton("source #2"));
				myViewer.refresh();
			}
		};
		new TestButton("Remove source filter") {
			@Override
			protected void onClick() {
				Column sourceColumn = myViewer.getModel().getColumnSource();
				ColumnFilter filter = sourceColumn.getFilter();
				filter.clear();
				myViewer.refresh();
			}
		};
		new TestButton("Filter type to 'type #3'") {
			@Override
			protected void onClick() {
				Column typeColumn = myViewer.getModel().getColumnType();
				ColumnFilter filter = typeColumn.getFilter();
				filter.setAllowedValues(Collections.singleton("type #3"));
				myViewer.refresh();
			}
		};
		new TestButton("Remove type filter") {
			@Override
			protected void onClick() {
				Column typeColumn = myViewer.getModel().getColumnType();
				ColumnFilter filter = typeColumn.getFilter();
				filter.clear();
				myViewer.refresh();
			}
		};

		new TestButton("Switch Entry wrap mode") {
			private boolean myIsWrapping = myViewer.isWrappingEntries();

			@Override
			protected void onClick() {
				myIsWrapping = !myIsWrapping;
				myViewer.setWrappingEntries(myIsWrapping);
			}
		};

		new TestButton("Open filter dialog for source") {
			@Override
			protected void onClick() {
				Column sourceColumn = myViewer.getModel().getColumnSource();
				myViewer.showFilterDialog(sourceColumn);
			}
		};

		new TestButton("Open filter dialog for type") {
			@Override
			protected void onClick() {
				Column typeColumn = myViewer.getModel().getColumnType();
				myViewer.showFilterDialog(typeColumn);
			}
		};
	}

	private abstract class TestButton {
		private final Button myButton;

		public TestButton(String caption) {
			myButton = new Button(myButtonPanel, SWT.NONE);
			myButton.setText(caption);
			myButton.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
			myButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					onClick();
				}
			});
		}

		abstract protected void onClick();
	}

	public void setFocus() {
		myViewer.setFocus();
	}

}
