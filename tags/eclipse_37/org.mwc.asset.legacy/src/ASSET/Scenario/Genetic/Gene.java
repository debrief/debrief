/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Nov 2, 2001
 * Time: 2:09:35 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Scenario.Genetic;

import ASSET.Util.MonteCarlo.XMLVariance;
import ASSET.Util.MonteCarlo.XMLVarianceList;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.util.Comparator;
import java.util.Iterator;

public class Gene implements Comparable<Gene>
{
  /***************************************************************
   *  member methods
   ***************************************************************/
  /**
   * the list of variables (chromosomes) we represent
   */
  XMLVarianceList _myList = null;

  /**
   * the text file containing our scenario
   */
  private String _newDocument = null;

  /**
   * our fitness figure (or -1 if we haven't got one)
   */
  private double _myFitness = ScenarioRunner.ScenarioOutcome.INVALID_SCORE;

  /**
   * formatter for the fitness function
   */
  static private java.text.DecimalFormat _formatter = new java.text.DecimalFormat("000.0000");


  /**
   * the name of this gene
   */
  private String _myName;

  /**
   * summary of the performance of this gene
   */
  private String _mySummary;

  /**
   * ************************************************************
   * constructor
   * *************************************************************
   */
  public Gene(final Element varianceElement,
              final InputStream inputVary)
  {
    this(new XMLVarianceList());
    _myList.loadFrom(varianceElement);
  }

  /**
   * copy constructor, using deep copy
   */
  private Gene(final XMLVarianceList variables)
  {
    _myFitness = ScenarioRunner.ScenarioOutcome.INVALID_SCORE;
    if (variables != null)
    {
      _myList = (XMLVarianceList) variables.clone();
    }
  }

  /**
   * internal constructor - private since we need valid data in order to test
   */
  private Gene()
  {
    this(new XMLVarianceList());
  }

  /***************************************************************
   *  member methods
   ***************************************************************/

  /**
   * testing method, which returns a plain gene without having to read in data,
   * etc
   *
   * @return a blank Gene
   */
  final public static Gene getGeneForTesting()
  {
    return new Gene();
  }


  /**
   * get the list of variables
   */
  public XMLVarianceList getChromosomes()
  {
    return _myList;
  }

  /**
   * create a new permutation of this gene
   */
  public Gene createRandom()
  {
    final Gene res = new Gene();
    res._myList = (XMLVarianceList) this._myList.clone();
    
    final Iterator<XMLVariance> it = res._myList.getIterator();
    while (it.hasNext())
    {
      final XMLVariance variable = (XMLVariance) it.next();
      final XMLVariance new_var = (XMLVariance) variable.clone();
      new_var.randomise();
      res._myList.add(new_var);
    }

    return res;
  }

  /**
   * merge this gene with the one supplied
   *
   * @param other       Gene to merge with
   * @param probability threshold above which we merge a chromosome (0..1)
   */
  public void mergeWith(final Gene other, final double probability)
  {
    // merge random genes
    final Iterator<XMLVariance> it_a = _myList.getIterator();
    final Iterator<XMLVariance> it_b = other._myList.getIterator();
    while (it_a.hasNext())
    {
      final XMLVariance my_next = (XMLVariance) it_a.next();
      final XMLVariance his_next = (XMLVariance) it_b.next();

      final double nextP = ASSET.Util.RandomGenerator.nextRandom();
      if (nextP <= probability)
      {
        my_next.merge(his_next);
      }
    }
  }

  public double getFitness()
  {
    return _myFitness;
  }

  public String getSummary()
  {
    return _mySummary;
  }

  public void setFitness(final double val, String summary)
  {
    _myFitness = val;
    _mySummary = summary;
  }

  public String getDocument()
  {
    //    if(_newDocument == null)
    //    {
    //      _newDocument = _myList.getNewPermutation();
    //    }
    // todo: reinstate this
    return _newDocument;
  }


  /**
   * comparison operator
   */
  public boolean equals(final Gene other)
  {
    boolean res = true;
    if (_myList.size() != other._myList.size())
    {
      res = false;
    }
    else
    {
      for (int i = 0; i < _myList.size(); i++)
      {
        final Iterator<XMLVariance> it_a = _myList.getIterator();
        final Iterator<XMLVariance> it_b = other._myList.getIterator();

        final XMLVariance mine = (XMLVariance) it_a.next();
        final XMLVariance his = (XMLVariance) it_b.next();

        final boolean eq = mine.equals(his);

        if (!eq)
        {
          res = false;
          break;
        }
      }
    }

    return res;
  }

  public String toString()
  {

    String res = "Fit:" + _formatter.format(getFitness()) + " || ";
    res += "Summary:" + getSummary() + " || ";
    final Iterator<XMLVariance> it = _myList.getIterator();
    while (it.hasNext())
    {
      final XMLVariance variable = (XMLVariance) it.next();
      res += variable.getName() + ":" + variable.getValue() + "| ";
    }

    return res + " " + getName();
  }

  public int compareTo(final Gene o2)
  {
    int res = 0;
    final double fit1 = getFitness();
    final double fit2 = ((Gene) o2).getFitness();
    if (o2 == this)
      res = 0;
    else if (fit1 > fit2)
      res = 1;
    else
      res = -1;

    return res;
  }

  public static class ReverseComparator implements Comparator<Gene>
  {
    public int compare(final Gene o1, final Gene o2)
    {
      int res = 0;
      final double fit1 = ((Gene) o1).getFitness();
      final double fit2 = ((Gene) o2).getFitness();
      if (o2 == o1)
        res = 0;
      else if (fit1 < fit2)
        res = 1;
      else
        res = -1;

      return res;
    }
  }


  /**
   * ************************************************************
   * testing code
   * *************************************************************
   */
  public static class GeneTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public GeneTest(final String val)
    {
      super(val);
    }

    public void testIt()
    {

      //      String code_root = System.getProperty("CODE_ROOT");
      //      if(code_root == null)
      //        code_root = "D:\\dev\\asset\\src\\java";
      //
      //      final String docPath = code_root + "\\ASSET_SRC\\ASSET\\Util\\XMLFactory\\SuperSearchSSK.xml";
      //      final String varyPath = code_root + "\\ASSET_SRC\\ASSET\\Util\\XMLFactory\\vary_ssk.xml";
      //      final String badVaryPath = code_root + "\\ASSET_SRC\\ASSET\\Util\\XMLFactory\\BAD_vary_ssk.xml";
      //
      //      /***************************************************************
      //       *  initialise from stream (files missing)
      //       ***************************************************************/
      //      boolean ex_thrown = false;
      //      try{
      //        final Gene noFile = new Gene(new java.io.FileInputStream("none"), new java.io.FileInputStream("none"));
      //      }
      //      catch(java.io.FileNotFoundException fe)
      //      {
      //        ex_thrown = true;
      //      }
      //      assertTrue("FileNotFound thrown", ex_thrown);
      //
      //      /***************************************************************
      //       *  initialise from stream (invalid files)
      //       ***************************************************************/
      //      ex_thrown = false;
      //      try{
      //        java.io.FileInputStream inputDoc = new java.io.FileInputStream(docPath);
      //        assertNotNull("Failed to find input document", inputDoc);
      //        java.io.FileInputStream inputVary = new java.io.FileInputStream(badVaryPath);
      //        assertNotNull("Failed to find input variance", inputVary);
      //        final Gene noFile = new Gene(inputDoc, inputVary);
      //      }
      //      catch(java.lang.RuntimeException re)
      //      {
      //        // check it's the error we're after
      //        final String msg = re.getMessage();
      //        if(msg.indexOf("VaribleList") > 0)
      //          ex_thrown = true;
      //      }
      //      catch(java.io.FileNotFoundException fe)
      //      {
      //        fe.printStackTrace();
      //        ex_thrown = true;
      //      }
      //      assertTrue("Bad file report thrown", ex_thrown);
      //
      //      /***************************************************************
      //       *  initialise from stream (valid)
      //       ***************************************************************/
      //      Gene ng = null;
      //      try{
      //
      //        // check the files exist
      //        assertTrue("input document not found", new File(docPath).exists());
      //        assertTrue("variance document not found", new File(varyPath).exists());
      //
      //        java.io.FileInputStream inputDoc = new java.io.FileInputStream(docPath);
      //        assertNotNull("Failed to find input document", inputDoc);
      //        java.io.FileInputStream inputVary = new java.io.FileInputStream(varyPath);
      //        assertNotNull("Failed to find input variance", inputVary);
      //        ng = new Gene(inputDoc, inputVary);
      //      }
      //      catch(java.io.FileNotFoundException fe)
      //      {
      //        fe.printStackTrace();
      //      }
      //
      //      assertNotNull("Gene created", ng);
      //
      //      /***************************************************************
      //       *  create a new random gene
      //       ***************************************************************/
      //      final XMLVarianceList xl = ng._myList;
      //
      //      final Gene rg = ng.createRandom();
      //
      //      final XMLVarianceList xl2 = rg._myList;
      //
      //      assertEquals("Random list of correct length", xl2.size(), xl.size());
      //      assertTrue("Random List contains different genes", (xl != xl2));
      //
      //      /***************************************************************
      //       * setting fitness
      //       ***************************************************************/
      //      assertEquals("fitness initialised", ScenarioRunner.ScenarioOutcome.INVALID_SCORE, ng.getFitness(), 0);
      //
      //      final double newFit = 12d;
      //
      //      ng.setFitness(newFit, "null");
      //
      //      assertEquals("fitness assigned", newFit, ng.getFitness(), 0);
      //
      //      /***************************************************************
      //       *  checking equality
      //       ***************************************************************/
      //      assertTrue("same genes are equal", (ng.equals(ng)));
      //      assertTrue("different genes aren't equal", (!ng.equals(rg)));
      //
      //      /***************************************************************
      //       *  comparing genes
      //       ***************************************************************/
      //      final double otherFit = 15d;
      //      rg.setFitness(otherFit, null);
      //
      //      assertEquals("comparison of genes", ng.compareTo(rg), -1);
      //
      //      ex_thrown = false;
      //      try
      //      {
      //        final int res = ng.compareTo(new java.util.Date());
      //      }
      //      catch(java.lang.ClassCastException ce)
      //      {
      //        ex_thrown = true;
      //      }
      //      assertTrue("recognised comparison of invalid object", ex_thrown);
      //
      //      /***************************************************************
      //       *  able to produce document
      //       ***************************************************************/
      //      final String newDoc = ng.getDocument();
      //
      //      assertNotNull("document created", newDoc);
      //      assertTrue("document contains stuff", newDoc.length() > 0);
      //      assertTrue("document is different to original (we only value in dem course):" + newDoc.indexOf("134.293"),
      //        511 < newDoc.indexOf("134.293"));
      //
      //      /***************************************************************
      //       *  merge operation
      //       ***************************************************************/
      //      final double prob_a = 0.0;
      //      final double prob_b = 1.0;
      //
      //      // take copy of gene
      //      final Gene safeG = new Gene(ng._myList);
      //
      //      ng.mergeWith(rg, prob_a);
      //
      //      assertTrue("1 makes no change", safeG.equals(ng));
      //
      //      ng.mergeWith(rg, prob_b);
      //
      //      assertTrue("zero thresh makes all changes", allDiff(safeG, ng));
      // todo: reinstate this
    }

    /** output this gene
     *
     */
    //    private static String showGene(final Gene g)
    //    {
    //      String res = "";
    //      final XMLVarianceList lst =  g._myList;
    //      final Iterator it = lst.getIterator();
    //      while (it.hasNext())
    //      {
    //        final XMLVariance variable = (XMLVariance) it.next();
    //        res += variable.toString() + "\n";
    //      }
    //      return res;
    //    }

    /**
     * are these genes completely different?
     */
    protected static boolean allDiff(final Gene a, final Gene b)
    {
      boolean diff = true;
      // merge random genes
      final Iterator<XMLVariance> it_a = a._myList.getIterator();
      final Iterator<XMLVariance> it_b = b._myList.getIterator();
      while (it_a.hasNext())
      {
        final XMLVariance my_next = (XMLVariance) it_a.next();
        final XMLVariance his_next = (XMLVariance) it_b.next();
        if (my_next.equals(his_next))
        {
          diff = false;
        }
      }
      return diff;
    }
  }

  private String getName()
  {
    return _myName;
  }

  public void setName(final String name)
  {
    this._myName = name;
  }


}
