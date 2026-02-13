package DiagramProcessing;

import ObjectTypes.BoxedObject;

public class Comparer {
    private static final int TOLERANCE = 5;

    public boolean isPointOnBox(BoxedObject box, int px, int py) {
        boolean top = (Math.abs(py - box.getYTopLocation()) <= TOLERANCE) && (px >= box.getXLeftLocation() - TOLERANCE && px <= box.getXRightLocation() + TOLERANCE);
        boolean bot = (Math.abs(py - box.getYBottomLocation()) <= TOLERANCE) && (px >= box.getXLeftLocation() - TOLERANCE && px <= box.getXRightLocation() + TOLERANCE);
        boolean left = (Math.abs(px - box.getXLeftLocation()) <= TOLERANCE) && (py >= box.getYTopLocation() - TOLERANCE && py <= box.getYBottomLocation() + TOLERANCE);
        boolean right = (Math.abs(px - box.getXRightLocation()) <= TOLERANCE) && (py >= box.getYTopLocation() - TOLERANCE && py <= box.getYBottomLocation() + TOLERANCE);

        return top || bot || left || right;
    }
}