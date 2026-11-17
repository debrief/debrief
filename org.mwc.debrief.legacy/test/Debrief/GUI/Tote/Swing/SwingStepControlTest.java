/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package Debrief.GUI.Tote.Swing;

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.GUI.Tote.Swing.SwingStepControl.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import Debrief.GUI.Tote.Swing.SwingStepControl.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import Debrief.GUI.Tote.StepControl;
import Debrief.GUI.Tote.Painters.PainterManager;
import Debrief.Wrappers.LabelWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.StepperListener;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;
import MWC.GUI.Tools.Swing.MyMetalToolBarUI;
import MWC.GUI.Tools.Swing.RepeaterButton;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class SwingStepControlTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testStepperFormatting() {

		// create some dummy data so the panel has start/stop times
		final Layers layers = new Layers();
		final BaseLayer base = new BaseLayer();
		base.setName("Ian's layer");
		layers.addThisLayer(base);
		final LabelWrapper label = new LabelWrapper("my label", new WorldLocation(12, 12, 100), Color.red,
				new HiResDate(120000000), new HiResDate(150000000));
		base.add(label);

		// ok, create a new SwingStepControl
		final SwingStepControl ssc = new SwingStepControl(null, layers, null, null, null, null);

		// check this is valid
		final MWC.GUI.Properties.DateFormatPropertyEditor pe = new MyDateEditor();

		final String[] tags = pe.getTags();

		// check we found tags
		assertNotNull(tags, "tags got found from property editor");
		assertEquals(7, tags.length, "tags found from property editor");

		// and ditch the stuff
		ssc.closeMe();

	}
}
