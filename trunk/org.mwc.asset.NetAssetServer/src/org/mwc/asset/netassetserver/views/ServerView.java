package org.mwc.asset.netassetserver.views;

import java.util.Date;
import java.util.Vector;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.part.ViewPart;

import ASSET.NetworkScenario;
import ASSET.Scenario.MultiScenarioLister;

public class ServerView extends ViewPart
{

	/**
	 * The ID of the view as specified by the extension.
	 */
	private ListWrap _myList;
	@SuppressWarnings("unused")
	private SPresenter _myPres;

	/**
	 * The constructor.
	 */
	public ServerView()
	{
	}

	public static class ListWrap implements SView
	{

		private List _list;

		public ListWrap(List list)
		{
			_list = list;
		}

		@Override
		public void showMessage(Date date, String msg)
		{
			_list.add(msg);
		}

	}

	public void createPartControl(Composite parent)
	{
		List list = new List(parent, SWT.NONE);
		_myList = new ListWrap(list);
		_myPres = new SPresenter(_myList)
		{

			@Override
			public Vector<NetworkScenario> getScenarios()
			{
				Vector<NetworkScenario> res = null;
				MultiScenarioLister lister = findScenarioProvider();
				if (lister != null)
					res = lister.getScenarios();
				return res;
			}
		};

	}

	private MultiScenarioLister findScenarioProvider()
	{
		IViewReference[] views = getViewSite().getPage().getViewReferences();
		MultiScenarioLister res = null;
		for (int i = 0; i < views.length; i++)
		{
			IViewReference thisV = views[i];
			IViewPart view = thisV.getView(true);
			if (view instanceof IAdaptable)
			{
				IAdaptable ia = view;
				Object lister = ia.getAdapter(MultiScenarioLister.class);
				if (lister != null)
					res = (MultiScenarioLister) lister;
			}
		}
		return res;
	}

	@Override
	public void setFocus()
	{
	}

}