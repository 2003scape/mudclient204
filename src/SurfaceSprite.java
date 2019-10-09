import java.awt.*;

public class SurfaceSprite extends Surface {

    public mudclient mudclientref;

    public SurfaceSprite(int width, int height, int k, Component component) {
        super(width, height, k, component);
    }

    public void spriteClipping(int x, int y, int w, int h, int id, int tx, int ty) {
        if (id >= 50000) {
            mudclientref.drawTeleportBubble(x, y, w, h, id - 50000, tx, ty);
            return;
        }
        if (id >= 40000) {
            mudclientref.drawItem(x, y, w, h, id - 40000, tx, ty);
            return;
        }
        if (id >= 20000) {
            mudclientref.drawNpc(x, y, w, h, id - 20000, tx, ty);
            return;
        }
        if (id >= 5000) {
            mudclientref.drawPlayer(x, y, w, h, id - 5000, tx, ty);
            return;
        } else {
            super.spriteClipping(x, y, w, h, id);
            return;
        }
    }
}
