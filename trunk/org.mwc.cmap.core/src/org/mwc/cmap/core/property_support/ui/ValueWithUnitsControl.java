/**
 * 
 */
package org.mwc.cmap.core.property_support.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.mwc.cmap.core.property_support.DebriefProperty;

final public class ValueWithUnitsControl extends Composite implements
		ModifyListener
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
	private DebriefProperty _property;

	/**
	 * default constructor. it doesn't have all data, but we're not in control of
	 * it's signature
	 * 
	 * @param parent
	 */
	public ValueWithUnitsControl(Composite parent)
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
	public ValueWithUnitsControl(Composite parent, String textTip,
			String comboText, ValueWithUnitsDataModel dataModel,
			DebriefProperty property)
	{
		this(parent);
		init(textTip, comboText, dataModel);
		_property = property;

		if (_property != null)
			_myModel.storeMe(property.getValue());
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
		_myCombo.select(units);
		_myText.setText(txt);
	}

	/**
	 * encode ourselves into an object
	 * 
	 * @return
	 */
	public Object getValue()
	{
		Object res = null;
		final String distTxt = _myText.getText();
		if (distTxt.length() > 0)
		{
			double dist = new Double(distTxt).doubleValue();
			final int units = _myCombo.getSelectionIndex();
			if(units != -1)
				res = _myModel.createResultsObject(dist, units);
		}
		else
		{
			System.err.println("DUFF DOUBLE VALUE");
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
	public void init(String textTip, String comboTip,
			ValueWithUnitsDataModel model)
	{
		_myModel = model;
		_myText.setToolTipText(textTip);
		_myCombo.setToolTipText(comboTip);
		_myCombo.setItems(_myModel.getTagsList());
		_myCombo.select(0);
	}

	/**
	 * set ourselves to this value
	 * 
	 * @param value
	 */
	protected void setValue(Object value)
	{
		_myModel.storeMe(value);
		doUpdate();
	}

	@Override
	public void modifyText(ModifyEvent e)
	{
		// store the value in the property, if we have one?
		if (_property != null)
			_property.setValue(getValue());
		
		// also tell any listeners
		Listener[] listeners = this.getListeners(SWT.Selection);
		for (int i = 0; i < listeners.length; i++)
		{
			Listener listener = listeners[i];
			listener.handleEvent(new Event());
		}
		
	}

}