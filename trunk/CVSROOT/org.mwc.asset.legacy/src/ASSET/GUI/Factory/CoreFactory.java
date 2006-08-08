/*
* Desciption:  Class which implements base (non-GUI) aspects of our factory
* User: administrator
* Date: Nov 5, 2001
* Time: 11:47:17 AM
*/
package ASSET.GUI.Factory;

import ASSET.Scenario.Genetic.Gene;
import ASSET.Scenario.Genetic.GeneticAlgorithm;
import ASSET.Scenario.Genetic.ScenarioRunner;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GUI.DragDrop.FileDropSupport;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReaderWriter;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Vector;

public class CoreFactory implements GeneticAlgorithm.GAProgressed, GeneticAlgorithm.GAStepped
{
  /***************************************************************
   *  member variables
   ***************************************************************/
  /**
   * our genetic algorithm
   */
  GeneticAlgorithm _myGA = null;

  /**
   * the file to take the scenario from
   */
  private String _docFile;

  /**
   * the file containing our varianec
   */
  private String _varyFile;

  /**
   * the object which runs through the scenario for us
   */
  private ScenarioRunner _myRunner;

  /**
   * our list of observers
   */
  private Vector _myObservers = new Vector(0, 1);

  /**
   * the listener for observer files being dropped in
   */
  MWC.GUI.DragDrop.FileDropSupport _observerDropper;

  /**
   * the listener for scenario files being dropped in
   */
  MWC.GUI.DragDrop.FileDropSupport _scenarioDropper;

  /**
   * the listener for variance files being dropped in
   */
  MWC.GUI.DragDrop.FileDropSupport _varianceDropper;

  /**
   * how many genes to create
   */
  private int _genes = 0;

  /**
   * how many stars to retain
   */
  private int _stars = 0;

  /**
   * whether to search for low scores instead of high
   */
  private boolean _lowScoresHigh = false;


  /**
   * ************************************************************
   * constructor
   * *************************************************************
   */
  CoreFactory()
  {
    _myRunner = new CoreScenarioRunner();
    createDroppers();
  }

  /***************************************************************
   *  member methods
   ***************************************************************/


  /**
   * method to check if all data is now loaded
   */
  private void checkDataLoaded()
  {
    if ((_docFile != null) && (_myObservers != null) && (_varyFile != null))
    {
      signalAllDataLoaded();
    }
  }

  public void signalAllDataLoaded()
  {
    // don't really bother - other classes can over-ride this method to find out when data is loaded
  }

  public boolean getLowScoresHigh()
  {
    return _lowScoresHigh;
  }

  private void createDroppers()
  {
    _observerDropper = new FileDropSupport();
    _observerDropper.setFileDropListener(new FileDropSupport.FileDropListener()
    {
      public void FilesReceived(final Vector files)
      {
        final Iterator it = files.iterator();
        while (it.hasNext())
        {
          final File file = (File) it.next();

          // and load it
          loadObserver(file.getPath());
        }
      }
    }, ".XML");

    _scenarioDropper = new FileDropSupport();
    _scenarioDropper.setFileDropListener(new FileDropSupport.FileDropListener()
    {
      public void FilesReceived(final Vector files)
      {
        final Iterator it = files.iterator();
        while (it.hasNext())
        {
          final File file = (File) it.next();

          // and store the filename
          setScenarioFile(file.getPath());
        }
      }
    }, ".XML");

    _varianceDropper = new FileDropSupport();
    _varianceDropper.setFileDropListener(new FileDropSupport.FileDropListener()
    {
      public void FilesReceived(final Vector files)
      {
        final Iterator it = files.iterator();
        while (it.hasNext())
        {
          final File file = (File) it.next();

          // and store the filename
          setVariablesFile(file.getPath());
        }
      }
    }, ".XML");
  }

  /**
   * store the filename containing the scenario, and check if we are ready to start
   *
   * @param file the file containing the scenario
   */
  public void setScenarioFile(final String file)
  {
    _docFile = file;

    // see if we now have all of our data
    checkDataLoaded();
  }

  /**
   * store the filename containing the variables, and check if we are ready to start
   *
   * @param file the file containing the variables
   */
  public void setVariablesFile(final String file)
  {
    _varyFile = file;

    // see if we now have all of our data
    checkDataLoaded();
  }

  /**
   * load the observers from the indicated file
   *
   * @param fName file containing the observers
   */
  public void loadObserver(final String fName)
  {

    try
    {
      File file = new File(fName);
      final MWC.Utilities.ReaderWriter.XML.MWCXMLReader reader = new
        ASSET.Util.XML.Control.FactoryHandler()
        {
          public void setFactory(final Vector list, final int genes, final int stars, boolean lowScoresHigh)
          {
            _myObservers.addAll(list);
            _genes = genes;
            _stars = stars;
            _lowScoresHigh = lowScoresHigh;
          }
        };

      MWCXMLReaderWriter.importThis(reader, file.getName(), new FileInputStream(file));
    }
    catch (SAXException fe)
    {
      MWC.Utilities.Errors.Trace.trace(fe, "Loading factory observer list");
    }
    catch (FileNotFoundException fe)
    {
      MWC.Utilities.Errors.Trace.trace(fe, "Loading factory observer list");
    }

    // see if we now have all of our data
    checkDataLoaded();
  }

  /**
   * add the indicated observer to our list.  The observer
   * will receive setup and teardown calls for each new scenario
   */
  public void addObserver(final ScenarioObserver observer)
  {
    _myObservers.add(observer);
  }

  /**
   * remove the indicated observer from our list
   */
  public void removeObserver(final ScenarioObserver observer)
  {
    _myObservers.remove(observer);
  }


  /**
   * set the document
   */
  private void setDocument(final String doc)
  {
    _docFile = doc;
  }

  /**
   * set the variance file
   */
  private void setVariance(final String vary)
  {
    _varyFile = vary;
  }

  /**
   * build the GA
   */
  public boolean build()
  {
    boolean worked = false;

    // check we have our data files
    if (_docFile == null)
    {
      MWC.Utilities.Errors.Trace.trace("Scenario file missing");
      return worked;
    }

    // check we have our data files
    if (_varyFile == null)
    {
      MWC.Utilities.Errors.Trace.trace("Variables file missing");
      return worked;
    }


    // read in the data files & store the data
    try
    {
      _myGA = new GeneticAlgorithm(_genes, _stars);
      FileInputStream fs = new FileInputStream(_docFile);

      _myGA.createGene(new java.io.FileInputStream(_docFile), new java.io.FileInputStream(_varyFile));

      // swap the runner
      //        _myRunner = new GeneticAlgorithm.RandomRunner();
      //      _myRunner = new GeneticAlgorithm.TotalRunner();

      _myGA.setRunner(_myRunner);
      _myGA.addGAProgressListener(this);
    }
    catch (java.io.FileNotFoundException fe)
    {
      MWC.Utilities.Errors.Trace.trace(fe, "Reading in data files");
      return worked;
    }


    worked = true;
    return worked;
  }

  public void cycle()
  {
    _myGA.generate();

    _myGA.mutate();

    _myGA.step(getLowScoresHigh());

    _myGA.promote();

    _myGA.retire();
  }



  /***************************************************************
   *  GA progressed methods
   ***************************************************************/


  /**
   * we have generated a fresh population
   */
  public void generated()
  {
  }

  /**
   * our population has been sorted
   */
  public void sorted()
  {

  }

  /**
   * our population has mutated
   */
  public void mutated()
  {

  }

  /**
   * our population has grown
   */
  public void stepCompleted()
  {
  }

  /**
   * our star performers have been promoted
   */
  public void promoted()
  {
    // print the list of variables & scores
  }

  /**
   * we have retired the losers
   */
  public void retired()
  {
  }
  /***************************************************************
   *  GA stepped methods
   ***************************************************************/
  /**
   * a gene has developed
   */
  public void stepped()
  {
    System.out.print(".");
  }

  public static void showGenes(final Iterator gene)
  {
    while (gene.hasNext())
    {
      final Gene thisG = (Gene) gene.next();
      System.out.println(thisG.toString());
    }
    System.out.println("================");
  }


  /***************************************************************
   *
   * Interfaces
   *
   ***************************************************************/
  /**
   * basic interface of scenario runner, which just returns a random number
   */
  private class CoreScenarioRunner implements ScenarioRunner, ASSET.Scenario.ScenarioRunningListener
  {
    boolean _running = false;

    /**
     * the scenario has stopped running on auto
     */
    public void paused()
    {
      // let's not worry about this little thing
    }

    /**
     * the scenario has stopped running on auto
     */
    public void finished(long elapsedTime, String reason)
    {
      _running = false;
    }

    /**
     * the scenario step time has changed
     */
    public void newScenarioStepTime(int val)
    {
    }

    /**
     * the GUI step time has changed
     */
    public void newStepTime(int val)
    {
    }

    public void restart()
    {
    }

    /**
     * the scenario has started running on auto
     */
    public void started()
    {
      _running = true;
    }

    public ScenarioRunner.ScenarioOutcome runThis(final String scenario, final String name, String desc)
    {

      System.out.print(".");
      ScenarioRunner.ScenarioOutcome res = new ScenarioOutcome();
      res.score = -1;
      res.summary = "";

      final ASSET.Scenario.CoreScenario thisScen = new ASSET.Scenario.CoreScenario();
      ASSETReaderWriter.importThis(thisScen, "", new java.io.ByteArrayInputStream(scenario.getBytes()));

      // set the name
      thisScen.setName(name);

      // so, we now have our scenario - run through the observers
      for (int i = 0; i < _myObservers.size(); i++)
      {
        final ScenarioObserver observer = (ScenarioObserver) _myObservers.elementAt(i);
        observer.setup(thisScen);
      }

      // add ourselves as a listener
      thisScen.addScenarioRunningListener(this);

      // run through
      _running = true;
      while (_running)
      {
        thisScen.step();
      }

      // so, we now have our scenario - run through the observers
      for (int i = 0; i < _myObservers.size(); i++)
      {
        // get the next observer
        final ScenarioObserver observer = (ScenarioObserver) _myObservers.elementAt(i);

        // does this have a score?
        if (observer instanceof ScenarioObserver.ScenarioReferee)
        {
          final ScenarioObserver.ScenarioReferee ref = (ScenarioObserver.ScenarioReferee) observer;

          // has this returned a valid score?
          ScenarioRunner.ScenarioOutcome thisRes = ref.getOutcome();
          if (thisRes != null)
          {
            // do we have a valid score?
            if (thisRes.score != ScenarioRunner.ScenarioOutcome.INVALID_SCORE)
              res.score += thisRes.score;

            res.summary += thisRes.summary;
          }
        }
        // and tear down the observer
        observer.tearDown(thisScen);
      }

      // remove ourselves as a listener
      thisScen.removeScenarioRunningListener(this);

      // clear the scenario
      return res;
    }


  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  public static class FactoryTest extends junit.framework.TestCase
  {
    // todo reinstate these tests (should be UNIT)
    static public final String TEST_ALL_TEST_TYPE = "UNTIE";

    public FactoryTest(final String val)
    {
      super(val);
    }

    public void testStepping()
    {
      //      final CoreFactory cf = new CoreFactory();
      ////    cf.setDocument("d:\\dev\\Asset\\ASSET2_OUT\\tstFactory.xml");
      ////    cf.setVariance("d:\\dev\\Asset\\ASSET2_OUT\\vary_factory.xml");
      //
      //      String root = System.getProperty("TEST_ROOT");
      //      if(root == null)
      //      {
      //        root = "d:\\dev\\asset2_out\\";
      //      }
      //
      //      cf.setDocument(root + "factory_scenario.xml");
      //      cf.setVariance(root + "factory_variables.xml");
      //
      //
      //      // artificially set the genes & stars
      //      cf._genes = 30;
      //      cf._stars = 5;
      //      cf._myRunner = new GeneticAlgorithm.TotalRunner();
      //
      //      boolean worked = cf.build();
      //
      //      // check the build worked ok
      //      assertTrue("couldn't load data", worked);
      //
      //      final GeneticAlgorithm ga = cf._myGA;
      //
      //      // check empty
      //      assertEquals("genes empty", ga.getGenes().size(), 0);
      //      assertEquals("no stars got created", ga.getStarGenes().size(), 0);
      //
      //      // do the step components first
      //      ga.generate();
      //
      //      // check some got created
      //      assertEquals("genes created", ga.getGenes().size(), 30);
      //      assertEquals("no stars got created", ga.getStarGenes().size(), 0);
      //
      //      ga.mutate();
      //
      //      // no mutation expected, since first cycle
      //      assertEquals("genes created", ga.getGenes().size(), 30);
      //      assertEquals("no stars got created", ga.getStarGenes().size(), 0);
      //
      //      ga.step(false);
      //
      //      // check changed
      //      final Gene firstG = (Gene) ga.getGenes().iterator().next();
      //      double firstScore = firstG.getFitness();
      //      assertTrue("gene got score", firstG.getFitness() != -1);
      //      assertEquals("genes still there", ga.getGenes().size(), 30);
      //      assertEquals("no stars got created", ga.getStarGenes().size(), 0);
      //
      //      ga.promote();
      //
      //      // check changed
      //      Gene firstStar = (Gene) ga.getStarGenes().iterator().next();
      //      firstScore = firstG.getFitness();
      //      assertTrue("star got score", firstScore != -1);
      //      assertEquals("genes got scores", ga.getGenes().size(), 30);
      //      assertEquals("stars got created", ga.getStarGenes().size(), 5);
      //
      //      ga.retire();
      //
      //      // check changed
      //      assertEquals("genes got retired", ga.getGenes().size(), 0);
      //      assertEquals("stars got created", ga.getStarGenes().size(), 5);
      //
      //      cf.cycle();
      //
      //      // check that the score has changed
      //      firstStar = (Gene) ga.getStarGenes().iterator().next();
      //      final double secondScore = firstStar.getFitness();
      //      assertTrue("score changed", secondScore != firstScore);
      //
      // todo: reinstate this
    }
  }



  /***************************************************************
   *  testing methods
   ***************************************************************/


}
