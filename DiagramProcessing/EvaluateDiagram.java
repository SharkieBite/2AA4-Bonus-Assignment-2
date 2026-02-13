package DiagramProcessing;

import ObjectTypes.BoxedObject;
import ObjectTypes.LineObject;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluateDiagram {

    private List<BoxedObject> classes = new ArrayList<>();
    private List<BoxedObject> interfaces = new ArrayList<>();
    private List<LineObject> lines = new ArrayList<>();
    private Map<String, BoxedObject> boxIdMap = new HashMap<>();

    public List<BoxedObject> getClasses() { return classes; }
    public List<BoxedObject> getInterfaces() { return interfaces; }
    public List<LineObject> getLines() { return lines; }

    public void parseXml(String filePath) {
        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList cellList = doc.getElementsByTagName("mxCell");

            for (int i = 0; i < cellList.getLength(); i++) {
                Element cell = (Element) cellList.item(i);
                if ("1".equals(cell.getAttribute("vertex"))) {
                    parseBox(cell);
                }
            }

            for (int i = 0; i < cellList.getLength(); i++) {
                Element cell = (Element) cellList.item(i);
                if ("1".equals(cell.getAttribute("edge"))) {
                    parseLine(cell);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseBox(Element cell) {
        String id = cell.getAttribute("id");
        String style = cell.getAttribute("style");
        String value = cell.getAttribute("value");
        Element geometry = (Element) cell.getElementsByTagName("mxGeometry").item(0);

        if (geometry != null) {
            try {
                int x = (int) Math.round(parseDoubleSafe(geometry.getAttribute("x")));
                int y = (int) Math.round(parseDoubleSafe(geometry.getAttribute("y")));
                int w = (int) Math.round(parseDoubleSafe(geometry.getAttribute("width")));
                int h = (int) Math.round(parseDoubleSafe(geometry.getAttribute("height")));

                String name = value.replaceAll("<[^>]*>", "").replaceAll("[^a-zA-Z0-9_]", "");

                if (w > 0 && h > 0) {
                    BoxedObject box = new BoxedObject(name, x, y, w, h);
                    boxIdMap.put(id, box);

                    if (style != null && (style.contains("dashed=1") || style.contains("dashed=true"))) {
                        interfaces.add(box);
                    } else {
                        classes.add(box);
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    private void parseLine(Element cell) {
        String sourceId = cell.getAttribute("source");
        String targetId = cell.getAttribute("target");
        String style = cell.getAttribute("style");

        int sX = 0, sY = 0, tX = 0, tY = 0;

        boolean isVisuallyReversed = false;
        if (style != null) {
            boolean hasStartArrow = style.contains("startArrow=") && !style.contains("startArrow=none");
            boolean hasEndArrow = style.contains("endArrow=") && !style.contains("endArrow=none");

            if (hasStartArrow && !hasEndArrow) {
                isVisuallyReversed = true;
            }
        }

        if (boxIdMap.containsKey(sourceId)) {
            Point p = calculateAnchor(boxIdMap.get(sourceId), style, "source");
            sX = p.x; sY = p.y;
        } else {
            Point p = getManualPoint(cell, "sourcePoint");
            sX = p.x; sY = p.y;
        }

        if (boxIdMap.containsKey(targetId)) {
            Point p = calculateAnchor(boxIdMap.get(targetId), style, "target");
            tX = p.x; tY = p.y;
        } else {
            Point p = getManualPoint(cell, "targetPoint");
            tX = p.x; tY = p.y;
        }

        if (isVisuallyReversed) {
            lines.add(new LineObject(tX, tY, sX, sY));
        } else {
            lines.add(new LineObject(sX, sY, tX, tY));
        }
    }

    private Point calculateAnchor(BoxedObject box, String style, String type) {
        double relX = 0.5, relY = 0.5;
        String prefix = (type.equals("source")) ? "exit" : "entry";
        if (style != null) {
            String[] tokens = style.split(";");
            for (String t : tokens) {
                if (t.startsWith(prefix + "X=")) relX = parseDoubleSafe(t.split("=")[1]);
                if (t.startsWith(prefix + "Y=")) relY = parseDoubleSafe(t.split("=")[1]);
            }
        }
        int w = box.getXRightLocation() - box.getXLeftLocation();
        int h = box.getYBottomLocation() - box.getYTopLocation();
        return new Point(box.getXLeftLocation() + (int)(w * relX), box.getYTopLocation() + (int)(h * relY));
    }

    private Point getManualPoint(Element cell, String asType) {
        Element geo = (Element) cell.getElementsByTagName("mxGeometry").item(0);
        if (geo != null) {
            NodeList points = geo.getElementsByTagName("mxPoint");
            for (int i = 0; i < points.getLength(); i++) {
                Element p = (Element) points.item(i);
                if (asType.equals(p.getAttribute("as"))) {
                    return new Point((int)parseDoubleSafe(p.getAttribute("x")), (int)parseDoubleSafe(p.getAttribute("y")));
                }
            }
        }
        return new Point(0,0);
    }

    private double parseDoubleSafe(String val) {
        try { return Double.parseDouble(val); } catch (Exception e) { return 0.0; }
    }
    private class Point { int x, y; Point(int x, int y) { this.x=x; this.y=y; } }
}