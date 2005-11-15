package com.borlander.rac353542.bislider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Snippet {

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
        shell.setText("Bi-Slider demo");
        shell.setLayout (new FillLayout ());
        Composite parent = new Composite(shell, SWT.NONE);
        parent.setLayout(new FillLayout());
        new BiSlider(parent, SWT.NONE);
        shell.setSize (500, 200);
    }
    
}
