/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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


    /**
     * Provide a list of id numbers of Participant we contain
     *
     * @return list of ids of Participant we contain
     */
    public Integer[] getListOfParticipants();


}
