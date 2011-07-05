package org.mwc.asset.netasset2.view;

import java.util.Vector;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.mwc.asset.netasset2.common.Network.LightParticipant;

public class VControl extends Composite implements IVControl
{
	private Table table;
	private Button btnPing;
	private ListViewer listServerViewer;
	private ListViewer listScenarioViewer;
	private List listServers;
	private List listScenarios;
	private TableViewer partViewer;

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

		partViewer = new TableViewer(grpConnection, SWT.BORDER | SWT.FULL_SELECTION);
		table = partViewer.getTable();
		table.setBounds(10, 129, 240, 81);

		TableViewerColumn nameCol = new TableViewerColumn(partViewer, SWT.NONE);
		TableColumn colName = nameCol.getColumn();
		colName.setWidth(50);
		colName.setText("Name");

		TableViewerColumn catCol = new TableViewerColumn(partViewer, SWT.NONE);
		TableColumn colCategory = catCol.getColumn();
		colCategory.setWidth(100);
		colCategory.setText("Category");

		TableViewerColumn actCol = new TableViewerColumn(partViewer, SWT.NONE);
		TableColumn colActivity = actCol.getColumn();
		colActivity.setWidth(100);
		colActivity.setText("Activity");

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

	@Override
	public void setPartContentProvider(IContentProvider provider)
	{
		partViewer.setContentProvider(provider);
	}

	@Override
	public void setPartLabelProvider(IBaseLabelProvider labelProvider)
	{
		partViewer.setLabelProvider(labelProvider);
	}

	@Override
	public void addParticipantListener(IDoubleClickListener listener)
	{
		partViewer.addDoubleClickListener(listener);
	}

	@Override
	public void setParticipants(Vector<LightParticipant> listOfParticipants)
	{
		partViewer.setInput(listOfParticipants); 
	}
}
