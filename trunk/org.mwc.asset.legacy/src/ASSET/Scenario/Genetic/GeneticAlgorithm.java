package ASSET.Scenario.Genetic;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import java.util.*;
import java.io.InputStream;

import ASSET.Util.MonteCarlo.XMLVariance;

public class GeneticAlgorithm
{

  /***************************************************************
   *  interfaces for listeners who wish to know as we pass through stages
   ***************************************************************/
  public interface GAProgressed
  {
    /** we have generated a fresh population
     *
     */
    public void generated();

    /** our population has been sorted
     *
     */
    public void sorted();

    /** our population has mutated
     *
     */
    public void mutated();

    /** our population has grown
     *
     */
    public void stepCompleted();

    /** our star performers have been promoted
     *
     */
    public void promoted();

    /** we have retired the losers
     *
     */
    public void retired();
  }

  /** interfaces for listeners who wish to know as we work through each scenario
   *
   */
  public interface GAStepped
  {
    /** a gene has developed
     *
     */
    public void stepped();
  }


  /***************************************************************
   *  member variables
   ***************************************************************/

  /** current list of genes
   *
   */
  private SortedSet _theseGenes;

  /** current list of top performers
   *
   */
  private SortedSet _starGenes;

  /** the size of each population
   *
   */
  private int _population_size;

  /** the size of the set of stars
   *
   */
  private int _stars_size;

  /** the Gene we base ourselves upon
   *
   */
  private Gene _baseGene;

  /** listeners for use moving through ga
   *
   */
  private Vector _GAListeners = new Vector(0, 1);

  /** listeners for stepping through population
   *
   */
  private Vector _stepListeners = new Vector(0, 1);

  /** the object which performs the run for us
   *
   */
  private ScenarioRunner _myRunner = null;

  /** a counter to keep track of how many scenarios have been run through
   *
   */
  private int _counter = 0;

  /** TEMPORARY working object which lets us get the gene currently running
   *
   */
  private static Gene _currentGene = null;

  /***************************************************************
   *  constructor
   ***************************************************************/
  /** constructor for our genetic algorithm
   *  @param population_size the number of genes in the population
   *  @param stars_size the number of genes we retain in high scores
   */
  public GeneticAlgorithm(final int population_size, final int stars_size)
  {
    _population_size = population_size;
    _stars_size = stars_size;

    _theseGenes = new TreeSet();
    _starGenes = new TreeSet();

  }

  /***************************************************************
   *  member variables
   ***************************************************************/

  /** set the object which performs the run for us
   *
   */
  public void setRunner(final ScenarioRunner runner)
  {
    _myRunner = runner;
  }

  /** set the data streams necessary to create the gene
   *
   */
  public void createGene(final InputStream inputDoc, final InputStream inputVary)
  {
//    _baseGene = new Gene(inputDoc, inputVary);
    // todo: reinstate this
  }


  /** add/remove a step listener
   *
   */
  public void addStepListener(final GAStepped listener)
  {
    _stepListeners.add(listener);
  }

  /** add/remove a step listener
   *
   */
  public void removeStepListener(final GAStepped listener)
  {
    _stepListeners.remove(listener);
  }

  /** add/remove a progress listener
   *
   */
  public void addGAProgressListener(final GAProgressed listener)
  {
    _GAListeners.add(listener);
  }

  /** add/remove a progress listener
   *
   */
  public void removeGAProgressListener(final GAProgressed listener)
  {
    _GAListeners.remove(listener);
  }

  public void setGene(final Gene myGene)
  {
    _baseGene = myGene;
  }

  public Gene getGene()
  {
    return _baseGene;
  }

  public void generate()
  {
    // clear the waiting items list
    _theseGenes.clear();

    // build up list of genes in waiting list
    for (int i = 0; i < _population_size; i++)
    {
      final Gene newG = _baseGene.createRandom();

      _theseGenes.add(newG);
    }

    // inform the listeners
    final Iterator it = _GAListeners.iterator();
    while (it.hasNext())
    {
      final GAProgressed progressed = (GAProgressed) it.next();
      progressed.generated();
    }
  }

  /** mutate using our star performers
   *
   */
  public void mutate()
  {
    // do we have any stars?
    if (_starGenes.size() > 0)
    {
      // we now merge the random genes with our stars
      final double percentage_step = 0.3d / _starGenes.size();
      double this_prob = percentage_step;
      final Iterator it = _starGenes.iterator();
      while (it.hasNext())
      {
        final Gene nextStar = (Gene) it.next();
        // pass through the new genes, and mate with this one according to the %age
        mateWithThis(nextStar, this_prob);
        this_prob += percentage_step;
      }
    }

    // inform the listeners
    final Iterator it = _GAListeners.iterator();
    while (it.hasNext())
    {
      final GAProgressed progressed = (GAProgressed) it.next();
      progressed.mutated();
    }

  }

  private void mateWithThis(final Gene star, final double prob)
  {
    final Iterator it = _theseGenes.iterator();

    while (it.hasNext())
    {
      final Gene newG = (Gene) it.next();
      newG.mergeWith(star, prob);

    }
  }

  /** Move the supplied scenario forward one step, returning it's fitness figure
   * @return the fitness of this scenario
   */
  private ScenarioRunner.ScenarioOutcome stepThis(final String scenario, final String name, final String desc)
  {
    final ScenarioRunner.ScenarioOutcome res = _myRunner.runThis(scenario, name, desc);
    return res;
  }

  /** move all of the scenarios forward, calculate the fitnesses
   *
   */
  public void step(boolean lowScoresHigh)
  {
    /***************************************************************
     *  step through the genes
     ***************************************************************/
    Iterator it = _theseGenes.iterator();
    while (it.hasNext())
    {
      final Gene thisG = (Gene) it.next();

      _currentGene = thisG;

      /***************************************************************
       *  get the scenario
       ***************************************************************/

      // do a simulation using this combination
      final String thisScenario = thisG.getDocument();

      // create a new name
      final String name = "" + _counter++;

      // name the gene
      thisG.setName(name);

      /***************************************************************
       *  get the fitness
       ***************************************************************/


      final ScenarioRunner.ScenarioOutcome fitness = stepThis(thisScenario, name, thisG.toString());

      // reverse the score, if we running in reverse
      if(lowScoresHigh && (fitness.score != ScenarioRunner.ScenarioOutcome.INVALID_SCORE))
       fitness.score = -fitness.score;

      thisG.setFitness(fitness.score, fitness.summary);

      /***************************************************************
       *  inform the listeners
       ***************************************************************/
      // inform the listeners
      final Iterator it2 = _stepListeners.iterator();
      while (it2.hasNext())
      {
        final GAStepped progressed = (GAStepped) it2.next();
        progressed.stepped();
      }


    }

    /***************************************************************
     *  inform the listeners that the whole cycle is complete
     ***************************************************************/

    // inform the listeners
    it = _GAListeners.iterator();
    while (it.hasNext())
    {
      final GAProgressed progressed = (GAProgressed) it.next();
      progressed.stepCompleted();
    }


  }

  public void sort()
  {

    // create a blank list
    TreeSet ts = new TreeSet();

    // add our exiting genes to it, to sort it
    Iterator it = _theseGenes.iterator();
    while (it.hasNext())
    {
      final Gene gene = (Gene) it.next();

      // did this succeed?
      if (gene.getFitness() != ScenarioRunner.ScenarioOutcome.INVALID_SCORE)
        ts.add(gene);
    }

    // and store as our own generation
    _theseGenes = ts;


    // inform the listeners
    it = _GAListeners.iterator();
    while (it.hasNext())
    {
      final GAProgressed progressed = (GAProgressed) it.next();
      progressed.sorted();
    }
  }

  public void promote()
  {
    // compare the top performers with the one at the bottom of the stars list

    // do we have a list of stars?
    if (_starGenes.size() == 0)
    {
      // just create it afresh!
      _starGenes.addAll(_theseGenes);
    }
    else
    {
      // get the poorest of our stars
      final Object lowest = _starGenes.first();

      // get the list of the finished genes which are equal to or greater than
      // the lowest of our start
      final SortedSet newStars = _theseGenes.tailSet(lowest);

      // add this list to our stars
      _starGenes.addAll(newStars);

    }

    // sort the star genes
    final Object[] list = _starGenes.toArray();
    Arrays.sort(list,
                new Comparator()
                {
                  public int compare(final Object o1, final Object o2)
                  {
                    final Gene na = (Gene) o1;
                    return 1 - na.compareTo(o2);
                  }
                });

    // finally trim it down to the desired size
    final SortedSet ss = new TreeSet();
    final Iterator it2 = _starGenes.iterator();
    for (int ii = 0; ii < _stars_size; ii++)
    {
      ss.add(list[ii]);
    }

    _starGenes = ss;



    // inform the listeners
    final Iterator it = _GAListeners.iterator();
    while (it.hasNext())
    {
      final GAProgressed progressed = (GAProgressed) it.next();
      progressed.promoted();
    }

  }

  public void retire()
  {
    // ditch all of the genes from the completed list
    _theseGenes.clear();


    // inform the listeners
    final Iterator it = _GAListeners.iterator();
    while (it.hasNext())
    {
      final GAProgressed progressed = (GAProgressed) it.next();
      progressed.retired();
    }

  }

  /***************************************************************
   *  accessor methods
   ***************************************************************/
  public Collection getStarGenes()
  {
    return _starGenes;
  }

  public Collection getGenes()
  {
    return _theseGenes;
  }



  /***************************************************************
   *  embedded class to run a scenario
   ***************************************************************/

  /** basic interface of scenario runner, which just returns a random number
   *
   */
  static public class RandomRunner implements ScenarioRunner
  {
    public ScenarioRunner.ScenarioOutcome runThis(String scenario, String name, String desc)
    {
      ScenarioRunner.ScenarioOutcome res = new ScenarioRunner.ScenarioOutcome();
      res.score = ASSET.Util.RandomGenerator.nextRandom() * 100;
      return res;
    }
  }

  /** basic interface of scenario runner, which just returns a random number
   *
   */
  static public class TotalRunner implements ScenarioRunner
  {
    public ScenarioRunner.ScenarioOutcome runThis(String scenario, String name, String desc)
    {
      // get the product of the values in the genes
      ScenarioRunner.ScenarioOutcome res = new ScenarioRunner.ScenarioOutcome();

      final Iterator it = _currentGene._myList.getIterator();
      while (it.hasNext())
      {
        final XMLVariance variable = (XMLVariance) it.next();
        final String curVal = variable.getValue();
        final double thisV = Double.valueOf(curVal).doubleValue();
        res.score *= thisV;
      }


      return res;
    }
  }

  //////////////////////////////////////////////////////////////////////
  // comparator code
  //////////////////////////////////////////////////////////////////////
  private class compareGenes implements Comparator
  {

    public int compare(final Object o1, final Object o2)
    {
      int res = 0;
      final double fit1 = ((Gene) o1).getFitness();
      final double fit2 = ((Gene) o2).getFitness();
      if (fit1 == fit2)
        res = 0;
      else if (fit1 > fit2)
        res = 1;
      else
        res = -1;

      return res;
    }
  }

  //////////////////////////////////////////////////////////////////////
  // testing code
  //////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
//  public static class dtestGA extends junit.framework.TestCase
//  {
//    // todo: reinstate this test, rename it as testGA
//    static public final String TEST_ALL_TEST_TYPE = "UNIT";
//
//    public dtestGA(final String val)
//    {
//      super(val);
//    }
//
//    int gen = 0;
//    int mut = 0;
//    int step = 0;
//    int prom = 0;
//    int ret = 0;
//    int sort = 0;
//
//    private class listener implements GAProgressed, GAStepped
//    {
//      /** our population has been sorted
//       *
//       */
//      public void sorted()
//      {
//        sort++;
//      }
//
//      /** we have generated a fresh population
//       *
//       */
//      public void generated()
//      {
//        gen++;
//      }
//
//      /** our population has mutated
//       *
//       */
//      public void mutated()
//      {
//        mut++;
//      }
//
//      /** our population has grown
//       *
//       */
//      public void stepped()
//      {
//        step++;
//      }
//
//      /** our population has grown
//       *
//       */
//      public void stepCompleted()
//      {
//        step++;
//      }
//
//      /** our star performers have been promoted
//       *
//       */
//      public void promoted()
//      {
//        prom++;
//      }
//
//      /** we have retired the losers
//       *
//       */
//      public void retired()
//      {
//        ret++;
//      }
//    }
//
//    public void testScoreSort()
//    {
//      Gene ga = Gene.getGeneForTesting();
//      ga.setFitness(12, "12");
//
//      Gene gb = Gene.getGeneForTesting();
//      gb.setFitness(16, "16");
//
//      Gene gc = Gene.getGeneForTesting();
//      gc.setFitness(14, "14");
//
//      Gene gd = Gene.getGeneForTesting();
//      gd.setFitness(14, "14");
//
//      Gene ge = Gene.getGeneForTesting();
//      ge.setFitness(15, "15");
//
//      Gene gf = Gene.getGeneForTesting();
//      gf.setFitness(13, "13");
//
//      GeneticAlgorithm gen = new GeneticAlgorithm(6, 2);
//      gen._theseGenes.add(ga);
//      gen._theseGenes.add(gb);
//      gen._theseGenes.add(gc);
//      gen._theseGenes.add(gd);
//      gen._theseGenes.add(ge);
//      gen._theseGenes.add(gf);
//
//      // check the initial order
//      assertEquals("first item is corect", gen._theseGenes.first(), ga);
//      assertEquals("last item is corect", gen._theseGenes.last(), gb);
//
//      // what happens if we promote?
//      gen.promote();
//
//      // check the initial order
//      assertEquals("first start is corect", gen._starGenes.first(), ge);
//      assertEquals("second start is corect", gen._starGenes.last(), gb);
//
//      list("first promotion", gen._starGenes);
//
//      // do a forward sort
//      gen.sort();
//
//      // check the order
//      assertEquals("first item is corect", gen._theseGenes.first(), ga);
//      assertEquals("last item is corect", gen._theseGenes.last(), gb);
//
//      list("forward", gen._theseGenes);
//
//      // do a reverse sort
//      gen.sort();
//
//      list("reverse", gen._theseGenes);
//
//      // check the order
//      assertEquals("first item is corect", gb,  gen._theseGenes.first());
//      assertEquals("last item is corect", ga, gen._theseGenes.last());
//
//      // reset the list of stars
//      gen._starGenes.clear();
//
//      gen.promote();
//
//      // promoted list?
//      list("promoted:",gen._starGenes);
//
//      // check the initial order
//      assertEquals("first star is corect", ga,  gen._starGenes.first());
//      assertEquals("second star is corect", gf, gen._starGenes.last());
//
//
//    }
//
//    public void testIt()
//    {
//
//
//      String test_root = System.getProperty("TEST_ROOT");
//      if(test_root == "" || test_root == null)
//      {
//        test_root = "d:\\dev\\Asset\\src\\test_data";
//      }
//
//      final String docPath = test_root + "\\factory_scenario.xml";
//      final String varyPath = test_root + "\\factory_variables.xml";
//
//
//      final int POP_SIZE = 10;
//      final int STAR_SIZE = 5;
//
//      /***************************************************************
//       *  read in the data
//       ***************************************************************/
//      // now put it into our ga
//      GeneticAlgorithm ga = null;
//      try
//      {
//        ga = new GeneticAlgorithm(POP_SIZE, STAR_SIZE);
//        ga.createGene(new java.io.FileInputStream(docPath), new java.io.FileInputStream(varyPath));
//        ga.setRunner(new RandomRunner());
//      }
//      catch (java.io.FileNotFoundException fe)
//      {
//        fe.printStackTrace();
//        assertTrue(fe.getMessage(), false);
//      }
//
//      /***************************************************************
//       *  add listeners
//       ***************************************************************/
//      final listener l = new listener();
//      ga.addGAProgressListener(l);
//      ga.addStepListener(l);
//
//
//      /***************************************************************
//       *  create a generation
//       ***************************************************************/
//
//      // do pre-checks
//      assertEquals("number before", 0, ga._theseGenes.size());
//      assertEquals("number before", 0, ga._starGenes.size());
//      assertEquals("correct pop size", POP_SIZE, ga._population_size);
//      assertEquals("correct pop size", STAR_SIZE, ga._stars_size);
//
//      ga.generate();
//
//      // do checks
//      // do pre-checks
//      assertEquals("pop number after", POP_SIZE, ga._theseGenes.size());
//      assertEquals("stars number after", 0, ga._starGenes.size());
//
//      // check counter
//      assertEquals("gen message sent", 1, gen);
//
//
//      /***************************************************************
//       *  mutate (not first time)
//       ***************************************************************/
//
//      // store generation
//      TreeSet safeGen = new TreeSet(ga._theseGenes);
//      ga.mutate();
//
//      // check gen still alive
//      assertTrue("we have kept same elements", checkSame(safeGen, ga._theseGenes));
//
//      assertEquals("mutate message sent", 1, mut);
//
//      /***************************************************************
//       *  step forward
//       ***************************************************************/
//
//      safeGen = new TreeSet(ga._theseGenes);
//
//      Gene first = (Gene) ga._theseGenes.first();
//      Gene last = (Gene) ga._theseGenes.last();
//
//      double beforeFit = first.getFitness();
//
//      // check gene is capable of producing document
//      String thisDoc = first.getDocument();
//      assertTrue("document was returned", (thisDoc != null));
//      assertTrue("document was returned", (thisDoc.length() > 0));
//
//      ga.step(false);
//
//      // check genes have changed
//      double afterFit = first.getFitness();
//
//      assertTrue("fitnesses have changed", (beforeFit != afterFit));
//
//      assertEquals("step message sent", POP_SIZE + 1, step);
//
//
//      /***************************************************************
//       *  sort
//       ***************************************************************/
//
//      // remember the order
//      safeGen = new TreeSet(ga._theseGenes);
//
//      // do the sort
//      ga.sort();
//
//      // check they are in roughly the correct order
//      first = (Gene) ga._theseGenes.first();
//      last = (Gene) ga._theseGenes.last();
//      assertEquals("correct order", -1, first.compareTo(last));
//
//      // check the order has changed
//      assertTrue("order is not same as before", (!checkSame(safeGen, ga._theseGenes)));
//
//      assertEquals("sort message sent", 1, sort);
//
//
//      /***************************************************************
//       *  promote
//       ***************************************************************/
//
//      assertEquals("before first promotion", 0, ga._starGenes.size());
//
//      ga.promote();
//
//      assertEquals("first promotion", STAR_SIZE, ga._starGenes.size());
//      // check they are in roughly the correct order
//      first = (Gene) ga._starGenes.first();
//      last = (Gene) ga._starGenes.last();
//      assertEquals("stars sorted", -1, first.compareTo(last));
//
//      Gene otherLast = (Gene) ga._theseGenes.first();
//      assertEquals("stars greater than normal sorted", -1, otherLast.compareTo(last));
//      assertEquals("promote message sent", 1, prom);
//
//
//
//      /***************************************************************
//       *  retire stragglers
//       ***************************************************************/
//      assertEquals("before retirement", POP_SIZE, ga._theseGenes.size());
//      assertEquals("before retirement", STAR_SIZE, ga._starGenes.size());
//
//      ga.retire();
//
//      assertEquals("after retirement", 0, ga._theseGenes.size());
//      assertEquals("after retirement", STAR_SIZE, ga._starGenes.size());
//      assertEquals("retire message sent", 1, ret);
//
//      /***************************************************************
//       *  create another
//       ***************************************************************/
//      ga.generate();
//
//      // do checks
//      // do pre-checks
//      assertEquals("pop number after", POP_SIZE, ga._theseGenes.size());
//      assertEquals("stars number after", STAR_SIZE, ga._starGenes.size());
//
//      // check counter
//      assertEquals("2nd gen message sent", 2, gen);
//
//
//      /***************************************************************
//       *  step
//       ***************************************************************/
//
//      first = (Gene) ga._theseGenes.first();
//      beforeFit = first.getFitness();
//
//      // check gene is capable of producing document
//      thisDoc = first.getDocument();
//      assertTrue("document was returned", (thisDoc != null));
//      assertTrue("document was returned", (thisDoc.length() > 0));
//
//      ga.step(false);
//
//      // check genes have changed
//      afterFit = first.getFitness();
//
//      assertTrue("fitnesses have changed", (beforeFit != afterFit));
//
//      assertEquals("2nd step message sent", 2 * POP_SIZE + 2, step);
//
//      /***************************************************************
//       *  sort
//       ***************************************************************/
//      ga.sort();
//
//      // check they are in roughly the correct order
//      first = (Gene) ga._theseGenes.first();
//      last = (Gene) ga._theseGenes.last();
//      assertEquals("correct order", -1, first.compareTo(last));
//
//      assertEquals("sort message sent", 2, sort);
//
//
//      /***************************************************************
//       *  promote
//       ***************************************************************/
//
//      // take copy of existing stars
//      final TreeSet ts = new TreeSet(ga._starGenes);
//
//      // check size
//      assertEquals("before second promotion", STAR_SIZE, ga._starGenes.size());
//
//      ga.promote();
//
//      assertEquals("first promotion", STAR_SIZE, ga._starGenes.size());
//      // check they are in roughly the correct order
//      first = (Gene) ga._starGenes.first();
//      last = (Gene) ga._starGenes.last();
//      assertEquals("stars sorted", -1, first.compareTo(last));
//
//      otherLast = (Gene) ga._theseGenes.first();
//      assertEquals("stars greater than normal sorted", -1, otherLast.compareTo(last));
//
//      // ok, now do retire
//      ga.retire();
//
//      // check that new list is different to old stars list
//      assertTrue("lists contain different items", (!checkSame(ts, ga._starGenes)));
//      assertEquals("promote message sent", 2, prom);
//
//    }
//
//
//    private static boolean checkSame(final SortedSet a, final SortedSet b)
//    {
//      boolean same = true;
//      final Iterator newIt = a.iterator();
//      final Iterator oldIt = b.iterator();
//
//      while (newIt.hasNext())
//      {
//        final Gene newOb = (Gene) newIt.next();
//        final Gene oldOb = (Gene) oldIt.next();
//
//        if (newOb != oldOb)
//        {
//          same = false;
//          break;
//        }
//      }
//      return same;
//    }
//
//    private static void list(final String msg, final SortedSet set)
//    {
//      if (msg != null)
//        System.out.println(msg);
//      final Iterator it = set.iterator();
//      while (it.hasNext())
//      {
//        final Gene variable = (Gene) it.next();
//        System.out.println(":" + variable.toString());
//      }
//    }
//  }


//  public static void main(String[] args)
//  {
//    final dtestGA ga = new dtestGA("test");
//    ga.testScoreSort();
//    ga.testIt();
//  }

}