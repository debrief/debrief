package org.mwc.asset.netasset2.view;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ListViewer;

public class VControl extends Composite implements IVClient
{
	private Table table;
	private Button btnPing;
	private ListViewer listServerViewer;
	private ListViewer listScenarioViewer;
	private List listServers;
	private List listScenarios;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public VControl(Composite parent, int style)
	{
		super(parent, style);
		setLayout(null);

		Group grpConnection = new Group(this, SWT.NONE);
		grpConnection.setBounds(0, 0, 260, 225);

		Label lblServers = new Label(grpConnection, SWT.NONE);
		lblServers.setBounds(10, 27, 59, 14);
		lblServers.setText("Servers");

		btnPing = new Button(grpConnection, SWT.NONE);
		btnPing.setBounds(10, 0, 47, 28);
		btnPing.setText("Ping");

		Label lblScenarios = new Label(grpConnection, SWT.NONE);
		lblScenarios.setText("Scenarios");
		lblScenarios.setBounds(133, 27, 59, 14);

		Label lblParticipants = new Label(grpConnection, SWT.NONE);
		lblParticipants.setText("Participants");
		lblParticipants.setBounds(10, 113, 67, 14);

		TableViewer tableViewer = new TableViewer(grpConnection, SWT.BORDER
				| SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setBounds(10, 129, 240, 81);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer,
				SWT.NONE);
		TableColumn colName = tableViewerColumn.getColumn();
		colName.setWidth(100);
		colName.setText("Name");

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer,
				SWT.NONE);
		TableColumn colCategory = tableViewerColumn_1.getColumn();
		colCategory.setWidth(100);
		colCategory.setText("Category");

		listServerViewer = new ListViewer(grpConnection, SWT.BORDER | SWT.V_SCROLL);
		 listServers = listServerViewer.getList();
		listServers.setBounds(10, 41, 105, 66);

		listScenarioViewer = new ListViewer(grpConnection, SWT.BORDER
				| SWT.V_SCROLL);
		listScenarios = listScenarioViewer.getList();
		listScenarios.setBounds(127, 41, 119, 66);

	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void addPingListener(SelectionAdapter handler)
	{
		btnPing.addSelectionListener(handler);
	}
	
	@Override
	public ListViewer getServerList()
	{
		return listServerViewer;
	}
	
	@Override
	public ListViewer getScenarioList()
	{
		return listScenarioViewer;
	}

	@Override
	public void addServerListener(IDoubleClickListener selectionAdapter)
	{
		listServerViewer.addDoubleClickListener(selectionAdapter);
	}

	@Override
	public void disableServers()
	{
		listServers.setEnabled(false);
	}

	@Override
	public void enableServers()
	{
		listServers.setEnabled(true);
	}

	@Override
	public void disableScenarios()
	{
		listScenarios.setEnabled(false);
	}

	@Override
	public void enableScenarios()
	{
		listScenarios.setEnabled(true);
	}

	@Override
	public void addScenarioListener(IDoubleClickListener iDoubleClickListener)
	{
		listScenarioViewer.addDoubleClickListener(iDoubleClickListener);
	}
}
