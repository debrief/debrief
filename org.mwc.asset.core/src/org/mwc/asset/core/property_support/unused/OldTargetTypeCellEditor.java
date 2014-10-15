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
package org.mwc.asset.core.property_support.unused;

import java.util.*;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.List;

import ASSET.Models.Decision.TargetType;
import ASSET.Participants.Category;

public class OldTargetTypeCellEditor extends CellEditor
{

	/**
	 * and the drop-down units bit
	 */
	Combo _myForce;

	/**
	 * and the drop-down units bit
	 */
	List _myEnvironment;

	/**
	 * and the drop-down units bit
	 */
	Combo _myType;

	/**
	 * our tooltip
	 */
	final private String _forceTip = "the force of the subject participant(s)";

	final private String _environmentTip = "the environment of the subject participant(s)";

	final private String _typeTip = "the type of the subject participant(s)";

	public OldTargetTypeCellEditor(final Composite parent)
	{
		super(parent);
	}

	protected Control createControl(final Composite parent)
	{
		final Composite holder = new Composite(parent, SWT.NONE);
		final RowLayout rows = new RowLayout();
		rows.marginLeft = rows.marginRight = 0;
		rows.marginTop = rows.marginBottom = 0;
		rows.fill = false;
		rows.spacing = 0;
		rows.pack = false;
		holder.setLayout(rows);

		_myForce = new Combo(holder, SWT.DROP_DOWN);
		_myForce.setToolTipText(_forceTip);
		_myForce.setItems(getForces());

		_myEnvironment = new List(holder, SWT.DROP_DOWN);
		_myEnvironment.setToolTipText(_environmentTip);
		_myEnvironment.setItems(getEnvironments());

		_myType = new Combo(holder, SWT.DROP_DOWN);
		_myType.setToolTipText(_typeTip);
		_myType.setItems(getTypes());

		return holder;
	}

	/**
	 * @return our list of values
	 */
	protected String[] getForces()
	{
		String[] res = new String[] { null };
		res = (String[]) Category.getForces().toArray(res);
		return res;
	}

	/**
	 * @return our list of values
	 */
	protected String[] getEnvironments()
	{
		String[] res = new String[] { null };
		res = (String[]) Category.getEnvironments().toArray(res);
		return res;
	}

	/**
	 * @return our list of values
	 */
	protected String[] getTypes()
	{
		String[] res = new String[] { null };
		res = (String[]) Category.getTypes().toArray(res);
		return res;
	}

	protected Object doGetValue()
	{
		final TargetType tt = new TargetType();

		if (_myForce.getSelectionIndex() != -1)
			tt.addTargetType(_myForce.getItem(_myForce.getSelectionIndex()));
		if (_myEnvironment.getSelectionIndex() != -1)
			tt.addTargetType(_myEnvironment.getItem(_myEnvironment.getSelectionIndex()));
		if (_myType.getSelectionIndex() != -1)
			tt.addTargetType(_myType.getItem(_myType.getSelectionIndex()));
		return tt;
	}

	protected void doSetFocus()
	{
	}

	protected void doSetValue(final Object value)
	{
		final TargetType _myCat = (TargetType) value;

		// ok, sort out the forces
		final Collection<String> types = _myCat.getTargets();
		for (final Iterator<String> iter = types.iterator(); iter.hasNext();)
		{
			final String type = (String) iter.next();

			// is this a force?
			if (Category.getForces().contains(type))
			{
				_myForce.select(_myForce.indexOf(type));
			}
			else if (Category.getTypes().contains(type))
			{
				_myType.select(_myType.indexOf(type));
			}
			else if (Category.getEnvironments().contains(type))
			{
				_myEnvironment.select(_myEnvironment.indexOf(type));
			}
		}
		// ok, set the values
		// _myForce.select(1);
		// _myEnvironment.select(2);
		// _myType.select(3);
	}

}