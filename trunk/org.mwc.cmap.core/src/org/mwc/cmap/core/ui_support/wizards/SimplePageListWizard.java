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
	public void addWizard(IWizardPage page)
	{
		if(_myPages == null)
		_myPages = new Vector<IWizardPage>();
		
		_myPages.add(page);
	}

	public void addPages()
	{
	  Iterator<IWizardPage> iter = _myPages.iterator();
	  while (iter.hasNext())
		{
			IWizardPage thisP = iter.next();
			addPage(thisP);
		}
	}

	public boolean performFinish()
	{
		return true;
	}


}
