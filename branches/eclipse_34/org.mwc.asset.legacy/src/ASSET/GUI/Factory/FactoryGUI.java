/*
 * Desciption:
 * User: administrator
 * Date: Nov 5, 2001
 * Time: 2:11:23 PM
 */
package ASSET.GUI.Factory;

import ASSET.Scenario.Genetic.Gene;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.util.Iterator;
import java.util.Vector;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FactoryGUI extends JPanel implements
      ASSET.Scenario.Genetic.GeneticAlgorithm.GAProgressed,
      ASSET.Scenario.Genetic.GeneticAlgorithm.GAStepped
{
  /***************************************************************
   *  member variables
   ***************************************************************/
  /** our factory
   *
   */
  private CoreFactory _myFactory;

  /** our stars list
   *
   */
  private JList _starList;

  /** the build button (which gets enabled after all necessary data is loaded
   *
   */
  private JButton _buildBtn;

  /** our genes list
   *
   */
  private JList _geneList;

  /** our cycle button
   *
   */
  private JButton _cycleBtn;

  /** our step button
   *
   */
  private JButton _stepBtn;

  /** our list of discrete steps
   *
   */
  private JToolBar _discreteList;


  /***************************************************************
   *  constructor
   ***************************************************************/
  private FactoryGUI()
  {
    /** create the factory
     *
     */
    _myFactory = new CoreFactory(){
      public void signalAllDataLoaded()
      {
        super.signalAllDataLoaded();
        _buildBtn.setEnabled(true);
      }
    };

    /** and build the form
     *
     */
    initForm();
  }


  /***************************************************************
   *  member methods
   ***************************************************************/
  /** build the form
   *
   */
  private void initForm()
  {
    setLayout(new BorderLayout());

    final JToolBar tools = new JToolBar("Tools", JToolBar.HORIZONTAL);

    final JPanel lists = new JPanel();
    lists.setName("lists");
    lists.setLayout(new GridLayout(2,0));
    _geneList = new JList();
    _geneList.setBorder(new TitledBorder("Genes"));
    _starList = new JList();
    _starList.setBorder(new TitledBorder("Stars"));
    _starList.add(new JLabel("blank"));
    lists.add(new JScrollPane(_starList));
    lists.add(new JScrollPane(_geneList));

    _buildBtn = new JButton("build");
    _buildBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        doBuild();
      }
    });
    _buildBtn.setEnabled(false);

    _discreteList = new JToolBar(JToolBar.HORIZONTAL);
    _discreteList.setEnabled(false);

    final JButton generate = new JButton("generate");
    generate.setEnabled(false);
    generate.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        _myFactory._myGA.generate();
      }
    });
    final JButton mutate = new JButton("mutate");
    mutate.setEnabled(false);
    mutate.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        _myFactory._myGA.mutate();
      }
    });
    _stepBtn = new JButton("step");
    _stepBtn.setEnabled(false);
    _stepBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        doStep();
      }
    });
    final JButton sort = new JButton("sort");
    sort.setEnabled(false);
    sort.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        _myFactory._myGA.sort();
      }
    });
    final JButton promote = new JButton("promote");
    promote.setEnabled(false);
    promote.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        _myFactory._myGA.promote();
      }
    });
    final JButton retire = new JButton("retire");
    retire.setEnabled(false);
    retire.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        _myFactory._myGA.retire();
      }
    });


    _cycleBtn = new JButton("cycle");
    _cycleBtn.setEnabled(false);
    _cycleBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        _myFactory.cycle();
      }
    });
    final JButton exit = new JButton("exit");
    exit.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        System.exit(0);
      }
    });

    // put in the tools
    tools.add(_buildBtn);
    tools.add(_cycleBtn);

    tools.add(new javax.swing.JSeparator(JSeparator.VERTICAL));
    _discreteList.add(generate);
    _discreteList.add(mutate);
    _discreteList.add(_stepBtn);
    _discreteList.add(sort);
    _discreteList.add(promote);
    _discreteList.add(retire);

    tools.add(_discreteList);

    tools.add(new javax.swing.JSeparator(JSeparator.VERTICAL));

    final JLabel observers = new JLabel("Control");
    _myFactory._observerDropper.addComponent(observers);
    observers.setBorder(new javax.swing.border.EtchedBorder(EtchedBorder.RAISED));
    tools.add(observers);

    final JLabel Variables = new JLabel("Variables");
    _myFactory._varianceDropper.addComponent(Variables);
    Variables.setBorder(new javax.swing.border.EtchedBorder(EtchedBorder.RAISED));
    tools.add(Variables);

    final JLabel Scenario = new JLabel("Scenario");
    _myFactory._scenarioDropper.addComponent(Scenario);
    Scenario.setBorder(new javax.swing.border.EtchedBorder(EtchedBorder.RAISED));
    tools.add(Scenario);


    tools.add(new javax.swing.JSeparator(JSeparator.VERTICAL));

    tools.add(exit);

    this.add("South", tools);
    this.add("Center", lists);

  }

  /** do a step operation
   *
   */
  private void doStep()
  {
    _stepBtn.setEnabled(false);

    final Runnable doIt = new Runnable(){
      public void run()
      {
        _myFactory._myGA.step(_myFactory.getLowScoresHigh());
        _stepBtn.setEnabled(true);
      }
    };

    final Thread tt = new Thread(doIt);
    tt.setPriority(Thread.MIN_PRIORITY);
    tt.start();
  }


  /** do the build operation
   *
   */
  private void doBuild()
  {
 //   _myFactory.setDocument("C:\\Asset\\ASSET2_OUT\\tstFactory.xml");
 //   _myFactory.setVariance("C:\\Asset\\ASSET2_OUT\\vary_factory.xml");

    _myFactory.build();

    // check it worked
    if(_myFactory._myGA == null)
      return;

    /** listen to the GA
     *
     */
    _myFactory._myGA.addGAProgressListener(this);

    _myFactory._myGA.addStepListener(this);

    // and enable the cycle btn
    _cycleBtn.setEnabled(true);

    //
    _discreteList.setEnabled(true);

    final Component[] comps =  _discreteList.getComponents();
    for (int i = 0; i < comps.length; i++)
    {
      final Component comp = comps[i];
      comp.setEnabled(true);
    }


  }

  private void updateGenes(final Iterator iter)
  {

    // empty the list
    _geneList.removeAll();

    // re-populate it
    final Vector newL = new Vector(0,1);
    while (iter.hasNext())
    {
      final Gene gene = (Gene) iter.next();
      final String newStr = gene.toString();
      newL.add(newStr);
    }
    _geneList.setListData(newL);

    // trigger refresh
    _geneList.invalidate();
  }

  /** usign the supplied iterator put a list of genes
   * into the panel
   */
  private void updateStars(final Iterator iter)
  {
    // empty the list
    _starList.removeAll();

    // re-populate it
    final Vector newL = new Vector(0,1);
    while (iter.hasNext())
    {
      final Gene gene = (Gene) iter.next();
      final String newStr = gene.toString();
      newL.add(newStr);
    }
    _starList.setListData(newL);

    // trigger refresh
    _starList.invalidate();
  }

  /** we have generated a fresh population
   *
   */
  public void generated()
  {
    final Iterator it = _myFactory._myGA.getGenes().iterator();
    updateGenes(it);
  }

  /** our population has been sorted
   *
   */
  public void sorted()
  {
    final Iterator it = _myFactory._myGA.getGenes().iterator();
    updateGenes(it);
  }

  /** our population has mutated
   *
   */
  public void mutated()
  {
    final Iterator it = _myFactory._myGA.getGenes().iterator();
    updateGenes(it);
  }

  /** our population has grown
   *
   */
  public void stepCompleted()
  {
    final Iterator it = _myFactory._myGA.getGenes().iterator();
    updateGenes(it);
  }

  /** our star performers have been promoted
   *
   */
  public void promoted()
  {
    final Iterator it = _myFactory._myGA.getStarGenes().iterator();
    updateStars(it);
  }

  /** we have retired the losers
   *
   */
  public void retired()
  {
    final Iterator it = _myFactory._myGA.getGenes().iterator();

    // we have to clear out our list of genes
    _geneList.setListData(new java.util.Vector(0,1));
    updateGenes(it);
  }

  /** a gene has developed
   *
   */
  public void stepped()
  {
    final Iterator it = _myFactory._myGA.getGenes().iterator();
    updateGenes(it);
    _geneList.repaint();
  }


  /***************************************************************
   *  class which wraps a gene in a Label, to place it into a List
   ***************************************************************/
//  private class GeneWrapper extends JLabel
//  {
//    Gene _myGene = null;
//
//
//    public GeneWrapper()
//    {
//    }
//
//    private void pressed()
//    {
//      MWC.GUI.Dialogs.DialogFactory.showMessage("Title", "view pressed");
//    }
//
//    public void setGene(Gene gene)
//    {
//      if(_myGene != null)
//      {
//        // clear out the old data
//      }
//      _myGene = gene;
//      setText("val:" + _myGene.toString());
//    }
//
//    public void updateMe()
//    {
//      setText(_myGene.toString());
//    }
//
//    public String toString()
//    {
//      return _myGene.toString();
//    }
//  }


  /***************************************************************
   *  run this class
   ***************************************************************/
  public static void main(String[] args)
  {
    final FactoryGUI scf = new FactoryGUI();
    final JFrame fr = new JFrame("Factory");
    fr.setSize(800, 600);
    fr.getContentPane().setLayout(new BorderLayout());
    fr.getContentPane().add("Center", scf);
    fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    fr.setVisible(true);

//    scf._myFactory.loadObserver("d:\\dev\\asset\\asset2_out\\datum_factory_control.xml");
//    scf._myFactory.setScenarioFile("d:\\dev\\asset\\asset2_out\\datum_factory_scenario.xml");
//    scf._myFactory.setVariablesFile("d:\\dev\\asset\\asset2_out\\datum_factory_variables.xml");

  }
}
