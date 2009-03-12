/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 16-Sep-02
 * Time: 11:29:20
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Scenario;

import MWC.GenericData.WorldLocation;
import ASSET.ParticipantType;

/** class used to represent an object which listens out for significant activity within a
 * scenario, typically weapon detonations (see {@link ASSET.Models.Decision.Tactical.Detonate detonate}) and creation of
 * new participants ({@link ASSET.Models.Decision.Tactical.LaunchWeapon firing a weapon}) or
 * ({@link ASSET.Models.Decision.Tactical.LaunchSensor dropping a sonar buoy}) triggers this).
 */

public interface ScenarioActivityMonitor
{
    /** the detonation event itself
     * @param id the id of the weapon which exploded (or INVALID_ID)
     * @param loc the location of the detonation
     * @param power the strength of the detonation
     */
    public void detonationAt(int id, WorldLocation loc, double power);

    /** method to add a new participant
     * @param newPart the new participant
     *
     */
    public void createParticipant(ASSET.ParticipantType newPart);

    /** method to get a participant
     * @param id the id of the participant
     * @return the participant we're looking for
     *
     */
    public  ParticipantType getThisParticipant(final int id);


}
