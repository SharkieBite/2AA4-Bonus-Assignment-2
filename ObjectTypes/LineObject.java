package ObjectTypes;

public class LineObject implements isLineSyntax {

    private int sourceX;
    private int sourceY;
    private int targetX;
    private int targetY;

    public LineObject(int sourceX, int sourceY, int targetX, int targetY) {
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    public int getSourceX() { return sourceX; }

    @Override
    public int getSourceY() { return sourceY; }

    @Override
    public int getTargetX() { return targetX; }

    @Override
    public int getTargetY() { return targetY; }
}