package ASSET.Models.Decision;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: Feb 8, 2003
 * Time: 9:03:26 AM
 * To change this template use Options | File Templates.
 */
public interface BehaviourList extends ASSET.Models.DecisionType, MWC.GUI.Editable {
  String UPDATED = "Updated";

  void addListener(String type, java.beans.PropertyChangeListener listener);

  void removeListener(String type, java.beans.PropertyChangeListener listener);

  void insertAtHead(ASSET.Models.DecisionType decision);

  void insertAtFoot(ASSET.Models.DecisionType decision);

  /** get a description of what is happening in this behaviour
   *
   * @return
   */
  String getActivity();

  /** get the list of models contained in this sequence
   *
   * @return
   */
  Vector<ASSET.Models.DecisionType> getModels();

  /** return the class name as a string (in particular this is used by the Waterfall Editor
   * to indicate what is being edited
   *
   * @return the name
   */
  public String getBehaviourName();
}
