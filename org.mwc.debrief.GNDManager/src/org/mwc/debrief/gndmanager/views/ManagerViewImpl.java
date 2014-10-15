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
package org.mwc.debrief.gndmanager.views;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.wb.swt.SWTResourceManager;
import org.mwc.debrief.gndmanager.views.io.SearchModel.Match;
import org.mwc.debrief.gndmanager.views.io.SearchModel.MatchList;

import swing2swt.layout.BorderLayout;

public class ManagerViewImpl extends Composite implements ManagerView
{
	private final Table table;
	private Listener _myListener;
	private final FacetList _platforms;
	private final FacetList _platformTypes;
	private final FacetList _trials;
	private final CheckboxTableViewer checkboxTableViewer;
	private final Composite filterControls;
	private final Composite searchControls;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ManagerViewImpl(final Composite parent, final int style)
	{
		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		final Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(BorderLayout.NORTH);
		composite.setLayout(new RowLayout(SWT.VERTICAL));

		final Composite composite_2 = new Composite(composite, SWT.NONE);

		final Button connectBtn = new Button(composite_2, SWT.NONE);
		connectBtn.setSize(82, 28);
		connectBtn.setText("Connect");
		connectBtn.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				if (_myListener != null)
					_myListener.doConnect();
			}
		});

		filterControls = new Composite(composite, SWT.NONE);
		filterControls.setEnabled(false);
		filterControls.setLayout(new GridLayout(3, false));

		final Label lblNewLabel = new Label(filterControls, SWT.NONE);
		lblNewLabel.setBounds(0, 0, 50, 14);
		lblNewLabel.setText("Platform");

		final Label lblPlatformType = new Label(filterControls, SWT.NONE);
		lblPlatformType.setText("Platform Type");

		final Label lblNewLabel2 = new Label(filterControls, SWT.NONE);
		lblNewLabel2.setBounds(0, 0, 59, 14);
		lblNewLabel2.setText("Trial");

		
		
		final List platforms = new List(filterControls, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		final GridData gd_platforms = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_platforms.minimumWidth = 80;
		platforms.setLayoutData(gd_platforms);
		platforms.setBounds(0, 0, 155, 47);
		_platforms = new EasyBox(platforms);

		final List platformTypes = new List(filterControls, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		final GridData gd_platformTypes = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_platformTypes.minimumWidth = 80;
		platformTypes.setLayoutData(gd_platformTypes);
		platformTypes.setBounds(0, 0, 155, 47);
		_platformTypes = new EasyBox(platformTypes);

		final List trials = new List(filterControls, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		final GridData gd_trials = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_trials.minimumWidth = 80;
		trials.setLayoutData(gd_trials);
		trials.setBounds(0, 0, 155, 66);
		_trials = new EasyBox(trials);

		searchControls = new Composite(composite, SWT.NONE);
		searchControls.setLayout(new GridLayout(3, false));

		final Button btnReset = new Button(searchControls, SWT.NONE);
		btnReset.setText("Reset");

		final Label label = new Label(searchControls, SWT.NONE);
		label.setText("  ");

		final Button searchBtn = new Button(searchControls, SWT.NONE);
		searchBtn.setText("Search");
		searchBtn.addSelectionListener(new SelectionListener()
		{

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				if (_myListener != null)
					_myListener.doSearch();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{

			}
		});

		checkboxTableViewer = CheckboxTableViewer.newCheckList(this, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		table = checkboxTableViewer.getTable();
		table.setBounds(0, 0, 300, 100);
		checkboxTableViewer.getTable().setLayoutData(
				new GridData(GridData.FILL_BOTH));
		final MatchContentProvider provider = new MatchContentProvider();
		checkboxTableViewer.setContentProvider(provider);
		checkboxTableViewer.setLabelProvider(new MatchLabelProvider());

		final Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayoutData(BorderLayout.SOUTH);
		composite_1.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Button selectAllBtn = new Button(composite_1, SWT.NONE);
		selectAllBtn.setText("Select all/none");
		selectAllBtn.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				// is the first one selected
				final int num = checkboxTableViewer.getCheckedElements().length;

				boolean doAll = false;
				if (num == 0)
					doAll = true;

				checkboxTableViewer.setAllChecked(doAll);

			}
		});

		final Button importBtn = new Button(composite_1, SWT.NONE);
		importBtn
				.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD));
		importBtn.setText("Import");
		importBtn.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				doImport();
			}
		});
		
		filterControls.layout(true);
	}

	protected void doImport()
	{
		// get the selected rows
		final ArrayList<String> items = new ArrayList<String>();

		// have a look at the content
		final Object[] sel2 = checkboxTableViewer.getCheckedElements();
		for (int i = 0; i < sel2.length; i++)
		{
			final Match match = (Match) sel2[i];
			items.add(match.getId());
		}

		// pass the rows on
		if (_myListener != null)
			_myListener.doImport(items);
	}

	protected static class EasyBox implements FacetList
	{
		private final List _myList;

		public EasyBox(final List list)
		{
			_myList = list;
			// put in some dummy (spacer) data
			_myList.setData(new String[]{"asassd ", "sd23ds"});
		}

		@Override
		public ArrayList<String> getSelectedItems()
		{
			final ArrayList<String> res = new ArrayList<String>();
			final int[] items = _myList.getSelectionIndices();
			for (int i = 0; i < items.length; i++)
			{
				final int j = items[i];
				final String thisItem = _myList.getItem(j);
				res.add(thisItem);
			}
			return res;
		}

		@Override
		public void setItems(final ArrayList<String> items, final boolean keepSelection)
		{
			_myList.removeAll();
			for (final Iterator<String> iterator = items.iterator(); iterator.hasNext();)
			{
				final String string = (String) iterator.next();
				_myList.add(string);
			}
		}

	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void setListener(final Listener listener)
	{
		_myListener = listener;
	}

	@Override
	public FacetList getPlatforms()
	{
		return _platforms;
	}

	@Override
	public FacetList getPlatformTypes()
	{
		return _platformTypes;
	}

	@Override
	public FacetList getTrials()
	{
		return _trials;
	}

	@Override
	public void setFoxus()
	{
		table.setFocus();
	}

	@Override
	public String getFreeText()
	{
		return null;
	}

	@Override
	public void setResults(final MatchList res)
	{
		checkboxTableViewer.setInput(res);
	}

	@Override
	public void enableControls(final boolean enabled)
	{
		filterControls.setEnabled(enabled);
		searchControls.setEnabled(enabled);
	}

	protected static class MatchContentProvider implements
			IStructuredContentProvider
	{

		@Override
		public void dispose()
		{

		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
		{

		}

		@Override
		public Object[] getElements(final Object inputElement)
		{
			final Comparator<Match> comparator = new Comparator<Match>()
			{

				@Override
				public int compare(final Match arg0, final Match arg1)
				{
					return arg0.getName().compareTo(arg1.getName());
				}
			};
			final SortedSet<Match> items = new TreeSet<Match>(comparator);
			final MatchList item = (MatchList) inputElement;
			final int len = item.getNumMatches();
			for (int i = 0; i < len; i++)
			{
				final Match match = item.getMatch(i);
				items.add(match);
			}
			return items.toArray();
		}

	}

	protected static class MatchLabelProvider implements ILabelProvider
	{

		@Override
		public void addListener(final ILabelProviderListener listener)
		{
		}

		@Override
		public void dispose()
		{

		}

		@Override
		public boolean isLabelProperty(final Object element, final String property)
		{
			return false;
		}

		@Override
		public void removeListener(final ILabelProviderListener listener)
		{
		}

		@Override
		public Image getImage(final Object element)
		{
			return null;
		}

		@Override
		public String getText(final Object element)
		{
			final Match match = (Match) element;
			return match.getPlatform();
		}

	}

	@Override
	public ISelectionProvider getSelectionProvider()
	{
		return checkboxTableViewer;
	}

}
