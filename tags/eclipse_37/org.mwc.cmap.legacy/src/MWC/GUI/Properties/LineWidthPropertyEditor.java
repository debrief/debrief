/*
 * Created by Ian Mayo, PlanetMayo Ltd.
 * User: Ian.Mayo
 * Date: 24-Oct-2002
 * Time: 16:08:42
 */
package MWC.GUI.Properties;
public class LineWidthPropertyEditor extends AbstractPropertyEditor
{
  ////////////////////////////////////////////////////
  // member objects
  ////////////////////////////////////////////////////
  private String _stringTags[] =
  {
                     "Hairwidth",
                     "1 pixels",
                     "2 pixels",
                     "3 pixels",
                     "4 pixels",
                     "5 pixels",
  };

  ////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////
  public String[] getTags()
  {
    return _stringTags;
  }



}
