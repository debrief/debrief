/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.core.property_support.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.mwc.cmap.core.property_support.IDebriefProperty;

final public class ValueWithUnitsControl extends Composite implements
		ModifyListener, FocusListener, SelectionListener
{

	/**
	 * hmm, the text bit.
	 * 
	 */
	private final Text _myText;

	/**
	 * and the drop-down units bit
	 * 
	 */
	private final Combo _myCombo;

	/**
	 * the helper class that actually handles the data
	 * 
	 */
	private ValueWithUnitsDataModel _myModel;

	/**
	 * the (optional) object we're editing
	 * 
	 */
	private IDebriefProperty _property;

	/**
	 * default constructor. it doesn't have all data, but we're not in control of
	 * it's signature
	 * 
	 * @param parent
	 */
	public ValueWithUnitsControl(final Composite parent)
	{
		super(parent, SWT.NONE);

		// sort ourselves out
		final RowLayout rows = new RowLayout();
		rows.marginLeft = rows.marginRight = 0;
		rows.marginTop = rows.marginBottom = 0;
		rows.fill = false;
		rows.spacing = 0;
		rows.pack = false;
		setLayout(rows);

		// and put in the controls
		_myText = new Text(this, SWT.BORDER);
		_myText.setTextLimit(7);
		_myText.addModifyListener(this);
		_myText.addFocusListener(this);
		_myText.addSelectionListener(this);
		_myCombo = new Combo(this, SWT.DROP_DOWN);
		_myCombo.addModifyListener(this);
	}

	/**
	 * convenience constructor - for when we're building ourselves
	 * 
	 * @param parent
	 *          where to stick ourselves
	 * @param textTip
	 *          the tooltip on the text field
	 * @param comboText
	 *          the tooltip on the combo box
	 * @param dataModel
	 *          the data model we're manipulating
	 * @param property
	 *          who we tell if we've changed
	 */
	public ValueWithUnitsControl(final Composite parent, final String textTip,
			final String comboText, final ValueWithUnitsDataModel dataModel,
			final IDebriefProperty property)
	{
		this(parent);
		init(textTip, comboText, dataModel);
		_property = property;

		if (_property != null)
			_myModel.storeMe(property.getValue());
	}

	@Override
	public void dispose()
	{
		_myText.removeFocusListener(this);
		_myText.removeSelectionListener(this);

		// and drop everything else
		super.dispose();
	}

	/**
	 * update the values displayed
	 * 
	 */
	final private void doUpdate()
	{
		// get the best units
		final int units = _myModel.getUnitsValue();
		final String txt = "" + _myModel.getDoubleValue();
		_myText.setText(txt);
		_myCombo.select(units);
	}

	@Override
	public void focusGained(final FocusEvent e)
	{
		selectAll();
	}

	@Override
	public void focusLost(final FocusEvent e)
	{
	}

	/**
	 * encode ourselves into an object
	 * 
	 * @return
	 */
	@Override
	public Object getData()
	{
		Object res = null;
		final String distTxt = _myText.getText();
		if (distTxt.length() > 0)
		{
			final double dist = new Double(distTxt).doubleValue();
			final int units = _myCombo.getSelectionIndex();
			if (units != -1)
				res = _myModel.createResultsObject(dist, units);
		}
		return res;
	}

	/**
	 * initialise ourselves, post-constructor. We have to do this, because we
	 * don't have control over the constructor - sometimes it gets called by the
	 * cell editor constructor.
	 * 
	 * @param textTip
	 * @param comboTip
	 * @param model
	 */
	public void init(final String textTip, final String comboTip,
			final ValueWithUnitsDataModel model)
	{
		_myModel = model;
		_myText.setToolTipText(textTip);
		_myCombo.setToolTipText(comboTip);
		_myCombo.setItems(_myModel.getTagsList());
		_myCombo.select(0);
	}

	@Override
	public void modifyText(final ModifyEvent e)
	{
		// store the value in the property, if we have one?
		if (_property != null)
			_property.setValue(getData());

		// also tell any listeners
		final Listener[] listeners = this.getListeners(SWT.Selection);
		for (int i = 0; i < listeners.length; i++)
		{
			final Listener listener = listeners[i];
			listener.handleEvent(new Event());
		}

	}

	private void selectAll()
	{
		if (_myText != null)
			if (!_myText.isDisposed())
				_myText.selectAll();
	}

	/**
	 * set ourselves to this value
	 * 
	 * @param value
	 */
	@Override
	public void setData(final Object value)
	{
		// let the daddy do his bit
		super.setData(value);

		// now store the data itself
		_myModel.storeMe(value);
		doUpdate();
	}

	@Override
	public void setEnabled(final boolean enabled)
	{
		super.setEnabled(enabled);
		_myCombo.setEnabled(enabled);
		_myText.setEnabled(enabled);
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e)
	{
	}

	@Override
	public void widgetSelected(final SelectionEvent e)
	{
		selectAll();
	}

}