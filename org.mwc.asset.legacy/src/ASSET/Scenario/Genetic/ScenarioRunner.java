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
