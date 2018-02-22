package MWC.GUI;

import java.awt.Font;

public class Defaults
{
  public static interface FontProvider
  {
    public Font getDefaultFont();
    
    public String getPreference(final String name);
  }
  
  public static final String SENSOR_TRANSPARENCY = "SensorTransparency";

  private static FontProvider _provider;

  public static void setProvider(FontProvider provider)
  {
    _provider = provider;
  }

  public static Font getScaledFont(final float scale)
  {
    return getFont().deriveFont(getFont().getSize() * scale);
  }
  
  public static Font getFont()
  {
    final Font pref = _provider != null ? _provider.getDefaultFont() : null;
    final Font res = pref != null ? pref : new Font("Arial",
        Font.PLAIN, 10);
      
    return res;
  }
  
  public static String getPreference(String prefName)
  {
    final String res = _provider != null ? _provider.getPreference(prefName) : null;
    return res;
  }
}
