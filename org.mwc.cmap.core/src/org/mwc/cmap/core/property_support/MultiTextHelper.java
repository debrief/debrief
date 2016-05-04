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
package org.mwc.cmap.core.property_support;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MultiTextHelper extends EditorHelper
{

  public MultiTextHelper()
  {
    super(String.class);
  }

  @Override
  public CellEditor getCellEditorFor(Composite parent)
  {
    return new MultiTextCellEditor(parent);
  }
  
 

  static class MultiText extends Composite
  {

    Text text;
    Button button;
    boolean hasFocus;
    Listener listener, filter;
    Shell _shell;
    boolean dialogModel;
    Color foreground, background;
    Font font;

    public MultiText(Composite parent, int style)
    {
      super(parent, checkStyle(style));
      dialogModel = false;
      _shell = super.getShell();

      text = new Text(this, style);
      button = new Button(this, SWT.DOWN);
      button.setText("..."); //$NON-NLS-1$

      filter = new Listener()
      {
        public void handleEvent(Event event)
        {
          if (isDisposed())
            return;
          Shell shell = ((Control) event.widget).getShell();
          if (shell == MultiText.this.getShell())
          {
            handleFocus(SWT.FocusOut);
          }
        }
      };

      listener = new Listener()
      {
        public void handleEvent(Event event)
        {
          if (isDisposed())
            return;
          if (text == event.widget)
          {
            textEvent(event);
            return;
          }
          if (button == event.widget)
          {
            buttonEvent(event);
            return;
          }
          if (MultiText.this == event.widget)
          {
            customTextEvent(event);
            return;
          }
          if (getShell() == event.widget)
          {
            getDisplay().asyncExec(new Runnable()
            {
              public void run()
              {
                if (isDisposed())
                  return;
                handleFocus(SWT.FocusOut);
              }
            });
          }
        }
      };

      int[] customTextEvents =
      {SWT.Dispose, SWT.FocusIn, SWT.Resize};
      for (int i = 0; i < customTextEvents.length; i++)
        this.addListener(customTextEvents[i], listener);

      int[] textEvents =
      {SWT.FocusIn};
      for (int i = 0; i < textEvents.length; i++)
        text.addListener(textEvents[i], listener);

      int[] buttonEvents =
      {SWT.Selection, SWT.FocusIn};
      for (int i = 0; i < buttonEvents.length; i++)
        button.addListener(buttonEvents[i], listener);

    }

    void textEvent(Event event)
    {
      switch (event.type)
      {
      case SWT.FocusIn:
      {
        handleFocus(SWT.FocusIn);
        break;
      }

      default:
        notifyListeners(event.type, event);
        break;
      }
    }

    void buttonEvent(Event event)
    {
      switch (event.type)
      {
      case SWT.FocusIn:
      {
        handleFocus(SWT.FocusIn);
        break;
      }

      case SWT.Selection:
        dialogModel = true;
        MultiLineInputDialog dialog =
            new MultiLineInputDialog(text.getShell(), text.getText());
        dialog.open();
        dialogModel = false;
        text.setText(dialog.mContent);
        text.setFocus();
        break;

      default:
        notifyListeners(event.type, event);
        break;
      }
    }

    void customTextEvent(Event event)
    {
      switch (event.type)
      {
      case SWT.Dispose:
        removeListener(SWT.Dispose, listener);
        notifyListeners(SWT.Dispose, event);
        event.type = SWT.None;

        Shell shell = getShell();
        shell.removeListener(SWT.Deactivate, listener);
        Display display = getDisplay();
        display.removeFilter(SWT.FocusIn, filter);
        text = null;
        button = null;
        _shell = null;
        break;
      case SWT.FocusIn:
        Control focusControl = getDisplay().getFocusControl();
        if (focusControl == button)
          return;
        text.setFocus();
        break;
      case SWT.Resize:
        internalLayout(false);
        break;
      }
    }

    void internalLayout(boolean changed)
    {
      Rectangle rect = getClientArea();
      int width = rect.width;
      int height = rect.height;
      Point arrowSize = button.computeSize(SWT.DEFAULT, height, changed);
      text.setBounds(0, 0, width - arrowSize.x, height);
      button.setBounds(width - arrowSize.x, 0, arrowSize.x, arrowSize.y);
    }

    void handleFocus(int type)
    {
      switch (type)
      {
      case SWT.FocusIn:
      {
        if (hasFocus)
          return;
        if (getEditable())
          text.selectAll();
        hasFocus = true;
        Shell shell = getShell();
        shell.removeListener(SWT.Deactivate, listener);
        shell.addListener(SWT.Deactivate, listener);
        Display display = getDisplay();
        display.removeFilter(SWT.FocusIn, filter);
        display.addFilter(SWT.FocusIn, filter);
        Event e = new Event();
        notifyListeners(SWT.FocusIn, e);
        break;
      }
      case SWT.FocusOut:
      {
        if (dialogModel)
          return;
        if (!hasFocus)
          return;
        Control focusControl = getDisplay().getFocusControl();
        if (focusControl == button || focusControl == text)
          return;
        hasFocus = false;
        Shell shell = getShell();
        shell.removeListener(SWT.Deactivate, listener);
        Display display = getDisplay();
        display.removeFilter(SWT.FocusIn, filter);
        Event e = new Event();
        notifyListeners(SWT.FocusOut, e);
        break;
      }
      }
    }

    public Shell getShell()
    {
      checkWidget();
      Shell shell = super.getShell();
      if (shell != _shell)
      {
        if (_shell != null && !_shell.isDisposed())
        {
          _shell.removeListener(SWT.Deactivate, listener);
        }
        _shell = shell;
      }
      return _shell;
    }

    public boolean getEditable()
    {
      checkWidget();
      return text.getEditable();
    }

    static int checkStyle(int style)
    {
      int mask =
          SWT.BORDER | SWT.READ_ONLY | SWT.FLAT | SWT.LEFT_TO_RIGHT
              | SWT.RIGHT_TO_LEFT;
      return SWT.NO_FOCUS | (style & mask);
    }

    public void setEditable(boolean editable)
    {
      checkWidget();
      text.setEditable(editable);
    }

    public void setEnabled(boolean enabled)
    {
      super.setEnabled(enabled);
      if (text != null)
        text.setEnabled(enabled);
      if (button != null)
        button.setEnabled(enabled);
    }

    public boolean setFocus()
    {
      checkWidget();
      if (!isEnabled() || !getVisible())
        return false;
      if (isFocusControl())
        return true;
      return text.setFocus();
    }

    public void setFont(Font font)
    {
      super.setFont(font);
      this.font = font;
      text.setFont(font);
      internalLayout(true);
    }

    public void setForeground(Color color)
    {
      super.setForeground(color);
      foreground = color;
      if (text != null)
        text.setForeground(color);
      if (button != null)
        button.setForeground(color);
    }

    
    public void setLayout(Layout layout)
    {
      checkWidget();
      return;
    }

    public void setMenu(Menu menu)
    {
      text.setMenu(menu);
    }

   
    public void setSelection(Point selection)
    {
      checkWidget();
      if (selection == null)
        SWT.error(SWT.ERROR_NULL_ARGUMENT);
      text.setSelection(selection.x, selection.y);
    }

    
    public void setText(String string)
    {
      checkWidget();
      if (string == null)
        SWT.error(SWT.ERROR_NULL_ARGUMENT);
      text.setText(string);
    }

    
    public void setTextLimit(int limit)
    {
      checkWidget();
      text.setTextLimit(limit);
    }

    public void setToolTipText(String string)
    {
      checkWidget();
      super.setToolTipText(string);
      button.setToolTipText(string);
      text.setToolTipText(string);
    }

    public int getSelectionCount()
    {
      return text.getSelectionCount();
    }

    public void addSelectionListener(SelectionAdapter selectionAdapter)
    {
      text.addSelectionListener(selectionAdapter);
    }

    public void addModifyListener(ModifyListener modifyListener)
    {
      text.addModifyListener(modifyListener);
    }

    public String getText()
    {
      return text.getText();
    }

    public void selectAll()
    {
      text.selectAll();
    }

    public void removeModifyListener(ModifyListener modifyListener)
    {
      text.removeModifyListener(modifyListener);
    }

    public int getCharCount()
    {
      return text.getCharCount();
    }

    public int getCaretPosition()
    {
      return text.getCaretPosition();
    }

    public void copy()
    {
      text.copy();
    }

    public void cut()
    {
      text.cut();
    }

    public void insert(String string)
    {
      text.insert(string);
    }

    public void setSelection(int pos, int i)
    {
      text.setSelection(pos, i);
    }

    public void paste()
    {
      text.paste();
    }

  }

  class MultiTextCellEditor extends CellEditor
  {

    private MultiText text;

    private ModifyListener modifyListener;

    private boolean isSelection = false;

    private boolean isDeleteable = false;

    private boolean isSelectable = false;

    private static final int defaultStyle = SWT.SINGLE;

    public MultiTextCellEditor()
    {
      setStyle(defaultStyle);
    }

    public MultiTextCellEditor(Composite parent)
    {
      this(parent, defaultStyle);
    }

    public MultiTextCellEditor(Composite parent, int style)
    {
      super(parent, style);
    }

    private void checkDeleteable()
    {
      boolean oldIsDeleteable = isDeleteable;
      isDeleteable = isDeleteEnabled();
      if (oldIsDeleteable != isDeleteable)
      {
        fireEnablementChanged(DELETE);
      }
    }

    private void checkSelectable()
    {
      boolean oldIsSelectable = isSelectable;
      isSelectable = isSelectAllEnabled();
      if (oldIsSelectable != isSelectable)
      {
        fireEnablementChanged(SELECT_ALL);
      }
    }

    private void checkSelection()
    {
      boolean oldIsSelection = isSelection;
      isSelection = text.getSelectionCount() > 0;
      if (oldIsSelection != isSelection)
      {
        fireEnablementChanged(COPY);
        fireEnablementChanged(CUT);
      }
    }

    protected Control createControl(Composite parent)
    {
      text = new MultiText(parent, getStyle()|SWT.MULTI|SWT.V_SCROLL);
      text.addSelectionListener(new SelectionAdapter()
      {
        public void widgetDefaultSelected(SelectionEvent e)
        {
          handleDefaultSelection(e);
        }
      });
      text.addKeyListener(new KeyAdapter()
      {

        public void keyPressed(KeyEvent e)
        {
          keyReleaseOccured(e);

          if ((getControl() == null) || getControl().isDisposed())
          {
            return;
          }
          checkSelection();
          checkDeleteable();
          checkSelectable();
        }
      });
      text.addTraverseListener(new TraverseListener()
      {
        public void keyTraversed(TraverseEvent e)
        {
          if (e.detail == SWT.TRAVERSE_ESCAPE
              || e.detail == SWT.TRAVERSE_RETURN)
          {
            e.doit = false;
          }
        }
      });

      text.addMouseListener(new MouseAdapter()
      {
        public void mouseUp(MouseEvent e)
        {
          checkSelection();
          checkDeleteable();
          checkSelectable();
        }
      });
      text.addFocusListener(new FocusAdapter()
      {
        public void focusLost(FocusEvent e)
        {
          MultiTextCellEditor.this.focusLost();
        }
      });
      text.setFont(parent.getFont());
      text.setBackground(parent.getBackground());
      text.setText("");//$NON-NLS-1$
      text.addModifyListener(getModifyListener());
      return text;
    }

    protected Object doGetValue()
    {
      return text.getText();
    }

    /*
     * (non-Javadoc) Method declared on CellEditor.
     */
    protected void doSetFocus()
    {
      if (text != null)
      {
        text.selectAll();
        text.setFocus();
        checkSelection();
        checkDeleteable();
        checkSelectable();
      }
    }

    protected void doSetValue(Object value)
    {
      assert (text != null && (value instanceof String));
      text.removeModifyListener(getModifyListener());
      text.setText((String) value);
      text.addModifyListener(getModifyListener());
    }

    protected void editOccured(ModifyEvent e)
    {
      String value = text.getText();
      if (value == null)
      {
        value = "";//$NON-NLS-1$
      }
      Object typedValue = value;
      boolean oldValidState = isValueValid();
      boolean newValidState = isCorrect(typedValue);
      if (!newValidState)
      {
        // try to insert the current value into the error message.
        setErrorMessage(MessageFormat.format(getErrorMessage(), new Object[]
        {value}));
      }
      valueChanged(oldValidState, newValidState);
    }

    public LayoutData getLayoutData()
    {
      LayoutData data = new LayoutData();
      data.minimumWidth = 0;
      return data;
    }

    private ModifyListener getModifyListener()
    {
      if (modifyListener == null)
      {
        modifyListener = new ModifyListener()
        {
          public void modifyText(ModifyEvent e)
          {
            editOccured(e);
          }
        };
      }
      return modifyListener;
    }

    protected void handleDefaultSelection(SelectionEvent event)
    {
      // same with enter-key handling code in keyReleaseOccured(e);
      fireApplyEditorValue();
      deactivate();
    }

    public boolean isCopyEnabled()
    {
      if (text == null || text.isDisposed())
      {
        return false;
      }
      return text.getSelectionCount() > 0;
    }

    public boolean isCutEnabled()
    {
      if (text == null || text.isDisposed())
      {
        return false;
      }
      return text.getSelectionCount() > 0;
    }

    public boolean isDeleteEnabled()
    {
      if (text == null || text.isDisposed())
      {
        return false;
      }
      return text.getSelectionCount() > 0
          || text.getCaretPosition() < text.getCharCount();
    }

    public boolean isPasteEnabled()
    {
      if (text == null || text.isDisposed())
      {
        return false;
      }
      return true;
    }

    public boolean isSaveAllEnabled()
    {
      if (text == null || text.isDisposed())
      {
        return false;
      }
      return true;
    }

    public boolean isSelectAllEnabled()
    {
      if (text == null || text.isDisposed())
      {
        return false;
      }
      return text.getCharCount() > 0;
    }

    protected void keyReleaseOccured(KeyEvent keyEvent)
    {
      if (keyEvent.character == '\r')
      { // Return key

        if (text != null && !text.isDisposed()
            && (text.getStyle() & SWT.MULTI) != 0)
        {
          if ((keyEvent.stateMask & SWT.CTRL) != 0)
          {
            super.keyReleaseOccured(keyEvent);
          }
        }
        return;
      }
      super.keyReleaseOccured(keyEvent);
    }

    public void performCopy()
    {
      text.copy();
    }

    public void performCut()
    {
      text.cut();
      checkSelection();
      checkDeleteable();
      checkSelectable();
    }

    public void performDelete()
    {
      if (text.getSelectionCount() > 0)
      {
        // remove the contents of the current selection
        text.insert(""); //$NON-NLS-1$
      }
      else
      {
        // remove the next character
        int pos = text.getCaretPosition();
        if (pos < text.getCharCount())
        {
          text.setSelection(pos, pos + 1);
          text.insert(""); //$NON-NLS-1$
        }
      }
      checkSelection();
      checkDeleteable();
      checkSelectable();
    }

    public void performPaste()
    {
      text.paste();
      checkSelection();
      checkDeleteable();
      checkSelectable();
    }

    public void performSelectAll()
    {
      text.selectAll();
      checkSelection();
      checkDeleteable();
    }

    protected boolean dependsOnExternalFocusListener()
    {
      return getClass() != MultiTextCellEditor.class;
    }
  }

  static class MultiLineInputDialog extends Dialog
  {

    public Text mContentText;
    public String mContent;

    public MultiLineInputDialog(Shell parentShell, String content)
    {
      super(parentShell);
      this.mContent = content;
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
      Composite container = (Composite) super.createDialogArea(parent);
      container.setLayout(new FillLayout(SWT.HORIZONTAL));

      mContentText = new Text(container, SWT.BORDER | SWT.WRAP | SWT.MULTI);
      mContentText.setText(mContent);

      return container;
    }

    @Override
    protected void buttonPressed(int buttonId)
    {
      if (buttonId == IDialogConstants.OK_ID)
      {
        this.mContent = mContentText.getText();
      }
      super.buttonPressed(buttonId);
    }

    @Override
    protected Point getInitialSize()
    {
      return new Point(600, 300);
    }
  }

}
