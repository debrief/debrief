package org.mwc.cmap.media.views;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.nebula.widgets.formattedtext.DateTimeFormatter;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.media.Activator;
import org.mwc.cmap.media.PlanetmayoFormats;
import org.mwc.cmap.media.time.ITimeListener;
import org.mwc.cmap.media.utility.DateUtils;

public class TestHarnessView extends ViewPart {
	private static final String STATE_FIRED_TIME = "firedTime";
	private static final String STATE_TIME_TO_FIRE = "timeToFire";
	
	public static final String ID = "org.mwc.cmap.media.views.TestHarnessView";
	
	private IMemento memento;
	
	private ITimeListener timeListener;
	
	private Label firedTime;
	private FormattedText timeToFire;
	private Button button;

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.memento = memento;
		timeListener = new ITimeListener() {
			
			SimpleDateFormat dateFormat = PlanetmayoFormats.getInstance().getDateFormat();
			
			@Override
			public void newTime(Object src, long millis) {
				firedTime.setText(dateFormat.format(new Date(millis)));				
			}
		};
		Activator.getDefault().getTimeProvider().addListener(timeListener);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		Activator.getDefault().getTimeProvider().removeListener(timeListener);
	}
	
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		Date date = (Date) timeToFire.getValue();
		memento.putString(STATE_FIRED_TIME, firedTime.getText());
		memento.putString(STATE_TIME_TO_FIRE, Long.toString(date.getTime()));
	}
	
	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		Label label = new Label(main, SWT.LEFT | SWT.TOP);
		label.setText("Fired Time: ");
		GridData data = new GridData();
		data.heightHint = 50;
		label.setLayoutData(data);
		
		firedTime = new Label(main, SWT.LEFT);
		firedTime.setText("           -");
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		data.heightHint = 50;
		firedTime.setLayoutData(data);
		
		label = new Label(main, SWT.LEFT);
		label.setText("Time to fire: ");	
		
		Composite fireComposite = new Composite(main, SWT.NONE);
		fireComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		timeToFire = new FormattedText(fireComposite);
		timeToFire.setFormatter(new DateTimeFormatter(PlanetmayoFormats.getInstance().getDateFormatPattern()));
		timeToFire.setValue(new Date());
		
		button = new Button(fireComposite, SWT.PUSH);
		button.setText("Fire");
		button.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				Date fireTime = (Date) timeToFire.getValue();
				DateUtils.removeMilliSeconds(fireTime);
				Activator.getDefault().getTimeProvider().fireNewTime(TestHarnessView.this, fireTime.getTime());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		restoreSavedState();
	}	

	@Override
	public void setFocus() {
		firedTime.setFocus();
	}	
	
	private void restoreSavedState() {
		if (memento == null) {
			return;
		}
		if (memento.getString(STATE_FIRED_TIME) != null) {
			firedTime.setText(memento.getString(STATE_FIRED_TIME));
		}
		try {
			if (memento.getString(STATE_TIME_TO_FIRE) != null) {
				timeToFire.setValue(new Date(Long.parseLong(memento.getString(STATE_TIME_TO_FIRE))));
			}
		} catch (NumberFormatException ex) {
			
		}
	}
}
