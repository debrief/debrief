package MWC.GUI;

import java.awt.Font;

public class Defaults
{
  public static interface FontProvider
  {
    public Font getDefaultFont();
  }

  private static FontProvider _provider;

  public static void setProvider(FontProvider provider)
  {
    _provider = provider;
  }

  public static Font getFont()
  {
    final Font pref = _provider != null ? _provider.getDefaultFont() : null;
    final Font res = pref != null ? pref : new Font("Arial",
        Font.PLAIN, 10);
      
    return res;
  }
}
