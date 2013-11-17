package org.mwc.cmap.xyplot.views.snail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

public class SnailPeriodTracker extends WorkbenchWindowControlContribution 
{
	private Combo _reader;
	private List<ISnailPeriodChangedListener> _listeners = new ArrayList<ISnailPeriodChangedListener>();
	
	public static interface TIME_PERIODS
	{
		final String ZERO = "0 (the current/nearest position)";
		final String FIVE_MIN = "5 minutes";
		final String TEN_MIN = "10 minutes";
		final String THIRTY_MIN = "30 minutes";
		final String HOUR = "1 hour";
		final String TWO_HOURS = "2 hours";
	}
	
	public static Map<String, Integer> PERIODS_IN_SEC = new HashMap<String, Integer>();

	public SnailPeriodTracker() 
	{		
		PERIODS_IN_SEC.put(TIME_PERIODS.ZERO, 0);
		PERIODS_IN_SEC.put(TIME_PERIODS.FIVE_MIN, 5 * 60);
		PERIODS_IN_SEC.put(TIME_PERIODS.TEN_MIN, 10 * 60);
		PERIODS_IN_SEC.put(TIME_PERIODS.THIRTY_MIN, 30 * 60);
		PERIODS_IN_SEC.put(TIME_PERIODS.HOUR, 60 * 60);
		PERIODS_IN_SEC.put(TIME_PERIODS.TWO_HOURS, 120 * 60);
	}
	
	
	@Override
	protected Control createControl(Composite parent) 
	{
	    Composite container = new Composite(parent, SWT.NONE);
	    GridLayout glContainer = new GridLayout(1, false);
	    glContainer.marginTop = -1;
	    glContainer.marginHeight = 0;
	    glContainer.marginWidth = 0;
	    container.setLayout(glContainer);
	    GridData glReader = new GridData(SWT.FILL, 
	    		SWT.FILL, false, false, 1, 1);
	    glReader.widthHint = 280;
	    final Label label = new Label(parent, SWT.NONE);
	    label.setText("Time period: ");
	    //TODO: layout the label
	    
	    _reader = new Combo(container, SWT.BORDER| SWT.READ_ONLY
	            | SWT.DROP_DOWN);
	    _reader.setLayoutData(glReader);
	    
	    for (String value: PERIODS_IN_SEC.keySet())
	    	_reader.add(value);
	    _reader.setText(TIME_PERIODS.ZERO);
	    
	    _reader.addSelectionListener(new SelectionAdapter(){
	    	@Override
	    	public void widgetSelected(SelectionEvent e) 
	    	{
	    		super.widgetSelected(e);
	    		for (ISnailPeriodChangedListener listener: _listeners)
	    		{
	    			final long period = PERIODS_IN_SEC.get(_reader.getText()).longValue();
	    			listener.snailPeriodChanged(period);
	    		}
	    	}
	    });
		

	    return container;
	}
	
	@Override
	protected int computeWidth(Control control) 
	{
	    return 300;
	}
	
	public void addSnailPeriodChangedListener(final ISnailPeriodChangedListener listener) 
	{
		if (! _listeners.contains(listener))
			_listeners.add(listener);			
	}

	
	public void removeSnailPeriodChangedListener(
			final ISnailPeriodChangedListener listener) 
	{
		_listeners.remove(listener);		
	}


}
