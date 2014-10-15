/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.xyplot.views.snail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	private long _period = 0;
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
	
	public static Map<String, Integer> PERIODS_IN_MILLISEC = new HashMap<String, Integer>();

	public SnailPeriodTracker() 
	{		
		PERIODS_IN_MILLISEC.put(TIME_PERIODS.ZERO, 0);
		PERIODS_IN_MILLISEC.put(TIME_PERIODS.FIVE_MIN, 5 * 60 * 1000);
		PERIODS_IN_MILLISEC.put(TIME_PERIODS.TEN_MIN, 10 * 60 * 1000);
		PERIODS_IN_MILLISEC.put(TIME_PERIODS.THIRTY_MIN, 30 * 60 * 1000);
		PERIODS_IN_MILLISEC.put(TIME_PERIODS.HOUR, 60 * 60 * 1000);
		PERIODS_IN_MILLISEC.put(TIME_PERIODS.TWO_HOURS, 120 * 60 * 1000);		
	}
	
	
	@Override
	protected Control createControl(final Composite parent) 
	{
	    final Composite container = new Composite(parent, SWT.NONE);
	    final GridLayout glContainer = new GridLayout(2, false);
	    glContainer.marginTop = -1;
	    glContainer.marginHeight = 0;
	    glContainer.marginWidth = 0;
	    container.setLayout(glContainer);
	    
	    final Label label = new Label(container, SWT.NONE);
	    label.setText("Time period: ");
	   
	    final GridData glReader = new GridData(SWT.FILL, 
	    		SWT.FILL, false, false, 1, 1);
	    glReader.widthHint = 250;	   
	    _reader = new Combo(container, SWT.BORDER| SWT.READ_ONLY
	            | SWT.DROP_DOWN);
	    _reader.setLayoutData(glReader);
	    
	    for (String value: PERIODS_IN_MILLISEC.keySet())
	    	_reader.add(value);
	    
	    _reader.setText(getPeriodText(_period));
	    
	    _reader.addSelectionListener(new SelectionAdapter(){
	    	@Override
	    	public void widgetSelected(SelectionEvent e) 
	    	{
	    		super.widgetSelected(e);
	    		for (ISnailPeriodChangedListener listener: _listeners)
	    		{
	    			final long period = PERIODS_IN_MILLISEC.get(_reader.getText()).longValue();
	    			listener.snailPeriodChanged(period);
	    		}
	    	}
	    });
		

	    return container;
	}
	
	@Override
	protected int computeWidth(final Control control) 
	{
	    return 350;
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
	
	public void setPeriod(final long period)
	{
		_period = period;
	}
	
	String getPeriodText(final long period)
	{
		final Iterator<Entry<String, Integer>> iter = PERIODS_IN_MILLISEC.entrySet().iterator();
		while(iter.hasNext())
		{
			final Entry<String, Integer> next = iter.next();
			if (next.getValue().longValue() == period)
			{
				return next.getKey();
			}
		}
		return TIME_PERIODS.ZERO;
	}


}
