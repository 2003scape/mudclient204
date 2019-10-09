public class Polygon {

    protected int minPlaneX;
    protected int minPlaneY;
    protected int maxPlaneX;
    protected int maxPlaneY;
    protected int minZ;
    protected int maxZ;
    protected GameModel model;
    protected int face;
    protected int depth;
    protected int normalX;
    protected int normalY;
    protected int normalZ;
    protected int visibility;
    protected int facefill;
    protected boolean skipSomething;
    protected int index;
    protected int index2;

    public Polygon() {
        skipSomething = false;
        index2 = -1;
    }
}
