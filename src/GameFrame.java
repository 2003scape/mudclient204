import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends Frame {

    int windowWidth;
    int windowHeight;
    int translationMode;
    int windowYTranslation;
    GameShell gameShell;

    public GameFrame(GameShell game, int width, int height, String title, boolean resizable, boolean flag1) {
        windowYTranslation = 28;
        windowWidth = width;
        windowHeight = height;
        this.gameShell = game;
        if (flag1)
            windowYTranslation = 48;
        else
            windowYTranslation = 28;
        setTitle(title);
        setResizable(resizable);
        setVisible(true);
        toFront();
        setSize(windowWidth, windowHeight);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                gameShell.destroy();
            }
        });
    }

    public Graphics getGraphics() {
        Graphics g = super.getGraphics();
        if (translationMode == 0)
            g.translate(0, 24);
        else
            g.translate(-5, 0);
        return g;
    }

    public void setSize(int x, int y) {
        super.setSize(x, y + windowYTranslation);
    }

    protected void processEvent(AWTEvent e) {
        if (e instanceof MouseEvent) {
            MouseEvent evt = (MouseEvent) e;
            e = new MouseEvent(evt.getComponent(), evt.getID(), evt.getWhen(), evt.getModifiers(), evt.getX(), evt.getY() - 24, evt.getClickCount(), evt.isPopupTrigger());
        }
        super.processEvent(e);
    }

    public void paint(Graphics g) {
        gameShell.paint(g);
    }
}
