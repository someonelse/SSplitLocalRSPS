package client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class RSApplet
    extends JPanel
    implements Runnable,
               MouseListener,
               MouseMotionListener,
               KeyListener,
               FocusListener,
               WindowListener
{
    // ───────── Fields ─────────
    private int anInt4;
    private int delayTime;
    int minDelay;
    private final long[] aLongArray7;
    int fps;
    boolean shouldDebug;
    int myWidth;
    int myHeight;
    Graphics graphics;
    RSImageProducer fullGameScreen;
    RSFrame gameFrame;            // needed by getGameComponent()
    private boolean shouldClearScreen;
    boolean awtFocus;
    int idleTime;
    int clickMode2;
    public int mouseX;
    public int mouseY;
    private int clickMode1;
    private int clickX;
    private int clickY;
    private long clickTime;
    int clickMode3;
    int saveClickX;
    int saveClickY;
    long aLong29;
    final int[] keyArray;
    private final int[] charQueue;
    private int readIndex;
    private int writeIndex;
    public static int anInt34;

    // ───────── Constructor ─────────
    public RSApplet() {
        delayTime         = 20;
        minDelay          = 1;
        aLongArray7       = new long[10];
        shouldDebug       = false;
        shouldClearScreen = true;
        awtFocus          = true;
        keyArray          = new int[128];
        charQueue         = new int[128];
    }

    // ─────── JFrame host methods ───────
    final void createClientFrame(int width, int height) {
        myWidth  = width;
        myHeight = height;

        JFrame frame = new JFrame("SoulSplit Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        setPreferredSize(new Dimension(myWidth, myHeight));
        frame.getContentPane().add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        graphics       = this.getGraphics();
        fullGameScreen = new RSImageProducer(myWidth, myHeight, this);
        startRunnable(this, 1);
    }

    final void initClientFrame(int width, int height) {
        myWidth        = width;
        myHeight       = height;
        graphics       = this.getGraphics();
        fullGameScreen = new RSImageProducer(myWidth, myHeight, this);
        startRunnable(this, 1);
    }

    // ───────── Runnable ─────────
    @Override
    public void run() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        addFocusListener(this);

        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) window.addWindowListener(this);

        drawLoadingText(0, "Loading...");
        startUp();

        for (int i = 0; i < aLongArray7.length; i++) {
            aLongArray7[i] = System.currentTimeMillis();
        }

        while (anInt4 >= 0) {
            processGameLoop();
            processDrawing();
            if (shouldDebug) dumpDebug();
        }
        if (anInt4 == -1) exit();
    }

    // ───────── Painting ─────────
    @Override
    public final void paint(Graphics g) {
        if (graphics == null) graphics = g;
        shouldClearScreen = true;
        raiseWelcomeScreen();
    }

    @Override
    public final void update(Graphics g) {
        if (graphics == null) graphics = g;
        shouldClearScreen = true;
        raiseWelcomeScreen();
    }

    // ───────── Window/Input methods ─────────
    public final void mousePressed(MouseEvent mouseevent) {
        int i = mouseevent.getX();
        int j = mouseevent.getY();
        if (gameFrame != null) {
            i -= 4; j -= 22;
        }
        idleTime   = 0;
        clickX     = i;
        clickY     = j;
        clickTime  = System.currentTimeMillis();
        if (mouseevent.isMetaDown()) {
            clickMode1 = clickMode2 = 2;
        } else {
            clickMode1 = clickMode2 = 1;
        }
    }

    public final void mouseReleased(MouseEvent mouseevent) {
        idleTime   = 0;
        clickMode2 = 0;
    }

    public final void mouseClicked(MouseEvent mouseevent) { }
    public final void mouseEntered(MouseEvent mouseevent) { }
    public final void mouseExited(MouseEvent mouseevent) {
        idleTime = 0; mouseX = -1; mouseY = -1;
    }

    public final void mouseDragged(MouseEvent mouseevent) {
        int i = mouseevent.getX();
        int j = mouseevent.getY();
        if (gameFrame != null) { i -= 4; j -= 22; }
        idleTime = 0;
        mouseX   = i;
        mouseY   = j;
    }

    public final void mouseMoved(MouseEvent mouseevent) {
        int i = mouseevent.getX();
        int j = mouseevent.getY();
        if (gameFrame != null) { i -= 4; j -= 22; }
        idleTime = 0;
        mouseX   = i;
        mouseY   = j;
    }

    public final void keyPressed(KeyEvent keyevent) {
        idleTime = 0;
        int i = keyevent.getKeyCode();
        int j = keyevent.getKeyChar();

        // hotkey logic...
        // (paste your old key-handling block here)

        // map arrow/ctrl/etc to j and queue it
        if (j < 30) j = 0;
        if (i == 37) j = 1;
        // ... rest of your mapping ...
        if (j > 4) {
            charQueue[writeIndex] = j;
            writeIndex = (writeIndex + 1) & 0x7f;
        }
    }

    public final void keyReleased(KeyEvent keyevent) {
        idleTime = 0;
        int i = keyevent.getKeyCode();
        char c = keyevent.getKeyChar();
        if (c < '\036') c = '\0';
        if (i == 37) c = '\001';
        // ... rest of your mapping ...
        if (c > 0 && c < '\200') keyArray[c] = 0;
    }

    public final void keyTyped(KeyEvent keyevent) { }

    final int readChar(int dummy) {
        while (dummy >= 0) { /* spin */ }
        int k = -1;
        if (writeIndex != readIndex) {
            k = charQueue[readIndex];
            readIndex = (readIndex + 1) & 0x7f;
        }
        return k;
    }

    public final void focusGained(FocusEvent focusevent) {
        awtFocus = true;
        shouldClearScreen = true;
        raiseWelcomeScreen();
    }

    public final void focusLost(FocusEvent focusevent) {
        awtFocus = false;
        for (int i = 0; i < keyArray.length; i++) keyArray[i] = 0;
    }

    public final void windowOpened(WindowEvent windowevent)    { }
    public final void windowClosing(WindowEvent windowevent)   { destroy(); }
    public final void windowClosed(WindowEvent windowevent)    { }
    public final void windowIconified(WindowEvent windowevent) { }
    public final void windowDeiconified(WindowEvent windowevent) { }
    public final void windowActivated(WindowEvent windowevent)   { }
    public final void windowDeactivated(WindowEvent windowevent) { }

    // ───────── Legacy lifecycle ─────────
    private void exit() {
        anInt4 = -2;
        cleanUpForQuit();
        if (gameFrame != null) {
            try { Thread.sleep(1000L); } catch(Exception ignored) { }
            try { System.exit(0); } catch (Throwable ignored) { }
        }
    }

    final void method4(int i) {
        delayTime = 1000 / i;
    }

    public final void start() {
        if (anInt4 >= 0) anInt4 = 0;
    }

    public final void stop() {
        if (anInt4 >= 0) anInt4 = 4000 / delayTime;
    }

    public final void destroy() {
        anInt4 = -1;
        try { Thread.sleep(5000L); } catch (Exception ignored) { }
        if (anInt4 == -1) exit();
    }

    /** Called when shouldDebug is true */
    protected void dumpDebug() {
        // paste in your original System.out debug dumps here,
        // or just clear the flag if you don’t need them:
        shouldDebug = false;
    }
    
    // ───────── Stubbed game logic ─────────
    void startUp()         { /* copy your old startup logic here */ }
    void processGameLoop() { /* copy your old game-loop steps */ }
    void cleanUpForQuit()  { /* copy your old cleanup logic */ }
    void processDrawing()  { /* copy your old drawing steps */ }
    void raiseWelcomeScreen() { /* copy your old welcome-screen code */ }

    Component getGameComponent() {
        return (gameFrame != null) ? gameFrame : this;
    }

    public void startRunnable(Runnable runnable, int priority) {
        Thread thread = new Thread(runnable, "GameThread");
        thread.setPriority(priority);
        thread.start();
    }

    void drawLoadingText(int i, String s) {
        while (graphics == null) {
            graphics = getGameComponent().getGraphics();
            try { getGameComponent().repaint(); } catch (Exception ignored) { }
            try { Thread.sleep(1000L); } catch (Exception ignored) { }
        }
        Font font  = new Font("Helvetica", Font.BOLD, 13);
        FontMetrics fm = getGameComponent().getFontMetrics(font);
        Font font2 = new Font("Helvetica", Font.PLAIN, 13);

        if (shouldClearScreen) {
            graphics.setColor(Color.black);
            graphics.fillRect(0, 0, myWidth, myHeight);
            shouldClearScreen = false;
        }
        Color color = new Color(140, 17, 17);
        int j = myHeight / 2 - 18;
        graphics.setColor(color);
        graphics.drawRect(myWidth/2 - 152, j, 304, 34);
        graphics.fillRect(myWidth/2 - 150, j+2, i*3, 30);
        graphics.setColor(Color.black);
        graphics.fillRect((myWidth/2 - 150) + i*3, j+2, 300 - i*3, 30);
        graphics.setFont(font);
        graphics.setColor(Color.white);
        graphics.drawString(s, (myWidth - fm.stringWidth(s))/2, j + 22);
    }    
}