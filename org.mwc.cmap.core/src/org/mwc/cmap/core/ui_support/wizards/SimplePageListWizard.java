/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core.ui_support.wizards;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

/** simple wizard that takes series of pages, showing them in order
 * 
 * @author ianmayo
 *
 */
public class SimplePageListWizard extends Wizard
{
	private Vector<IWizardPage> _myPages;
	
	public SimplePageListWizard()
	{
	}
	
	/** include the specified wizard page
	 * 
	 * @param page
	 */
	public void addWizard(final IWizardPage page)
	{
		if(_myPages == null)
		_myPages = new Vector<IWizardPage>();
		
		_myPages.add(page);
	}

	public void addPages()
	{
	  final Iterator<IWizardPage> iter = _myPages.iterator();
	  while (iter.hasNext())
		{
			final IWizardPage thisP = iter.next();
			addPage(thisP);
		}
	}

	public boolean performFinish()
	{
		return true;
	}


}
