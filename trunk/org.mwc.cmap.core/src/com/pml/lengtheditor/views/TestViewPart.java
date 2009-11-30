package com.pml.lengtheditor.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pml.lengtheditor.LengthPropertyDescriptor;

public class TestViewPart extends ViewPart {

	// init default value
	private Double myValue = new Double(0);

	private Label myLabel;

	private PropertySource myPS;

	@Override
	public void createPartControl(Composite parent) {
		myLabel = new Label(parent, SWT.NONE);
		myLabel.setText(myValue.toString());
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		getViewSite().setSelectionProvider(new ISelectionProvider() {

			public void setSelection(ISelection selection) {
				// nothing
			}

			public void removeSelectionChangedListener(ISelectionChangedListener listener) {
				// nothing
			}

			public ISelection getSelection() {
				return new StructuredSelection(getPropertySource());
			}

			public void addSelectionChangedListener(ISelectionChangedListener listener) {
				// nothing
			}
		});
	}

	@Override
	public void setFocus() {
		myLabel.setFocus();
	}

	private PropertySource getPropertySource() {
		if (myPS == null) {
			myPS = new PropertySource();
		}
		return myPS;
	}

	private static String PROPERTY_ID = "value";//$NON-NLS-1$

	private static final TextPropertyDescriptor LENGTH_PROP_DESC = new LengthPropertyDescriptor(PROPERTY_ID, "Length");

	private static final IPropertyDescriptor[] DESCRIPTORS = { LENGTH_PROP_DESC };

	private class PropertySource implements IPropertySource {

		public Object getEditableValue() {
			return myValue.toString();
		}

		public IPropertyDescriptor[] getPropertyDescriptors() {
			return DESCRIPTORS;
		}

		public Object getPropertyValue(Object id) {
			if (PROPERTY_ID.equals(id)) {
				return myValue.toString();
			}
			return null;
		}

		public boolean isPropertySet(Object id) {
			return false;
		}

		public void resetPropertyValue(Object id) {
			setNewValue(0.0);
		}

		public void setPropertyValue(Object id, Object value) {
			if (PROPERTY_ID.equals(id)) {
				try {
					setNewValue(new Double((String) value));
				} catch (Exception e) {
					// nothing
				}
			}

		}
	}

	public void setNewValue(Double d) {
		myValue = d;
		myLabel.setText(myValue.toString());
	}
}