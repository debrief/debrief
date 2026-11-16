package ASSET.Models.Detection;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Models.Detection.DetectionList.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ASSET.Models.Detection.DetectionList.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Sensor.Initial.OpticSensor;
import ASSET.Models.Vessels.SSN;
import ASSET.Participants.Category;
import ASSET.Util.SupportTesting;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;
import org.junit.jupiter.api.Test;

/**
 * our list of detections
 *
 */
// Vector _list;

//////////////////////////////////////////////////
// property testing
//////////////////////////////////////////////////
public class DetectionListTest extends SupportTesting {

	@Test
	public void testFullLists() {
		final SSN ssnA = new SSN(44);
		final SSN ssnB = new SSN(45);
		final CoreSensor newSensor = new OpticSensor(22);
		final DetectionEvent de1 = new DetectionEvent(12, 12, null, newSensor, null, null, null, null, null,
				new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
				ssnA);
		final DetectionEvent de2 = new DetectionEvent(13, 12, null, newSensor,
				new WorldDistance(12, WorldDistance.NM), null, null, null, null,
				new Category(Category.Force.GREEN, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
				ssnA);
		final DetectionEvent de3 = new DetectionEvent(11, 12, null, newSensor, null, null, null, null, null,
				new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FRIGATE),
				new WorldSpeed(12, WorldSpeed.Kts), null, ssnA, DetectionEvent.DETECTED);
		final DetectionEvent de4 = new DetectionEvent(10, 12, null, newSensor,
				new WorldDistance(3, WorldDistance.NM), null, null, null, null,
				new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
				ssnA);
		final DetectionEvent de5 = new DetectionEvent(17, 12, null, newSensor,
				new WorldDistance(7, WorldDistance.NM), null, null, null, null,
				new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
				ssnB);
		final DetectionEvent de6 = new DetectionEvent(15, 12, null, newSensor, null, null, null, null, null,
				new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FRIGATE),
				new WorldSpeed(12, WorldSpeed.Kts), null, ssnB, DetectionEvent.CLASSIFIED);

		final DetectionList dl = new DetectionList();
		dl.add(de1);
		dl.add(de2);
		dl.add(de3);
		dl.add(de4);
		dl.add(de5);
		dl.add(de6);

		// ok, do some tests
		assertEquals(6, dl.size(), 0, "wrong number of items in list");

		// see which is the nearest
		final DetectionEvent nearest = dl.getNearestDetection();
		assertEquals(de4, nearest, "failed to find closest detection :");

		final TargetType redType = new TargetType(Category.Force.RED);
		final DetectionList reds = dl.getDetectionsOf(redType);
		assertEquals(3, reds.size(), "wrong number of red targets");

		final DetectionEvent mostRecent = reds.getMostRecentDetection();
		assertEquals(de5, mostRecent, "wrong detection returned as most recent");

		// couple of other quick ones
		final DetectionList validDets = dl.getDetectionsOf(ssnB.getId());
		assertEquals(2, validDets.size(), 0, "wrong number of ssnb detections");
		assertEquals(de5, validDets.getMostRecentDetection(), "wrong one returned as most recent");
		assertEquals(de5, dl.getMostRecentDetection(), "wrong one returned as overall most recent");
		// DetectionEvent bestDet = validDets.getBestDetection();
		assertEquals(de6, dl.getBestDetection(), "wrong one returned as best");

	}

	@Test
	public void testLists() {
		final SSN ssnA = new SSN(44);
		final SSN ssnB = new SSN(45);
		final SSN ssnC = new SSN(65);
		final CoreSensor newSensor = new OpticSensor(22);
		final DetectionEvent de1 = new DetectionEvent(12, 12, null, newSensor, null, null, null, null, null,
				new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
				ssnA);
		final DetectionEvent de2 = new DetectionEvent(13, 12, null, newSensor, null, null, null, null, null,
				new Category(Category.Force.GREEN, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
				ssnA);
		final DetectionEvent de3 = new DetectionEvent(11, 12, null, newSensor, null, null, null, null, null,
				new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
				ssnA);
		final DetectionEvent de4 = new DetectionEvent(10, 12, null, newSensor, null, null, null, null, null,
				new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
				ssnC);
		final DetectionEvent de5 = new DetectionEvent(17, 12, null, newSensor, null, null, null, null, null,
				new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
				ssnB);
		final DetectionEvent de6 = new DetectionEvent(15, 12, null, newSensor, null, null, null, null, null,
				new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FRIGATE), null, null,
				ssnB);

		final DetectionList dl = new DetectionList();
		dl.add(de1);
		dl.add(de2);
		dl.add(de3);
		dl.add(de4);
		dl.add(de5);
		dl.add(de6);

		// ok, do some tests
		assertEquals(6, dl.size(), 0, "wrong number of items in list");
		DetectionList myDets = dl.getDetectionsOf(new Integer(ssnC.getId()), new TargetType(Category.Force.RED));
		assertEquals(1, myDets.size(), 0, "didn't find the target we're after");
		assertEquals(de4, myDets.getMostRecentDetection(), "didn't find the target we're after");
		myDets = dl.getDetectionsOf(new Integer(712), new TargetType(Category.Force.RED));
		assertEquals(3, myDets.size(), 0, "didn't find the target we're after");
		assertEquals(de5, myDets.getMostRecentDetection(), "didn't find the target we're after");
		myDets = dl.getDetectionsOf(null, new TargetType(Category.Force.RED));
		assertEquals(3, myDets.size(), 0, "didn't find the target we're after");
		assertEquals(de5, myDets.getMostRecentDetection(), "didn't find the target we're after");
	}
}
