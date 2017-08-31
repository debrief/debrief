package org.mwc.debrief.core.editors;

import java.awt.Color;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.ui_support.wizards.SimplePageListWizard;
import org.mwc.cmap.core.wizards.EnterBooleanPage;
import org.mwc.cmap.core.wizards.EnterRangePage;
import org.mwc.cmap.core.wizards.EnterStringPage;
import org.mwc.cmap.core.wizards.SelectColorPage;

import MWC.GenericData.WorldDistance;

public interface SensorImportHelper
{

  public static class SensorImportHelperHeadless implements SensorImportHelper
  {
    
    private String _sensorName;

    public SensorImportHelperHeadless(final String sensorName)
    {
      _sensorName = sensorName;
    }

    @Override
    public boolean success()
    {
      return true;
    }

    @Override
    public String getName()
    {
      return _sensorName;
    }

    @Override
    public Color getColor()
    {
      return Color.yellow;
    }

    @Override
    public boolean getVisiblity()
    {
      return false;
    }

    @Override
    public WorldDistance getRange()
    {
      return null;
    }

    @Override
    public boolean applyRainbow()
    {
      return false;
    }

  }

  public static class SensorImportHelperUI implements SensorImportHelper
  {
    private final EnterStringPage getName;
    private final SelectColorPage getColor;
    private final EnterBooleanPage getVis;
    private final EnterRangePage getRange;
    private final EnterBooleanPage applyRainbowInRainbowColors;
    private final WizardDialog dialog;

    public SensorImportHelperUI(final String sensorName,
        final Color sensorColor, final String introString, boolean needsRange)
    {
      final String imagePath = "images/NameSensor.jpg";
      final String explain = "\nNote: you can prevent this wizard from opening using" +
      		"\nthe Debrief preference titled:\n" +
      		"'Show the wizard when importing sensor data from REP'";
      getName =
          new EnterStringPage(null, sensorName, "Import Sensor data",
              "Please provide the name for this sensor" + explain, introString, imagePath,
              null, false, explain);
      getColor =
          new SelectColorPage(null, sensorColor, "Import Sensor data",
              "Now format the new sensor",
              "The default color for the cuts for this new sensor", imagePath,
              null, null);
      getVis =
          new EnterBooleanPage(null, false, "Import Sensor data",
              "Please specify if this sensor should be displayed once loaded",
              "yes/no", imagePath, null, null);
      final WorldDistance defRange = new WorldDistance(5, WorldDistance.KYDS);
      getRange =
          new EnterRangePage(
              null,
              "Import Sensor data",
              "Please provide a default range for the sensor cuts \n(or enter 0.0 to leave them as infinite length)",
              "Default range", defRange, imagePath, null, null);
      applyRainbowInRainbowColors =
          new EnterBooleanPage(null, false,
              "Apply Rainbow Shades in rainbow colors",
              "Should Debrief apply Rainbow Shades to these sensor cuts?",
              "yes/no", "images/ShadeRainbow.png", null, null);

      // create the wizard to color/name this
      final SimplePageListWizard wizard = new SimplePageListWizard();
      wizard.addWizard(getName);
      wizard.addWizard(getColor);
      if (needsRange)
        wizard.addWizard(getRange);
      wizard.addWizard(getVis);
      wizard.addWizard(applyRainbowInRainbowColors);
      dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
      dialog.create();
      dialog.setBlockOnOpen(true);
      dialog.open();
    }

    @Override
    public boolean success()
    {
      return dialog.getReturnCode() == WizardDialog.OK;
    }

    @Override
    public String getName()
    {
      return getName.getString();
    }

    @Override
    public Color getColor()
    {
      return getColor.getColor();
    }

    @Override
    public boolean getVisiblity()
    {
      return getVis.getBoolean();
    }

    @Override
    public WorldDistance getRange()
    {
      return getRange.getRange();
    }

    @Override
    public boolean applyRainbow()
    {
      return applyRainbowInRainbowColors.getBoolean();
    }
  }

  boolean success();

  String getName();

  Color getColor();

  boolean getVisiblity();

  WorldDistance getRange();

  boolean applyRainbow();

}