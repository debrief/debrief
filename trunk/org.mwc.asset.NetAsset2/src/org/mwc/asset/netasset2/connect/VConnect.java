package org.mwc.asset.netasset2.connect;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.mwc.asset.netCore.common.Network;
import org.mwc.asset.netCore.common.Network.LightParticipant;
import org.mwc.asset.netCore.common.Network.LightScenario;

public class VConnect extends Composite implements IVConnect
{
	private Table partTable;
	private Button btnPing;
	private ListViewer listServerViewer;
	private ListViewer listScenarioViewer;
	private List listServers;
	private List listScenarios;
	private TableViewer partViewer;
	private Button btnDisconnect;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public VConnect(Composite parent, int style)
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
		lblScenarios.setBounds(106, 27, 59, 14);

		Label lblParticipants = new Label(grpConnection, SWT.NONE);
		lblParticipants.setText("Participants");
		lblParticipants.setBounds(10, 113, 67, 14);

		partViewer = new TableViewer(grpConnection, SWT.BORDER | SWT.FULL_SELECTION);
		partTable = partViewer.getTable();
		partTable.setBounds(10, 129, 240, 81);

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
		listServers.setBounds(10, 41, 90, 66);

		listScenarioViewer = new ListViewer(grpConnection, SWT.BORDER
				| SWT.V_SCROLL);
		listScenarios = listScenarioViewer.getList();
		listScenarios.setBounds(106, 41, 140, 66);

		Button button = new Button(grpConnection, SWT.NONE);
		button.setText("Ping");
		button.setBounds(10, 0, 47, 28);

		btnDisconnect = new Button(grpConnection, SWT.NONE);
		btnDisconnect.setText("Disconnect");
		btnDisconnect.setBounds(160, 13, 90, 28);

	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void addPingListener(final ClickHandler handler)
	{
		btnPing.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				handler.clicked();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});
	}

	@Override
	public void addDisconnectListener(final ClickHandler handler)
	{
		btnDisconnect.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				handler.clicked();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});
	}

	@Override
	public void addServerListener(final ServerSelected listener)
	{
		listServerViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				ISelection sel = event.getSelection();
				StructuredSelection ss = (StructuredSelection) sel;
				InetAddress address = (InetAddress) ss.getFirstElement();
				listener.selected(address);
			}
		});
	}

	@Override
	public void disableServers()
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				listServers.setEnabled(false);
			}
		});
	}

	@Override
	public void enableServers()
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				listServers.setEnabled(true);
			}
		});
	}

	@Override
	public void disableScenarios()
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				listScenarios.setEnabled(false);
			}
		});
	}

	@Override
	public void enableScenarios()
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				listScenarios.setEnabled(true);
			}
		});
	}

	@Override
	public void addScenarioListener(final ScenarioSelected listener)
	{
		listScenarioViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				ISelection sel = event.getSelection();
				StructuredSelection ss = (StructuredSelection) sel;
				LightScenario scenario = (LightScenario) ss.getFirstElement();
				listener.selected(scenario);
			}
		});
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
	public void addParticipantListener(final ParticipantSelected listener)
	{
		partViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				ISelection sel = event.getSelection();
				StructuredSelection ss = (StructuredSelection) sel;
				LightParticipant part = (LightParticipant) ss.getFirstElement();
				listener.selected(part);
			}
		});
	}

	@Override
	public void setParticipants(final Vector<LightParticipant> listOfParticipants)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				partViewer.setInput(listOfParticipants);
			}
		});
	}

	@Override
	public void setScenarios(final Vector<LightScenario> results)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				listScenarioViewer.getList().removeAll();
				Iterator<LightScenario> items = results.iterator();
				while (items.hasNext())
				{
					Network.LightScenario ls = (Network.LightScenario) items.next();
					listScenarioViewer.add(ls);
				}
			}
		});
	}

	@Override
	public void setServers(final java.util.List<InetAddress> adds)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				Iterator<InetAddress> items = adds.iterator();
				while (items.hasNext())
				{
					InetAddress inetAddress = (InetAddress) items.next();
					listServerViewer.add(inetAddress);
				}
			}
		});
	}

	@Override
	public void disableParticipants()
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				partTable.setEnabled(false);
			}
		});
	}

	@Override
	public void enableParticipants()
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				partTable.setEnabled(true);
			}
		});
	}

	@Override
	public void enableDisconnect()
	{
		btnDisconnect.setEnabled(true);
	}

	@Override
	public void disableDisconnect()
	{
		btnDisconnect.setEnabled(false);
	}
}
