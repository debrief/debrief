/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: 17-Mar-02
 * Time: 10:03:59
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.RMI.Server;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ASSET.ParticipantType;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.Status;
import ASSET.Server.CoreServer;
import MWC.GenericData.WorldSpeed;

public class ServerApp
{
  static CoreServer srv = null;
  static ASSET.ScenarioType scen = null;


  public static void main(String[] args)
  {
    System.out.println("starting");
    srv = new CoreServer();
    try
    {
      new ServerImpl(srv);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    System.out.println("finished");
    final JFrame fr = new JFrame();
    final JPanel panel = new JPanel();
    fr.getContentPane().add(panel);
    fr.setSize(300,300);

    panel.setLayout(new GridLayout(0,1));
    final JButton add = new JButton("Add");
    add.addActionListener(new ActionListener()
      {
      public void actionPerformed(ActionEvent e)
      {
        final int id = srv.createNewScenario("");
        scen = srv.getThisScenario(id);
      }
    });
    panel.add(add);

    final JButton populate = new JButton("Populate");
    populate.addActionListener(new ActionListener()
      {
      public void actionPerformed(ActionEvent e)
      {
        final int index = scen.createNewParticipant(ASSET.Participants.Category.Type.SUBMARINE);
        final ParticipantType part = scen.getThisParticipant(index);
        final Status stat = new Status(22, 0);
        stat.setLocation(new MWC.GenericData.WorldLocation(2,2,2));
        stat.setCourse(22);
        stat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
        final SimpleDemandedStatus demStat = new SimpleDemandedStatus(33, stat);
        demStat.setCourse(33);
        part.setStatus(stat);
        part.setDemandedStatus(demStat);
      }
    });
    panel.add(populate);

    final JButton step = new JButton("Step");
    step.addActionListener(new ActionListener()
      {
      public void actionPerformed(ActionEvent e)
      {
        scen.step();
      }
    });
    panel.add(step);



    final JButton quit = new JButton("Quit");
    quit.addActionListener(new ActionListener()
      {
      public void actionPerformed(ActionEvent e)
      {
        System.exit(0);
      }
    });
    panel.add(quit);


    fr.doLayout();
    fr.setVisible(true);

  }
}
