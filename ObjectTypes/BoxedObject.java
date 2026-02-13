package ObjectTypes;

public class BoxedObject implements isBoxedSyntax {

    public String name;
    int xLeftLocation;
    int xRightLocation;
    int yBottomLocation;
    int yTopLocation;

    public BoxedObject(String name, int x, int y, int w, int h) {
        // Default to "Untitled" if name is empty to prevent errors
        this.name = (name == null || name.trim().isEmpty()) ? "Untitled_" + x : name;
        this.xLeftLocation = x;
        this.yTopLocation = y;
        this.xRightLocation = x + w;
        this.yBottomLocation = y + h;
    }

    @Override
    public int getXLeftLocation() { return xLeftLocation; }

    @Override
    public int getXRightLocation() { return xRightLocation; }

    @Override
    public int getYTopLocation() { return yTopLocation; }

    @Override
    public int getYBottomLocation() { return yBottomLocation; }
}