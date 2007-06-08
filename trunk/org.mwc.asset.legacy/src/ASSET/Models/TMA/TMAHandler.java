package ASSET.Models.TMA;

import ASSET.Participants.CoreParticipant;
import ASSET.Participants.Status;
import ASSET.Participants.DemandedStatus;
import ASSET.Models.Vessels.SSN;
import ASSET.Models.Vessels.SSK;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Scenario.CoreScenario;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.GenericData.WorldSpeed;
import MWC.Algorithms.Conversions;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Vector;

/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: 13-Mar-02
 * Time: 20:53:40
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */

public class TMAHandler
{

  static {
    System.load("d:\\dev\\asset\\lib\\tma.dll");
  }


  private static java.io.FileWriter fo = null;

  static private java.text.DecimalFormat df = new java.text.DecimalFormat("0000.0000");
  static private java.text.DecimalFormat df2 = new java.text.DecimalFormat("0.00000E00");

  public native void displayHelloWorld();
  public native double newCalc(double val);
  public native double B180(double val);
  public native int sumArray(int[] arr);
  public static native double sumDArray(double[] arr);

  /*****************************************************************
   * routines from TMA_DLL
   ****************************************************************/

  public static native double SetControlParameters(double SpeedOfSound, double RangePrior,
                  double SpeedPrior, double RangePriorSigma,
									double CoursePriorSigma,double SpeedPriorSigma, double OutOfContactTime,
									double ManoeuvreDetectionSensitivity, double CorrelationBetweenSolutions,
									double MinInfoMatrixDeterminant, double SolutionDegradationFactor);

  public static native double InitialiseNonFreq(double[] Target,double[] Sensor, double[] TrueTarget,double[] Sol, double[] Cov,
								   double[] PolarCov, double B, double SigB, double R, double SigR, double RN[],
								   double ThisUpdateTime, double[] LastUpdateTime, double[] InfMat, int TMASeed);

  public static native double Update(double[] LastUpdateTime, double[] InfMat, double[] Sensor,
					   double[] TrueTarget, double[] Target, double ThisUpdateTime,
					   double B, double SigB, double F, double SigF, double R, double SigR,
					   double C, double SigC, double S, double SigS,
					   double[] RN, double[] PolarCov, double[] CartCov,
					   int[] Singular, double[] Sol, int[] Manoeuvre);

  public static native double TUACalcs(double ThisTime, double LastTime, double[] Ownship,
                                     double []Solution, double[] CartCov,
                                     double[] PolSolution, double[] Sigs, double[] Axis1, double[] Axis2,
                                     double[] Orientation, double[] Bdot, double[] Rdot);


//  public static void main(String[] args)
//  {
//    final dtestWrapper tw = new dtestWrapper("scrap");
//    tw.testIainState();
//  }


//    TMAHandler tm = new TMAHandler();
//    System.out.println("working");
//    System.out.println("res:" + tm.B180(-0.9));
//    int[] val = new int[]{33, 44, 55};
//    System.out.println("sum:" + tm.sumArray(val));
//    double[] dVal = new double[]{33, 44, 55};
//    System.out.println("sum:" + sumDArray(dVal));
//    System.out.println("new val:" + dVal[0]);
// //   System.out.println("val is:" + val[0]);
//
//    boolean firstStep = true;
//
//    // create sensor
//
//    // create target
//
//    // initialise TMA
//
//    // looping through
//
//      // first step?
//      if(firstStep)
//      {
//        firstStep = false;
//
//        // initialise non frequency component
//        // InitialiseNonFreq
//      }
//
//      // move sensor
//
//      // move target
//
//      // do update
//      // Update
//
//      // get GUI parts
//      // TUACalcs
 // }

  static class ParticipantWrapper
  {
    /** the participant we are wrapping
     *
     */
    CoreParticipant _myPart = null;

    /** the origin for this scenario
     *
     */
    WorldLocation _origin = null;

    /** constructor
     *
     */
    public ParticipantWrapper(final CoreParticipant part, final WorldLocation origin)
    {
      _myPart = part;
      _origin = origin;
    }


    public String toString()
    {
      final double[] val = state();
      final String res = "x:" + df.format(val[0]) + " y:" + df.format(val[1])
              + " dx:" + df.format(val[2]) + " dy:" + df.format(val[3]);
      return res;
    }

    /** get the state of this participant
     *
     */
    public double[] state()
    {
      // produce an array of the current state of this object
      // get x and y from origin
      final WorldVector dist = _myPart.getStatus().getLocation().subtract(_origin);

      final double rng = MWC.Algorithms.Conversions.Degs2m(dist.getRange());
      double brg = dist.getBearing();

      final double x = rng * Math.sin(brg);
      final double y = rng * Math.cos(brg);

      final double spd_myps = _myPart.getStatus().getSpeed().getValueIn(WorldSpeed.M_sec);
      brg = MWC.Algorithms.Conversions.Degs2Rads(_myPart.getStatus().getCourse());

      final double dx = spd_myps * Math.sin(brg);
      final double dy = spd_myps * Math.cos(brg);

      final double[] res = new double[4];
      res[0] = x;
      res[1] = y;
      res[2] = dx;
      res[3] = dy;

      return res;
    }

    /** get the 5 element state for this participant
     *
     */
    public double[] state5element()
    {
      final double[] res = new double[5];
      final double[] state = state();
      System.arraycopy(state, 0, res, 0, 4);
      res[4] = 300;
      return res;
    }

  }

  /*****************************************************************
   * store the details for a single track held by a single participant
   ****************************************************************/
  static class SingleTrack
  {
    public final double[] target = new double[5];
    public final double[] sol = new double[5];
    public final double[] rn = new double[5];
    public final double[] infoMat = new double[25];
    public final double[] polarCov = new double[25];
    public final double[] cartCov = new double[25];
    public final int[] singular = new int[1];
    public final int[] manoeuvre = new int[1];
    public final double[] tua = new double[3];

    public String toString()
    {
      return "x:" + df.format(sol[0]) +
                                 "  y:" + df.format(sol[1]) +
                                 " dx:" + df.format(sol[2]) +
                                 " dy:" + df.format(sol[3]) +
                                 " hz:" + df.format(sol[4]);
    }
  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
//  public static class dtestWrapper extends junit.framework.TestCase
//  {
//
//    // todo - implement this testing, and change class name back from dTest to test,
//    // todo - so that it gets caught up in the automated builds
//    static public final String TEST_ALL_TEST_TYPE  = "DEVELOPMENT";
//    public dtestWrapper(final String val)
//    {
//      super(val);
//    }
//
//
//    public void testIainState()
//    {
//
//      // put ourselves on a flat earth
//      MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.FlatEarth());
//
//      // scenario
//      final CoreScenario scenario = new CoreScenario();
//      scenario.setTime(0);
//      scenario.setScenarioStepTime(4000);
//      final WorldLocation origin = new WorldLocation(0,0,0);
//
//      // the track
//      final SingleTrack track = new SingleTrack();
//
//      // ssn
//      final Status status = new Status(33, 0);
//      status.setCourse(90);
//      status.setSpeed(new WorldSpeed(8, WorldSpeed.M_sec));
//      status.setLocation(origin);
//      final SimpleDemandedStatus demStatus = new SimpleDemandedStatus(1200, status);
//      final SSN ssn = new SSN(12, status, demStatus, "TRAF");
//      scenario.addParticipant(12, ssn);
//      final ParticipantWrapper ssn_wrapper = new ParticipantWrapper(ssn, origin);
//
//      // ssk
//      final Status status2 = new Status(33, 0);
//      status2.setCourse(210);
//      status2.setSpeed(new WorldSpeed(6, WorldSpeed.M_sec));
//      status2.setLocation(new WorldLocation(origin.getLat() + MWC.Algorithms.Conversions.m2Degs(-6858),
//                                            origin.getLong() + MWC.Algorithms.Conversions.m2Degs(11878.4), 0));
//      final SimpleDemandedStatus demStatus2 = new SimpleDemandedStatus(1200, status2);
//      final SSK ssk = new SSK(15, status2, demStatus2, "KILO");
//      scenario.addParticipant(15, ssk);
//      final ParticipantWrapper ssk_wrapper = new ParticipantWrapper(ssk, origin);
//
//      // initialise
//      setControl();
//
//      long newTime;
//      long oldTime;
//      oldTime = newTime  = scenario.getTime();
//
//      final Vector res = new Vector(0,1);
//
//      try
//      {
//        fo = new java.io.FileWriter("c:\\asset.rep");
//      }
//      catch (Exception e)
//      {
//        System.out.println("failed to open file!");
//      }
//
//      // try the init
//      init(ssn_wrapper, ssk_wrapper, track, scenario.getTime());
//
//      final StringBuffer msg = new StringBuffer();
//
//      final int len = 600;
//      System.out.println();
//      System.out.println("Time\tTrue Tgt\tAss Tgt\tOwnship\tXY Sol\tTrue Pol\tRBSCF");
//      for(int i=0;i<len;i++)
//      {
//
//        // do any necessary manoeuvres
//        if(i == 90)
//        {
//          // manoeuvre ownship
//
//          final SimpleDemandedStatus demandedStatus = (SimpleDemandedStatus) ssn_wrapper._myPart.getDemandedStatus();
//          demandedStatus.setCourse(180);
//        }
//
//        if(i == 205)
//        {
//          // manoeuvre ownship
//          final SimpleDemandedStatus demandedStatus = (SimpleDemandedStatus) ssn_wrapper._myPart.getDemandedStatus();
//          demandedStatus.setCourse(130);
//        }
//
//        // do the step, and calculate the results
//        doStep(ssn_wrapper, ssk_wrapper, track, newTime, oldTime);
//
//
//        /////////////////////////////////////////
//        // output combined state matrix
////        for(int t=0; t<4;t++)
////        {
////          if(t == 0)
////            msg.append(newTime / 1000 + "\t");
////          else
////            msg.append(" " + "\t");
////
////          msg.append(ssk_wrapper.state()[t] + "\t");
////          msg.append(track.target[t] + "\t");
////          msg.append(ssn_wrapper.state()[t] + "\t");
////          msg.append(track.sol[t] + "\t");
////          if(t<3)
////            msg.append(track.tua[t] + "\t");
////
////          msg.append(System.getProperty("line.separator"));
////        }
////        msg.append(System.getProperty("line.separator"));
//        ///////////////////////////////////////////////////////////
//
//
//
//        // remember the old time
//        oldTime = newTime;
//
//        final double[] newSol = new double[5];
//        System.arraycopy(track.sol, 0, newSol, 0, 5);
//        final double[] newTrueSol = new double[5];
//        System.arraycopy(track.target, 0, newTrueSol, 0, 5);
//        final double[] newPolarSol = new double[5];
//        System.arraycopy(track.polarCov, 0, newPolarSol, 0, 5);
//
//        // output the information matrix
////        msg.append(System.getProperty("line.separator"));
////        for(int m=0;m<5;m++)
////        {
////          if(m == 0)
////            msg.append(newTime / 1000 + "\t");
////          else
////            msg.append("\t");
////
////          for(int n=0;n<5;n++)
////          {
////             msg.append(track.cartCov[m*5+n] + "\t ");
////          }
////          msg.append(System.getProperty("line.separator"));
////        }
//
//
//
//        final Object[] thisSol = new Object[]{ ssn_wrapper.state(),
//                                         ssk_wrapper.state(),
//                                         newSol,
//                                         newTrueSol,
//                                         new Long(oldTime),
//                                         newPolarSol
//            };
//        res.add(thisSol);
//
//        // collate the data to output
//        String str = "";
//
//        try
//        {
//          fo.write(writeReplay(newTime, str, "target", "@A", ssk_wrapper));
//          fo.write(System.getProperty("line.separator"));
//          str = "";
//          fo.write(writeReplay(newTime, str, "sensor", "@B", ssn_wrapper));
//          fo.write(System.getProperty("line.separator"));
//        }
//        catch (IOException e)
//        {
//          e.printStackTrace();
//        }
//
////        if(i == 82)
////          ssk_wrapper._myPart.getDemandedStatus().setCourse(344);
//
//        // lastly move the scenario forward
//        scenario.step();
//        newTime = scenario.getTime();
//      }
//
//
//      // put our diagnostic string on the clipboard
////      java.awt.datatransfer.Clipboard cb =
////        java.awt.Toolkit.getDefaultToolkit().
////        getSystemClipboard();
////      java.awt.datatransfer.StringSelection contents =
////        new java.awt.datatransfer.StringSelection(msg);
////      cb.setContents(contents, null);
//      System.out.println(msg);
//
//      try
//      {
//        fo.close();
//      }
//      catch (IOException e)
//      {
//        e.printStackTrace();
//      }
//
//      // ok, now run through the results
//      for (int thisStep = 0; thisStep < res.size(); thisStep++)
//      {
//        final Object[] objects = (Object[]) res.elementAt(thisStep);
//        final double[] sensor = (double[])objects[0];
//        final double[] tgt = (double[])objects[1];
//        final double[] sol = (double[])objects[2];
//        final double[] trueSol = (double[])objects[3];
//        final long time = ((Long)objects[4]).longValue();
//        final double[] polarSol = (double[])objects[5];
////        System.out.println("time:" + time + ", sx:" + df.format(sensor[0])
////                           + ", sy:" + df.format(sensor[1]) + ", tx:" + df.format(tgt[0])
////                           + ", ty:" + df.format(tgt[1]) + ", solx:"
////                           + df.format(sol[0]) + ", soly:" + df.format(sol[1]) +
////        ", p0:" + df.format(polarSol[0]) + ", p1:" + df.format(polarSol[1]) + ", p2:" + df.format(polarSol[2]) +
////        ", p3:" + df.format(polarSol[3]) + ", p4:" + df.format(polarSol[4])
////        );
//      }
//
//
//
//    }
//
//
//
//    public void testState()
//    {
//
//      // put ourselves on a flat earth
//      MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.FlatEarth());
//
//      // scenario
//      final CoreScenario scenario = new CoreScenario();
//      scenario.setTime(0);
//      scenario.setScenarioStepTime(4000);
//      final WorldLocation origin = new WorldLocation(0,0,0);
//
//      // the track
//      final SingleTrack track = new SingleTrack();
//
//      // ssn
//      final Status status = new Status(33, 0);
//      status.setCourse(27);
//      status.setSpeed(new WorldSpeed(7, WorldSpeed.M_sec));
//      status.setLocation(origin);
//      final SimpleDemandedStatus demStatus = new SimpleDemandedStatus(1200, status);
//      final SSN ssn = new SSN(12, status, demStatus, "TRAF");
//      scenario.addParticipant(12, ssn);
//      final ParticipantWrapper ssn_wrapper = new ParticipantWrapper(ssn, origin);
//
//      // ssk
//      final Status status2 = new Status(33, 0);
//      status2.setCourse(2);
//      status2.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));
//      status2.setLocation(
//              origin.add(
//                      new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90),
//                                      MWC.Algorithms.Conversions.Yds2Degs(400), 0)));
//      final SimpleDemandedStatus demStatus2 = new SimpleDemandedStatus(1200, status2);
//      final SSK ssk = new SSK(15, status2, demStatus2, "KILO");
//      scenario.addParticipant(15, ssk);
//      final ParticipantWrapper ssk_wrapper = new ParticipantWrapper(ssk, origin);
//
//      // initialise
//      setControl();
//
//      long newTime;
//      long oldTime;
//      oldTime = newTime  = scenario.getTime();
//
//      final Vector res = new Vector(0,1);
//
//      try
//      {
//        fo = new java.io.FileWriter("c:\\asset.rep");
//      }
//      catch (Exception e)
//      {
//        System.out.println("failed to open file!");
//      }
//
//      // try the init
//      init(ssn_wrapper, ssk_wrapper, track, scenario.getTime());
//
//      final StringBuffer msg = new StringBuffer();
//
//      final int len = 11;
//      System.out.println();
//      System.out.println("Time\tTrue Tgt\tAss Tgt\tOwnship\tXY Sol\tTrue Pol\tRBSCF");
//      for(int i=0;i<len;i++)
//      {
//        // do the step, and calculate the results
//        doStep(ssn_wrapper, ssk_wrapper, track, newTime, oldTime);
//
//
//        /////////////////////////////////////////
//        // output combined state matrix
////        for(int t=0; t<4;t++)
////        {
////          if(t == 0)
////            msg.append(newTime / 1000 + "\t");
////          else
////            msg.append(" " + "\t");
////
////          msg.append(ssk_wrapper.state()[t] + "\t");
////          msg.append(track.target[t] + "\t");
////          msg.append(ssn_wrapper.state()[t] + "\t");
////          msg.append(track.sol[t] + "\t");
////          if(t<3)
////            msg.append(track.tua[t] + "\t");
////
////          msg.append(System.getProperty("line.separator"));
////        }
////        msg.append(System.getProperty("line.separator"));
//        ///////////////////////////////////////////////////////////
//
//
//        // remember the old time
//        oldTime = newTime;
//
//        final double[] newSol = new double[5];
//        System.arraycopy(track.sol, 0, newSol, 0, 5);
//        final double[] newTrueSol = new double[5];
//        System.arraycopy(track.target, 0, newTrueSol, 0, 5);
//        final double[] newPolarSol = new double[5];
//        System.arraycopy(track.polarCov, 0, newPolarSol, 0, 5);
//
//        // output the information matrix
////        msg.append(System.getProperty("line.separator"));
////        for(int m=0;m<5;m++)
////        {
////          if(m == 0)
////            msg.append(newTime / 1000 + "\t");
////          else
////            msg.append("\t");
////
////          for(int n=0;n<5;n++)
////          {
////             msg.append(track.cartCov[m*5+n] + "\t ");
////          }
////          msg.append(System.getProperty("line.separator"));
////        }
//
//
//
//        final Object[] thisSol = new Object[]{ ssn_wrapper.state(),
//                                         ssk_wrapper.state(),
//                                         newSol,
//                                         newTrueSol,
//                                         new Long(oldTime),
//                                         newPolarSol
//            };
//        res.add(thisSol);
//
//        // collate the data to output
//        String str = "";
//
//        try
//        {
//          fo.write(writeReplay(newTime, str, "target", "@A", ssk_wrapper));
//          fo.write(System.getProperty("line.separator"));
//          str = "";
//          fo.write(writeReplay(newTime, str, "sensor", "@B", ssn_wrapper));
//          fo.write(System.getProperty("line.separator"));
//        }
//        catch (IOException e)
//        {
//          e.printStackTrace();
//        }
//
////        if(i == 82)
////          ssk_wrapper._myPart.getDemandedStatus().setCourse(344);
//
//        // lastly move the scenario forward
//        scenario.step();
//        newTime = scenario.getTime();
//      }
//
//
//      // put our diagnostic string on the clipboard
////      java.awt.datatransfer.Clipboard cb =
////        java.awt.Toolkit.getDefaultToolkit().
////        getSystemClipboard();
////      java.awt.datatransfer.StringSelection contents =
////        new java.awt.datatransfer.StringSelection(msg);
////      cb.setContents(contents, null);
//      System.out.println(msg);
//
//      try
//      {
//        fo.close();
//      }
//      catch (IOException e)
//      {
//        e.printStackTrace();
//      }
//
//      // ok, now run through the results
//      for (int thisStep = 0; thisStep < res.size(); thisStep++)
//      {
//        final Object[] objects = (Object[]) res.elementAt(thisStep);
//        final double[] sensor = (double[])objects[0];
//        final double[] tgt = (double[])objects[1];
//        final double[] sol = (double[])objects[2];
//        final double[] trueSol = (double[])objects[3];
//        final long time = ((Long)objects[4]).longValue();
//        final double[] polarSol = (double[])objects[5];
////        System.out.println("time:" + time + ", sx:" + df.format(sensor[0])
////                           + ", sy:" + df.format(sensor[1]) + ", tx:" + df.format(tgt[0])
////                           + ", ty:" + df.format(tgt[1]) + ", solx:"
////                           + df.format(sol[0]) + ", soly:" + df.format(sol[1]) +
////        ", p0:" + df.format(polarSol[0]) + ", p1:" + df.format(polarSol[1]) + ", p2:" + df.format(polarSol[2]) +
////        ", p3:" + df.format(polarSol[3]) + ", p4:" + df.format(polarSol[4])
////        );
//      }
//
//
//
//    }
//
//    private String writeReplay(final long newTime, String str, final String name, final String sym, final ParticipantWrapper ssk_wrapper)
//    {
//      str += MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(newTime);
//      str += " " + name + "  " + sym + " ";
//      str += MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(ssk_wrapper._myPart.getStatus().getLocation());
//      str += " " ;
//      str += MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(ssk_wrapper._myPart.getStatus().getCourse());
//      str += " " ;
//      str += MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(ssk_wrapper._myPart.getStatus().getSpeed().getValueIn(WorldSpeed.M_sec));
//      str += " " ;
//      str += MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(ssk_wrapper._myPart.getStatus().getLocation().getDepth());
//      str += " " ;
//      return str;
//    }
//
//    private void doStep(final ParticipantWrapper sensor, final ParticipantWrapper target,
//                        final SingleTrack track,
//                        final long newTime,
//                        final long oldTime)
//    {
//      update(sensor, target, track, newTime, oldTime);
//    }
//
//    static final double SIG_B = MWC.Algorithms.Conversions.Degs2Rads(0.5);
//    static final double SIG_F = -1;
//    static final double SIG_C = -1;
//    static final double SIG_S = -1;
//    static final double SIG_R = -1;
////    static final double SIG_C = 60;
////    static final double SIG_S = 10;
////    static final double SIG_R = 9144;
//
//
//    static void update(final ParticipantWrapper sensor,
//                                 final ParticipantWrapper target,
//                                 final SingleTrack track,
//                                 long time,
//                                 long lastTime)
//    {
//      final WorldVector sep = target._myPart.getStatus().getLocation().subtract(sensor._myPart.getStatus().getLocation());
//      final double brg = sep.getBearing();
//      final double brgMeasured = brg - SIG_B + ASSET.Util.RandomGenerator.nextRandom() * SIG_B * 2;
//      final double rng = MWC.Algorithms.Conversions.Degs2m(sep.getRange());
//      final double course = MWC.Algorithms.Conversions.Degs2Rads(target._myPart.getStatus().getCourse());
//      final double speed = target._myPart.getStatus().getSpeed().getValueIn(WorldSpeed.M_sec);
//      time /= 1000;
//      lastTime /= 1000;
//      double res = 4;
//      final double[] lastUpdateTime = new double[]{lastTime};
//
////      System.out.println("=========    Update     =============");
////      System.out.println("  Sensor: " + sensor);
////      System.out.println("  Target: " + target);
////      System.out.println("   input: " + "singular: " + track.singular[0] + " manoeuvre:" + track.manoeuvre[0] + " new time:" + time + " last time:" + lastUpdateTime[0]);
////      System.out.println("     Cut: brg:" + df.format(brg) + " (" + df.format(Math.toDegrees(brg)) + " degs)"
////                            + " meas brg:" + df.format(brgMeasured) + " (" + df.format(Math.toDegrees(brgMeasured)) + " degs)"
////                            + " rng:" + df.format(rng)
////                            + " crse:" + df.format(course) + " (" + df.format(Math.toDegrees(course)) + " degs)"
////                            + " spd:" + df.format(speed));
//
//
// //     System.out.println("polar[0] before update" + track.polarCov[0]);
//
//
//      res = TMAHandler.Update(lastUpdateTime, track.infoMat, sensor.state(),
//                 target.state5element(), track.target, time,
//                 brg, SIG_B, -1, SIG_F,
////                 rng, SIG_R,
//                 0, -1,
//                 course, SIG_C, speed, SIG_S,
////                 0, SIG_C, 0, SIG_S,
//                 track.rn, track.polarCov, track.cartCov,
//                 track.singular, track.sol, track.manoeuvre);
//
//      // try to do TUA calcs
//      TUACalcs(sensor, target, track, time, lastTime);
//
//
//      System.out.println("--output: " + "singular: " + track.singular[0] + " manoeuvre:" + track.manoeuvre[0] + " new time:||  " + time + "  || last time:" + lastUpdateTime[0]);
//      System.out.println("  Target: " + "x:" + df.format(track.target[0]) + " y:" + df.format(track.target[1])
//                         + " dx:" + df.format(track.target[2]) + " dy:" + df.format(track.target[3]));
//      System.out.println(" Ownship:" + sensor);
//      System.out.println("Solution: " + track + "||");
//
//      // write the information matrix
//      for(int k=0;k<5;k++)
//      {
//        for(int l=0;l<5;l++)
//        {
//          System.out.print(" " + df2.format(track.polarCov[k * 5 + l]));
//        }
//        System.out.println("");
//      }
//      System.out.println("=========================================");
//
//
//    }
//
//    static void init(final ParticipantWrapper sensor,
//                               final ParticipantWrapper target,
//                               final SingleTrack track,
//                               long time)
//    {
//
//      final WorldVector sep = target._myPart.getStatus().getLocation().subtract(sensor._myPart.getStatus().getLocation());
//      final double brg = sep.getBearing();
//      final double rng = MWC.Algorithms.Conversions.Degs2m(sep.getRange());
//      final double[] sensor4 = sensor.state();
//      final double[] target5 = target.state5element();
//      final double[] lastUpdateTime =  new double[1];
//      // convert to millis
//      time /= 1000;
//      final int seed = 3;
//
//
//      final double res = TMAHandler.InitialiseNonFreq( track.target, sensor4, target5,
//                                                track.sol, track.cartCov, track.polarCov,
//                                                brg, SIG_B,
//                                                0, -1,
//                                                //rng, SIG_R,
//                                                track.rn, time, lastUpdateTime, track.infoMat, seed);
//
////      System.out.println("========= Init Non Freq =============");
////      System.out.println("  Sensor: " + sensor);
////      System.out.println("  Target: " + target);
////      System.out.println("     Cut: brg:" + df.format(brg) + " (" + df.format(Math.toDegrees(brg)) + " degs)"
////                            + " rng:" + df.format(rng));
////      System.out.println("Solution: " + track);
//
//    }
//
//
//    static void setControl()
//    {
//      final double SpeedOfSound = -1;
//      final double RangePrior = -1;
//      final double SpeedPrior = -1;
//      final double RangePriorSigma = -1;
//      final double CoursePriorSigma = -1;
//      final double SpeedPriorSigma = -1;
//      final double OutOfContactTime = -1;
//      final double ManoeuvreDetectionSensitivity = -1;
//      final double CorrelationBetweenSolutions = -1;
//      final double MinInfoMatrixDeterminant = -1;
//      final double SolutionDegradationFactor = -1;
//
//      SetControlParameters(SpeedOfSound, RangePrior, SpeedPrior, RangePriorSigma,
//									CoursePriorSigma,SpeedPriorSigma, OutOfContactTime,
//									ManoeuvreDetectionSensitivity, CorrelationBetweenSolutions,
//									MinInfoMatrixDeterminant, SolutionDegradationFactor);
//    }
//
//    static void TUACalcs(final ParticipantWrapper sensor,
//                               ParticipantWrapper target,
//                               final SingleTrack track,
//                               final long time, final long lastTime)
//    {
//      final double[] Axis1 = new double[1];
//      final double[] Axis2 = new double[1];
//      final double[] Orientation = new double[1];
//      final double[] BDot = new double[1];
//      final double[] RDot = new double[1];
//      final double[] sigmas = new double[5];
//
//      final double[] polSolution = new double[5];
//
//      double res = 1;
//
//      res = TMAHandler.TUACalcs(time, lastTime, sensor.state(),
//                          track.sol, track.cartCov,
//                          polSolution, sigmas, Axis1, Axis2, Orientation,
//                          BDot, RDot);
//
////      System.out.println("Sol returned, axis1:" + Axis1[0] + " axis2:" +
////        Axis2[0] + " orient:" + Orientation[0] + " rng:" +
////        polSolution[0] + " brgs:" + polSolution[1]);
//
//      track.tua[0] = Axis1[0];
//      track.tua[1] = Axis2[0];
//      track.tua[2] = Math.toDegrees(Orientation[0]);
//
//      // ok, output our solution
//
////      ;ELLIPSE: BD YYMMDD HHMMSS DD MM SS.SS H DD MM SS.SS H CCC XXXX YYYY xx.xx
////      ;; symb, date, time, lat, long, orientation, maxima (yards), minima (yards), label
//
//
//      if(time == 360)
//        System.out.println("here!");
//
//      double rng = polSolution[0];
//      rng = MWC.Algorithms.Conversions.m2Degs(rng);
//      final double brg = polSolution[1];
//      final WorldVector sep = new WorldVector(brg, rng, 0);
//      final WorldLocation origin = sensor._myPart.getStatus().getLocation().add(sep);
//
//      String str = ";ELLIPSE: @";
//      if(track.manoeuvre[0] == 0)
//      {
//        str += "D ";
//      }
//      else
//      {
//        str += "E ";
//      }
//      str += MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(time * 1000);
//      str += " ";
//      str += MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(origin);
//      str += " ";
//      str += MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(MWC.Algorithms.Conversions.Rads2Degs(Orientation[0]));
//      str += " ";
//      str += MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(Axis1[0] * 0.9144);
//      str += " ";
//      str += MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(Axis2[0] * 0.9144);
//      str += "  ";
//
// //     System.out.println("orient:" + Math.toDegrees(Orientation[0]) + " axis1:" + Axis1[0] + " axis2:" + Axis2[0]);
//
//      final java.text.SimpleDateFormat dateF = new java.text.SimpleDateFormat("mm:ss");
//
//      str += dateF.format(new java.util.Date(time * 1000));
//
//
//      try
//      {
//        fo.write(str + System.getProperty("line.separator"));
//      }
//      catch (IOException e)
//      {
//        e.printStackTrace();
//      }
//
//  //    System.out.println("axis1:" + Axis1[0] + ", 2:" + Axis2[0] + " Orient:" + Orientation[0]);
//
//    }
//
//  }
//
//
}
