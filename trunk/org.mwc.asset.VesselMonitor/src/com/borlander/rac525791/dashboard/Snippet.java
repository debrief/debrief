package com.borlander.rac525791.dashboard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

import com.borlander.rac525791.dashboard.data.DashboardDataModel;

public class Snippet extends Composite {

    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        decorateShell(shell);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

    private static void decorateShell(Shell shell) {
        shell.setText("Vessel Dashboard Demo");
        shell.setLayout (new FillLayout(SWT.VERTICAL));
        new Snippet(shell);
        shell.setSize(600, 800);
    }
    
    public Snippet(Composite parent) {
    	super(parent, SWT.NONE);
    	setLayout(new GridLayout(2, false));
    	
    	Dashboard dashboard = createDashboardInSash();
    	final DashboardDataModel dataModel = dashboard.getDataModel(); 
    	
    	createLabeledText("Vessel Name:", "HMS \"Victory\" -- one of the best ships ever", new DataChange(dataModel) {
			public void apply(String value) {
				getDataModel().setVesselName(value);
			}
		});

    	createLabeledText("Vessel Status:", "Battle of Trafalgar (Do you know who will win?)", new DataChange(dataModel) {
			public void apply(String value) {
				getDataModel().setVesselStatus(value);
			}
		});
    	
    	createLabeledSlider("Actual Course:", 0, 360, 90, new DataChange(dataModel) {
			public void apply(int value) {
				getDataModel().setActualDirection(value);
			}
		});

    	createLabeledSlider("Demanded Course:", 0, 360, 135, new DataChange(dataModel) {
			public void apply(int value) {
				getDataModel().setDemandedDirection(value);
			}
		});
    	
    	createLabeledCheckBox("Ignore Demanded Course:", new DataChange(dataModel) {
			public void apply(boolean value) {
				getDataModel().setIgnoreDemandedDirection(value);
			}
		});
    	
    	createLabeledSlider("Allowed Course Threshold:", 0, 10, 5, new DataChange(dataModel) {
			public void apply(int value) {
				getDataModel().setDirectionThreshold(value);
			}
		});

    	createLabeledSlider("Actual Speed:", 0, 1000, 350, new DataChange(dataModel) {
			public void apply(int value) {
				getDataModel().setActualSpeed(value);
			}
		});

    	createLabeledSlider("Demanded Speed:", 0, 1000, 350, new DataChange(dataModel) {
			public void apply(int value) {
				getDataModel().setDemandedSpeed(value);
			}
		});
    	
    	createLabeledCheckBox("Ignore Demanded Speed:", new DataChange(dataModel) {
			public void apply(boolean value) {
				getDataModel().setIgnoreDemandedSpeed(value);
			}
		});
    	
    	createLabeledSlider("Allowed Speed Threshold:", 0, 100, 20, new DataChange(dataModel) {
			public void apply(int value) {
				getDataModel().setSpeedThreshold(value);
			}
		});

    	createLabeledSlider("Actual Depth:", 0, 1000, 700, new DataChange(dataModel) {
			public void apply(int value) {
				getDataModel().setActualDepth(value);
			}
		});

    	createLabeledSlider("Demanded Depth:", 0, 1000, 900, new DataChange(dataModel) {
			public void apply(int value) {
				getDataModel().setDemandedDepth(value);
			}
		});
    	
    	createLabeledCheckBox("Ignore Demanded Depth:", new DataChange(dataModel) {
			public void apply(boolean value) {
				getDataModel().setIgnoreDemandedDepth(value);
			}
		});

    	createLabeledSlider("Allowed Depth Threshold:", 0, 100, 80, new DataChange(dataModel) {
			public void apply(int value) {
				getDataModel().setDepthThreshold(value);
			}
		});

    	createLabeledCombo("Vertical units:", new String[] {"depth", "alt"}, new DataChange(dataModel) {
			public void apply(String value) {
				getDataModel().setDepthUnits(value);
			}
		});
    	
    	createLabeledCombo("Horizontal units:", new String[] {"km/h", "mph", "m/sec"}, new DataChange(dataModel) {
			public void apply(String value) {
				getDataModel().setSpeedUnits(value);
			}
		});
    	
    }
    
    private Combo createLabeledCombo(String label, String[] values, DataChange change){
    	createLabel(label);
    	
    	Combo result = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.BORDER);
    	result.setLayoutData(rightGD());
    	result.setItems(values);
    	
    	result.addSelectionListener(new ComboListener(change));
    	result.select(0);
    	
    	change.apply(values[0]);
    	return result;
    }
    
    private Slider createLabeledSlider(String label, int min, int max, int initial, DataChange change){
    	createLabel(label);
    	
    	Composite group = new Composite(this, SWT.NONE);
    	group.setLayout(new GridLayout(2, false));
    	group.setLayoutData(rightGD());
    	
    	final Slider slider = new Slider(group, SWT.HORIZONTAL);
    	GridData sliderGD = new GridData();
    	sliderGD.grabExcessHorizontalSpace = true;
    	sliderGD.horizontalAlignment = GridData.FILL;
    	slider.setLayoutData(sliderGD);
    	
    	final Label value = new Label(group, SWT.READ_ONLY);
    	GridData valueGD = new GridData();
    	valueGD.horizontalAlignment = GridData.FILL;
    	valueGD.widthHint = 40;
    	value.setLayoutData(valueGD);
    	
    	slider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				value.setText("[" + slider.getSelection() + "]");
			}
		});

    	slider.setIncrement(1);
    	
    	/*
		 * I do not understand why I need to add 10, but slider with maximum of
		 * 360 selects 350 at the rightmost position
		 */
    	slider.setMaximum(max + 10); 
    	slider.setMinimum(min);
    	slider.setSelection(initial);
    	slider.addSelectionListener(new SliderListener(change));
    	change.apply(initial);
    	value.setText("[" + slider.getSelection() + "]");
    	return slider;
    }
    
    private Text createLabeledText(String label, String text, DataChange change){
    	createLabel(label);
    	Text result = new Text(this, SWT.SINGLE | SWT.BORDER);
    	result.setLayoutData(rightGD());
    	
    	result.addModifyListener(new TextListener(change));
    	result.setText(text);
    	return result;
    }
    
    private Label createLabel(String text){
    	Label label = new Label(this, SWT.NONE);
    	label.setText(text);
    	label.setLayoutData(leftGD());
    	return label;
    }
    
    private Button createLabeledCheckBox(String label, DataChange change){
    	createLabel(label);
    	Button result = new Button(this, SWT.CHECK);
    	result.setLayoutData(rightGD());
    	result.addSelectionListener(new CheckBoxListener(change));
    	return result;
    }
    
    protected Dashboard createDashboardInSash(){
    	Group dashboardPanel = new Group(this, SWT.NONE);
    	dashboardPanel.setLayout(new GridLayout(1, true));
    	dashboardPanel.setText("Sample");
    	
    	SashForm form = new SashForm(dashboardPanel, SWT.HORIZONTAL | SWT.BORDER);
    	SashForm topForm = new SashForm(form, SWT.VERTICAL | SWT.BORDER);
    	new Composite(form, SWT.NONE);
    	form.setWeights(new int[]{9, 1});

    	Dashboard dashboard = new Dashboard(topForm);
    	new Composite(topForm, SWT.NONE);
    	topForm.setWeights(new int[] {9, 1});

    	GridData panelGD = new GridData();
    	panelGD.horizontalAlignment = GridData.FILL;
    	panelGD.verticalAlignment = GridData.FILL;
    	panelGD.horizontalSpan = 2;
    	panelGD.grabExcessHorizontalSpace = true;
    	panelGD.grabExcessVerticalSpace = true;
    	dashboardPanel.setLayoutData(panelGD);
    	
    	GridData formGD = new GridData();
    	formGD.horizontalAlignment = GridData.FILL;
    	formGD.verticalAlignment = GridData.FILL;
    	formGD.grabExcessHorizontalSpace = true;
    	formGD.grabExcessVerticalSpace = true;
    	form.setLayoutData(formGD);
    	
    	return dashboard;
    }
    
    protected Dashboard createDashboardInGrid(){
    	Group dashboardPanel = new Group(this, SWT.NONE);
    	dashboardPanel.setLayout(new GridLayout(1, false));
    	dashboardPanel.setText("Sample");
    	Dashboard dashboard = new Dashboard(dashboardPanel);
    	GridData dashGD = new GridData();
    	dashGD.horizontalAlignment = GridData.FILL;
    	dashGD.verticalAlignment = GridData.FILL;
    	dashGD.grabExcessHorizontalSpace = true;
    	dashGD.grabExcessVerticalSpace = true;
    	dashboard.setLayoutData(dashGD);
    	
    	GridData panelGD = new GridData();
    	panelGD.horizontalAlignment = GridData.FILL;
    	panelGD.verticalAlignment = GridData.FILL;
    	panelGD.horizontalSpan = 2;
    	panelGD.grabExcessHorizontalSpace = true;
    	panelGD.grabExcessVerticalSpace = true;
    	dashboardPanel.setLayoutData(panelGD);
    	
    	return dashboard;
    }

    private GridData leftGD(){
    	GridData left = new GridData();
    	left.horizontalAlignment = GridData.BEGINNING;
    	left.verticalAlignment = GridData.CENTER;
    	return left;
    }
    
    private GridData rightGD(){
    	GridData right = new GridData();
    	right.horizontalAlignment = GridData.FILL;
    	right.verticalAlignment = GridData.CENTER;
    	right.grabExcessHorizontalSpace = true;
    	return right;
    }
    
    private static abstract class DataChange {
    	private final DashboardDataModel myModel;

		public DataChange(DashboardDataModel model){
			myModel = model;
    	}
		
		protected DashboardDataModel getDataModel(){
			return myModel;
		}
    	
    	public void apply(String value){
    		//
    	}
    	
    	public void apply(int value){
    		//
    	}
    	
    	public void apply(boolean value){
    		//
    	}
    }
    
    private static class TextListener implements ModifyListener {
    	private final DataChange myChange;

		public TextListener(DataChange change){
			myChange = change;
    	}
		
		public void modifyText(ModifyEvent e) {
			Text widget = (Text)e.widget;
			String text = widget.getText();
			if (text == null){
				text = "";
			}
			myChange.apply(text);
		}
    }
    
    private static class SliderListener implements SelectionListener {
    	private final DataChange myChange;

		public SliderListener(DataChange change){
			myChange = change;
    	}
		
		public void widgetSelected(SelectionEvent e) {
			Slider widget = (Slider)e.widget;
			myChange.apply(widget.getSelection());
		}
		
		public void widgetDefaultSelected(SelectionEvent e) {
			throw new UnsupportedOperationException("Slider should not call this");
		}
		
    }
    
    private static class ComboListener implements SelectionListener {
    	private final DataChange myChange;

		public ComboListener(DataChange change){
			myChange = change;
    	}
		
		public void widgetSelected(SelectionEvent e) {
			Combo widget = (Combo)e.widget;
			myChange.apply(widget.getItem(widget.getSelectionIndex()));
		}
		
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
		
    }
    
    private static class CheckBoxListener implements SelectionListener {
    	private final DataChange myChange;

		public CheckBoxListener(DataChange change){
			myChange = change;
    	}
		
		public void widgetSelected(SelectionEvent e) {
			Button widget = (Button)e.widget;
			myChange.apply(widget.getSelection());
		}
		
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
		
    }
    
}
