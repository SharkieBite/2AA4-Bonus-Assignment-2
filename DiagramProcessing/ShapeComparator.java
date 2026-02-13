package DiagramProcessing;

import ObjectTypes.BoxedObject;
import ObjectTypes.LineObject;

// FIX 1: Renamed to avoid conflict with java.lang.Comparable
interface ShapeComparator {
    boolean attachedToObject(BoxedObject currentBox, LineObject currentLine);
}
