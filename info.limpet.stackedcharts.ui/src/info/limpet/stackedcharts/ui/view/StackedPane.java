package info.limpet.stackedcharts.ui.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;

class StackedPane extends Composite
{
  private final StackLayout _stackLayout;
  private final Map<Integer, Control> _panes = new HashMap<Integer, Control>();
  private final List<Control> _pages = new ArrayList<Control>();
  private List<SelectionListener> listeners = new ArrayList<>(1);
  private int _activePane = -1;

  public StackedPane(Composite parent)
  {
    super(parent, SWT.NO_FOCUS);
    _stackLayout = new StackLayout();
    setLayout(_stackLayout);
    _stackLayout.marginHeight = 0;
    _stackLayout.marginWidth = 0;
  }

  public StackedPane(Composite parent, int style)
  {
    super(parent, style);
    _stackLayout = new StackLayout();
    setLayout(_stackLayout);
  }

  @Override
  public StackLayout getLayout()
  {
    return (StackLayout) super.getLayout();
  }

  @Override
  public void setLayout(Layout layout)
  {
    if (!(layout instanceof StackLayout))
    {
      throw new IllegalArgumentException("Only support StackLayout");
    }
    super.setLayout(layout);
  }

  public void add(int key, Control control)
  {
    if (_stackLayout.topControl == null)
    {
      _stackLayout.topControl = control;
    }
    _panes.put(key, control);
    _pages.add(control);
  }

  public Control getControl(int key)
  {
    return _panes.get(key);
  }

  public void showPane(int pane)
  {
    showPane(pane, true);
    
  }
  public void showPane(int pane,boolean fireEvent)
  {
	 
    if(getActiveControlKey()==pane)
    {
      return;
    }
    _activePane  = pane;
    Control control = _panes.get(pane);
    control.setSize(getSize());
  
    if(fireEvent)
      fireSelection(control);
    // fix for work around on mac
    if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0 || System.getProperty("os.name")
        .toLowerCase().indexOf("nux") >= 0)
    {
      completeSelection();
    }
   
    
    
    
  }
  
  void completeSelection()
  {
    Control control = _panes.get(_activePane);

    if (control != null)
    {

      _stackLayout.topControl = control;
    }

    layout(true);

    if (control instanceof Composite)
    {
      ((Composite) control).layout(true);
    }
  }

  public void remove(int key)
  {
    Control control = _panes.get(key);
    if (control != null)
    {
      int indexOf = _pages.indexOf(control) - 1;
      _panes.remove(key);
      _pages.remove(control);

      control.dispose();
      if (indexOf > 0 && indexOf < _pages.size())
      {
        _stackLayout.topControl = _pages.get(indexOf);
        layout(true);
      }
      else if (_pages.size() > 0)
      {
        _stackLayout.topControl = _pages.get(0);
        layout(true);
      }
    }
  }

  public int getActiveControlKey()
  {
    
    return _activePane;
  }

  public void addSelectionListener(SelectionListener listener)
  {
    listeners.add(listener);
  }

  public void removeSelectionListener(SelectionListener listener)
  {
    listeners.remove(listener);
  }

  void fireSelection(Control c)
  {
    for (SelectionListener listener : new ArrayList<>(listeners))
    {
      Event e = new Event();
      e.item = c;
      e.widget = c;
      listener.widgetSelected(new SelectionEvent(e));
    }
  }

  public Control getActiveControl()
  {
    return _stackLayout.topControl;
  }
}
