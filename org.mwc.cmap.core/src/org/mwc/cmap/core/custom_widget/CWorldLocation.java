/**
 * 
 */
package org.mwc.cmap.core.custom_widget;

import java.text.ParseException;

import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.MaskFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.layout.RowLayout;
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

  public CWorldLocation(Composite parent, int style)
  {
    super(parent, style);
    final Composite panel = new Composite(parent, SWT.NONE);
    final RowLayout rows = new RowLayout();
    rows.marginLeft = rows.marginRight = 0;
    rows.marginTop = rows.marginBottom = 0;
    rows.fill = false;
    rows.spacing = 0;
    rows.pack = false;
    panel.setLayout(rows);

    myLatitude = new FormattedText(panel, SWT.BORDER);
    myLongitude = new FormattedText(panel, SWT.BORDER);

    myLatitude.setFormatter(new IgnoreTabsMaskFormatter(getFormat().getNebulaPattern(false)));
    myLongitude.setFormatter(new IgnoreTabsMaskFormatter(getFormat().getNebulaPattern(true)));

    /*final KeyAdapter enterEscapeKeyHandler = new KeyAdapter() {

      @Override
      public void keyPressed(final KeyEvent e) {
        //Text cell editor hooks keyPressed to call keyReleased as well
        CWorldLocation.this.keyReleaseOccured(e);
      }
    };*/

    final TraverseListener enterEscapeTraverseHandler = new TraverseListener() {

      public void keyTraversed(final TraverseEvent e) {
        if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
          e.doit = false;
          return;
        }
      }
    };
    myLatitude.getControl().addTraverseListener(enterEscapeTraverseHandler);
    myLongitude.getControl().addTraverseListener(enterEscapeTraverseHandler);
//    myLatitude.getControl().addKeyListener(enterEscapeKeyHandler);
//    myLongitude.getControl().addKeyListener(enterEscapeKeyHandler);

    /*new MultiControlFocusHandler(myLatitude.getControl(), myLongitude.getControl()) {

      @Override
      protected void focusReallyLost(final FocusEvent e) {
        CWorldLocation.this.focusLost();
      }
    };*/

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
