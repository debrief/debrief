/**
 * 
 */
package org.mwc.cmap.core.custom_widget;

import java.text.ParseException;
import java.util.Vector;

import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.MaskFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.gridharness.data.base60.Sexagesimal;
import org.mwc.cmap.gridharness.data.base60.SexagesimalFormat;

import MWC.GenericData.WorldLocation;

/**
 * This is a custom composite to specify the world location
 * in lat and long
 * @author Ayesha
 *
 */
public class CWorldLocation extends Composite
{
  private FormattedText myLatitude;

  private FormattedText myLongitude;
  
  private Vector<LocationModifiedListener> _locationModifiedListeners = new Vector<>();

  public CWorldLocation(Composite parent, int style)
  {
    super(parent, style);
    final GridLayout rows = new GridLayout(2,true);
    rows.marginLeft = rows.marginRight = 0;
    rows.marginTop = rows.marginBottom = 0;
    setLayout(rows);
    GridData gd = new GridData();
    gd.heightHint=40;
    setLayoutData(gd);
    myLatitude = new FormattedText(this, SWT.BORDER);
    myLongitude = new FormattedText(this, SWT.BORDER);
    GridData data1 = new GridData();
    data1.widthHint = 90;
    myLatitude.getControl().setLayoutData(data1);
    GridData data2 = new GridData();    
    data2.widthHint = 100;
    myLongitude.getControl().setLayoutData(data2);
    myLatitude.setFormatter(new IgnoreTabsMaskFormatter(getFormat().getNebulaPattern(false)));
    myLongitude.setFormatter(new IgnoreTabsMaskFormatter(getFormat().getNebulaPattern(true)));

    myLatitude.getControl().addModifyListener(new ModifyListener()
    {
      @Override
      public void modifyText(ModifyEvent event)
      {
        valueModified(event.getSource());
      }
    });
    myLongitude.getControl().addModifyListener(new ModifyListener()
    {
      @Override
      public void modifyText(ModifyEvent event)
      {
        valueModified(event.getSource());
      }
    });
    

  }
  
  public void addLocationModifiedListener(LocationModifiedListener listener) {
    _locationModifiedListeners.add(listener);
  }
 
  
  private void valueModified(final Object source)
  {
    LocationModifiedEvent event = new LocationModifiedEvent(source,myLatitude.getValue(),myLongitude.getValue());
    for(LocationModifiedListener listener:_locationModifiedListeners) {
      listener.modifyValue(event);
    }
  }
  

  private SexagesimalFormat getFormat() {
    //intentionally reevaluated each time
    return CorePlugin.getDefault().getLocationFormat();
  }
  
  private static class IgnoreTabsMaskFormatter extends MaskFormatter {

    public IgnoreTabsMaskFormatter(final String mask) {
      super(mask);
    }

    @Override
    public void verifyText(final VerifyEvent e) {
      if (ignore) {
        return;
      }
      if (e.keyCode == SWT.TAB) {
        e.doit = false;
        return;
      }
      super.verifyText(e);
    }
  }
  public WorldLocation getValue() {
    if (!myLatitude.isValid() || !myLongitude.isValid()) {
      return null;
    }
    final String latitudeString = (String) myLatitude.getFormatter().getDisplayString();
    final String longitudeString = (String) myLongitude.getFormatter().getDisplayString();

    Sexagesimal latitude;
    Sexagesimal longitude;
    try {
      latitude = getFormat().parse(latitudeString, false);
      longitude = getFormat().parse(longitudeString, true);
    } catch (final ParseException e) {
      //thats ok, formatter does not know the hemisphere characters
      return null;
    }

    final WorldLocation location = new WorldLocation(latitude.getCombinedDegrees(), longitude.getCombinedDegrees(),0);
    return location;
  }

  public void setValue(final WorldLocation location) {
    final Sexagesimal latitude = getFormat().parseDouble(location.getLat());
    final Sexagesimal longitude = getFormat().parseDouble(location.getLong());

    myLatitude.setValue(getFormat().format(latitude, false));
    myLongitude.setValue(getFormat().format(longitude, true));
  }
}
