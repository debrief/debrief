package com.planetmayo.debrief.satc.ui;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

public class UIUtils {
	
	public static GridLayout createGridLayoutWithoutMargins(int numColumns, boolean makeColumnsEqualWidth) {
		GridLayout gridLayout = new GridLayout(numColumns, makeColumnsEqualWidth);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		return gridLayout;
	}
	
	public static Label createLabel(Composite parent, String text, Object layoutData) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		label.setLayoutData(layoutData);
		return label;
	}
	
	public static Label createSpacer(Composite parent, Object layoutData) {
		return createLabel(parent, "", layoutData);
	}
	
	public static Composite createEmptyComposite(Composite parent, Layout layout, Object layoutData) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);
		return composite; 
	}
	
	public static UpdateValueStrategy converterStrategy(IConverter converter) {
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(converter);
		return strategy;
	}
}
