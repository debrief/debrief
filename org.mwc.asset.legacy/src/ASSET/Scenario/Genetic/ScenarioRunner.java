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
package ASSET.Scenario.Genetic;

/** interface which defines an object which can run through a scenario
 *
 */
public interface ScenarioRunner
{
  /** run through the supplied scenario, returning the fitness score
   * from the referees/observers
   */
  public ScenarioOutcome runThis(String scenario, String name, String desc);

  /** embedded class to represent the outcome of running a scenario
   *
   */
  public static class ScenarioOutcome
  {
    public final static double INVALID_SCORE = -1;

    /** the score for this scenario
     *
     */
    public double score = INVALID_SCORE;

    /** a textual description of the outcome of this scenario
     *
     */
    public String summary = "";
  }

}
