package org.mwc.asset.scenariocontroller2.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import org.mwc.asset.scenariocontroller2.views.MultiScenarioView.UIDisplay;

public class UISkeleton2 extends Composite implements UIDisplay
{

	/**
	 * Auto-generated method to display this org.eclipse.swt.widgets.Composite
	 * inside a new Shell.
	 */
	public static void showGUI()
	{
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		UISkeleton2 inst = new UISkeleton2(shell, SWT.NULL);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if (size.x == 0 && size.y == 0)
		{
			inst.pack();
			shell.pack();
		}
		else
		{
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private Label scenarioVal;
	private Label controlVal;
	private Button btnGenerate;
	private Button btnRunAll;
	private Button btnInit;
	private Button btnStep;
	private Button btnPlay;
	private Composite multiTableHolder;
	private Label lblTime;
	private Font _timeFont;
	private Color _timeFore;
	private Color _timeBack;
	private Group grpDataFiles;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public UISkeleton2(Composite parent, int style)
	{
		super(parent, style);
		setLayout(new FormLayout());

		Composite topPanel = new Composite(this, SWT.NONE);
		FormData fd_topPanel = new FormData();
		fd_topPanel.right = new FormAttachment(100, -10);
		fd_topPanel.top = new FormAttachment(0, 3);
		fd_topPanel.left = new FormAttachment(0, 3);
		topPanel.setLayoutData(fd_topPanel);
		topPanel.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		grpDataFiles = new Group(topPanel, SWT.NONE);
		grpDataFiles.setText("Data files");
		GridLayout gl_grpDataFiles = new GridLayout(1, false);
		gl_grpDataFiles.verticalSpacing = 0;
		gl_grpDataFiles.marginWidth = 0;
		gl_grpDataFiles.marginHeight = 0;
		gl_grpDataFiles.horizontalSpacing = 0;
		grpDataFiles.setLayout(gl_grpDataFiles);

		Composite filenameHolder = new Composite(grpDataFiles, SWT.NONE);
		GridLayout gl_filenameHolder = new GridLayout(1, false);
		gl_filenameHolder.verticalSpacing = 0;
		gl_filenameHolder.marginHeight = 0;
		gl_filenameHolder.horizontalSpacing = 0;
		filenameHolder.setLayout(gl_filenameHolder);

		Label lblScenarioFile = new Label(filenameHolder, SWT.NONE);
	//	lblScenarioFile.setBounds(0, 0, 59, 14);
		lblScenarioFile.setText("Scenario file:");

		scenarioVal = new Label(filenameHolder, SWT.NONE);
		scenarioVal.setAlignment(SWT.RIGHT);
		scenarioVal.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
//		scenarioVal.setBounds(0, 0, 59, 14);
		scenarioVal.setText("[pending]                     ");

		Label lblControlFile = new Label(filenameHolder, SWT.NONE);
//		lblControlFile.setBounds(0, 0, 59, 14);
		lblControlFile.setText("Control file:");

		controlVal = new Label(filenameHolder, SWT.NONE);
		controlVal.setAlignment(SWT.RIGHT);
		controlVal.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
// 		controlVal.setBounds(0, 0, 59, 14);
		controlVal.setText("[pending]               ");

		Group grpManageScenarios = new Group(topPanel, SWT.NONE);
		grpManageScenarios.setText("Manage scenarios");
		grpManageScenarios.setLayout(new RowLayout(SWT.HORIZONTAL));

		Group grpAllScenarios = new Group(grpManageScenarios, SWT.NONE);
		grpAllScenarios.setText("All scenarios");
		grpAllScenarios.setLayout(new RowLayout(SWT.VERTICAL));

		btnGenerate = new Button(grpAllScenarios, SWT.NONE);
		btnGenerate.setText("Generate");

		btnRunAll = new Button(grpAllScenarios, SWT.NONE);
		btnRunAll.setText("Run all");

		Group grpSelectedScenario = new Group(grpManageScenarios, SWT.NONE);
		grpSelectedScenario.setText("Selected scenario");
		grpSelectedScenario.setLayout(new GridLayout(2, false));

		btnInit = new Button(grpSelectedScenario, SWT.NONE);
		btnInit.setText("Init");
		btnInit.setEnabled(false);

		btnPlay = new Button(grpSelectedScenario, SWT.NONE);
		btnPlay.setText(PLAY_LABEL);
		btnPlay.setEnabled(false);

		btnStep = new Button(grpSelectedScenario, SWT.NONE);
		btnStep.setText("Step");
		btnStep.setEnabled(false);

		lblTime = new Label(grpSelectedScenario, SWT.NONE);
		_timeFont = SWTResourceManager.getFont("Courier New", 11, SWT.BOLD);
		_timeFore = SWTResourceManager.getColor(SWT.COLOR_GREEN);
		_timeBack = SWTResourceManager.getColor(105, 105, 105);
		lblTime.setFont(_timeFont);
		lblTime.setForeground(_timeFore);
		lblTime.setBackground(_timeBack);
		lblTime.setText("00:00:00");

		multiTableHolder = new Composite(this, SWT.BORDER);
		multiTableHolder.setLayout(new GridLayout(1, false));
		FormData fd_multiTableHolder = new FormData();
		fd_multiTableHolder.bottom = new FormAttachment(100, -10);
		fd_multiTableHolder.right = new FormAttachment(topPanel, 0, SWT.RIGHT);
		fd_multiTableHolder.top = new FormAttachment(topPanel, 6);
		fd_multiTableHolder.left = new FormAttachment(0, 10);
		multiTableHolder.setLayoutData(fd_multiTableHolder);

	}

	@Override
	public void dispose()
	{
		super.dispose();

		// and ditch our custom objects
		_timeFont.dispose();
		_timeFore.dispose();
		_timeBack.dispose();
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	public Composite getMultiTableHolder()
	{
		return multiTableHolder;
	}

	public void setScenario(final String name)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				scenarioVal.setText(name);
			}
		});
	}

	public void setControl(final String name)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				controlVal.setText(name);
			}
		});
	}

	public void addGenerateListener(final SelectionListener listener)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				btnGenerate.addSelectionListener(listener);
			}
		});
	}

	public void addRunAllListener(final SelectionListener listener)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				btnRunAll.addSelectionListener(listener);
			}
		});
	}

	public void setRunAllEnabled(final boolean b)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				btnRunAll.setEnabled(b);
			}
		});
	}

	public void setGenerateEnabled(final boolean b)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				btnGenerate.setEnabled(b);
			}
		});
	}

	public void setInitEnabled(final boolean enabled)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				btnInit.setEnabled(enabled);
			}
		});
	}

	public void setStepEnabled(final boolean enabled)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				btnStep.setEnabled(enabled);
			}
		});
	}

	public void setPlayEnabled(final boolean enabled)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				btnPlay.setEnabled(enabled);
			}
		});
	}

	public void setPlayLabel(final String text)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				btnPlay.setText(text);
			}
		});
	}

	public void setTime(final String time)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				lblTime.setText(time);
			}
		});
	}

	public void addInitListener(SelectionAdapter selectionAdapter)
	{
		btnInit.addSelectionListener(selectionAdapter);
	}

	public void addStepListener(SelectionAdapter selectionAdapter)
	{
		btnStep.addSelectionListener(selectionAdapter);
	}

	public void addPlayListener(SelectionAdapter selectionAdapter)
	{
		btnPlay.addSelectionListener(selectionAdapter);
	}

}
