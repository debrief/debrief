package ASSET.GUI.Editors;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Mediums.BroadbandRadNoise;
import ASSET.Models.Mediums.Optic;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
import MWC.GUI.PlainChart;
import MWC.GUI.Properties.PlainPropertyEditor;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;
import MWC.GUI.Properties.Swing.SwingPropertyEditor2;
import MWC.GUI.ToolParent;
import MWC.GenericData.WorldDistance;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.Collection;
import java.util.Iterator;

public class RadiatedNoiseViewer extends MWC.GUI.Properties.Swing.SwingCustomEditor
  implements MWC.GUI.Properties.NoEditorButtons,
  PlainPropertyEditor.EditorUsesChart,
  PlainPropertyEditor.EditorUsesPropertyPanel,
  PlainPropertyEditor.EditorUsesToolParent
{
  /**
   * the border
   */
  final BorderLayout mainBorder = new BorderLayout();

  /**
   * the list of radiated noise types
   */
  private JList _noiseList;

  /**
   * the radiated noise characeristics we are viewing
   */
  private RadiatedCharacteristics _chars;

  /**
   * panel to contain editors themselves
   */
  private JPanel _editors;

  /**
   * the table with the properties
   */
  private SwingPropertyEditor2 spe = null;

  /**
   * the parent
   */
  private ToolParent _theParent;


  /**
   * *************************************************
   * constructor
   * *************************************************
   */
  public RadiatedNoiseViewer()
  {
    initForm();
  }

  public void setChart(final PlainChart theChart)
  {
    _theChart = theChart;
  }

  public void setPanel(final PropertiesPanel thePanel)
  {
    _thePanel = thePanel;
  }

  public void setParent(final ToolParent theParent)
  {
    _theParent = theParent;
  }

  private void initForm()
  {
    // layout
    this.setLayout(new GridLayout(0, 1));

    // create the list
    _noiseList = new JList();
    _noiseList.setName("List of noise mediums");

    // update the list
    updateList();

    // collate the form
    this.add(_noiseList);

    // sort out the editors
    _editors = new JPanel();
    _editors.setLayout(new GridLayout(1, 0));
    this.add(_editors);

    // listen out for any change
    _noiseList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
    {
      /**
       * Called whenever the value of the selection changes.
       *
       * @param e the event that characterizes the change.
       */
      public void valueChanged(final ListSelectionEvent e)
      {
        if (!e.getValueIsAdjusting())
        {
          itemSelected();
        }
      }
    });


  }


  /**
   * *************************************************
   * support for a noise source being selected
   * *************************************************
   */
  private void itemSelected()
  {
    final Object newOne = _noiseList.getSelectedValue();
    if (newOne instanceof MWC.GUI.Editable)
    {
      final MWC.GUI.Editable ed = (MWC.GUI.Editable) newOne;
      if (ed.hasEditor())
      {
        // do we currently have one open?
        if (spe != null)
          spe.close();

        // empty the list
        _editors.removeAll();

        this.doLayout();
        this.repaint();

        // create the new editor
        spe = new SwingPropertyEditor2(ed.getInfo(), (SwingPropertiesPanel) _thePanel, _theChart, _theParent, null);

        // put into the panel
        _editors.add(spe.getPanel());

        this.doLayout();
        this.repaint();
      }
    }
  }

  /**
   * update the list of mediums
   */
  private void updateList()
  {
    if (_chars != null)
    {
      final DefaultListModel newList = new javax.swing.DefaultListModel();

      final Collection coll = _chars.getMediums();
      final Iterator iter = coll.iterator();
      while (iter.hasNext())
      {
        final RadiatedCharacteristics.Medium thisMed =  (RadiatedCharacteristics.Medium) iter.next();
        newList.addElement(thisMed);
      }

      // and add to list
      _noiseList.setModel(newList);
    }
  }


  /**
   * *************************************************
   * update the object we are editing
   * *************************************************
   */
  public void setObject(final Object data)
  {
    // this is our vessel, start listening to it
    if (data instanceof RadiatedCharacteristics)
    {
      // store it
      _chars = (RadiatedCharacteristics) data;

      // and update it
      updateList();
    }
  }

  /**
   * handle close event
   */
  public void doClose()
  {
    // do the parent bit
    super.doClose();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  public static class ViewerTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public ViewerTest(final String val)
    {
      super(val);
    }

    boolean set = false;

    public void testMe()
    {
      // create the object
      final RadiatedNoiseViewer dv = new RadiatedNoiseViewer();

      set = false;
      final ASSET.Models.Vessels.SSN cp = new ASSET.Models.Vessels.SSN(12);

      RadiatedCharacteristics rc = new RadiatedCharacteristics();
      rc.add(1, new Optic(12, new WorldDistance(12, WorldDistance.METRES)));
      rc.add(3, new BroadbandRadNoise(23));
      cp.setRadiatedChars(rc);

      // store the radiated chars
      dv.setObject(cp.getRadiatedChars());

      assertEquals("our data stored", dv._chars, cp.getRadiatedChars());

      // check data loaded
      assertEquals("list shows our data", dv._chars.getMediums().size(), 2);

      // check gui control created
      assertEquals("form correctly laid out", dv._noiseList.getName(), "List of noise mediums");

      // close down
      dv.doClose();


    }
  }
}