package org.mwc.debrief.lite;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.jdesktop.application.Application;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXFindPanel;

/**
 * A demo for the {@code JXCollapsiblePane}.
 *
 * @author Karl George Schaefer
 */
@SuppressWarnings("serial")
public class CollapsiblePaneDemo extends JPanel {
    private JXCollapsiblePane collapsiblePane;
    private CardLayout containerStack;
    private JButton previousButton;
    private JButton collapsingButton;
    private JButton nextButton;
    
    /**
     * main method allows us to run as a standalone demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("");
                
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new CollapsiblePaneDemo());
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    public CollapsiblePaneDemo() {
        createCollapsiblePaneDemo();
        
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
        
        bind();
    }
    
    private void createCollapsiblePaneDemo() {
        setLayout(new BorderLayout());
        
        collapsiblePane = new JXCollapsiblePane();
        collapsiblePane.setName("collapsiblePane");
        add(collapsiblePane, BorderLayout.NORTH);
        
        containerStack = new CardLayout();
        collapsiblePane.setLayout(containerStack);
        collapsiblePane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        
        collapsiblePane.add(new JTree(), "");
        collapsiblePane.add(new JTable(4, 4), "");
        collapsiblePane.add(new JXFindPanel(), "");
        
        add(new JLabel("Main Content Goes Here", JLabel.CENTER));
        
        JPanel buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        
        previousButton = new JButton();
        previousButton.setName("previousButton");
        buttonPanel.add(previousButton);
        
        collapsingButton = new JButton();
        collapsingButton.setName("toggleButton");
        buttonPanel.add(collapsingButton);
        
        nextButton = new JButton();
        nextButton.setName("nextButton");
        buttonPanel.add(nextButton);
    }
    
    private void bind() {
        collapsingButton.addActionListener(collapsiblePane.getActionMap().get(
                JXCollapsiblePane.TOGGLE_ACTION));
        
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                containerStack.next(collapsiblePane.getContentPane());
            }
        });
        
        previousButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                containerStack.previous(collapsiblePane.getContentPane());
            }
        });
    }
}