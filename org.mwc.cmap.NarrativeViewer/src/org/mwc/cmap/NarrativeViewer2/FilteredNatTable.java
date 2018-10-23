/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.NarrativeViewer2;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.internal.SelectionWithFocusRow;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * 
 * @author Anuradha
 * 
 *         Direct port from Jface FilteredTree
 * 
 * 
 * 
 */
public abstract class FilteredNatTable extends Composite
{

  private static boolean useNativeSearchField(final Composite composite)
  {
    if (useNativeSearchField == null)
    {
      useNativeSearchField = Boolean.FALSE;
      Text testText = null;
      try
      {
        testText = new Text(composite, SWT.SEARCH | SWT.ICON_CANCEL);
        useNativeSearchField =
            new Boolean((testText.getStyle() & SWT.ICON_CANCEL) != 0);
      }
      finally
      {
        if (testText != null)
        {
          testText.dispose();
        }
      }

    }
    return useNativeSearchField.booleanValue();
  }

  /**
   * The filter text widget to be used by this grid. This value may be <code>null</code> if there is
   * no filter widget, or if the controls have not yet been created.
   */
  protected Text filterText;

  /**
   * The control representing the clear button for the filter text entry. This value may be
   * <code>null</code> if no such button exists, or if the controls have not yet been created.
   * <p>
   * <strong>Note:</strong> As of 3.5, this is not used if the new look is chosen.
   * </p>
   */
  protected ToolBarManager filterToolBar;

  /**
   * The control representing the clear button for the filter text entry. This value may be
   * <code>null</code> if no such button exists, or if the controls have not yet been created.
   * <p>
   * <strong>Note:</strong> This is only used if the new look is chosen.
   * </p>
   * 
   * @since 3.5
   */
  protected Control clearButtonControl;

  /**
   * The Composite on which the filter controls are created. This is used to set the background
   * color of the filter controls to match the surrounding controls.
   */
  protected Composite filterComposite;

  /**
   * The text to initially show in the filter text control.
   */
  protected String initialText = ""; //$NON-NLS-1$

  /**
   * The job used to refresh the grid.
   */
  private Job refreshJob;

  /**
   * The parent composite of the filtered grid.
   * 
   * @since 3.3
   */
  protected Composite parent;

  /**
   * Whether or not to show the filter controls (text and clear button). The default is to show
   * these controls. This can be overridden by providing a setting in the product configuration
   * file. The setting to add to not show these controls is:
   * 
   * org.eclipse.ui/SHOW_FILTERED_TEXTS=false
   */
  protected boolean showFilterControls;

  /**
   * @since 3.3
   */
  protected Composite gridComposite;

  /**
   * Tells whether to use the pre 3.5 or the new look.
   * 
   * @since 3.5
   */
  private boolean useNewLook = false;

  private Control gridControl;

  /**
   * Image descriptor for enabled clear button.
   */
  private static final String CLEAR_ICON =
      "org.eclipse.ui.internal.dialogs.CLEAR_ICON"; //$NON-NLS-1$

  /**
   * Image descriptor for disabled clear button.
   */
  private static final String DISABLED_CLEAR_ICON =
      "org.eclipse.ui.internal.dialogs.DCLEAR_ICON"; //$NON-NLS-1$

  private static final String FilteredGrid_FilterMessage =
      "filter text (including * or ?)";

  private static final String FilteredGrid_ClearToolTip = "Clear";

  /**
   * Get image descriptors for the clear button.
   */
  static
  {
    ImageDescriptor descriptor =
        AbstractUIPlugin.imageDescriptorFromPlugin(PlatformUI.PLUGIN_ID,
            "$nl$/icons/full/etool16/clear_co.gif"); //$NON-NLS-1$
    if (descriptor != null)
    {
      JFaceResources.getImageRegistry().put(CLEAR_ICON, descriptor);
    }
    descriptor =
        AbstractUIPlugin.imageDescriptorFromPlugin(PlatformUI.PLUGIN_ID,
            "$nl$/icons/full/dtool16/clear_co.gif"); //$NON-NLS-1$
    if (descriptor != null)
    {
      JFaceResources.getImageRegistry().put(DISABLED_CLEAR_ICON, descriptor);
    }
  }

  private static Boolean useNativeSearchField;

  /**
   * Create a new instance of the receiver.
   * 
   * @param parent
   *          the parent <code>Composite</code>
   * @param gridStyle
   *          the style bits for the <code>Grid</code>
   * @param filter
   *          the filter to be used
   * @param useNewLook
   *          <code>true</code> if the new 3.5 look should be used
   * @since 3.5
   */
  public FilteredNatTable(final Composite parent, final int gridStyle,
      final boolean useNewLook)
  {
    super(parent, SWT.NONE);
    this.parent = parent;
    this.useNewLook = useNewLook;
    init(gridStyle);
  }

  /**
   * Clears the text in the filter text widget.
   */
  protected void clearText()
  {
    setFilterText(""); //$NON-NLS-1$
    textChanged();
  }

  private Image getImageFromJFaceResources(String imageDes) {
    if(JFaceResources.getImageRegistry().getDescriptor(imageDes)!=null) {
      return JFaceResources.getImageRegistry().getDescriptor(imageDes).createImage();
    }
    return null;
  }
  /**
   * Create the button that clears the text.
   * 
   * @param parent
   *          parent <code>Composite</code> of toolbar button
   */
  private void createClearTextNew(final Composite parent)
  {
    // only create the button if the text widget doesn't support one
    // natively
    if ((filterText.getStyle() & SWT.ICON_CANCEL) == 0)
    {
      
      final Image inactiveImage =getImageFromJFaceResources(DISABLED_CLEAR_ICON);
      final Image activeImage =getImageFromJFaceResources(CLEAR_ICON);
      final Image pressedImage = activeImage==null?null:
          new Image(getDisplay(), activeImage, SWT.IMAGE_GRAY);

      final Label clearButton = new Label(parent, SWT.NONE);
      clearButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
          false));
      clearButton.setImage(inactiveImage);
      clearButton.setBackground(parent.getDisplay().getSystemColor(
          SWT.COLOR_LIST_BACKGROUND));
      clearButton.setToolTipText(FilteredGrid_ClearToolTip);
      clearButton.addMouseListener(new MouseAdapter()
      {
        private MouseMoveListener fMoveListener;

        private boolean isMouseInButton(final MouseEvent e)
        {
          final Point buttonSize = clearButton.getSize();
          return 0 <= e.x && e.x < buttonSize.x && 0 <= e.y
              && e.y < buttonSize.y;
        }

        @Override
        public void mouseDown(final MouseEvent e)
        {
          clearButton.setImage(pressedImage);
          fMoveListener = new MouseMoveListener()
          {
            private boolean fMouseInButton = true;

            @Override
            public void mouseMove(final MouseEvent e)
            {
              final boolean mouseInButton = isMouseInButton(e);
              if (mouseInButton != fMouseInButton)
              {
                fMouseInButton = mouseInButton;
                clearButton.setImage(mouseInButton ? pressedImage
                    : inactiveImage);
              }
            }
          };
          clearButton.addMouseMoveListener(fMoveListener);
        }

        @Override
        public void mouseUp(final MouseEvent e)
        {
          if (fMoveListener != null)
          {
            clearButton.removeMouseMoveListener(fMoveListener);
            fMoveListener = null;
            final boolean mouseInButton = isMouseInButton(e);
            clearButton.setImage(mouseInButton ? activeImage : inactiveImage);
            if (mouseInButton)
            {
              clearText();
              filterText.setFocus();
            }
          }
        }
      });
      clearButton.addMouseTrackListener(new MouseTrackListener()
      {
        @Override
        public void mouseEnter(final MouseEvent e)
        {
          clearButton.setImage(activeImage);
        }

        @Override
        public void mouseExit(final MouseEvent e)
        {
          clearButton.setImage(inactiveImage);
        }

        @Override
        public void mouseHover(final MouseEvent e)
        {
        }
      });
      clearButton.addDisposeListener(new DisposeListener()
      {
        @Override
        public void widgetDisposed(final DisposeEvent e)
        {
          if(inactiveImage!=null) {
            inactiveImage.dispose();
          }
          if(activeImage!=null) {
            activeImage.dispose();
          }
          if(pressedImage!=null) {
            pressedImage.dispose();
          }
        }
      });

      this.clearButtonControl = clearButton;
    }
  }

  /**
   * Create the button that clears the text.
   * 
   * @param parent
   *          parent <code>Composite</code> of toolbar button
   */
  private void createClearTextOld(final Composite parent)
  {
    // only create the button if the text widget doesn't support one
    // natively
    if ((filterText.getStyle() & SWT.ICON_CANCEL) == 0)
    {
      filterToolBar = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL);
      filterToolBar.createControl(parent);

      final IAction clearTextAction = new Action("", IAction.AS_PUSH_BUTTON) {//$NON-NLS-1$
            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run()
            {
              clearText();
            }
          };

      clearTextAction.setToolTipText(FilteredGrid_ClearToolTip);
      clearTextAction.setImageDescriptor(JFaceResources.getImageRegistry()
          .getDescriptor(CLEAR_ICON));
      clearTextAction.setDisabledImageDescriptor(JFaceResources
          .getImageRegistry().getDescriptor(DISABLED_CLEAR_ICON));

      filterToolBar.add(clearTextAction);
    }
  }

  /**
   * Create the filtered grid's controls. Subclasses should override.
   * 
   * @param parent
   * @param gridStyle
   */
  protected void createControl(final Composite parent, final int gridStyle)
  {
    final GridLayout layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    setLayout(layout);
    setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    if (showFilterControls)
    {
      if (!useNewLook || useNativeSearchField(parent))
      {
        filterComposite = new Composite(this, SWT.NONE);
      }
      else
      {
        filterComposite = new Composite(this, SWT.BORDER);
        filterComposite.setBackground(getDisplay().getSystemColor(
            SWT.COLOR_LIST_BACKGROUND));
      }
      final GridLayout filterLayout = new GridLayout(2, false);
      filterLayout.marginHeight = 0;
      filterLayout.marginWidth = 0;
      filterComposite.setLayout(filterLayout);
      filterComposite.setFont(parent.getFont());

      createFilterControls(filterComposite);
      filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
          false));
    }

    gridComposite = new Composite(this, SWT.NONE);
    final GridLayout gridCompositeLayout = new GridLayout();
    gridCompositeLayout.marginHeight = 0;
    gridCompositeLayout.marginWidth = 0;
    gridComposite.setLayout(gridCompositeLayout);
    final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
    gridComposite.setLayoutData(data);
    gridControl = createGridControl(gridComposite, gridStyle);
  }

  /**
   * Create the filter controls. By default, a text and corresponding tool bar button that clears
   * the contents of the text is created. Subclasses may override.
   * 
   * @param parent
   *          parent <code>Composite</code> of the filter controls
   * @return the <code>Composite</code> that contains the filter controls
   */
  protected Composite createFilterControls(final Composite parent)
  {
    createFilterText(parent);
    if (useNewLook)
    {
      createClearTextNew(parent);
    }
    else
    {
      createClearTextOld(parent);
    }
    if (clearButtonControl != null)
    {
      // initially there is no text to clear
      clearButtonControl.setVisible(false);
    }
    if (filterToolBar != null)
    {
      filterToolBar.update(false);
      // initially there is no text to clear
      filterToolBar.getControl().setVisible(false);
    }
    return parent;
  }

  /**
   * Creates the filter text and adds listeners. This method calls
   * {@link #doCreateFilterText(Composite)} to create the text control. Subclasses should override
   * {@link #doCreateFilterText(Composite)} instead of overriding this method.
   * 
   * @param parent
   *          <code>Composite</code> of the filter text
   */
  protected void createFilterText(final Composite parent)
  {
    filterText = doCreateFilterText(parent);

    filterText.addFocusListener(new FocusAdapter()
    {
      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
       */
      @Override
      public void focusGained(final FocusEvent e)
      {
        if (!useNewLook)
        {
          /*
           * Running in an asyncExec because the selectAll() does not appear to work when using
           * mouse to give focus to text.
           */
          final Display display = filterText.getDisplay();
          display.asyncExec(new Runnable()
          {
            @Override
            public void run()
            {
              if (!filterText.isDisposed())
              {
                if (getInitialText().equals(filterText.getText().trim()))
                {
                  filterText.selectAll();
                }
              }
            }
          });
          return;
        }
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.swt.events.FocusAdapter#focusLost(org.eclipse.swt.events.FocusEvent)
       */
      @Override
      public void focusLost(final FocusEvent e)
      {
        if (!useNewLook)
        {
          return;
        }
        if (filterText.getText().equals(initialText))
        {
          setFilterText(""); //$NON-NLS-1$
          textChanged();
        }
      }
    });

    if (useNewLook)
    {
      filterText.addMouseListener(new MouseAdapter()
      {
        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.swt.events.MouseAdapter#mouseDown(org.eclipse.swt.events.MouseEvent)
         */
        @Override
        public void mouseDown(final MouseEvent e)
        {
          if (filterText.getText().equals(initialText))
          {
            clearText();
          }
        }
      });
    }

    filterText.addKeyListener(new KeyAdapter()
    {
      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
       */
      @Override
      public void keyPressed(final KeyEvent e)
      {
        // on a CR we want to transfer focus to the list

        if (e.keyCode == SWT.ARROW_DOWN)
        {
          gridControl.setFocus();
          return;
        }
      }
    });

    // enter key set focus to grid
    filterText.addTraverseListener(new TraverseListener()
    {
      @Override
      public void keyTraversed(final TraverseEvent e)
      {
        if (e.detail == SWT.TRAVERSE_RETURN)
        {
          e.doit = false;
          // if (getViewer().getGrid().getItemCount() == 0)
          // {
          // Display.getCurrent().beep();
          // }
          // else
          // {
          // // if the initial filter text hasn't changed, do not try
          // // to match
          // boolean hasFocus = getViewer().getGrid().setFocus();
          // boolean textChanged =
          // !getInitialText().equals(filterText.getText().trim());
          // if (hasFocus && textChanged
          // && filterText.getText().trim().length() > 0)
          // {
          // selectFirst();
          //
          // }
          // }
        }
      }
    });

    filterText.addModifyListener(new ModifyListener()
    {
      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
       */
      @Override
      public void modifyText(final ModifyEvent e)
      {
        textChanged();
      }
    });

    // if we're using a field with built in cancel we need to listen for
    // default selection changes (which tell us the cancel button has been
    // pressed)
    if ((filterText.getStyle() & SWT.ICON_CANCEL) != 0)
    {
      filterText.addSelectionListener(new SelectionAdapter()
      {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.swt.events.SelectionAdapter#widgetDefaultSelected(org.eclipse.swt.events.
         * SelectionEvent)
         */
        @Override
        public void widgetDefaultSelected(final SelectionEvent e)
        {
          if (e.detail == SWT.ICON_CANCEL)
          {
            clearText();
          }
        }
      });
    }

    setFilterTextlayoutData();
  }

  protected abstract Control createGridControl(Composite parent, int style);

  /**
   * Create the refresh job for the receiver.
   * 
   */
  private void createRefreshJob()
  {
    refreshJob = doCreateRefreshJob();
    refreshJob.setSystem(true);
  }

  /**
   * Creates the text control for entering the filter text. Subclasses may override.
   * 
   * @param parent
   *          the parent composite
   * @return the text widget
   * 
   * @since 3.3
   */
  protected Text doCreateFilterText(final Composite parent)
  {
    if (!useNewLook || useNativeSearchField(parent))
    {
      return new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.SEARCH
          | SWT.ICON_CANCEL);
    }
    return new Text(parent, SWT.SINGLE);
  }

  /**
   * Creates the grid viewer. Subclasses may override.
   * 
   * @param parent
   *          the parent composite
   * @param style
   *          SWT style bits used to create the grid viewer
   * @return the grid viewer
   * 
   * @since 3.3
   */
  protected GridTableViewer doCreateGridViewer(final Composite parent,
      final int style)
  {
    return new GridTableViewer(parent, style)
    {

      /**
       * override getSelection in GridTableViewer to avoid bug with ILazyContentProvider and
       * GridTableViewer as it need to check grid.getFocusItem() before access getData()
       */
      @Override
      public ISelection getSelection()
      {
        final Grid grid = getGrid();
        if (!grid.isCellSelectionEnabled())
        {
          final List<?> list = getSelectionFromWidget();
          Object el = null;
          if (grid.getFocusItem() != null && !grid.getFocusItem().isDisposed())
          {
            el = grid.getFocusItem().getData();
          }
          return new SelectionWithFocusRow(list, el, getComparer());
        }
        else
        {
          return super.getSelection();
        }
      }

    };
  }

  /**
   * Creates a workbench job that will refresh the grid based on the current filter text. Subclasses
   * may override.
   * 
   * @return a workbench job that can be scheduled to refresh the grid
   * 
   * @since 3.4
   */
  protected WorkbenchJob doCreateRefreshJob()
  {
    return new WorkbenchJob("Refresh Filter") {//$NON-NLS-1$
      @Override
      public IStatus runInUIThread(final IProgressMonitor monitor)
      {
        if (gridControl.isDisposed())
        {
          return Status.CANCEL_STATUS;
        }

        if (monitor.isCanceled())
        {
          return Status.CANCEL_STATUS;
        }

        final String text = getFilterString();
        if (text == null)
        {
          return Status.OK_STATUS;
        }

        Display.getDefault().asyncExec(new Runnable()
        {

          @Override
          public void run()
          {
            final boolean initial =
                initialText != null && initialText.equals(text);

            try
            {

              updateGridData(text);

              if (text.length() > 0 && !initial)
              {

                // enabled toolbar - there is text to clear
                // and the list is currently being filtered
                updateToolbar(true);

              }
              else
              {
                // disabled toolbar - there is no text to clear
                // and the list is currently not filtered
                updateToolbar(false);
              }
            }
            finally
            {

            }
          }
        });
        return Status.OK_STATUS;
      }

    };
  }

  /**
   * Get the filter text for the receiver, if it was created. Otherwise return <code>null</code>.
   * 
   * @return the filter Text, or null if it was not created
   */
  public Text getFilterControl()
  {
    return filterText;
  }

  /**
   * Convenience method to return the text of the filter control. If the text widget is not created,
   * then null is returned.
   * 
   * @return String in the text, or null if the text does not exist
   */
  protected String getFilterString()
  {
    return filterText != null ? filterText.getText() : null;
  }

  public Composite getGridComposite()
  {
    return gridComposite;
  }

  /**
   * Get the initial text for the receiver.
   * 
   * @return String
   */
  protected String getInitialText()
  {
    return initialText;
  }

  /**
   * Return the time delay that should be used when scheduling the filter refresh job. Subclasses
   * may override.
   * 
   * @return a time delay in milliseconds before the job should run
   * 
   * @since 3.5
   */
  protected long getRefreshJobDelay()
  {
    return 200;
  }

  /**
   * Create the filtered grid.
   * 
   * @param gridStyle
   *          the style bits for the <code>Grid</code>
   * @param filter
   *          the filter to be used
   * 
   * @since 3.3
   */
  protected void init(final int gridStyle)
  {
    showFilterControls = true;
    createControl(parent, gridStyle);
    createRefreshJob();
    setInitialText(FilteredGrid_FilterMessage);
    setFont(parent.getFont());

  }

  /**
   * Select all text in the filter text field.
   * 
   */
  protected void selectAll()
  {
    if (filterText != null)
    {
      filterText.selectAll();
    }
  }

  protected void selectFirst()
  {

  }

  /**
   * Set the background for the widgets that support the filter text area.
   * 
   * @param background
   *          background <code>Color</code> to set
   */
  @Override
  public void setBackground(final Color background)
  {
    super.setBackground(background);
    if (filterComposite != null
        && (!useNewLook || useNativeSearchField(filterComposite)))
    {
      filterComposite.setBackground(background);
    }
    if (filterToolBar != null && filterToolBar.getControl() != null)
    {
      filterToolBar.getControl().setBackground(background);
    }
  }

  public void setFilterMode(final boolean checked)
  {
    clearText();
    filterText.setVisible(checked);
    filterComposite.setVisible(checked);
    if (checked)
    {
      filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
          false));
      setFilterTextlayoutData();
    }
    else
    {
      final GridData gridData =
          new GridData(SWT.FILL, SWT.CENTER, false, false);
      gridData.widthHint = 0;
      gridData.heightHint = 0;
      filterText.setLayoutData(gridData);
      GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, false,
          false);
      layoutData.widthHint = 0;
      layoutData.heightHint = 0;
      filterComposite.setLayoutData(layoutData);
    }
    layout(true);

  }

  /**
   * Set the text in the filter control.
   * 
   * @param string
   */
  protected void setFilterText(final String string)
  {
    if (filterText != null)
    {
      filterText.setText(string);
      selectAll();
    }
  }

  private void setFilterTextlayoutData()
  {
    final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    // if the text widget supported cancel then it will have it's own
    // integrated button. We can take all of the space.
    if ((filterText.getStyle() & SWT.ICON_CANCEL) != 0)
    {
      gridData.horizontalSpan = 2;
    }
    filterText.setLayoutData(gridData);
  }

  /**
   * Set the text that will be shown until the first focus. A default value is provided, so this
   * method only need be called if overriding the default initial text is desired.
   * 
   * @param text
   *          initial text to appear in text field
   */
  public void setInitialText(final String text)
  {
    initialText = text;
    if (useNewLook && filterText != null)
    {
      filterText.setMessage(text);
      if (filterText.isFocusControl())
      {
        setFilterText(initialText);
        textChanged();
      }
      else
      {
        getDisplay().asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            if (!filterText.isDisposed() && filterText.isFocusControl())
            {
              setFilterText(initialText);
              textChanged();
            }
          }
        });
      }
    }
    else
    {
      setFilterText(initialText);
      textChanged();
    }
  }

  /**
   * Update the receiver after the text has changed.
   */
  protected void textChanged()
  {
    // cancel currently running job first, to prevent unnecessary redraw
    refreshJob.cancel();
    refreshJob.schedule(getRefreshJobDelay());
  }

  protected abstract void updateGridData(String text);

  protected void updateToolbar(final boolean visible)
  {
    if (clearButtonControl != null)
    {
      clearButtonControl.setVisible(visible);
    }
    if (filterToolBar != null)
    {
      filterToolBar.getControl().setVisible(visible);
    }
  }

}
