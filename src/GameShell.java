import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

public class GameShell extends Applet
        implements Runnable, MouseListener, MouseMotionListener, KeyListener {

    public static GameFrame gameFrame = null;
    private static String charMap = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
    public int mouseActionTimeout;
    public int loadingStep;
    public String logoHeaderText;
    //public boolean keyLsb;
    //public boolean keyRsb;
    public boolean keyLeft;
    public boolean keyRight;
    public boolean keyUp;
    public boolean keyDown;
    public boolean keySpace;
    //public boolean keyNm;
    public int threadSleep;
    public int mouseX;
    public int mouseY;
    public int mouseButtonDown;
    public int lastMouseButtonDown;
    //public int unusedKeyCode1;
    //public int unusedKeyCode2;
    public boolean interlace;
    public String inputTextCurrent;
    public String inputTextFinal;
    public String inputPmCurrent;
    public String inputPmFinal;
    private int appletWidth;
    private int appletHeight;
    private Thread appletThread;
    private int targetFps;
    private int maxDrawTime;
    private long timings[];
    private boolean startedAsApplet;
    private int stopTimeout;
    private int interlaceTimer;
    private boolean hasRefererLogoNotused;
    private int loadingProgressPercent;
    private String loadingProgessText;
    private Font fontTimesRoman15;
    private Font fontHelvetica13b;
    private Font fontHelvetica12;
    private Image imageLogo;
    private Graphics graphics;

    public GameShell() {
        appletWidth = 512;
        appletHeight = 384;
        targetFps = 20;
        maxDrawTime = 1000;
        timings = new long[10];
        loadingStep = 1;
        hasRefererLogoNotused = false;
        loadingProgessText = "Loading";
        fontTimesRoman15 = new Font("TimesRoman", 0, 15);
        fontHelvetica13b = new Font("Helvetica", Font.BOLD, 13);
        fontHelvetica12 = new Font("Helvetica", 0, 12);
        //keyLsb = false;
        //keyRsb = false;
        keyLeft = false;
        keyRight = false;
        keyUp = false;
        keyDown = false;
        keySpace = false;
        //keyNm = false;
        threadSleep = 1;
        interlace = false;
        inputTextCurrent = "";
        inputTextFinal = "";
        inputPmCurrent = "";
        inputPmFinal = "";
    }

    protected void startGame() {
    }

    protected synchronized void handleInputs() {
    }

    protected void onClosing() {
    }

    protected synchronized void draw() {
    }

    protected void startApplication(int width, int height, String title, boolean resizeable) {
        startedAsApplet = false;
        System.out.println("Started application");
        appletWidth = width;
        appletHeight = height;
        gameFrame = new GameFrame(this, width, height, title, resizeable, false);
        gameFrame.addMouseListener(this);
        gameFrame.addMouseMotionListener(this);
        gameFrame.addKeyListener(this);
        loadingStep = 1;
        appletThread = new Thread(this);
        appletThread.start();
        appletThread.setPriority(1);
    }

    protected boolean getStartedAsApplet() {
        return startedAsApplet;
    }

    protected void setTargetFps(int i) {
        targetFps = 1000 / i;
    }

    protected void resetTimings() {
        for (int i = 0; i < 10; i++)
            timings[i] = 0L;

    }

    public synchronized void keyPressed(KeyEvent e) {
        char chr = e.getKeyChar();
        int code = e.getKeyCode();
        handleKeyPress(chr);
        mouseActionTimeout = 0;
        if (code == KeyEvent.VK_LEFT) {
            keyLeft = true;
        } else if (code == KeyEvent.VK_RIGHT) {
            keyRight = true;
        } else if (code == KeyEvent.VK_UP) {
            keyUp = true;
        } else if (code == KeyEvent.VK_DOWN) {
            keyDown = true;
        } else if (code == KeyEvent.VK_SPACE) {
            keySpace = true;
        } else if (code == KeyEvent.VK_F1) {
            interlace = !interlace;
        }
        boolean foundText = false;
        for (int i = 0; i < charMap.length(); i++) {
            if (charMap.charAt(i) == chr) {
                foundText = true;
                break;
            }
        }
        if (foundText) {
            if (inputTextCurrent.length() < 20) {
                inputTextCurrent += chr;
            }
            if (inputPmCurrent.length() < 80) {
                inputPmCurrent += chr;
            }
        }
        if (code == KeyEvent.VK_BACK_SPACE) {
            if (inputTextCurrent.length() > 0) {
                inputTextCurrent = inputTextCurrent.substring(0, inputTextCurrent.length() - 1);
            }
            if (inputPmCurrent.length() > 0) {
                inputPmCurrent = inputPmCurrent.substring(0, inputPmCurrent.length() - 1);
            }
        }
        if (code == KeyEvent.VK_ENTER) {
            inputTextFinal = inputTextCurrent;
            inputPmFinal = inputPmCurrent;
        }
    }

    protected void handleKeyPress(int i) {
    }

    public synchronized void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_LEFT) {
            keyLeft = false;
        } else if (code == KeyEvent.VK_RIGHT) {
            keyRight = false;
        } else if (code == KeyEvent.VK_UP) {
            keyUp = false;
        } else if (code == KeyEvent.VK_DOWN) {
            keyDown = false;
        } else if (code == KeyEvent.VK_SPACE) {
            keySpace = false;
        }
    }

    public synchronized void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        mouseButtonDown = 0;
        mouseActionTimeout = 0;
    }

    public synchronized void mouseReleased(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        mouseButtonDown = 0;
    }

    public synchronized void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        mouseX = x;
        mouseY = y;
        if (e.isMetaDown()) {
            mouseButtonDown = 2;
        } else {
            mouseButtonDown = 1;
        }
        lastMouseButtonDown = mouseButtonDown;
        mouseActionTimeout = 0;
        handleMouseDown(mouseButtonDown, x, y);
    }

    protected void handleMouseDown(int i, int j, int k) {
    }

    public synchronized void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        if (e.isMetaDown()) {
            mouseButtonDown = 2;
        } else {
            mouseButtonDown = 1;
        }
    }

    public void init() {
        startedAsApplet = true;
        System.out.println("Started applet");
        appletWidth = 512;
        appletHeight = 344;
        loadingStep = 1;
        Utility.appletCodeBase = getCodeBase();
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        startThread(this);
    }

    public void start() {
        if (stopTimeout >= 0)
            stopTimeout = 0;
    }

    public void stop() {
        if (stopTimeout >= 0)
            stopTimeout = 4000 / targetFps;
    }

    public void destroy() {
        stopTimeout = -1;
        try {
            Thread.sleep(5000L);
        } catch (Exception ignored) {
        }
        if (stopTimeout == -1) {
            System.out.println("5 seconds expired, forcing kill");
            closeProgram();
            if (appletThread != null) {
                //appletThread.stop();
                appletThread = null;
            }
        }
    }

    private void closeProgram() {
        stopTimeout = -2;
        System.out.println("Closing program");
        onClosing();
        try {
            Thread.sleep(1000L);
        } catch (Exception ignored) {
        }
        if (gameFrame != null)
            gameFrame.dispose();
        if (!startedAsApplet)
            System.exit(0);
    }

    public void run() {
        if (loadingStep == 1) {
            loadingStep = 2;
            graphics = getGraphics();
            loadJagex();
            drawLoadingScreen(0, "Loading...");
            startGame();
            loadingStep = 0;
        }
        int i = 0;
        int j = 256;
        int sleep = 1;
        int i1 = 0;
        for (int j1 = 0; j1 < 10; j1++)
            timings[j1] = System.currentTimeMillis();

        //long l = System.currentTimeMillis();
        while (stopTimeout >= 0) {
            if (stopTimeout > 0) {
                stopTimeout--;
                if (stopTimeout == 0) {
                    closeProgram();
                    appletThread = null;
                    return;
                }
            }
            int k1 = j;
            int lastSleep = sleep;
            j = 300;
            sleep = 1;
            long time = System.currentTimeMillis();
            if (timings[i] == 0L) {
                j = k1;
                sleep = lastSleep;
            } else if (time > timings[i])
                j = (int) ((long) (2560 * targetFps) / (time - timings[i]));
            if (j < 25)
                j = 25;
            if (j > 256) {
                j = 256;
                sleep = (int) ((long) targetFps - (time - timings[i]) / 10L);
                if (sleep < threadSleep)
                    sleep = threadSleep;
            }
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException ignored) {
            }
            timings[i] = time;
            i = (i + 1) % 10;
            if (sleep > 1) {
                for (int j2 = 0; j2 < 10; j2++)
                    if (timings[j2] != 0L)
                        timings[j2] += sleep;

            }
            int k2 = 0;
            while (i1 < 256) {
                handleInputs();
                i1 += j;
                if (++k2 > maxDrawTime) {
                    i1 = 0;
                    interlaceTimer += 6;
                    if (interlaceTimer > 25) {
                        interlaceTimer = 0;
                        interlace = true;
                    }
                    break;
                }
            }
            interlaceTimer--;
            i1 &= 0xff;
            draw();
        }
        if (stopTimeout == -1)
            closeProgram();
        appletThread = null;
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
        if (loadingStep == 2 && imageLogo != null) {
            drawLoadingScreen(loadingProgressPercent, loadingProgessText);
            //return;
        }
        //if (loadingStep == 0)
        //    emptyMethod();
    }

    private void loadJagex() {
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, appletWidth, appletHeight);
        byte buff[] = readDataFile("jagex.jag", "Jagex library", 0);
        if (buff != null) {
            byte logo[] = Utility.loadData("logo.tga", 0, buff);
            // TODO dump this logo and see if tga loader loads it
            imageLogo = createImage(logo);
        }
        buff = readDataFile("fonts" + Version.FONTS + ".jag", "Game fonts", 5);
        if (buff != null) {
            Surface.createFont(Utility.loadData("h11p.jf", 0, buff), 0);
            Surface.createFont(Utility.loadData("h12b.jf", 0, buff), 1);
            Surface.createFont(Utility.loadData("h12p.jf", 0, buff), 2);
            Surface.createFont(Utility.loadData("h13b.jf", 0, buff), 3);
            Surface.createFont(Utility.loadData("h14b.jf", 0, buff), 4);
            Surface.createFont(Utility.loadData("h16b.jf", 0, buff), 5);
            Surface.createFont(Utility.loadData("h20b.jf", 0, buff), 6);
            Surface.createFont(Utility.loadData("h24b.jf", 0, buff), 7);
        }
    }

    private void drawLoadingScreen(int percent, String text) {
        try {
            int midx = (appletWidth - 281) / 2;
            int midy = (appletHeight - 148) / 2;
            graphics.setColor(Color.black);
            graphics.fillRect(0, 0, appletWidth, appletHeight);
            if (!hasRefererLogoNotused)
                graphics.drawImage(imageLogo, midx, midy, this);
            midx += 2;
            midy += 90;
            loadingProgressPercent = percent;
            loadingProgessText = text;
            graphics.setColor(new Color(132, 132, 132));
            if (hasRefererLogoNotused)
                graphics.setColor(new Color(220, 0, 0));
            graphics.drawRect(midx - 2, midy - 2, 280, 23);
            graphics.fillRect(midx, midy, (277 * percent) / 100, 20);
            graphics.setColor(new Color(198, 198, 198));
            if (hasRefererLogoNotused)
                graphics.setColor(new Color(255, 255, 255));
            drawString(graphics, text, fontTimesRoman15, midx + 138, midy + 10);
            if (!hasRefererLogoNotused) {
                drawString(graphics, "Created by JAGeX - visit www.jagex.com", fontHelvetica13b, midx + 138, midy + 30);
                drawString(graphics, "\2512001-2002 Andrew Gower and Jagex Ltd", fontHelvetica13b, midx + 138, midy + 44);
            } else {
                graphics.setColor(new Color(132, 132, 152));
                drawString(graphics, "\2512001-2002 Andrew Gower and Jagex Ltd", fontHelvetica12, midx + 138, appletHeight - 20);
            }
            if (logoHeaderText != null) {
                graphics.setColor(Color.white);
                drawString(graphics, logoHeaderText, fontHelvetica13b, midx + 138, midy - 120);
            }
        } catch (Exception ignored) {
        }
    }

    protected void showLoadingProgress(int i, String s) {
        try {
            int j = (appletWidth - 281) / 2;
            int k = (appletHeight - 148) / 2;
            j += 2;
            k += 90;
            loadingProgressPercent = i;
            loadingProgessText = s;
            int l = (277 * i) / 100;
            graphics.setColor(new Color(132, 132, 132));
            if (hasRefererLogoNotused)
                graphics.setColor(new Color(220, 0, 0));
            graphics.fillRect(j, k, l, 20);
            graphics.setColor(Color.black);
            graphics.fillRect(j + l, k, 277 - l, 20);
            graphics.setColor(new Color(198, 198, 198));
            if (hasRefererLogoNotused)
                graphics.setColor(new Color(255, 255, 255));
            drawString(graphics, s, fontTimesRoman15, j + 138, k + 10);
        } catch (Exception ignored) {
        }
    }

    protected void drawString(Graphics g, String s, Font font, int i, int j) {
        Object obj;
        if (gameFrame == null)
            obj = this;
        else
            obj = gameFrame;
        FontMetrics fontmetrics = ((Component) (obj)).getFontMetrics(font);
        fontmetrics.stringWidth(s);
        g.setFont(font);
        g.drawString(s, i - fontmetrics.stringWidth(s) / 2, j + fontmetrics.getHeight() / 4);
    }

    private Image createImage(byte buff[]) {
        int i = buff[13] * 256 + buff[12];
        int j = buff[15] * 256 + buff[14];
        byte abyte1[] = new byte[256];
        byte abyte2[] = new byte[256];
        byte abyte3[] = new byte[256];
        for (int k = 0; k < 256; k++) {
            abyte1[k] = buff[20 + k * 3];
            abyte2[k] = buff[19 + k * 3];
            abyte3[k] = buff[18 + k * 3];
        }

        IndexColorModel indexcolormodel = new IndexColorModel(8, 256, abyte1, abyte2, abyte3);
        byte abyte4[] = new byte[i * j];
        int l = 0;
        for (int i1 = j - 1; i1 >= 0; i1--) {
            for (int j1 = 0; j1 < i; j1++)
                abyte4[l++] = buff[786 + j1 + i1 * i];

        }

        MemoryImageSource memoryimagesource = new MemoryImageSource(i, j, indexcolormodel, abyte4, 0, i);
        return createImage(memoryimagesource);
    }

    protected byte[] readDataFile(String file, String description, int percent) {
        //System.out.println("Using default load");
        file = "./data204/" + file;
        int archiveSize = 0;
        int archiveSizeCompressed = 0;
        byte archiveData[] = null;
        try {
            showLoadingProgress(percent, "Loading " + description + " - 0%");
            java.io.InputStream inputstream = Utility.openFile(file);
            DataInputStream datainputstream = new DataInputStream(inputstream);
            byte header[] = new byte[6];
            datainputstream.readFully(header, 0, 6);
            archiveSize = ((header[0] & 0xff) << 16) + ((header[1] & 0xff) << 8) + (header[2] & 0xff);
            archiveSizeCompressed = ((header[3] & 0xff) << 16) + ((header[4] & 0xff) << 8) + (header[5] & 0xff);
            showLoadingProgress(percent, "Loading " + description + " - 5%");
            int read = 0;
            archiveData = new byte[archiveSizeCompressed];
            while (read < archiveSizeCompressed) {
                int length = archiveSizeCompressed - read;
                if (length > 1000)
                    length = 1000;
                datainputstream.readFully(archiveData, read, length);
                read += length;
                showLoadingProgress(percent, "Loading " + description + " - " + (5 + (read * 95) / archiveSizeCompressed) + "%");
            }
            datainputstream.close();
        } catch (IOException ignored) {
        }
        showLoadingProgress(percent, "Unpacking " + description);
        if (archiveSizeCompressed != archiveSize) {
            byte decompressed[] = new byte[archiveSize];
            BZLib.decompress(decompressed, archiveSize, archiveData, archiveSizeCompressed, 0);
            return decompressed;
        } else {
            return archiveData;
        }
    }

    public Graphics getGraphics() {
        if (gameFrame != null)
            return gameFrame.getGraphics();
        else
            return super.getGraphics();
    }

    public Image createImage(int i, int j) {
        if (gameFrame != null)
            return gameFrame.createImage(i, j);
        else
            return super.createImage(i, j);
    }

    public URL getCodeBase() {
        return super.getCodeBase();
    }

    public URL getDocumentBase() {
        return super.getDocumentBase();
    }

    public String getParameter(String s) {
        return super.getParameter(s);
    }

    protected Socket createSocket(String s, int i)
            throws IOException {
        Socket socket;
        if (getStartedAsApplet())
            socket = new Socket(InetAddress.getByName(getCodeBase().getHost()), i);
        else
            socket = new Socket(InetAddress.getByName(s), i);
        socket.setSoTimeout(30000);
        socket.setTcpNoDelay(true);
        return socket;
    }

    protected void startThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    }

    public void keyTyped(KeyEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
