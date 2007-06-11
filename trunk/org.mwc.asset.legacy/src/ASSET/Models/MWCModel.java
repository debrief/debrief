package ASSET.Models;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: May 16, 2003
 * Time: 1:50:13 PM
 * Interface implemented by an algorithm modelling a real-world system.
 */
public interface MWCModel
{
  /** get the version details for this model.
   * <pre>
   * $Log: MWCModel.java,v $
   * Revision 1.1  2006/08/08 14:22:03  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:26:12  Ian.Mayo
   * First versions
   *
   * Revision 1.3  2004/05/24 16:04:03  Ian.Mayo
   * Commit updates from home
   *
   * Revision 1.1.1.1  2004/03/04 20:30:51  ian
   * no message
   *
   * Revision 1.2  2003/11/07 14:40:31  Ian.Mayo
   * Include templated javadoc
   *
   *
   * </pre>
   */
  public String getVersion();

}
