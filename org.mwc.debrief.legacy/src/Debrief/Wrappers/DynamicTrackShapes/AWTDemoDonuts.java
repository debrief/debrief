package Debrief.Wrappers.DynamicTrackShapes;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class AWTDemoDonuts extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private int xDestination;
    private int yDestination;

    private double innerRadius;
    private double outerRadius;

    private double minAngle;
    private double maxAngle;

    private int leftCornerShift;

    private Shape fullDonut;
    private Shape sectorDonut;

    public AWTDemoDonuts() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400,700);
        setLayout(null);
    }

    /**
     * Method to show shapes.
     * @param x of upper left corner of donut sector
     * @param y of upper left corner of donut sector
     * @param innerR radius of inner circle in donut
     * @param outerR raius of outer circle in donut
     * @param minA start angle of donut sector
     * @param maxA end angle of donut sector
     */
    public void setParameters(int x, int y,
                           double innerR, double outerR,
                           double minA, double maxA) {
        xDestination = x;
        yDestination = y;
        innerRadius = innerR;
        outerRadius = outerR;
        minAngle =  minA;
        maxAngle =  maxA;
        leftCornerShift = (int) (outerRadius - innerRadius) / 2;
    }

    public void showShapes() {
        setVisible(true);
    }

    public void createFullDonut() {
        Ellipse2D circleOuter = new Ellipse2D.Double();
        Ellipse2D circleInner= new Ellipse2D.Double();
        circleOuter.setFrame(xDestination, yDestination, outerRadius, outerRadius);
        circleInner.setFrame(xDestination + leftCornerShift, yDestination + leftCornerShift, innerRadius, innerRadius);

        fullDonut = subtract(circleOuter, circleInner);
    }

    public void createSectorDonut() {
        Ellipse2D circleOuter = new Ellipse2D.Double();
        circleOuter.setFrame(xDestination, yDestination, outerRadius, outerRadius);

        Ellipse2D circleInner= new Ellipse2D.Double();
        circleInner.setFrame(xDestination + leftCornerShift, yDestination + leftCornerShift, innerRadius, innerRadius);

        /**
         * Create circle sector from maxAngle to minAngle.
         */
        Arc2D circleSector = new Arc2D.Double(xDestination - 1, yDestination - 1, outerRadius + 2, outerRadius + 2, minAngle, - 360 + (maxAngle - minAngle), Arc2D.PIE);

        /**
         * Subtract inner circle from outer thus creating needed donut.
         * Subtract circle sector from maxAngle to minAngle from created donut.
         */
        sectorDonut = subtract(subtract(circleOuter,circleInner), circleSector);
    }

    @Override
    public void paint(Graphics graphics) {
        if (sectorDonut == null || fullDonut == null) {
            System.out.println("PLease create full and sector donuts first.");
        }
        Graphics2D graphics2D = (Graphics2D) graphics;
   //     graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.setPaint(Color.green);
        graphics2D.fill(sectorDonut);
   //     graphics2D.draw(sectorDonut);

        graphics2D.setPaint(Color.green);
        graphics2D.fill(fullDonut);
   //     graphics2D.draw(fullDonut);
    }

    /**
     * Helper method to create shapes by subtracting some two of them
     * @param shape1 minuend
     * @param shape2 subtrahend
     * @return new shape
     */
    private Shape subtract(Shape shape1, Shape shape2) {
        Shape result = new Area(shape1);
        ((Area) result).subtract(new Area(shape2));
        return result;
    }

    public static void main(String[] args) {
        AWTDemoDonuts awtDemoDonuts = new AWTDemoDonuts();
        awtDemoDonuts.setParameters(100,400,150,200,45,135);
        awtDemoDonuts.createFullDonut();
        awtDemoDonuts.setParameters(100,100,150,200,45,135);
        awtDemoDonuts.createSectorDonut();
        awtDemoDonuts.showShapes();
    }
}