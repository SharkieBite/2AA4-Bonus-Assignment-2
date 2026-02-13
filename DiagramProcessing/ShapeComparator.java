package DiagramProcessing;

import ObjectTypes.BoxedObject;
import ObjectTypes.LineObject;

interface ShapeComparator {
    boolean attachedToObject(BoxedObject currentBox, LineObject currentLine);
}
